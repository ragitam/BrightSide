package com.triplefighter.brightside;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHSchedule;
import com.triplefighter.brightside.data.ScheduleListAdapter;

import java.util.List;
import java.util.Map;


public class AlarmList extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHSchedule phSchedule;
    private List<PHSchedule> jadwal;

    private ScheduleListAdapter adapter;
    private ListView listJadwal;
    private View emptyView;

    String namaAlarm;

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

        emptyView = view.findViewById(R.id.emptyviewAlarm);
        listJadwal = (ListView) view.findViewById(R.id.list_lamp);
        listJadwal.setEmptyView(emptyView);

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
            listJadwal.setEmptyView(emptyView);
        }else {
            jadwal = bridge.getResourceCache().getAllSchedules(true);
            Log.d("nama","nama" +jadwal);
            if(jadwal.isEmpty()){
                listJadwal.setEmptyView(emptyView);
            }else {
                adapter = new ScheduleListAdapter(getActivity().getApplicationContext(),jadwal);
                listJadwal.setAdapter(adapter);

                listJadwal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        phSchedule = jadwal.get(i);
                        namaAlarm = phSchedule.getIdentifier();
                        Intent in = new Intent(getActivity(),AlarmDetail.class);
                        in.putExtra("idAlarm",namaAlarm);
                        startActivity(in);
                    }
                });
            }
        }

        return view;
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
