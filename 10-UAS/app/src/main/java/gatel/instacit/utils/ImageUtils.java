package gatel.instacit.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Pair;

import java.util.List;

/**
 * Created by Toshiba on 12/20/2015.
 */
public class ImageUtils {

    public static final int MAX_IMAGE_SIZE = 768;
    public static final int MAX_IMAGE_AREA = MAX_IMAGE_SIZE * MAX_IMAGE_SIZE;
    public static final int GRAYSCALE_MASK = 0x00010101;

    private ImageUtils() {
        // Utility class
    }

    public static Bitmap convertAndRecycleBitmap(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap.recycle();
        return convertedBitmap;
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

    public static int[] getPixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    public static Bitmap convertToGrayscale(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//        Bitmap grayscaleImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        for (int x = 0; x < width; ++x) {
//            for (int y = 0; y < height; ++y) {
//                int color = bitmap.getPixel(x, y);
//                int average = getGrayscaleColor(color);
//                grayscaleImage.setPixel(x, y, average * GRAYSCALE_MASK);
//            }
//        }
//        return grayscaleImage;
        int[] pixels = getPixels(bitmap);
        for (int offset = 0; offset < width * height; ++offset) {
            int average = getGrayscaleColor(pixels[offset]);
            pixels[offset] = average * GRAYSCALE_MASK;
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    public static int getGrayscaleColor(int color) {
        return ((color & 0xFF) + ((color & 0xFF00) >> 8) + ((color & 0xFF0000) >> 16)) / 3;
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

    public static Bitmap getBinaryImage(Bitmap image, int threshold) {
        //calculateThreshold(image);
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

    /**
     * This will erase (whiten) all black pixels near the edges
     */
    public static Bitmap trimSurrounding(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = getPixels(bitmap);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; y += height - 1) {
                int offset = x + y * width;
                if (pixels[offset] == Color.BLACK) {
                    floodFill(pixels, width, height, x, y);
                }
            }
        }
//        for (int x = 0; x < width; ++x) {
//            for (int y = 0, offset = x; y < height; ++y, offset += y) {
//                if (isSkinColor(pixels[offset]) && !visited[offset]) {
//                    try {
//                        Pair<Point, Point> bounds = floodFill(pixels, visited, width, height, x, y);
//
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565);
    }

    private static int[] stack = new int[MAX_IMAGE_AREA];
    private static final int FLOOD_FILL_GAP = 1;

    private static void floodFill(int[] pixels, int width, int height, int startX, int startY) {
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalStateException("Flood fill starting from out of bounds position");
        }
        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        int stackTop = 0;
        int offset = startY * width + startX;
        stack[stackTop++] = offset;
        while (stackTop > 0) {
            int top = stack[--stackTop];
            int x = top % width;
            int y = top / width;
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            for (int ix = x - FLOOD_FILL_GAP; ix <= x + FLOOD_FILL_GAP; ++ix) {
                for (int iy = y - FLOOD_FILL_GAP; iy <= y + FLOOD_FILL_GAP; ++iy) {
                    if (ix < 0 || ix >= width || iy < 0 || iy >= height) {
                        continue;
                    }
                    int newOffset = iy * width + ix;
                    if (pixels[newOffset] == Color.BLACK) {
                        stack[stackTop++] = newOffset;
                        pixels[newOffset] = Color.WHITE;
                    }
                }
            }
        }
    }

    public static Bitmap generateBitmapFromPoints(List<Point> points) {
        if (points.isEmpty()) {
            Bitmap result = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
            result.setPixel(0, 0, Color.WHITE);
            return result;
        } else {
            Point topLeft = new Point(points.get(0));
            Point bottomRight = new Point(points.get(0));
            for (Point point : points) {
                if (point.x < topLeft.x) {
                    topLeft.x = point.x;
                }
                if (point.y < topLeft.y) {
                    topLeft.y = point.y;
                }
                if (point.x > bottomRight.x) {
                    bottomRight.x = point.x;
                }
                if (point.y > bottomRight.y) {
                    bottomRight.y = point.y;
                }
            }
            int width = bottomRight.x - topLeft.x + 1;
            int height = bottomRight.y - topLeft.y + 1;
            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    result.setPixel(x, y, Color.WHITE);
                }
            }
            for (Point point : points) {
                result.setPixel(point.x - topLeft.x, point.y - topLeft.y, Color.BLACK);
            }
            return result;
        }
    }

}
