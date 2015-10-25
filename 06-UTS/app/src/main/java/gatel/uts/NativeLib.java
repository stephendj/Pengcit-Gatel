package gatel.uts;

import android.graphics.Bitmap;

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
        int[] pixels = ImageUtils.getPixels(bitmap);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        NativeLib._registerBitmap(pixels, width, height);
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

    // Native methods declaration
    public static native void _registerBitmap(int[] pixels, int width, int height);
    public static native void _equalize(int lowerThreshold, int upperThreshold);
    public static native int[] _getFrequency();
    public static native int[] _getEqualizedFrequency();
    public static native int[] _getEqualizedImage();
    public static native int[] _getGrayscaleImage();
    public static native int[] _getBinaryImage();

    static {
        System.loadLibrary("native");
    }
}
