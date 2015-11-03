package gatel.facedetection;

import android.graphics.Bitmap;

import java.util.Arrays;

public class FaceDetection {
    public static Bitmap homogenConvert(Bitmap image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int windowWidth = 3, windowHeight = 3;

        Bitmap newImage = image.copy(Bitmap.Config.RGB_565, true);
        int[] window = new int[windowWidth * windowHeight];
        int edgeX = windowWidth/2;
        int edgeY = windowHeight/2;

        for(int x = edgeX; x < (imageWidth - edgeX) ; ++x) {
            for(int y = edgeY; y < (imageHeight - edgeY) ; ++y) {
                int i = 0;
                for(int fx = 0; fx < windowWidth; ++fx) {
                    for(int fy = 0; fy < windowHeight; ++fy) {
                        window[i] = image.getPixel(x + edgeX - fx, y + edgeY - fy);
                        ++i;
                    }
                }
                for(int j = 0; j < window.length; ++i) {
                    if(j != window.length/2) {
                        window[j] = Math.abs(window[window.length/2] - window[j]);
                    }
                }
                Arrays.sort(window);
                newImage.setPixel(x, y, window[window.length-1]);
            }
        }

        return newImage;
    }

    public static Bitmap differenceConvert(Bitmap image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int windowWidth = 3, windowHeight = 3;

        Bitmap newImage = image.copy(Bitmap.Config.RGB_565, true);
        int[] window = new int[windowWidth * windowHeight];
        int edgeX = windowWidth/2;
        int edgeY = windowHeight/2;

        for(int x = edgeX; x < (imageWidth - edgeX) ; ++x) {
            for(int y = edgeY; y < (imageHeight - edgeY) ; ++y) {
                int i = 0;
                for(int fx = 0; fx < windowWidth; ++fx) {
                    for(int fy = 0; fy < windowHeight; ++fy) {
                        window[i] = image.getPixel(x + edgeX - fx, y + edgeY - fy);
                        ++i;
                    }
                }
                int[] array = new int[] {Math.abs(window[0] - window[8]), Math.abs(window[1] - window[7]),
                                            Math.abs(window[2] - window[6]), Math.abs(window[3] - window[5])};
                Arrays.sort(array);
                newImage.setPixel(x, y, array[array.length-1]);
            }
        }

        return newImage;
    }
}
