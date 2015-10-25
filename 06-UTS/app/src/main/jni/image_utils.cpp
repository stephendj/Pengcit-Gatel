//
// Created by Toshiba on 10/18/2015.
//

#include <string.h>
#include <algorithm>
#include <android/log.h>
#include "image_utils.h"

namespace util {
    unsigned char convertPixelToGrayscale(int pixel) {
        return (unsigned char)(((pixel & 0xFF) + ((pixel >> 8) & 0xFF) + ((pixel >> 16) & 0xFF)) / 3);
    }

    unsigned char calculateGrayscaleThreshold(const std::vector<unsigned char> &pixels, int width, int height, const int *frequency) {
        int sum = 0, total = 0;
        for (int i = 1; i < 256; ++i) {
            sum += i * frequency[i];
            total += frequency[i];
        }
        int sumB = 0, wB = 0, wF = 0;
        int mB, mF;
        double max = 0.0, between = 0.0;
        unsigned char threshold1 = 0, threshold2 = 0;
        for (int i = 0; i < 256; ++i) {
            wB += frequency[i];
            if (wB == 0) {
                continue;
            }
            wF = total - wB;
            if (wF == 0) {
                break;
            }
            sumB += i * frequency[i];
            mB = sumB / wB;
            mF = (sum - sumB) / wF;
            between = wB * wF * (mB - mF) * (mB - mF);
            if (between >= max) {
                threshold1 = i;
                if (between > max) {
                    threshold2 = i;
                }
                max = between;
            }
        }
        return ((threshold1 + threshold2) / 2);
    }

    void convertGrayscaleToBinaryAndRemoveNoise(std::vector<unsigned char> &pixels, int width, int height, const int *frequency) {
        unsigned char threshold = calculateGrayscaleThreshold(pixels, width, height, frequency);
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Threshold = %d", (int)threshold);

        for (int i = 0; i < width * height; ++i) {
            if (pixels[i] <= threshold) {
                pixels[i] = 0;
            } else {
                pixels[i] = 1;
            }
        }

//        int windowWidth = 3, windowHeight = 3;
//        int windowLength = windowWidth * windowHeight, windowMedianPosition = (windowLength + 1) / 2;
//        int window[windowLength];
//        int edgeX = windowWidth / 2;
//        int edgeY = windowHeight / 2;
//
//        for (int x = edgeX; x < (width - edgeX) ; ++x) {
//            for (int y = edgeY; y < (height - edgeY) ; ++y) {
//                int occ[2] = {0};
//                for (int fx = 0; fx < windowWidth; ++fx) {
//                    for (int fy = 0; fy < windowHeight; ++fy) {
//                        occ[pixels[(x + edgeX - fx) + (y + edgeY - fy) * width] & 1]++;
//                    }
//                }
//                if (occ[pixels[x + y * width] & 1] < 4) {
//                    pixels[x + y * width] ^= 1;
//                }
//            }
//        }
    }
}
