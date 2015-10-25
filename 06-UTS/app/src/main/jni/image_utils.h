//
// Created by Toshiba on 10/18/2015.
//

#pragma once

#include <vector>

#define GRAYSCALE_MASK 0x00010101
#define BLACKWHITE_MASK 0x00FFFFFF

namespace util {
    unsigned char convertPixelToGrayscale(int pixel);
    unsigned char calculateGrayscaleThreshold(const std::vector<unsigned char> &pixels, int width, int height, const int *frequency);
    void convertGrayscaleToBinaryAndRemoveNoise(std::vector<unsigned char> &pixels, int width, int height, const int *frequency);
}
