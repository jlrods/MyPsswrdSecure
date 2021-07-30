package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import javax.crypto.spec.IvParameterSpec;

public class EditQuestionActivity extends AddQuestionActivity {
    //Attribute definition
    private Bundle extras;
    private boolean isPreloadedQuestion = false;
    //Method definition

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditUser","Enter onCreate method in the EditQuestionActivity class.");
        this.extras = getIntent().getExtras();
        //Set activity title
        getSupportActionBar().setTitle(R.string.editQuestTitle);
        //Extract user name by passing in the _id attribute stored in the extras
        this.question = Question.extractQuestion(this.accountsDB.getQuestionCursorByID(this.extras.getInt("_id")));
        int position = this.extras.getInt("position");
        this.answer = question.getAnswer();
        //Set the edit text field with the user name value after decryption
        if(isQuestionPreLoaded(this.question,true)){
            Resources res = getResources();
            //set the string text by calling ID, so text can change with language set up
            this.etNewItemField.setText(res.getString(res.getIdentifier(question.getValue(),"string",getBaseContext().getPackageName())));
            this.etNewItemField.setEnabled(false);
            this.isPreloadedQuestion = true;
        }else{
            this.etNewItemField.setText(question.getValue());
        }
        if(this.answer != null && this.answer.getValue() != null && this.answer.getIv() != null ){
            this.etAnswer.setText(this.cryptographer.decryptText(this.answer.getValue(),new IvParameterSpec(this.answer.getIv())));
        }
        //Check the current question is not preloaded to enable the fab button, otherwise leave the fab button invisible
        if(!this.isPreloadedQuestion){
            this.fabDelete.setVisibility(View.VISIBLE);
            //Set up fab onClick event listener by creating a new object of the sub class which handles this event
            this.fabDelete.setOnClickListener(
                    //Create the subclass object by passing in the text required to populate the AlertDialog box and the intent attribute name to be passed to caller activity
                    new FabOnClickEventHandler(question,getResources().getString(R.string.questionDeleteTitle),
                            getResources().getString(R.string.questionDeleteMssg),
                            question.getValue(),
                            "itemDeleted",position)
            );//End of setOnClickEventHandler method
        }//End of if statement to check the question is not preloaded
        Log.d("OnCreateEditUser","Exit onCreate method in the EditQuestionActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "Enter onOptionsItemSelected method in EditQuestionActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in EditQuestionActivity class.");
                //Check for UI data validity
                //As Question ID is passed in as extra to the activity, use that value to create a new question object
                //Then compare the answer and question texts to make sure at least one changed
                //if none changed throw warning
                //otherwise, continue with validation check by calling general method
                String newQuestionValue = etNewItemField.getText().toString().trim();
                String newAnswerValue = etAnswer.getText().toString();
                boolean questionAndAnswerAreValid = false;
                //Boolean flags to know what changed, the question, the answer or both
                boolean questionChanged = !this.question.getValue().equals(newQuestionValue);
                boolean answerChanged = false;
                if(this.answer != null && this.answer.getValue() != null){
                    answerChanged = !this.cryptographer.decryptText(this.answer.getValue(),new IvParameterSpec(this.answer.getIv())).equals(newAnswerValue);
                }else{
                    if(!newAnswerValue.equals("")){
                        answerChanged = true;
                    }//End of if statement to check the input value for answer isn't empty
                }//End of if else statement to check the answer in DB isn't empty (null)

                //Check if question is preloaded
                if(!this.isPreloadedQuestion){
                    //Check at least one value changed in the UI
                    if(questionChanged || answerChanged){
                        //Check if the answer changed
                        if(answerChanged){
                            //Check if answer is valid
                            if(this.isDataValid(5)) {
                                //Check if question is valid
                                if(this.isDataValid(7,this.question.get_id())) {
                                    questionAndAnswerAreValid = true;
                                }//End of if statement that checks the question is valid
                            }//End of if statement that checks the answer is valid
                        } else if(questionChanged){
                            //Check if question is valid
                            if(this.isDataValid(7,this.question.get_id())) {
                                questionAndAnswerAreValid = true;
                            }//End of if statement that checks the question is valid
                        }//End of if statement to check the answer changed
                    }else{
                        //Throw user notification toast
                        //Prompt the user the question or answer value have not changed
                        MainActivity.displayToast(this, getResources().getString(R.string.questionAnswerNoTChanged), Toast.LENGTH_SHORT, Gravity.CENTER);
                    }//End of if else that checks the question or answer value changed//End of if statement to check the answer changed
                }else{
                    //If question is preloaded check only the answer changed
                    if(answerChanged){
                        if(this.isDataValid(5)) {
                            questionAndAnswerAreValid = true;
                        }//End of if statement to check the answer is valid
                    }else{
                        //Prompt the user the answer value has not changed
                        MainActivity.displayToast(this, getResources().getString(R.string.answerNoTChanged), Toast.LENGTH_SHORT, Gravity.CENTER);
                    }//End of if else that checks the answer value changed (for preloaded questions)
                }//End of if else that checks for preloaded questions

                //If both, question and answer are valid, continue with DB update process
                if(questionAndAnswerAreValid){
                    //Do the DB update here once all data has been checked. In case of error, a prompt must have been thrown already
                    //Flag to make sure all data was added on DB
                    boolean answerDbTransCompleted = false;
                    boolean questionDbTransCompleted = false;
                    //variable to store answer id when new answer has to be added to the DB (Preloaded questions with empty answer)
                    int newAnswerID = -1;
                    //Store the  new question value and answer encrypted value and IV in the respective objects
                    if(answerChanged){
                        this.answer.setValue(this.cryptographer.encryptText(this.etAnswer.getText().toString()));
                        this.answer.setIv(this.cryptographer.getIv().getIV());
                        //Store the values to be updated in the DB
                        //Update the new answer in the DB
                        ContentValues values = new ContentValues();
                        if(this.answer.get_id() > 0){
                            values.put(MainActivity.getIdColumn(),this.answer.get_id());
                            values.put(MainActivity.getValueColumn(),this.answer.getValue());
                            values.put(MainActivity.getInitVectorColumn(),this.answer.getIv());
                            answerDbTransCompleted = this.accountsDB.updateTable(MainActivity.getAnswerTable(),values);
                        }else{
                            newAnswerID = this.accountsDB.addItem(this.answer);
                            if(newAnswerID > 0){
                                this.answer.set_id(newAnswerID);
                                answerDbTransCompleted = true;
                            }//End of if statement that checks the new answer was successfully added to the DB
                        }//End of if else statement that checks the answer object has a valid DB _id, any value greater than 0 means it exists in DB
                    }else{
                        answerDbTransCompleted = true;
                    }//End of if else statement to check the answer changed
                    //Declare map to store the values to be updated in the DB
                    ContentValues values = new ContentValues();
                    //Check for preloaded questions and that question changed
                    if(!this.isPreloadedQuestion && questionChanged && answerDbTransCompleted){
                        this.question.setValue(this.etNewItemField.getText().toString());
                        //Store the values to be updated in the DB
                        values.put(MainActivity.getIdColumn(),this.question.get_id());
                        values.put(MainActivity.getValueColumn(),this.question.getValue());
                    }else{
                        //In case the preloaded questions, if new answer was added, update the question record in the DB
                        values.put(MainActivity.getIdColumn(),this.question.get_id());
                        values.put(MainActivity.getAnswerIdColumn(),this.answer.get_id());
                    }//End of if statement to check for preloaded questions
                    questionDbTransCompleted = this.accountsDB.updateTable(MainActivity.getQuestionTable(),values);
                    //Update the new question in the DB
                    if(questionDbTransCompleted && answerDbTransCompleted){
                        //Go back to previous activity
                        intent.putExtra("questionID",this.question.get_id());
                        intent.putExtra("questionValue",this.question.getValue());
                        intent.putExtra("itemDeleted",false);
                        intent.putExtra("position",extras.getInt("position"));
                        result = true;
                        setResult(RESULT_OK, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The question "+ this.question.getValue() +" has been added into the DB through EditQuestionActivity class.");
                    }else{
                        //Throw an error for DB problem
                        //Prompt the user about a DB error while updating the user name
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarQuestionNotAdded),Toast.LENGTH_LONG,Gravity.CENTER);
                        Log.d("onOptionsItemSelected","The question "+ this.question.getValue()  +" has not been updated due to DB issue.");
                    }//End of if else statement to check for successful DB transactions
                }//End of if statement that checks UI validation was successful
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so caller activity can decide what to do if this is the case
                setResult(RESULT_CANCELED, null);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in EditQuestionActivity class.");
                finish();
                break;
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in EditQuestionActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement
        return result;
    }//End of onOptionsItemSelected method
}//End of EditQuestionActivity class