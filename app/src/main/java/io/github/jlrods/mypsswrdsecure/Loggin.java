package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

//Interface to define Loggin attributes and methods to be implemented
abstract class Loggin {

    //Attributes definition
    protected int _id; //DB unique ID
    protected UserName userName;
    protected Psswrd psswrd;

    // Methods definition

    //Constructors
    protected Loggin(){
        Log.d("LogginDef_Ent","Enter Loggin Default Constructor");
        this._id = 0;
        this.userName = null;
        this.psswrd = null;
        Log.d("LogginDef_Ext","Exit Loggin Default Constructor");
    }

    protected Loggin(int _id, UserName userName, Psswrd psswrd){
        Log.d("LogginFull_Ent","Enter Loggin Full Constructor");
        this._id = _id;
        this.userName = userName;
        this.psswrd = psswrd;
        Log.d("LogginFull_Ext","Exit Loggin Full Constructor");
    }

    //Getter and setters
    protected int get_id() {
        return _id;
    }

    protected void set_id(int _id) {
        this._id = _id;
    }

    protected UserName getUserName(){
        return this.userName;
    }

    protected void setUserName(UserName userName){
        this.userName = userName;
    }

    protected Psswrd getPsswrd(){
        return this.psswrd;
    }

    protected void setPsswrd(Psswrd psswrd){
        this.psswrd = psswrd;
    }

    //Other methods
    protected static ArrayList<Object> extractLoggin(Cursor c){
        Log.d("Ent_ExtractLoggin","Enter extractLoggin method in the Loggin abstract class.");
        //Initialize local variables
        ArrayList<Object> attributes = null;
        int _id;
        String userNameValue ="";
        String psswrdValue ="";
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        userNameValue = c.getString(1);
        psswrdValue = c.getString(2);
        attributes.add(_id);
        attributes.add(userNameValue);
        attributes.add(psswrdValue);
        //Create a new Icon object by calling full constructor
        Log.d("Ext_ExtractLoggin","Exit extractLoggin method in the Loggin abstract class.");
        return attributes;
    }// End of extractAnswer method

}// End of Loggin abstract class
