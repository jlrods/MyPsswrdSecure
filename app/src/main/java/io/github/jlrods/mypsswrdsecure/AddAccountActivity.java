package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class AddAccountActivity extends DisplayAccountActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateDispAcc", "Enter onCreate method in the a AddAccountActivity class.");
        //Set up current date into the Created date field
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        this.tvAccDateCreated.setText(new SimpleDateFormat(MainActivity.getDateFormat()).format(calendar.getTime()));
        Log.d("OnCreateDispAcc", "eXIT onCreate method in the AddAccountActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddAccountActivity class.");
        //Boolean to return method result
        boolean result = false;
        int answerID = -1;
        int questionID = -1;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","The save button was selected by user in AddAccountActivity class.");
                String accountName = this.etAccountName.getText().toString();
                long psswrdRenewDate = this.getPsswrdRenewDate();
                //Cursor accountCursor = this.accountsDB.getAccountCursorByName(accountName);
                Account account = null;
                //Check all data input is valid and correct (Account name not in use and valid password renew date, as most data come from dropdown menus which are already valid)
                //Check the name entered is not empty
                if(!accountName.equals("")){
                    //Check account name is available
                    if(!isAccNameUsed(accountName)){
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
                                        account = this.getItemFromUIData();
                                        //Check the grocery is not empty
                                        if(account != null){
                                            //Declare and instantiate to invalid value a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                                            int accountID = -1;
                                            //Call the addItem method and receive the id sent from method
                                            accountID    = accountsDB.addItem(account);
                                            //Check the id from the DB is valid and different than the dummy one.
                                            if(accountID != -1){
                                                account.set_id(accountID);
                                                //Call method to update data set displayed on the recycler view and display proper message after adding the grocery to the DB
                                                //Put extra info to transfer to the Main activity
                                                intent.putExtra("accountID",account.get_id());
                                                intent.putExtra("accountName",account.getName());
                                                setResult(RESULT_OK, intent);
                                                Log.d("onOptionsItemSelected","Set activity result to OK  on onOptionsItemSelected method in AddAccountActivity class.");
                                                finish();
                                            }//End of if statement to check the accountID is not -1
                                        }else{
                                            setResult(RESULT_CANCELED, intent);
                                            Log.d("onOptionsItemSelected","Set activity result to CANCELED  on onOptionsItemSelected method in AddAccountActivity class.");
                                        }//End of if else statement to check the account retrieved form UI isn't null
                                    }else{
                                        //In case the psswrd renew date is in the past, prompt the user
                                        MainActivity.displayToast(this,getResources().getString(R.string.accountRenewDateInPast),Toast.LENGTH_LONG, Gravity.CENTER);
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
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddAccountActivity class.");
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddAccountActivity class.");
        return result;
    }//End of onOptionsItemSelected

    //Method to check if Account name is in use
    private boolean isAccNameUsed(String accountName){
        Log.d("isAccNameUsed","Enter isAccNameUsed method in AddAccountActivity class.");
        boolean isAccNameUsed = false;
        //Cursor cursorListOfAccounts = this.accountsDB.getAccountsList();
        Cursor accountCursor = this.accountsDB.getAccountCursorByName(accountName);
        //Cryptographer cryptographer = MainActivity.getCryptographer();
        if(accountCursor != null){
            if(accountName.toLowerCase().equals(accountCursor.getString(1).toLowerCase())){
                isAccNameUsed = true;
                Log.d("isAccNameUsed","Account name "+ accountName+" found in account list by isAccNameUsed method in AddAccountActivity class.");
            }//End of if statement to check account name is in use
        }
        return isAccNameUsed;
    }//End of isAccNameUsed method
}//End of AddAccountActivity method
