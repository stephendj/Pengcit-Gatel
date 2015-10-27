//
// Created by Toshiba on 10/18/2015.
//

#include <string.h>
#include <algorithm>
#include <android/log.h>
#include "image_utils.h"

namespace util {
    unsigned char convertPixelToGrayscale(int pixel) {
        return (unsigned char)(((pixel & 0xFF) + ((pixel >> 8) & 0xFF) + ((pixel >> 16) & 0xFF)) / 3);
    }

    unsigned char calculateGrayscaleThreshold(const int *frequency) {
        int sum = 0, total = frequency[0];
        for (int i = 1; i < 256; ++i) {
            sum += i * frequency[i];
            total += frequency[i];
        }
        int sumB = 0, wB = 0, wF = 0;
        int mB, mF;
        double max = 0.0, between = 0.0;
        unsigned char threshold1 = 0, threshold2 = 0;
        for (int i = 0; i < 256; ++i) {
            wB += frequency[i];
            if (wB == 0) {
                continue;
            }
            wF = total - wB;
            if (wF == 0) {
                break;
            }
            sumB += i * frequency[i];
            mB = sumB / wB;
            mF = (sum - sumB) / wF;
            between = wB * wF * (mB - mF) * (mB - mF);
            if (between >= max) {
                threshold1 = i;
                if (between > max) {
                    threshold2 = i;
                }
                max = between;
            }
        }
        return ((threshold1 + threshold2) / 2);
    }

    void convertGrayscaleToBinary(std::vector<unsigned char> &pixels, int width, int height, unsigned char threshold, bool invert) {
        unsigned char color0 = 0, color1 = 1;
        if (invert) {
            color0 = 1;
            color1 = 0;
        }
        int size = width * height;
        for (int i = 0; i < size; ++i) {
            pixels[i] = pixels[i] <= threshold ? color0 : color1;
        }
    }

    void removeNoise(std::vector<unsigned char> &pixels, int width, int height) {
        int windowWidth = 3, windowHeight = 3;
        int windowLength = windowWidth * windowHeight, windowMedianPosition = (windowLength + 1) / 2;
        int window[windowLength];
        int edgeX = windowWidth / 2;
        int edgeY = windowHeight / 2;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int occ[2] = {0};
                for (int fx = x - 1; fx <= x + 1; ++fx) {
                    for (int fy = y - 1; fy <= y + 1; ++fy) {
                        if (fx < 0 || fx >= width || fy < 0 || fy >= height) continue;
                        occ[pixels[fx + fy * width] & 1]++;
                    }
                }
                unsigned char& pixel = pixels[x + y * width];
                if (occ[pixel & 1] < occ[~pixel & 1]) {
                    pixel ^= 1;
                }
            }
        }
    }

    struct ChainCodeState {
        point position;
        int direction;

        ChainCodeState(point position, int direction) {
            this->position = position;
            this->direction = direction;
        }
    };

    const int REPLACEMENT[8][2] = {{1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}};

    std::vector<unsigned char> generateChainCode(const std::vector<unsigned char> &pixel, int width, int height, int x, int y) {
//        __android_log_print(ANDROID_LOG_INFO, "Tag", "Generating chain code from (%d, %d)", x, y);
        std::vector<unsigned char> chainCode;

        point firstPosition = std::make_pair(x, y);
        int firstDirection = 7;
        ChainCodeState firstState(firstPosition, firstDirection);
        ChainCodeState state = firstState;
        do {
//            __android_log_print(ANDROID_LOG_INFO, "Tag", "Current position: (%d, %d)", state.position.X, state.position.Y);
            int currentDirection = state.direction;
            point currentPosition = state.position;
            int startNextDirection = ((currentDirection % 2 == 0 ? 7 : 6) + currentDirection) % 8;
            int endNextDirection = (startNextDirection + 7) % 8;
            bool foundNextState = false;

            for (int nextDirection = startNextDirection; nextDirection != endNextDirection;
                 nextDirection = (nextDirection + 1) % 8) {
                point neighbor = std::make_pair(
                        currentPosition.X + REPLACEMENT[nextDirection][0],
                        currentPosition.Y + REPLACEMENT[nextDirection][1]);
//                __android_log_print(ANDROID_LOG_INFO, "Tag", "(%d, %d) -> (%d, %d)", state.position.X, state.position.Y, neighbor.X, neighbor.Y);

                if (0 <= neighbor.X && neighbor.X < width &&
                        0 <= neighbor.Y && neighbor.Y < height) {
                    int offset = neighbor.X + neighbor.Y * width;
                    if (!pixel[offset]) {
                        state = ChainCodeState(neighbor, nextDirection);
                        foundNextState = true;
                        break;
                    }
                }
            }
            if (!foundNextState) break;
            chainCode.push_back(state.direction);
        } while (state.position != firstPosition);

        return chainCode;
    }

    std::vector<std::vector<unsigned char> > generateGrid(const std::vector<unsigned char> &pixel, int width, int height, std::pair<point, point> boundary, int gridWidth, int gridHeight, bool atLeastOne) {
        int dx = boundary.second.X - boundary.first.X + 1;
        int dy = boundary.second.Y - boundary.first.Y + 1;

        std::vector<std::vector<unsigned char> > grid(gridWidth, std::vector<unsigned char>(gridHeight));

        for (int x = 0; x < gridWidth; ++x) {
            for (int y = 0; y < gridHeight; ++y) {
                int sx = boundary.first.X + x * dx / gridWidth;
                int sy = boundary.first.Y + y * dy / gridHeight;
                int ex = boundary.first.X + ((x + 1) * dx + gridWidth - 1) / gridWidth;
                int ey = boundary.first.Y + ((y + 1) * dy + gridHeight - 1) / gridHeight;
                int ntotal = 0, nblack = 0;
                for (int ix = sx; ix < ex; ++ix) {
                    for (int iy = sy; iy < ey; ++iy) {
                        ntotal++;
                        if (pixel[ix + iy * width] == 0) {
                            nblack++;
                        }
                    }
                }
//                 Uncomment for option at least half must be black to be marked
                if (atLeastOne) {
                    grid[x][y] = (nblack > 0) ? 1 : 0;
                } else {
                    grid[x][y] = (nblack * 2 >= ntotal) ? 1 : 0;
                }
            }
        }
        return grid;
    }
}
