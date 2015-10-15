package gatel.uts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;

    private Uri imageUri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
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
        File photo = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);

        startActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startPictureActivity() throws Exception{
        //Write file
        String filename = "bitmap.png";
        FileOutputStream outputStream = this.openFileOutput(filename, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        //Cleanup
        outputStream.close();
        bitmap.recycle();

        //Pop intent
        Intent in1 = new Intent(this, PictureActivity.class);
        in1.putExtra("image", filename);
        startActivity(in1);
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

                startPictureActivity();
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

                startPictureActivity();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Intent intent = new Intent(this, PictureActivity.class);
                intent.putExtra("bitmap", bitmap);
                startActivity(intent);
            }
        }
    }
}
