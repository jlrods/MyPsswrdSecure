package io.github.jlrods.mypsswrdsecure;



import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;

public class LogOutTimer extends CountDownTimer {
    long logOutTimeRemainder;
    private Context context;
    private AlertDialog alertDialog;
    private boolean isLogOutTimeout;
    private static final long INNER_COUNTE_DOWN_INTERVAL = 10000;
    private static CountDownTimer promptIdleTimer = null;
    private static boolean isPromptIdleTimerRunning = false;

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
        this.isLogOutTimeout = false;
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
        this.isLogOutTimeout = true;
        promptIdleTimer = new CountDownTimer(INNER_COUNTE_DOWN_INTERVAL,AutoLogOutService.COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("onTickInner", "Enter/Exit Inner CountDownTimer onTick method for logout in LogOutTime class.");
                if(isPromptIdleTimerRunning == false){
                    isPromptIdleTimerRunning = true;
                }
            }//End of onTick method

            @Override
            public void onFinish() {
                Log.d("onFinish", "Enter Inner CountDownTimer onFinish method for logout in LogOutTime class.");
                //@TODO: Toast to be deleted
                Toast.makeText(getContext(), "Inner Timer is done!", Toast.LENGTH_SHORT).show();
                //Reset flag for inner time running
                isPromptIdleTimerRunning = false;
                //Call logout method to go back to login screen
                MainActivity.logout(getContext());
                Log.d("onFinish", "Exit Inner CountDownTimer onFinish method for logout in LogOutTime class.");
            }//End of onFinish method
        };
        //Start the idle prompt timer
        promptIdleTimer.start();
        //Call method to handle logout timeout event
        this.logOutTimeOut();
        Log.d("onFinish", "Exit CountDownTimer onFinish method for logout in LogOutTime class.");
    }//End of onFinish method

    public long getLogOutTimeRemainder() {
        Log.d("onTick", "Enter/Exit  getLogOutTimeRemainder method for logout in LogOutTime class.");
        return this.logOutTimeRemainder;
    }//End of getLogOutTimeRemainder method

    public void logOutTimeOut(){
        Log.d("logOutTimeOut", "Enter  logOutTimeOut method for logout in LogOutTime class.");

        //Declare and instantiate a new intent object
        Intent i = new Intent(this.context,AutoLogOutDialogActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        this.context.startActivity(i);
        //Set flag for the time out pop up is being shown
        AutoLogOutDialogActivity.setIsTimeOutPopUpShowing(true);
        Log.d("logOutTimeOutSent", "logOutTimeOut intent sent for display from  LogOutTime class.");
        Log.d("logOutTimeOut", "Exit  logOutTimeOut method for logout in LogOutTime class.");
    }//End logOutTimeOut method

    public void  setContext(Context context){
        this.context = context;
    }//End of setContext method

    public Context getContext() {
        return context;
    }

    public void setAlertDialog(AlertDialog alertDialog){
        this.alertDialog = alertDialog;
    }//End of serAlertDialog method

    public AlertDialog getAlertDialog(){return  this.alertDialog;}//End of getAlerDialog method

    public boolean isLogOutTimeout() {
        return isLogOutTimeout;
    }//End of isTimerDone method

    public void setLogOutTimeout(boolean logOutTimeout) {
        isLogOutTimeout = logOutTimeout;
    }//End of setTimerDone method

    public static CountDownTimer getPromptIdleTimer() {
        return promptIdleTimer;
    }

    public static void setPromptIdleTimer(CountDownTimer promptIdleTimer) {
        LogOutTimer.promptIdleTimer = promptIdleTimer;
    }

    public static boolean isPromptIdleTimerRunning() {
        return isPromptIdleTimerRunning;
    }

    public static void setPromptIdleTimerRunning(boolean isPromptIdleTimerRunning) {
        LogOutTimer.isPromptIdleTimerRunning = isPromptIdleTimerRunning;
    }


    //Method to check if MyPsswrdSecure app is on the foreground (important for Auto Logout Alert dialog display)
    public static boolean isAppInForeground() {
        Log.d("isAppInForeground", "Enter  isAppInForeground method for logout in LogOutTimer class.");
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        Log.d("isAppInForeground", "Exit  isAppInForeground method for logout in LogOutTime class.");
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND);
    }//End of isAppInForeground method
}//End of LogoOutTimer class