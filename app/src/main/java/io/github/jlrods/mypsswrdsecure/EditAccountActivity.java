package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;


public class EditAccountActivity extends DisplayAccountActivity{
    //Attribute definition
    final private Intent[] intents = {new Intent()};
    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditAcc","Enter onCreate method in the EditAccountActivity abstract class.");
        //Check if Activity was call by notification pending intent
        if(extras.getBoolean("isActivityCalledFromNotification")){
            if(extras.getBoolean("notifiCationIssuedFromMainAct")){
                //Launch decrypt service
                Intent decryptDataService = new Intent(this, DecryptDataService.class);
                startService(decryptDataService);
            }

            //Reset boolean flag to display notification
            MainActivity.setPushNotificationSent(false);
            //Remove back arrow button to avoid stopping service
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        //Set activity title
        getSupportActionBar().setTitle(R.string.editAccTitle);
        //Extract account details by passing in the _id attribute stored in the extras
        Cursor cursorAccount = this.accountsDB.getAccountCursorByID(this.extras.getInt("_id"));
        if(cursorAccount != null && cursorAccount.getCount() > 0){
            this.account = Account.extractAccount(cursorAccount);
        }//End of if statement to check the cursor account isn't null
        //Set the UI data as per the account just extracted
        //Set Account name
        this.etAccountName.setText(this.account.getName());
        //Set account logo
        if(this.account.getIcon().getLocation().equals(MainActivity.getRESOURCES())){
            //Extract all the logos from the app resources
            //Call static method that will get the resource by passing in it's name and set it as the resource image of the ImageView passed in
            MainActivity.setAccountLogoImageFromRes(this.imgAccLogo,getBaseContext(),account.getIcon().getName());
            this.logo = this.accountsDB.getIconByName(account.getIcon().getName());
            //Set selectedPosition to correct value of selected logo in the Icon adapter
            IconAdapter iconAdapter = new IconAdapter(this,MainActivity.getAccountsLogos());
            //Find the current logo in the iconList stored in the iconAdapter and store its location
            boolean found = false;
            int i =0;
            while(!found && i< iconAdapter.getIconList().size()){
                if(iconAdapter.getIconList().get(i).getName().equals(this.logo.getName())){
                    this.selectedPosition = i;
                    found =true;
                }
                i++;
            }//End of while statement
        }else if(this.account.getIcon().getLocation().equals(String.valueOf(R.mipmap.ic_my_psswrd_secure_new))){
            //Setup the app logo if required
            this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure_new);
        }else if(this.account.getIcon().getLocation().startsWith(MainActivity.getExternalImageStorageClue())){
            //Set up image from URI if required
            this.logo = this.accountsDB.getIconByID(this.account.getIcon().get_id());
            this.imgAccLogo.setImageURI(Uri.parse(this.account.getIcon().getLocation()));
        }//End of if else statement to check if logo comes from app resources

        //Set up the category spinner to display the category assigned to the account by calling method that gets the cursor position by passing in it's text value
        this.spCategory.setSelection(this.getItemPositionInSpinner(this.cursorCategory,this.account.getCategory().get_id()));
        //Set up the user name spinner to display the user name assigned to the account by calling method that gets the cursor position by passing in it's text value
        if(this.account.getUserName() != null){
            this.spAccUserName.setSelection(this.getItemPositionInSpinner(this.cursorUserName,this.account.getUserName().get_id()));
        }
        if(this.account.getPsswrd() != null){
            //Set up the user name spinner to display the user name assigned to the account by calling method that gets the cursor position by passing in it's text value
            this.spAccPsswrd.setSelection(this.getItemPositionInSpinner(this.cursorPsswrd,this.account.getPsswrd().get_id()));
        }
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


