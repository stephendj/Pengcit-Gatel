package gatel.uts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.util.Range;
import android.view.View;
import android.widget.ImageView;

import com.edmodo.rangebar.RangeBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.util.List;

public class PictureActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageEqualizer equalizer;

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
            equalizer = ImageEqualizer.create(bitmap);
            Bitmap equalizedBitmap = equalizer.getBaseImage();
            imageView.setImageBitmap(equalizedBitmap);

            // Get the graphview and set the graphview
            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(255);
            int[] frequency = equalizer.getColorFrequency();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(); // First series
            LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(); series2.setColor(Color.RED); // Second series

            int increment = 0;
            for(Integer i : frequency) {
                series.appendData(new DataPoint(increment, i), false, 256);
                series2.appendData(new DataPoint(increment, i + 2000), false, 256);
                ++increment;
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
            for(Pair<Point, Point> boundaryPoint : boundaryPoints) {
                canvas.drawRect(boundaryPoint.first.x -2 , boundaryPoint.first.y -2,
                        boundaryPoint.second.x + 2, boundaryPoint.second.y + 2, boxPaint);
            }
            imageViewBinary.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }
    }
}
