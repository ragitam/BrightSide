package com.triplefighter.brightside.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    private PHLightState state2 = new PHLightState();
    private PHHueSDK phHueSDK = PHHueSDK.create();
    private PHBridge bridge = phHueSDK.getSelectedBridge();

    private Boolean kondisiLampu;
    private Boolean adaLampu;
    private Boolean status = false;
    private int intensitas;

    public static int total_lampu = 0;
    public static int lampu_nyala = 0;
    public static float[] arr_intentsity = new float[] {0,0};
    public static int [] arr_hour=new int[] {0,0};
    public float inte = 0;
    public float x;

    class LampuItem{
        private TextView namaLampu;
        public ToggleButton power_but;
        private SeekBar brightness;
        private TextView brightness_num;
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
            item.brightness.setMax(254);
            item.brightness_num = (TextView) view.findViewById(R.id.brightness_num);
            item.mode_container= (RadioGroup) view.findViewById(R.id.mode_container);
            item.eco_mode= (RadioButton) view.findViewById(R.id.eco_mode);
            item.night_mode= (RadioButton) view.findViewById(R.id.night_mode);
            item.none_mode= (RadioButton) view.findViewById(R.id.none_mode);

            view.setTag(item);
        } else {
            item = (LampuItem) view.getTag();
        }

        total_lampu = lampuList.size();

        light = lampuList.get(position);
        final String lampuId = light.getName();
        item.namaLampu.setText(lampuId);

        Log.v("coba","total " +light.getIdentifier());

        kondisiLampu = light.getLastKnownLightState().isOn();
        adaLampu = light.getLastKnownLightState().isReachable();
        intensitas = light.getLastKnownLightState().getBrightness();

        Log.d("coba"," status " +intensitas);

        if(kondisiLampu == true){
            item.power_but.setChecked(true);
            lampu_nyala = 1;
            lampu_nyala++;
            int persen = (intensitas*100/254);
            if(persen == 0){
                item.brightness.setProgress(intensitas);
                item.brightness_num.setText("1%");
                arr_intentsity[position] = 0.0013f;
            }else {
                item.brightness.setProgress(intensitas);
                item.brightness_num.setText(persen +"%");

                x= (float) persen/100;
                arr_intentsity[position]= (float) (7.9836*Math.pow(x,3)-3.3322*Math.pow(x,2)+3.0089*x+1.3604)/1000;
            }
        }else if(kondisiLampu == false){
            item.power_but.setChecked(false);
            if(lampu_nyala > 0){
                lampu_nyala--;
            }else if(lampu_nyala < 0){
                lampu_nyala = 0;
            }
            inte = 0;
            item.brightness.setProgress(1);
            item.brightness_num.setText("1%");
            arr_intentsity[position] = 0;
        }

        Log.v("status", String.valueOf(status));

        if (item.night_mode.isChecked()){
            item.night_mode.setAlpha(1);
        }

        item.brightness.setMax(254);
        final LampuItem finalItem = item;
        final LampuItem finalItem3 = item;
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
                bridge.updateLightState(light,state);
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
                    if(finalItem3.power_but.isChecked() == false){
                        arr_intentsity[position] = 0;
                    }
                }else if(persen==0){
                    arr_intentsity[position] = 0.0013f;
                    if(finalItem3.power_but.isChecked() == false){
                        arr_intentsity[position] = 0;
                    }
                }
                Log.d("coba","intensitas ke " +position +" " +arr_intentsity[position]);
            }
        });

        final LampuItem finalItem2 = item;
        item.power_but.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                light = lampuList.get(position);
                if(b){
                    if(adaLampu == true || kondisiLampu == false){
                        lampu_nyala++;
                        state.setOn(true);
                        bridge.updateLightState(light,state);
                        status = state.isOn();
                        Log.v("coba","status " +status);

                        int persen = (intensitas*100/254);
                        if(persen == 0){
                            arr_intentsity[position] = 0.0013f;

                            finalItem2.brightness.setProgress(intensitas);
                            finalItem2.brightness_num.setText("1%");
                        }else {
                            finalItem2.brightness.setProgress(intensitas);
                            finalItem2.brightness_num.setText(persen +"%");

                            x= (float) persen/100;
                            arr_intentsity[position]= (float) (7.9836*Math.pow(x,3)-3.3322*Math.pow(x,2)+3.0089*x+1.3604)/1000;
                        }

                        arr_hour[position]=arr_hour[position]+1;
                    }
                }else {
                    lampu_nyala--;
                    state.setOn(false);
                    bridge.updateLightState(light,state);
                    status = state.isOn();
                    Log.v("coba","status " +status);

                    arr_intentsity[position] = 0;
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
                    int bright = 13;
                    finalItem1.brightness.setProgress(bright);
                    int persen = (bright*100/254);
                    x= (float) persen/100;
                    arr_intentsity[position]= (float) (7.9836*Math.pow(x,3)-3.3322*Math.pow(x,2)+3.0089*x+1.3604)/1000;
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
                    PHSchedule schedule;
                    Calendar cal = Calendar.getInstance();

                    int a = cal.get(Calendar.HOUR_OF_DAY);

                    Log.d("coba","jam " +a);
                    if(a > 17 || a < 6){
                        schedule = new PHSchedule(String.valueOf(R.string.eco_mode_on));
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.HOUR_OF_DAY,18);

                        int bright = 191;
                        finalItem1.brightness.setProgress(bright);
                        int persen = (bright*100/254);
                        x= (float) persen/100;
                        arr_intentsity[position]= (float) (7.9836*Math.pow(x,3)-3.3322*Math.pow(x,2)+3.0089*x+1.3604)/1000;
                        finalItem1.brightness_num.setText(persen +"%");
                        finalItem1.brightness.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                        state.setOn(true);
                        finalItem1.power_but.setChecked(true);

                        schedule.setLightState(state);
                        schedule.setLightIdentifier(id);
                        schedule.setLocalTime(true);
                        schedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_NONE.getValue());
                        schedule.setStatus(PHSchedule.PHScheduleStatus.ENABLED);
                        schedule.setDate(cal.getTime());

                        bridge.createSchedule(schedule,listener);
                    }else if(a > 5 || a < 18){
                        schedule = new PHSchedule(String.valueOf(R.string.eco_mode_off));
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.HOUR_OF_DAY,6);

                        int bright = 191;
                        finalItem1.brightness.setProgress(bright);
                        int persen = (bright*100/254);
                        x= (float) persen/100;
                        arr_intentsity[position]= (float) (7.9836*Math.pow(x,3)-3.3322*Math.pow(x,2)+3.0089*x+1.3604)/1000;
                        finalItem1.brightness_num.setText(persen +"%");
                        finalItem1.brightness.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                        state.setOn(false);
                        finalItem1.power_but.setChecked(false);


                        schedule.setLightState(state);
                        schedule.setLightIdentifier(id);
                        schedule.setLocalTime(true);
                        schedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_NONE.getValue());
                        schedule.setStatus(PHSchedule.PHScheduleStatus.ENABLED);
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
            Log.d("onCreated","eco mode has been created");
        }

        @Override
        public void onSuccess() {
            Log.d("onSuccess","eco mode has been created");
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

        }
    };

    PHScheduleListener listener2 = new PHScheduleListener() {
        @Override
        public void onCreated(PHSchedule phSchedule) {
            Log.d("onCreated","eco mode has been created");
        }

        @Override
        public void onSuccess() {
            Log.d("onSuccess","eco mode has been created");
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


}