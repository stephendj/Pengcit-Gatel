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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gatel.instacit.FaceDetector;
import gatel.instacit.Person;
import gatel.instacit.PersonClassifier;
import gatel.instacit.R;
import gatel.instacit.utils.FacePartConverter;
import gatel.instacit.utils.GaussianBlur;
import gatel.instacit.utils.ImageUtils;
import gatel.instacit.utils.KMeans;
import gatel.instacit.utils.KirschOperator;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BROWSE = 1;
    private static final int REQUEST_CODE_CAPTURE = 2;
    private static final int COMPONENT_SIZE = 300;

    private FloatingActionsMenu menu;
    private Uri tempImageUri;
    private ImageView mainImageView;
    private LinearLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        PersonClassifier.loadDataset(this);

        menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        mainImageView = (ImageView) findViewById(R.id.image_view_main);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
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
        long startTime = System.currentTimeMillis();

        bitmap = ImageUtils.rescaleAndRecycleBitmap(ImageUtils.convertAndRecycleBitmap(bitmap, Bitmap.Config.RGB_565));
//        Bitmap blurredBitmap = GaussianBlur.fromBitmap(bitmap).blurBitmap(5);

        List<Pair<Point, Point>> boundaryPoints = FaceDetector.getBoundaries(bitmap);

        Bitmap taggedBitmap = FaceDetector.generateTaggedBitmap(bitmap, boundaryPoints);
        mainImageView.setImageDrawable(new BitmapDrawable(getResources(), taggedBitmap));

        // TODO: move this to a separate function
        resultLayout.removeAllViews();
        for (Pair<Point, Point> boundaryPoint : boundaryPoints) {
            // initialize layout
            LinearLayout topLayout = new LinearLayout(this);
            topLayout.setOrientation(LinearLayout.VERTICAL);
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
            horizontalScrollView.setId(boundaryPoints.indexOf(boundaryPoint));
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            // cropped image
            int topx = Math.max(boundaryPoint.first.x - 2, 0);
            int topy = Math.max(boundaryPoint.first.y - 2, 0);
            int bottomx = Math.min(boundaryPoint.second.x + 2, bitmap.getWidth() - 1);
            int bottomy = Math.min(boundaryPoint.second.y + 2, bitmap.getHeight() - 1);
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, topx , topy, bottomx - topx, bottomy - topy);
            ImageView imageViewOri = createImageViewForComponent(croppedBitmap);
            linearLayout.addView(imageViewOri);

            // face!
            Bitmap gsBitmap = ImageUtils.convertToGrayscale(croppedBitmap);
//            Bitmap gsBlurredBitmap = GaussianBlur.fromBitmap(gsBitmap).blurBitmap(3);
            Bitmap opBitmap = KirschOperator.convertImage(gsBitmap);
            Bitmap bwBitmap = ImageUtils.getBinaryImage(opBitmap, ImageUtils.calculateThreshold(opBitmap));
            Bitmap trimmedBitmap = ImageUtils.trimSurrounding(bwBitmap);
            KMeans kMeans = KMeans.fromBitmap(trimmedBitmap);
            kMeans.doCluster();
            Bitmap clusteredBitmap = kMeans.makeMarkedClusterImage();

            ImageView imageViewGrayscale = createImageViewForComponent(gsBitmap);
            linearLayout.addView(imageViewGrayscale);
//            ImageView imageViewBlurredGrayscale = createImageViewForComponent(gsBlurredBitmap);
//            linearLayout.addView(imageViewBlurredGrayscale);
            ImageView imageViewEdgeDetected = createImageViewForComponent(opBitmap);
            linearLayout.addView(imageViewEdgeDetected);
            ImageView imageViewBlackWhite = createImageViewForComponent(bwBitmap);
            linearLayout.addView(imageViewBlackWhite);
            ImageView imageViewTrimmed = createImageViewForComponent(trimmedBitmap);
            linearLayout.addView(imageViewTrimmed);
            ImageView imageViewMarked = createImageViewForComponent(clusteredBitmap);
            linearLayout.addView(imageViewMarked);

            Bitmap leftEye = ImageUtils.generateBitmapFromPoints(kMeans.getLeftEyePoints());
            Bitmap rightEye = ImageUtils.generateBitmapFromPoints(kMeans.getRightEyePoints());
            Bitmap nose = ImageUtils.generateBitmapFromPoints(kMeans.getNosePoints());
            Bitmap mouth = ImageUtils.generateBitmapFromPoints(kMeans.getMouthPoints());
            Person unknown = new Person(
                    "?",
                    FacePartConverter.getFacePartMatrix(leftEye, FacePartConverter.Part.LEFT_EYE),
                    FacePartConverter.getFacePartMatrix(rightEye, FacePartConverter.Part.RIGHT_EYE),
                    FacePartConverter.getFacePartMatrix(nose, FacePartConverter.Part.NOSE),
                    FacePartConverter.getFacePartMatrix(mouth, FacePartConverter.Part.MOUTH));
            PersonClassifier.classify(unknown);
            TextView textView = new TextView(this);
            textView.setText("Classified as " + unknown.getName() + " (similarity " + unknown.getSimilarity() + ")");

//            ImageView imageViewLeftEye = createImageViewForComponent(leftEye);
//            linearLayout.addView(imageViewLeftEye);
//            Bitmap rightEye = ImageUtils.generateBitmapFromPoints(kMeans.getRightEyePoints());

            // Pack them altogether
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 20, 0, 20);

            horizontalScrollView.addView(linearLayout);
            topLayout.addView(horizontalScrollView);
            topLayout.addView(textView);

            resultLayout.addView(topLayout, layoutParams);
        }

        // recycle unused bitmaps
//        bitmap.recycle();
//        blurredBitmap.recycle();

        long finishTime = System.currentTimeMillis();
        Toast.makeText(MainActivity.this, "Running Time (" + bitmap.getWidth() + "x" + bitmap.getHeight() + "): " + (finishTime - startTime) + " ms", Toast.LENGTH_SHORT).show();
    }

    private ImageView createImageViewForComponent(Bitmap clusteredBitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setImageBitmap(clusteredBitmap);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(COMPONENT_SIZE, COMPONENT_SIZE));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }

}
