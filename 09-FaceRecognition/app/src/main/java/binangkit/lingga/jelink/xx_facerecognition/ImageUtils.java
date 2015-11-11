package binangkit.lingga.jelink.xx_facerecognition;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageUtils {

    public static final int GRAYSCALE_MASK = 0x00010101;
    private static final int MAX_COLOR = 256;
    private static int threshold = 40;

    private ImageUtils () {
        // utility class
    }

    public static Bitmap getBinaryImage(Bitmap image) {
        //calculateThreshold(image);
        System.out.println("aaaaaaaaaa" + threshold);
        Bitmap binaryBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int color = image.getPixel(x, y);
                int average = getGrayscaleColor(color);
                if(average < threshold) {
                    binaryBitmap.setPixel(x, y, Color.WHITE);
                } else if(average >= threshold) {
                    binaryBitmap.setPixel(x, y, Color.BLACK);
                }
            }
        }
        return binaryBitmap;
    }

    public static void calculateThreshold(Bitmap image) {
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
        threshold =  (threshold1 + threshold2) / 2;
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
            grayscalePixels[i] = (byte)getGrayscaleColor(pixels[i]);
        }
        return grayscalePixels;
    }
}
