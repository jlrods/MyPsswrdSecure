package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
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

import java.util.ArrayList;

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
    protected LogOutTimer logOutTimer;
    protected long logOutTime;
    Bundle extras;

    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddItemActivity abstract class.");
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Set language as per preferences
        MainActivity.setAppLanguage(this);
        this.extras = getIntent().getExtras();
        if(MainActivity.isIsLogOutActive()){
            logOutTime = this.extras.getLong("timeOutRemainder");
            logOutTimer = new LogOutTimer(logOutTime, 250,this);
            logOutTimer.start();
        }
        //Set layout for this activity
        setContentView(R.layout.activity_add_item);
        this.accountsDB = HomeFragment.getAccountsDB();
        //Find common view elements on the generic layout
        this.imgAddActivityIcon = (ImageView) findViewById(R.id.imgAddActivityIcon);
        this.tvAddActivityTag = (TextView) findViewById(R.id.tvAddActivityTag);
        this.etNewItemField = (EditText) findViewById(R.id.etNewItemField);
        this.fabDelete = findViewById(R.id.fabDelete);
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
            case 8:
                Log.d("isDataValid","Type 8 detected // Add Category call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getCategoryByName(inputValue);
                itemValueNotEnteredStringID = R.string.catNotEntered;
                itemValueExistsStringID = R.string.catExists;
                break;
            case 9:
                Log.d("isDataValid","Type 9 detected // Edit Category call passed into isDataValid method in the AddItemActivity abstract class.");
                this.cursor = accountsDB.getCategoryByName(inputValue);
                itemValueNotEnteredStringID = R.string.catNotEntered;
                itemValueExistsStringID = R.string.catExists;
                if(itemID != -1){
                    checkItemValueChange = true;
                    item = (Category) this.accountsDB.getCategoryByID(itemID);
                    itemValueNotChangedStringID = R.string.catNotChanged;
                }//End of if statement
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
                }else if(item instanceof Category){
                    dbValue = ((Category) item).getName();
                    //nt dbIconID = ((Category) item).getIcon().get_id();
                }
                if(!dbValue.equals(inputValue)){
                    //Check the input value isn't in the DB already
                    if(checkDBValidation){
                        if(this.cursor == null || this.cursor.getCount() ==0){
                            //Check Home and Favorites categories when adding a new category
                            if(type==8 || type == 9){
                                if(!inputValue.toLowerCase().equals(MainActivity.getHomeCategory().getName().toLowerCase()) && !inputValue.toLowerCase().equals(MainActivity.getFavCategory().getName().toLowerCase())){
                                    isValid = true;
                                }else{
                                    //Prompt the user the category input already exists in the list
                                    MainActivity.displayToast(this,getResources().getString(itemValueExistsStringID),Toast.LENGTH_LONG,Gravity.CENTER);
                                    Log.d("onOptionsItemSelected","The input value already exists in the DB.");
                                }
                            }else{
                                isValid = true;
                            }
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
                    //Check Home and Favorites categories when adding a new category
                    if(type==8 || type == 9){
                        if((!inputValue.toLowerCase().equals(MainActivity.getHomeCategory().getName().toLowerCase())
                                && !inputValue.toLowerCase().equals(MainActivity.getFavCategory().getName().toLowerCase()))){
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
        //Cursor cursor;
        String alertDiaglogTitle ="";
        String alertDialogMessage ="";
        String itemDeletedName ="";
        String itemDeletedNameForIntent ="";
        int itemPosition= -1;

        FabOnClickEventHandler(Object item, String alertDiaglogTitle, String alertDialogMessage,
                               String itemDeletedName, String itemDeletedNameForIntent,int itemPosition){
            Log.d("FabOnClickEventHandler","Enter the FabOnClickEventHandler constructor method in FabOnClickEventHandler inner class of the AddQuestionActivity abstract class.");
            this.item = item;
            this.alertDiaglogTitle = alertDiaglogTitle;
            this.alertDialogMessage = alertDialogMessage;
            this.itemDeletedName = itemDeletedName;
            this.itemDeletedNameForIntent = itemDeletedNameForIntent;
            this.itemPosition = itemPosition;
            Log.d("FabOnClickEventHandler","Exit the FabOnClickEventHandler constructor method in FabOnClickEventHandler inner class of the AddQuestionActivity abstract class.");
        }//End of constructor method

        @Override
        //Method to handle click events on fab  button
        public void onClick(View v) {
            Log.d("FabOnClickEHandOnClick","Enter the onClick method in FabOnClickEventHandler inner class of the AddQuestionActivity abstract class.");
            int timesUsed = 0;
            final int[] timesUsedList = {timesUsed};
            int itemID = -1;
            final int[] itemIDs = {itemID};
            String itemType = "";
            ArrayList listOfAccountsUsingTheItem = null;
            final ArrayList[] listOfAccountsUsingTheItemArray = {listOfAccountsUsingTheItem};
            Cursor listOfQuestionListsUsingTheQuestion = null;
            final Cursor[] listOfQuestionListsUsingTheQuestionArray = {listOfQuestionListsUsingTheQuestion};
            //Check if the item is being used in any account
            //If item's being used, alert the user where to find it by modifying the alertDialogMessage text
            if(this.item instanceof Psswrd){
                itemID = ((Psswrd)item).get_id();
                timesUsed = accountsDB.getTimesUsedPsswrd(itemID);
                itemType = MainActivity.getPASSWORD();
            }else if(this.item instanceof UserName){
                itemID = ((UserName)item).get_id();
                timesUsed = accountsDB.getTimesUsedUserName(itemID);
                itemType = MainActivity.getUserName();
            }else if(this.item instanceof Question){
                itemID = ((Question)item).get_id();
                timesUsed = accountsDB.getTimesUsedQuestion(itemID);
                itemType = MainActivity.getQUESTION();
                listOfQuestionListsUsingTheQuestion = accountsDB.getQuestionAssignmentCursorFor1QuestionID(itemID);
                listOfQuestionListsUsingTheQuestionArray[0] = listOfQuestionListsUsingTheQuestion;
            }//End of if else statement to check type of object to delete and set up variables
            itemIDs[0] = itemID;
            //Check the number of times the item is being used. If greater than 0, then prompt the user the accounts that will get affected
            if(timesUsed != 0){
                //Get the accounts name where the item is being used
                listOfAccountsUsingTheItem = accountsDB.getAccountsIDListUsingItemWithID(itemType,itemID);
                listOfAccountsUsingTheItemArray[0] = listOfAccountsUsingTheItem;
                if(timesUsed > 1){
                    alertDialogMessage += "\nThe "+ itemType+ " is being used " + timesUsed + " times.\nThe item will be removed from the following accounts:";
                }else{
                    alertDialogMessage += "\nThe "+ itemType+ " is being used " + timesUsed + " time.\nThe item will be removed from the following account:";
                }
                //Concat the accounts names to the warning text
                for(int i=0;i < listOfAccountsUsingTheItem.size();i++){
                    Account account = accountsDB.getAccountByID((int)listOfAccountsUsingTheItem.get(i));
                    if( account != null){
                        alertDialogMessage += "\n❎ "+ account.getName();
                    }//End of if statement to check account isn't null
                }//End of for loop to include every account name in the warning message
            }//End of if statement to check the the item is being used
            //Otherwise, just alert user is about to delete an item and display the alertDialogMessage
            //Display AlertDialog to warn user it's about to delete an item
            AlertDialog.Builder dialog = MainActivity.displayAlertDialogNoInput(fabDelete.getContext(),alertDiaglogTitle,alertDialogMessage);
            dialog.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset dialog alert text so message is not duplicated when cancelled alert dialog box
                    alertDialogMessage = "";
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //Reset dialog alert text so message is not duplicated when exit alert dialog box without pressing any button
                    alertDialogMessage = "";
                }
            });
            dialog.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Update all the DB rows where the item will be removed from and set item value as null
                    //Use a ContentValues object to hold table columns and respective values to be updated in the DB
                    ContentValues values;
                    //Check if item to be deleted is a question
                    if(item instanceof Question){
                        //If it's a question the item to be deleted, the question should be removed from QuestionList it's part of, not from the account directly
                        //only if the question list has only 1 question (the one to be deleted) in that case the account column for QuestionListID must be set to null
                        //First of, iterate through all the lists affected  by removing the question
                        while(listOfQuestionListsUsingTheQuestionArray[0].moveToNext()){
                            //Record current question assignment ID and current questionList ID for later use
                            int questionAssignmentID = listOfQuestionListsUsingTheQuestionArray[0].getInt(0);
                            int questionListID = listOfQuestionListsUsingTheQuestionArray[0].getInt(1);
                            //Get a question list object created based on data from DB corresponding to the current question list ID
                            QuestionList questionListUsingTheQuestionToBeDeleted =accountsDB.getQuestionListById(questionListID);
                            //Get a cursor object to hold the list of accounts that use the current question list during the while loop
                            Cursor listOfAccountsUsingTheQuestionList = accountsDB.getAccountsWithSpecifcValue(MainActivity.getQuestionListIdColumn(),questionListID);
                            //Declare and initialize boolean flag to select correct data when only 1 QuestionAssignment is required to be deleted from the DB.
                            //This is true when the modified question list is reused after deleting the selected question
                            boolean only1QuestionAssignmentToDelete = false;

                            //When removing a question from a question list there are there main possibilities:
                            //1.- The question to be removed is not the only question in the list
                            //2.- The question to be removed is the only question in the list

                            if(questionListUsingTheQuestionToBeDeleted != null){
                                if(questionListUsingTheQuestionToBeDeleted.getSize() > 1){
                                    //Path 1.- If the question to be removed is the only question in the list (Path 1.-) the Accounts holding that list should be updated with Null QuestionListID
                                    //Now call method that makes QuestionList re-structuring and updates the QuestionList row in the DB. The question assignment update is done later down below
                                    //This method returns an int value, any value greater than -1 means the list ID resulting from deleting the desired question (including the current list which can be reused)
                                    //Therefore, all accounts holding the list with the question to be removed must be updated to the resulting list (which already exists in DB)
                                    //Call re-structuring method and pass in the current question list and the ID of the question to be deleted
                                    int re_structuredQuestionListID = accountsDB.re_sctructureQuestionList(questionListUsingTheQuestionToBeDeleted,itemIDs[0]);

                                    //This leads to two possibilities:
                                    //1.1.- The id from restructuring the list is not -1, so the list exists and it's not, of course, the current list ID
                                    //1.2.- The id from restructuring the list is -1, which means the resulting list does not exists, therefore the current list ID can be recycled

                                    if(re_structuredQuestionListID!=-1 && re_structuredQuestionListID!= questionListID){
                                        //Path 1.1.- Iterate through the list of accounts that have the current QuestionListID associated to them
                                        //And update the new QuesitonListID in each account
                                        while(listOfAccountsUsingTheQuestionList.moveToNext()){
                                            values = new ContentValues();
                                            values.put(MainActivity.getIdColumn(),listOfAccountsUsingTheQuestionList.getInt(0));
                                            values.put(MainActivity.getQuestionListIdColumn(),re_structuredQuestionListID);
                                            accountsDB.updateTable(MainActivity.getAccountsTable(),values);
                                        }//End of while loop to update accounts holding current question list
                                    }else{
                                        //Path 1.2.- The re-structured list does not exist in DB, therefore the current question list ID can be reused and not deleted.
                                        // In this case, only set boolean flag that indicates only 1 QuestionAssignment is required to be deleted from the DB.
                                        //If re-structured list isn't in the DB, it's necessary to distinguish between that or making a null assignment to the
                                        only1QuestionAssignmentToDelete = true;
                                    }//End of if statement that checks the 'question list got re-structured
                                }else{
                                    //Path 2.- The question to be removed is the only question in the list. Update the accounts holding the QuestionList that
                                    // holds the question to be deleted
                                    //Iterate through the list of accounts
                                    while(listOfAccountsUsingTheQuestionList.moveToNext()){
                                        values = new ContentValues();
                                        values.put(MainActivity.getIdColumn(),listOfAccountsUsingTheQuestionList.getInt(0));
                                        values.put(MainActivity.getQuestionListIdColumn(),"(null)");
                                        accountsDB.updateTable(MainActivity.getAccountsTable(),values);
                                    }//End of while loop that iterates through list of Accounts holding the questionList therefore the question to be deleted
                                }//End of if else statement to check the current questionList is made of more than 1 question

                                String table ="";
                                String column ="";
                                if(only1QuestionAssignmentToDelete){
                                    //Path 1.2.-
                                    //Remove the QUESTIONASSIGMENT row that links the questionListID and the _id of the question to be deleted
                                    table = MainActivity.getQuestionassignmentTable();
                                    column = MainActivity.getIdColumn();
                                    accountsDB.deleteRowFromTable(table,column,questionAssignmentID);
                                }else{
                                    //Path 1.1.- and Path 2.- merge here and follow same steps.
                                    table = MainActivity.getQuestionassignmentTable();
                                    column = MainActivity.getQuestionListIdColumn();
                                    accountsDB.deleteRowFromTable(table,column,questionListID);
                                    //Remove the question list
                                    //Question list, it was just deleted above
                                    accountsDB.deleteItem(questionListUsingTheQuestionToBeDeleted);
                                }//End of if else statement that checks the boolean flag that indicates only 1 QuestionAssignment is required to be deleted from the DB.
                            }//End of if secure if statement that checks the current questionList object isn't null before accessing it's size method
                        }//End of while loop that iterates through list of QuestionLists that hold the question to be deleted
                    }else{
                        //Path used for items to be deleted other that questions (User names and passwords)
                        //Check the number of times the user or password are being used. If greater than 0, proceed to update each account using the object
                        //Otherwise, ignore this section
                        if(timesUsedList[0] != 0){
                            for(int i=0; i < listOfAccountsUsingTheItemArray[0].size();i++){
                                //Rest the values object every time a new iteration begins
                                values = new ContentValues();
                                //Assign the _id column to the current account id value
                                values.put(MainActivity.getIdColumn(),(int)listOfAccountsUsingTheItemArray[0].get(i));
                                //Check if item is an instance of question class so the answer can be deleted first
                                if(item instanceof Psswrd){
                                    //Set password column to null
                                    values.put(MainActivity.getPsswrdIdColumn(),"NULL");
                                }else if(item instanceof UserName){
                                    //Set user name column to
                                    values.put(MainActivity.getUserNameIdColumn(),"NULL");
                                }//End of if else statement to check what type of object the item is
                                //Update the current account and set item column to nll
                                accountsDB.updateTable(MainActivity.getAccountsTable(),values);
                            }//End of for loop to iterate through list of Accounts holding the item to be deleted
                        }
                        //If item to delete isn't a question, start iterating through list of accounts that hold the item to be deleted

                    }//End of if else statement to check if item is a Question or any type of object

                    //All different paths merge here, final deletion of the item and preparation of result transfer to caller method
                    //Request delete item on DB and handle bad result
                    if(accountsDB.deleteItem(item)){
                        //MainActivity.displayToast(getBaseContext(),itemDeletedToastText,Toast.LENGTH_LONG,Gravity.CENTER);
                        Intent intent = new Intent();
                        intent.putExtra("itemDeletedName",itemDeletedName);
                        intent.putExtra(itemDeletedNameForIntent,true);
                        String itemDeletedType = "";
                        //Check the type of item deleted and associate it's type accordingly
                        if(item instanceof Psswrd){
                            itemDeletedType = "psswrdValue";
                        }else if(item instanceof UserName){
                            itemDeletedType = "userNameValue";
                        }else if(item instanceof Question){
                            itemDeletedType = "questionValue";
                        }
                        intent.putExtra("itemDeletedType",itemDeletedType);
                        intent.putExtra("position",itemPosition);
                        setResult(RESULT_OK,intent);
                        finish();
                    }else{
                        MainActivity.displayToast(getBaseContext(),getString(R.string.itemNotDeletedMssg),Toast.LENGTH_SHORT,Gravity.CENTER);
                    }

                }//End of onClick method
            });//End of
            dialog.show();
            Log.d("FabOnClickEHandOnClick","Enter the onClick method in FabOnClickEventHandler inner class of the AddQuestionActivity abstract class.");
        }//End of the onClick method
    }//End of FabOnClickEventHandler inner class
}//End of AddItemActivity abstract class
