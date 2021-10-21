package io.github.jlrods.mypsswrdsecure;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.ManagerFactoryParameters;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class DecryptDataService extends Service {

    private static AccountsDB accountsDB;
    private ArrayAdapter<CharSequence> decryptedUserNameArrayAdapter;
    private static List<String> decryptedUserNameList;
    private static List<String> decryptedPsswrdList;
    private static Cryptographer cryptographer;
    private static boolean isDecryptionFinished = false;
    private static HashMap decryptedUserNameIDList;
    private static HashMap decryptedPsswrdIDList;
    private static int LIST_TYPE_USER_NAME = 0;
    private static int LIST_TYPE_PSSWRD = 1;
    @Override
    public void onCreate(){
        Toast.makeText(this, "Decrypt service started", Toast.LENGTH_SHORT).show();
        Log.d("decryptServ", "Enter  onCreate method DecryptDataService class.");
        this.cryptographer = MainActivity.getCryptographer();
        this.accountsDB = HomeFragment.getAccountsDB();
        Cursor encryptedUserNameCursor = this.accountsDB.getUserNameList();
        Cursor encryptedPsswrdCursor = this.accountsDB.getPsswrdList();
        //this.userNameArrayList = new ArrayList<UserName>();
        this.decryptedUserNameList = new ArrayList<String>();
        this.decryptedPsswrdList = new ArrayList<String>();
        this.decryptedUserNameIDList = new HashMap(encryptedUserNameCursor.getCount());
        this.decryptedPsswrdIDList = new HashMap(encryptedPsswrdCursor.getCount());
        if(this.updateList(LIST_TYPE_USER_NAME) && this.updateList(LIST_TYPE_PSSWRD)){
            this.isDecryptionFinished = true;
            Toast.makeText(this, "User name list size = " + decryptedUserNameIDList.size()+"\nUPsswrd list size = "+ decryptedPsswrdIDList.size() , Toast.LENGTH_SHORT).show();
        }
        Log.d("decryptServ", "Exit  onCreate method DecryptDataService class.");
    }//End of onCreate method

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.d("onStartCommand", "Enter  onStartCommand method DecryptService class, Service has started on the background.");
        Toast.makeText(this, "Decrypt service started", Toast.LENGTH_SHORT).show();
        Log.d("onStartCommand", "Enter  onStartCommand method DecryptService class, Service has started on the background.");
        return START_NOT_STICKY;
    }//End of onStartCommand method

    @Override
    public void onDestroy(){
        Toast.makeText(this, "Decrypt service done", Toast.LENGTH_SHORT).show();
        this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static List<String> getDecryptedUserNameList() {
        return decryptedUserNameList;
    }

    public static void setDecryptedUserNameList(List<String> decryptedUserNameList) {
        decryptedUserNameList = decryptedUserNameList;
    }

    public static List<String> getDecryptedPsswrdList() {
        return decryptedPsswrdList;
    }

    public static void setDecryptedPsswrdList(List<String> decryptedPsswrdList) {
        decryptedPsswrdList = decryptedPsswrdList;
    }

    public static boolean isDecryptionFinished() {
        return isDecryptionFinished;
    }

    public static void setDecryptionFinished(boolean decryptionFinished) {
        isDecryptionFinished = decryptionFinished;
    }

    public static int getListTypeUserName() {
        return LIST_TYPE_USER_NAME;
    }

    public static void setListTypeUserName(int listTypeUserName) {
        LIST_TYPE_USER_NAME = listTypeUserName;
    }

    public static int getListTypePsswrd() {
        return LIST_TYPE_PSSWRD;
    }

    public static void setListTypePsswrd(int listTypePsswrd) {
        LIST_TYPE_PSSWRD = listTypePsswrd;
    }

    public static int getSelectedItemID(String value, int listType){
        int _id = -1;
        switch(listType){
        case 0:
            _id = (int) decryptedUserNameIDList.get(value);
            break;
        case 1:
            _id = (int) decryptedPsswrdIDList.get(value);
            break;
        }

        return _id;
    }

    public static boolean updateList(int listType){
        boolean result = false;
        switch(listType){
            case 0:
                Cursor encryptedUserNameCursor = accountsDB.getUserNameList();
                if(decryptedUserNameIDList.size() > 0){
                    decryptedUserNameList.clear();
                    decryptedUserNameIDList.clear();
                }
                decryptedUserNameList.clear();
                while(encryptedUserNameCursor.moveToNext()){
                    UserName user = UserName.extractUserName(encryptedUserNameCursor);
                    //this.userNameArrayList.add(user);
                    String userName = cryptographer.decryptText(user.getValue(),new IvParameterSpec(user.getIv()));
                    decryptedUserNameList.add(userName);
                    decryptedUserNameIDList.put(userName, user.get_id());
                }//End of while loop to decrypt the usernames
                result = true;
                break;
            case 1:
                Cursor encryptedPsswrdCursor = accountsDB.getPsswrdList();
                if(decryptedPsswrdIDList.size() > 0){
                    decryptedPsswrdList.clear();
                    decryptedPsswrdIDList.clear();
                }
                while(encryptedPsswrdCursor.moveToNext()){
                    Psswrd psswrd = Psswrd.extractPsswrd(encryptedPsswrdCursor);
                    //this.userNameArrayList.add(user);
                    String psswrdValue = cryptographer.decryptText(psswrd.getValue(),new IvParameterSpec(psswrd.getIv()));
                    decryptedPsswrdList.add(psswrdValue);
                    decryptedPsswrdIDList.put(psswrdValue, psswrd.get_id());
                }//End of while loop to decrypt passwords
                result = true;
                break;
        }//End of switch statement
        return result;
    }//End of updateList method
}//End of DecryptDataService class
