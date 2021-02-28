package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

public class AddUserNameActivity extends AddItemActivity{
    //Attributes to be used while saving the new question
    protected UserName userName = null;

    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddUserNameActivity class.");
        //Update layout fields according to Add Security question layout
        this.imgAddActivityIcon.setImageResource(R.mipmap.ic_account_black_48dp);
        this.tvAddActivityTag.setText(R.string.account_userName);
        //Set activity title
        getSupportActionBar().setTitle(R.string.addUserTitle);
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddUserNameActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddUserNameActivity class.");
        //Boolean to return method result
        boolean result = false;
        int userNameID = -1;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in AddUserNameActivity class.");
                //Create userName object
                String userNameValue = this.etNewItemField.getText().toString().trim();
                byte[] userNameValueEncrypted =  null;
                //Check the userName isn't empty
                if(this.isDataValid(1)){
                    //Encrypt the user name
                    userNameValueEncrypted = this.cryptographer.encryptText(userNameValue);
                    //If user name not in the list create new user object and store it in global variable used to build the account object
                    this.userName = new UserName(userNameValueEncrypted,this.cryptographer.getIv().getIV());
                    //Call DB method to insert  the user name object into the DB
                    userNameID = this.accountsDB.addItem(this.userName);
                    //Put remainder time for logout so MainActivity can continue the count down
                    intent.putExtra("timeOutRemainder",this.logOutTimer.getLogOutTimeRemainder());
                    if(userNameID > 0 ){
                        //Update the userName object ID and prepare data to exit activity
                        this.userName.set_id(userNameID);
                        intent.putExtra("userNameID",this.userName.get_id());
                        setResult(RESULT_OK, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The user name "+ userNameValue +" has been added into the DB through addNewUserName method in the AddUserNameActivity class.");
                    }else{
                        //Prompt the user the user name input failed to be inserted in the DB
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarUserNotAdded),Toast.LENGTH_LONG,Gravity.CENTER);
                        //Set activity result as cancelled so caller activity can decide what to do if this is the case
                        setResult(RESULT_CANCELED, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The user name "+ userNameValue +" has NOT been added into the DB through onOptionsItemSelected method in the AddUserNameActivity class, due to DB problem.");
                    }//End of if else statement to check user name was saved in DB
                }//End of if statement that check UI data is valid
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so caller activity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddUserNameActivity class.");
                finish();
                break;
        }//End of switch statement to check the menu option selected
        Log.d("onOptionsItemSelected","Exit onOptionsItemSelected method in AddUserNameActivity class.");
        return result;
    }//End of onOptionsItemSelected method
}//End of AddUserNameActivity activity
