package com.example.pravinewa.markii;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback{
    private Context context;


    public FingerprintHandler(Context context){
        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject,cancellationSignal,0,this,null);
    }

    public void onAuthenticationError(int errorCode, CharSequence errString){

        this.update("There was an auth error"+errString,false);
    }

    public void onAuthenticationFailed(){
        this.update("Authentication failed",false);
    }

    public void onAuthenticationHelp(int helpCode, CharSequence helpString){
        this.update(" "+helpString,false);
    }

    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result){
        this.update("You can access the app",true);
    }
    private void update(String s, boolean b) {

        //TextView paraLabel = (TextView)((Activity)context).findViewById(R.id.paraLabel);
        ImageView imageView = (ImageView)((Activity)context).findViewById(R.id.fingerprintImage);
        //paraLabel.setText(s);

        if(b==false){
            //paraLabel.setTextColor(ContextCompat.getColor(context,R.color.colorError));
            imageView.setImageResource(R.drawable.ic_action_red_fingerprint);
        }else{
            //paraLabel.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
            imageView.setImageResource(R.drawable.ic_action_green_fingerprint);
                this.context.startActivity(new Intent(this.context, Home_page.class));
        }
    }
}
