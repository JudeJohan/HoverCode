package com.rc.hover.hoverx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Johannes on 2015-10-05.
 */
public class DriveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        vsb1 = (VerticalSeekBar)findViewById(R.id.seekBar);
        vsb2 = (VerticalSeekBar)findViewById(R.id.seekBar2);
        tx1 = (TextView)findViewById(R.id.textView);
        tx2 = (TextView)findViewById(R.id.textView2);
        tx1.setText("Current value: " + vsb1.getProgress());
        tx2.setText("Current value: " + vsb2.getProgress());

        vsb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tx1.setText("Current value: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((VerticalSeekBar) seekBar).setProgressAndThumb(50);
            }
        });

        vsb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tx2.setText("Current value: " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((VerticalSeekBar)seekBar).setProgressAndThumb(50);
            }
        });
    }
}
