//
// Created by Toshiba on 10/18/2015.
//

#pragma once

#include <vector>

namespace equalizer {
    void registerBitmap(int *_pixels, int _width, int _height);
    void calculateFrequency();
    void equalize(unsigned char lowerThreshold, unsigned char upperThreshold, int* result);
    int getSize();
    int* getFrequency();
}
