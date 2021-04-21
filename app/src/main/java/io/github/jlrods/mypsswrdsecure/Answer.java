package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.ArrayList;

// Class to handle Answer object definition
class Answer extends StringValue{
    //Method definition

    //Constructor
    public Answer(int _id, byte[] value, byte[] iv){
        super(_id, value, iv);
        Log.d("AnswFullConst","Exit Answer Full Constructor");
    }

    public Answer(byte[] value, byte[] iv){
        this(-1,value, iv);
        Log.d("AnswConst3Parms","Exit Answer 3 params Constructor");
    }

    public Answer(){
        super();
        Log.d("AnswConstNoParms","Exit Answer No params Constructor");
    }

    //ToString method
    @NonNull
    @Override
    public String toString() {
        Log.d("Answer_ToStr_Ent","Enter Answer class ToString method");
        return "Answer ID: " + this._id +"\nValue: " + this.value;
    }//Endof toString method

    //Method to extract an Answer from a cursor object
    public static Answer extractAnswer(Cursor c){
        Log.d("Ent_ExtractAns","Enter extractAns method in the Answer class.");
        //Initialize local variables
        Answer answer = null;
        //Call common method from parent class to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = extractStrValue(c);
        //Create a new Icon object by calling full constructor
        answer = new Answer((int) attributes.get(0), (byte[]) attributes.get(1),(byte[]) attributes.get(2));
        Log.d("Ext_ExtractAns","Exit extractAns method in the Answer class.");
        return answer;
    }// End of extractAnswer method
}// End of Answer class
