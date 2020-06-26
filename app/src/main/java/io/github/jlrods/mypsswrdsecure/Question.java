package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

// Class to handle Question object definition
class Question extends StringValue{
    //Attribute definition
    private Answer answer;

    //Method definition

    //Additional Constructor
    public Question(){
        super();
        this.answer = null;
    }

    public Question(int _id, String value, int AnswerID,String answerText){
        super(_id,value);
        this.answer = new Answer(_id, answerText);
    }

    public Question(String value, Answer answer){
        this(-1,value,answer);
    }

    public Question(int _id, String value, Answer answer){
        super(_id,value);
        this.answer = answer;
    }
    //
    @NonNull
    @Override
    public String toString() {
        Log.d("Question_ToStr_Ent","Enter Question class ToString method");
        return "Question ID: " + this._id +"\nValue: " + this.value+"\nAnswer: "+ this.answer.toString() ;
    }

    //Method to extract an Answer from a cursor object
    public static Question extractQuestion(Cursor c){
        Log.d("Ent_ExtractQst","Enter extractQuestion method in the Question class.");
        //Declare and initialize a db manager class to run sql queries
        //AccountsDB accounts = HomeFragment.getAccounts();
        //Initialize local variables
        Question question = null;
        int _id;
        String value ="";
        Answer answer = null;
        //Call common method from parent class to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = extractStrValue(c);
        //Create a new Answer object by calling full constructor
        //answer = accounts.getAnswerByID(c.getInt(2));
        answer = new Answer(c.getInt(2),c.getString(4));
        //Create new Question by using full constructor
        question = new Question((int) attributes.get(0),(String) attributes.get(1),answer);
        Log.d("Ext_ExtractAns","Exit extractQuestion method in the Question class.");
        return question;
    }// End of extractAnswer method

    //Setter and getter methods

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}// End of Question class
