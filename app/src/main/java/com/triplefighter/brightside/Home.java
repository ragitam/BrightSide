package com.triplefighter.brightside;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.triplefighter.brightside.data.AccessPointListAdapter;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight lamp;
    private HueSharedPreferences prefs;

    private LampuListAdapter adapter;

    String TAG = "Brightside";

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home,container,false);

        //Log.e("HOME", "onCreateView");

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        if(bridge == null){
            AlertDialogWizard.showErrorDialog(getActivity(), "No Bridge Found", R.string.btn_ok);
        }else {
            adapter = new LampuListAdapter(getActivity().getApplicationContext(),bridge.getResourceCache().getAllLights());

            ListView list = (ListView)v.findViewById(R.id.listLampu);
            list.setAdapter(adapter);

        }
        return v;
    }
}
