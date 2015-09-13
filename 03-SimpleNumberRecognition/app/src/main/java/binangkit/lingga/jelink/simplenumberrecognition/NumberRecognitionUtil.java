package binangkit.lingga.jelink.simplenumberrecognition;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by jelink on 9/13/2015.
 */
public class NumberRecognitionUtil {

    private static int detectedColor = 0xff000000;
    private static int backgroundColor = 0xffffffff;
    private static Bitmap procImage;

    /* rekognisi dari image menjadi list of integers */
    public static ArrayList<Integer> recognizeNumbers(Bitmap image) {
        //System.out.println("recognize");
        int width = image.getWidth();
        int height = image.getHeight();
        procImage = image.copy(image.getConfig(), true);
        ArrayList<Integer> results = new ArrayList<Integer>();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int color = procImage.getPixel(x, y);
                if (color == detectedColor) {
                    //System.out.println(x + " " + y);
                    //System.out.println("detected");
                    results.add(getNumber(x, y));
                    floodInit(y, x);
                }
            }
        }

        return results;
    }

    /* Dapatkan chain code, konversi chain code ke angka */
    private static int getNumber(int x, int y) {
        //System.out.println("chain : " + createChainCode(x,y));
        //return 1;
        return getNumberFromChainCode(createChainCode(x,y));
    }

    private static int getNumberFromChainCode(String chainCode) {
        switch (chainCode) {
            case "44443222222222221000024444444444460000766666666666666010010" :
                return 1;
            case "24544433222111112112244444444600000765655555666677000001" :
                return 2;
            case "25444433221010024443322211000072244444546566677754566677000001" :
                return 3;
            case "224444443222246666546076666666660112112111" :
                return 4;
            case "22222224443332221100072244445465666677700766654444460000000" :
                return 5;
            case "222222232334444555666676070001176656545444367000011121" :
                return 6;
            case "2444444431121212121212246566565656565566000000000" :
                return 7;
            case "222234344445465666777565667700001122233111" :
                return 8;
            case "222232434445532212101000723444455565666666676770000111" :
                return 9;
            case "2222223223344445566566666676677000011221" :
                return 0;
            default:
                return -99;
        }
    }

    /* menyusun chain code dari titik awal x,y */
    private static String createChainCode(int x, int y) {
        String chainCode = "";

        int[] neighbor = new int[3];
        neighbor[0] = x;
        neighbor[1] = y;
        neighbor[2] = 4; //previous direction
        //System.out.println ("x " + x + " : y " + y);
        do {
            neighbor = getNeighbor(neighbor[0], neighbor[1], neighbor[2]);
            chainCode += neighbor[2];
            //System.out.println (neighbor[0] + " : " + neighbor[1]);
        } while (neighbor[0] != x || neighbor[1] != y);

        return chainCode;
    }

    /* mengambil neighbor yang hitam dan arah nya
     * [0] = column
     * [1] = row
     * [2] = direction
     */
    private static int[] getNeighbor(int col, int row, int dir) {
        int mark = -999999;
        int[][] neighbor = new int[8][3];

        for (int i=0 ; i<8 ; i++) {
            neighbor[i][0] = mark;
        }

        if (neighborExistsAndBlack(col-1, row) && dir != 4){
            neighbor[4][0] = col-1;
            neighbor[4][1] = row;
            neighbor[4][2] = 0;
        }
        if (neighborExistsAndBlack(col-1, row-1) && dir != 3){
            neighbor[3][0] = col-1;
            neighbor[3][1] = row-1;
            neighbor[3][2] = 7;
        }
        if (neighborExistsAndBlack(col, row-1) && dir != 2){
            neighbor[2][0] = col;
            neighbor[2][1] = row-1;
            neighbor[2][2] = 6;
        }
        if (neighborExistsAndBlack(col+1, row-1) && dir != 1){
            neighbor[1][0] = col+1;
            neighbor[1][1] = row-1;
            neighbor[1][2] = 5;
        }
        if (neighborExistsAndBlack(col+1, row) && dir != 0){
            neighbor[0][0] = col+1;
            neighbor[0][1] = row;
            neighbor[0][2] = 4;
        }
        if (neighborExistsAndBlack(col+1, row+1) && dir != 7){
            neighbor[7][0] = col+1;
            neighbor[7][1] = row+1;
            neighbor[7][2] = 3;
        }
        if (neighborExistsAndBlack(col, row+1) && dir != 6){
            neighbor[6][0] = col;
            neighbor[6][1] = row+1;
            neighbor[6][2] = 2;
        }
        if (neighborExistsAndBlack(col-1, row+1) && dir != 5){
            neighbor[5][0] = col-1;
            neighbor[5][1] = row+1;
            neighbor[5][2] = 1;
        }

        for (int i = 1 ; i < 8 ; i++) {
            if (neighbor[(dir + i) % 8][0] != mark){
                return neighbor[(dir + i) % 8];
            }
        }
        return null;
    }

    private static boolean neighborExistsAndBlack (int col, int row) {
        if (col >= 0 && col < procImage.getWidth() && row >= 0 && row < procImage.getHeight()) {
            if (procImage.getPixel(col, row) == detectedColor) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static void floodInit(int row, int col) {
        boolean[][] mark = new boolean[procImage.getHeight()][procImage.getWidth()];
        flood(mark, row, col);
    }

    private static void flood(boolean[][] mark,
                              int row, int col) {
        // make sure row and col are inside the image
        if (row < 0) return;
        if (col < 0) return;
        if (row >= procImage.getHeight()) return;
        if (col >= procImage.getWidth()) return;

        // make sure this pixel hasn't been visited yet
        if (mark[row][col]) return;

        // make sure this pixel is the right color to fill
        if (procImage.getPixel(col, row) != detectedColor) return;

        // fill pixel with target color and mark it as visited
        procImage.setPixel(col, row, backgroundColor);
        mark[row][col] = true;

        // recursively fill surrounding pixels
        // (this is equivelant to depth-first search)
        flood(mark, row - 1, col);
        flood(mark, row + 1, col);
        flood(mark, row, col - 1);
        flood(mark, row, col + 1);
    }

}
