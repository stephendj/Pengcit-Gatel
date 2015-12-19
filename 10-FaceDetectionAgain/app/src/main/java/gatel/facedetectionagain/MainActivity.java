package gatel.facedetectionagain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private static final int MAX_IMAGE_WIDTH = 500;
    private ImageView inputImage;
    private ImageView outImage;
    private TextView numRec;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputImage = (ImageView) findViewById(R.id.inputImageView);
        outImage = (ImageView) findViewById(R.id.outputImageView);
        EditText editText = (EditText) findViewById(R.id.editTextThreshold);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int newThreshold = Integer.parseInt(v.getText().toString());
                FaceDetector.SKIN_THRESHOLD = newThreshold;
                return false;
            }
        });
        editText.setText(Integer.toString(FaceDetector.SKIN_THRESHOLD));
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
                // resize bitmap when necessary
                if (bitmap.getWidth() > MAX_IMAGE_WIDTH) {
                    Bitmap rescaledBitmap = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_WIDTH, bitmap.getHeight() * MAX_IMAGE_WIDTH / bitmap.getWidth(), false);
                    bitmap.recycle();
                    bitmap = rescaledBitmap;
                }

                inputImage.setImageBitmap(bitmap);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                List<Pair<Point, Point>> boundaryPoints = FaceDetector.getBoundaries(bitmap);
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        if (FaceDetector.mark[x + y * width]) {
                            tempBitmap.setPixel(x, y, Color.GREEN);
                        }
                    }
                }
                Canvas canvas = new Canvas(tempBitmap);
                canvas.drawBitmap(bitmap, 0, 0, null);
                Paint boxPaint = new Paint();
                boxPaint.setColor(Color.RED);
                boxPaint.setStyle(Paint.Style.STROKE);
                for(Pair<Point, Point> boundaryPoint : boundaryPoints) {
                    canvas.drawRect(Math.max(boundaryPoint.first.x - 2, 0) , Math.max(boundaryPoint.first.y - 2, 0),
                            Math.min(boundaryPoint.second.x + 2, width - 1), Math.min(boundaryPoint.second.y + 2, height - 1), boxPaint);
                }
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        if (FaceDetector.mark[x + y * width]) {
                            tempBitmap.setPixel(x, y, Color.GREEN);
                        }
                    }
                }
                outImage.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
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
