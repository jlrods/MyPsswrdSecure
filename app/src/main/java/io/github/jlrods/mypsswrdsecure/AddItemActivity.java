package io.github.jlrods.mypsswrdsecure;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public abstract class AddItemActivity extends AppCompatActivity {
    //Attributes
    protected ImageView imgAddActivityIcon;
    protected TextView tvAddActivityTag;
    protected EditText etNewItemField;
    protected EditText etAnswer;
    //DB
    protected AccountsDB accountsDB;
    protected Cursor cursor;
    //Cryptographer object
    protected Cryptographer cryptographer = MainActivity.getCryptographer();
    //Floating action button to delete existing item
    protected FloatingActionButton fabDelete = null;

    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddItemActivity abstract class.");
        //Set layout for this activity
        setContentView(R.layout.activity_add_item);
        this.accountsDB = HomeFragment.getAccountsDB();
        //Find common view elements on the generic layout
        this.imgAddActivityIcon = (ImageView) findViewById(R.id.imgAddActivityIcon);
        this.tvAddActivityTag = (TextView) findViewById(R.id.tvAddActivityTag);
        this.etNewItemField = (EditText) findViewById(R.id.etNewItemField);
        this.fabDelete = findViewById(R.id.fabDelete);
//        this.fabDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Call the correct activity based on tab selection
//                int selectedTab = MainActivity.getCurrentTabID();
//                String title = "";
//                String message = "";
//                Object item = null;
//                switch (selectedTab){
//                    //Set up the variables required to delete the item
//                    case 1:
//                        title ="User Name";
//                        message = "Delete user name!";
//                        break;
//                    case 2:
//                        break;
//                    case 3:
//                        break;
//                }//End of switch statement
//
//                //Display AlertDialog to warn user it's about to delete an item
//                AlertDialog.Builder dialog = MainActivity.displayAlertDialog(view.getContext(),null,title,message,null );
//                dialog.setPositiveButton(R.string.dialog_OK, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//                //Set up the positive button to delete the item
//
//            }//End of on click method implementation
//        });//End of set on click listener method
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddItemActivity class.");
    }//End of onCreate method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the AddItemActivity class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the AddItemActivity class.");
        return true;
    }//End of onCreateOptionsMenu method

    // Method to check the menu item selected and execute the corresponding actions depending on the
    // item to be added (User name, password of question)
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected boolean isFieldNotEmpty(View view){
        Log.d("isFieldNotEmpty","Enter isFieldNotEmpty method in the AddItemActivity abstract class.");
        boolean isNotEmpty = false;
        if(!((EditText)view).getText().toString().equals("")){
            isNotEmpty = true;
        }
        Log.d("isFieldNotEmpty","Exit isFieldNotEmpty method in the AddItemActivity abstract class.");
        return isNotEmpty;
    }//End of checkForEmptyField

    //Method to validate UI data before adding or updating an item (user name, psswrd, question)
    protected boolean isDataValid(int type, int itemID){
        Log.d("isDataValid","Enter isDataValid method in the AddItemActivity abstract class.");
        //Declare and initialize variables to be used in the method
        //Boolean flag to be returned
        boolean isValid = false;
        //Boolean flag to check for no change in the input value
        boolean checkItemValueChange = false;
        //Boolean flag to check for preloaded questions
        boolean checkPreloadedQuestions = false;
        //Boolean flag to check for DB repeat value
        boolean checkDBValidation = true;
        //Boolean flag to ignore question is in DB
        boolean acceptSameQuestion = false;
        //Object used to check encrypted data against input value
        Object item = null;
        //String Id values to change text in the error messages Toasts
        int itemValueNotEnteredStringID = -1;
        int itemValueNotChangedStringID = -1;
        int itemValueExistsStringID = -1;
        //Text view object to be validated
        EditText etToBeValidated = this.etNewItemField;
        String dbValue = "";
        //Extract input value from edit text view
        String inputValue = etToBeValidated.getText().toString().trim();
        //Switch statement to check the type of object passed in as parameter. This way proper data will be set up
        switch(type){
            case 1:
                Log.d("isDataValid","Type 1 detected // Add User Name call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getUserNameByName(inputValue);
                itemValueNotEnteredStringID = R.string.userNameNotEntered;
                itemValueExistsStringID = R.string.snackBarUserExists;
                break;
            case 2:
                Log.d("isDataValid","Type 2 detected // Add Password call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getPsswrdByName(inputValue);
                itemValueNotEnteredStringID = R.string.psswrdNotEntered;
                itemValueExistsStringID = R.string.snackBarPsswrdExists;
                break;
            case 3:
                Log.d("isDataValid","Type 3 detected // Edit User Name call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getUserNameByName(inputValue);
                itemValueNotEnteredStringID = R.string.userNameNotEntered;
                itemValueExistsStringID = R.string.snackBarUserExists;
                if(itemID != -1){
                    checkItemValueChange = true;
                    item = (UserName) this.accountsDB.getUserNameByID(itemID);
                    itemValueNotChangedStringID = R.string.userNameNotChanged;
                }//End of if statement
                break;
            case 4:
                Log.d("isDataValid","Type 4 detected // Edit Password call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getPsswrdByName(inputValue);
                itemValueNotEnteredStringID = R.string.psswrdNotEntered;
                itemValueExistsStringID = R.string.snackBarPsswrdExists;
                if(itemID != -1){
                    checkItemValueChange = true;
                    item = (Psswrd) this.accountsDB.getPsswrdByID(itemID);
                    itemValueNotChangedStringID = R.string.psswrdNotChanged;
                }//End of if statement
                break;
            case 5:
                Log.d("isDataValid","Type 5 detected // Add Answer call passed into isDataValid method in the AddItemActivity abstract class.");
                checkDBValidation = false;
                etToBeValidated = this.etAnswer;
                itemValueNotEnteredStringID = R.string.answerNotEntered;
                break;
            case 6:
                Log.d("isDataValid","Type 6 detected // Add-Edit Question call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getQuestionByValue(inputValue);
                checkPreloadedQuestions = true;
                itemValueNotEnteredStringID = R.string.questionNotEntered;
                break;
            case 7:
                Log.d("isDataValid","Type 7 detected // Edit Question call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getQuestionByValue(inputValue);
                itemValueNotEnteredStringID = R.string.questionNotEntered;
                if(itemID != -1){
                    //Make decisions based on data coming from DB. If at least one value changed
                    item = (Question) this.accountsDB.getQuestionByID(itemID);
                    if(((Question)item).getValue().equals(inputValue)){
                        //Set up boolean to ignore the fact that the cursor is not null or count is 0
                        acceptSameQuestion = true;
                    }
                    checkPreloadedQuestions = true;
                }//End of if statement
                break;
            default:
                Log.d("isDataValid","No valid type passed into isDataValid method in the AddItemActivity abstract class.");
                break;
        }//End of switch statement
        //Start the validation checks
        //Check field isn't empty
        if(this.isFieldNotEmpty(etToBeValidated)){
            //Check unique field validation is required (Check the item isn't in the DB)
            //Check if changes in the value is required (where validation request comes from Edit Activities)
            if(checkItemValueChange){
                //Check current value in the edit field isn't the same already recorded on the DB
                if(item instanceof UserName){
                    dbValue = this.cryptographer.decryptText(((UserName)item).getValue(),new IvParameterSpec(((UserName)item).getIv())).trim();
                }else if(item instanceof Answer){
                    dbValue = this.cryptographer.decryptText(((Answer)item).getValue(),new IvParameterSpec(((Answer)item).getIv())).trim();
                }else if(item instanceof Question){
                    dbValue = ((Question) item).getValue();
                }
                if(!dbValue.equals(inputValue)){
                    //Check the input value isn't in the DB already
                    if(checkDBValidation){
                        if(this.cursor == null || this.cursor.getCount() ==0){
                            isValid = true;
                        }else{
                            //Prompt the user the user name input already exists in the list
                            MainActivity.displayToast(this,getResources().getString(itemValueExistsStringID),Toast.LENGTH_LONG,Gravity.CENTER);
                            Log.d("onOptionsItemSelected","The input value already exists in the DB.");
                        }
                    }else{
                        isValid = true;
                    }
                }else{
                    //Prompt the user the user name value has not changed
                    MainActivity.displayToast(this, getResources().getString(itemValueNotChangedStringID), Toast.LENGTH_SHORT, Gravity.CENTER);
                }//End of if else statement to check the user name changed
            }else if(checkDBValidation){
                if(checkPreloadedQuestions){
                    if(!isQuestionPreLoaded(new Question(inputValue)) || (this.cursor == null || this.cursor.getCount() ==0) || acceptSameQuestion){
                        isValid = true;
                    }else{
                        //Otherwise prompt the user the question already exists
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarQuestionExists),Toast.LENGTH_LONG,Gravity.CENTER);
                    }//End of if statement to check for preloaded questions
                }else if(this.cursor == null || this.cursor.getCount() ==0){
                    isValid = true;
                }else{
                    //Prompt the user the user name input already exists in the list
                    MainActivity.displayToast(this,getResources().getString(itemValueExistsStringID),Toast.LENGTH_LONG,Gravity.CENTER);
                    Log.d("onOptionsItemSelected","The input value already exists in the DB.");
                }//End of if else statement to check the item wasn't found onthe DB
            }else{
                //Data is valid by not being empty field (case of answer)
                isValid = true;
            }//End of if else statement to check the change in the input value is required (applicable when edit call is passed in)
        }else{
            //Display toast to prompt user the input field is empty
            MainActivity.displayToast(this,getResources().getString(itemValueNotEnteredStringID), Toast.LENGTH_LONG, Gravity.CENTER);
        }//End of if else statement to check the input field is not empty
        Log.d("isDataValid","Exit isDataValid method in the AddItemActivity abstract class.");
        return isValid;
    }//End of isDataValid method

    protected boolean isDataValid(int type){
        Log.d("isDataValid","Enter-Exit isDataValid overloaded method in the AddItemActivity abstract class.");
        return this.isDataValid(type,-1);
    }//End of isDataValid overloaded method

    protected boolean isQuestionPreLoaded(Question question, boolean isStringID){
        Log.d("isQuestionPreLoaded","Enter the isQuestionPreLoaded method in AddQuestionActivity class.");
        //Declare and instantiate variables to look for the questions
        Resources res = getResources();// Used to get preloaded sting data
        Cursor preLoadedQuestions = this.accountsDB.getPreLoadedQuestions(); // list of preloaded questions (stored with id in the DB and not the string value)
        //Booloean flag to be returned by method
        boolean isPreloaded = false;
        //While loop to iterate through the list of preloaded questions and check their text against parameter value
        while(!isPreloaded && preLoadedQuestions.moveToNext()){
            if(isStringID){
                if(question.getValue().equals(preLoadedQuestions.getString(1))){
                    isPreloaded = true;
                    Log.d("isQuestionPreLoaded","The question has been found as preloaded question in the AddQuestionActivity class.");
                }
            }else{
                if(question.getValue().toLowerCase().trim().equals(
                        res.getString(res.getIdentifier(preLoadedQuestions.getString(1),
                                "string",getBaseContext().getPackageName())).toLowerCase().trim())){
                    isPreloaded = true;
                    Log.d("isQuestionPreLoaded","The question has been found as preloaded question in the AddQuestionActivity class.");
                }
            }
        }//End of while loop to iterate through
        Log.d("isQuestionPreLoaded","Exit the isQuestionPreLoaded method in AddQuestionActivity class.");
        return isPreloaded;
    }//End of isQuestionPreLoaded

    protected boolean isQuestionPreLoaded(Question question){
        Log.d("isQuestionPreLoadedOL","Enter/Exit the isQuestionPreLoaded overloaded method in AddQuestionActivity class.");
        return this.isQuestionPreLoaded(question,false);
    }//End of isQuestionPreLoaded method

    //Inner class to handle the Delete item fab onClick event listener
    protected class FabOnClickEventHandler implements View.OnClickListener{
        //Attributes
        Object item;
        Cursor cursor;
        String alertDiaglogTitle ="";
        String alertDialogMessage ="";
        String itemDeletedName ="";
        String itemDeletedNameForIntent ="";

        FabOnClickEventHandler(Object item, String alertDiaglogTitle, String alertDialogMessage,
                               String itemDeletedName, String itemDeletedNameForIntent){
            this.item = item;
            this.alertDiaglogTitle = alertDiaglogTitle;
            this.alertDialogMessage = alertDialogMessage;
            this.itemDeletedName = itemDeletedName;
            this.itemDeletedNameForIntent = itemDeletedNameForIntent;
        }

        @Override
        //Method to handle click events on fab  button
        public void onClick(View v) {
            //Display AlertDialog to warn user it's about to delete an item
            AlertDialog.Builder dialog = MainActivity.displayAlertDialogNoInput(fabDelete.getContext(),alertDiaglogTitle,alertDialogMessage);
            dialog.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Check if item is an instance of question class so the answer can be deleted first
                    if(item instanceof Question){
                        accountsDB.deleteItem(((Question)item).getAnswer());
                    }
                    accountsDB.deleteItem(item);
                    //MainActivity.displayToast(getBaseContext(),itemDeletedToastText,Toast.LENGTH_LONG,Gravity.CENTER);
                    Intent intent = new Intent();
                    intent.putExtra("itemDeletedName",itemDeletedName);
                    intent.putExtra(itemDeletedNameForIntent,true);
                    setResult(RESULT_OK,intent);
                    finish();
                }//End of onClick method
            });//End of 
            dialog.show();
        }
    }

}//End of AddItemActivity abstract class
