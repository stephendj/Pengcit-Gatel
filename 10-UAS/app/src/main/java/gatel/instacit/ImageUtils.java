package gatel.instacit;

import android.graphics.Bitmap;

/**
 * Created by Toshiba on 12/20/2015.
 */
public class ImageUtils {

    private static final int MAX_IMAGE_WIDTH = 1024;

    private ImageUtils() {
        // Utility class
    }

    public static Bitmap rescaleAndRecycleBitmap(Bitmap bitmap) {
        if (bitmap.getWidth() > MAX_IMAGE_WIDTH) {
            Bitmap rescaledBitmap = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_WIDTH,
                    bitmap.getHeight() * MAX_IMAGE_WIDTH / bitmap.getWidth(), false);
            bitmap.recycle();
            return rescaledBitmap;
        } else {
            return bitmap;
        }
    }
}
