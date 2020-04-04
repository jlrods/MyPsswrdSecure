package io.github.jlrods.mypsswrdsecure;

import android.util.Log;

//Interface to define Loggin attributes and methods to be implemented
abstract class Loggin {

    //Attributes definition
    protected int _id; //DB unique ID
    protected String userName;
    protected String psswrd;

    // Methods definition


    //Getter and setters

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getPsswrd(){
        return this.psswrd;
    }
    public void setPsswrd(String psswrd){
        this.psswrd = psswrd;
    }

}// End of Loggin abstract class
