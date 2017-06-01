package com.triplefighter.brightside;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.philips.lighting.hue.listener.PHBridgeConfigurationListener;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.triplefighter.brightside.data.HueSharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private HueSharedPreferences prefs;

    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    ProgressDialog dialog;

    private PHHueSDK sdk;
    private PHBridge bridge;
    private PHBridgeConfiguration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sdk = PHHueSDK.create();
        bridge = sdk.getInstance().getSelectedBridge();
        config = new PHBridgeConfiguration();

        Log.d("nama Main","bridge " +config.getBridgeID());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_lamp) {
            bridge.findNewLights(listener);
            dialog.setMessage("Please Wait");
            dialog.show();
        }else if(id==R.id.wifi){
            startActivity(new Intent(getApplicationContext(),selectBridge.class));
            return true;
        }else if(id == R.id.logOut){
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }else if(id==R.id.add_alarm){
            Intent i=new Intent(this,AlarmDetail.class);
            startActivity(i);
            return true;
        }else if(id == R.id.clean_up){
            dialog.setMessage("Please Wait");
            dialog.show();
            config.setReboot(true);
            bridge.updateBridgeConfigurations(config,configListener);
            dialog.cancel();
        }else if(id == R.id.delete_light){
            startActivity(new Intent(this,DeleteLightActivity.class));
        }
        else if(id == R.id.about){
            startActivity(new Intent(this, About.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    Home home= new Home();
                    return home;
                case 1:
                    AlarmList alarm= new AlarmList();
                    return alarm;
                case 2:
                    Statistic statistic=new Statistic();
                    return statistic;
            }
            return new Home();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Timer";
                case 2:
                    return "Statistic";
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_home, container,false);
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView=inflater.inflate(R.layout.fragment_alarm_list,container,false);
            }else if(getArguments().getInt(ARG_SECTION_NUMBER)==3){
                rootView=inflater.inflate(R.layout.fragment_statistic,container,false);
            }

            return rootView;
        }
    }

    PHLightListener listener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight phLight) {
            String nama = phLight.getName();
            Log.d("coba","nama " +nama);
            Log.d("success","success");
            dialog.cancel();
        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> list) {
            Log.d("success","success");
            dialog.cancel();
        }

        @Override
        public void onSearchComplete() {
            Log.d("success","success");
            dialog.cancel();
        }

        @Override
        public void onSuccess() {
            Log.d("success","success");
            dialog.cancel();
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

        }
    };

    PHBridgeConfigurationListener configListener = new PHBridgeConfigurationListener() {
        @Override
        public void onReceivingConfiguration(PHBridgeConfiguration phBridgeConfiguration) {
            Log.d("config", "updated");
            Log.d("config", "updated" +bridge.getResourceCache().getBridgeConfiguration().isReboot());
        }

        @Override
        public void onSuccess() {
            Log.d("config", "updated");
            Log.d("config", "updated" +bridge.getResourceCache().getBridgeConfiguration().isReboot());
        }

        @Override
        public void onError(int i, String s) {
            Log.d("config", "failed");
        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {
            Log.d("config", "updated");
            Log.d("config", "updated" +bridge.getResourceCache().getBridgeConfiguration().isReboot());
        }
    };

}