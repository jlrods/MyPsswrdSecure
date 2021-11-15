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

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

////Service to run user name and password decryption on the background, independently from the UI thread.
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
        Log.d("decryptServ", "Enter  onCreate method DecryptDataService class.");
        //Initialize util objects
        this.cryptographer = MainActivity.getCryptographer();
        this.accountsDB = HomeFragment.getAccountsDB();
        //Get encrypted list of user names and passwords
        Cursor encryptedUserNameCursor = this.accountsDB.getUserNameList();
        Cursor encryptedPsswrdCursor = this.accountsDB.getPsswrdList();
        //Initialize array list to store decrypted list of user names and passwords
        this.decryptedUserNameList = new ArrayList<String>();
        this.decryptedPsswrdList = new ArrayList<String>();
        //Initialize has map to keep decrypted item and ID relatioship
        this.decryptedUserNameIDList = new HashMap(encryptedUserNameCursor.getCount());
        this.decryptedPsswrdIDList = new HashMap(encryptedPsswrdCursor.getCount());
        //Update and populate user name and password decrypted list
        if(this.updateList(LIST_TYPE_USER_NAME) && this.updateList(LIST_TYPE_PSSWRD)){
            this.isDecryptionFinished = true;
        }//End of is statement
        Log.d("decryptServ", "Exit  onCreate method DecryptDataService class.");
    }//End of onCreate method

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        Log.d("onStartCommand", "Enter/Exit  onStartCommand method DecryptService class, Service has started on the background.");
        return START_NOT_STICKY;
    }//End of onStartCommand method

    @Override
    public void onDestroy(){
        Log.d("onDestroyDecryptSer", "Enter  onDestroy method DecryptService class, Service has started on the background.");
        //Clear all lists
        decryptedUserNameList.clear();
        decryptedUserNameIDList.clear();
        decryptedPsswrdList.clear();
        decryptedPsswrdIDList.clear();
        this.stopSelf();
        Log.d("onDestroyDecryptSer", "Exit  onDestroy method DecryptService class, Service has started on the background.");
    }//End of onDestroy method

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



    //Method to get ID from selected item on the respective spinners (User name or password)
    public static int getSelectedItemID(String value, int listType){
        Log.d("getSelectedItemID", "Enter  getSelectedItemID method DecryptService class.");
        int _id = -1;
        switch(listType){
        case 0:
            Log.d("getSelectedItemID", "User name list passed into  getSelectedItemID method DecryptService class.");
            _id = (int) decryptedUserNameIDList.get(value);
            break;
        case 1:
            Log.d("getSelectedItemID", "Psswrd list passed into  getSelectedItemID method DecryptService class.");
            _id = (int) decryptedPsswrdIDList.get(value);
            break;
        }//End of switch statement
        Log.d("getSelectedItemID", "Exit  getSelectedItemID method DecryptService class.");
        return _id;
    }//End of getSelectedItemID method

    //Method to update and populate a spinner list based on the input passed in
    public static boolean updateList(int listType){
        Log.d("updateList", "Enter  updateList method DecryptService class.");
        //Declare and initialize return variable
        boolean result = false;
        //Check the input parameter to select the spinner list to be updated and populated
        switch(listType){
            case 0:
                Log.d("updateList", "User name list passed into  updateList method DecryptService class.");
                //Get the encrypted user name list
                Cursor encryptedUserNameCursor = accountsDB.getUserNameList();
                //If list size is greater than zero, clear.
                if(decryptedUserNameIDList.size() > 0){
                    decryptedUserNameList.clear();
                    decryptedUserNameIDList.clear();
                }
                //Loop through the encrypted list to add decrypted items and ids to respective lists
                while(encryptedUserNameCursor.moveToNext()){
                    //Extract user name from encrypted list
                    UserName user = UserName.extractUserName(encryptedUserNameCursor);
                    //Decrypt the user name value
                    String userName = cryptographer.decryptText(user.getValue(),new IvParameterSpec(user.getIv()));
                    //Add the decrypted user name value to both the array list and the hashmap
                    decryptedUserNameList.add(userName);
                    decryptedUserNameIDList.put(userName, user.get_id());
                }//End of while loop to decrypt the usernames
                //Set method result to true
                result = true;
                break;
            case 1:
                Log.d("updateList", "Psswrd list passed into  updateList method DecryptService class.");
                //Get the encrypted psserd list
                Cursor encryptedPsswrdCursor = accountsDB.getPsswrdList();
                //If list size is greater than zero, clear.
                if(decryptedPsswrdIDList.size() > 0){
                    decryptedPsswrdList.clear();
                    decryptedPsswrdIDList.clear();
                }
                //Loop through the encrypted list to add decrypted items and ids to respective lists
                while(encryptedPsswrdCursor.moveToNext()){
                    //Extract psswrd from encrypted list
                    Psswrd psswrd = Psswrd.extractPsswrd(encryptedPsswrdCursor);
                    //Decrypt the psswrd value
                    String psswrdValue = cryptographer.decryptText(psswrd.getValue(),new IvParameterSpec(psswrd.getIv()));
                    //Add the decrypted psswrd value to add into both the array list and the hashmap
                    decryptedPsswrdList.add(psswrdValue);
                    decryptedPsswrdIDList.put(psswrdValue, psswrd.get_id());
                }//End of while loop to decrypt passwords
                //Set method result to true
                result = true;
                break;
        }//End of switch statement
        Log.d("updateList", "Exit  updateList method DecryptService class.");
        return result;
    }//End of updateList method
}//End of DecryptDataService class
