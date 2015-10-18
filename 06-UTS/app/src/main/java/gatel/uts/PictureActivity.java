package gatel.uts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;

public class PictureActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Context that = this;
        SeekBar sb = (SeekBar)findViewById(R.id.sb);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long timeStart = System.currentTimeMillis();
                Bitmap bitmap = ImageEqualizer.equalize(progress);
                imageView.setImageBitmap(bitmap);
                long timeFinish = System.currentTimeMillis();
//                Toast.makeText(that, String.format("Size = %d x %d, time elapsed: %d ms", bitmap.getWidth(), bitmap.getHeight(), (int)(timeFinish - timeStart)), Toast.LENGTH_SHORT).show();
                Snackbar.make(seekBar, String.format("Size = %d x %d, time elapsed: %d ms", bitmap.getWidth(), bitmap.getHeight(), (int)(timeFinish - timeStart)),
                        Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                Log.d("pictureActivity", String.format("Size = %d x %d, time elapsed: %d ms", bitmap.getWidth(), bitmap.getHeight(), (int)(timeFinish - timeStart)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(bitmap != null) {
            long timeStart = System.currentTimeMillis();
            ImageEqualizer.registerBitmap(bitmap);
            imageView.setImageBitmap(ImageEqualizer.getBaseImage());
            long timeFinish = System.currentTimeMillis();
            Toast.makeText(this, "time elapsed: " + (timeFinish - timeStart) + " ms", Toast.LENGTH_SHORT).show();
            Log.d("pictureActivity", "time elapsed: " + (timeFinish - timeStart) + "ms");

            GraphView graph = (GraphView) findViewById(R.id.graph);
            int[] frequency = ImageEqualizer.getColorFrequency();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

            int increment = 0;
            for (Integer i : frequency) {
                series.appendData(new DataPoint(increment, i), false, 255);
                ++increment;
            }
            graph.addSeries(series);
        }
    }
}
