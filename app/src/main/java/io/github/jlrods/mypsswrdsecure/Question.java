package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

// Class to handle Question object definition
class Question{
    //Attribute definition
    private int _id;
    private String value;
    private Answer answer;

    //Method definition

    //Additional Constructor
    public Question(int _id, String value,Answer answer){
        Log.d("QuestionFullConst","Enter Question Full Constructor");
        this._id = _id;
        this.value = value;
        this.answer = answer;
        Log.d("QuestionFullConst","Exit Question Full Constructor");
    }

    public Question(int _id, String value,int AnswerID,byte[] answerText,byte[] answerIV){
        this(_id,value,new Answer(_id, answerText,answerIV));
        Log.d("QuestionConst1","Exit Question Constructor 4 arguments");
    }

    public Question(String value, Answer answer){
        this(-1,value,answer);
        Log.d("UserNameConst2","Exit Question Constructor 2 arguments");
    }

    public Question(String value,int AnswerID,byte[] answerText, byte[] answerIV){
        this(value,new Answer(AnswerID, answerText,answerIV));
        Log.d("QuestionConst3","Exit Question Constructor 3 arguments");
    }

    public Question(String value){
        this(value,null);
        Log.d("QuestionConst4","Exit Question Constructor 1 arguments");
    }

    public Question(){
        this("",null);
        Log.d("QuestionConst5","Exit Question Constructor no arguments");
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
        //Initialize local variables
        Question question = null;
        int _id;
        String value ="";
        Answer answer = null;
        _id = c.getInt(0);
        value = c.getString(1);
        //Create a new Answer object by calling full constructor
        answer = new Answer(c.getInt(2),c.getBlob(3),c.getBlob(4));
        //Create new Question by using full constructor
        question = new Question(_id,value,answer);
        Log.d("Ext_ExtractAns","Exit extractQuestion method in the Question class.");
        return question;
    }// End of extractAnswer method

    //Setter and getter methods


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}// End of Question class
