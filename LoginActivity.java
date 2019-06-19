package com.example.pravinewa.markii;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ImageView mFingerprintImage;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    SharedPref sharedPref;
    private SharedPreferences sharedPreferences;

    private Button login_button;
    private TextView signup_button;
    private CheckBox rememberme;
    private Switch fingerswitch;

    private EditText userEmailLogin;
    private EditText userPasswordLogin;
    private static final String PREFS_NAME = "Prefsfile";

    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private boolean networkstatus;

    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();

        sharedPref = new SharedPref(this);
        if(sharedPref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        if(Build.VERSION.SDK_INT>=21){
            window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!isNetworkConnected(LoginActivity.this)){
            networkstatus = false;
            buildDialog(LoginActivity.this).show();

        }else{
            networkstatus = true;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), Home_page.class));
        }

        sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        userEmailLogin = (EditText) findViewById(R.id.userEmailLogin);
        userPasswordLogin = (EditText) findViewById(R.id.userPasswordLogin);
        rememberme = (CheckBox) findViewById(R.id.remembermecBox);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        fingerswitch = (Switch) findViewById(R.id.myfingerswitch);

        mFingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//
//            if (!fingerprintManager.isHardwareDetected()) {
//    //            mParaLabel.setText("Fingerprint Scanner not detected in this device");
//                    //fingerswitch.setEnabled(false);
//                    mFingerprintImage.setVisibility(View.INVISIBLE);
//
//            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//      //          mParaLabel.setText("Permission not granted");
//                    //fingerswitch.setEnabled(false);
//            } else if (!keyguardManager.isKeyguardSecure()) {
//        //        mParaLabel.setText("Add Lock to use fingerprint");
//                    //fingerswitch.setEnabled(false);
//            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
//          //      mParaLabel.setText("You should add one fingerprint");
//                    //fingerswitch.setEnabled(false);
//            } else {
//                    //fingerswitch.setEnabled(true);
//                    mFingerprintImage.setVisibility(View.VISIBLE);
//                //if(fingerswitch.isChecked()) {
//                    //    mParaLabel.setText("Place your finger in scanner");
//                    FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
//                    fingerprintHandler.startAuth(fingerprintManager, null);
//                //}
//            }
//
//        }

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkstatus == true) {
                    userLogin();
                }else{
                    buildDialog(LoginActivity.this).show();
                }
            }
        });

        signup_button = (TextView) findViewById(R.id.signups_button);
        signup_button.setPaintFlags(signup_button.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkstatus == true) {
                    openSignupActivity();
                }else{
                    buildDialog(LoginActivity.this).show();
                }
            }
        });

        getPreferenceData();

    }

    private void getPreferenceData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(sp.contains("pref_email")){
            String u = sp.getString("pref_email","not found");
            userEmailLogin.setText(u.toString());
        }
        if(sp.contains("pref_password")){
            String p = sp.getString("pref_password","not found");
            userPasswordLogin.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            boolean b = sp.getBoolean("pref_check",false);
            rememberme.setChecked(b);
        }
    }

    public void userLogin(){
            String email = userEmailLogin.getText().toString().trim();
            String password = userPasswordLogin.getText().toString().trim();

        if(email.isEmpty()){
            userEmailLogin.setError("Email Required");
            userEmailLogin.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmailLogin.setError("Enter valid email address");
            userEmailLogin.requestFocus();
            return;
        }

        if(password.isEmpty()){
            userPasswordLogin.setError("Password Required");
            userPasswordLogin.requestFocus();
            return;
        }


        if(password.length() <6){
            userPasswordLogin.setError("Password should more than 6 digit");
            userPasswordLogin.requestFocus();
            return;
        }

        if(rememberme.isChecked()){
            boolean boolischecked = rememberme.isChecked();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pref_email",userEmailLogin.getText().toString());
            editor.putString("pref_password",userPasswordLogin.getText().toString());
            editor.putBoolean("pref_check",boolischecked);
            editor.apply();

        }else {
            sharedPreferences.edit().clear().apply();
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), Home_page.class));
//                            userEmailLogin.getText().clear();
//                            userPasswordLogin.getText().clear();
                        }else{
                            Toast.makeText(LoginActivity.this,"Email and Password incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(firebaseAuth.getCurrentUser() != null){
//            finish();
//            startActivity(new Intent(this, Home_page.class));
//        }
//    }


    public void openSignupActivity(){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);

    }

    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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