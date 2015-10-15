package gatel.uts;

import android.graphics.Bitmap;

public class ImageUtils {

    public static final int GRAYSCALE_MASK = 0x10101;

    private ImageUtils () {
        // utility class
    }

    public static Bitmap convertToGrayscale(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap grayscaleImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int color = image.getPixel(x, y);
                int average = (getRed(color) + getGreen(color) + getBlue(color))/3;
                grayscaleImage.setPixel(x, y, average * GRAYSCALE_MASK);
            }
        }
        return grayscaleImage;
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

    public static int getGrayscaleColor(int color) {
        int red = getRed(color);
        int green = getGreen(color);
        int blue = getBlue(color);

        return ((red + green + blue) / 3);
    }

    public static int[] getPixels(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    public static byte[] convertToGrayscale(int[] pixels) {
        int length = pixels.length;
        byte[] grayscalePixels = new byte[length];
        for (int i = 0; i < length; ++i) {
            grayscalePixels[i] = (byte)getGrayscaleColor(pixels[i]);
        }
        return grayscalePixels;
    }
}
