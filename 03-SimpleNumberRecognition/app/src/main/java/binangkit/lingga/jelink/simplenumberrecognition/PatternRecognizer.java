package binangkit.lingga.jelink.simplenumberrecognition;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class PatternRecognizer {

    private static final double ERROR_THRESHOLD = 0.25;

    private final List<List<Integer>> chainCodes;

    public PatternRecognizer(List<List<Integer>> chainCodes) {
        this.chainCodes = chainCodes;
    }

    public static PatternRecognizer fromBitmap(Bitmap bitmap) {
        // TODO: convert to B/W
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        ChainCodeGenerator chainCodeGenerator = new ChainCodeGenerator(pixels, width, height);
        List<List<Integer>> chainCodes = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            if (PatternRecognizerUtils.isBlack(pixels[i])) {
                int x = i % width;
                int y = i / width;
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

        return new PatternRecognizer(chainCodes);
    }

    private static void floodFill(int[] pixels, int width, int height, int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        int offset = y * width + x;
        if (!PatternRecognizerUtils.isBlack(pixels[offset])) {
            return;
        }
        pixels[offset] = PatternRecognizerUtils.WHITE;
        for (int direction = 0; direction < 8; ++direction) {
            floodFill(
                    pixels,
                    width,
                    height,
                    x + ChainCodeGenerator.REPLACEMENT[direction][0],
                    y + ChainCodeGenerator.REPLACEMENT[direction][1]);
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

        Log.d("HUBA", Objects.toString(chainCode));
        Log.d("HUBA", String.format("minError = %d", minError));

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
