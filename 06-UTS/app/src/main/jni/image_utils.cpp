//
// Created by Toshiba on 10/18/2015.
//

#include "image_utils.h"

unsigned char convertPixelToGrayscale(int pixel) {
    return (unsigned char)(((pixel & 0xFF) + ((pixel >> 8) & 0xFF) + ((pixel >> 16) & 0xFF)) / 3);
}
