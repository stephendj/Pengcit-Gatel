package gatel.uts;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edmodo.rangebar.RangeBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.util.List;

public class PictureActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private Bitmap[] croppedBitmaps;

    private RangeBar rangeBar;
    private ImageView imageView;
    private ImageView imageViewBinary;

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
                // Change the graph, change the image
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bitmap != null) {
            NativeLib.registerBitmap(bitmap);
            NativeLib.equalize(0, 255);
            Bitmap equalizedBitmap = NativeLib.getEqualizedBitmap();
            imageView.setImageBitmap(equalizedBitmap);

            // Get the graphview and set the graphview
            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(255);
            int[] frequency = NativeLib.getFrequency();
            int[] equalizedFrequency = NativeLib.getEqualizedFrequency();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(); // First series
            LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(); series2.setColor(Color.RED); // Second series

            for (int i = 0; i < 256; ++i) {
                series.appendData(new DataPoint(i, frequency[i]), false, 256);
                series2.appendData(new DataPoint(i, equalizedFrequency[i]), false, 256);
            }

            graph.addSeries(series);
            graph.addSeries(series2);

            // Draw rectangle on recognized patterns
            Bitmap tempBitmap = Bitmap.createBitmap(equalizedBitmap.getWidth(), equalizedBitmap.getHeight(), Bitmap.Config.RGB_565);
            List<Pair<Point, Point>> boundaryPoints = PatternRecognizer.getBoundaryPoints(equalizedBitmap);

            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(equalizedBitmap, 0, 0, null);
            Paint boxPaint = new Paint();
            boxPaint.setColor(Color.RED);
            boxPaint.setStyle(Paint.Style.STROKE);
            croppedBitmaps = new Bitmap[boundaryPoints.size()];
            int inc = 0;
            for(Pair<Point, Point> boundaryPoint : boundaryPoints) {
                canvas.drawRect(boundaryPoint.first.x -2 , boundaryPoint.first.y -2,
                        boundaryPoint.second.x + 2, boundaryPoint.second.y + 2, boxPaint);
                Bitmap croppedBitmap=Bitmap.createBitmap(equalizedBitmap, boundaryPoint.first.x -2, boundaryPoint.first.y -2,
                        boundaryPoint.second.x - boundaryPoint.first.x + 4,
                        boundaryPoint.second.y - boundaryPoint.first.y + 4);
                croppedBitmaps[inc] = croppedBitmap;
                ++inc;
            }
            imageViewBinary.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

            // Create cropped number bitmap from picture
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
            for(int i = 0; i < croppedBitmaps.length; ++i) {
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
                horizontalScrollView.setId(i);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Show the original cropped image
                ImageView imageViewOri = new ImageView(this);
                imageViewOri.setPadding(2, 2, 2, 2);
                imageViewOri.setImageBitmap(croppedBitmaps[i]);
                imageViewOri.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
                imageViewOri.setAdjustViewBounds(true);
                imageViewOri.setScaleType(ImageView.ScaleType.FIT_XY);
                linearLayout.addView(imageViewOri);

                // Show the image that has been plotted to 5x5
                ImageView imageViewPlotted = new ImageView(this);
                imageViewPlotted.setPadding(2, 2, 2, 2);
                imageViewPlotted.setImageBitmap(NumberSmoother.getNumberBitmap(equalizedBitmap, boundaryPoints.get(i)));
                imageViewPlotted.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
                imageViewPlotted.setAdjustViewBounds(true);
                imageViewPlotted.setScaleType(ImageView.ScaleType.FIT_XY);
                linearLayout.addView(imageViewPlotted);

                // Show the image that has been thinned using Zhang Suen Algorithm
                Bitmap thinBitmap = ZhangSuen.thinImageBitmap(croppedBitmaps[i]);
                ImageView imageViewThinned = new ImageView(this);
                imageViewThinned.setPadding(2, 2, 2, 2);
                imageViewThinned.setImageBitmap(thinBitmap);
                imageViewThinned.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
                imageViewThinned.setAdjustViewBounds(true);
                imageViewThinned.setScaleType(ImageView.ScaleType.FIT_XY);
                linearLayout.addView(imageViewThinned);

                horizontalScrollView.addView(linearLayout);
                mainLayout.addView(horizontalScrollView);
            }
        }
    }
}
