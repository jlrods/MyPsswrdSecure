package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AddAccountActivity extends DisplayAccountActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateDispAcc", "Enter onCreate method in the a AddAccountActivity class.");
        //Set up the Created date
        this.setDateText(this.tvAccDateCreated,System.currentTimeMillis());
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //Extract extra data from owner Activity
        this.extras = getIntent().getExtras();
        //Retrieve current category so the correct category can be selected on the category spinner
        int currentCategoryID = extras.getInt("category");
        if(currentCategoryID > 0){
            spCategory.setSelection(MainActivity.getCategoryPositionByID(currentCategoryID)-MainActivity.getIndexToGetLastTaskListItem());
        }
        //Set activity title
        getSupportActionBar().setTitle(R.string.addAccTitle);
        Log.d("OnCreateDispAcc", "eXIT onCreate method in the AddAccountActivity class.");
    }//End of onCreate method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the DisplayAccountActivity abstract class.");
        menu.getItem(0).setVisible(false);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the DisplayAccountActivity abstract class.");
        return true;
    }//End of onCreateOptionsMenu method


    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddAccountActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","The save button was selected by user in AddAccountActivity class.");
                String accountName = this.etAccountName.getText().toString();
                long psswrdRenewDate = this.getPsswrdRenewDate();
                //Check all data input is valid and correct (Account name not in use and valid password renew date, as most data come from dropdown menus which are already valid)
                //Check the name entered is not empty
                if(!accountName.equals("")){
                    //Check account name is available but first check if it contains apostrophe so the sql query contains scape character
                    if(accountName.contains("'")){
                        accountName = accountsDB.includeApostropheEscapeChar(accountName);
                    }//End of if to check the name contains apostrophe
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
                                        this.account = this.getAccountFromUIData();
                                        //Check the grocery is not empty
                                        if(this.account != null){
                                            //Declare and instantiate to invalid value a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                                            int accountID = -1;
                                            //Call method to add new icon into the DB if icon isn't the app logo or comes from the app resources
                                            this.isAddIconRequired(this.account);
                                            //Call the addItem method and receive the id sent from method
                                            accountID    = accountsDB.addItem(this.account);
                                            //Check the id from the DB is valid and different than the dummy one.
                                            if(accountID != -1){
                                                this.account.set_id(accountID);
                                                //Call method to update data set displayed on the recycler view and display proper message after adding the grocery to the DB
                                                //Put extra info to transfer to the Main activity
                                                intent.putExtra("accountID",this.account.get_id());
                                                intent.putExtra("accountName",this.account.getName());
                                                //Put remainder time for logout so MainActivity can continue the count down
                                                intent.putExtra("timeOutRemainder",logOutTimeRemainder);
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
            case R.id.action_logout:
                //Call method to throw LoginActivity and clear activity stack.
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in AddAccountActivity class.");
                MainActivity.logout(this);
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddAccountActivity class.");
        return result;
    }//End of onOptionsItemSelected


    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","Enter/Exit the onActivityResult method in the AddAccountActivity class.");
    }//End of onActivityResult method


}//End of AddAccountActivity method
