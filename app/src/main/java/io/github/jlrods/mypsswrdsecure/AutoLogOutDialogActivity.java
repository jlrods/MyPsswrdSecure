package io.github.jlrods.mypsswrdsecure;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AutoLogOutDialogActivity extends AppCompatActivity {

    //Attribute definition
    private Button btnLogOut;
    private Button btnContinue;
    private LogOutTimer logOutTimer;
    private static Boolean isTimeOutPopUpShowing = false;
    private static boolean isPopUpToBeDisplayed = false;


    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreateAutoLogOutDiag", "Enter onCreate method in AutoLogOutDialogActivity class.");
        super.onCreate(savedInstanceState);
        //Set language as per preferences
        MainActivity.setAppLanguage(this);
        //Set layout for this activity
        setContentView(R.layout.activity_autologout_dialog);
        //Set activity title
        this.setTitle(R.string.logOutMssg);
        //Disable finish request when touching outside the dialog box.
        this.setFinishOnTouchOutside(false);
        //Get logout time from the AutoLogOut service class
        this.logOutTimer = AutoLogOutService.getLogOutTimer();
        //Initialize continue and logout buttons
        this.btnContinue = (Button) findViewById(R.id.btnContinue);
        this.btnLogOut = (Button) findViewById(R.id.btnLogOut);
        //Set buttons onClick listeners
        this.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(logOutTimer != null){
                    logOutTimer.start();
                    logOutTimer.setLogOutTimeout(false);
                    //Check the inner timer isn't null, then stop it
                    if(logOutTimer.getPromptIdleTimer()!= null){
                        logOutTimer.getPromptIdleTimer().cancel();
                    }
                    finish();
                }
            }//End of onClick method
        });//End of setOnClickListener method of btnContinue button
        //Set buttons onClick listener for the btnLogOut button
        this.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the inner timer isn't null, then stop it
                if(logOutTimer.getPromptIdleTimer()!= null){
                    logOutTimer.getPromptIdleTimer().cancel();
                }
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(logOutTimer.getContext(), true);
                //Call logout method
                MainActivity.logout(logOutTimer.getContext());
            }//End of onClick method
        });//End of setOnClickListener method for btnLogOut button
        Log.d("onCreateAutoLogOutDiag", "Exit onCreate method in AutoLogOutDialogActivity class.");
    }//End of onCreate method of AutoLogOutDialogActivity

    @Override
    public void onResume(){
        super.onResume();
        Log.d("onResAutoLogOut", "Enter onResume method in AutoLogOutDialogActivity class.");
        //Set flag for the time out pop up is not being shown
        this.isTimeOutPopUpShowing = true;
        Log.d("onResAutoLogOut", "Exit onResume method in AutoLogOutDialogActivity class.");
    }//End of onResume method


    @Override
    public void onStop(){
        super.onStop();
        Log.d("onStopAutoLogOut", "Enter onStop method in AutoLogOutDialogActivity class.");
        //Set flag for the time out pop up is not being shown
        this.isTimeOutPopUpShowing = false;
        //Dismiss auto log out time out dialog activity
        finish();
        Log.d("onStopAutoLogOut", "Exit onStop method in AutoLogOutDialogActivity class.");
    }//End of onStop method

    @Override
    public void onBackPressed(){
        Log.d("onBackPressedLogOut", "Enter onBackPressed method in AutoLogOutDialogActivity class.");
        //Get the logout timer from background service
        LogOutTimer logOutTimer = AutoLogOutService.getLogOutTimer();
        //Call logout time out method to display dialog
        logOutTimer.logOutTimeOut();
        super.onBackPressed();
        Log.d("onBackPressedLogOut", "Exit onBackPressed method in AutoLogOutDialogActivity class.");
    }//End of onBackPressed method

    public static Boolean isTimeOutPopUpShowing() {
        return isTimeOutPopUpShowing;
    }

    public static void setIsTimeOutPopUpShowing(Boolean isTimeOutPopUpShowing) {
        AutoLogOutDialogActivity.isTimeOutPopUpShowing = isTimeOutPopUpShowing;
    }

    //Method to make sure popup can be displayed on phone screen if app in foreground
    public static boolean isPopUpToBeDisplayed() {
        Log.d("isPopUpToBeDisplayed", "Enter isPopUpToBeDisplayed method in AutoLogOutDialogActivity class.");
        isPopUpToBeDisplayed = false;
        LogOutTimer logOutTimer = AutoLogOutService.getLogOutTimer();
        if(logOutTimer != null){
            if(logOutTimer.isAppInForeground() && !(((Activity)logOutTimer.getContext()).isFinishing())){
                if(logOutTimer.isLogOutTimeout() && logOutTimer.isPromptIdleTimerRunning() ){
                    isPopUpToBeDisplayed = true;
                }//End of if to check logout time out is due
            }//End of if statement to check app is in foreground and not finishing
        }//End of if statement to check logout time isn't null
        Log.d("isPopUpToBeDisplayed", "Exit isPopUpToBeDisplayed method in AutoLogOutDialogActivity class.");
        return isPopUpToBeDisplayed;
    }//End of isPopUpToBeDisplayed method

    public static void setIsPopUpToBeDisplayed(boolean isPopUpToBeDisplayed) {
        AutoLogOutDialogActivity.isPopUpToBeDisplayed = isPopUpToBeDisplayed;
    }//End of setIsPopUpToBeDisplayed method
}//End of AutoLogOutDialogActivity class
