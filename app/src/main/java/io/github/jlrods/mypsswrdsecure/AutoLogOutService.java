package io.github.jlrods.mypsswrdsecure;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AutoLogOutService extends Service {
    private static LogOutTimer logOutTimer;

    @Override
    public void onCreate(){
        long logOutTime = MainActivity.getLogOutTime(this);
        logOutTimer = new LogOutTimer(logOutTime, 250,this);
        logOutTimer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        super.onStartCommand(intent,flags,startId);
        this.logOutTimer.start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        logOutTimer.cancel();
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
    }

    public void teminateService(){
        this.stopSelf();
    }


    public static CountDownTimer getLogOutTimer(){
        return  logOutTimer;
    }

}
