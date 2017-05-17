package com.triplefighter.brightside;

import android.app.AlarmManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHSchedule;
import com.triplefighter.brightside.data.ScheduleListAdapter;
import com.triplefighter.brightside.data.SpinnerListAdapter;

public class AlarmDetail extends AppCompatActivity {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight light;
    private PHLightState state;
    private PHSchedule phSchedule;

    TimePicker time_pick;
    AlarmManager alarmManager;
    Calendar calendar;
    Spinner lamp_name_spinner;
    TextView lamp_name_view;
    Switch repeat_alarm;
    Switch condition;
    Button submit_alarm;
    EditText alarm_name;
    CheckBox monday,tuesday,wednesday,thursday,friday,saturday,sunday;

    String choosen;
    int jam,menit;

    String idAlarm;

    List<PHLight> lamp_name_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        Intent in = getIntent();
        idAlarm = in.getStringExtra("idAlarm");

        if(idAlarm == null){
            getSupportActionBar().setTitle("Set Alarm");
        }else {
            getSupportActionBar().setTitle("Edit Alarm");
        }

        calendar = Calendar.getInstance();

        time_pick=(TimePicker) findViewById(R.id.time_pick);
        lamp_name_view=(TextView) findViewById(R.id.lamp_name_view);
        lamp_name_spinner=(Spinner) findViewById(R.id.lamp_name);
        repeat_alarm=(Switch) findViewById(R.id.repeat_alarm);
        condition=(Switch) findViewById(R.id.lamp_condition);
        submit_alarm=(Button) findViewById(R.id.submit_alarm);
        monday=(CheckBox) findViewById(R.id.monday);
        tuesday=(CheckBox) findViewById(R.id.tuesday);
        wednesday=(CheckBox) findViewById(R.id.wednesday);
        thursday=(CheckBox) findViewById(R.id.thursday);
        friday=(CheckBox) findViewById(R.id.friday);
        saturday=(CheckBox) findViewById(R.id.saturday);
        sunday=(CheckBox) findViewById(R.id.sunday);
        submit_alarm=(Button) findViewById(R.id.submit_alarm);
        alarm_name = (EditText) findViewById(R.id.alarm_name);

        repeat_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeat_alarm.isChecked()){
                    monday.setChecked(true);
                    tuesday.setChecked(true);
                    wednesday.setChecked(true);
                    thursday.setChecked(true);
                    friday.setChecked(true);
                    saturday.setChecked(true);
                    sunday.setChecked(true);
                }
            }
        });

        lamp_name_arr = bridge.getResourceCache().getAllLights();

        if(lamp_name_arr.isEmpty()){
            SpinnerListAdapter adapter = new SpinnerListAdapter(this,null);
            lamp_name_spinner.setAdapter(adapter);
        }else {
            SpinnerListAdapter adapter = new SpinnerListAdapter(this,lamp_name_arr);
            lamp_name_spinner.setAdapter(adapter);
            lamp_name_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    light = lamp_name_arr.get(i);
                    choosen=light.getIdentifier();
                    lamp_name_view.setText(choosen);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        submit_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
    }



    public void setAlarm(){
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        int cur_hour=calendar.get(Calendar.HOUR);
        int cur_minute=calendar.get(Calendar.MINUTE);
        if(currentApiVersion>android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
            calendar.set(Calendar.HOUR_OF_DAY,time_pick.getHour());
            calendar.set(Calendar.MINUTE,time_pick.getMinute());
            jam=time_pick.getHour();
            menit=time_pick.getMinute();
            if(jam<cur_hour && menit<cur_minute){
                calendar.add(Calendar.DATE,1);
                Log.d("def","besok2");
                if(time_pick.getMinute()<10){
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala besok pada jam "+jam+":0"+menit,Toast.LENGTH_SHORT).show();
                }else{
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala besok pada jam "+jam+":"+menit,Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.d("asd","besok");
                if(time_pick.getMinute()<10){
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala pada jam "+jam+":0"+menit,Toast.LENGTH_SHORT).show();
                }else{
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala pada jam "+jam+":"+menit,Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            calendar.set(Calendar.HOUR_OF_DAY,time_pick.getCurrentHour());
            calendar.set(Calendar.MINUTE,time_pick.getCurrentMinute());
            jam=time_pick.getCurrentHour();
            menit=time_pick.getCurrentMinute();
            if(jam<=cur_hour && menit<=cur_minute){
                calendar.set(Calendar.DATE,1);
                if(time_pick.getCurrentMinute()<10){
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala besok pada jam "+jam+":0"+menit,Toast.LENGTH_SHORT).show();
                }else{
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala besok pada jam "+jam+":"+menit,Toast.LENGTH_SHORT).show();
                }
            }else{
                if(time_pick.getCurrentMinute()<10){
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala pada jam "+jam+":0"+menit,Toast.LENGTH_SHORT).show();
                }else{
                    addSchedule();
                    Toast.makeText(AlarmDetail.this,choosen+" akan menyala pada jam "+jam+":"+menit,Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(idAlarm == null){
            MenuItem menuItem = menu.findItem(R.id.delete_alarm);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete_alarm){
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteAlarm(){
        if(idAlarm != null){
            bridge.removeSchedule(idAlarm,listener);
        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_alarm_confirm);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAlarm();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addSchedule(){
        String namaAlarm = alarm_name.getText().toString().trim();

        if(namaAlarm.isEmpty()){
            phSchedule = new PHSchedule("Brightside Alarm");
        }else {
            phSchedule = new PHSchedule(namaAlarm);
        }

        state = new PHLightState();

        phSchedule.setLightIdentifier(choosen);
        phSchedule.setLocalTime(true);
        phSchedule.setDate(calendar.getTime());

        if(repeat_alarm.isChecked()){
            phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_ALL_DAY.getValue());
        }else{
            if(monday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_MONDAY.getValue());
            }
            if(tuesday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_TUESDAY.getValue());
            }
            if(wednesday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_WEDNESDAY.getValue());
            }
            if(thursday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_THURSDAY.getValue());
            }
            if(friday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_FRIDAY.getValue());
            }
            if(saturday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_SATURDAY.getValue());
            }
            if(sunday.isChecked()){
                phSchedule.setRecurringDays(PHSchedule.RecurringDay.RECURRING_SUNDAY.getValue());
            }
        }

        if(condition.isChecked() == true){
            state.setOn(true);
            phSchedule.setLightState(state);
        }else {
            state.setOn(false);
            phSchedule.setLightState(state);
        }



        bridge.createSchedule(phSchedule,listener);
        finish();
    }

    PHScheduleListener listener = new PHScheduleListener() {
        @Override
        public void onCreated(PHSchedule phSchedule) {

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
