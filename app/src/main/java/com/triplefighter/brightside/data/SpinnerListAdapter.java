package com.triplefighter.brightside.data;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.triplefighter.brightside.R;

import java.util.List;

public class SpinnerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PHLight> lampuList;

    private PHLight light;
    private PHLightState state = new PHLightState();
    private PHHueSDK phHueSDK = PHHueSDK.create();
    private PHBridge bridge = phHueSDK.getSelectedBridge();

    public SpinnerListAdapter(Context context, List<PHLight> lampuList) {
        inflater = LayoutInflater.from(context);
        this.lampuList = lampuList;
    }

    @Override
    public int getCount() {
        return lampuList.size();
    }

    @Override
    public Object getItem(int i) {
        return lampuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, null);

            TextView idLampuText = (TextView)view.findViewById(R.id.lamp_id);
            TextView namaLampuText = (TextView)view.findViewById(R.id.hue_lamp_name);

            light = lampuList.get(position);
            final String lampuName = light.getName();
            final String lampuId = light.getIdentifier();

            idLampuText.setText(lampuId);
            idLampuText.setVisibility(View.GONE);

            namaLampuText.setText(lampuName);
            namaLampuText.setTextSize(20);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, null);

            TextView idLampuText = (TextView)convertView.findViewById(R.id.lamp_id);
            TextView namaLampuText = (TextView)convertView.findViewById(R.id.hue_lamp_name);

            light = lampuList.get(position);
            final String lampuName = light.getName();
            final String lampuId = light.getIdentifier();

            idLampuText.setText(lampuId);
            idLampuText.setVisibility(View.GONE);

            namaLampuText.setText(lampuName);
            namaLampuText.setTextSize(20);
        }

        return convertView;
    }
}