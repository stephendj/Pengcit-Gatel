package gatel.carplaterecognition;

public class ColorUtils {

    public static final int WHITE = 0x00FFFFFF;
    public static final int BLACK = 0;
    public static final int GRAYSCALE_MASK = 0x00010101;

    private ColorUtils() {
        // Utility Class
    }

    public static int getGrayscale(int pixel) {
        return (((pixel >> 16) & 0xFF) + ((pixel >> 8) & 0xFF) + (pixel & 0xFF)) / 3;
    }

    public static int getBlue(int color) {
        return 0x0000FF & color;
    }

    public static int getGreen(int color) {
        return (0x00FF00 & color) >> 8;
    }

    public static int getRed(int color) {
        return (0xFF0000 & color) >> 16;
    }

}
