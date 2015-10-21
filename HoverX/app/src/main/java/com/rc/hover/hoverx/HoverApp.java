package com.rc.hover.hoverx;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.rc.hover.hoverx.DataInfo.DataInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johni003 on 2015-10-21.
 */
public class HoverApp extends Application {
    public String TempString;
    public ArrayList<DataInfo> dataHolder;
    public MenuActivity menuActivity;

    @Override
    public void onCreate(){
        super.onCreate();
        dataHolder = new ArrayList<>();
    }

    public void displayToast(String s) {
        menuActivity.displayToast(s);
    }
}
