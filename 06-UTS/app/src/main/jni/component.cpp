//
// Created by Toshiba on 10/25/2015.
//

#include <android/log.h>
#include "component.h"

std::pair<point, point> floodFill(const std::vector<unsigned char> &pixels, int width, int height,
                                  int startX, int startY, std::vector<bool> &visited) {
//    __android_log_print(ANDROID_LOG_INFO, "Tag", "Starting flood fill from (%d, %d): %d", startX, startY, (int)pixels[startY * width + startX]);
    if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
        // illegal state
        return std::make_pair(std::make_pair(-1, -1), std::make_pair(-1, -1));
    }
    int minX = startX, maxX = startX, minY = startY, maxY = startY;
    std::vector<int> stack;
    int offset = startY * width + startX;
    stack.push_back(offset);
    visited[offset] = 1;
    while (!stack.empty()) {
        int top = stack.back(); stack.pop_back();
        int x = top % width;
        int y = top / width;
        minX = std::min(minX, x);
        minY = std::min(minY, y);
        maxX = std::max(maxX, x);
        maxY = std::max(maxY, y);
        for (int ix = x - 1; ix <= x + 1; ++ix) {
            for (int iy = y - 1; iy <= y + 1; ++iy) {
                if (ix < 0 || ix >= width || iy < 0 || iy >= height) {
                    continue;
                }
                int newOffset = iy * width + ix;
                if (!pixels[newOffset] && !visited[newOffset]) {
                    stack.push_back(newOffset);
                    visited[newOffset] = true;
                }
            }
        }
    }
    return std::make_pair(std::make_pair(minX, minY), std::make_pair(maxX, maxY));
}

std::vector<Component> Component::fromBitmap(const std::vector<unsigned char> &pixel, int width, int height) {
    __android_log_print(ANDROID_LOG_INFO, "Tag", "Getting components from bitmap of size %d x %d", width, height);
    int size = width * height;
    std::vector<bool> visited(size);
    std::vector<Component> result;

    bool found = false, foundUnsuitable = false;
    point firstPoint;
    std::pair<point, point> firstBoundary;

    for (int x = 0; x < width; ++x) {
        int offset = x;
        for (int y = 0; y < height; ++y, offset += width) {
            if (pixel[offset] || visited[offset]) continue;
            std::pair<point, point> boundary = floodFill(pixel, width, height, x, y, visited);
//            __android_log_print(ANDROID_LOG_INFO, "Tag", "Got boundary (%d, %d) ... (%d, %d)", boundary.first.X, boundary.first.Y, boundary.second.X, boundary.second.Y);
            // don't take too large or too small components
            if (!foundUnsuitable) {
                foundUnsuitable = true;
                firstBoundary = boundary;
                firstPoint = std::make_pair(x, y);
            }
            int componentWidth = boundary.second.X - boundary.first.X;
            int componentHeight = boundary.second.Y - boundary.first.Y;
            // discard small components
            if (componentWidth * 30 < width && componentHeight * 30 < height) continue;
            // discard component with weird dimensions
            if (componentWidth > componentHeight * 3) continue;
            // discard too large components
            if ((componentWidth * componentHeight) * 2 > width * height) continue;

            std::vector<unsigned char> chainCode = util::generateChainCode(pixel, width, height, x, y);
            std::vector<std::vector<unsigned char> > grid = util::generateGrid(pixel, width, height, boundary, GRID_SIZE, GRID_SIZE);
            Component component;
            component.boundary = boundary;
            component.chainCode = chainCode;
            component.grid = grid;
            result.push_back(component);
            found = true;
        }
    }
    if (!found && foundUnsuitable) {
        int x = firstPoint.X;
        int y = firstPoint.Y;
        std::vector<unsigned char> chainCode = util::generateChainCode(pixel, width, height, x, y);
        std::vector<std::vector<unsigned char> > grid = util::generateGrid(pixel, width, height, firstBoundary, 5, 5);
        Component component;
        component.boundary = firstBoundary;
        component.chainCode = chainCode;
        component.grid = grid;
        result.push_back(component);
    }
    __android_log_print(ANDROID_LOG_INFO, "Tag", "Done getting components from bitmap of size %d x %d. A total of %d components was found", width, height, result.size());

    return result;
}

