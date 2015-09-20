package gatel.carplaterecognition;

import android.graphics.Bitmap;

public class MedianFilter {
    public static Bitmap removeNoise(Bitmap image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int windowWidth = 3, windowHeight = 3;

        Bitmap noiselessImage = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.RGB_565);
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
                sortWindow(window);
                noiselessImage.setPixel(x, y, window[(int) Math.ceil((windowWidth * windowHeight)/2)]);
            }
        }

        return noiselessImage;
    }

    private static void sortWindow(int[] window) {
        int temp;
        for(int i = 0; i < (window.length - 1); ++i) {
            for(int j = 1; j < (window.length - i); ++j){
                if(window[j-1] > window[j]){
                    temp=window[j-1];
                    window[j-1] = window[j];
                    window[j] = temp;
                }
            }
        }
    }
}
