package gatel.numbersmoother;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NumberSmoother {

    public static Bitmap[] getNumberBitmaps(Bitmap bitmap, List<Pair<Point, Point>> boundaries) {
        Bitmap[] numberBitmaps = new Bitmap[boundaries.size()];
        for(int i = 0 ; i < boundaries.size(); ++i) {
            Bitmap numberBitmap = Bitmap.createBitmap(5, 5, Bitmap.Config.RGB_565);

            double dx = (double) (boundaries.get(i).second.x - boundaries.get(i).first.x) / 5;
            double dy = (double) (boundaries.get(i).second.y - boundaries.get(i).first.y) / 5;

            for(double x = boundaries.get(i).first.x; Math.round(x) < boundaries.get(i).second.x; x+=dx) {
                for(double y = boundaries.get(i).first.y; Math.round(y) < boundaries.get(i).second.y; y+=dy) {
                    System.out.println(Math.round(x) + " " +  Math.round(y));
                }
            }
        }
        return numberBitmaps;
    }

    public static List<Pair<Point, Point>> getBoundaryPoints(Bitmap bitmap, int threshold) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        Log.d("NumberSmoother#getBoundaryPoints", String.format("Image size is %d x %d", width, height));
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        List<Pair<Integer, Integer>> lines = new ArrayList<>();
        List<Pair<Point, Point>> numberBoundaries = new ArrayList<>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int offset = x + y * width;
                if (ColorUtils.getGrayscale(pixels[offset]) <= threshold) {
                    try {
                        Pair<Point, Point> bounds = floodFill(pixels, width, height, x, y, threshold);
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

    private static Pair<Point, Point> floodFill(int[] pixels, int width, int height, int startX, int startY, int threshold) {
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
                    if (ColorUtils.getGrayscale(pixels[newOffset]) <= threshold) {
                        stack.push(newOffset);
                        pixels[newOffset] = ColorUtils.WHITE;
                    }
                }
            }
        }
        return new Pair<>(new Point(minX, minY), new Point(maxX, maxY));
    }
}
