package gatel.uts;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toshiba on 10/18/2015.
 */
public class NativeLib {

    private static int width;
    private static int height;

    private NativeLib() {
        // Utility class
    }

    // Helper functions
    public static void registerBitmap(Bitmap bitmap) {
        int[] pixels = getPixels(bitmap);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        NativeLib._registerBitmap(pixels, width, height);
    }

    public static int[] getPixels(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    public static int[] getFrequency() {
        return _getFrequency();
    }

    public static int[] getEqualizedFrequency() {
        return _getEqualizedFrequency();
    }

    public static void equalize(int lowerThreshold, int upperThreshold) {
        _equalize(lowerThreshold, upperThreshold);
    }

    public static Bitmap getEqualizedBitmap() {
        int[] pixels = _getEqualizedImage();
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap getGrayscaleBitmap() {
        int[] pixels = _getGrayscaleImage();
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap getBinaryBitmap() {
        int[] pixels = _getBinaryImage();
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static void setBinaryThreshold(int threshold) {
        _setBinaryThreshold(threshold);
    }

    public static int getBinaryThreshold() {
        return _getBinaryThreshold();
    }

    public static List<Pair<Point, Point>> getBoundaries() {
        int[][] boundariesArray = _getBoundaries();
        List<Pair<Point, Point>> boundaries = new ArrayList<>();

        for (int[] boundaryArray : boundariesArray) {
            Pair<Point, Point> boundary = new Pair<>(
                    new Point(boundaryArray[0], boundaryArray[1]),
                    new Point(boundaryArray[2], boundaryArray[3]));
            boundaries.add(boundary);
        }
        return boundaries;
    }

    public static List<int[][]> getGrids() {
        int[][] gridsArray = _getGrids();
        List<int[][]> grids = new ArrayList<>();

        for (int[] gridArray : gridsArray) {
            int size = (int)Math.round(Math.sqrt(gridArray.length));
            int[][] grid = new int[size][size];
            for (int x = 0; x < size; ++x) {
                for (int y = 0; y < size; ++y) {
                    grid[x][y] = gridArray[x + y * size];
                }
            }
            grids.add(grid);
        }
        return grids;
    }
    public static String recognizePattern() {
        return _recognizePattern();
    }

    /**
     * Warning: this will erase previously registered bitmap. This should be called early before
     * any other bitmaps were registered.
     */
    public static void registerPattern(Bitmap bitmap, String value) {
        registerBitmap(bitmap);
        _registerPattern(value);
    }

    // Native methods declaration
    public static native void _registerBitmap(int[] pixels, int width, int height);
    public static native void _equalize(int lowerThreshold, int upperThreshold);
    public static native int[] _getFrequency();
    public static native int[] _getEqualizedFrequency();
    public static native int[] _getEqualizedImage();
    public static native int[] _getGrayscaleImage();
    public static native int[] _getBinaryImage();
    public static native void _setBinaryThreshold(int threshold);
    public static native int _getBinaryThreshold();
    public static native int[][] _getBoundaries();
    public static native int[][] _getGrids();
    public static native String _recognizePattern();
    public static native void _registerPattern(String value);

    static {
        System.loadLibrary("native");
    }
}
