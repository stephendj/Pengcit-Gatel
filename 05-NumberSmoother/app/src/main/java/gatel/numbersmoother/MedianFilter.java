package gatel.numbersmoother;

import android.graphics.Bitmap;

import java.util.Arrays;

public class MedianFilter {

    public static Bitmap removeNoise(Bitmap image) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int windowWidth = 3, windowHeight = 3;

        Bitmap noiselessImage = image.copy(Bitmap.Config.RGB_565, true);
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
                Arrays.sort(window);
                noiselessImage.setPixel(x, y, window[(int) Math.ceil((windowWidth * windowHeight)/2)]);
            }
        }

        return noiselessImage;
    }
}
