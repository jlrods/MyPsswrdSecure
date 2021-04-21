package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;

public class LogOutTimer extends CountDownTimer {
    long logOutTimeRemainder;
    private Context context;
    /**
     * @param logOutTime    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public LogOutTimer(long logOutTime, long countDownInterval, Context context) {
        super(logOutTime, countDownInterval);
        this.context = context;
    }//End of Contructor method

    @Override
    public void onTick(long millisUntilFinished) {
        this.logOutTimeRemainder = millisUntilFinished;
        Log.d("onTick", "Enter/Exit  CountDownTimer onTick method for logout in MainActivity class.");
    }//End of onTick method

    @Override
    public void onFinish() {
        Log.d("onFinish", "Enter/Exit CountDownTimer onFinish method for logout in MainActivity class.");
        logout();
    }//End of onFinish method

    public long getLogOutTimeRemainder() {
        Log.d("onTick", "Enter/Exit  getLogOutTimeRemainder method for logout in MainActivity class.");
        return this.logOutTimeRemainder;
    }//End of getLogOutTimeRemainder method


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
                MainActivity.logout(context);
            }

        }).show();
    }//End of logout method
}//End of LogoOutTimer class