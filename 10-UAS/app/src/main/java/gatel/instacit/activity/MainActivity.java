package gatel.instacit.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gatel.instacit.FaceDetector;
import gatel.instacit.R;
import gatel.instacit.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;

    private FloatingActionsMenu menu;
    private Uri tempImageUri;
    private ImageView mainImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        mainImageView = (ImageView) findViewById(R.id.image_view_main);
    }

    public void browseImage(View view) {
        menu.collapse();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_BROWSE);
    }

    public void captureImage(View View) {
        menu.collapse();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
        tempImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_BROWSE) {
                processBitmap(getBitmapFromIntent(data));
            } else if (requestCode == REQUEST_CODE_CAPTURE) {
                processBitmap(getBitmapFromMedia());
            }
        }
    }

    private Bitmap getBitmapFromMedia() {
        getContentResolver().notifyChange(tempImageUri, null);
        ContentResolver contentResolver = getContentResolver();
        try {
            return android.provider.MediaStore.Images.Media.getBitmap(contentResolver, tempImageUri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getBitmapFromIntent(Intent data) {
        InputStream stream = null;
        try {
            stream = getContentResolver().openInputStream(data.getData());
            return BitmapFactory.decodeStream(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    /**
     * Segala-galanya taroh di sini ya.
     */
    private void processBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(MainActivity.this, "Failure on loading bitmap", Toast.LENGTH_SHORT).show();
            return;
        }
        bitmap = ImageUtils.rescaleAndRecycleBitmap(bitmap);

        List<Pair<Point, Point>> boundaryPoints = FaceDetector.getBoundaries(bitmap);

        Bitmap taggedBitmap = FaceDetector.generateTaggedBitmap(bitmap, boundaryPoints);
        mainImageView.setImageDrawable(new BitmapDrawable(getResources(), taggedBitmap));
    }
}
