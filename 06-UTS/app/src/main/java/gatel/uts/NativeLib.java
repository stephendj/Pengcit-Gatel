package gatel.uts;

import android.graphics.Bitmap;

/**
 * Created by Toshiba on 10/18/2015.
 */
public class NativeLib {

    static {
        System.loadLibrary("native");
    }

    private NativeLib() {
        // Utility class
    }

    public static native byte[] convertToGrayscale(int[] pixels);

    // equalizer stuff
    public static native void registerBitmapPixelsForEqualizer(int[] pixels, int width, int height);
    public static native int[] equalize(byte lowerThreshold, byte upperThreshold);
    public static native int[] getFrequency();
}
