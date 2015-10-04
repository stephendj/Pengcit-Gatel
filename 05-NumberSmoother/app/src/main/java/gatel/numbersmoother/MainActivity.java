package gatel.numbersmoother;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private ImageView inputImage;
    private TextView numRec;
    private TextView codes;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputImage = (ImageView) findViewById(R.id.imageView);
        numRec = (TextView) findViewById(R.id.numRec);
        codes = (TextView) findViewById(R.id.codes);
        codes.setText("Turning Codes :\n1\n2\n3\n4\n5\n6\n");
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
                int threshold = ImageUtils.calculateThreshold(bitmap);
                Bitmap binaryBitmap = ImageUtils.getBinaryImage(bitmap, threshold);

                inputImage.setImageBitmap(binaryBitmap);
                List<Pair<Point, Point>> boundaries = NumberSmoother.getBoundaryPoints(binaryBitmap, threshold);

                Bitmap[] numberBitmaps = NumberSmoother.getNumberBitmaps(binaryBitmap, boundaries, threshold);
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
