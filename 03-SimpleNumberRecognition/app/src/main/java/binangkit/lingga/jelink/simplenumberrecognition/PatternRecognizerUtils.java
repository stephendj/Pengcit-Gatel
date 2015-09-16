package binangkit.lingga.jelink.simplenumberrecognition;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatternRecognizerUtils {

    private static final int BLACK_THRESHOLD = 0x0F;
    public static final int WHITE = 0xFFFFFFFF;

    private PatternRecognizerUtils() {
        // Utility class
    }

    public static boolean isBlack(int pixel) {
        int grayscalePixel = (((pixel << 16) & 0xFF) + ((pixel << 8) & 0xFF) + (pixel & 0xFF)) / 3;
        return grayscalePixel <= BLACK_THRESHOLD;
    }

    public static <T> int calculateEditDistance(List<T> patternA, List<T> patternB) {
        int lengthA = patternA.size();
        int lengthB = patternB.size();
        int edit[][] = new int[lengthA + 1][lengthB + 1];

        for (int iA = 0; iA <= lengthA; ++iA) {
            edit[iA][0] = iA;
        }
        for (int iB = 0; iB <= lengthB; ++iB) {
            edit[0][iB] = 0;
        }
        for (int iA = 1; iA <= lengthA; ++iA) {
            T objectA = patternA.get(iA - 1);
            for (int iB = 1; iB <= lengthB; ++iB) {
                T objectB = patternB.get(iB - 1);
                int costWithInsertion = 1 + edit[iA][iB - 1];
                int costWithDeletion = 1 + edit[iA - 1][iB];
                int costWithReplacement = (Objects.equals(objectA, objectB) ? 0 : 1) +
                        edit[iA - 1][iB - 1];
                edit[iA][iB] = Math.min(Math.min(costWithInsertion, costWithDeletion),
                        costWithReplacement);
            }
        }
        return edit[lengthA][lengthB];
    }
}
