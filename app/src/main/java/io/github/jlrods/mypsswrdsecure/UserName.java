package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

//Class to handle UsenName object definition
class UserName extends StringValue {
    //Attribute definition
    private long createDate;

    //Method definition

    //Constructor
    public UserName(){
        super();
    }

    public UserName(int _id, String value){
        super(_id,value);
        this.createDate = System.currentTimeMillis() ;
    }

    //Other methods
    @NonNull
    @Override
    public String toString() {
        Log.d("UsrName_ToStr_Ent","Enter UserValue ToString method");
        return "UserName ID: " + this._id +"\nName: " + this.value;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    //Method to extract an Answer from a cursor object
    public UserName extractUserName(Cursor c){
        Log.d("Ent_ExtractUser","Enter extractUserName method in the UserName class.");
        //Initialize local variables
        UserName userName = null;
        //Call common method to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = this.extractStrValue(c);
        //Create a new Icon object by calling full constructor
        userName = new UserName((int) attributes.get(0), (String) attributes.get(1));
        Log.d("Ext_ExtractUser","Exit extractUserName method in the UserName class.");
        return userName;
    }// End of extractPsswrd method
}// End of user Class
