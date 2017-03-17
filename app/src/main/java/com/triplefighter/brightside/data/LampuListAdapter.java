package com.triplefighter.brightside.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.triplefighter.brightside.R;

import java.util.List;

public class LampuListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PHLight> lampuList;

    class LampuItem{
        private TextView namaLampu;

    }

    public LampuListAdapter(Context context, List<PHLight> lampuList) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        LampuItem item = new LampuItem();;

        if (view == null) {
            view = inflater.inflate(R.layout.list_home, null);

            item.namaLampu = (TextView) view.findViewById(R.id.nama_lampu);

            view.setTag(item);
        } else {
            item = (LampuItem) view.getTag();
        }
        PHLight light = lampuList.get(i);
        item.namaLampu.setText(light.getIdentifier());

        Log.v("gg",light.getIdentifier());

        return view;
    }
}
