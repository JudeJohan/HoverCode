package com.rc.hover.wifidirect;

/**
 * Created by Johannes on 2015-10-13.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectReceiver extends BroadcastReceiver implements
        WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {

    WifiP2pManager _wfdManager = null;
    WifiP2pManager.Channel _wfdChannel = null;
    MainActivity _appMainActivity = null;
    private boolean _isWifiDirectEnabled = false;
    WifiP2pDevice[] _wfdDevices = null;

    private IntentFilter _intentFilter = null;
    public WiFiDirectReceiver(){}


    public WiFiDirectReceiver(WifiP2pManager wfdManager, WifiP2pManager.Channel wfdChannel,
                                       MainActivity appMainActivity) {
        super();
         _wfdManager = wfdManager;
         _wfdChannel = wfdChannel;
         _appMainActivity = appMainActivity;
    }

    public void registerReceiver(){
        _appMainActivity.registerReceiver(this, getIntentFilter());
    }

    public void unregisterReceiver()
    {
        _appMainActivity.unregisterReceiver(this);
    }
    public boolean isWifiDirectEnabled()
    {
        return _isWifiDirectEnabled;
    }

    public WifiP2pDevice getFirstAvailableDevice(){
        return isWifiDirectEnabled() && _wfdDevices != null && _wfdDevices.length > 0 ?
                _wfdDevices[0] : null;
    }

    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            handleWifiP2pStateChanged(intent);
        }
        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            handleWifiP2pPeersChanged(intent);
        }
        if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            handleWifiP2pConnectionChanged(intent);
        }
        if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action))
        {
            handleWifiP2pThisDeviceChanged(intent);
        }
    }


    private void  handleWifiP2pStateChanged(Intent intent)
    {
        int wfdState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        _isWifiDirectEnabled = (wfdState == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
    }
    private void  handleWifiP2pPeersChanged(Intent intent)
    {
        //WifiP2pDevice thisDevice = intent.getParcelableExtra();
    }
    private void  handleWifiP2pConnectionChanged(Intent intent)
    {

    }
    private void  handleWifiP2pThisDeviceChanged(Intent intent)
    {
        WifiP2pDevice thisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
    }

    private IntentFilter getIntentFilter()
    {
        if(_intentFilter == null)
        {
            _intentFilter = new IntentFilter();
            _intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            _intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            _intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            _intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }
        return _intentFilter;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

    }
}