//
// Created by Toshiba on 10/26/2015.
//

#include <android/log.h>
#include "image_recognizer.h"

namespace recognizer {

    // Chain Code Recognizer
    static std::multimap<char, std::vector<unsigned char> > savedChainCodes;

    void ChainCodeRecognizer::registerPattern(const std::vector<unsigned char> &pattern, const char value) {
        savedChainCodes.insert(std::make_pair(value, pattern));
    }

    char ChainCodeRecognizer::recognizePattern(const std::vector<unsigned char> &pattern) {
        double timeStart = (double)clock() / CLOCKS_PER_SEC;
        // Circular Dynamic Time Warping
        char best = '?';
        int minError = pattern.size() * 4;

        for (std::multimap<char, std::vector<unsigned char> >::iterator it = savedChainCodes.begin(); it != savedChainCodes.end(); ++it) {
            int distance = calculateCircularDynamicTimeWarping(pattern, it->second);
            if (distance != -1 && distance < minError) {
                best = it->first;
                minError = distance;
            }
        }

        double timeFinish = (double)clock() / CLOCKS_PER_SEC;
        __android_log_print(ANDROID_LOG_INFO, "Tag", "%.3lf ms: [%c] was found with error = %d / %d", (timeFinish - timeStart) * 1e3, best, minError, pattern.size());

        if (minError <= (int)(CHAINCODE_ERROR_THRESHOLD * pattern.size() * 4)) {
            return best;
        } else {
            return '?';
        }
    }

    int ChainCodeRecognizer::calculateCircularDynamicTimeWarping(
            const std::vector<unsigned char> &patternA, const std::vector<unsigned char> &patternB) {
        int lengthA = patternA.size();
        int lengthB = patternB.size();
        int edit[lengthA + 1][lengthB * 2 + 1];

        edit[0][0] = 0;
        for (int iA = 1; iA <= lengthA; ++iA) {
            edit[iA][0] = 4 * iA;
        }
        for (int iB = 0; iB <= lengthB * 2; ++iB) {
            edit[0][iB] = 4 * iB;
        }
        for (int iA = 1; iA <= lengthA; ++iA) {
            unsigned char objectA = patternA[iA - 1];
            for (int iB = 1; iB <= lengthB * 2; ++iB) {
                unsigned char objectB = patternB[(iB - 1) % lengthB];
                int cost = abs((int)objectA - objectB);
                if (cost > 4) {
                    cost = 8 - cost;
                }
                edit[iA][iB] = cost + std::min(edit[iA][iB - 1], std::min(edit[iA - 1][iB], edit[iA - 1][iB - 1]));
            }
        }
        int minEditDistance = lengthA;
        for (int iB = lengthB; iB <= lengthB * 2; ++iB) {
            int nr = lengthA, nc = iB;
            bool untraceable = false;
            while (!untraceable && nr > 0 && nc > iB - lengthB) {
                unsigned char objectA = patternA[nr - 1];
                unsigned char objectB = patternB[(nc - 1) % lengthB];
                int cost = abs((int)objectA - objectB);
                if (cost > 4) {
                    cost = 8 - cost;
                }
                int curEditDistance = edit[nr][nc];
                if (edit[nr - 1][nc] + cost == curEditDistance) {
                    nr--;
                } else if (edit[nr - 1][nc - 1] + cost == curEditDistance) {
                    nr--;
                    nc--;
                } else if (edit[nr][nc - 1] + cost == curEditDistance) {
                    nc--;
                } else {
                    untraceable = true;
                }
            }
            if (!untraceable) {
                int editDistance = (edit[lengthA][iB] - edit[nr][nc]);
                if (editDistance < minEditDistance) {
                    minEditDistance = editDistance;
                }
            }
        }
        return minEditDistance;
    }
}