package com.example.mavri.brightside;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class Alarm extends Fragment {

    String[] mode_list=new String[]{"None", "Economic","Sleep Mode"};
    Spinner mode,repeat_mode;
    //ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,mode_list);
    public Alarm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_alarm_detail,container,false);
        mode=(Spinner) view.findViewById(R.id.mode);
        repeat_mode=(Spinner) view.findViewById(R.id.repeat);
        //mode.setAdapter(adapter);
        return inflater.inflate(R.layout.fragment_alarm_detail, container, false);
    }

}
