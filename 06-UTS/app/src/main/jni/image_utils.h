//
// Created by Toshiba on 10/18/2015.
//

#pragma once

#include <vector>

typedef std::pair<int, int> point;
#define X first
#define Y second

#define GRAYSCALE_MASK 0x00010101
#define BLACKWHITE_MASK 0x00FFFFFF

namespace util {
    unsigned char convertPixelToGrayscale(int pixel);
    unsigned char calculateGrayscaleThreshold(const int *frequency);
    void convertGrayscaleToBinary(std::vector<unsigned char> &pixels, int width, int height, unsigned char threshold, bool invert = true);
    void removeNoise(std::vector<unsigned char> &pixels, int width, int height);

    std::vector<unsigned char> generateChainCode(const std::vector<unsigned char> &pixel, int width, int height, int x, int y);

    std::vector<std::vector<unsigned char> > generateGrid(const std::vector<unsigned char> &pixel, int width, int height, std::pair<point, point> pair, int gridWidth, int gridHeight, bool atLeastOne = false);

}
