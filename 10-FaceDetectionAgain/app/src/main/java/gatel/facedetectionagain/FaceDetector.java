package gatel.facedetectionagain;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Toshiba on 11/18/2015.
 */
public class FaceDetector {

    private static final List<Integer> SKIN_COLORS = new ArrayList<>();
    private static final int FLOOD_FILL_GAP = 3;
    public static int SKIN_THRESHOLD = 15;
    private static int SIZE_THRESHOLD = 10;

    static {
        registerSkinColor(Color.rgb(186, 166, 129)); // jidat Alvin
        registerSkinColor(Color.rgb(97, 75, 52)); // jidat Winson ketutupan bayangan
        registerSkinColor(Color.rgb(142, 119, 85)); // jidat Eldwin
        registerSkinColor(Color.rgb(124, 82, 58)); // jidat Ahmad
        registerSkinColor(Color.rgb(75, 47, 33)); // pipi Menori
        registerSkinColor(Color.rgb(161, 130, 110)); // jidat Yanfa "bercahaya"
        registerSkinColor(Color.rgb(182, 136, 103)); // pipi Afik
        registerSkinColor(Color.rgb(175, 158, 128)); // jidat Zaky

    }

    public static void registerSkinColor(int color) {
        SKIN_COLORS.add(color);
    }

    public static boolean isSkinColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        for (Integer template : SKIN_COLORS) {
            int dist = Math.max(Math.max(Math.abs(red - Color.red(template)), Math.abs(green - Color.green(template))), Math.abs(blue - Color.blue(template)));
            if (dist < SKIN_THRESHOLD) return true;
        }
        return false;
    }

    public static boolean[] mark;

    public static List<Pair<Point, Point>> getBoundaries(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        Log.d("FaceDetector#getBoundaryPoints", String.format("Image size is %d x %d", width, height));
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        boolean[] visited = new boolean[size];
        mark = visited;

        List<Pair<Point, Point>> numberBoundaries = new ArrayList<>();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int offset = x + y * width;
                if (isSkinColor(pixels[offset]) && !visited[offset]) {
                    try {
                        Pair<Point, Point> bounds = floodFill(pixels, visited, width, height, x, y);
                        int boundWidth = bounds.second.x - bounds.first.x;
                        int boundHeight = bounds.second.y - bounds.first.y;
                        if (Math.min(boundHeight, boundWidth) > SIZE_THRESHOLD) {
                            numberBoundaries.add(bounds);
                        }
                        Log.d("FaceDetector#getBoundaryPoints", "Boundary : (" + bounds.first.x + "," + bounds.first.y + "),"
                                + "(" + bounds.second.x + "," + bounds.second.y + ")");
                    } catch (Exception e) {
                        Log.d("FaceDetector#getBoundaryPoints", "Failed to find boundary points: " + e.getMessage());
                    }
                }
            }
        }

        return numberBoundaries;
    }

    /**
     * @return the bounding box of the component.
     */
    private static Pair<Point, Point> floodFill(int[] pixels, boolean[] visited, int width, int height, int startX, int startY) {
        Log.d("FaceDetector#floodFill", String.format("Starting Flood Fill from (%d, %d)", startX, startY));
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalStateException("Flood fill starting from out of bounds position");
        }
        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        Stack<Integer> stack = new Stack<>();
        int offset = startY * width + startX;
        stack.push(offset);
        visited[offset] = true;
        while (!stack.empty()) {
            int top = stack.pop();
            int x = top % width;
            int y = top / width;
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            for (int ix = x - FLOOD_FILL_GAP; ix <= x + FLOOD_FILL_GAP; ++ix) {
                for (int iy = y - FLOOD_FILL_GAP; iy <= y + FLOOD_FILL_GAP; ++iy) {
                    if (ix < 0 || ix >= width || iy < 0 || iy >= height) {
                        continue;
                    }
                    int newOffset = iy * width + ix;
                    if (!visited[newOffset] && isSkinColor(pixels[newOffset])) {
                        stack.push(newOffset);
                        visited[newOffset] = true;
                    }
                }
            }
        }
        return new Pair<>(new Point(minX, minY), new Point(maxX, maxY));
    }

}
