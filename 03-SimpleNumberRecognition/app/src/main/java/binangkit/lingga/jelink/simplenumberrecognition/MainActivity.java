package binangkit.lingga.jelink.simplenumberrecognition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
        final EditText blackThreshold = (EditText) findViewById(R.id.whiteThreshold);
        final EditText errorThreshold = (EditText) findViewById(R.id.errorThreshold);

        blackThreshold.setText(Integer.toString(PatternRecognizerUtils.BLACK_THRESHOLD));
        errorThreshold.setText(Integer.toString((int)(PatternRecognizer.ERROR_THRESHOLD * 100)));

        blackThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int blackThresholdValue = Integer.valueOf(blackThreshold.getText().toString());
                    PatternRecognizerUtils.BLACK_THRESHOLD = blackThresholdValue;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

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

                inputImage.setImageBitmap(bitmap);

                List<Integer> recognizedValues = NumberRecognitionUtils.recognizeBitmap(bitmap);
                List<String> realValues = new ArrayList<>();
                for(int i = 0; i < recognizedValues.size(); ++i) {
                    switch (recognizedValues.get(i)) {
                        case 10 : realValues.add("square"); break;
                        case 11 : realValues.add("triangle"); break;
                        case 12 : realValues.add("right triangle"); break;
                        case 13 : realValues.add("rectangle"); break;
                        case 14 : realValues.add("circle"); break;
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
