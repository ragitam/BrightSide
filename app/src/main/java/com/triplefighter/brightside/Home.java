package com.triplefighter.brightside;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    ToggleButton power_but;
    SeekBar brightness;
    TextView nama_lampu,alarm_time,brightness_num;
    ImageView repeat_mode;
    RadioGroup mode_container;
    RadioButton eco_mode,night_mode,none_mode;
    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight lamp;
    private HueSharedPreferences prefs;
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

        if (night_mode.isChecked()){
            night_mode.setAlpha(1);
        }

        brightness.setProgress(0);
        brightness_num.setText("0%");
        brightness.setMax(100);
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Log.e("BRIGHT", String.valueOf(i));
                    brightness_num.setText(String.valueOf(i)+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
