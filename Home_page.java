package com.example.pravinewa.markii;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Home_page extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;
    String switch_view = "Collapsed";
    private Button settingButton;
    private long backPressedTime;
    private Toast backToast;
    private Button log_out;

    SharedPref sharedPref;

    private FirebaseAuth firebaseAuth;

    private Button scan_btn;
    private TextView username;
    private Button profileButton;


    Vibrator vibrator;
    private boolean networkstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        if(!isNetworkConnected(Home_page.this)){
            networkstatus = false;
            buildDialog(Home_page.this).show();
        }else {
            networkstatus = true;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        vibrator =(Vibrator) getSystemService(VIBRATOR_SERVICE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        settingButton = (Button) findViewById(R.id.setting_buttons);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingActivity();
            }
        });

        scan_btn = (Button) findViewById(R.id.scann_buttons);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkstatus == true) {
                    openQrCodeActivity();
                }else {
                    buildDialog(Home_page.this).show();
                }
            }
        });

        profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        log_out = (Button) findViewById(R.id.logs_button);
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogOutActivity();
            }
        });
        changeFragment(new Home_frag());


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(firebaseAuth.getCurrentUser() == null){
//            finish();
//            startActivity(new Intent(this, LoginActivity.class));
//        }
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch(menuItem.getItemId()){
                case R.id.menu_button:
                    vibrator.vibrate(50);
                    if(switch_view=="Collapsed") {
                            vibrator.vibrate(50);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            switch_view="Expanded";
                    }else{
                            vibrator.vibrate(50);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            switch_view="Collapsed";
                    }
                    break;
                case R.id.home_button:
                    changeFragment(new Home_frag());
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    switch_view="Collapsed";
                    break;
                case R.id.notification:
                    changeFragment(new Notification_frag());
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    switch_view="Collapsed";
                    break;

            }
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    private void changeFragment(Fragment targetFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


    @Override
    public void onBackPressed(){


        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            finishAffinity();
            super.onBackPressed();
            System.exit(1);

        }else{
            backToast = Toast.makeText(getBaseContext(),"Press back again to Exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
    public void openSettingActivity(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        switch_view="Collapsed";
    }

    public void openQrCodeActivity() {
        Intent intent = new Intent(this, QrcodeActivity.class);
        startActivity(intent);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        switch_view="Collapsed";
    }

    public void openProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        switch_view="Collapsed";
    }

    public void openLogOutActivity(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        switch_view="Collapsed";
    }

    public boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile!=null && mobile.isConnectedOrConnecting()) || (wifi!=null && wifi.isConnectedOrConnecting())) return true;
            else return false;

        }else {
            return false;
        }

    }

    public AlertDialog.Builder buildDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile data or Wifi to Access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;

    }


}
