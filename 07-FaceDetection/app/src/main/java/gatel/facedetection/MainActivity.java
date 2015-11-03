package gatel.facedetection;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;

    private Bitmap bitmap;
    private Uri imageUri;

    private ImageView imageView;

    private int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void pickImage(View View) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CODE_BROWSE);
    }

    public void captureImage(View View) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = "pic" + counter + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
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

                Bitmap greyScaleBitmap = ImageUtils.convertToGrayscale(bitmap);
                imageView.setImageBitmap(FaceDetection.homogenConvert(greyScaleBitmap));
            } catch (Exception e) {
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
        } else if (requestCode == REQUEST_CODE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri capturedImage = imageUri;
            getContentResolver().notifyChange(capturedImage, null);
            ContentResolver cr = getContentResolver();

            try {
                // recycle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }

                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, capturedImage);

                Bitmap greyScaleBitmap = ImageUtils.convertToGrayscale(bitmap);
                imageView.setImageBitmap(greyScaleBitmap);

                ++counter;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
