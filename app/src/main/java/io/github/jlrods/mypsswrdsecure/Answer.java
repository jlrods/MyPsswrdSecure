package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

// Class to handle Answer object definition
class Answer extends StringValue{
    //Method definition

    //Constructor
    public Answer(){
        super();
    }

    public Answer(int _id, String value){
        super();
    }

    //ToString method
    @NonNull
    @Override
    public String toString() {
        Log.d("Answer_ToStr_Ent","Enter Answer class ToString method");
        return "Answer ID: " + this._id +"\nValue: " + this.value;
    }
    //Method to extract an Answer from a cursor object
    public Answer extractAnswer(Cursor c){
        Log.d("Ent_ExtractAns","Enter extractAns method in the Answer class.");
        //Initialize local variables
        Answer answer = null;
        //Call common method from parent class to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = this.extractStrValue(c);
        //Create a new Icon object by calling full constructor
        answer = new Answer((int) attributes.get(0), (String) attributes.get(1));
        Log.d("Ext_ExtractAns","Exit extractAns method in the Answer class.");
        return answer;
    }// End of extractAnswer method

}// End of Answer class
