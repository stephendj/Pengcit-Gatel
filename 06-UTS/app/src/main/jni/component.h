//
// Created by Toshiba on 10/25/2015.
//

#pragma once

#include <vector>
#include "image_utils.h"

#define GRID_SIZE 10

struct Component {
    std::vector<unsigned char> chainCode;
    std::vector<std::vector<unsigned char> > grid;
    std::pair<point, point> boundary;

    static std::vector<Component> fromBitmap(const std::vector<unsigned char> &pixel, int width, int height);
};

