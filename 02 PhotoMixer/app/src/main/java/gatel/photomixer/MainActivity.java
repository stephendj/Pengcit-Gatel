package gatel.photomixer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private static final int step = 1;
    private static final int min = 0;
    private static final int max = 255;

    private Bitmap bitmap;
    private Uri imageUri;
    private int greyScaleValue;
    private ImageEqualizer equalizer;

    private ImageView imageView;
    private SeekBar seekBar;
    private TextView textView;

    private int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.textView);
        seekBar.setMax((max - min)/step);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                greyScaleValue = min + (progress * step);
                textView.setText(Integer.toString(greyScaleValue));
                imageView.setImageBitmap(equalizer.equalize(greyScaleValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    public void captureImage(View View) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = "pic" + counter + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
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
                equalizer = ImageEqualizer.create(bitmap);

                imageView.setImageBitmap(equalizer.getBaseImage());

                seekBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
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
        } else if (requestCode == REQUEST_CODE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri capturedImage = imageUri;
            getContentResolver().notifyChange(capturedImage, null);
            ContentResolver cr = getContentResolver();

            try {
                // recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }

                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, capturedImage);
                equalizer = ImageEqualizer.create(bitmap);

                imageView.setImageBitmap(equalizer.getBaseImage());
                seekBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);

                ++counter;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
