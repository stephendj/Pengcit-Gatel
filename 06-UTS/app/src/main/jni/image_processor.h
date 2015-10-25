//
// Created by Toshiba on 10/18/2015.
//

#pragma once

namespace processor {
    void registerBitmap(int *_pixels, int _width, int _height);
    void calculateFrequency();
    void equalize(unsigned char lowerThreshold, unsigned char upperThreshold);
    int getSize();
    int* getFrequency();
    int* getEqualizedFrequency();
    void getEqualizedImage(int *result);
    void getGrayscaleImage(int *result);
    void getBinaryImage(int *result);
}
