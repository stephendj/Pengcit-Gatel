package gatel.instacit.utils;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Source: http://blog.ivank.net/fastest-gaussian-blur.html. Using 4th algorithm
 *
 * Created by Toshiba on 12/20/2015.
 */
public class GaussianBlur {

    private static final int source[] = new int[ImageUtils.MAX_IMAGE_AREA];
    private static final int target[] = new int[ImageUtils.MAX_IMAGE_AREA];

    private final int width;
    private final int height;
    private final int[] pixels;

    public GaussianBlur(int pixels[], int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public static GaussianBlur fromBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return new GaussianBlur(pixels, width, height);
    }

    public Bitmap blurBitmap(int radius) {
        int[] blurredPixels = blur(radius);
        return Bitmap.createBitmap(blurredPixels, width, height, Bitmap.Config.RGB_565);
    }

    public int[] blur(int radius) {
        long timeStart = System.currentTimeMillis();
        int[] boxes = getBoxesForGauss(radius, 3);
        int[] result = new int[width * height];
        for (int channel = 0; channel < 3; ++channel) {
            for (int i = 0; i < width * height; ++i) {
                source[i] = (pixels[i] >> (channel << 3)) & 0xFF;
            }
            boxBlur(source, target, (boxes[0] - 1) / 2);
            boxBlur(target, source, (boxes[1] - 1) / 2);
            boxBlur(source, target, (boxes[2] - 1) / 2);
            for (int i = 0; i < width * height; ++i) {
                result[i] |= (target[i] << (channel << 3));
            }
        }
        long timeFinish = System.currentTimeMillis();
        Log.d("Runtime", "Blurring " + width + "x" + height + " image: " + (timeFinish - timeStart) + " ms");
        return result;
    }

    private int[] getBoxesForGauss(int sigma, int n) {
        double wIdeal = Math.sqrt((double) (12 * sigma * sigma) / n + 1);
        int wl = (int)Math.floor(wIdeal);
        if (wl % 2 == 0) {
            wl--;
        }
        int wu = wl + 2;

        double mIdeal = (double)(12 * sigma * sigma - n * wl * wl - 4 * n * wl - 3 * n)/(-4 * wl - 4);
        int m = (int)Math.round(mIdeal);

        int[] sizes = new int[n];
        for (int i = 0; i < n; ++i) {
            sizes[i] = i < m ? wl : wu;
        }
        return sizes;
    }

    private void boxBlur(int[] source, int[] target, int r) {
        for (int i = 0; i < width * height; ++i) {
            target[i] = source[i];
        }
        boxBlurH(target, source, r);
        boxBlurT(source, target, r);
    }

    private void boxBlurH(int[] target, int[] source, int r) {
        float iarr = 1.F / (r + r + 1);
        for (int i = 0; i < height; ++i) {
            int ti = i * width;
            int li = ti;
            int ri = ti + r;
            int fv = source[ti];
            int lv = source[ti + width - 1];
            int val = (r + 1) * fv;
            for (int j = 0; j < r; ++j) {
                val += source[ti + j];
            }
            for (int j=0; j <= r ; j++) {
                val += source[ri++] - fv;
                target[ti++] = Math.round(iarr * val);
            }
            for (int j = r + 1; j < width - r; j++) {
                val += source[ri++] - source[li++];
                target[ti++] = Math.round(iarr * val);
            }
            for (int j = width - r; j < width  ; j++) {
                val += lv - source[li++];
                target[ti++] = Math.round(iarr * val);
            }
        }
    }

    private void boxBlurT(int[] source, int[] target, int r) {
        float iarr = 1.F / (r + r + 1);
        for (int i = 0; i < width; i++) {
            int ti = i;
            int li = ti;
            int ri = ti + r * width;
            int fv = source[ti];
            int lv = source[ti + width * (height - 1)];
            int val = (r + 1) * fv;
            for (int j = 0; j < r; j++) {
                val += source[ti + j * width];
            }
            for (int j = 0; j <= r; j++) {
                val += source[ri] - fv;
                target[ti] = Math.round(iarr * val);
                ri += width;
                ti += width;
            }
            for (int j = r + 1; j < height - r; j++) {
                val += source[ri] - source[li];
                target[ti] = Math.round(iarr * val);
                li += width;
                ri += width;
                ti += width;
            }
            for (int j = height - r; j < height; j++) {
                val += lv - source[li];
                target[ti] = Math.round(iarr * val);
                li += width;
                ti += width;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels() {
        return pixels;
    }

}
