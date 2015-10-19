package com.rc.hover.hoverx;

import android.app.ActionBar;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Created by Johannes on 2015-09-29.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        //getActionBar().hide();

        Thread splash_timer = new Thread(){
            public void run(){
                try{
                    sleep(5000);
                    Intent menuIntent = new Intent ("android.intent.action.MENUACTIVITY");
                    startActivity(menuIntent);
                    } catch(InterruptedException e){

                    e.printStackTrace();
                }
                finally{
                    finish();
                }
            }

        };
        splash_timer.start();

    }
}