    @Override
    public void onBackPressed(){
        Log.d("onBackPressedEditAcc", "Enter onBackPressed method in EditAccountActivity class.");
        //Check if activity was called from notification and if notification comes from when MainActivity was  running or not
        if(extras.getBoolean("isActivityCalledFromNotification") && !extras.getBoolean("notifiCationIssuedFromMainAct")){
            if(MainActivity.isAutoLogOutActive()){
                Intent iService = new Intent(this,AutoLogOutService.class);
                this.stopService(iService);
            }//End of if statement to check auto logout is active
        }//End of if statement to check activity was called from push notification
        super.onBackPressed();
        Log.d("onBackPressedEditAcc", "Exit onBackPressed method in EditAccountActivity class.");
    }//End of onBackPressed method


    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in EditAccountActivity class.");
        //Boolean to return method result
        boolean result = false;
        final Intent intent = new Intent();
        this.intents[0] = intent;
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","The save button was selected by user in EditAccountActivity class.");
                String accountName = this.etAccountName.getText().toString();
                long psswrdRenewDate = this.getPsswrdRenewDate();
                //Check all data input is valid and correct (Account name not in use and valid password renew date, as most data come from dropdown menus which are already valid)
                //Check the name entered is not empty
                if(!accountName.equals("")){
                    //Check account name is available but first check if it contains apostrophe so the sql query to check name is used contains scape character
                    // to avoid app crash
                    if(accountName.contains("'")){
                        accountName = accountsDB.includeApostropheEscapeChar(accountName);
                    }//End of if to check the name contains apostrophe
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
                                        Account newAccount = this.getAccountFromUIData();
                                        //Check the grocery is not empty
                                        if(newAccount != null){
                                            newAccount.set_id(this.extras.getInt("_id"));
                                            //Check at least one value has change on the UI, otherwise prompt the user
                                            if(this.account != null){
                                                //Check at least one value has change on the UI, otherwise prompt the user
                                                //First call method to compare the two question lists and record its result
                                                Boolean isQuestionListsTheSame = this.isQuestionListTheSame(this.account.getQuestionList(),newAccount.getQuestionList());
                                                if(!(this.account.getName().equals(newAccount.getName())
                                                        && this.account.getIcon().get_id() == newAccount.getIcon().get_id()
                                                        && this.account.getCategory().get_id() == newAccount.getCategory().get_id()
                                                        && this.isItemTheSame(this.account.getUserName(),newAccount.getUserName())
                                                        && this.isItemTheSame(this.account.getPsswrd(),newAccount.getPsswrd())
                                                        && isQuestionListsTheSame
                                                        && this.account.isFavorite() == newAccount.isFavorite()
                                                        //Date created is not checked as this value is not editable, so it must remain the same in the DB
                                                        //&& this.account.getDateCreated() == newAccount.getDateCreated()
                                                        && this.account.getDateChange() == newAccount.getDateChange())) {
                                                    ContentValues values = new ContentValues();
                                                    values.put(MainActivity.getIdColumn(),newAccount.get_id());
                                                    values.put(MainActivity.getNameColumn(),newAccount.getName());
                                                    values.put(MainActivity.getCategoryIdColumn(),newAccount.getCategory().get_id());
                                                    values.put(MainActivity.getUserNameIdColumn(),newAccount.getUserName().get_id());
                                                    values.put(MainActivity.getPsswrdIdColumn(),newAccount.getPsswrd().get_id());

                                                    if(!isQuestionListsTheSame){
                                                        if(this.account.getQuestionList() == null && newAccount.getQuestionList() != null){
                                                            values.put(MainActivity.getQuestionListIdColumn(),newAccount.getQuestionList().get_id());
                                                        }else if(this.account.getQuestionList() != null && newAccount.getQuestionList() == null){
                                                            values.put(MainActivity.getQuestionListIdColumn(),"(null)");
                                                        }else if(this.account.getQuestionList().get_id() != newAccount.getQuestionList().get_id()){
                                                            values.put(MainActivity.getQuestionListIdColumn(),newAccount.getQuestionList().get_id());
                                                        }//End of if else statements to catch null lists
                                                    }//End of if statement to check the question list are the same
                                                    //Call method to add icon into DB if required
                                                    this.isAddIconRequired(newAccount);
                                                    values.put(MainActivity.getIconIdColumn(),newAccount.getIcon().get_id());
                                                    values.put(MainActivity.getIsFavoriteColumn(),newAccount.isFavorite());
                                                    //values.put("DateCreated",this.account.getDateCreated());
                                                    values.put(MainActivity.getDateChangeColumn(),newAccount.getDateChange());
                                                    if(result = this.accountsDB.updateTable(MainActivity.getAccountsTable(),values)){
                                                        if(extras.getBoolean("isActivityCalledFromNotification")){
                                                            MainActivity.displayToast(this,accountName + getResources().getString(R.string.accountUpdated),Toast.LENGTH_LONG,Gravity.CENTER);
                                                            //If that is the case we need to manually force MainActivity to be displayed
                                                            Intent i = new Intent(this,MainActivity.class);
                                                            intent.putExtra("isMainActCalledFromEditAccAct",true);
                                                            startActivity(i);
                                                        }else{
                                                            //Call method to update data set displayed on the recycler view and display proper message after adding the grocery to the DB
                                                            //Put extra info to transfer to the Main activity
                                                            intent.putExtra("accountID",newAccount.get_id());
                                                            intent.putExtra("accountName",newAccount.getName());
                                                            intent.putExtra("position",extras.getInt("position"));
                                                            setResult(RESULT_OK, intent);
                                                            //Update notifications and cancel if account before saving had a password "change date" greater than 0
                                                            //but less than system time OR check box not checked.
                                                            if ((this.account.getDateChange() > 0 && this.account.getDateChange() < System.currentTimeMillis())
                                                                && ((this.cbHasToBeChanged.isChecked() && newAccount.getDateChange() > System.currentTimeMillis())
                                                                || !this.cbHasToBeChanged.isChecked())){
                                                                    NotificationManagerCompat notificationManager = MainActivity.getNotificationManager();
                                                                    if (notificationManager != null){
                                                                        //Send cancel notification request by passing in account ID. If notification was sent
                                                                        //for this account it will be cancel, otherwise, notification will remain unchanged.
                                                                        NotificationManagerCompat.from(getBaseContext()).cancel(account.get_id());
                                                                    }//End of if to check notification manager not null (so notification was sent earlier)
                                                            }//End of if statement to check account change date was overdue and updated.
                                                            Log.d("onOptionsItemSelected","Set activity result to OK  on onOptionsItemSelected method in EditAccountActivity class.");
                                                            finish();
                                                        }//End of if else statement that checks activity is called from notification
                                                    }else{
                                                        //In case of failure updating the account, try to roll back the question list assigned to account on DB
                                                        //Check the new and old list are not the same
                                                        if(!isQuestionListsTheSame){
                                                            //If not the same, remove the new question list from DB
                                                            rollBackQuestionListInsertion(newAccount.getQuestionList());
                                                            //Try to bring back old list to DB
                                                            //Get the old question list from the original account object
                                                            QuestionList oldSecurityQuestionList = this.account.getQuestionList();
                                                            //Check it's not null
                                                            if(oldSecurityQuestionList != null) {
                                                                //Check if the current list is in use
                                                                int timesUsedQuestionList = accountsDB.getTimesUsedQuestionList(oldSecurityQuestionList.get_id());
                                                                if(timesUsedQuestionList > 0){
                                                                    //If is in use, proceed to update the account ????
                                                                    values = new ContentValues();
                                                                    values.put(MainActivity.getIdColumn(),account.get_id());
                                                                    values.put(MainActivity.getQuestionListIdColumn(),oldSecurityQuestionList.get_id());
                                                                    accountsDB.updateTable(MainActivity.getAccountsTable(),values);
                                                                }else{
                                                                    //add the list and retrieve new list _id
                                                                    int _id = accountsDB.addItem(oldSecurityQuestionList);
                                                                    //Update the account row with old list, but new list _id
                                                                    values = new ContentValues();
                                                                    values.put(MainActivity.getIdColumn(),account.get_id());
                                                                    values.put(MainActivity.getQuestionListIdColumn(),_id);
                                                                    //update account with new list _id
                                                                    accountsDB.updateTable(MainActivity.getAccountsTable(),values);
                                                                }//End of if else statement
                                                            }//End of if statement to check the old question list isn't null
                                                        }//End of if  statement to check the questions list are not the same
                                                        //Report DB error when updating the record
                                                        MainActivity.displayToast(this,getResources().getString(R.string.accountUpdateError),Toast.LENGTH_LONG,Gravity.CENTER);
                                                    }//End of if statement to check the accountID is not -1
                                                }else{
                                                    //Prompt the user no change has been done on the UI data
                                                    MainActivity.displayToast(this,getResources().getString(R.string.accountNotChanged),Toast.LENGTH_SHORT,Gravity.CENTER);
                                                }//End of if else statement to check the UI data has changed, at least one field
                                            }//End of if statement to check the old account isn't null
                                            //Check the id from the DB is valid and different than the dummy one.
                                        }else{
                                            setResult(RESULT_CANCELED, intent);
                                            //Display error about DB insertion
                                            MainActivity.displayToast(this,getResources().getString(R.string.accountUpdateError),Toast.LENGTH_LONG,Gravity.CENTER);
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
            case R.id.select_logo_delete:
                //Prompt the user if they're sure to delete the account
                AlertDialog.Builder dialog = MainActivity.displayAlertDialogNoInput(this,getResources().getString(R.string.accountDeleteTitle),getResources().getString(R.string.accountDeleteMssg));
                dialog.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                            @Override
                            //Handle the onclick event for the dialog alert buttons, Ok and cancel
                            public void onClick(DialogInterface dialog, int which) {
                                //If Ok was pressed, call DB method that runs query that deletes an account from Accounts table by passing in it's _id
                                //Now the account can be deleted
                                if(deleteAccount(accountsDB,account)){
                                    //Call method to update data set displayed on the recycler view and display proper message after adding the grocery to the DB
                                    //Put extra info to transfer to the Main activity
                                    intents[0].putExtra("accountID",-1);
                                    intents[0].putExtra("accountName",account.getName());
                                    intents[0].putExtra("position",extras.getInt("position"));
                                    setResult(RESULT_OK, intents[0]);
                                    //Update notifications and cancel if account before saving had a password "change date" greater than 0
                                    //but less than system time OR check box not checked.
                                    if (account.getDateChange() > 0 && account.getDateChange() < System.currentTimeMillis()){
                                        NotificationManagerCompat notificationManager = MainActivity.getNotificationManager();
                                        if (notificationManager != null){
                                            //Send cancel notification request by passing in account ID. If notification was sent
                                            //for this account it will be cancel, otherwise, notification will remain unchanged.
                                            NotificationManagerCompat.from(getBaseContext()).cancel(account.get_id());
                                        }//End of if statement to check notification manager is not null, so notification was sent erlier.
                                    }//End of if statement to check account password was overdue before deleting.
                                    Log.d("onOptionsItemSelected","Set activity result to OK  on onOptionsItemSelected method in EditAccountActivity class.");
                                    finish();
                                }else{
                                    //Display error message
                                    //Prompt the user about a DB error while updating the user name
                                    MainActivity.displayToast(getBaseContext(),getResources().getString(R.string.itemNotDeletedMssg),Toast.LENGTH_LONG,Gravity.CENTER);
                                    Log.d("onOptionsItemSelected","The account "+ account.getName() +" has not been updated due to DB issue.");
                                }//End of if else statement that checks account deletion was successful
                                //Check the boolean flag that confirms the account was deleted so proper info is passed back to caller activity
                            }//End of onClick method
                });//End of setPositive button action
                dialog.show();
                Log.d("onOptionsItemSelected","Delete option selected on onOptionsItemSelected method in EditAccountActivity class.");
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                //Go back to previous activity
                //Check if activity got call from a notification
                if(extras.getBoolean("isActivityCalledFromNotification")){
                    //If that is the case we need to manually force MainActivity to be displayed
                    Intent i = new Intent(this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    finish();
                }//End of if else statement to check activity is called from push notification
                break;
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in EditAccountActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in EditAccountActivity class.");
        return result;
    }//End of onOptionsItemSelected method


