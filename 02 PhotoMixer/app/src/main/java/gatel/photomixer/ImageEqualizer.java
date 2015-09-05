package gatel.photomixer;

import android.graphics.Bitmap;

public class ImageEqualizer {

    private static final int MAX_COLOR = 256;

    private final Bitmap baseImage;
    private final int[] cumulativeFrequency;

    private ImageEqualizer(Bitmap image, int[] cumulativeFrequency) {
        if (cumulativeFrequency.length != MAX_COLOR) {
            throw new IllegalArgumentException(
                    "Cumulative frequency must be of length " + MAX_COLOR);
        }
        if (!ImageUtils.isGrayscale(image)) {
            throw new IllegalArgumentException("Image must be grayscale");
        }
        this.baseImage = image;
        this.cumulativeFrequency = cumulativeFrequency;
    }

    public static ImageEqualizer create(Bitmap image) {
        Bitmap grayscaleImage = ImageUtils.convertToGrayscale(image);
        int[] frequency = calculateFrequency(grayscaleImage);
        int[] cumulativeFrequency = calculateCumulativeFrequency(frequency);
        return new ImageEqualizer(grayscaleImage, cumulativeFrequency);
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

    private static int[] calculateFrequency(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] frequency = new int[MAX_COLOR];

        for(int i = 0 ; i < MAX_COLOR; ++i) {
            frequency[i] = 0;
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int color = image.getPixel(x, y);
                int grayscaleColor = ImageUtils.getGrayscaleColor(color);
                frequency[grayscaleColor]++;
            }
        }
        return frequency;
    }

    public Bitmap getBaseImage() {
        return baseImage;
    }

    public Bitmap equalize(int threshold) {
        if (threshold < 0 || threshold > MAX_COLOR) {
            throw new IllegalArgumentException("Only values from 0 to " + MAX_COLOR +
                    " are allowed as threshold. Your value: " + threshold);
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
        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        Bitmap mappedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int color = ImageUtils.getGrayscaleColor(baseImage.getPixel(x, y));
                color = colorMap[color];
                mappedImage.setPixel(x, y, color * ImageUtils.GRAYSCALE_MASK);
            }
        }
        return mappedImage;
    }

}
