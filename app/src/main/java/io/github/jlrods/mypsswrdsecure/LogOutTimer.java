package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class LogOutTimer extends CountDownTimer {
    long logOutTimeRemainder;
    private Context context;
    private AlertDialog alertDialog;
    private boolean isTimerDone = false;
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
        this.isTimerDone = false;
        Log.d("LogOutTimer", "Enter/Exit  LogOutTimer constructor method  in LogOutTime class.");
    }//End of Constructor method

    public LogOutTimer(long logOutTime, long countDownInterval) {
        this(logOutTime,countDownInterval,null);
        Log.d("LogOutTimer2", "Enter/Exit  LogOutTimer constructor 2 method  in LogOutTime class.");
    }//End of Constructor method

    @Override
    public void onTick(long millisUntilFinished) {
        this.logOutTimeRemainder = millisUntilFinished;
        Log.d("onTick", "Enter/Exit  CountDownTimer onTick method for logout in LogOutTime class.");
    }//End of onTick method

    @Override
    public void onFinish() {
        Log.d("onFinish", "Enter CountDownTimer onFinish method for logout in LogOutTime class.");
        Toast.makeText(context, "Logout Timer is done!", Toast.LENGTH_SHORT).show();
        this.isTimerDone = true;
        //Call method to handle logout timeout event
        this.timeOut();
        Log.d("onFinish", "Exit CountDownTimer onFinish method for logout in LogOutTime class.");
    }//End of onFinish method

    public long getLogOutTimeRemainder() {
        Log.d("onTick", "Enter/Exit  getLogOutTimeRemainder method for logout in LogOutTime class.");
        return this.logOutTimeRemainder;
    }//End of getLogOutTimeRemainder method

    //Method to handle logout timeout event
    private void timeOut() {
        Log.d("timeOut", "Enter  timeOut method for logout in LogOutTime class.");
        //Display alert with justification about why permit is necessary
        final AlertDialog.Builder alert = MainActivity.displayAlertDialogNoInput(this.context, context.getString(R.string.logOutTitle), context.getString(R.string.logOutMssg));
        alert.setPositiveButton(R.string.logOutContinue,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start();
                isTimerDone = false;
            }

        }).setNegativeButton(R.string.logOutTitle,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss current alert dialog window
                if( alertDialog != null){
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                MainActivity.logout(context);
            }//End of onClick method

        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Dismiss current alert dialog window
                if( alertDialog != null){
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                MainActivity.logout(context);
            }//End of onCancel method
        });
        alertDialog = alert.create();
        alertDialog.show();
        Log.d("timeOut", "Exit  timeOut method for logout in LogOutTime class.");
    }//End of logout method

    public void  setContext(Context context){
        this.context = context;
    }//End of setContext method

    public void setAlertDialog(AlertDialog alertDialog){
        this.alertDialog = alertDialog;
    }//End of serAlertDialog method

    public AlertDialog getAlertDialog(){return  this.alertDialog;}//End of getAlerDialog method

    public boolean isTimerDone() {
        return isTimerDone;
    }//End of isTimerDone method

    public void setTimerDone(boolean timerDone) {
        isTimerDone = timerDone;
    }//End of setTimerDone method
}//End of LogoOutTimer class