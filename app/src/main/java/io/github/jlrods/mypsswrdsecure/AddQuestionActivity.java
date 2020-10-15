package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddQuestionActivity extends AddItemActivity {
    //Layout Attributes
    protected TextView tvQuestionTag;
    protected LinearLayout answerSubLayout = null;
    //Attributes to be used while saving the new question
    protected Question question = null;
    protected Answer answer = null;


    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddQuestionActivity class.");
        //Update layout fields according to Add Security question layout
        this.imgAddActivityIcon.setImageResource(R.mipmap.ic_shield_lock_black_48dp);
        this.tvAddActivityTag.setText(R.string.addQuestQuestionTag);
        this.tvQuestionTag = (TextView) findViewById(R.id.tvSecQuestionTag);
        this.tvQuestionTag.setVisibility(View.VISIBLE);
        this.etNewItemField.setHint(R.string.addQuestQuestionHint);
        this.answerSubLayout = (LinearLayout) findViewById(R.id.layout_add_item_answer);
        this.answerSubLayout.setVisibility(View.VISIBLE);
        this.etAnswer = (EditText) findViewById(R.id.etAnswer);
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddQuestionActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddQuestionActivity class.");
        //Boolean to return method result
        boolean result = false;
        int answerID = -1;
        int questionID = -1;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                //Check if answer is valid
                if(this.isDataValid(5)){
                    //Check the question is valid
                    if(this.isDataValid(6)){
                        //Create question and answer objects
                        String answerValue = this.etAnswer.getText().toString().trim();
                        byte[] answerValueEncrypted =  this.cryptographer.encryptText(answerValue);
                        this.answer = new Answer(answerValueEncrypted,this.cryptographer.getIv().getIV());
                        this.question = new Question(this.etNewItemField.getText().toString().trim(),this.answer);
                        //Flag to make sure all data was added on DB
                        boolean dbTransCompleted = true;
                        //If question isn't in the DB, insert the answer in the DB, then insert then insert the question
                        answerID = accountsDB.addItem(answer);
                        if(answerID > 0){
                            this.answer.set_id(answerID);
                        }else{
                            dbTransCompleted = false;
                        }
                        questionID = this.accountsDB.addItem(question);
                        if(questionID > 0){
                            this.question.set_id(questionID);
                        }else{
                            dbTransCompleted = false;
                        }
                        if(dbTransCompleted){
                            Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                            intent.putExtra("answerID",this.answer.get_id());
                            intent.putExtra("questionID",this.question.get_id());
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            //Otherwise prompt the user the question already exists
                            MainActivity.displayToast(this,getResources().getString(R.string.snackBarQuestionNotAdded),Toast.LENGTH_LONG,Gravity.CENTER);
                        }
                    }//End of if statement to check the question is valid
                }//End of if to check the answer is valid
                break;
            case R.id.select_logo_cancel:
               //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                finish();
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddQuestionActivity class.");
        return result;
    }//End of onOptionsItemSelected


}//End of AddQuestionActivity
