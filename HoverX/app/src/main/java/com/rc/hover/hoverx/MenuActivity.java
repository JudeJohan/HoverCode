package com.rc.hover.hoverx;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.internal.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.app.ListFragment;
import android.widget.Toast;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MenuActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {

    public ThreadSpeaker _threadSpeaker = null;
    public ListView _listView = null;
    public List<String> _arrayList = null;
    public ArrayAdapter<String> _arrayAdapter = null;
    private WiFiDirectReceiver _wfdReceiver = null;
    private WifiP2pManager _wfdManager = null;
    private WifiP2pManager.Channel _wfdChannel = null;
    public WifiP2pDevice _selectedDevice = null;

    Handler m_handler;
    Runnable m_handlerTask ;
    final Runnable toastMe = new Runnable() {
        @Override
        public void run() {
            displayToast(_threadSpeaker.text_from_read);
        }
    };

    private final IntentFilter intentFilter = new IntentFilter();

    public static final String TAG = "wifidirectdemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        _threadSpeaker = new ThreadSpeaker();
        _arrayList = new ArrayList<String>();
        _wfdManager = (WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        _wfdChannel = _wfdManager.initialize(this, getMainLooper(), this);
        _listView = (ListView) findViewById(R.id.listView);
        _arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.item,
                R.id.textView1,
                _arrayList);
        _listView.setAdapter(_arrayAdapter);
        final Button connect = (Button) findViewById(R.id.connect_button);
        final Button drive = (Button) findViewById(R.id.drive_button);
        drive.setEnabled(false);
        final TextView connect_status = (TextView) findViewById(R.id.connect_text);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connect.getText() == getResources().getString(R.string.connect_button)) {

                   onClickMenuConnect(null);

                       connect_status.setText(R.string.connection_status_con);
                       connect.setText(R.string.disconnect_button);
                       connect_status.setTextColor(getResources().getColor(R.color.LIME));
                       drive.setEnabled(true);
                       _threadSpeaker.text_to_write = "Hej min vän";

                } else if (connect.getText() == getResources().getString(R.string.disconnect_button)) {
                    connect_status.setText(R.string.connection_status);
                    connect.setText(R.string.connect_button);
                    connect_status.setTextColor(getResources().getColor(R.color.RED));
                    drive.setEnabled(false);
                    _threadSpeaker.text_to_write = "Hej då min vän";
                }
            }
        });



        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(_selectedDevice == null) {
                    view.setBackgroundColor(Color.parseColor("#00FF00"));
                    _selectedDevice = _wfdReceiver._wfdDevices[position];
                }
                else
                {
                    _selectedDevice = null;
                   for(int i = 0; i < _wfdReceiver._wfdDevices.length; i++)
                   {
                       _listView.getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"));
                   }
                    view.setBackgroundColor(Color.parseColor("#00FF00"));
                    _selectedDevice = _wfdReceiver._wfdDevices[position];
                }
            }
        });


        _wfdReceiver = new WiFiDirectReceiver(_wfdManager, _wfdChannel, this);
        _wfdReceiver.registerReceiver();

        m_handler = new Handler();
        m_handlerTask = new Runnable() {
            @Override
            public void run() {
                m_handler.postDelayed(m_handlerTask, 5000);
                onClickMenuDiscover(null);
            }
        };
        m_handler.postDelayed(m_handlerTask, 5000);
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

    public void displayToast(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
    }

    public void onClickMenuDiscover(MenuItem item)
    {
        if(isWfdReceiverRegisteredAndFeatureEnabled())
        {
            _wfdManager.discoverPeers(_wfdChannel, new ActionListenerHandler(this, "Discover Peers"));
        }
    }

    public void onClickMenuConnect(MenuItem item)
    {
        if(isWfdReceiverRegisteredAndFeatureEnabled())
        {
            //WifiP2pDevice theDevice = _wfdReceiver.getFirstAvailableDevice();
            WifiP2pDevice theDevice = _selectedDevice;
            if(theDevice != null)
            {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = theDevice.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                _wfdManager.connect(_wfdChannel, config, new ActionListenerHandler(this, "Connection"));
                displayToast("Connected to: " + theDevice.deviceName);
            }
            else
            {
                displayToast("No device currently available");
            }
        }

    }

    private boolean isWfdReceiverRegisteredAndFeatureEnabled()
    {
        boolean isWfdUsable = _wfdReceiver != null && _wfdReceiver.isWifiDirectEnabled();
        if(!isWfdUsable)
        {
            showWfdReceiverNotRegisteredOrFeatureNotEnabledMessage();
        }
        return isWfdUsable;
    }

    private void showWfdReceiverNotRegisteredOrFeatureNotEnabledMessage()
    {
        displayToast(_wfdReceiver == null ? "Wifi Broadcast Receiver Not Yet Registered" : "Wifi Direct Not Enabled On Phone");
    }

    public void onChannelDisconnected(){
        displayToast("Wifi Direct channel disconnected - Reinitializing");
        reinitializeChannel();
    }

    private void reinitializeChannel(){
        _wfdChannel = _wfdManager.initialize(this, getMainLooper(), this);
        if(_wfdChannel != null){
            displayToast("Initialization successful");
        }
        else
        {
            displayToast("Initialization failed");
        }
    }

    private void unregisterWfdReceiver(){
        if(_wfdReceiver != null)
        {
            _wfdReceiver.unregisterReceiver();
            _wfdReceiver = null;
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterWfdReceiver();
    }
}
