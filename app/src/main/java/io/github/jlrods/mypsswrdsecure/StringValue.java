package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

// Class define d to handle all string value classes
abstract class StringValue {
    //Attribute definition
    protected int _id;
    protected String value;
    //Method definition

    //Constructors
    public StringValue(){
        Log.d("StrVal_Def_Ent","Enter StringValue Default Constructor");
        this._id = 0;
        this.value = "";
        Log.d("StrVal_Def_Ext","Exit StringValue  Default Constructor");
    }

    public StringValue(int _id, String value){
        Log.d("StrVal_Full_Ent","Enter StringValue Full Constructor");
        this._id = _id;
        this.value = value;
        Log.d("StrVal_Full_Ext","Exit StringValue  Full Constructor");
    }

    //Setter and Getter methods
    public int get_id() {
        return _id;
    }

    public String getValue() {
        return value;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //Other methods
    //Method to extract a StringValue object from a cursor object
    protected static ArrayList<Object> extractStrValue(Cursor c){
        Log.d("Ent_ExtractStrValue","Enter extractStrValue method in the StringValue class.");
        //Initialize local variables
        ArrayList<Object> attributes = new ArrayList<>();
        int _id;
        String value ="";
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        value = c.getString(1);
        attributes.add(_id);
        attributes.add(value);
        //Create a new Icon object by calling full constructor
        Log.d("Ext_ExtractStrValue","Exit extractStrValue method in the StringValue class.");
        return attributes;
    }// End of extractAnswer method
}// End of UserName class
