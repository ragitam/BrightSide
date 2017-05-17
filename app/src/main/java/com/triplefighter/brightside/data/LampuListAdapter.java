package com.triplefighter.brightside.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;
import com.triplefighter.brightside.R;
import com.triplefighter.brightside.Statistic;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
    private int intensitas;

    public int total_lampu = 0;
    public int lampu_nyala = 0;
    public float[] arr_intentsity = new float[] {0,0};
    public float inte = 0;
    public float usage_total=0,x,usage_cost=0;

    android.os.Handler customHandler = new android.os.Handler();
    android.os.Handler showStats= new android.os.Handler();

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

        LampuItem item = new LampuItem();

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

        final Intent in = new Intent(view.getContext(), Statistic.class);

        total_lampu = lampuList.size();
        Log.v("coba","total " +total_lampu);

        light = lampuList.get(position);
        final String lampuId = light.getName();
        item.namaLampu.setText(lampuId);

        kondisiLampu = light.getLastKnownLightState().isOn();
        adaLampu = light.getLastKnownLightState().isReachable();
        intensitas = light.getLastKnownLightState().getBrightness();

        if(kondisiLampu == true){
            item.power_but.setChecked(true);
            lampu_nyala++;
            item.brightness.setProgress(intensitas);
            int persen = (intensitas*100/254);
            if(persen == 0){
                item.brightness_num.setText("1%");
            }else {
                item.brightness_num.setText(persen +"%");
            }
        }else {
            item.power_but.setChecked(false);
            inte = 0;
            item.brightness.setProgress(1);
            item.brightness_num.setText("1%");
        }

        Log.v("status", String.valueOf(status));


        if (item.night_mode.isChecked()){
            item.night_mode.setAlpha(1);
        }

        item.brightness.setMax(254);
        final LampuItem finalItem = item;
        item.brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                light = lampuList.get(position);
                int persen = (i*100/254);
                if(persen == 0){
                    finalItem.brightness_num.setText("1%");
                }else {
                    finalItem.brightness_num.setText(String.valueOf(persen)+"%");
                }

                state.setBrightness(i);
                bridge.updateLightState(light,state,phLightListener);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int persen = (seekBar.getProgress()*100/254);
                if(persen>0){
                    x= (float) persen/100;
                    arr_intentsity[position]= (float) (7.9836*Math.pow(x,3)-3.3322*Math.pow(x,2)+3.0089*x+1.3604)/1000;
                }else if(persen==0){
                    arr_intentsity[position] = 0;
                }
                Log.d("coba","intensitas ke " +position +" " +arr_intentsity[position]);
            }
        });

        Log.d("coba","intensitas ke " +position +" " +inte);

        item.power_but.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                light = lampuList.get(position);
                if(b){
                    if(adaLampu == true || kondisiLampu == false){
                        lampu_nyala++;
                        state.setOn(true);
                        bridge.updateLightState(light,state,phLightListener);
                        status = state.isOn();
                        Log.v("coba","status " +status);
                    }
                }else {
                    lampu_nyala--;
                    state.setOn(false);
                    bridge.updateLightState(light,state,phLightListener);
                    status = state.isOn();
                    Log.v("coba","status " +status);
                }
                Log.v("coba","nyala " +lampu_nyala);
            }
        });

        Log.v("coba","nyala " +lampu_nyala);

        final LampuItem finalItem1 = item;
        item.mode_container.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                light = lampuList.get(position);
                String id = light.getIdentifier();

                if(i == R.id.night_mode){
                    int bright = 75;
                    finalItem1.brightness.setProgress(bright);
                    int persen = (bright*100/254);
                    finalItem1.brightness_num.setText(persen +"%");
                    finalItem1.brightness.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return true;
                        }
                    });
                    finalItem1.power_but.setChecked(true);
                    state.setOn(true);
                    state.setBrightness(bright);
                    bridge.updateLightState(light,state,phLightListener);
                }else if(i == R.id.none_mode){
                    finalItem1.brightness.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    finalItem1.power_but.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                    finalItem1.power_but.setChecked(true);
                    state.setOn(true);
                    bridge.updateLightState(light,state,phLightListener);
                }else if(i == R.id.eco_mode){
                    PHSchedule schedule = new PHSchedule(String.valueOf(R.string.eco_mode));
                    Calendar cal = Calendar.getInstance();
                    if(Calendar.HOUR_OF_DAY == 18 && Calendar.HOUR_OF_DAY < 7){
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.HOUR_OF_DAY,18);

                        state.setOn(true);
                        finalItem1.power_but.setChecked(true);
                        finalItem1.power_but.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });

                        schedule.setLightState(state);
                        schedule.setLightIdentifier(id);
                        schedule.setLocalTime(true);
                        schedule.setDate(cal.getTime());

                        bridge.createSchedule(schedule,listener);
                    }
                    if(Calendar.HOUR_OF_DAY == 6 && Calendar.HOUR_OF_DAY < 19){
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.HOUR_OF_DAY,6);

                        state.setOn(false);
                        finalItem1.power_but.setChecked(false);
                        finalItem1.power_but.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });

                        schedule.setLightState(state);
                        schedule.setLightIdentifier(id);
                        schedule.setLocalTime(true);
                        schedule.setDate(cal.getTime());

                        bridge.createSchedule(schedule,listener);
                    }
                }
            }
        });

        return view;
    }

    PHScheduleListener listener = new PHScheduleListener() {
        @Override
        public void onCreated(PHSchedule phSchedule) {
            notifyDataSetChanged();
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

    PHLightListener phLightListener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight phLight) {
            notifyDataSetChanged();
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

    public void updateDate(List<PHLight> lampuList){
        this.lampuList = lampuList;
        notifyDataSetChanged();
    }

}