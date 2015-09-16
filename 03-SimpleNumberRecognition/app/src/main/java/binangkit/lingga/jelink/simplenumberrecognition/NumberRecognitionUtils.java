package binangkit.lingga.jelink.simplenumberrecognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jelink on 9/13/2015.
 */
public class NumberRecognitionUtils {

    private static final int[][] CHAIN_CODES = {{5, 5, 6, 6, 5, 6, 6, 6, 6, 6, 6, 7, 6, 6, 7, 7, 0, 0, 0, 0, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 3, 2, 2, 3, 3, 4, 4, 4, 4}, {5, 4, 4, 5, 4, 0, 0, 0, 0, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 4, 4, 4, 4, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 4, 4, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4}, {5, 6, 0, 1, 0, 0, 0, 7, 7, 6, 6, 6, 5, 5, 5, 5, 5, 6, 5, 5, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 4, 4, 4, 4, 3, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 4, 4, 4, 4, 4}, {5, 6, 1, 0, 0, 0, 0, 7, 7, 6, 6, 5, 4, 5, 4, 4, 6, 0, 0, 0, 7, 7, 6, 6, 6, 5, 5, 4, 4, 4, 4, 3, 6, 6, 0, 0, 0, 0, 0, 1, 0, 2, 1, 2, 2, 2, 3, 3, 3, 1, 0, 1, 2, 2, 2, 3, 3, 4, 4, 4, 4, 4}, {5, 5, 6, 5, 5, 6, 5, 5, 5, 6, 6, 0, 0, 0, 0, 0, 0, 7, 6, 6, 6, 6, 0, 2, 2, 2, 2, 1, 0, 2, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4}, {6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 7, 7, 7, 6, 6, 6, 5, 5, 4, 4, 4, 3, 6, 6, 0, 0, 0, 0, 1, 0, 2, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 3, 2, 2, 2, 1, 0, 0, 0, 0, 0, 2, 4, 4, 4, 4, 4, 4, 4}, {5, 5, 5, 6, 5, 6, 6, 6, 6, 6, 6, 6, 7, 6, 7, 7, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 3, 2, 4, 3, 4, 4, 4, 5, 5, 3, 2, 2, 1, 2, 1, 0, 1, 0, 0, 0, 7, 2, 3, 4, 4, 4, 4}, {6, 0, 0, 0, 0, 0, 0, 0, 7, 5, 5, 6, 5, 6, 5, 6, 5, 6, 5, 6, 5, 6, 6, 0, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4}, {5, 5, 6, 6, 6, 7, 7, 5, 5, 5, 6, 6, 6, 6, 7, 0, 7, 0, 0, 0, 0, 1, 0, 2, 1, 2, 2, 2, 3, 3, 3, 1, 2, 1, 2, 2, 3, 3, 4, 4, 4, 4}, {5, 5, 5, 6, 6, 6, 6, 7, 6, 0, 7, 0, 0, 0, 1, 1, 7, 6, 6, 5, 6, 5, 4, 5, 4, 4, 4, 3, 6, 7, 0, 0, 0, 0, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 3, 2, 3, 3, 4, 4, 4, 4}};
    private static Map<Integer, List<Integer>> CHAIN_CODE_MAP = new HashMap<>();
    static {
        for (int i = 0; i < CHAIN_CODES.length; ++i) {
            List<Integer> chainCode = new ArrayList<>();
            for (int j = 0; j < CHAIN_CODES[i].length; ++j) {
                chainCode.add(CHAIN_CODES[i][j]);
            }
            CHAIN_CODE_MAP.put(i, chainCode);
        }
    }

    public static List<Integer> recognizeBitmap(Bitmap bitmap) {
        PatternRecognizer recognizer = PatternRecognizer.fromBitmap(bitmap);
        return recognizer.recognizePattern(CHAIN_CODE_MAP);
    }

}
