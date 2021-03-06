package com.rc.hover.hoverx;

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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.rc.hover.hoverx.DataInfo.DataInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectReceiver extends BroadcastReceiver implements
        WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {

    HoverApp app;
    WifiP2pManager _wfdManager = null;
    WifiP2pManager.Channel _wfdChannel = null;
    MenuActivity _appMainActivity = null;
    DriveActivity _appDriveActivity = null;
    private boolean _isWifiDirectEnabled = false;
    public WifiP2pDevice[] _wfdDevices = null;

    private IntentFilter _intentFilter = null;
    Thread networkThread = null;

    public WiFiDirectReceiver(){}

    public WiFiDirectReceiver(WifiP2pManager wfdManager, WifiP2pManager.Channel wfdChannel,
                              MenuActivity appMainActivity) {
        super();
        _wfdManager = wfdManager;
        _wfdChannel = wfdChannel;
        _appMainActivity = appMainActivity;
        app = _appMainActivity.app;
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
        _wfdManager.requestPeers(_wfdChannel, this);
        //WifiP2pDevice thisDevice = intent.getParcelableExtra();
    }
    private void  handleWifiP2pConnectionChanged(Intent intent)
    {
        NetworkInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {
            _wfdManager.requestConnectionInfo(_wfdChannel, this);
        }
        else {
            //app.displayToast("Connection closed.");
        }
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
        if(info.groupFormed) {
            if(info.isGroupOwner) {
                if(networkThread != null) {
                    networkThread.interrupt();
                }
                networkThread = new Thread(new nettThread(true, 10101, null));
                networkThread.start();
                //socket.bind();
            }
            else {
                if(networkThread != null) {
                    networkThread.interrupt();
                }
                networkThread = new Thread(new nettThread(false, 10101, info.groupOwnerAddress));
                networkThread.start();
                //open a socket to info.groupOwnerAddress
            }
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        //_appMainActivity.displayToast("onPeersAvaliable");

        if(peers != null &&
                peers.getDeviceList() != null &&
                peers.getDeviceList().size() > 0) {
            _wfdDevices = peers.getDeviceList().toArray(new WifiP2pDevice[0]);
            _appMainActivity._arrayList.clear();
            _appMainActivity._arrayAdapter.clear();
            for(int i = 0; i < _wfdDevices.length; i++) {
                _appMainActivity._arrayList.add(i, _wfdDevices[i].deviceName);
            }
            _appMainActivity._arrayAdapter.notifyDataSetChanged();
        } else {
            _wfdDevices = null;
        }
    }

    class nettThread implements Runnable {

        private boolean _isServer = false;
        private InetAddress _serverAddr = null;
        private int _port = 0;
        public Socket socket = null;
        public ServerSocket serverSocket = null;

        Handler updateConversationHandler;

        public nettThread(boolean isServer, int port, InetAddress serverAddr) {
            _isServer = isServer;
            _serverAddr = serverAddr;
            _port = port;
        }

        @Override
        public void run() {
            if (_isServer) {
                try {
                    serverSocket = new ServerSocket(_port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(_serverAddr, _port), 5000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (!Thread.currentThread().isInterrupted()) {
                if (_isServer) {
                    try {
                        socket = serverSocket.accept();
                        commThread comThread = new commThread(socket);
                        new Thread(comThread).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!app.dataHolder.isEmpty()) {
                        try {
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                            out.write(app.dataHolder.get(0).toByteArray());
                            app.dataHolder.remove(0);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {

                        }
                    }
                }
            }
        }
    }

    class commThread implements Runnable {
        private Socket _clientSocket;
        private DataInputStream _stream;

        public commThread(Socket clientSocket) {
            _clientSocket = clientSocket;
            try {
                _stream = new DataInputStream(_clientSocket.getInputStream());
            } catch (IOException e) {

            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte id;
                    byte type;
                    int size;

                    id = _stream.readByte();
                    type = _stream.readByte();
                    size = _stream.readInt();

                    switch (id) {
                        case DataInfo.ID.LeftDrive:
                            if(type == DataInfo.TYPE.Integer) {
                                final int dataInt = _stream.readInt();
                                Log.d("LeftDrive","" + dataInt);
                            }
                            break;
                        case DataInfo.ID.RightDrive:
                            if(type == DataInfo.TYPE.Integer) {
                                final int dataInt = _stream.readInt();
                                Log.d("RightDrive","" + dataInt);
                            }
                            break;
                        case DataInfo.ID.ToToast:
                            if(type == DataInfo.TYPE.String) {
                                byte[] dataByte = new byte[size - DataInfo.SIZE_OF_OVERHEAD];
                                int result = _stream.read(dataByte, 0, size - DataInfo.SIZE_OF_OVERHEAD);

                                final String temp = new String(dataByte, "UTF-8");

                                _appMainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _appMainActivity.displayToast(temp);
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

