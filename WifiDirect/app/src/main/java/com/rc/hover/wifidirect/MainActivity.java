package com.rc.hover.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener {

    private WifiP2pManager _wfdManager;
    private WifiP2pManager.Channel _wfdChannel;

    private WiFiDirectReceiver _wfdReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _wfdManager = (WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        _wfdChannel = _wfdManager.initialize(this, getMainLooper(), this);


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

        }
    }

    public void onClickMenuConnect(MenuItem item)
    {
        if(isWfdReceiverRegisteredAndFeatureEnabled())
        {
            WifiP2pDevice theDevice = _wfdReceiver.getFirstAvailableDevice();
            if(theDevice != null)
            {

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
        if (id == R.id.action_settings) {
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
