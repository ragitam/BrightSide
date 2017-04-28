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
    private TextView noAlarmTextView;

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

        noAlarmTextView = (TextView) view.findViewById(R.id.noAlarm);

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
            noAlarmTextView.setVisibility(View.VISIBLE);
        }else {
            jadwal = bridge.getResourceCache().getAllSchedules(true);
            Log.d("nama","nama" +jadwal);
            if(jadwal.isEmpty()){
                noAlarmTextView.setVisibility(View.VISIBLE);
            }else {
                noAlarmTextView.setVisibility(View.GONE);
                listJadwal = (ListView) view.findViewById(R.id.list_lamp);
                adapter = new ScheduleListAdapter(getActivity().getApplicationContext(),jadwal);
                listJadwal.setAdapter(adapter);

                listJadwal.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        phSchedule = jadwal.get(i);
                        namaAlarm = phSchedule.getIdentifier();
                        Log.d("nama","nama "+namaAlarm);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage(R.string.delete_alarm_confirm);
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        bridge.removeSchedule(namaAlarm,listener);
                                    }
                                });

                        builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        return true;
                    }
                });

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

    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

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
