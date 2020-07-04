package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class AddQuestionActivity extends AppCompatActivity {
    //Attributes
    private EditText etQuestion;
    private EditText etAnswer;
    //DB
    private AccountsDB accounts;

    private Cursor cursorQuestion = null;
    private Cursor cursorAnswer = null;
    private Question question = null;
    private Answer answer = null;


    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddQuestionActivity abstract class.");
        //Set layout for this activity
        setContentView(R.layout.activity_add_question);
        this.accounts = HomeFragment.getAccounts();
        //Extract extra data from Bundle object
        //extras = getIntent().getExtras();
        this.etQuestion = (EditText) findViewById(R.id.etQuestion);
        this.etAnswer = (EditText) findViewById(R.id.etAnswer);
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddQuestionActivity abstract class.");
    }//End of onCreate method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the AddQuestionActivity abstract class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_select_logo, menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the AddQuestionActivity abstract class.");
        return true;
    }//End of onCreateOptionsMenu method

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
                Log.d("onOptionsItemSelected","The save button was selected by user in AddQuestionActivity class.");
                //Create question and answer objects
                answer = new Answer(this.etAnswer.getText().toString().trim());
                question = new Question(this.etQuestion.getText().toString().trim(),answer);
                //Check the question isn't in the DB already
                //Check the question isn't one of the pre-loaded questions
                if(!isQuestionPreLoaded(question)){
                    cursorQuestion = accounts.getQuestionByValue(question.getValue());
                    if(cursorQuestion == null  || cursorQuestion.getCount() == 0){
                        //If question isn't in the DB, insert the answer in the DB, then insert then insert the question
                        answerID = accounts.addItem(answer);
                        if(answerID > 0){
                            answer.set_id(answerID);
                        }
                        questionID = accounts.addItem(question);
                        if(questionID > 0){
                            question.set_id(questionID);
                        }
                    }else{
                        //Otherwise prompt the user the question already exists
                        Toast toast = Toast.makeText(this,R.string.snackBarQuestionExists,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                }else{
                    //Otherwise prompt the user the question already exists
                    Toast toast = Toast.makeText(this,R.string.snackBarQuestionExists,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                intent.putExtra("answerID",this.answer.get_id());
                intent.putExtra("questionID",this.question.get_id());
                setResult(RESULT_OK, intent);
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                break;
            case R.id.select_logo_cancel:
               //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddQuestionActivity class.");
        finish();
        return result;
    }//End of onOptionsItemSelected

    private boolean isQuestionPreLoaded(Question question){
        Log.d("isQuestionPreLoaded","Enter the isQuestionPreLoaded method in AddQuestionActivity class.");
        //Declare and instantiate variables to look for the questions
        Resources res = getResources();// Used to get preloaded sting data
        Cursor preLoadedQuestions = accounts.getPreLoadedQuestions(); // list of preloaded questions (stored with id in the DB and not the string value)
        //Booloean flag to be returned by method
        boolean isPreloaded = false;
        //While loop to iterate through the list of preloaded qestions and check their text against parameter value
        while(!isPreloaded && preLoadedQuestions.moveToNext()){
            if(question.getValue().toLowerCase().trim().equals(
                    res.getString(res.getIdentifier(preLoadedQuestions.getString(1),
                            "string",getBaseContext().getOpPackageName())).toLowerCase().trim())){
                isPreloaded = true;
                Log.d("isQuestionPreLoaded","The question has been found as preloaded quesiton in the AddQuestionActivity class.");
            }
        }//End of while loop to iterate through
        Log.d("isQuestionPreLoaded","Exit the isQuestionPreLoaded method in AddQuestionActivity class.");
        return isPreloaded;
    }//End of isQuestionPreLoaded
}//End of AddQuestionActivity
