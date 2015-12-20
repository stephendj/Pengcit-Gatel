package gatel.instacit.utils;

import android.graphics.Bitmap;

/**
 * Created by Toshiba on 12/20/2015.
 */
public class ImageUtils {

    private static final int MAX_IMAGE_SIZE = 512;

    private ImageUtils() {
        // Utility class
    }

    public static Bitmap rescaleAndRecycleBitmap(Bitmap bitmap) {
        double scale = Math.min(
                (double) MAX_IMAGE_SIZE / bitmap.getWidth(),
                (double) MAX_IMAGE_SIZE / bitmap.getHeight());
        if (scale < 1) {
            Bitmap rescaledBitmap = Bitmap.createScaledBitmap(bitmap,
                    (int)(scale * bitmap.getWidth()),
                    (int)(scale * bitmap.getHeight()), false);
            bitmap.recycle();
            return rescaledBitmap;
        } else {
            return bitmap;
        }
    }
}
