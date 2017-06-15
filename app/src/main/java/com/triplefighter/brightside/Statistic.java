package com.triplefighter.brightside;

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
import com.triplefighter.brightside.Model.DataStatistic;
import com.triplefighter.brightside.Model.UserInformation;
import com.triplefighter.brightside.data.HueSharedPreferences;
import com.triplefighter.brightside.data.LampuListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.triplefighter.brightside.data.LampuListAdapter.arr_intentsity;

public class Statistic extends Fragment {
    private TextView usageText, costText, statusText,month,usage_last_month,usage_predict;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference statsReference;
    private ValueEventListener statsListener;
    private HueSharedPreferences prefs;

    static String key;
    String namaBridge;
    int biaya = 0;

    int position = 0;
    public static int lampuNyala = 0, hour_total=0, j;
    public float usage_total=0,usage_cost=0, stats_usage=0;
    double temp_stats=0;
    String status;

    DateFormat dateFormat=new SimpleDateFormat("MMMM");
    Date date=new Date();

    String bulan;

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
        prefs = HueSharedPreferences.getInstance(getContext());

        //namaBridge = AccessPointListAdapter.namaBridge;

        namaBridge = Home.namaBridge;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        key = "myBridgeStats";

        TextView total = (TextView) v.findViewById(R.id.lamps_total);
        total.setText(String.valueOf(totalLampu));

        TextView nyala = (TextView) v.findViewById(R.id.lamps_on);
        nyala.setText(String.valueOf(lampuNyala));

        month = (TextView)v.findViewById(R.id.month);
        usageText = (TextView)v.findViewById(R.id.usage_kwh);
        costText = (TextView)v.findViewById(R.id.usage_cost);
        statusText = (TextView)v.findViewById(R.id.usage_status);
        usage_last_month =(TextView)v.findViewById(R.id.usage_last_month);
        usage_predict =(TextView)v.findViewById(R.id.usage_predict);

        month.setTypeface(mTypeFace);
        usageText.setTypeface(mTypeFace);
        costText.setTypeface(mTypeFace);
        nyala.setTypeface(mTypeFace);
        total.setTypeface(mTypeFace);
        statusText.setTypeface(mTypeFace);
        usage_last_month.setTypeface(mTypeFace);
        usage_predict.setTypeface(mTypeFace);

        biaya = (int) usage_cost;

        month.setText(toString().valueOf(dateFormat.format(date)));
        bulan = dateFormat.format(date);

        usageText.setText(String.valueOf(usage_total));
        costText.setText(String.valueOf(biaya));

        customHandler.postDelayed(updateTimerThread, 0);
        showStats.postDelayed(show,0);

        return v;
    }

    //Menghitung usage dalam bentuk kwh dari intensitas yg udah diatur dan di convert jadi rupiah
    public void usageAndCost() {
        if(lampuNyala>0){
            for (int i=0;i<arr_intentsity.length;i++){
                usage_total=usage_total+arr_intentsity[i];
                usage_cost= (float) (usage_cost+(arr_intentsity[i]*1467.28));
                Calendar cal = Calendar.getInstance();
                int a = cal.get(Calendar.HOUR_OF_DAY);
                if(a == 24){
                    stats_usage = 0;
                }else {
                    stats_usage=stats_usage+arr_intentsity[i];
                }
            }
            // tambahin kodingan buat ngirim data ke firebase
        }
    }

    //Menentukan status pemakaian
    public void status(){
        //stats_usage=(double) usage_total*hour_total*30/2;
        //temp_stats=usage_total/lampuNyala;
        if(temp_stats<0.04){
            statusText.setText(getText(R.string.eco_status));
        }else if(temp_stats>=0.04 && temp_stats<=0.084){
            statusText.setText(getText(R.string.normal_status));
        }else if(temp_stats>0.084){
            statusText.setText(getText(R.string.boros_status));
        }
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run() {
            FirebaseUser user = mAuth.getCurrentUser();
            final String userId = user.getUid();
            mDatabase.child("Data User").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                //Input data ke database
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInformation user = dataSnapshot.getValue(UserInformation.class);

                    //cek apakah user sudah terdaftar atau belum
                    if (user == null) {
                        Toast.makeText(getContext(), "username not found", Toast.LENGTH_SHORT).show();
                    } else {
                        //Memasukkan data statistik ke dalam database
                        FirebaseDatabase.getInstance().getReference().child("stats").child(namaBridge).child(key).child(bulan)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DataStatistic a = dataSnapshot.getValue(DataStatistic.class);

                                        Log.d("dataSnapshot","isi " +dataSnapshot);

                                        if(a == null){
                                            storeData(usage_total,usage_cost,stats_usage);
                                        }else {
                                            //showData();
                                            usageAndCost();
                                            status();
                                            storeData(usage_total,usage_cost,stats_usage);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            customHandler.postDelayed(this, 1000);
        }
    };

    private Runnable show = new Runnable() {
        public void run() {
            showData();
            showStats.postDelayed(this, 1000);
        }
    };

    //Menyimpan data ke dalam database
    public void storeData(float usage_total, float biaya, float stats_usage){
        DataStatistic post = new DataStatistic(usage_total,biaya,stats_usage);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/stats/" +namaBridge +"/" + key +"/" +bulan, postValues);

        mDatabase.updateChildren(childUpdates);
        Log.d("dataSnapshot","upload");
    }

    //Menampilkan data statistik yg telah tersimpan di database
    public void showData(){
        statsReference = FirebaseDatabase.getInstance().getReference().child("stats").child(namaBridge).child(key).child(bulan);

        statsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataStatistic post = dataSnapshot.getValue(DataStatistic.class);
                // [START_EXCLUDE]

                if(post == null){
                    usageText.setText("0");
                    costText.setText("0");
                }else{

                    usage_total = post.getUsage();
                    usage_cost = post.getCost();
                    stats_usage = post.getUsageStat();

                    Log.d("stats","onDataChange " +usage_total);
                    Log.d("stats","onDataChange " +usage_cost);
                    Log.d("stats","onDataChange " +stats_usage);

                    biaya = (int) post.cost;
                    String b = String.valueOf(biaya);
                    usageText.setText(String.format("%.4f", post.getUsage()));
                    costText.setText(b);

                    Log.d("dataSnapshot","retrieve");

                    status();
                    Log.d("stats","tempData " +temp_stats);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load post.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        showData();
    }

    @Override
    public void onResume() {
        super.onResume();
        showData();
    }
}
