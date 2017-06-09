package com.triplefighter.brightside.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class LampuAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PHLight> lampuList;

    private PHLight light;
    private PHHueSDK phHueSDK;
    private PHBridge bridge;

    public LampuAdapter(Context context, List<PHLight> lampuList) {
        inflater = LayoutInflater.from(context);
        this.lampuList = lampuList;
    }

    class LampuItem{
        private TextView namaLampu;
        private TextView idUnik;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        LampuItem item = new LampuItem();

        if (view == null) {
            view = inflater.inflate(R.layout.list_lampu_item, null);

            item.namaLampu = (TextView) view.findViewById(R.id.nama);
            item.idUnik = (TextView) view.findViewById(R.id.unique_id);

            view.setTag(item);
        } else {
            item = (LampuItem) view.getTag();
        }

        phHueSDK = PHHueSDK.create();
        bridge = phHueSDK.getSelectedBridge();
        light = lampuList.get(i);
        String lampuNama = light.getName();
        String unikId = light.getUniqueId();

        item.namaLampu.setText(lampuNama);
        item.idUnik.setText(unikId);

        return view;
    }
}
