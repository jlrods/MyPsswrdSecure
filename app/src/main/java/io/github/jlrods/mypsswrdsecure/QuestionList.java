package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;

import java.util.ArrayList;

class QuestionList {
//    Attribute definition
//    Question q1;
//    Question q2;
//    Question q3;
//
    int _id;
    ArrayList<Question> questions;

//    Method definition

    //Constructors
    public QuestionList(int _id,Question q1, Question q2, Question q3){
        this._id = _id;
        questions = new ArrayList<Question>();
        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
    }
    public QuestionList(){
        this(-1,null,null,null);
    }

    public QuestionList(int _id,Question q1){
        this(_id,q1,null,null);
    }
    public QuestionList(int _id,Question q1, Question q2){
        this(_id,q1,q2,null);
    }

    public QuestionList(int _id,Cursor qList){
        this._id = _id;
        this.questions = new ArrayList<Question>();
        if(qList != null && qList.getCount()>0){
            while(qList.moveToNext()){
                questions.add(Question.extractQuestion(qList));
            }
        }else{
            this.questions = null;
        }
    }

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
        this.questions.add(q1);
        this.questions.add(q2);
    }

    public void removeQuestion(Question q){
        this.questions.remove(q);
    }

    public int getSize(){
        return this.questions.size();
    }
}
