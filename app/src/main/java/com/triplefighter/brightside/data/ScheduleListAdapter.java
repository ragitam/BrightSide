package com.triplefighter.brightside.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;
import com.triplefighter.brightside.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ScheduleListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PHSchedule> jadwal;

    private PHLight light;
    private PHLightState state = new PHLightState();
    private PHHueSDK phHueSDK = PHHueSDK.create();
    private PHBridge bridge = phHueSDK.getSelectedBridge();
    private PHSchedule schedule;

    class ScheduleItem{
        private TextView namaAlarm;
        private TextView alarmTime;
    }

    public ScheduleListAdapter(Context context,List<PHSchedule> jadwal){
        inflater = LayoutInflater.from(context);
        this.jadwal = jadwal;
    }

    @Override
    public int getCount() {
        return jadwal.size();
    }

    @Override
    public Object getItem(int i) {
        return jadwal.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ScheduleItem item = new ScheduleItem();

        if(view == null){
            view = inflater.inflate(R.layout.list_lamps,viewGroup,false);

            item.namaAlarm = (TextView) view.findViewById(R.id.lamp_name);
            item.alarmTime = (TextView) view.findViewById(R.id.alarm_waktu);

            view.setTag(item);
        } else {
            item = (ScheduleItem) view.getTag();
        }

        schedule = jadwal.get(i);
        Log.d("jadwal","jadwal " +schedule.getDate());

        DateFormat df = new SimpleDateFormat("HH:mm");
        String waktu = df.format(schedule.getDate());

        item.namaAlarm.setText(schedule.getName());
        item.alarmTime.setText(waktu);

        return view;
    }
}
