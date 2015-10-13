package com.rc.hover.hoverx;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.net.wifi.p2p.*;
import android.app.ListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MenuActivity extends AppCompatActivity {

    ListView itemList;
    final HashMap<String, String> buddies = new HashMap<String, String>();
    ArrayList<String> itemArrayList = new ArrayList<String>();
    ArrayAdapter<String> itemArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        itemList = (ListView) findViewById(R.id.listView);
        final Button connect = (Button) findViewById(R.id.connect_button);
        final Button drive = (Button) findViewById(R.id.drive_button);
        drive.setEnabled(false);
        final TextView connect_status = (TextView) findViewById(R.id.connect_text);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connect.getText() == getResources().getString(R.string.connect_button))
                {
                    connect_status.setText(R.string.connection_status_con);
                    connect.setText(R.string.disconnect_button);
                    connect_status.setTextColor(getResources().getColor(R.color.LIME));
                    drive.setEnabled(true);
                }
                else if(connect.getText() ==  getResources().getString(R.string.disconnect_button))
                {
                    connect_status.setText(R.string.connection_status);
                    connect.setText(R.string.connect_button);
                    connect_status.setTextColor(getResources().getColor(R.color.RED));
                    drive.setEnabled(false);
                }
            }
        });

        itemArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                itemArrayList);
        itemList.setAdapter(itemArrayAdapter);

        discoverService();


    }

    public void enable_drive(View v){
        Intent drive_activity = new Intent(this, DriveActivity.class);
        startActivity(drive_activity);
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
                Log.d("HoverX", "DNS SD TXT RECORD AVAILABLE - " + map.toString());
                buddies.put(wifiP2pDevice.deviceAddress, map.get("buddyname"));
                itemArrayList.add(map.get("buddyname") + " : " + wifiP2pDevice.deviceAddress);
                itemArrayAdapter.notifyDataSetChanged();
                //itemArrayAdapter.add(map.get("buddyname") + " : " + wifiP2pDevice.deviceAddress);

                WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType,
                                                        WifiP2pDevice resourceType) {
                        resourceType.deviceName =
                                buddies.containsKey(resourceType.deviceAddress) ?
                                        buddies.get(resourceType.deviceAddress) :
                                        resourceType.deviceName;
                    }
                };
            }
        };
    }

}
