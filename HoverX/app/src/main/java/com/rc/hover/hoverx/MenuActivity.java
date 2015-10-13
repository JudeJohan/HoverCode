package com.rc.hover.hoverx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.app.ListFragment;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenuActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener  {

    ListView itemList;
    final HashMap<String, String> buddies = new HashMap<String, String>();
    ArrayList<String> itemArrayList = new ArrayList<String>();
    ArrayAdapter<String> itemArrayAdapter;
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    public static final String TAG = "wifidirectdemo";
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private BroadcastReceiver receiver = null;
    private List peers = new ArrayList();


    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(wifiP2pDeviceList.getDeviceList());

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
            itemArrayAdapter.notifyDataSetChanged();
            if (peers.size() == 0) {
                Log.d(TAG, "No devices found");
                return;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        itemList = (ListView) findViewById(R.id.listView);
        final Button connect = (Button) findViewById(R.id.connect_button);
        final Button drive = (Button) findViewById(R.id.drive_button);
        drive.setEnabled(false);
        final TextView connect_status = (TextView) findViewById(R.id.connect_text);

        receiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

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

        //discoverService();
        setUpWifi();


    }

    @Override
    public void connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MenuActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    private void setUpWifi() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    @Override
    public void onChannelDisconnected() {

    }

    /*private void discoverService() {
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
    }*/

}
