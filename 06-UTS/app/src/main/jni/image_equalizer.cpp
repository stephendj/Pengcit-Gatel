//
// Created by Toshiba on 10/18/2015.
//

#include "image_equalizer.h"
#include "image_utils.h"
#include <android/log.h>

namespace equalizer {
    // All variables are meant to be reused. For instance, the gray scale pixels were stored statically.

    // Bitmap definition
    static std::vector<unsigned char> grayscalepixels;
    static int width = 0;
    static int height = 0;

    // Equalizer stuff
    static int frequency[256];

    void registerBitmap(int *_pixels, int _width, int _height) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Registering bitmap of size %d x %d", _width, _height);
        grayscalepixels.clear();

        width = _width;
        height = _height;
        int i = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                grayscalepixels.push_back(convertPixelToGrayscale(_pixels[i]));
                ++i;
            }
        }

        calculateFrequency();
    }

    void calculateFrequency() {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Calculating frequencies");
        memset(frequency, 0, sizeof(frequency));
        for (int i = 0; i < 256; ++i) {
            frequency[grayscalepixels[i]]++;
        }
        for (int i = 1; i < 256; ++i) {
            frequency[i] += frequency[i-1];
        }
    }

    void equalize(unsigned char lowerThreshold, unsigned char upperThreshold, int *result) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Equalizing bitmap of size %d x %d", width, height);
        int pixelMap[256];
        int size = getSize();
        int totalPixel = frequency[255];
        int diffThreshold = upperThreshold - lowerThreshold;
        for (int i = 0; i < 256; ++i) {
            pixelMap[i] = frequency[i] * diffThreshold / totalPixel + lowerThreshold;
        }
        for (int i = 0; i < size; ++i) {
            result[i] = pixelMap[grayscalepixels[i]] * GRAYSCALE_MASK;
        }
    }

    int getSize() {
        return width * height;
    }

    int* getFrequency() {
        return frequency;
    }
}