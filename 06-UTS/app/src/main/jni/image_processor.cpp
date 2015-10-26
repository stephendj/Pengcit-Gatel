//
// Created by Toshiba on 10/18/2015.
//

#include "image_processor.h"
#include "image_utils.h"
#include "component.h"
#include <android/log.h>

namespace processor {
    // All variables are meant to be reused. For instance, the gray scale pixels were stored statically.

    // Bitmap definition
    static std::vector<unsigned char> grayscalePixels;
    static std::vector<unsigned char> equalizedPixels;
    static std::vector<unsigned char> binaryPixels;
    static int width = 0;
    static int height = 0;
    static unsigned char threshold = 0;

    // Equalizer stuff
    static int frequency[256];
    static int cumulativeFrequency[256];
    static int equalizedFrequency[256];
    static int equalizedCumulativeFrequency[256];

    // recognizer stuff
    static std::vector<Component> components;

    void registerBitmap(int *_pixels, int _width, int _height) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Registering bitmap of size %d x %d", _width, _height);
        grayscalePixels.clear();

        width = _width;
        height = _height;
        int i = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                grayscalePixels.push_back(util::convertPixelToGrayscale(_pixels[i]));
                ++i;
            }
        }
        grayscalePixels;
        calculateFrequency();

        updateBinaryThreshold(util::calculateGrayscaleThreshold(frequency));

        equalizedPixels = grayscalePixels;
        equalize(0, 255);
    }

    void calculateFrequency() {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Calculating frequencies");
        memset(frequency, 0, sizeof(frequency));
        for (int i = 0; i < width * height; ++i) {
            frequency[grayscalePixels[i]]++;
        }
        cumulativeFrequency[0] = frequency[0];
        for (int i = 1; i < 256; ++i) {
            cumulativeFrequency[i] = frequency[i] + cumulativeFrequency[i - 1];
        }
    }

    void equalize(unsigned char lowerThreshold, unsigned char upperThreshold) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Equalizing bitmap of size %d x %d", width, height);
        int pixelMap[256];
        int size = getSize();
        int totalPixel = cumulativeFrequency[255];
        int diffThreshold = upperThreshold - lowerThreshold;
        memset(equalizedFrequency, 0, sizeof(equalizedFrequency));
        for (int i = 0; i < 256; ++i) {
            pixelMap[i] = cumulativeFrequency[i] * diffThreshold / totalPixel + lowerThreshold;
            equalizedFrequency[pixelMap[i]] += frequency[i];
        }
        equalizedCumulativeFrequency[0] = equalizedFrequency[0];
        for (int i = 1; i < 256; ++i) {
            equalizedCumulativeFrequency[i] = equalizedCumulativeFrequency[i - 1] + equalizedFrequency[i];
        }
        for (int i = 0; i < size; ++i) {
            equalizedPixels[i] = pixelMap[grayscalePixels[i]];
        }
    }

    void updateBinaryThreshold(unsigned char _threshold) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Updating threshold to %d", _threshold);
        threshold = _threshold;

        // guess whether we should invert the color or not
        int frequencyBelowThreshold = 0, frequencyAboveThreshold = 0;
        for (int i = 0; i <= threshold; ++i) {
            frequencyBelowThreshold += frequency[i];
        }
        for (int i = threshold + 1; i < 256; ++i) {
            frequencyAboveThreshold += frequency[i];
        }
        bool invert = frequencyBelowThreshold > frequencyAboveThreshold;

        binaryPixels = grayscalePixels;
        util::convertGrayscaleToBinary(binaryPixels, width, height, threshold, invert);
        util::removeNoise(binaryPixels, width, height);

        process();
    }

    void process() {
        components = Component::fromBitmap(binaryPixels, width, height);
    }

    std::string recognizePattern(recognizer::RecognizingMethod method) {
        std::string result = "";
        if (method == recognizer::CHAIN_CODE) {
            for (int i = 0; i < components.size(); ++i) {
                result += recognizer::ChainCodeRecognizer::recognizePattern(components[i].chainCode);
            }
        }
        return result;
    }

    void registerPattern(std::string value) {
        int size = value.size();
        if (size != components.size()) {
            __android_log_print(ANDROID_LOG_INFO, "Tag", "Failed to register pattern. Expecting %d values, but %d was found", size, components.size());
        } else {
            for (int i = 0; i < size; ++i) {
                recognizer::ChainCodeRecognizer::registerPattern(components[i].chainCode, value[i]);
            }
            __android_log_print(ANDROID_LOG_INFO, "Tag", "Successfully register pattern: %s", value.c_str());
        }
    }

    int getSize() {
        return width * height;
    }

    int* getFrequency() {
        return frequency;
    }

    int* getEqualizedFrequency() {
        return equalizedFrequency;
    }

    void getEqualizedImage(int *result) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Getting equalized image");
        for (int i = 0; i < width * height; ++i) {
            result[i] = GRAYSCALE_MASK * equalizedPixels[i];
        }
    }

    void getGrayscaleImage(int *result) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Getting grayscale image");
        for (int i = 0; i < width * height; ++i) {
            result[i] = GRAYSCALE_MASK * grayscalePixels[i];
        }
    }

    void getBinaryImage(int *result) {
        __android_log_print(ANDROID_LOG_INFO, "Tag", "Getting binary image");
        for (int i = 0; i < width * height; ++i) {
            result[i] = BLACKWHITE_MASK * binaryPixels[i];
        }
    }

    int getThreshold() {
        return (int)threshold;
    }

    std::vector<Component>& getComponents() {
        return components;
    }
}