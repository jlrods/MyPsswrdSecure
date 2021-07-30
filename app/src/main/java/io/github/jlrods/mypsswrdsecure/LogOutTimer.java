package io.github.jlrods.mypsswrdsecure;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class LogOutTimer extends CountDownTimer {
    long logOutTimeRemainder;
    private Context context;
    private AlertDialog alertDialog;
    private boolean isTimerDone = false;
    private static final long INNER_COUNTE_DOWN_INTERVAL = 10000;
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
        //Check if app is on the foreground, then display alert dialog if so
        if(this.isAppInForeground()){
            //Display alert with justification about why permit is necessary
            final CountDownTimer promptIdleTimer = new CountDownTimer(INNER_COUNTE_DOWN_INTERVAL,AutoLogOutService.COUNT_DOWN_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("onTick", "Enter/Exit Inner CountDownTimer onTick method for logout in LogOutTime class.");
                }//End of onTick method

                @Override
                public void onFinish() {
                    Log.d("onFinish", "Enter Inner CountDownTimer onFinish method for logout in LogOutTime class.");
                    Toast.makeText(context, "Inner Timer is done!", Toast.LENGTH_SHORT).show();
                    if( alertDialog != null){
                        alertDialog.dismiss();
                        alertDialog = null;
                    }
                    MainActivity.logout(context);
                    Log.d("onFinish", "Exit Inner CountDownTimer onFinish method for logout in LogOutTime class.");
                }//End of onFinish method
            };
            //Declare and initialize alert builder object for Auto logout prompt
            final AlertDialog.Builder alert = MainActivity.displayAlertDialogNoInput(this.context, context.getString(R.string.logOutTitle), context.getString(R.string.logOutMssg));
            alert.setPositiveButton(R.string.logOutContinue,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("onClickLogOutPromt", "Enter Possitive button onClick method for logout in LogOutTime class.");
                    //Start logout timer
                    start();
                    //Reset boolean flag for timer done
                    isTimerDone = false;
                    //Stop timer for idle auto logout prompt
                    promptIdleTimer.cancel();
                    Log.d("onClickLogOutPromt", "Exit Positive button onClick method for logout in LogOutTime class.");
                }//End of onClick method

            }).setNegativeButton(R.string.logOutTitle,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout(promptIdleTimer);
                    Log.d("onClickLogOutPromt", "Enter/Exit Negative button onClick method for logout in LogOutTime class.");
                }//End of onClick method

            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    logout(promptIdleTimer);
                    Log.d("onClickLogOutPromt", "Enter/Exit onCancel method for logout in LogOutTime class.");
                }//End of onCancel method
            });
            //Create the dialog
            alertDialog = alert.create();
            //Ignore touch outside the prompt alert box
            alertDialog.setCanceledOnTouchOutside(false);
            //Display the auto logout timeout prompt
            alertDialog.show();
            //Start inner count down timer for auto logout on idle response to prompt
            promptIdleTimer.start();
            //Create a inner timer to carry out auto logout if window expires
        }else{
            //Call main activity logout method to kill service and call LoginActivity
            MainActivity.logout(context);
        }//End of if else statement to check app is on the foreground
        //Otherwise, stop service
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

    //Method to check if MyPsswrdSecure app is on the foreground (important for Auto Logout Alert dialog display)
    public static boolean isAppInForeground() {
        Log.d("isAppInForeground", "Enter  isAppInForeground method for logout in LogOutTimer class.");
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        Log.d("isAppInForeground", "Exit  isAppInForeground method for logout in LogOutTime class.");
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND);
    }//End of isAppInForeground method

    //Method that carries out steps for proper logout
    private void logout(CountDownTimer promptIdleTimer){
        Log.d("logoutTimer", "Enter  logout method for logout in LogOutTimer class.");
        //Dismiss current alert dialog window
        if( alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
        //Stop timer for idle auto logout prompt
        promptIdleTimer.cancel();
        //Call method to check for notification sent and update if required
        MainActivity.checkForNotificationSent(context,true);
        //Call logout method
        MainActivity.logout(context);
        Log.d("logoutTimer", "Exit  logout method for logout in LogOutTimer class.");
    }//End of logout method
}//End of LogoOutTimer class