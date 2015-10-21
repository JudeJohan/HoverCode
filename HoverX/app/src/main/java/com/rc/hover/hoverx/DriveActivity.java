
package com.rc.hover.hoverx;

import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rc.hover.hoverx.DataInfo.DataInfo;
import com.rc.hover.hoverx.DataInfo.intDataInfo;

/**
 * Created by Johannes on 2015-10-05.
 */
public class DriveActivity extends AppCompatActivity {
    VerticalSeekBar vsb1, vsb2 = null;
    TextView tx1, tx2 = null;
    HoverApp app;

    Handler m_handler;
    Runnable m_handlerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        app = ((HoverApp)getApplication());
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
                ((VerticalSeekBar) seekBar).setProgressAndThumb(50);
            }
        });

        m_handler = new Handler();
        m_handlerTask = new Runnable() {
            @Override
            public void run() {
                m_handler.postDelayed(m_handlerTask, 100);
                app.dataHolder.add(new intDataInfo(DataInfo.ID.LeftDrive, DataInfo.TYPE.Integer,
                        vsb1.getProgress()));
                app.dataHolder.add(new intDataInfo(DataInfo.ID.RightDrive, DataInfo.TYPE.Integer,
                        vsb2.getProgress()));
            }
        };
        m_handler.postDelayed(m_handlerTask, 100);
    }
}