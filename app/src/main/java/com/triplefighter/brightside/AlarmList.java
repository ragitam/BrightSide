package com.triplefighter.brightside;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHDateTimePattern;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHSchedule;
import com.triplefighter.brightside.data.ScheduleListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmList extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHSchedule phSchedule;
    private List<PHSchedule> jadwal;

    private ScheduleListAdapter adapter;
    private ListView listJadwal;
    private TextView noAlarmTextView;

    public AlarmList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm_list,container,false);

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        //noAlarmTextView = (TextView) view.findViewById(R.id.noAlarm);

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
        }else {
            jadwal = bridge.getResourceCache().getAllSchedules(true);

            listJadwal = (ListView) view.findViewById(R.id.list_lamp);
            adapter = new ScheduleListAdapter(getActivity().getApplicationContext(),jadwal);
            listJadwal.setAdapter(adapter);
        }


        return view;
    }

}
