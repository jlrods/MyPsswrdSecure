package io.github.jlrods.mypsswrdsecure;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;


//Class that extends the service class and is used to run a logout timer
public class AutoLogOutService extends Service {
    //LogOutTimer object, which counts down time to prompt user about login out of app or continue working.
    private static LogOutTimer logOutTimer;
    public static int COUNT_DOWN_INTERVAL = 250;

    @Override
    public void onCreate(){
        Log.d("timeOut", "Enter  onCreate method AutoLogOutService class.");
        long logOutTime = MainActivity.getLogOutTime(this);
        this.logOutTimer = new LogOutTimer(logOutTime, COUNT_DOWN_INTERVAL,this);
        Log.d("timeOut", "Exit  onCreate method AutoLogOutService class.");
    }//End of onCreate method

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.d("onStartCommand", "Enter  onStartCommand method AutoLogOutService class, Service has started on the background.");
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        //Start timeout timer
        this.logOutTimer.start();
        Log.d("onStartCommand", "Enter  onStartCommand method AutoLogOutService class, Service has started on the background.");
        return START_NOT_STICKY;
    }//End of onStartCommand method

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//End of onBind method

    @Override
    public void onDestroy(){
        Log.d("onDestroyServ", "Enter  onDestroy method in AutoLogOutService class, Service is done on the background.");
        //Stop the logout timer
        logOutTimer.cancel();
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
        Log.d("onDestroyServ", "Exit  onDestroy method in AutoLogOutService class, Service is done on the background.");
    }//End of onDestroy method

    public void terminateService(){
        this.stopSelf();
    } //End of terminateService method


    public static CountDownTimer getLogOutTimer(){
        return  logOutTimer;
    }//End of getLogOutTimer method
}//End of AutoLogoutService
