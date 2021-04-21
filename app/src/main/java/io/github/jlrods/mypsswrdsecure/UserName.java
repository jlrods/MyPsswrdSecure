package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.ArrayList;

//Class to handle UsenName object definition
public class UserName extends StringValue {
    //Attribute definition
    protected long dateCreated;

    //Method definition
    //Constructor
    public UserName(int _id, byte[] value, byte[] iv, long dateCreated){
        super(_id,value,iv);
        Log.d("UserNameFullConst","Enter UserName Full Constructor");
        this.dateCreated = dateCreated;
        Log.d("UserNameFullConst","Exit UserName Full Constructor");
    }//End of UserName constructor

    public UserName(int _id, byte[] value, byte[] iv){
        this(_id,value, iv, System.currentTimeMillis());
        Log.d("UserNameConst2","Enter UserName Constructor 3 arguments");
    }//End of UserName constructor

    public UserName(byte[] value,byte[] iv){
        this(-1, value,iv);
        Log.d("UserNameConst3","Enter UserName Constructor 2 arguments");
    }//End of UserName constructor
    public UserName(){
        this(null, null);
        Log.d("UserNameConst2","Enter UserName Constructor 0 arguments");
    }//End of UserName constructor


    //Other methods
    @NonNull
    @Override
    public String toString() {
        Log.d("UsrName_ToStr_Ent","Enter UserValue ToString method");
        return "UserName ID: " + this._id +"\nName: " + this.value;
    }//End of toString method

    //Getter and setter methods
    public long getDateCreated() {
        return dateCreated;
    }//End of getDateCreated method

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }//End of getDateCreated method

    //Method to extract an Answer from a cursor object
    public static UserName extractUserName(Cursor c){
        Log.d("Ent_ExtractUser","Enter extractUserName method in the UserName class.");
        //Initialize local variables
        UserName userName = null;
        //Call common method to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = extractStrValue(c);
        //Create a new user name object by calling full constructor, but check date created value first
        if(c.getLong(3) > 0){
            userName = new UserName((int) attributes.get(0), (byte[]) attributes.get(1),(byte[]) attributes.get(2), c.getLong(3));
        }else{
            userName = new UserName((int) attributes.get(0), (byte[]) attributes.get(1),(byte[]) attributes.get(2));
        }//End of if else statement to check created value is greater than zero
        Log.d("Ext_ExtractUser","Exit extractUserName method in the UserName class.");
        return userName;
    }// End of extractPsswrd method
}// End of user Class