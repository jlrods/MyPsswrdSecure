package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import javax.crypto.spec.IvParameterSpec;

//Class to handle the adapter objects to link spinners UI and question data
class SpinnerAdapterQuestion extends SpinnerAdapter {

    //Constructor method
    public SpinnerAdapterQuestion(Context context, Cursor c, int flags) {
        super(context, c, flags);
        Log.d("SpinnerAdapterQuestion","Enter|Exit SpinnerAdapterQuestion constructor method in SpinnerAdapterEncrypted class.");
    }//End of SpinnerAdapterQuestion constructor

    //Method to create a new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.spinner_item_question,parent,false);
    }//End of newView method

    //Method to bind the view and the data via a cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("QuestionbindView","Enter bindView method to populate stvAnswerpinners in SpinnerAdapterQuestion class.");
        //Declare and instantiate a TextView object to hold the unit name and symbol
        TextView tvQuestionItem = (TextView) view.findViewById(R.id.tvQuestionItem);
        TextView tvAnswer = (TextView) view.findViewById(R.id.tvAnswer);
        //Declare and instantiate a String to hold the name by extracting data from cursor (Column 1 will hold the name attribute)
        String questionText = cursor.getString(1);
        //As brand new app has no answers for preloaded questions... Check the answer field isn't null, otherwise skip populating the answer value
        String answerText = "";
        if(cursor.getBlob(3) != null && cursor.getBlob(4) != null){
            answerText = cryptographer.decryptText(cursor.getBlob(3),new IvParameterSpec(cursor.getBlob(4))) ;
        }
        //Declare and instantiate an int to hold the string id from resources
        int questionID = context.getResources().getIdentifier(questionText,"string",context.getPackageName());
        //If textID is 0, means it's not stored in the app resources
        if(questionID > 0){
            //Set text from resources by passing in its id
            tvQuestionItem.setText(context.getString(questionID));
        }else{
            //In the case of not being a resource, print the text retrieved from DB
            tvQuestionItem.setText(questionText);
        }//End of if else statement
        //Set answer text retrieved from DB
        tvAnswer.setText(answerText);
        Log.d("QuestionbindView","Exit bindView method to populate spinners in SpinnerAdapterQuestion class.");
    }//End of bindView method
}//End of SpinnerAdapterQuestion class