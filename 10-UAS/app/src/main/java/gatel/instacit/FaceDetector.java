package gatel.instacit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import gatel.instacit.utils.ImageUtils;

/**
 * Created by Toshiba on 11/18/2015.
 */
public class FaceDetector {

    private static final List<Integer> SKIN_COLORS = new ArrayList<>();
    private static final int FLOOD_FILL_GAP = 3;

    public static int SKIN_THRESHOLD = 15;
    private static int SIZE_THRESHOLD = 10;

    static {
        registerSkinColor(Color.rgb(186, 166, 129)); // jidat Alvin
        registerSkinColor(Color.rgb(97, 75, 52)); // jidat Winson ketutupan bayangan
        registerSkinColor(Color.rgb(142, 119, 85)); // jidat Eldwin
        registerSkinColor(Color.rgb(124, 82, 58)); // jidat Ahmad
        registerSkinColor(Color.rgb(75, 47, 33)); // pipi Menori
        registerSkinColor(Color.rgb(161, 130, 110)); // jidat Yanfa "bercahaya"
        registerSkinColor(Color.rgb(182, 136, 103)); // pipi Afik
        registerSkinColor(Color.rgb(175, 158, 128)); // jidat Zaky
    }

    public static void registerSkinColor(int color) {
        SKIN_COLORS.add(color);
    }

    public static boolean isSkinColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        for (Integer template : SKIN_COLORS) {
            int dist = Math.max(Math.max(Math.abs(red - Color.red(template)), Math.abs(green - Color.green(template))), Math.abs(blue - Color.blue(template)));
            if (dist < SKIN_THRESHOLD) return true;
        }
        return false;
    }

    public static boolean[] mark;
    public static final int[] stack = new int[ImageUtils.MAX_IMAGE_AREA];

    public static List<Pair<Point, Point>> getBoundaries(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        Log.d("FaceDetector#getBoundaryPoints", String.format("Image size is %d x %d", width, height));

        // using android for now ...
        android.media.FaceDetector nativeFaceDetector = new android.media.FaceDetector(width, height, 25);
        android.media.FaceDetector.Face[] faces = new android.media.FaceDetector.Face[25];
        int nFaces = nativeFaceDetector.findFaces(bitmap, faces);
        Log.d("Faces", nFaces + " faces found " + bitmap.getConfig());
        List<Pair<Point, Point>> faceBoundaries = new ArrayList<>();
        for (int i = 0; i < nFaces; ++i) {
            PointF center = new PointF();
            faces[i].getMidPoint(center);
            float radius = faces[i].eyesDistance();
            Point topLeft = new Point((int)(center.x - radius), (int)(center.y - radius));
            Point bottomRight = new Point((int)(center.x + radius), (int)(center.y + 1.618F * radius));
            faceBoundaries.add(new Pair<Point, Point>(topLeft, bottomRight));
        }

//        int[] pixels = new int[size];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//        boolean[] visited = new boolean[size];
//        mark = visited;
//
//        List<Pair<Point, Point>> faceBoundaries = new ArrayList<>();
//
//        for (int x = 0; x < width; ++x) {
//            for (int y = 0, offset = x; y < height; ++y, offset += y) {
//                if (isSkinColor(pixels[offset]) && !visited[offset]) {
//                    try {
//                        Pair<Point, Point> bounds = floodFill(pixels, visited, width, height, x, y);
//                        int boundWidth = bounds.second.x - bounds.first.x;
//                        int boundHeight = bounds.second.y - bounds.first.y;
//                        if (Math.min(boundHeight, boundWidth) > SIZE_THRESHOLD) {
//                            faceBoundaries.add(bounds);
//                        }
////                        Log.d("FaceDetector#getBoundaryPoints", "Boundary : (" + bounds.first.x + "," + bounds.first.y + "),"
////                                + "(" + bounds.second.x + "," + bounds.second.y + ")");
//                    } catch (Exception e) {
//                        Log.d("FaceDetector#getBoundaryPoints", "Failed to find boundary points: " + e.getMessage());
//                    }
//                }
//            }
//        }

        return faceBoundaries;
    }

    /**
     * @return the bounding box of the component.
     */
    private static Pair<Point, Point> floodFill(int[] pixels, boolean[] visited, int width, int height, int startX, int startY) {
//        Log.d("FaceDetector#floodFill", String.format("Starting Flood Fill from (%d, %d)", startX, startY));
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalStateException("Flood fill starting from out of bounds position");
        }
        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        int stackTop = 0;
        int offset = startY * width + startX;
        stack[stackTop++] = offset;
        visited[offset] = true;
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
                    if (!visited[newOffset] && isSkinColor(pixels[newOffset])) {
                        stack[stackTop++] = newOffset;
                        visited[newOffset] = true;
                    }
                }
            }
        }
        return new Pair<>(new Point(minX, minY), new Point(maxX, maxY));
    }

    public static Bitmap generateTaggedBitmap(Bitmap bitmap, List<Pair<Point, Point>> boundaryPoints) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
//        for (int x = 0; x < width; ++x) {
//            for (int y = 0; y < height; ++y) {
//                if (FaceDetector.mark[x + y * width]) {
//                    tempBitmap.setPixel(x, y, Color.GREEN);
//                }
//            }
//        }
        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5);
        for(Pair<Point, Point> boundaryPoint : boundaryPoints) {
            canvas.drawRect(Math.max(boundaryPoint.first.x - 2, 0) , Math.max(boundaryPoint.first.y - 2, 0),
                    Math.min(boundaryPoint.second.x + 2, width - 1), Math.min(boundaryPoint.second.y + 2, height - 1), boxPaint);
        }
        return tempBitmap;
    }
}
