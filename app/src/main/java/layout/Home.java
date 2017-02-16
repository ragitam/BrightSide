package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.mavri.brightside.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    ToggleButton power_but;
    SeekBar brightness;
    TextView brightness_num;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_home,container,false);
        brightness=(SeekBar) v.findViewById(R.id.brightness);
        brightness_num = (TextView) v.findViewById(R.id.brightness_num);
        brightness.setProgress(0);
        brightness.setMax(100);
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b==true){
                    brightness_num.setText(String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return v;
    }
}
