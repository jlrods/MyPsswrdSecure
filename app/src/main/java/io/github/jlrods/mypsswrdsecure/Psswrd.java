package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

// Class to handle Psswrd object efinition
class Psswrd extends StringValue{
    //Attribute definition
    private long createDate;
    //Method definition

    //Constructor
    public Psswrd(){
        super();
    }

    public Psswrd(int _id, String value){

        super(_id,value);
        this.createDate = System.currentTimeMillis() ;
    }

    @NonNull
    @Override
    public String toString() {
        Log.d("Psswrd_ToStr_Ent","Enter Psswrd class ToString method");
        return "Password ID: " + this._id +"\nValue: " + this.value;
    }

    //Method to extract a passwrod from a cursor object
    public Psswrd extractPsswrd(Cursor c){
        Log.d("Ent_ExtractPsswrd","Enter extractPsswrd method in the Psswrd class.");
        //Initialize local variables
        Psswrd psswrd = null;
        //Call common method from parent class to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = this.extractStrValue(c);
        //Create a new Icon object by calling full constructor
        psswrd = new Psswrd((int) attributes.get(0), (String) attributes.get(1));
        Log.d("Ext_ExtractPsswrd","Exit extractPsswrd method in the Psswrd class.");
        return psswrd;
    }// End of extractPsswrd method
}// End of Psswrd class
