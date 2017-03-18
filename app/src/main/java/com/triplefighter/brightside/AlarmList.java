package com.triplefighter.brightside;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmList extends Fragment {
    ListView listView;
    TextView lamp_name,alarm_time;
    ImageView repeat_icon;
    public AlarmList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_alarm_list,container,false);
        listView=(ListView) view.findViewById(R.id.list_lamp);
        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

}
