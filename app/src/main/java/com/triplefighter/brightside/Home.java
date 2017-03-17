package com.triplefighter.brightside;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    ToggleButton power_but;
    SeekBar brightness;
    TextView brightness_num;
    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHBridgeResource phBridgeResource;

    private LampuListAdapter adapter;

    String TAG = "Brightside";

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home,container,false);

        //Log.e("HOME", "onCreateView");

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
        }else{
            //final List<PHLight> lampu = bridge.getResourceCache().getAllLights();
            //List<String> daftar = group.getLightIdentifiers();

            ListView list = (ListView)v.findViewById(R.id.listLampu);

            adapter = new LampuListAdapter(getActivity().getApplicationContext(), bridge.getResourceCache().getAllLights());
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i){
                        case 0:

                            break;
                        case 1:
                            break;
                    }
                }
            });

            /*
            brightness=(SeekBar) v.findViewById(R.id.brightness);
            brightness_num = (TextView) v.findViewById(R.id.brightness_num);
            brightness.setProgress(0);
            brightness.setMax(100);
            brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    //Log.e("BRIGHT", String.valueOf(i));
                        //brightness_num.setText(String.valueOf(i)+"%");

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            */
        }
        return v;
    }

    PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int code, final String message) {
        }

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {
            String iden = phBridgeResource.getIdentifier();
            return;
        }

        @Override
        public void onSearchComplete() {

        }
    };
}
