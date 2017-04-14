package com.triplefighter.brightside;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.triplefighter.brightside.Model.DataLampu;
import com.triplefighter.brightside.data.AccessPointListAdapter;
import com.triplefighter.brightside.data.CobaAdapter;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight light;
    private HueSharedPreferences prefs;

    private LampuListAdapter adapter;

    private List<PHLight> lampu;
    private ListView list;

    ArrayList<String> a;

    String TAG = "Brightside";

    public Home() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home,container,false);

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
        }else {
            lampu = bridge.getResourceCache().getAllLights();
            for (int pos = 0; pos < lampu.size(); pos++){
                light = lampu.get(pos);
                String b = light.getName();
                a = new ArrayList<String>();
                a.add(b);
                Log.d("aaa","lampu " +a);

            }

            adapter = new LampuListAdapter(getActivity().getApplicationContext(),lampu);

            list = (ListView)v.findViewById(R.id.listLampu);
            list.setAdapter(adapter);
        }

        return v;
    }
}