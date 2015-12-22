package gatel.instacit.utils;


import android.graphics.Bitmap;

public class KirschOperator {

    public static Bitmap convertImage(Bitmap image) {
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
                int[] magnitudes = new int[8];
                for(int fx = 0; fx < windowWidth; ++fx) {
                    for(int fy = 0; fy < windowHeight; ++fy) {
                        window[i] = ImageUtils.getGrayscaleColor(image.getPixel(x + edgeX - fx, y + edgeY - fy));
                        ++i;
                    }
                }

                for(int inc = 0; inc < 2; ++inc) {
                    switch (inc) {
                        case 0 :
                            int[] mask0 = new int[] {-1, 0, 1, -2, 0, 2, -1, 0, 1};
                            int sum0 = 0;
                            for(int j = 0; j < window.length; ++j) {
                                sum0 += (mask0[j] * window[j]);
                            }
                            magnitudes[0] = sum0; break;
                        case 1 :
                            int[] mask1 = new int[] {-1, -2, -1, 0, 0, 0, 1, 2, 1};
                            int sum1 = 0;
                            for(int j = 0; j < window.length; ++j) {
                                sum1 += (mask1[j] * window[j]);
                            }
                            magnitudes[1] = sum1; break;
//                        case 2 :
//                            int[] mask2 = new int[] {5, -3, -3, 5, 0, -3, 5, -3, -3};
//                            int sum2 = 0;
//                            for(int j = 0; j < window.length; ++j) {
//                                sum2 += mask2[j] * window[j];
//                            }
//                            magnitudes[2] = sum2; break;
//                        case 3 :
//                            int[] mask3 = new int[] {-3, -3, -3, 5, 0, -3, 5, 5, -3};
//                            int sum3 = 0;
//                            for(int j = 0; j < window.length; ++j) {
//                                sum3 += mask3[j] * window[j];
//                            }
//                            magnitudes[3] = sum3; break;
//                        case 4 :
//                            int[] mask4 = new int[] {-3, -3, -3, -3, 0, -3, 5, 5, 5};
//                            int sum4 = 0;
//                            for(int j = 0; j < window.length; ++j) {
//                                sum4 += mask4[j] * window[j];
//                            }
//                            magnitudes[4] = sum4; break;
//                        case 5 :
//                            int[] mask5 = new int[] {-3, -3, -3, -3, 0, 5, -3, 5, 5};
//                            int sum5 = 0;
//                            for(int j = 0; j < window.length; ++j) {
//                                sum5 += mask5[j] * window[j];
//                            }
//                            magnitudes[5] = sum5; break;
//                        case 6 :
//                            int[] mask6 = new int[] {-3, -3, 5, -3, 0, 5, -3, -3, 5};
//                            int sum6 = 0;
//                            for(int j = 0; j < window.length; ++j) {
//                                sum6 += mask6[j] * window[j];
//                            }
//                            magnitudes[6] = sum6; break;
//                        case 7 :
//                            int[] mask7 = new int[] {-3, 5, 5, -3, 0, 5, -3, -3, -3};
//                            int sum7 = 0;
//                            for(int j = 0; j < window.length; ++j) {
//                                sum7 += mask7[j] * window[j];
//                            }
//                            magnitudes[7] = sum7; break;
                    }
                }
//                Arrays.sort(magnitudes);
                int magnitudeApprox = Math.abs(magnitudes[0]) + Math.abs(magnitudes[1]);
                if(magnitudeApprox < 0) {
                    magnitudeApprox = 0;
                } else if(magnitudeApprox > 255) {
                    magnitudeApprox = 255;
                }
                newImage.setPixel(x, y, magnitudeApprox * ImageUtils.GRAYSCALE_MASK);
            }
        }

        return newImage;
    }
}
