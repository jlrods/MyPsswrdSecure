package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import javax.crypto.spec.IvParameterSpec;

public class EditAccountActivity extends DisplayAccountActivity {
    //Attribute definition
    private Bundle extras;
    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditAcc","Enter onCreate method in the EditAccountActivity abstract class.");
        //Extract extra data from owner Activity
        this.extras = getIntent().getExtras();
        //Extract account details by passing in the _id attribute stored in the extras
        this.account = Account.extractAccount(this.accountsDB.getAccountCursorByID(this.extras.getInt("_id")));
        //Set the UI data as per the account just extracted
        //Set Account name
        this.etAccountName.setText(this.account.getName());
        //Set account logo
        if(this.account.getIcon().getLocation().equals(MainActivity.getRESOURCES())){
            //Extract all the logos from the app resources
            //Call static method that will get the resource by passing in it's name and set it as the resource image of the ImageView passed in
            MainActivity.setAccountLogoImage(this.imgAccLogo,getBaseContext(),account.getIcon().getName());
            this.logo = this.accountsDB.getIconByName(account.getIcon().getName());
        }else if(this.account.getIcon().getLocation().equals(String.valueOf(R.mipmap.ic_my_psswrd_secure))){
            this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure);
        }//End of if else statement to check if logo comes from app resources
        //Set up the category spinner to display the category assigned to the account by calling method that gets the cursor position by passing in it's text value
        this.spCategory.setSelection(this.getItemPositionInSpinner(this.cursorCategory,this.account.getCategory().get_id()));
        //Set up the user name spinner to display the user name assigned to the account by calling method that gets the cursor position by passing in it's text value
        this.spAccUserName.setSelection(this.getItemPositionInSpinner(this.cursorUserName,this.account.getUserName().get_id()));
        //Set up the user name spinner to display the user name assigned to the account by calling method that gets the cursor position by passing in it's text value
        this.spAccPsswrd.setSelection(this.getItemPositionInSpinner(this.cursorPsswrd,this.account.getPsswrd().get_id()));
        //Set up the security question list if applicable
        if(this.account.getQuestionList() != null){
            //For each question in the list, call method that will add the question to the security question list
            for(int i=0;i<this.account.getQuestionList().getSize();i++){
                Question questionToBeAdded = this.account.getQuestionList().getQuestions().get(i);
                //Find the question position in the available question adapter to pass it as parameter of the addQuestionToSecList method
                int questionPositionInAvailableList = this.accountsDB.findItemPositionInCursor(this.cursorListOfQuestionsAvailable,questionToBeAdded.get_id());
                this.addQuestionToSecList(questionPositionInAvailableList);
            }//End of for loop to iterate through question list
        }//End of if statement to check the question list isn't null
        //Set up the is favorite image and text based on the isFavorite attribute of the account object
        if(this.account.isFavorite()){
            this.toggleIsFavorite();
        }//End of if statement to check the isFav attribute
        //Set up the Created date
        this.setDateText(this.tvAccDateCreated,this.account.getDateCreated());
        //Set up the password renew date
        if(this.account.getDateChange()!= 0){
            this.cbHasToBeChanged.setChecked(true);
            this.setDateText(this.tvAccDateRenewValue,this.account.getDateChange());
            this.showChangeDateLayout( this.cbHasToBeChanged.isChecked());
        }//End of if statement that checks the renew password date is to be updated on UI
        Log.d("OnCreateEditAcc","Exit onCreate method in the EditAccountActivity abstract class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in EditAccountActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","The save button was selected by user in EditAccountActivity class.");
                String accountName = this.etAccountName.getText().toString();
                long psswrdRenewDate = this.getPsswrdRenewDate();
                //Check all data input is valid and correct (Account name not in use and valid password renew date, as most data come from dropdown menus which are already valid)
                //Check the name entered is not empty
                if(!accountName.equals("")){
                    //Check account name is available
                    if(!isAccNameUsed(accountName,this.account.get_id())){
                        //If Account name not in use
                        //Check a category has been selected
                        if(this.spCategory.getSelectedItemPosition() >= 0){
                            //Check a user name has been selected
                            if(this.spAccUserName.getSelectedItemPosition() >=0 ){
                                //Check a password has been selected
                                if(this.spAccPsswrd.getSelectedItemPosition() >= 0){
                                    //Check the renew date is valid
                                    if(this.isValidRenewDate(psswrdRenewDate)){
                                        //Create a new account based on data input by user
                                        Account newAccount = this.getItemFromUIData();
                                        //Check the grocery is not empty
                                        if(newAccount != null){
                                            newAccount.set_id(this.extras.getInt("_id"));
                                            //Check at least one value has change on the UI, otherwise prompt the user
                                            if(this.account != null){
                                                //Check at least one value has change on the UI, otherwise prompt the user
                                                if(!(this.account.getName().equals(newAccount.getName())
                                                        && this.account.getIcon().get_id() == newAccount.getIcon().get_id()
                                                        && this.account.getCategory().get_id() == newAccount.getCategory().get_id()
                                                        && this.account.getUserName().get_id() == newAccount.getUserName().get_id()
                                                        && this.account.getPsswrd().get_id() == newAccount.getPsswrd().get_id()
                                                        //@Fixme: When system calls Extract Account from UI method, the question is being recorded on the DB with new ID
                                                        //@Fixme: improve the way question lists are checked and recorded on DB
                                                        && this.isQuestionListTheSame(this.account.getQuestionList(),newAccount.getQuestionList())
                                                        && this.account.isFavorite() == newAccount.isFavorite()
                                                        //Date created is not checked as this value is not editable, so it must remain the same in the DB
                                                        //&& this.account.getDateCreated() == newAccount.getDateCreated()
                                                        && this.account.getDateChange() == newAccount.getDateChange())) {
                                                    ContentValues values = new ContentValues();
                                                    values.put("_id",newAccount.get_id());
                                                    values.put("Name",newAccount.getName());
                                                    values.put("CategoryID",newAccount.getCategory().get_id());
                                                    values.put("UserNameID",newAccount.getUserName().get_id());
                                                    values.put("PsswrdID",newAccount.getPsswrd().get_id());
                                                    //@Fixme:
                                                    if(!this.isQuestionListTheSame(this.account.getQuestionList(),newAccount.getQuestionList())){
                                                        if(this.account.getQuestionList() == null && newAccount.getQuestionList() != null){
                                                            values.put("QuestionListID",newAccount.getQuestionList().get_id());
                                                        }else if(this.account.getQuestionList() != null && newAccount.getQuestionList() == null){
                                                            values.put("QuestionListID","NULL");
                                                        }else if(this.account.getQuestionList().get_id() != newAccount.getQuestionList().get_id()){
                                                            values.put("QuestionListID",newAccount.getQuestionList().get_id());
                                                        }//End of if else statement to check the question list states
                                                    }//End of if statement to check the question list are the same

                                                    values.put("IconID",newAccount.getIcon().get_id());
                                                    values.put("IsFavorite",newAccount.isFavorite());
                                                    //values.put("DateCreated",this.account.getDateCreated());
                                                    values.put("DateChange",newAccount.getDateChange());
                                                    if(result = this.accountsDB.updateTable(MainActivity.getAccountsTable(),values)){
                                                        //Call method to update data set displayed on the recycler view and display proper message after adding the grocery to the DB
                                                        //Put extra info to transfer to the Main activity
                                                        intent.putExtra("accountID",newAccount.get_id());
                                                        intent.putExtra("accountName",newAccount.getName());
                                                        setResult(RESULT_OK, intent);
                                                        Log.d("onOptionsItemSelected","Set activity result to OK  on onOptionsItemSelected method in EditAccountActivity class.");
                                                        finish();
                                                    }else{
                                                        //Report DB error when updating the record

                                                    }//End of if statement to check the accountID is not -1
                                                }else{
                                                    //Prompt the user no change has been done on the UI data
                                                    MainActivity.displayToast(this,getResources().getString(R.string.accountNotChanged),Toast.LENGTH_SHORT,Gravity.CENTER);
                                                }//End of if else statement to check the UI data has changed, at least one field
                                            }//End of if statement to check the old account isn't null
                                            //Check the id from the DB is valid and different than the dummy one.
                                        }else{
                                            setResult(RESULT_CANCELED, intent);
                                            Log.d("onOptionsItemSelected","Set activity result to CANCELED  on onOptionsItemSelected method in EditAccountActivity class.");
                                        }//End of if else statement to check the account retrieved form UI isn't null
                                    }else{
                                        //In case the psswrd renew date is in the past, prompt the user
                                        MainActivity.displayToast(this,getResources().getString(R.string.accountRenewDateInPast), Toast.LENGTH_LONG, Gravity.CENTER);
                                    }//End of if else statement to check input validity (renew date)
                                }else{
                                    //Display proper error message for no password selected
                                    MainActivity.displayToast(this,getResources().getString(R.string.accountNoPsswrdSelected),Toast.LENGTH_LONG, Gravity.CENTER);
                                }//End of if else statement to check password has been selected
                            }else{
                                //Display proper error message for no user name selected
                                MainActivity.displayToast(this,getResources().getString(R.string.accountNoUserSelected),Toast.LENGTH_LONG, Gravity.CENTER);
                            }//End of if else statement to check a user name has been selected
                        }else{
                            //Display proper error message for no category selected
                            MainActivity.displayToast(this,getResources().getString(R.string.accountNoCatSelected),Toast.LENGTH_LONG, Gravity.CENTER);
                        }//End of if else statement to check a category has been selected
                    }else{
                        //In case the account name is being used, prompt the user
                        MainActivity.displayToast(this,getResources().getString(R.string.accountNameInUse),Toast.LENGTH_LONG, Gravity.CENTER);
                    }// End of if else statement to check input validity (account name)
                }else{
                    //If name left in blank, prompt user proper message
                    MainActivity.displayToast(this,getResources().getString(R.string.accountEmptyName),Toast.LENGTH_LONG, Gravity.CENTER);
                }//End of if else statement to check account name was not left in blank
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                //Go back to previous activity
                finish();
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in EditAccountActivity class.");
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in EditAccountActivity class.");
        return result;
    }//End of onOptionsItemSelected method

    //Method to check two accounts have different question list (including null list)
    private boolean isQuestionListTheSame(QuestionList list1, QuestionList list2){
        Log.d("isQuestionListTheSame","Enter the isQuestionListTheSame method in EditAccountActivity class.");
        //Declare boolean flag to be returned by method
        boolean isTheSame;
        //Check both lists are null
        if(list1 == null && list2 == null){
            isTheSame = true;
        }else if(list1 == null && list2 != null){
            //If list1 is null and the other doesn't, they aren't the same
            isTheSame = false;
        }else if(list1 != null && list2 == null){
            //If list2 is null and the other doesn't, they aren't the same
            isTheSame = false;
        }else if(list1.get_id() == list2.get_id()){
            //If both list hold the same id they are the same
            isTheSame = true;
        }else{
            //Any other case return false
            isTheSame = false;
        }//End of if else statements to check list status and ids
        Log.d("isQuestionListTheSame","Exit the isQuestionListTheSame method in EditAccountActivity class.");
        return isTheSame;
    }//End of isQuestionListTheSame method
}//End of EditAccountActivity class
