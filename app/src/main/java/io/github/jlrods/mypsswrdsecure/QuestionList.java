package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

class QuestionList {
//    Attribute definition
    int _id;
    ArrayList<Question> questions;

//    Method definition

    //Constructors
    public QuestionList(int _id,Question q1, Question q2, Question q3){
        Log.d("QuestionList","Enter the QuestionList full constructor method in the QuestionList class.");
        this._id = _id;
        questions = new ArrayList<Question>();
        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
        Log.d("QuestionList","Exit the QuestionList full constructor method in the QuestionList class.");
    }//End of full constructor

    public QuestionList(){
        Log.d("QuestionList1","Enter the QuestionList overloaded constructor method 1 in the QuestionList class.");
        this._id = -1;
        this.questions = new ArrayList<Question>();
        Log.d("QuestionList","Exit the QuestionList overloaded constructor method 1 in the QuestionList class.");
    }//End of overloaded constructor


    public QuestionList(int _id,Question q1){
        Log.d("QuestionList2","Enter the QuestionList overloaded constructor method 2 in the QuestionList class.");
        this._id = _id;
        this.questions = new ArrayList<Question>();
        questions.add(q1);
        Log.d("QuestionList2","Exit the QuestionList overloaded constructor method 2 in the QuestionList class.");
    }//End of overloaded constructor

    public QuestionList(int _id,Question q1, Question q2){
        Log.d("QuestionList3","Enter the QuestionList overloaded constructor method 3 in the QuestionList class.");
        this._id = _id;
        this.questions = new ArrayList<Question>();
        questions.add(q1);
        questions.add(q2);
        Log.d("QuestionList3","Exit the QuestionList overloaded constructor method 3 in the QuestionList class.");
    }//End of overloaded constructor

    public QuestionList(int _id,Cursor qList){
        Log.d("QuestionList4","Enter the QuestionList overloaded constructor method 4 in the QuestionList class.");
        this._id = _id;
        this.questions = new ArrayList<Question>();
        if(qList != null && qList.getCount()>0){
            while(qList.moveToNext()){
                questions.add(Question.extractQuestion(qList));
            }
        }else{
            this.questions = null;
        }
        Log.d("QuestionList4","Exit the QuestionList overloaded constructor method 4 in the QuestionList class.");
    }//End of overloaded constructor

    //Getter and setter methods
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    //Other methods
    public void addQuestion(Question q){
        this.questions.add(q);
    }

    public void addTwoQuestions(Question q1, Question q2){
        Log.d("QuestionList4","Enter the QuestionList overloaded constructor method 4 in the QuestionList class.");
        this.questions.add(q1);
        this.questions.add(q2);
        Log.d("QuestionList4","Enter the QuestionList overloaded constructor method 4 in the QuestionList class.");
    }//End of addTwoQuestions method

    public void removeQuestion(Question q){
        this.questions.remove(q);
    }

    public int getSize(){
        return this.questions.size();
    }
}//End of QuestionList class
