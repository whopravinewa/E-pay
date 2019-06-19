package com.example.pravinewa.markii;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {


    private EditText userFullName;
    private EditText userEmailId;
    private EditText userDOB;
    private EditText userAddress;
    private EditText userMobileNumber;
    private EditText userPassword;
    private EditText userConPassword;
    private Button register_btn;
    private CheckBox agreeCheckbox;
    private TextView loginsignup_button;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceUsers;

    Window window;
    SharedPref sharedPref;
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
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        userFullName = (EditText) findViewById(R.id.userFullName);
        userEmailId = (EditText) findViewById(R.id.userEmailId);
        userMobileNumber = (EditText) findViewById(R.id.userMobileNumber);
        userAddress = (EditText) findViewById(R.id.userAddress);
        userDOB = (EditText) findViewById(R.id.userDOB);
        userPassword = (EditText) findViewById(R.id.userPassword);
        userConPassword = (EditText) findViewById(R.id.userConPassword);
        agreeCheckbox = (CheckBox) findViewById(R.id.agreeCheckbox);
        loginsignup_button = (TextView) findViewById(R.id.loginsignup_button);
        loginsignup_button.setPaintFlags(loginsignup_button.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        loginsignup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        register_btn = (Button) findViewById(R.id.register_button);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    public void registerUser(){

        final String fullname = userFullName.getText().toString().trim();
        final String email = userEmailId.getText().toString().trim();
        final String number = userMobileNumber.getText().toString().trim();
        final String address = userAddress.getText().toString().trim();
        //final String Dob = userDOB.getText().toString().trim();
        String Password = userPassword.getText().toString().trim();
        String cPassword = userConPassword.getText().toString().trim();

        if(fullname.isEmpty()){
            userFullName.setError("Name Required");
            userFullName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            userEmailId.setError("Email Required");
            userEmailId.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmailId.setError("Enter valid email address");
            userEmailId.requestFocus();
            return;
        }

        if(number.isEmpty()){
            userMobileNumber.setError("Number Required");
            userMobileNumber.requestFocus();
            return;
        }

        if(number.length() != 10){
            userMobileNumber.setError("Enter valid mobile number");
            userMobileNumber.requestFocus();
            return;
        }

        if(address.isEmpty()){
            userAddress.setError("Address Required");
            userAddress.requestFocus();
            return;
        }

//        if(Dob.isEmpty()){
//            userDOB.setError("Dob Required");
//            userDOB.requestFocus();
//            return;
//        }

        if(Password.isEmpty()){
            userPassword.setError("Password Required");
            userPassword.requestFocus();
            return;
        }

        if(cPassword.isEmpty()){
            userConPassword.setError("Confirmation Password Required");
            userConPassword.requestFocus();
            return;
        }

//        if(Password != cPassword){
//
//            userPassword.setError("Password didn't Match");
//            userConPassword.setError("Password didn't Match");
//            userPassword.requestFocus();
//            userConPassword.requestFocus();
//            return;
//        }

        if(Password.length() <6){
            userPassword.setError("Password should more than 6 digit");
            userPassword.requestFocus();
            return;
        }

        if(agreeCheckbox.isChecked()==false){
            agreeCheckbox.setError("Clcik to agree condition");
            agreeCheckbox.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    User user = new User(
                            fullname,
                            email,
                            number,
                            address
                    );
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, "Registered Succesfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                            else{
                                Toast.makeText(SignupActivity.this, "Registered Failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else{
                    Toast.makeText(SignupActivity.this, "Registered Fail", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
