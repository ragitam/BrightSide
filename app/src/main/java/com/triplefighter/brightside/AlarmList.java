package com.triplefighter.brightside;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    String namaAlarm, idAlarm;
    ProgressDialog pDialog;


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

        pDialog = new ProgressDialog(getContext());

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
                adapter.notifyDataSetChanged();
                listJadwal.setAdapter(adapter);

                for(int i = 0; i < jadwal.size(); i++){
                    phSchedule = jadwal.get(i);
                    Log.d("timer","hari " +phSchedule.getRecurringDays());
                    Log.d("timer","nama " +phSchedule.getName());
                    Log.d("timer","status lampu " +phSchedule.getLightState().isOn());
                    Log.d("timer","status " +phSchedule.getStatus());
                    Log.d("timer","lampu id " +phSchedule.getLightIdentifier());
                    Log.d("timer","local " +phSchedule.getLocalTime());
                    Log.d("timer","start " +phSchedule.getStartTime());
                    Log.d("timer","date " +phSchedule.getDate());
                    Log.d("timer","api " +bridge.getResourceCache().getBridgeConfiguration().getAPIVersion());
                }

                listJadwal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        phSchedule = jadwal.get(i);
                        idAlarm = phSchedule.getIdentifier();
                        namaAlarm = phSchedule.getName();

                        DateFormat df = new SimpleDateFormat("HH");
                        String jam = df.format(phSchedule.getDate());

                        DateFormat dfo = new SimpleDateFormat("dd");
                        String menit = dfo.format(phSchedule.getDate());

                        Intent in = new Intent(getActivity(),AlarmDetail.class);
                        in.putExtra("idAlarm",idAlarm);
                        in.putExtra("namaAlarm",namaAlarm);
                        in.putExtra("jam",jam);
                        in.putExtra("menit",menit);
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
        jadwal = bridge.getResourceCache().getAllSchedules(true);
        adapter = new ScheduleListAdapter(getActivity(),jadwal);
        adapter.notifyDataSetChanged();
        if(jadwal == null){
            listJadwal.setEmptyView(emptyView);
        }else {
            adapter.notifyDataSetChanged();
            //listJadwal.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        jadwal = bridge.getResourceCache().getAllSchedules(true);
        adapter = new ScheduleListAdapter(getActivity(),jadwal);
        if(jadwal == null){
            listJadwal.setEmptyView(emptyView);
        }else {
            listJadwal.setAdapter(adapter);
        }
    }

}
