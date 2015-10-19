package com.rc.hover.wifidirect;

import android.app.Activity;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {

    private WifiP2pManager _wfdManager;
    private WifiP2pManager.Channel _wfdChannel;

    private WiFiDirectReceiver _wfdReceiver;

    public ListView _listView = null;
    public ArrayList<String> _arrayList = null;
    public ArrayAdapter<String> _arrayAdapter = null;

    public WifiP2pDevice _selectedDevice = null;
    public TextView connect_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _wfdManager = (WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        _wfdChannel = _wfdManager.initialize(this, getMainLooper(), this);

        _listView = (ListView) this.findViewById(R.id.listView);
        _arrayList = new ArrayList<String>();
        _arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.textView1, _arrayList);
        _listView.setAdapter(_arrayAdapter);
        connect_id = (TextView) findViewById(R.id.connected_to_txt);
        connect_id.setText("No connection!");

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _selectedDevice = _wfdReceiver._wfdDevices[position];
                onClickMenuConnect(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onClickMenuRegister(MenuItem item)
    {
        registerWfdReceiver();
    }

    public void onClickMenuUnregister(MenuItem item)
    {
        unregisterWfdReceiver();
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
            }
            else
            {
                displayToast("No device currently available");
            }
        }

    }
    public void onClickMenuExit(MenuItem item)
    {
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterWfdReceiver();
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
    public void displayToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
    private void registerWfdReceiver(){
        _wfdReceiver = new WiFiDirectReceiver(_wfdManager, _wfdChannel, this);
        _wfdReceiver.registerReceiver();
    }

    private void unregisterWfdReceiver(){
        if(_wfdReceiver != null)
        {
            _wfdReceiver.unregisterReceiver();
            _wfdReceiver = null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_register_receiver) {
            onClickMenuRegister(null);
            return true;
        }
        else if (id == R.id.menu_unregister_receiver) {
            onClickMenuUnregister(null);
            return true;
        }
        else if (id == R.id.menu_discover_devices) {
            onClickMenuDiscover(null);
            return true;
        }
        else if (id == R.id.menu_connect) {
            _selectedDevice =  _wfdReceiver.getFirstAvailableDevice();
            onClickMenuConnect(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
