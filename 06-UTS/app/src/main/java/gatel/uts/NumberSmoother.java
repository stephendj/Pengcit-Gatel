package gatel.uts;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Pair;

public class NumberSmoother {
    public static Bitmap getNumberBitmap(Bitmap bitmap, Pair<Point, Point> boundary) {
        Bitmap numberBitmap = Bitmap.createBitmap(5, 5, Bitmap.Config.RGB_565);

        double dx = (double) (boundary.second.x - boundary.first.x) / 5;
        double dy = (double) (boundary.second.y - boundary.first.y) / 5;

        int j = 0;
        int nbBlack = 0;
        int nbTotal = 0;

        for (double x = boundary.first.x; Math.round(x) < boundary.second.x; x += dx) {
            for (double y = boundary.first.y; Math.round(y) < boundary.second.y; y += dy) {
                // cari boundary
                int rightBoundary = (int) Math.round(x + dx);
                if (rightBoundary > boundary.second.x - dx) {
                    rightBoundary = boundary.second.x + 1;
                }
                int bottomBoundary = (int) Math.round(y + dy);
                if (bottomBoundary > boundary.second.y - dy) {
                    bottomBoundary = boundary.second.y + 1;
                }
                // iterasikan di dalam kotak 1/5
                for (int xi = (int) Math.round(x); xi < rightBoundary; xi++) {
                    for (int yi = (int) Math.round(y); yi < bottomBoundary; yi++) {
                        if (ColorUtils.isBlack(bitmap.getPixel(xi,yi))) {
                            nbBlack++;
                        }
                        nbTotal++;
                    }
                }
                // hitung hasil per kotak 1/5
                if (nbBlack >= nbTotal / 2) {
                    System.out.println("set " + j / 5 + ", " + j % 5 + " : black");
                    numberBitmap.setPixel(j / 5, j % 5, ColorUtils.BLACK);
                } else {
                    System.out.println("set " + j / 5 + ", " + j % 5 + " : white");
                    numberBitmap.setPixel(j / 5, j % 5, ColorUtils.WHITE);
                }
                nbBlack = 0;
                nbTotal = 0;
                j++;
            }
        }

        return numberBitmap;
    }
}
