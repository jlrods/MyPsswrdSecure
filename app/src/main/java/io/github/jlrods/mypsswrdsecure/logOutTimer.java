package io.github.jlrods.mypsswrdsecure;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

public class logOutTimer extends CountDownTimer {
    long logOutTimeRemainder;
    private Context context;
    /**
     * @param logOutTime    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public logOutTimer(long logOutTime, long countDownInterval,Context context) {
        super(logOutTime, countDownInterval);
        this.context = context;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.logOutTimeRemainder = millisUntilFinished;
        Log.d("onTick", "Enter/Exit  CountDownTimer onTick method for logout in MainActivity class.");
    }

    @Override
    public void onFinish() {
        Log.d("onFinish", "Enter/Exit CountDownTimer onFinish method for logout in MainActivity class.");
        logout();
    }

    public long getLogOutTimeRemainder() {
        return this.logOutTimeRemainder;
    }



    private void logout() {
        //Display alert with justification about why permit is necessary
        AlertDialog.Builder alert = MainActivity.displayAlertDialogNoInput(this.context, "Logout", "Log out Timeout!");
        alert.setPositiveButton("Continue",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start();
            }

        }).setNegativeButton("LogOut",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent((Activity)context, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ((Activity)context).startActivity(i);
                //((Activity) context).setResult(MainActivity.getRESULT_TIMEOUT());
                //((Activity) context).finish();
            }

        }).show();
    }

}
