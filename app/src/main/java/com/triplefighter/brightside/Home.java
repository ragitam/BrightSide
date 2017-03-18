package com.triplefighter.brightside;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.triplefighter.brightside.data.AccessPointListAdapter;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight lamp;
    private HueSharedPreferences prefs;

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
        }else {
            adapter = new LampuListAdapter(getActivity().getApplicationContext(),bridge.getResourceCache().getAllLights());

            ListView list = (ListView)v.findViewById(R.id.listLampu);
            list.setAdapter(adapter);

        }

        /*
        power_but=(ToggleButton) v.findViewById(R.id.power_but);
        brightness=(SeekBar) v.findViewById(R.id.brightness);
        brightness_num = (TextView) v.findViewById(R.id.brightness_num);
        nama_lampu = (TextView) v.findViewById(R.id.nama_lampu);
        alarm_time = (TextView) v.findViewById(R.id.alarm_time);
        repeat_mode= (ImageView) v.findViewById(R.id.repeat_mode);
        mode_container= (RadioGroup) v.findViewById(R.id.mode_container);
        eco_mode= (RadioButton) v.findViewById(R.id.eco_mode);
        night_mode= (RadioButton) v.findViewById(R.id.night_mode);
        none_mode= (RadioButton) v.findViewById(R.id.none_mode);


        */

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
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {

        }
    };

}
