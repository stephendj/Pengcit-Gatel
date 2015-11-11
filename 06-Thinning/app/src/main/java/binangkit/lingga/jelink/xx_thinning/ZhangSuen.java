package binangkit.lingga.jelink.xx_thinning;

/**
 * Created by jelink on 10/17/2015.
 * Reference : http://rosettacode.org/wiki/Zhang-Suen_thinning_algorithm#Java
 */

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.*;

public class ZhangSuen {

    static Bitmap thinBitmap;

    static final int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
            {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    static final int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6},
            {0, 4, 6}}};

    static Set<Point> toWhite = new HashSet<>();

    /* Dihapus aja nanti kalau inputnya udah Binary Black di atas White */
    private static boolean isBlack(int pixel) {
        int grayscalePixel = (((pixel >> 16) & 0xFF) + ((pixel >> 8) & 0xFF) + (pixel & 0xFF)) / 3;
        return grayscalePixel <= 0x50;
    }

    /* Dihapus aja nanti kalau inputnya udah Binary Black di atas White */
    private static void setWhite(int r, int c) {
        thinBitmap.setPixel(c, r, -1);
    }

    static Bitmap thinImageBitmap(Bitmap bitmap) {
        thinBitmap = bitmap.copy(bitmap.getConfig(), true);

        boolean firstStep = false;
        boolean hasChanged;

        do {
            hasChanged = false;
            firstStep = !firstStep;

            for (int r = 1; r < thinBitmap.getHeight() - 1; r++) {
                for (int c = 1; c < thinBitmap.getWidth() - 1; c++) {

                    if (!isBlack(thinBitmap.getPixel(c, r))) {
                        setWhite(r ,c); /* setWhite Dihapus aja nanti kalau inputnya udah Binary Black di atas White */
                        continue;
                    }

                    int nn = numNeighborsBitmap(r, c);
                    if (nn < 2 || nn > 6)
                        continue;

                    if (numTransitionsBitmap(r, c) != 1)
                        continue;

                    if (!atLeastOneIsWhiteBitmap(r, c, firstStep ? 0 : 1))
                        continue;

                    toWhite.add(new Point(c, r));
                    hasChanged = true;
                }
            }

            for (Point p : toWhite) {
                System.out.println("turn white : " + p.x + ", " + p.y);
                thinBitmap.setPixel(p.x, p.y, -1);
            }
            toWhite.clear();

        } while (hasChanged || firstStep);

        return thinBitmap;
    }

    static int numNeighborsBitmap(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++) {
            if (isBlack(thinBitmap.getPixel(c + nbrs[i][0], r + nbrs[i][1]))) {
                count++;
            }
        }
        return count;
    }

    static int numTransitionsBitmap(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (!isBlack(thinBitmap.getPixel(c + nbrs[i][0], r + nbrs[i][1]))) {
                if (isBlack(thinBitmap.getPixel(c + nbrs[i + 1][0], r + nbrs[i + 1][1])))
                    count++;
            }
        return count;
    }

    static boolean atLeastOneIsWhiteBitmap(int r, int c, int step) {
        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = nbrs[group[i][j]];
                if (!isBlack(thinBitmap.getPixel(c + nbr[0], r + nbr[1]))) {
                    count++;
                    break;
                }
            }
        return count > 1;
    }

}