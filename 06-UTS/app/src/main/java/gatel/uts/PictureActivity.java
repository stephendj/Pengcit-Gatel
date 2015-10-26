package gatel.uts;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edmodo.rangebar.RangeBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {

    private static final int COMPONENT_SIZE = 200;

    private Bitmap bitmap;
    private Bitmap[] croppedBitmaps;

    private RangeBar rangeBar;
    private RangeBar rangeBar2;
    private ImageView imageView;
    private ImageView imageViewBinary;
    private GraphView graphView;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageViewBinary = (ImageView) findViewById(R.id.imageViewBinary);

        // Get the rangebar and add listener
        rangeBar = (RangeBar) findViewById(R.id.rangebar);
        rangeBar.setConnectingLineColor(Color.RED);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                NativeLib.equalize(leftThumbIndex, rightThumbIndex);
                Bitmap equalizedBitmap = NativeLib.getEqualizedBitmap();
                imageView.setImageBitmap(equalizedBitmap);
                updateGraph();
            }
        });

        final Context context = this;
        rangeBar2 = (RangeBar) findViewById(R.id.rangebar2);
        rangeBar2.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                processImageAndShowResult(rightThumbIndex);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        graphView = (GraphView) findViewById(R.id.graph);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bitmap != null) {
            // resize bitmap when necessary
            if (bitmap.getWidth() > 600) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 600, bitmap.getHeight() * 600 / bitmap.getWidth(), false);
            }

            long timeStart = System.currentTimeMillis();
            NativeLib.registerBitmap(bitmap);
            Bitmap equalizedBitmap = NativeLib.getEqualizedBitmap();
            long timeFinish = System.currentTimeMillis();
            Log.d("halo", "Total processing time: " + (timeFinish - timeStart) + " ms");
            imageView.setImageBitmap(equalizedBitmap);

            updateGraph();
            rangeBar2.setThumbIndices(0, NativeLib.getBinaryThreshold());
            processImageAndShowResult(-1);
        }
    }

    void showResult(Bitmap binaryBitmap, List<Bitmap> cropped, List<Bitmap> grids, List<Bitmap> thinned) {
        final List<TextView> textViews = new ArrayList<>();

        mainLayout.removeAllViews();
        imageViewBinary.setImageBitmap(binaryBitmap);
        for (int i = 0; i < cropped.size(); ++i) {
            LinearLayout topLayout = new LinearLayout(this);
            topLayout.setOrientation(LinearLayout.VERTICAL);
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
            horizontalScrollView.setId(i);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Show the original cropped image
            ImageView imageViewOri = new ImageView(this);
            imageViewOri.setPadding(2, 2, 2, 2);
            imageViewOri.setImageBitmap(cropped.get(i));
            imageViewOri.setLayoutParams(new LinearLayout.LayoutParams(COMPONENT_SIZE, COMPONENT_SIZE));
            imageViewOri.setAdjustViewBounds(true);
            imageViewOri.setScaleType(ImageView.ScaleType.FIT_CENTER);
            linearLayout.addView(imageViewOri);

            // Show the image that has been plotted to 5x5
            ImageView imageViewPlotted = new ImageView(this);
            imageViewPlotted.setPadding(2, 2, 2, 2);
            imageViewPlotted.setImageBitmap(grids.get(i));
            imageViewPlotted.setLayoutParams(new LinearLayout.LayoutParams(COMPONENT_SIZE, COMPONENT_SIZE));
            imageViewPlotted.setAdjustViewBounds(true);
            imageViewPlotted.setScaleType(ImageView.ScaleType.FIT_CENTER);
            linearLayout.addView(imageViewPlotted);

            // Show the image that has been thinned using Zhang Suen Algorithm
            ImageView imageViewThinned = new ImageView(this);
            imageViewThinned.setPadding(2, 2, 2, 2);
            imageViewThinned.setImageBitmap(thinned.get(i));
            imageViewThinned.setLayoutParams(new LinearLayout.LayoutParams(COMPONENT_SIZE, COMPONENT_SIZE));
            imageViewThinned.setAdjustViewBounds(true);
            imageViewThinned.setScaleType(ImageView.ScaleType.FIT_CENTER);
            linearLayout.addView(imageViewThinned);

            // Show the recognized number
            TextView pattern = new TextView(this);
            pattern.setPadding(2, 2, 2, 2);
            pattern.setText("Recognized character: loading...");
            textViews.add(pattern);

            // Pack them altogether
            horizontalScrollView.addView(linearLayout);
            topLayout.addView(horizontalScrollView);
            topLayout.addView(pattern);
            mainLayout.addView(topLayout);
        }

        // Finally, recognize pattern
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                return NativeLib.recognizePattern();
            }

            @Override
            protected void onPostExecute(String recognized) {
                for (int i = 0; i < recognized.length(); ++i) {
                    textViews.get(i).setText("Recognized Character: " + recognized.charAt(i));
                }
            }
        }.execute();
    }

    private void processImageAndShowResult(final int newThreshold) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Processing ...", true);
        new AsyncTask<Void, Void, Void>() {
            Bitmap annotatedBitmap;
            List<Bitmap> croppedBitmaps;
            List<Bitmap> gridBitmaps;
            List<Bitmap> thinnedBitmaps;

            @Override
            protected Void doInBackground(Void... params) {
                if (newThreshold >= 0) {
                    NativeLib.setBinaryThreshold(newThreshold);
                }

                List<Pair<Point, Point>> boundaryPoints = NativeLib.getBoundaries();
                List<int[][]> grids = NativeLib.getGrids();
                int n = grids.size();
                Bitmap binaryBitmap = NativeLib.getBinaryBitmap();
                int width = binaryBitmap.getWidth();
                int height = binaryBitmap.getHeight();

                croppedBitmaps = new ArrayList<>();
                gridBitmaps = new ArrayList<>();
                thinnedBitmaps = new ArrayList<>();

                // Draw rectangle on recognized patterns while cropping things
                annotatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(annotatedBitmap);
                canvas.drawBitmap(binaryBitmap, 0, 0, null);
                Paint boxPaint = new Paint();
                boxPaint.setColor(Color.RED);
                boxPaint.setStyle(Paint.Style.STROKE);
                for (Pair<Point, Point> boundaryPoint : boundaryPoints) {
                    int topx = Math.max(boundaryPoint.first.x - 2, 0);
                    int topy = Math.max(boundaryPoint.first.y - 2, 0);
                    int bottomx = Math.min(boundaryPoint.second.x + 2, width - 1);
                    int bottomy = Math.min(boundaryPoint.second.y + 2, height - 1);
                    canvas.drawRect(topx, topy, bottomx, bottomy, boxPaint);
                    Bitmap croppedBitmap = Bitmap.createBitmap(binaryBitmap,
                            topx , topy, bottomx - topx, bottomy - topy);
                    croppedBitmaps.add(croppedBitmap);
                }

                for (int i = 0; i < n; ++i) {
                    gridBitmaps.add(getGridBitmap(grids.get(i)));
                    thinnedBitmaps.add(ZhangSuen.thinImageBitmap(croppedBitmaps.get(i)));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                dialog.dismiss();
                showResult(annotatedBitmap, croppedBitmaps, gridBitmaps, thinnedBitmaps);
            }
        }.execute();
    }

    private Bitmap getGridBitmap(int[][] grid) {
        Bitmap bitmap = Bitmap.createBitmap(COMPONENT_SIZE, COMPONENT_SIZE, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(255, 255, 255);
        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.GREEN);
        boxPaint.setStyle(Paint.Style.FILL);
        Paint outerBoxPaint = new Paint();
        outerBoxPaint.setColor(Color.rgb(0, 200, 0));
        outerBoxPaint.setStyle(Paint.Style.STROKE);
        int width = grid.length;
        int height = grid[0].length;
        int boxWidth = COMPONENT_SIZE / width;
        int boxHeight = COMPONENT_SIZE / height;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (grid[x][y] == 1) {
                    canvas.drawRect(x * boxWidth, y * boxHeight, (x + 1) * boxWidth - 1, (y + 1) * boxHeight - 1, boxPaint);
                    canvas.drawRect(x * boxWidth, y * boxHeight, (x + 1) * boxWidth - 1, (y + 1) * boxHeight - 1, outerBoxPaint);
                }
            }
        }
        return bitmap;
    }

    private void updateGraph() {
        int[] frequency = NativeLib.getFrequency();
        int[] equalizedFrequency = NativeLib.getEqualizedFrequency();
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(255);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(); // First series
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(); series2.setColor(Color.RED); // Second series

        for (int i = 0; i < 256; ++i) {
            series.appendData(new DataPoint(i, frequency[i]), false, 256);
            series2.appendData(new DataPoint(i, equalizedFrequency[i]), false, 256);
        }

        graphView.removeAllSeries();
        graphView.addSeries(series);
        graphView.addSeries(series2);
    }
}
