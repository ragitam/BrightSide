package com.triplefighter.brightside.data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.triplefighter.brightside.R;

import java.util.List;

public class AccessPointListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<PHAccessPoint> accessPoints;

    public static String namaBridge;

    class BridgeListItem {
        private TextView bridgeIp;
        private TextView bridgeMac;
        private TextView bridgeName;
    }

    public AccessPointListAdapter(Context context, List<PHAccessPoint> accessPoints) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        this.accessPoints = accessPoints;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        BridgeListItem item;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_bridge, null);

            item = new BridgeListItem();
            item.bridgeMac = (TextView) convertView.findViewById(R.id.bridge_mac);
            item.bridgeIp = (TextView) convertView.findViewById(R.id.bridge_ip);
            item.bridgeName = (TextView) convertView.findViewById(R.id.bridge_name);

            convertView.setTag(item);
        } else {
            item = (BridgeListItem) convertView.getTag();
        }
        PHAccessPoint accessPoint = accessPoints.get(position);
        namaBridge = accessPoint.getBridgeId();
        Log.d("nama","bridge " +accessPoint.getBridgeId());
        item.bridgeName.setTextColor(Color.BLACK);
        item.bridgeName.setText(accessPoint.getBridgeId());
        item.bridgeIp.setTextColor(Color.BLACK);
        item.bridgeIp.setText(accessPoint.getIpAddress());
        item.bridgeMac.setTextColor(Color.DKGRAY);
        item.bridgeMac.setText(accessPoint.getMacAddress());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return accessPoints.size();
    }

    @Override
    public Object getItem(int position) {
        return accessPoints.get(position);
    }

    public void updateData(List<PHAccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
        notifyDataSetChanged();
    }

}