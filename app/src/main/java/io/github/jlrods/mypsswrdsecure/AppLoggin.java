package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

public class AppLoggin extends Loggin {

    //Attribute expansion definition

    String email; // User's app email
    String message; //Message to be display on the drawer nav menu
    Icon picture; //Picture location of image to be displayed on the drawer nav menu


    //Constructors
    //Default Constructor
    public AppLoggin(){
        super();
        Log.d("AppLogginDef_Ent","Enter AppLoggin Default Constructor");
        this.email = "";
        this.message = "";
        this.picture = null;
        Log.d("AppLoggin_Def_Ext","Exit AppLoggin Default Constructor");
    }

    //Constructor with no password given
    public AppLoggin(UserName userName){
        Log.d("AppLoggin_NoPass_Ent","Enter AppLoggin Constructor 2");
        this.userName = userName;
        this.psswrd = null;
        this.email = "";
        this.message ="";
        this.picture = null;
        Log.d("AppLoggin_NoPass_Ext","Exit AppLoggin Constructor 2");
    }
    //Full Constructor
    public AppLoggin(int _id, String name, UserName userName, Psswrd psswrd, String email,String message, Icon picture){
        super(_id, name, userName, psswrd);
        Log.d("AppLog_Full_Ent","Enter AppLoggin Full Constructor");
        this.email = email;
        this.message = message;
        this.picture = picture;
        Log.d("AppLog_Full_Ext","Exit AppLoggin Full Constructor");
    }

    //Getter and Setter methods

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
        //Initialize local variables
        AppLoggin appLoggin = null;
        int _id;
        String name="";
        int userNameID;
        int psswrdID;
        String email = "";
        String message = "";
        int pictureID;
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        userNameID = c.getInt(1);
        psswrdID = c.getInt(2);
        name = c.getString(3);
        email = c.getString(4);
        message = c.getString(5);
        pictureID = c.getInt(6);

        //Call common method to extract basic StringValue object data from a cursor
        //ArrayList<Object> attributes = Loggin.extractLoggin(c);
        try{
            appLoggin = new AppLoggin(_id,name,accountsDB.getUserNameByID(userNameID),accountsDB.getPsswrdByID(psswrdID),email,message,accountsDB.getIconByID(pictureID));
        }
        catch (Exception e){
            appLoggin = null;
            Log.d("Ext_ExtractLoggin", "Error while extracting appLogin on extractLoggin method in the AppLoggin class. Error: "+e.getMessage());
        }
        Log.d("Ext_ExtractLoggin", "Exit extractLoggin method in the AppLoggin  class.");
        return appLoggin;
    }
}// End of AppUser
