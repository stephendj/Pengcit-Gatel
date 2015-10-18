package gatel.uts;

import android.graphics.Bitmap;

public class ImageEqualizer {

    private static final int MAX_COLOR = 256;

    private static int width = 0;
    private static int height = 0;

    private ImageEqualizer() {
        // This is only a wrapper class, calling NativeLib methods
    }

    public static void registerBitmap(Bitmap bitmap) {
        int[] pixels = ImageUtils.getPixels(bitmap);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        NativeLib.registerBitmapPixelsForEqualizer(pixels, width, height);
    }

    public static Bitmap equalize(int threshold) {
        if (threshold < 0 || threshold > MAX_COLOR) {
            throw new IllegalArgumentException("Only values from 0 to " + MAX_COLOR +
                    " are allowed as threshold. Your value is " + threshold);
        }
        int[] pixels = NativeLib.equalize((byte)0, (byte)threshold);
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap getBaseImage() {
        return equalize(MAX_COLOR - 1);
    }

    public static int[] getColorFrequency() {
        return NativeLib.getFrequency();
    }
}
