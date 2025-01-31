package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

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
            }//End of while loop
        }else{
            this.questions = null;
        }//End of if else statement to check question list isn't null or empty
        Log.d("QuestionList4","Exit the QuestionList overloaded constructor method 4 in the QuestionList class.");
    }//End of overloaded constructor

    //Getter and setter methods
    public int get_id() {
        return _id;
    }//End of get_id method

    public void set_id(int _id) {
        this._id = _id;
    }//End of set_id method

    public ArrayList<Question> getQuestions() {
        return questions;
    }//End of getQuestions method

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }//End of getQuestions method

    //Other methods
    public void addQuestion(Question q){
        this.questions.add(q);
    }//End of addQuestion method

    //method to add two questions to a list in one go
    public void addTwoQuestions(Question q1, Question q2){
        Log.d("QuestionList4","Enter the QuestionList overloaded constructor method 4 in the QuestionList class.");
        this.questions.add(q1);
        this.questions.add(q2);
        Log.d("QuestionList4","Enter the QuestionList overloaded constructor method 4 in the QuestionList class.");
    }//End of addTwoQuestions method

    public void removeQuestion(Question q){
        this.questions.remove(q);
    }//End of removeQuestion method

    public int getSize(){
        return this.questions.size();
    }//End of getSize method

    //Method to return string information of the question object
    @NonNull
    @Override
    public String toString() {
        Log.d("QuestionList_ToStr_Ent","Enter QuestionLIst class ToString method");
        String separator = "; ";
        String list = "";
        if(this.questions != null && this.questions.size() > 0){
            int i = 0;
            list = list.concat(String.valueOf(this.get_id()));
            while(i < this.questions.size()){
                list = list.concat(separator).concat(this.questions.get(i).toString());
                i++;
            }
        }
        return list;
    }//End of toString method

}//End of QuestionList class
