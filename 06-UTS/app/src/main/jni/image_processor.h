//
// Created by Toshiba on 10/18/2015.
//

#pragma once

#include "component.h"
#include "image_recognizer.h"
#include <string>

namespace processor {

    void registerBitmap(int *_pixels, int _width, int _height);
    void calculateFrequency();
    void equalize(unsigned char lowerThreshold, unsigned char upperThreshold);
    void updateBinaryThreshold(unsigned char _threshold);

    void process();
    std::string recognizePattern(recognizer::RecognizingMethod method);
    void registerPattern(std::string value);

    int getSize();
    int* getFrequency();
    int* getEqualizedFrequency();
    void getEqualizedImage(int *result);
    void getGrayscaleImage(int *result);
    void getBinaryImage(int *result);
    int getThreshold();
    std::vector<std::vector<int> > getSortedComponent();

    std::vector<Component>& getComponents();
}
