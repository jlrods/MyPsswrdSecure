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
    private EditText etAnswer;
    private TextView tvQuestionTag;
    private LinearLayout answerSubLayout = null;
    //Attributes to be used while saving the new question
    private Question question = null;
    private Answer answer = null;


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
                //Check the question field isn't empty
                if(this.isFieldNotEmpty(this.etNewItemField)){
                    //Check the answer field isn't empty
                    if(this.isFieldNotEmpty(this.etAnswer)){
                        //Create question and answer objects
                        String answerValue = this.etAnswer.getText().toString().trim();
                        byte[] answerValueEncrypted =  this.cryptographer.encryptText(answerValue);
                        this.answer = new Answer(answerValueEncrypted,this.cryptographer.getIv().getIV());
                        this.question = new Question(this.etNewItemField.getText().toString().trim(),this.answer);
                        //Check the question isn't in the DB already
                        //Check the question isn't one of the pre-loaded questions
                        if(!isQuestionPreLoaded(this.question)){
                            this.cursor = this.accountsDB.getQuestionByValue(this.question.getValue());
                            if(this.cursor == null  || this.cursor.getCount() == 0){
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
                                    finish();
                                }else{
                                    //Otherwise prompt the user the question already exists
                                    MainActivity.displayToast(this,getResources().getString(R.string.snackBarQuestionNotAdded),Toast.LENGTH_LONG,Gravity.CENTER);
                                }
                            }else{
                                //Otherwise prompt the user the question already exists
                                MainActivity.displayToast(this,getResources().getString(R.string.snackBarQuestionExists),Toast.LENGTH_LONG,Gravity.CENTER);
                            }
                        }else{
                            //Otherwise prompt the user the question already exists
                            MainActivity.displayToast(this,getResources().getString(R.string.snackBarQuestionExists),Toast.LENGTH_LONG,Gravity.CENTER);
                        }//End of if else statement to check the question is preloaded
                        intent.putExtra("answerID",this.answer.get_id());
                        intent.putExtra("questionID",this.question.get_id());
                        setResult(RESULT_OK, intent);
                    }else{
                        //Display toast to prompt user the input field is empty
                        MainActivity.displayToast(this,getResources().getString(R.string.answerNotEntered),Toast.LENGTH_LONG,Gravity.CENTER);
                    }//End of if else statement to check the answer isn't empty
                }else{
                    //Display toast to prompt user the input field is empty
                    MainActivity.displayToast(this,getResources().getString(R.string.questionNotEntered),Toast.LENGTH_LONG,Gravity.CENTER);
                }//End of if else statement to check input field isn't empty
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

    private boolean isQuestionPreLoaded(Question question){
        Log.d("isQuestionPreLoaded","Enter the isQuestionPreLoaded method in AddQuestionActivity class.");
        //Declare and instantiate variables to look for the questions
        Resources res = getResources();// Used to get preloaded sting data
        Cursor preLoadedQuestions = this.accountsDB.getPreLoadedQuestions(); // list of preloaded questions (stored with id in the DB and not the string value)
        //Booloean flag to be returned by method
        boolean isPreloaded = false;
        //While loop to iterate through the list of preloaded questions and check their text against parameter value
        while(!isPreloaded && preLoadedQuestions.moveToNext()){
            if(question.getValue().toLowerCase().trim().equals(
                    res.getString(res.getIdentifier(preLoadedQuestions.getString(1),
                            "string",getBaseContext().getPackageName())).toLowerCase().trim())){
                isPreloaded = true;
                Log.d("isQuestionPreLoaded","The question has been found as preloaded quesiton in the AddQuestionActivity class.");
            }
        }//End of while loop to iterate through
        Log.d("isQuestionPreLoaded","Exit the isQuestionPreLoaded method in AddQuestionActivity class.");
        return isPreloaded;
    }//End of isQuestionPreLoaded
}//End of AddQuestionActivity
