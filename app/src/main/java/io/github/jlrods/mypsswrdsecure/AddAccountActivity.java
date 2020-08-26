package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class AddAccountActivity extends DisplayAccountActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateDispAcc", "Enter onCreate method in the a AddAccountActivity class.");
        //Set up the Created date
        this.setDateText(this.tvAccDateCreated,System.currentTimeMillis());
//        //Set up current date into the Created date field
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        this.tvAccDateCreated.setText(new SimpleDateFormat(MainActivity.getDateFormat()).format(calendar.getTime()));
        Log.d("OnCreateDispAcc", "eXIT onCreate method in the AddAccountActivity class.");
    }//End of onCreate method

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        //Call super method
        super.onSaveInstanceState(saveState);
        Log.d("onSaveInstanceState", "Enter onSaveInstanceState method in the a AddAccountActivity class.");
        //Record input data that is not saved by OS already
        //Fields save by OS: Account Name edit text,
        //The date created is generated itself during onCreate method
        //Save all the input fields first
        //Save te Account name input in edit text field
        saveState.putString("etAccountNameText",this.etAccountName.getText().toString());
        //Save te Category spinner item position selected
        saveState.putInt("spCategorySelectedPosition",this.spCategory.getSelectedItemPosition());
        //Save te UserName spinner item position selected
        saveState.putInt("spUserNameSelectedPosition",this.spAccUserName.getSelectedItemPosition());
        //Save te Password spinner item position selected
        saveState.putInt("spPsswrdSelectedPosition",this.spAccPsswrd.getSelectedItemPosition());
        saveState.putBoolean("spAccSecQuestionListIsEnabled",this.spAccSecQuestionList.isEnabled());
        //Check if Security question spinner is enable
        if(this.spAccSecQuestionList.isEnabled()){
            //Check number of questions and store their QuestionIDs
            QuestionList questionList = this.extractQuestionsFromSpinner(this.spAccSecQuestionList);
            saveState.putInt("numberOfQuestionsInList",questionList.getSize());
            for(int i=0;i<questionList.getSize();i++){
                saveState.putInt("questionID"+(i+1),questionList.getQuestions().get(i).get_id());
            }
            //If so, save the current position
            saveState.putInt("spSecQuestListSelectedPos",this.spAccSecQuestionList.getSelectedItemPosition());
        }else{
            //Otherwise, save -1 (even though there's a dummy item selected)
            saveState.putInt("spSecQuestListSelectedPos",-1);
        }
        //Save the Question Available spinner item position selected
        saveState.putInt("spQuestAvailableSelectedPos",this.spQuestionsAvailable.getSelectedItemPosition());
        //Save the checkbox state of "Has to be changed?" checkbox
        saveState.putBoolean("cbHasToBeChanged",this.cbHasToBeChanged.isChecked());
        //If the checkbox is ticked, save the password renew date
        if(this.cbHasToBeChanged.isChecked()){
            saveState.putString("tvDateRenewValueText",this.tvAccDateRenewValue.getText().toString());
        }
        //Save the isFavorite attribute
        saveState.putBoolean("isFavorite",this.isFavorite);

        //Save the current icon
        if(this.logo.getLocation().equals(MainActivity.getRESOURCES())){
            saveState.putString("logoLocation",MainActivity.getRESOURCES());
            saveState.putInt("logoListPosition",this.selectedPosition);
        }else if(this.logo.getLocation().equals(String.valueOf(R.mipmap.ic_my_psswrd_secure))){
            saveState.putString("logoLocation",String.valueOf(R.mipmap.ic_my_psswrd_secure));
            saveState.putInt("logoListPosition",R.mipmap.ic_my_psswrd_secure);
        }else{
            //@FIXME: designated to include resources from device locations and URIs
        }

        Log.d("onSaveInstanceState", "Exit onSaveInstanceState method in the a AddAccountActivity class.");
    }//End of onSaveInstanceState

    @Override
    protected void onRestoreInstanceState(Bundle restoreState) {
        //Call the super method
        super.onRestoreInstanceState(restoreState);
        Log.d("onRestoreInstanceState", "Enter onRestoreInstanceState method in the a AddAccountActivity class.");
        //Check the stored state is not null
        if (restoreState != null){
            //Populate Account name
            this.etAccountName.setText(restoreState.getString("etAccountNameText"));
            //Set up current Category spinner position
            int spCategorySelectedPosition = restoreState.getInt("spCategorySelectedPosition");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spCategorySelectedPosition != -1){
                this.spCategory.setSelection(spCategorySelectedPosition);
            }else{
                //Set up Category spinner from beginning
                //Setup the Category spinner and populate with data
                this.cursorCategory = this.accountsDB.getCategoryListCursor();
                this.setUpSpinnerData(this.cursorCategory,this.spCategory,this.CATEGORY_SPINNER);
            }//End of if else statement  that checks the selected position in the category  spinner

            //Set up current UserName spinner position
            int spUserNameSelectedPosition = restoreState.getInt("spUserNameSelectedPosition");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spUserNameSelectedPosition != -1){
                this.spAccUserName.setSelection(spUserNameSelectedPosition);
            }else{
                //Set up UserName spinner from beginning
                this.cursorUserName = this.accountsDB.getUserNameList();
                this.setUpSpinnerData(this.cursorUserName,this.spAccUserName,this.USERNAME_SPINNER);
            }//End of if else statement that checks the selected position in the user name spinner

            //Set up current Password spinner position
            int spPsswrdSelectedPosition = restoreState.getInt("spPsswrdSelectedPosition");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spPsswrdSelectedPosition != -1){
                this.spAccPsswrd.setSelection(spPsswrdSelectedPosition);
            }else{
                //Set up UserName spinner from beginning
                this.cursorPsswrd = this.accountsDB.getPsswrdList();
                this.setUpSpinnerData(this.cursorPsswrd,this.spAccPsswrd,this.PSSWRD_SPINNER);
            }//End of if else statement that checks the selected position in the password spinner

            //Check the sec question list spinner is enabled (ignores dummy question cursor)
            if(restoreState.getBoolean("spAccSecQuestionListIsEnabled")){
                //Set up current Security question spinner position
                int spSecQuestListSelectedPos = restoreState.getInt("spSecQuestListSelectedPos");
                //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
                //Set up Sec question list spinner from beginning passing the questionsID to get the cursor
                switch(restoreState.getInt("numberOfQuestionsInList")){
                    case 1:
                        this.cursorQuestionList = this.accountsDB.getQuestionCursorByID(restoreState.getInt("questionID1"));
                        break;
                    case 2:
                        this.cursorQuestionList = this.accountsDB.getQuestionCursorByID(restoreState.getInt("questionID1"),restoreState.getInt("questionID2"));
                        break;
                    case 3:
                        this.cursorQuestionList = this.accountsDB.getQuestionCursorByID(restoreState.getInt("questionID1"),restoreState.getInt("questionID2"),restoreState.getInt("questionID3"));
                        break;
                }//End of switch statement
                //Set up the spinner data once the cursor has been generated based on number of questions in list
                this.setUpQuestionListSpinnerData(this.cursorQuestionList,this.spAccSecQuestionList);
                //Check spinner position selected (The spinner could hold questions but none is selected, display proper prompt in each case)
                if(spSecQuestListSelectedPos != -1){
                    //Just move spinner to correct item position
                    this.spAccSecQuestionList.setSelection(spSecQuestListSelectedPos);
                }else{
                    //Setup spinner prompt by getting proper text after calling method to get text
                    this.spAccSecQuestionList.setPrompt(this.getSecQuestListPrompt(this.cursorQuestionList.getCount()));
                }//End of if else statement to check spinner position selected (The spinner could hold questions but none is selected)
            }else{
                this.initSecQuestionListSpinner();
            }//End of if statement that checks the sec question list spinner is enabled (ignores dummy question cursor)

            //Set up current Question available  spinner position
            int spQuestAvailableSelectedPos = restoreState.getInt("spQuestAvailableSelectedPos");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spQuestAvailableSelectedPos != -1){
                this.spQuestionsAvailable.setSelection(spQuestAvailableSelectedPos);
            }else{
                //Set up Question available spinner from beginning
                this.initQuesitonAvailableListSpinner();
            }//End of if else statement to check the selected position for the list of question available

            //Setup the isFav image accordingly
            if(restoreState.getBoolean("isFavorite")){
                this.toggleIsFavorite();
            }//End of if statement that checks isFavorite property

            //Setup the checkbox state
            boolean isChecked = restoreState.getBoolean("cbHasToBeChanged");
            if(isChecked){
                this.cbHasToBeChanged.setChecked(isChecked);
                this.tvAccDateRenewValue.setText(restoreState.getString("tvDateRenewValueText"));
            }//End of if statement that checks the checkbox is ticked

            //Setup the account logo icon
            String logoLocation = restoreState.getString("logoLocation");
            if(logoLocation.equals(MainActivity.getRESOURCES())){
                this.selectedPosition = restoreState.getInt("logoListPosition");
                this.logo = this.iconAdapter.getIconList().get(this.selectedPosition);
                this.imgAccLogo.setImageResource(this.iconAdapter.getIconList().get(restoreState.getInt("logoListPosition")).getResourceID());
            }else if(restoreState.getString("logoLocation").equals(String.valueOf(R.mipmap.ic_my_psswrd_secure))){
                //this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure);
            }else{

            }

            //Buttons configuration
            //Setup Remove button visibility
            if(this.spAccSecQuestionList.getSelectedItemPosition() != -1){
                this.btnAccRemoveQuestion.setVisibility(View.VISIBLE);
            }else{
                this.btnAccRemoveQuestion.setVisibility(View.INVISIBLE);
            }//End of if else statement to setup remove question button visibility

            //Setup New Question button enable property
            if(this.spAccSecQuestionList.getAdapter().getCount() == 3){
                this.btnAccNewSecQuestion.setEnabled(false);
            }//End of if statement to setup new question button enable property
        }//End of if statement to check the restore state isn't null
        Log.d("onRestoreInstanceState", "Exit onRestoreInstanceState method in the a AddAccountActivity class.");
    }//End of onRestoreInstanceState method

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
                                        this.account = this.getItemFromUIData();
                                        //Check the grocery is not empty
                                        if(this.account != null){
                                            //Declare and instantiate to invalid value a new int var to hold the returned int from the addItem method which will correspond with the grocery just created
                                            int accountID = -1;
                                            //Call the addItem method and receive the id sent from method
                                            accountID    = accountsDB.addItem(this.account);
                                            //Check the id from the DB is valid and different than the dummy one.
                                            if(accountID != -1){
                                                this.account.set_id(accountID);
                                                //Call method to update data set displayed on the recycler view and display proper message after adding the grocery to the DB
                                                //Put extra info to transfer to the Main activity
                                                intent.putExtra("accountID",this.account.get_id());
                                                intent.putExtra("accountName",this.account.getName());
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


}//End of AddAccountActivity method