    //Method to check two accounts have different question list (including null list)
    private boolean isItemTheSame(UserName item1,UserName item2){
        Log.d("isItemTheSame","Enter the isItemTheSame method in EditAccountActivity class.");
        //Declare boolean flag to be returned by method
        boolean isTheSame;
        //Check both lists are null
        if(item1 == null && item2 == null){
            isTheSame = true;
        }else if(item1 == null && item2 != null){
            //If list1 is null and the other doesn't, they aren't the same
            isTheSame = false;
        }else if(item1 != null && item2 == null){
            //If list2 is null and the other doesn't, they aren't the same
            isTheSame = false;
        }else if(item1.get_id() == item2.get_id()){
            isTheSame = true;
        }else{
            //Any other case return false
            isTheSame = false;
        }//End of if else statements to check list status and ids
        Log.d("isItemTheSame","Exit the isItemTheSame method in EditAccountActivity class.");
        return isTheSame;
    }//End of isQuestionListTheSame method

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
            if(list1.getSize() == list2.getSize()){
                //If both list hold the same id they are the same
                isTheSame = true;
            }else{
                isTheSame = false;
            }
        }else{
            //Any other case return false
            isTheSame = false;
        }//End of if else statements to check list status and ids
        Log.d("isQuestionListTheSame","Exit the isQuestionListTheSame method in EditAccountActivity class.");
        return isTheSame;
    }//End of isQuestionListTheSame method

    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","Enter/Exit the onActivityResult method in the EditAccountActivity class.");
    }//End of onActivityResult method

    //Method to delete the required items not being used by any other account when an account is deleted on its own or as part of category deletion
    public static boolean deleteAccount(AccountsDB accountsDB,Account account){
        Log.d("deleteAccount","Enter the deleteAccount static method in the EditAccountActivity class.");
        boolean isAccountDeleted = false;
        //If Ok was pressed, call DB method that runs query that deletes an account from Accounts table by passing in it's _id
        //But, before deleting the account, check the components that make up the account, check if they are not being used
        //any longer and delete them from the DB if required. User names, passwords, questions, categories and resource icons  are the only
        //exceptions, where they will be able to exist in DB even when not being used.
        //Any other sub-item, not being used in any account will be removed form DB: This means QuestionList,
        //QuestionAssignment, Icon if the icon comes from URI
        //Check the QuestionList (if applicable) has to be deleted
        if(account.getQuestionList() != null){
            if(accountsDB.getTimesUsedQuestionList(account.getQuestionList().get_id()) <= 1){
                //Delete the question assignments
                accountsDB.deleteRowFromTable(MainActivity.getQuestionassignmentTable(),MainActivity.getQuestionListIdColumn(),account.getQuestionList().get_id());
                //Delete the list
                accountsDB.deleteItem(account.getQuestionList());
            }//End of if statement to check how many times the list is being used
        }//End of if statement to check th question list isn't null
        //Check the icon, if icon comes from URI, delete from DB if used only once
        Icon icon = account.getIcon();
        if(icon != null){
            if(!icon.getLocation().equals(MainActivity.getRESOURCES()) && !icon.equals(MainActivity.getMyPsswrdSecureLogo())){
                //Get the number of times used
                if(accountsDB.getAccountsWithSpecifcValue(MainActivity.getIconIdColumn(),icon.get_id()).getCount() <=1){
                    //Delete icon from DB
                    accountsDB.deleteRowFromTable(MainActivity.getIconTable(),MainActivity.getIdColumn(),icon.get_id());
                }//End of if statement to check the number of times the icon is being used
            }//End of if statement to check the isn't a resource file
        }//End of if statement to check the icon isn't null

        isAccountDeleted = accountsDB.deleteItem(account);
        Log.d("deleteAccount","Exit the deleteAccount static method in the EditAccountActivity class.");
        return isAccountDeleted;
    }//End of deleteAccount static method
}//End of EditAccountActivity class