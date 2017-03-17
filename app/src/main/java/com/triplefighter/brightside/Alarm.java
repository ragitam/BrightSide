package com.triplefighter.brightside;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class Alarm extends Fragment {

    String[] mode_list=new String[]{"None", "Economic","Sleep Mode"};
    Spinner lamp_name;
    Switch repeat_alarm;
    //ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,mode_list);
    public Alarm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_alarm_detail,container,false);
        lamp_name=(Spinner) view.findViewById(R.id.lamp_name);
        repeat_alarm=(Switch) view.findViewById(R.id.repeat_alarm);
        //mode.setAdapter(adapter);
        return inflater.inflate(R.layout.fragment_alarm_detail, container, false);
    }

}
