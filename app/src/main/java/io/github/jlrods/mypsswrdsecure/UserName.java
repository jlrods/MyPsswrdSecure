package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

//Class to handle UsenName object definition
class UserName extends StringValue {
    //Attribute definition
    protected long dateCreated;
    protected static Cryptographer cryptographer;

    //Method definition

    //Constructor
    public UserName(int _id, byte[] value, byte[] iv, long dateCreated){
        super(_id,value,iv);
        Log.d("UserNameFullConst","Enter UserName Full Constructor");
        this.dateCreated = dateCreated;
        cryptographer = MainActivity.getCryptographer();
        Log.d("UserNameFullConst","Exit UserName Full Constructor");
    }
    public UserName(int _id, byte[] value, byte[] iv){
        this(_id,value, iv, System.currentTimeMillis());
        Log.d("UserNameConst2","Enter UserName Constructor 3 arguments");
    }

    public UserName(byte[] value,byte[] iv){
        this(-1, value,iv);
        Log.d("UserNameConst3","Enter UserName Constructor 2 arguments");
    }
    public UserName(){
        this(null, null);
        Log.d("UserNameConst2","Enter UserName Constructor 0 arguments");
    }


    //Other methods
    @NonNull
    @Override
    public String toString() {
        Log.d("UsrName_ToStr_Ent","Enter UserValue ToString method");
        return "UserName ID: " + this._id +"\nName: " + this.value;
    }

    //Getter and setter methods
    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }


    //Method to extract an Answer from a cursor object
    public static UserName extractUserName(Cursor c){
        Log.d("Ent_ExtractUser","Enter extractUserName method in the UserName class.");
        //Initialize local variables
        UserName userName = null;
        //Call common method to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = extractStrValue(c);
        //Create a new Icon object by calling full constructor
        userName = new UserName((int) attributes.get(0), (byte[]) attributes.get(1),(byte[]) attributes.get(2));
        Log.d("Ext_ExtractUser","Exit extractUserName method in the UserName class.");
        return userName;
    }// End of extractPsswrd method

//    //Method to encrypt sensible data before sending it to DB
//    protected byte[] encryptData(String data){
//        return this.cryptographer.encryptText(data);
//    }
//    //Method to decrypt sensible data when retrieving encrypted data from DB
//    protected String decryptData(byte[] encryptedText, IvParameterSpec initVector){
//        return this.cryptographer.decryptText(encryptedText,initVector);
//    }
}// End of user Class
