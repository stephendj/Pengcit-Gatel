package gatel.carplaterecognition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private CharacterRecognitionUtils recognitor;
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

        blackThreshold.setText(Integer.toString(ColorScheme.DEFAULT_COLOR_SCHEME.getThreshold()));
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

//        CharacterRecognitionUtils.addTrainingSet(
//                BitmapFactory.decodeResource(getResources(), R.drawable.sample_calibri),
//                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
//        CharacterRecognitionUtils.addTrainingSet(
//                BitmapFactory.decodeResource(getResources(), R.drawable.sample_comicsans),
//                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        CharacterRecognitionUtils.addTrainingSet(
                BitmapFactory.decodeResource(getResources(), R.drawable.sample_fontplatnomor),
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ");
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
                // recycle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);

                final CheckBox resize = (CheckBox) findViewById(R.id.resize);
                double scale = 200. / (double)bitmap.getWidth();
                if (resize.isChecked()) {

                    if (scale < 1) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int)(scale * bitmap.getWidth()), (int)(scale * bitmap.getHeight()), true);
                    }
                }

                final CheckBox blackOnWhite = (CheckBox) findViewById(R.id.blackOnWhite);
                ColorScheme.Type type = blackOnWhite.isChecked() ?
                        ColorScheme.Type.BLACK_ON_WHITE :
                        ColorScheme.Type.WHITE_ON_BLACK;

                ColorScheme scheme = ImageUtils.calculateColorScheme(bitmap, type);
                Bitmap binaryBitmap = ImageUtils.getBinaryImage(bitmap, scheme);
                inputImage.setImageBitmap(binaryBitmap);

//                final TextView blackThreshold = (TextView) findViewById(R.id.whiteThreshold);
//                blackThreshold.setText(Integer.toString(scheme.getThreshold()));
//
//                List<Pair<Point, Point>> carPlateCorner = CharacterRecognitionUtils.recognizeSquareFromBitmap(binaryBitmap, scheme);
//
//                List<List<Character>> recognizedValues = new ArrayList<>();
//
//                if(carPlateCorner == null) {
//                    recognizedValues = CharacterRecognitionUtils.recognizeBitmapPerLine(binaryBitmap, scheme);
//                } else {
//                    List<List<List<Character>>> recognizedSquares = new ArrayList<>();
//                    for(int i = 0; i < carPlateCorner.size(); ++i) {
//                        Bitmap carPlateBitmap = Bitmap.createBitmap(binaryBitmap, carPlateCorner.get(i).first.x, carPlateCorner.get(i).first.y,
//                                carPlateCorner.get(i).second.x - carPlateCorner.get(i).first.x, carPlateCorner.get(i).second.y - carPlateCorner.get(i).first.y);
//                        recognizedSquares.add(CharacterRecognitionUtils.recognizeBitmapPerLine(carPlateBitmap, scheme));
//                    }
//                    for(int i = 0; i < recognizedSquares.size(); ++i) {
//                        for(int j = 0; j < recognizedSquares.get(i).size(); ++j) {
//                            recognizedValues.add(recognizedSquares.get(i).get(j));
//                        }
//                    }
//                }
//
//                StringBuilder builder = new StringBuilder();
//                for (List<Character> line : recognizedValues) {
//                    builder.append(Objects.toString(line));
//                    builder.append("\n");
//                }
//                numRec.setText(builder.toString());

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
