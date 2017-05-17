package com.triplefighter.brightside;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import java.util.ArrayList;
import java.util.List;

public class Statistic extends Fragment {

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHLight light;

    private List<PHLight> lampu;

    public Statistic() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_statistic, container, false);

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();

        lampu = bridge.getResourceCache().getAllLights();

        TextView total = (TextView) v.findViewById(R.id.lamps_total);
        total.setText(String.valueOf(lampu.size()));

        return v;
    }

}
