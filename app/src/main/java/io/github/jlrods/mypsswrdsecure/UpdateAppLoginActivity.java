package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class UpdateAppLoginActivity extends DisplayAccountActivity{
    private Cursor appState = null;
    private AppLoggin appLoggin = null;
    private static Cryptographer cryptographer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateUpAppLog", "Enter onCreate method in the a UpdateAppLoginActivity class.");

        //Set activity title
        getSupportActionBar().setTitle("Update credentials");

        //Initialize view objects from layout to have access to them and set different texts and other properties
        //Set all non necessary layouts to gone or invisible
        LinearLayout layoutAccLogo = (LinearLayout) findViewById(R.id.layoutAccLogo);
        layoutAccLogo.setVisibility(View.GONE);
        LinearLayout layoutCategory = (LinearLayout) findViewById(R.id.layoutCategory);
        layoutCategory.setVisibility(View.GONE);
        LinearLayout layoutSecQuestion = (LinearLayout) findViewById(R.id.layoutSecQuestion);
        layoutSecQuestion.setVisibility(View.INVISIBLE);
        LinearLayout linLayout_secQuestion = (LinearLayout) findViewById(R.id.linLayout_secQuestion);
        if(linLayout_secQuestion != null){
            linLayout_secQuestion.setVisibility(View.INVISIBLE);
        }
        LinearLayout layoutIsFav = (LinearLayout) findViewById(R.id.layoutIsFav);
        if(cbHasToBeChanged != null){
            cbHasToBeChanged.setVisibility(View.GONE);
        }
        layoutIsFav.setVisibility(View.INVISIBLE);
        LinearLayout layoutDates = (LinearLayout) findViewById(R.id.layoutDates);
        if(layoutDates != null){
            layoutDates.setVisibility(View.INVISIBLE);
        }
        RelativeLayout subLayout_blankSpace3 = (RelativeLayout) findViewById(R.id.subLayout_blankSpace3);
        if(subLayout_blankSpace3 != null){
            subLayout_blankSpace3.setVisibility(View.GONE);
        }
        RelativeLayout layoutAccDateCreated = (RelativeLayout) findViewById(R.id.layoutAccDateCreated);
        if(layoutAccDateCreated != null){
            layoutAccDateCreated.setVisibility(View.GONE);
        }

        //Get the appLogin object from main activity
        this.appLoggin = MainActivity.getCurrentAppLoggin();
        //Populate user current user name and password
        //If the user name object isn't null, move the spinner to the corresponding user name
        if(this.appLoggin.getUserName() != null){
            this.spAccUserName.setSelection(this.getItemPositionInSpinner(this.cursorUserName,this.appLoggin.getUserName().get_id()));
        }
        //If the password object isn't null, move the password spinner to the correspoinding position
        if(this.appLoggin.getPsswrd() != null){
            //Set up the user name spinner to display the user name assigned to the account by calling method that gets the cursor position by passing in it's text value
            this.spAccPsswrd.setSelection(this.getItemPositionInSpinner(this.cursorPsswrd,this.appLoggin.getPsswrd().get_id()));
        }
        Log.d("OnCreateUpAppLog", "Exit onCreate method in the a UpdateAppLoginActivity class.");
    }//End of onCreate method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the UpdateAppLoginActivity class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the UpdateAppLoginActivity class.");
        return true;
    }//End of onCreateOptionsMenu method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "Enter onOptionsItemSelected method in UpdateAppLoginActivity class.");
        boolean result = false;
        //Check the id of item selected in menu
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                cryptographer = MainActivity.getCryptographer();
                //Check a user name has been selected
                if(this.spAccUserName.getSelectedItemPosition() >=0 ){
                    this.userName = this.accountsDB.getUserNameByID((int) this.spAccUserName.getSelectedItemId());
                    if(this.spAccPsswrd.getSelectedItemPosition() >= 0){
                        this.psswrd = this.accountsDB.getPsswrdByID((int) this.spAccPsswrd.getSelectedItemId());
                        //Check user name and password aren't the same as the values already stored in DB
                        if(this.appLoggin.getUserName().get_id() != this.userName.get_id() ||
                            this.appLoggin.getPsswrd().get_id() != this.psswrd.get_id()){
                            //If at least one of the fields changed, update the app login table with new data
                            ContentValues values = new ContentValues();
                            values.put(MainActivity.getIdColumn(),this.appLoggin.get_id());
                            values.put(MainActivity.getUserNameIdColumn(),cryptographer.encryptText(String.valueOf(this.userName.get_id())));
                            values.put(MainActivity.getUserNameIvColumn(),cryptographer.getIv().getIV());
                            values.put(MainActivity.getPsswrdIdColumn(),cryptographer.encryptText(String.valueOf(this.psswrd.get_id())));
                            values.put(MainActivity.getPsswrdIvColumn(),cryptographer.getIv().getIV());
                            //Update the appLogin table with new values
                            if(this.accountsDB.updateTable(MainActivity.getApplogginTable(),values)){
                                //Go back to previous activity
                                intent.putExtra("userNameID",this.userName.get_id());
                                intent.putExtra("itemDeleted",false);
                                setResult(RESULT_OK, intent);
                                result = true;
                                finish();
                            }
                        }else{
                            //@Fixme:  Prompt user the data has not changed
                            MainActivity.displayToast(this,"Sorry, credentials have not changed, change at least one of the fields in the menus.", Toast.LENGTH_LONG,Gravity.CENTER);
                        }
                    }else{
                        //@Fixme:  Prompt error
                    }
                }else{
                    //@Fixme: Prompt error
                }
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in UpdateAppLoginActivity class.");
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                //Go back to previous activity
                finish();
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in UpdateAppLoginActivity class.");
                break;
            case R.id.action_logout:
                //Call method to throw LoginActivity and clear activity stack.
                Log.d("onOptionsItemSelected", "Logout option selected on onOptionsItemSelected method in UpdateAppLoginActivity class.");
                MainActivity.logout(this);
                break;
        }//End of switch statement to check menu item id selected by user
        Log.d("onOptionsItemSelected", "Exit onOptionsItemSelected method in UpdateAppLoginActivity class.");
        return result;
    }//End of onOptionsItemSelected method
}//End of UpdateAppLoginActivity class
