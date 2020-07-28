package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

// Class define d to handle all string value classes
abstract class StringValue {
    //Attribute definition
    protected int _id;
    protected byte[] value;
    protected byte[] iv;
    //Method definition

    //Constructors
    //Full constructor
    public StringValue(int _id, byte[] value, byte[] iv){
        Log.d("StrVal_Full_Ent","Enter StringValue Full Constructor");
        this._id = _id;
        this.value = value;
        this.iv = iv;
        Log.d("StrVal_Full_Ext","Exit StringValue  Full Constructor");
    }

    public StringValue(){
        this(-1,null,null);
        Log.d("StrVal_Def_Ext","Exit StringValue  Default Constructor");
    }


    //Setter and Getter methods
    public int get_id() {
        return _id;
    }

    public byte[] getValue() {
        return value;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    //Other methods
    //Method to extract a StringValue object from a cursor object
    protected static ArrayList<Object> extractStrValue(Cursor c){
        Log.d("Ent_ExtractStrValue","Enter extractStrValue method in the StringValue class.");
        //Initialize local variables
        ArrayList<Object> attributes = new ArrayList<>();
        int _id;
        byte[] value =null;
        byte[] iv = null;
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        value = c.getBlob(1);
        iv = c.getBlob(2);
        attributes.add(_id);
        attributes.add(value);
        attributes.add(iv);
        //Create a new Icon object by calling full constructor
        Log.d("Ext_ExtractStrValue","Exit extractStrValue method in the StringValue class.");
        return attributes;
    }// End of extractAnswer method
}// End of UserName class
