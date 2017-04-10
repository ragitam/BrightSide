package com.triplefighter.brightside.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.triplefighter.brightside.MainActivity;
import com.triplefighter.brightside.R;

import java.util.List;

public class LampuListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PHLight> lampuList;

    private PHLight light;
    private PHLightState state = new PHLightState();
    private PHHueSDK phHueSDK = PHHueSDK.create();
    private PHBridge bridge = phHueSDK.getSelectedBridge();

    private Boolean kondisiLampu;
    private Boolean adaLampu;
    private Boolean status = false;

    class LampuItem{
        private TextView namaLampu;
        private ToggleButton power_but;
        private SeekBar brightness;
        private TextView nama_lampu,alarm_time,brightness_num;
        private ImageView repeat_mode;
        private RadioGroup mode_container;
        private RadioButton eco_mode,night_mode,none_mode;

    }

    public LampuListAdapter(Context context, List<PHLight> lampuList) {
        inflater = LayoutInflater.from(context);
        this.lampuList = lampuList;
    }

    @Override
    public int getCount() {
        return lampuList.size();
    }

    @Override
    public Object getItem(int i) {
        return lampuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        LampuItem item = new LampuItem();;

        if (view == null) {
            view = inflater.inflate(R.layout.list_home, null);

            item.namaLampu = (TextView) view.findViewById(R.id.nama_lampu);
            item.power_but=(ToggleButton) view.findViewById(R.id.power_but);
            item.brightness=(SeekBar) view.findViewById(R.id.brightness);
            item.brightness_num = (TextView) view.findViewById(R.id.brightness_num);
            item.alarm_time = (TextView) view.findViewById(R.id.alarm_time);
            item.repeat_mode= (ImageView) view.findViewById(R.id.repeat_mode);
            item.mode_container= (RadioGroup) view.findViewById(R.id.mode_container);
            item.eco_mode= (RadioButton) view.findViewById(R.id.eco_mode);
            item.night_mode= (RadioButton) view.findViewById(R.id.night_mode);
            item.none_mode= (RadioButton) view.findViewById(R.id.none_mode);

            view.setTag(item);
        } else {
            item = (LampuItem) view.getTag();
        }
        light = lampuList.get(position);
        final String lampuId = light.getName();
        item.namaLampu.setText(lampuId);

        kondisiLampu = light.getLastKnownLightState().isOn();
        adaLampu = light.getLastKnownLightState().isReachable();

        Log.v("status", String.valueOf(status));


        if (item.night_mode.isChecked()){
            item.night_mode.setAlpha(1);
        }

        item.brightness.setProgress(0);
        item.brightness_num.setText("0%");
        item.brightness.setMax(100);
        final LampuItem finalItem = item;
        item.brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                light = lampuList.get(position);
                finalItem.brightness_num.setText(String.valueOf(i)+"%");
                state.setBrightness(i);
                bridge.updateLightState(light,state);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        item.power_but.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                light = lampuList.get(position);
                if(b){
                    if(adaLampu == true || kondisiLampu == false){
                        state.setOn(true);
                        bridge.updateLightState(light,state);
                        status = state.isOn();
                        Log.v("coba","status " +status);
                    }
                }else {
                    state.setOn(false);
                    bridge.updateLightState(light,state);
                    status = state.isOn();
                    Log.v("coba","status " +status);
                }

            }
        });

        return view;
    }



}