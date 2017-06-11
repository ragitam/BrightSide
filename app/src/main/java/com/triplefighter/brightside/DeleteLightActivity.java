package com.triplefighter.brightside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuAdapter;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteLightActivity extends AppCompatActivity {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight light;

    private LampuAdapter adapter;

    private List<PHLight> lampu;
    private ListView listView;
    View emptyLamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_light);
        getSupportActionBar().setTitle(getText(R.string.delete_lamp_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        listView = (ListView)findViewById(R.id.dataLampu);
        emptyLamp = findViewById(R.id.emptyViewLamp);
        listView.setEmptyView(emptyLamp);

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(this, "No Bridge Found", R.string.btn_ok);
        }else {
            lampu = bridge.getResourceCache().getAllLights();

            if(lampu.isEmpty()){
                listView.setEmptyView(emptyLamp);
            }else {

                adapter = new LampuAdapter(this,lampu);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        light = lampu.get(i);
                        final String idLampu = light.getIdentifier();
                        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteLightActivity.this);
                        builder.setMessage(R.string.delete_alarm_confirm);
                        builder.setPositiveButton(getText(R.string.delete_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                bridge.deleteLight(idLampu,listener);
                            }
                        });
                        builder.setNegativeButton(getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        }
    }

    PHLightListener listener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight phLight) {

        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> list) {

        }

        @Override
        public void onSearchComplete() {

        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

        }
    };
}
