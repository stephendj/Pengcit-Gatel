package gatel.numbersmoother;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageUtils {

    private static final int MAX_COLOR = 256;

    private ImageUtils () {
        // Utility class
    }

    public static Bitmap getBinaryImage(Bitmap image, int threshold) {
        Bitmap binaryBitmap = image.copy(Bitmap.Config.RGB_565, true);
        for (int x = 0; x < binaryBitmap.getWidth(); ++x) {
            for (int y = 0; y < binaryBitmap.getHeight(); ++y) {
                int color = image.getPixel(x, y);
                int average = ColorUtils.getGrayscale(color) * ColorUtils.GRAYSCALE_MASK;
                binaryBitmap.setPixel(x, y, ColorUtils.WHITE);
                if(average <= threshold) {
                    binaryBitmap.setPixel(x, y, ColorUtils.BLACK);
                }
            }
        }
        return MedianFilter.removeNoise(binaryBitmap);
    }

    public static int calculateThreshold(Bitmap image) {
        int[] pixels = ImageUtils.getPixels(image);
        byte[] grayscalePixels = ImageUtils.convertToGrayscale(pixels);
        int[] frequency = calculateFrequency(grayscalePixels);

        int sum = 0, total = 0;
        for (int i = 1; i < frequency.length; ++i) {
            sum += i * frequency[i];
            total += frequency[i];
        }
        int sumB = 0, wB = 0, wF = 0;
        int mB, mF;
        double max = 0.0, between = 0.0;
        int threshold1 = 0, threshold2 = 0;
        for (int i = 0; i < frequency.length; ++i) {
            wB += frequency[i];
            if (wB == 0)
                continue;
            wF = total - wB;
            if (wF == 0)
                break;
            sumB += i * frequency[i];
            mB = sumB / wB;
            mF = (sum - sumB) / wF;
            between = wB * wF * (mB - mF) * (mB - mF);
            if (between >= max) {
                threshold1 = i;
                if (between > max) {
                    threshold2 = i;
                }
                max = between;
            }
        }
        return ((threshold1 + threshold2) / 2);
    }

    private static int[] calculateFrequency(byte[] grayscalePixels) {
        int[] frequency = new int[MAX_COLOR];
        for (int i = 0 ; i < MAX_COLOR; ++i) {
            frequency[i] = 0;
        }
        for (byte grayscalePixel : grayscalePixels) {
            frequency[0xFF & grayscalePixel]++;
        }
        return frequency;
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
            grayscalePixels[i] = (byte) ColorUtils.getGrayscale(pixels[i]);
        }
        return grayscalePixels;
    }
}
