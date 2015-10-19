package com.rc.hover.hoverx;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by jae91 on 2015-10-14.
 */
public class ActionListenerHandler implements WifiP2pManager.ActionListener {

    MenuActivity _mainActivity;
    String _actionDisplayText;
    public ActionListenerHandler(MenuActivity mainActivity, String actionDisplayText) {
        _mainActivity = mainActivity;
        _actionDisplayText = actionDisplayText;
    }

    @Override
    public void onSuccess() {
        _mainActivity.displayToast(_actionDisplayText + " started!");
    }

    @Override
    public void onFailure(int reason) {
        _mainActivity.displayToast(_actionDisplayText + " failed.");
        Log.d("ALH", _actionDisplayText + " failed - error code " + reason);
    }
}
