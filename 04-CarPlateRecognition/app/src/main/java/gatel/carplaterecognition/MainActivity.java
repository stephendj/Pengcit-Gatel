package gatel.carplaterecognition;

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
                inputImage.setImageBitmap(binaryBitmap);

                final TextView blackThreshold = (TextView) findViewById(R.id.whiteThreshold);
                blackThreshold.setText(Integer.toString(PatternRecognizerUtils.BLACK_THRESHOLD));

//                List<Integer> recognizedValues = NumberRecognitionUtils.recognizeBitmap(binaryBitmap);
//                numRec.setText(Objects.toString(recognizedValues));
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
