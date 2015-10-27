//
// Created by Toshiba on 10/26/2015.
//

#pragma once

#include <vector>
#include <string>
#include <map>

#define CHAINCODE_ERROR_THRESHOLD 0.5
#define MAX_DTW_LENGTH 2048

namespace recognizer {
    enum RecognizingMethod {
        CHAIN_CODE
    };

    class ChainCodeRecognizer {
    public:
        static void registerPattern(const std::vector<unsigned char> &pattern, const char value);
        static char recognizePattern(const std::vector<unsigned char> &pattern);

    private:
        static double calculateCircularDynamicTimeWarping(const std::vector<unsigned char> &a,
                                                   const std::vector<unsigned char> &b);
        static int calculateCost(unsigned char a, unsigned char b);
    };
}
