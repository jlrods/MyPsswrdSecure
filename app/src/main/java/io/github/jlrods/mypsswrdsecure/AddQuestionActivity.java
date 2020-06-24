package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class AddQuestionActivity extends AppCompatActivity {
    //Attributes
    private EditText etQuestion;
    private EditText etAnswer;
    //DB
    private AccountsDB accounts;
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
        this.etQuestion = (EditText) findViewById(R.id.etAccountName);
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
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
//                intent.putExtra("selectedImgLocation",this.selectedIcon.getLocation());
//                intent.putExtra("selectedImgPosition",this.selectedPosition);
//                setResult(RESULT_OK, intent);
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                break;
            case R.id.select_logo_cancel:
//                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
//                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddQuestionActivity class.");
        finish();
        return result;
    }//End of onOptionsItemSelected
}//End of AddQuestionActivity
