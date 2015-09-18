package binangkit.lingga.jelink.simplenumberrecognition;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class PatternRecognizer {

    public static double ERROR_THRESHOLD = 0.75;

    private final List<List<Integer>> chainCodes;

    public PatternRecognizer(List<List<Integer>> chainCodes) {
        this.chainCodes = chainCodes;
    }

    public static PatternRecognizer fromBitmap(Bitmap bitmap) {
        // TODO: convert to B/W
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        Log.d("PatternRecognizer#fromBitmap", String.format("Image size is %d x %d", width, height));
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        ChainCodeGenerator chainCodeGenerator = new ChainCodeGenerator(pixels, width, height);
        List<List<Integer>> chainCodes = new ArrayList<>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int offset = x + y * width;
                if (PatternRecognizerUtils.isBlack(pixels[offset])) {
                    try {
                        List<Integer> chainCode = chainCodeGenerator.generateChainCode(x, y);
                        chainCodes.add(chainCode);
                    } catch (Exception e) {
                        Log.d("PatternRecognizer#fromBitmap", "ChainCode failed to created");
                    } finally {
                        floodFill(pixels, width, height, x, y);
                    }
                }
            }
        }

        return new PatternRecognizer(chainCodes);
    }

    private static void floodFill(int[] pixels, int width, int height, int startX, int startY) {
        Log.d("PatternRecognizer#floodFill", String.format("Starting Flood Fill from (%d, %d)", startX, startY));
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalStateException("Flood fill starting from out of bounds position");
        }
        Stack<Integer> stack = new Stack<>();
        int offset = startY * width + startX;
        stack.push(offset);
        pixels[offset] = PatternRecognizerUtils.WHITE;
        while (!stack.empty()) {
            int top = stack.pop();
            int x = top % width;
            int y = top / width;
            for (int ix = x - 1; ix <= x + 1; ++ix) {
                for (int iy = y - 1; iy <= y + 1; ++iy) {
                    if (ix < 0 || ix >= width || iy < 0 || iy >= height) {
                        continue;
                    }
                    int newOffset = iy * width + ix;
                    if (PatternRecognizerUtils.isBlack(pixels[newOffset])) {
                        stack.push(newOffset);
                        pixels[newOffset] = PatternRecognizerUtils.WHITE;
                    }
                }
            }
        }
    }

    public <T> List<T> recognizePattern(Map<T, List<Integer>> patterns) {
        List<T> recognizedPatterns = new ArrayList<>();
        for (List<Integer> chainCode : chainCodes) {
            T bestPattern = recognizeChainCode(chainCode, patterns);
            recognizedPatterns.add(bestPattern);
        }
        return recognizedPatterns;
    }

    private <T> T recognizeChainCode(List<Integer> chainCode, Map<T, List<Integer>> patterns) {
        T best = null;
        int minError = chainCode.size();
        int diffLength = 0;
        int chainCodeLength = chainCode.size();

        for (Map.Entry<T, List<Integer>> entry : patterns.entrySet()) {
            List<Integer> normalizedPattern = normalizePattern(
                    entry.getValue(),
                    chainCodeLength);
            int editDistance = PatternRecognizerUtils.calculateEditDistance(
                    chainCode,
                    normalizedPattern);
            if (editDistance < minError) {
                best = entry.getKey();
                minError = editDistance;
                diffLength = Math.abs(normalizedPattern.size() - chainCodeLength);
            }
        }

        Log.d("PatternRecognizer#recognizeChainCode", Objects.toString(chainCode));
        Log.d("PatternRecognizer#recognizeChainCode", String.format("minError = %d (%d %%)", minError, (minError * 100 / chainCodeLength)));

        if (minError <= (int)(ERROR_THRESHOLD * chainCodeLength) + diffLength) {
            return best;
        } else {
            return null;
        }
    }

    /**
     * Normalize the pattern so it (closely) matches the length by multiplying every element the
     * correct number of times.
     */
    private List<Integer> normalizePattern(List<Integer> pattern, int length) {
        int patternLength = pattern.size();
        if (patternLength == 0) {
            return pattern;
        }
        int factor = length / patternLength;
        if (factor == 0) {
            return pattern;
        }
        List<Integer> normalizedPattern = new ArrayList<>();
        for (Integer item : pattern) {
            for (int i = 0; i < factor; ++i) {
                normalizedPattern.add(item);
            }
        }
        return normalizedPattern;
    }

    public List<List<Integer>> getChainCodes() {
        return chainCodes;
    }

}
