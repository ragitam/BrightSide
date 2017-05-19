package com.triplefighter.brightside;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.triplefighter.brightside.Model.DataStatistic;
import com.triplefighter.brightside.Model.UserInformation;
import com.triplefighter.brightside.data.AccessPointListAdapter;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.triplefighter.brightside.data.LampuListAdapter.arr_hour;
import static com.triplefighter.brightside.data.LampuListAdapter.arr_intentsity;

public class Statistic extends Fragment {
    private TextView usageText, costText, statusText;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    int position = 0;
    public static int lampuNyala = 0, hour_total=0, j;
    public float usage_total=0,usage_cost=0;
    double stats_usage=0;
    String status;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    android.os.Handler customHandler = new android.os.Handler();
    android.os.Handler showStats= new android.os.Handler();

    public Statistic() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_statistic, container, false);

        Typeface mTypeFace= Typeface.createFromAsset(getActivity().getAssets(),"Nexa Light.otf");

        int totalLampu = LampuListAdapter.total_lampu;
        lampuNyala = LampuListAdapter.lampu_nyala;

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        TextView total = (TextView) v.findViewById(R.id.lamps_total);
        total.setText(String.valueOf(totalLampu));

        TextView nyala = (TextView) v.findViewById(R.id.lamps_on);
        nyala.setText(String.valueOf(lampuNyala));

        usageText = (TextView)v.findViewById(R.id.usage_kwh);
        costText = (TextView)v.findViewById(R.id.usage_cost);
        statusText = (TextView)v.findViewById(R.id.usage_status);

        usageText.setTypeface(mTypeFace);
        costText.setTypeface(mTypeFace);
        nyala.setTypeface(mTypeFace);
        total.setTypeface(mTypeFace);
        statusText.setTypeface(mTypeFace);

        usageText.setText(preferences.getString("usage",""));
        costText.setText("Rp " +preferences.getString("cost",""));
        statusText.setText(preferences.getString("status",""));

        customHandler.postDelayed(updateTimerThread, 0);
        showStats.postDelayed(show,0);

        return v;
    }

    public void usageAndCost() {
        for (int i = 0; i< arr_intentsity.length; i++){
            usage_total=usage_total+arr_intentsity[i];
            usage_cost= (float) (usage_cost+(arr_intentsity[i]*1467.28));
        }

        Log.d("statistik","harga : " +usage_cost);
    }

    public void status(){
        hour_total=0;
        for(j=0;j<arr_hour.length;j++){
            hour_total=hour_total+arr_hour[j];
        }
        stats_usage=(double) usage_total*hour_total*30/2;
        if(stats_usage<2.5){
            status = "Hemat";
        }else if(stats_usage>=2.5 && stats_usage<=5){
            status = "Normal";
        }else if(stats_usage>5){
            status = "Boros";
        }
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run() {
            usageAndCost();
            status();

            final int biaya = (int) usage_cost;
            editor = preferences.edit();
//            editor.putString("usage", String.valueOf(usage_total));
//            editor.putString("cost", String.valueOf(biaya));
//            editor.putString("status",status);
//            editor.apply();

//            FirebaseUser user = mAuth.getCurrentUser();
//            final String userId = user.getUid();
//            mDatabase.child("Data User").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    UserInformation user = dataSnapshot.getValue(UserInformation.class);
//
//                    if (user == null) {
//                        Toast.makeText(getContext(), getText(R.string.user_not_found), Toast.LENGTH_SHORT).show();
//                    } else {
//                        storeData(usage_total,biaya);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            customHandler.postDelayed(this, 1000);
        }
    };

    private Runnable show = new Runnable() {
        public void run() {
            int biaya = (int) usage_cost;
            usageText.setText(preferences.getString("usage",""));
            costText.setText("Rp " +preferences.getString("cost",""));
            statusText.setText(preferences.getString("status",""));
            showStats.postDelayed(this, 1000);
        }
    };

    public void storeData(float usage_total, int biaya){
        String namaBridge = AccessPointListAdapter.namaBridge;
        String key = mDatabase.child("stats").child(namaBridge).push().getKey();
        DataStatistic post = new DataStatistic(usage_total,biaya);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/stats/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
