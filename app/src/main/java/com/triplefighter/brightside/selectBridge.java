package com.triplefighter.brightside;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.triplefighter.brightside.data.AccessPointListAdapter;
import com.triplefighter.brightside.data.HueSharedPreferences;

import java.util.List;

public class selectBridge extends AppCompatActivity {

    private PHHueSDK phHueSDK;
    public static final String TAG = "Brightside";
    private HueSharedPreferences prefs;
    private AccessPointListAdapter adapter;

    private boolean lastSearchWasIPScan = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bridge);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(this, LoginActivity.class));
        }

        phHueSDK = PHHueSDK.create();

        phHueSDK.setAppName(String.valueOf(R.string.app_name));
        phHueSDK.setDeviceName(Build.MODEL);

        phHueSDK.getNotificationManager().registerSDKListener(listener);

        adapter = new AccessPointListAdapter(getApplicationContext(), phHueSDK.getAccessPointsFound());

        ListView accessPointList = (ListView) findViewById(R.id.listBridge);
        accessPointList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PHAccessPoint accessPoint = (PHAccessPoint) adapter.getItem(i);

                PHBridge connectedBridge = phHueSDK.getSelectedBridge();

                if (connectedBridge != null) {
                    String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
                    if (connectedIP != null) {   // We are already connected here:-
                        phHueSDK.disableHeartbeat(connectedBridge);
                        phHueSDK.disconnect(connectedBridge);
                    }
                }
                AlertDialogWizard.getInstance().showProgressDialog(R.string.connecting, selectBridge.this);
                phHueSDK.connect(accessPoint);
            }
        });
        accessPointList.setAdapter(adapter);

        // Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress   = prefs.getLastConnectedIPAddress();
        String lastUsername    = prefs.getUsername();

        if (lastIpAddress !=null && !lastIpAddress.equals("")) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
                AlertDialogWizard.getInstance().showProgressDialog(R.string.connecting, selectBridge.this);
                phHueSDK.connect(lastAccessPoint);
            }
        }
        else {
            doBridgeSearch();
        }
    }

    public void doBridgeSearch() {
        AlertDialogWizard.getInstance().showProgressDialog(R.string.search_progress, selectBridge.this);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);

        sm.search(true, true);
    }

    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            intent.addFlags(0x8000);
        startActivity(intent);
    }

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found. " + accessPoint.size());

            AlertDialogWizard.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(phHueSDK.getAccessPointsFound());
                    }
                });

            }

        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
            Log.w(TAG, "On CacheUpdated");

        }

        @Override
        public void onBridgeConnected(PHBridge b, String username) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(username);
            AlertDialogWizard.getInstance().closeProgressDialog();
            startMainActivity();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(selectBridge.this, PushBridgeActivity.class));

        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (selectBridge.this.isFinishing())
                return;

            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            }
            else if (code == PHHueError.AUTHENTICATION_FAILED || code== PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                AlertDialogWizard.getInstance().closeProgressDialog();
            }
            else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ");
                AlertDialogWizard.getInstance().closeProgressDialog();
                selectBridge.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialogWizard.showErrorDialog(selectBridge.this, message, R.string.btn_ok);
                        //finish();
                    }
                });

            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) {
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan=true;
                }
                else {
                    AlertDialogWizard.getInstance().closeProgressDialog();
                    selectBridge.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialogWizard.showErrorDialog(selectBridge.this, message, R.string.btn_ok);
                        }
                    });
                }


            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError: parsingErrorsList) {
                Log.e(TAG, "ParsingError : " + parsingError.getMessage());
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.find_new_bridge:
                doBridgeSearch();
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener !=null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
        phHueSDK.disableAllHeartbeat();
    }
}
