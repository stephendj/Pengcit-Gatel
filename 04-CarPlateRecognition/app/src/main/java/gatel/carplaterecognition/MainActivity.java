package gatel.carplaterecognition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private NumberRecognitionUtils recognitor;
    private ImageView inputImage;
    private TextView numRec;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputImage = (ImageView) findViewById(R.id.imageView);
        numRec = (TextView) findViewById(R.id.numRec);
        final TextView blackThreshold = (TextView) findViewById(R.id.whiteThreshold);
        final EditText errorThreshold = (EditText) findViewById(R.id.errorThreshold);

        blackThreshold.setText(Integer.toString(PatternRecognizerUtils.BLACK_THRESHOLD));
        errorThreshold.setText(Integer.toString((int) (PatternRecognizer.ERROR_THRESHOLD * 100)));

        errorThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int errorThresholdValue = Integer.valueOf(blackThreshold.getText().toString());
                    PatternRecognizer.ERROR_THRESHOLD = (double)errorThresholdValue / 100.;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void pickImage(View View) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CODE_BROWSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        InputStream stream = null;
        if (requestCode == REQUEST_CODE_BROWSE && resultCode == Activity.RESULT_OK) {
            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                ImageUtils.calculateThreshold(bitmap);

                Bitmap binaryBitmap = ImageUtils.getBinaryImage(bitmap);
                inputImage.setImageBitmap(bitmap);

                final TextView blackThreshold = (TextView) findViewById(R.id.whiteThreshold);
                blackThreshold.setText(Integer.toString(PatternRecognizerUtils.BLACK_THRESHOLD));

                List<Integer> recognizedValues = NumberRecognitionUtils.recognizeBitmap(binaryBitmap);
                List<String> realValues = new ArrayList<>();
                for(int i = 0; i < recognizedValues.size(); ++i) {
                    switch (recognizedValues.get(i)) {
                        case 10 : realValues.add("A"); break;
                        case 11 : realValues.add("B"); break;
                        case 12 : realValues.add("C"); break;
                        case 13 : realValues.add("D"); break;
                        case 14 : realValues.add("E"); break;
                        case 15 : realValues.add("F"); break;
                        case 16 : realValues.add("G"); break;
                        case 17 : realValues.add("H"); break;
                        case 18 : realValues.add("I"); break;
                        case 19 : realValues.add("J"); break;
                        case 20 : realValues.add("K"); break;
                        case 21 : realValues.add("L"); break;
                        case 22 : realValues.add("M"); break;
                        case 23 : realValues.add("N"); break;
                        case 24 : realValues.add("O"); break;
                        case 25 : realValues.add("P"); break;
                        case 26 : realValues.add("Q"); break;
                        case 27 : realValues.add("R"); break;
                        case 28 : realValues.add("S"); break;
                        case 29 : realValues.add("T"); break;
                        case 30 : realValues.add("U"); break;
                        case 31 : realValues.add("V"); break;
                        case 32 : realValues.add("W"); break;
                        case 33 : realValues.add("X"); break;
                        case 34 : realValues.add("Y"); break;
                        case 35 : realValues.add("Z"); break;
                        default : realValues.add(recognizedValues.get(i).toString());
                    }
                }
                numRec.setText(Objects.toString(realValues));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
