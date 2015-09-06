package gatel.photomixer;

import android.graphics.Bitmap;

public class ImageEqualizer {

    private static final int MAX_COLOR = 256;

    private final byte[] pixels;
    private final int width;
    private final int height;
    private final int[] cumulativeFrequency;

    private ImageEqualizer(byte[] pixels, int width, int height, int[] cumulativeFrequency) {
        if (cumulativeFrequency.length != MAX_COLOR) {
            throw new IllegalArgumentException(
                    "Cumulative frequency must be of length " + MAX_COLOR);
        }
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.cumulativeFrequency = cumulativeFrequency;
    }

    public static ImageEqualizer create(Bitmap image) {
        int[] pixels = ImageUtils.getPixels(image);
        byte[] grayscalePixels = ImageUtils.convertToGrayscale(pixels);
        int[] frequency = calculateFrequency(grayscalePixels);
        int[] cumulativeFrequency = calculateCumulativeFrequency(frequency);
        return new ImageEqualizer(
                grayscalePixels,
                image.getWidth(),
                image.getHeight(),
                cumulativeFrequency);
    }

    private static int[] calculateFrequency(byte[] grayscalePixels) {
        int[] frequency = new int[MAX_COLOR];
        for (int i = 0 ; i < MAX_COLOR; ++i) {
            frequency[i] = 0;
        }
        int length = grayscalePixels.length;
        for (int i = 0; i < length; ++i) {
            frequency[0xFF & grayscalePixels[i]]++;
        }
        return frequency;
    }

    private static int[] calculateCumulativeFrequency(int[] frequency) {
        int length = frequency.length;
        int[] cumulativeFrequency = new int[length];
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                cumulativeFrequency[i] = cumulativeFrequency[i - 1] + frequency[i];
            } else {
                cumulativeFrequency[i] = frequency[i];
            }
        }
        return cumulativeFrequency;
    }

    public Bitmap equalize(int threshold) {
        if (threshold < 0 || threshold > MAX_COLOR) {
            throw new IllegalArgumentException("Only values from 0 to " + MAX_COLOR +
                    " are allowed as threshold. Your value is " + threshold);
        }
        int[] colorMap = createColorMap(threshold);
        Bitmap equalizedImage = createBitmapUsingColorMap(colorMap);
        return equalizedImage;
    }

    private int[] createColorMap(int threshold) {
        int[] colorMap = new int[MAX_COLOR];
        int totalColor = cumulativeFrequency[MAX_COLOR - 1];
        for (int i = 0; i < MAX_COLOR; ++i) {
            colorMap[i] = cumulativeFrequency[i] * threshold / totalColor;
        }
        return colorMap;
    }

    private Bitmap createBitmapUsingColorMap(int[] colorMap) {
        int size = width * height;
        int[] mappedPixels = new int[size];
        for (int i = 0; i < size; ++i) {
            mappedPixels[i] = colorMap[0xFF & pixels[i]] * ImageUtils.GRAYSCALE_MASK;
        }
        return Bitmap.createBitmap(mappedPixels, width, height, Bitmap.Config.RGB_565);
    }

    public Bitmap getBaseImage() {
        return equalize(MAX_COLOR - 1);
    }
}
