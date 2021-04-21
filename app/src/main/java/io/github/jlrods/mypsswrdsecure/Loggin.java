package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;
import java.util.ArrayList;

//Interface to define Loggin attributes and methods to be implemented
abstract class Loggin {

    //Attributes definition
    protected int _id; //DB unique ID
    protected String name;
    protected UserName userName;
    protected Psswrd psswrd;

    // Methods definition
    //Constructors
    protected Loggin(){
        Log.d("LogginDef_Ent","Enter Loggin Default Constructor");
        this._id = -1;
        this.name="";
        this.userName = null;
        this.psswrd = null;
        Log.d("LogginDef_Ext","Exit Loggin Default Constructor");
    }

    protected Loggin(int _id, String name, UserName userName, Psswrd psswrd){
        Log.d("LogginFull_Ent","Enter Loggin Full Constructor");
        this._id = _id;
        this.name = name;
        this.userName = userName;
        this.psswrd = psswrd;
        Log.d("LogginFull_Ext","Exit Loggin Full Constructor");
    }

    //Getter and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public UserName getUserName(){
        return this.userName;
    }

    public void setUserName(UserName userName){
        this.userName = userName;
    }

    public Psswrd getPsswrd(){
        return this.psswrd;
    }

    public void setPsswrd(Psswrd psswrd){
        this.psswrd = psswrd;
    }

    //Other methods
    public static ArrayList<Object> extractLoggin(Cursor c){
        Log.d("Ent_ExtractLoggin","Enter extractLoggin method in the Loggin abstract class.");
        //Initialize local variables
        ArrayList<Object> attributes = new ArrayList<>();
        int _id;
        String name="";
        int userNameValue;
        int psswrdValue;
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        name = c.getString(1);
        userNameValue = c.getInt(3);
        psswrdValue = c.getInt(4);
        attributes.add(_id);
        attributes.add(name);
        attributes.add(userNameValue);
        attributes.add(psswrdValue);
        //Create a new Icon object by calling full constructor
        Log.d("Ext_ExtractLoggin","Exit extractLoggin method in the Loggin abstract class.");
        return attributes;
    }// End of extractAnswer method
}// End of Loggin abstract class