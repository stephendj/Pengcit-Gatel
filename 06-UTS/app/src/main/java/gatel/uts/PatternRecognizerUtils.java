package gatel.uts;

import android.util.Log;

import java.util.List;
import java.util.Objects;

public class PatternRecognizerUtils {

    public static final int WHITE = 0x00FFFFFF;

    private PatternRecognizerUtils() {
        // Utility class
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

    public static <T> int calculateCircularEditDistance(List<T> patternA, List<T> patternB) {
        long startTime = System.currentTimeMillis();
        int lengthA = patternA.size();
        int lengthB = patternB.size();
        int edit[][] = new int[lengthA + 1][lengthB * 2 + 1];

        for (int iA = 0; iA <= lengthA; ++iA) {
            edit[iA][0] = iA;
        }
        for (int iB = 0; iB <= lengthB * 2; ++iB) {
            edit[0][iB] = 0;
        }
        for (int iA = 1; iA <= lengthA; ++iA) {
            T objectA = patternA.get(iA - 1);
            for (int iB = 1; iB <= lengthB * 2; ++iB) {
                T objectB = patternB.get((iB - 1) % lengthB);
                int costWithInsertion = 1 + edit[iA][iB - 1];
                int costWithDeletion = 1 + edit[iA - 1][iB];
                int costWithReplacement = (Objects.equals(objectA, objectB) ? 0 : 1) +
                        edit[iA - 1][iB - 1];
                edit[iA][iB] = Math.min(Math.min(costWithInsertion, costWithDeletion),
                        costWithReplacement);
            }
        }
        int minEditDistance = lengthA;
        for (int iB = 1; iB <= lengthB * 2; ++iB) {
            int nr = lengthA, nc = iB;
            while (nr > 0) {
                int curEditDistance = edit[nr][nc];
                if (edit[nr - 1][nc] + 1 == curEditDistance) {
                    nr--;
                } else {
                    T objectA = patternA.get(nr - 1);
                    T objectB = patternB.get((nc - 1) % lengthB);
                    if (nc > 0 && edit[nr - 1][nc - 1] + (Objects.equals(objectA, objectB) ? 0 : 1) == curEditDistance) {
                        nr--;
                        nc--;
                    } else if (edit[nr][nc - 1] + 1 == curEditDistance) {
                        nc--;
                    } else {
                        throw new IllegalStateException("Edit distance is not tracable");
                    }
                }
            }
            int editDistance = (edit[lengthA][iB] - edit[nr][nc]) + Math.abs((iB - nc) - lengthB);
            if (editDistance < minEditDistance) {
                minEditDistance = editDistance;
            }
        }
        long endTime = System.currentTimeMillis();
        Log.d("PatternRecognizerUtils#calculateCircularEditDistance",
                String.format("Edit distance %d x %d: Running time %d", lengthA, lengthB, (int) (endTime - startTime)));
        return minEditDistance;
    }
}
