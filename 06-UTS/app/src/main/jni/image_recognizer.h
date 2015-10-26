//
// Created by Toshiba on 10/26/2015.
//

#pragma once

#include <vector>
#include <string>
#include <map>

#define CHAINCODE_ERROR_THRESHOLD 0.5

namespace recognizer {
    enum RecognizingMethod {
        CHAIN_CODE
    };

    class ChainCodeRecognizer {
    public:
        static void registerPattern(const std::vector<unsigned char> &pattern, const char value);
        static char recognizePattern(const std::vector<unsigned char> &pattern);

    private:
        static int calculateCircularDynamicTimeWarping(const std::vector<unsigned char> &a,
                                                   const std::vector<unsigned char> &b);
    };
}
