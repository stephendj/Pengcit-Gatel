package gatel.instacit.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

public class FacePartConverter {

    public enum Part {
        LEFT_EYE,
        RIGHT_EYE,
        NOSE,
        MOUTH
    }

    public static int[][] getFacePartMatrix(Bitmap image, Part facePart) {
        int[][] partPixels;
        int boxWidth, boxHeight;

        switch(facePart) {
            case LEFT_EYE:
            case RIGHT_EYE:
            case MOUTH:
                partPixels = new int[60][40]; boxWidth = 60; boxHeight = 40;
                break;
            case NOSE:
                partPixels = new int[40][60]; boxWidth = 40; boxHeight = 60;
                break;
            default :
                throw new IllegalStateException("Unknown face part: " + facePart);
        }

        double dx = (double) image.getWidth() / boxWidth;
        double dy = (double) image.getHeight() / boxHeight;

        int i = 0;
        int nbBlack = 0;

        for (double x = 0; Math.round(x) < image.getWidth(); x += dx) {
            for (double y = 0; Math.round(y) < image.getHeight(); y += dy) {

                int rightBoundary = (int) Math.round(x + dx);
                if (rightBoundary > image.getWidth()) {
                    rightBoundary = image.getWidth();
                }
                int bottomBoundary = (int) Math.round(y + dy);
                if (bottomBoundary > image.getHeight()) {
                    bottomBoundary = image.getHeight();
                }

                for (int xi = (int) Math.round(x); xi < rightBoundary; xi++) {
                    for (int yi = (int) Math.round(y); yi < bottomBoundary; yi++) {
                        int color = image.getPixel(xi, yi);
                        if (color == Color.BLACK) {
                            nbBlack++;
                        }
                    }
                }

                if (nbBlack > 0) {
                    partPixels[i/boxHeight][i%boxHeight] = 1;
                } else {
                    partPixels[i/boxHeight][i%boxHeight] = 0;
                }

                i++;
                nbBlack = 0;
            }
        }

        return partPixels;
    }

}
