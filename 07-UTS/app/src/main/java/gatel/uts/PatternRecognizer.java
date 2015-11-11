package gatel.uts;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class PatternRecognizer {

    public static double ERROR_THRESHOLD = 0.75;

    private final List<List<List<Integer>>> chainCodesPerLine;

    public PatternRecognizer(List<List<List<Integer>>> chainCodesPerLine) {
        this.chainCodesPerLine = chainCodesPerLine;
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

        List<Pair<Integer, Integer>> lines = new ArrayList<>();
        List<List<List<Integer>>> chainCodesPerLine = new ArrayList<>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int offset = x + y * width;
                if (ColorUtils.isBlack(pixels[offset])) {
                    List<Integer> chainCode;
                    try {
                        chainCode = chainCodeGenerator.generateChainCode(x, y);
                        Log.d("fromBitmap", Objects.toString(chainCode));
                        Pair<Point, Point> bounds = floodFill(pixels, width, height, x, y);
                        int bestLine = -1;
                        int maxCollision = 0;
                        for (int i = 0; i < lines.size(); ++i) {
                            Pair<Integer, Integer> line = lines.get(i);
                            int collision = Math.min(bounds.second.y, line.second) -
                                    Math.max(bounds.first.y, line.first);
                            if (collision > maxCollision) {
                                bestLine = i;
                                maxCollision = collision;
                            }
                        }
                        Pair<Integer, Integer> lastLine = new Pair<>(bounds.first.y, bounds.second.y);
                        if (bestLine < 0) {
                            List<List<Integer>> chainCodeInNewLine = new ArrayList<>();
                            chainCodeInNewLine.add(chainCode);
                            chainCodesPerLine.add(chainCodeInNewLine);
                            lines.add(lastLine);
                        } else {
                            chainCodesPerLine.get(bestLine).add(chainCode);
                            lines.set(bestLine, lastLine);
                        }
                    } catch (Exception e) {
                        Log.d("PatternRecognizer#fromBitmap", "ChainCode failed to created");
                    }
                }
            }
        }


        return new PatternRecognizer(chainCodesPerLine);
    }

    public static List<Pair<Point, Point>> getBoundaryPoints(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        Log.d("PatternRecognizer#getBoundaryPoints", String.format("Image size is %d x %d", width, height));
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        List<Pair<Integer, Integer>> lines = new ArrayList<>();
        List<Pair<Point, Point>> numberBoundaries = new ArrayList<>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int offset = x + y * width;
                if (ColorUtils.isBlack(pixels[offset])) {
                    try {
                        Pair<Point, Point> bounds = floodFill(pixels, width, height, x, y);
                        int bestLine = -1;
                        int maxCollision = 0;
                        for (int i = 0; i < lines.size(); ++i) {
                            Pair<Integer, Integer> line = lines.get(i);
                            int collision = Math.min(bounds.second.y, line.second) -
                                    Math.max(bounds.first.y, line.first);
                            if (collision > maxCollision) {
                                bestLine = i;
                                maxCollision = collision;
                            }
                        }
                        Pair<Integer, Integer> lastLine = new Pair<>(bounds.first.y, bounds.second.y);
                        if (bestLine < 0) {
                            lines.add(lastLine);
                        } else {
                            lines.set(bestLine, lastLine);
                        }
                        numberBoundaries.add(bounds);
                        Log.d("PatternRecognizer#getBoundaryPoints", "Boundary : (" + bounds.first.x + "," + bounds.first.y + "),"
                                + "(" + bounds.second.x + "," + bounds.second.y + ")");
                    } catch (Exception e) {
                        Log.d("PatternRecognizer#getBoundaryPoints", "Failed to find boundary points");
                    }
                }
            }
        }

        return numberBoundaries;
    }

    /**
     * @return the bounding box of the component.
     */
    private static Pair<Point, Point> floodFill(int[] pixels, int width, int height, int startX, int startY) {
        Log.d("PatternRecognizer#floodFill", String.format("Starting Flood Fill from (%d, %d)", startX, startY));
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalStateException("Flood fill starting from out of bounds position");
        }
        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        Stack<Integer> stack = new Stack<>();
        int offset = startY * width + startX;
        stack.push(offset);
        pixels[offset] = ColorUtils.WHITE;
        while (!stack.empty()) {
            int top = stack.pop();
            int x = top % width;
            int y = top / width;
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            for (int ix = x - 1; ix <= x + 1; ++ix) {
                for (int iy = y - 1; iy <= y + 1; ++iy) {
                    if (ix < 0 || ix >= width || iy < 0 || iy >= height) {
                        continue;
                    }
                    int newOffset = iy * width + ix;
                    if (ColorUtils.isBlack(pixels[newOffset])) {
                        stack.push(newOffset);
                        pixels[newOffset] = ColorUtils.WHITE;
                    }
                }
            }
        }
        return new Pair<>(new Point(minX, minY), new Point(maxX, maxY));
    }

    public <T> List<T> recognizePattern(Multimap<T, List<Integer>> patterns) {
        List<List<T>> recognizedPatternsPerLine = recognizePatternPerLine(patterns);
        List<T> recognizedPatterns = new ArrayList<>();
        for (List<T> line : recognizedPatternsPerLine) {
            recognizedPatterns.addAll(line);
        }
        return recognizedPatterns;
    }

    public <T> List<List<T>> recognizePatternPerLine(Multimap<T, List<Integer>> patterns) {
        List<List<T>> recognizedPatterns = new ArrayList<>();
        for (List<List<Integer>> chainCodesLine : chainCodesPerLine) {
            List<T> recognizedPatternsThisLine = new ArrayList<>();
            for (List<Integer> chainCode : chainCodesLine) {
                T bestPattern = recognizeChainCode(chainCode, patterns);
                recognizedPatternsThisLine.add(bestPattern);
            }
            recognizedPatterns.add(recognizedPatternsThisLine);
        }
        return recognizedPatterns;
    }

    public <T> T recognizeChainCode(List<Integer> chainCode, Multimap<T, List<Integer>> patterns) {
        T best = null;
        int minError = chainCode.size();
        int diffLength = 0;
        int chainCodeLength = chainCode.size();

        for (Map.Entry<T, List<Integer>> entry : patterns.entries()) {
            List<Integer> normalizedPattern = normalizePattern(
                    entry.getValue(),
                    chainCodeLength);
            List<Integer> normalizedChainCode = normalizePattern(
                    chainCode,
                    normalizedPattern.size());
            int scalingFactor = normalizedChainCode.size() / chainCodeLength;
            int editDistance = PatternRecognizerUtils.calculateCircularEditDistance(
                    normalizedChainCode,
                    normalizedPattern) / scalingFactor;
            if (editDistance < minError) {
                best = entry.getKey();
                minError = editDistance;
                diffLength = Math.abs(normalizedPattern.size() - normalizedChainCode.size());
            }
        }

        Log.d("PatternRecognizer#recognizeChainCode", Objects.toString(chainCode));
        Log.d("PatternRecognizer#recognizeChainCode",
                String.format("%s detected with minError = %d (%d %%)",
                        Objects.toString(best), minError, (minError * 100 / chainCodeLength)));

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
        List<List<Integer>> chainCodes = new ArrayList<>();
        for (List<List<Integer>> line : chainCodesPerLine) {
            chainCodes.addAll(line);
        }
        return chainCodes;
    }

    public List<List<List<Integer>>> getChainCodesPerLine() {
        return chainCodesPerLine;
    }

}
