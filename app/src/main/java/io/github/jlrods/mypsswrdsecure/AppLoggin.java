package io.github.jlrods.mypsswrdsecure;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;
public class AppLoggin extends Loggin {

    //Attribute expansion definition
    String message; //Message to be display on the drawer nav menu
    Icon picture; //Picture location of image to be displayed on the drawer nav menu

    //Constructors
    //Default Constructor
    public AppLoggin(){
        super();
        Log.d("AppLogginDef_Ent","Enter AppLoggin Default Constructor");
        this.message = "";
        this.picture = null;
        Log.d("AppLoggin_Def_Ext","Exit AppLoggin Default Constructor");
    }//End of AppLoggin constructor

    //Constructor with no password given
    public AppLoggin(UserName userName){
        Log.d("AppLoggin_NoPass_Ent","Enter AppLoggin Constructor 2");
        this.userName = userName;
        this.psswrd = null;
        this.message ="";
        this.picture = null;
        Log.d("AppLoggin_NoPass_Ext","Exit AppLoggin Constructor 2");
    }//End of AppLoggin constructor

    //Full Constructor
    public AppLoggin(int _id, String name, UserName userName, Psswrd psswrd,String message, Icon picture){
        super(_id, name, userName, psswrd);
        Log.d("AppLog_Full_Ent","Enter AppLoggin Full Constructor");
        this.message = message;
        this.picture = picture;
        Log.d("AppLog_Full_Ext","Exit AppLoggin Full Constructor");
    }//End of AppLoggin full constructor

    //Getter and Setter methods


    public String getMessage() {
        return message;
    }

    public Icon getPicture() {
        return picture;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPicture(Icon picture) {
        this.picture = picture;
    }

    //Other methods

    public static AppLoggin extractAppLoggin(Cursor c) {
        Log.d("Ent_ExtractLoggin", "Enter extractLoggin method in the AppLoggin  class.");
        AccountsDB accountsDB = LoginActivity.getAccountsDB();
        Cryptographer cryptographer = LoginActivity.getCryptographer();
        //Initialize local variables
        AppLoggin appLoggin = null;
        int _id;
        String name="";
        int userNameID;
        int psswrdID;
        String message = "";
        int pictureID;
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        userNameID = Integer.valueOf(cryptographer.decryptText(c.getBlob(1),new IvParameterSpec(c.getBlob(2))));
        psswrdID = Integer.valueOf(cryptographer.decryptText( c.getBlob(3),new IvParameterSpec(c.getBlob(4))));
        name = c.getString(5);
        message = c.getString(6);
        pictureID = c.getInt(7);

        try{
            appLoggin = new AppLoggin(_id,name,accountsDB.getUserNameByID(userNameID),accountsDB.getPsswrdByID(psswrdID),message,accountsDB.getIconByID(pictureID));
        }
        catch (Exception e){
            appLoggin = null;
            Log.d("Ext_ExtractLoggin", "Error while extracting appLogin on extractLoggin method in the AppLoggin class. Error: "+e.getMessage());
        }
        Log.d("Ext_ExtractLoggin", "Exit extractLoggin method in the AppLoggin  class.");
        return appLoggin;
    }//End of extractAppLoggin method

}// End of AppLogin