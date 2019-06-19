package com.example.pravinewa.markii;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    private Switch myswitch;
    private Switch fingerprintswitch;

    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState()==true){
        setTheme(R.style.DarkTheme);
    }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myswitch = (Switch) findViewById(R.id.myswitch);
        if(sharedPref.loadNightModeState()==true){
            myswitch.setChecked(true);
        }
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                    restartApp();
                }else {
                    sharedPref.setNightModeState(false);
                    restartApp();
                }
            }
        });

//        fingerprintswitch = (Switch) findViewById(R.id.myfingerswitch);
//        fingerprintswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            }
//        });
    }

//    public void onBackPressed(){
//        moveTaskToBack();
//    }

    public void restartApp(){
        Intent i = new Intent(getApplicationContext(), Home_page.class);
        startActivity(i);
        finish();
    }
}
