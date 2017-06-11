package com.triplefighter.brightside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight light;
    private PHLight light2;
    private HueSharedPreferences prefs;

    private LampuListAdapter adapter;

    private List<PHLight> lampu;
    private ListView list;
    public static String namaBridge;
    View emptyLamp;

    ArrayList<String> a;

    int lampuNyala;

    String TAG = "Brightside";

    public Home() {
    }


    @Override
    //Menampilkan list lampu yg dapat di kontrol
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        namaBridge = bridge.getResourceCache().getBridgeConfiguration().getBridgeID();

        emptyLamp = v.findViewById(R.id.noLamp);
        list = (ListView)v.findViewById(R.id.listLampu);
        list.setEmptyView(emptyLamp);

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
        }else {
            lampu = bridge.getResourceCache().getAllLights();

            Log.d("nama","bridge ");

            if(lampu.isEmpty()){    
                list.setEmptyView(emptyLamp);
            }else {
                for (int pos = 0; pos < lampu.size(); pos++){
                    light = lampu.get(pos);
                    String b = light.getName();
                    a = new ArrayList<String>();
                    a.add(b);
                    Log.d("aaa","lampu " +a);

                }
                adapter = new LampuListAdapter(getActivity().getApplicationContext(),lampu);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);

            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();
        lampu = bridge.getResourceCache().getAllLights();
        adapter = new LampuListAdapter(getActivity().getApplicationContext(),lampu);
        if(lampu.isEmpty()) {
            list.setEmptyView(emptyLamp);
        }else {
            adapter.notifyDataSetChanged();
            //list.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PHHueSDK sdk = PHHueSDK.create();
        PHBridge bridge = sdk.getInstance().getSelectedBridge();
        lampu = bridge.getResourceCache().getAllLights();
        adapter = new LampuListAdapter(getActivity().getApplicationContext(),lampu);
        if(lampu.isEmpty()) {
            list.setEmptyView(emptyLamp);
        }else {
            adapter.notifyDataSetChanged();
            //list.setAdapter(adapter);
        }
    }

    PHLightListener listener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight phLight) {

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