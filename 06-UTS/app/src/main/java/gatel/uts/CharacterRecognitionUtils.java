package gatel.uts;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

public class CharacterRecognitionUtils {

    private static Multimap<Character, List<Integer>> CHAIN_CODE_MAP = ArrayListMultimap.create();

    public static List<Character> recognizeBitmap(Bitmap bitmap) {
        PatternRecognizer recognizer = PatternRecognizer.fromBitmap(bitmap);
        return recognizer.recognizePattern(CHAIN_CODE_MAP);
    }

    public static void addTrainingSet(Bitmap bitmap, String expected) {
        PatternRecognizer recognizer = PatternRecognizer.fromBitmap(bitmap);
        List<List<Integer>> chainCodes = recognizer.getChainCodes();
        int expectedLength = expected.length();
        int chainCodesSize = chainCodes.size();
        if (chainCodesSize != expectedLength) {
            Log.d("CharacterRecognitionUtils#addTrainingSet",
                    String.format("Training failed: expecting %d values but %d chain codes was found",
                            expectedLength, chainCodesSize));
        } else {
            for (int i = 0; i < expectedLength; ++i) {
                CHAIN_CODE_MAP.put(expected.charAt(i), chainCodes.get(i));
            }
            Log.d("CharacterRecognitionUtils#addTrainingSet",
                    "Successfully added " + expected + " to chain code list");
        }
    }

    public static List<Integer> deriveChainCode(List<Integer> chainCode) {
        List<Integer> newChainCode = new ArrayList<>();

        // Derive the chain code, dk = ((ck) - (ck-1)) mod 8
        for(int i = 1; i < chainCode.size(); ++i) {
            int code = (chainCode.get(i)-chainCode.get(i-1))%8;
            int newCode = (code < 0) ? code + 8 : code;
            newChainCode.add(newCode);
        }

        // Normalize the new chain code, find the first 0 and switch circularly
        int i = 0;
        while(i < newChainCode.size()) {
            if(newChainCode.get(i) == 0) {
                for(int j = 0; j < i; ++j) {
                    newChainCode.add(newChainCode.get(j));
                }
                for(int j = 0; j < i; ++j) {
                    int x = newChainCode.remove(0);
                }
                break;
            } else {
                ++i;
            }
        }

        while(newChainCode.contains(0)) {
            newChainCode.remove(newChainCode.indexOf(0));
        }

        return newChainCode;
    }

    public static List<List<Character>> recognizeBitmapPerLine(Bitmap bitmap) {
        PatternRecognizer recognizer = PatternRecognizer.fromBitmap(bitmap);
        return recognizer.recognizePatternPerLine(CHAIN_CODE_MAP);
    }
}
