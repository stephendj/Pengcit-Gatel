package binangkit.lingga.jelink.xx_facerecognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Debug;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class KMeans {

    public static Bitmap makeBlackAndWhite(Bitmap image, int threshold) {
        Bitmap bwImage = image.copy(image.getConfig(), true);
        for (int j = 0; j < bwImage.getHeight(); j++) {
            for (int i = 0; i < bwImage.getWidth(); i++) {
                int color = image.getPixel(i, j);

                // get each color RGB
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                // count grayscale
                int greyColor = (int) (red + green + blue) / 3;

                // compare with threshold
                if (greyColor < threshold) {
                    bwImage.setPixel(i, j, Color.BLACK);
                } else {
                    bwImage.setPixel(i, j, Color.WHITE);
                }
            }
        }
        return bwImage;
    }

    public static Bitmap makeMarkedClusterImage(Bitmap image) {
        Bitmap processing_image = image.copy(image.getConfig(), true);

        // left eye, green
        Point leftEyeCentroid = getEyeCentroid(image, false);
        Point prev_leftEyeCentroid = new Point(-1,-1);
        ArrayList<Point> leftEyePoints = new ArrayList<>();

        // right eye, red
        Point rightEyeCentroid = getEyeCentroid(image, true);
        Point prev_rightEyeCentroid = new Point(-1,-1);
        ArrayList<Point> rightEyePoints = new ArrayList<>();

        // nose, blue
        Point noseCentroid = getNoseCentroid(image);
        Point prev_noseCentroid = new Point(-1,-1);
        ArrayList<Point> nosePoints = new ArrayList<>();

        // mouth, red
        Point mouthCentroid = getMouthCentroid(image);
        Point prev_mouthCentroid = new Point(-1,-1);
        ArrayList<Point> mouthPoints = new ArrayList<>();

        ArrayList<Point> centroids = new ArrayList<>();

        // masukkan seluruh centroid ke list
        centroids.add(leftEyeCentroid);
        centroids.add(rightEyeCentroid);
        centroids.add(noseCentroid);
        centroids.add(mouthCentroid);

        while (!noseCentroid.equals(prev_noseCentroid) ||
                !mouthCentroid.equals(prev_mouthCentroid) ||
                !leftEyeCentroid.equals(prev_leftEyeCentroid) ||
                !rightEyeCentroid.equals(prev_rightEyeCentroid)) {

            // backup centroids
            prev_leftEyeCentroid = leftEyeCentroid;
            prev_rightEyeCentroid = rightEyeCentroid;
            prev_noseCentroid = noseCentroid;
            prev_mouthCentroid = mouthCentroid;

            for (int j = 0; j < image.getHeight(); j++) {
                for (int i = 0; i < image.getWidth(); i++) {
                    if (image.getPixel(i, j) == Color.BLACK && (j!=0 && i!=0 && j!=image.getHeight()-1 && i!=image.getWidth()-1)) {
                        int idx = getClosestCentroidIndex(centroids, new Point(i, j));
                        switch (idx) {
                            case 0:
                                processing_image.setPixel(i, j, Color.BLUE);
                                leftEyePoints.add(new Point(i, j));
                                break;
                            case 1:
                                processing_image.setPixel(i, j, Color.RED);
                                rightEyePoints.add(new Point(i, j));
                                break;
                            case 2:
                                processing_image.setPixel(i, j, Color.GREEN);
                                nosePoints.add(new Point(i, j));
                                break;
                            case 3:
                                processing_image.setPixel(i, j, Color.CYAN);
                                mouthPoints.add(new Point(i, j));
                                break;
                        }
//                        System.out.println("pada " + i + ", " + j + " indexnya " + idx);
                    } else if(j==0 || i==0 || j==image.getHeight()-1 || i==image.getWidth()-1) {
                        processing_image.setPixel(i, j, Color.WHITE);
                    }
                }
            }

            //update centroids
            leftEyeCentroid = updateCentroid(leftEyePoints);
            rightEyeCentroid = updateCentroid(rightEyePoints);
            noseCentroid = updateCentroid(nosePoints);
            mouthCentroid = updateCentroid(mouthPoints);
        }

        return processing_image;
    }

    private static Point updateCentroid (ArrayList<Point> collection) {
        int new_x = 0;
        int new_y = 0;
        for (Point p : collection) {
            new_x += p.x;
            new_y += p.y;
        }
        if(collection.size() != 0) {
            new_x = new_x/collection.size();
            new_y = new_y/collection.size();
        }

        System.out.println("Update centroid ke : " + new_x + ", " + new_y);
        return new Point(new_x, new_y);
    }

    public static int getClosestCentroidIndex(ArrayList<Point> centroids, Point point) {
        ArrayList<Float> distToCentroids = new ArrayList<>();
        for (int i=0 ; i<centroids.size() ; i++) {
            distToCentroids.add((float) Math.sqrt(Math.pow(centroids.get(i).x - point.x, 2) + Math.pow(centroids.get(i).y - point.y, 2)));
        }
        float smallestdist = Float.MAX_VALUE;
        int smallest_index = -1;
        int i = 0;
        for (Float dist : distToCentroids) {
            if (dist < smallestdist) {
                smallestdist = dist;
                smallest_index = i;
            }
            i++;
        }
        return smallest_index;
    }

    public static Point getEyeCentroid(Bitmap image, boolean isRight) {
        for (int j=image.getHeight()/2 ; j>=0 ; j--) {
            int rightBoundary;
            int leftBoundary;
            if (isRight) {
                leftBoundary = image.getWidth()/2;
                rightBoundary = image.getWidth();
            } else {
                leftBoundary = 0;
                rightBoundary = image.getWidth()/2;
            }
            for (int i=leftBoundary ; i<rightBoundary ; i++) {
                if (image.getPixel(i,j) == Color.BLACK) {
                    return new Point(i,j);
                }
            }
        }

        return new Point(-1,-1);
    }

    public static Point getNoseAndMouthCentroid(Bitmap image) {
        int j = image.getHeight()/2;
        int x = image.getWidth()/2;

        while (j < image.getHeight()) {
            // bila merupakan warna hitam, maka hidung
            if (image.getPixel(x, j) == Color.BLACK) {
                return new Point(x,j);
            }
            j++;
        }

        return new Point(-1, -1);
    }

    public static Point getNoseCentroid(Bitmap image) {
        int j = image.getHeight()/2;
        int x = image.getWidth()/2;

        while (j < image.getHeight()) {
            // bila merupakan warna hitam, maka hidung
            if (image.getPixel(x, j) == Color.BLACK) {
                return new Point(x,j);
            }
            j++;
        }

        return new Point(-1, -1);
    }

    public static Point getMouthCentroid(Bitmap image) {
        int j = image.getHeight()-1;
        int x = image.getWidth()/2;

        while (j >= image.getHeight()/2) {
            // bila merupakan warna hitam, maka mulut
            if (image.getPixel(x, j) == Color.BLACK) {
                return new Point(x,j);
            }
            j--;
        }

        return new Point(-1, -1);
    }

}
