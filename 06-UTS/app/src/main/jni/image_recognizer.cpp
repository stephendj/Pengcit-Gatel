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

    // For circular DTW
    int edit[MAX_DTW_LENGTH + 1][MAX_DTW_LENGTH + 1];

    int costs[8][8] = {
            {0, 1, 4, 9, 16, 9, 4, 1},
            {1, 0, 1, 4, 9, 16, 9, 4},
            {4, 1, 0, 1, 4, 9, 16, 9},
            {9, 4, 1, 0, 1, 4, 9, 16},
            {16, 9, 4, 1, 0, 1, 4, 9},
            {9, 16, 9, 4, 1, 0, 1, 4},
            {4, 9, 16, 9, 4, 1, 0, 1},
            {1, 4, 9, 16, 9, 4, 1, 0}
    };
    char currentlyExaminedCharacter; // for debugging
    char ChainCodeRecognizer::recognizePattern(const std::vector<unsigned char> &pattern) {
        double timeStart = (double)clock() / CLOCKS_PER_SEC;
        // Circular Dynamic Time Warping
        char best = '?';
        double minError = 1;

        for (std::multimap<char, std::vector<unsigned char> >::iterator it = savedChainCodes.begin(); it != savedChainCodes.end(); ++it) {
            currentlyExaminedCharacter = it->first;
            double error = calculateDynamicTimeWarping(pattern, it->second);
            if (error < minError) {
                best = it->first;
                minError = error;
            }
        }

        double timeFinish = (double)clock() / CLOCKS_PER_SEC;
        __android_log_print(ANDROID_LOG_INFO, "Tag", "%.3lf ms: [%c] was found with error = %.3lf %%", (timeFinish - timeStart) * 1e3, best, minError * 100);

        if (minError <= CHAINCODE_ERROR_THRESHOLD) {
            return best;
        } else {
            return '?';
        }
    }

    double ChainCodeRecognizer::calculateDynamicTimeWarping(
            const std::vector<unsigned char> &patternA, const std::vector<unsigned char> &patternB) {
        double timeStart = (double)clock() / CLOCKS_PER_SEC;
        int lengthA = std::min((int)patternA.size(), MAX_DTW_LENGTH);
        int lengthB = std::min((int)patternB.size(), MAX_DTW_LENGTH / 2);

        edit[0][0] = 0;
        for (int iA = 1; iA <= lengthA; ++iA) {
            edit[iA][0] = 16 * iA;
        }
        for (int iB = 1; iB <= lengthB * 2; ++iB) {
            edit[0][iB] = 16 * iB;
        }
        for (int iA = 1; iA <= lengthA; ++iA) {
            unsigned char objectA = patternA[iA - 1];
            for (int iB = 1; iB <= lengthB; ++iB) {
                unsigned char objectB = patternB[iB - 1];
                int cost = costs[objectA][objectB];
                edit[iA][iB] = cost + std::min(edit[iA][iB - 1], std::min(edit[iA - 1][iB], edit[iA - 1][iB - 1]));
                // double step
                if (iA >= 2 && iB >= 2) {
                    cost += costs[patternA[iA - 2]][patternB[iB - 2]];
                    edit[iA][iB] = std::min(edit[iA][iB], cost + edit[iA - 2][iB - 2]);
                }
            }
        }
        int minEditDistance = edit[lengthA][lengthB];
        double timeFinish = (double)clock() / CLOCKS_PER_SEC;
        __android_log_print(ANDROID_LOG_INFO, "Tag", "%.3lf ms: Calculating DTW for [%c]: DTW size %d x %d: minError = %d (%.2lf %%)", (timeFinish - timeStart) * 1e3, currentlyExaminedCharacter, lengthA, lengthB, minEditDistance, (double)minEditDistance * 100. / lengthB);
        return (double)minEditDistance / lengthB;
    }

    double ChainCodeRecognizer::calculateCircularDynamicTimeWarping(
            const std::vector<unsigned char> &patternA, const std::vector<unsigned char> &patternB) {
        double timeStart = (double)clock() / CLOCKS_PER_SEC;
        int lengthA = std::min((int)patternA.size(), MAX_DTW_LENGTH);
        int lengthB = std::min((int)patternB.size(), MAX_DTW_LENGTH / 2);

        edit[0][0] = 0;
        for (int iA = 1; iA <= lengthA; ++iA) {
            edit[iA][0] = 16 * iA;
        }
        for (int iB = 1; iB <= lengthB * 2; ++iB) {
            edit[0][iB] = 16 * iB;
        }
        for (int iA = 1; iA <= lengthA; ++iA) {
            unsigned char objectA = patternA[iA - 1];
            for (int iB = 1; iB <= lengthB; ++iB) {
                unsigned char objectB = patternB[iB - 1];
                int cost = costs[objectA][objectB];
                edit[iA][iB] = cost + std::min(edit[iA][iB - 1], std::min(edit[iA - 1][iB], edit[iA - 1][iB - 1]));
                // double step
                if (iA >= 2 && iB >= 2) {
                    cost += costs[patternA[iA - 2]][patternB[iB - 2]];
                    edit[iA][iB] = std::min(edit[iA][iB], cost + edit[iA - 2][iB - 2]);
                }
            }
            for (int iB = 1; iB <= lengthB; ++iB) {
                unsigned char objectB = patternB[iB - 1];
                int cost = costs[objectA][objectB];
                edit[iA][iB + lengthB] = cost + std::min(edit[iA][iB + lengthB - 1], std::min(edit[iA - 1][iB + lengthB], edit[iA - 1][iB + lengthB - 1]));
                // double step
                if (iA >= 2 && iB >= 2) {
                    cost += costs[patternA[iA - 2]][patternB[iB - 2]];
                    edit[iA][iB + lengthB] = std::min(edit[iA][iB + lengthB], cost + edit[iA - 2][iB + lengthB - 2]);
                }
            }
        }
        int minEditDistance = lengthB;
        for (int iB = lengthB; iB <= lengthB * 2; ++iB) {
            int nr = lengthA, nc = iB;
            bool untraceable = false;
            while (!untraceable && nr > 0 && nc > iB - lengthB) {
                unsigned char objectA = patternA[nr - 1];
                unsigned char objectB = (nc - 1 < lengthB) ? patternB[nc - 1] : patternB[(nc - 1) - lengthB];
                int cost = costs[objectA][objectB];
                int curEditDistance = edit[nr][nc];
                if (edit[nr - 1][nc] + cost == curEditDistance) {
                    nr--;
                } else if (edit[nr - 1][nc - 1] + cost == curEditDistance) {
                    nr--;
                    nc--;
                } else if (edit[nr][nc - 1] + cost == curEditDistance) {
                    nc--;
                } else if (nr >= 2 && nc >= 2 + iB - lengthB) {
                    cost += costs[patternA[nr - 2]][nc - 2 < lengthB ? patternB[nc - 2] : patternB[nc - 2 - lengthB]];
                    if (curEditDistance == cost + edit[nr - 2][nc - 2]) {
                        nr -= 2;
                        nc -= 2;
                    } else {
                        untraceable = true;
                    }
                }
            }
            if (!untraceable) {
                int editDistance = (edit[lengthA][iB] - edit[nr][nc]);
                if (editDistance < minEditDistance) {
                    minEditDistance = editDistance;
                }
            }
        }
        double timeFinish = (double)clock() / CLOCKS_PER_SEC;
        __android_log_print(ANDROID_LOG_INFO, "Tag", "%.3lf ms: Calculating circular DTW for [%c]: DTW size %d x %d: minError = %d (%.2lf %%)", (timeFinish - timeStart) * 1e3, currentlyExaminedCharacter, lengthA, lengthB, minEditDistance, (double)minEditDistance * 100. / lengthB);
        return (double)minEditDistance / lengthB;
    }
}