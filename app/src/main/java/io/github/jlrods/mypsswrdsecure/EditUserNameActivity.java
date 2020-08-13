package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import javax.crypto.spec.IvParameterSpec;

public class EditUserNameActivity extends AddUserNameActivity {
    //Attribute definition
    private Bundle extras;
    //Method definition
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the EditUserNameActivity class.");
        this.extras = getIntent().getExtras();
        //Extract user name by passing in the _id attribute stored in the extras
        this.userName = UserName.extractUserName(this.accountsDB.getUserNameCursorByID(this.extras.getInt("_id")));
        //Set the edit text field with the user name value after decryption
        this.etNewItemField.setText(this.cryptographer.decryptText(userName.getValue(),new IvParameterSpec(userName.getIv())));
        Log.d("OnCreateAddQuest","Exit onCreate method in the EditUserNameActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "Enter onOptionsItemSelected method in EditUserNameActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in EditUserNameActivity class.");
                //Get the new user name text
                String userNameValue = this.etNewItemField.getText().toString().trim();
                //Check for UI data validity
                if(this.isDataValid(3,this.userName.get_id())){
                    //Store the  new user name encrypted value and IV in the user name object
                    this.userName.setValue(this.cryptographer.encryptText(this.etNewItemField.getText().toString()));
                    this.userName.setIv(this.cryptographer.getIv().getIV());
                    //Store the values to be updated in the DB
                    ContentValues values = new ContentValues();
                    values.put("_id",this.userName.get_id());
                    values.put("Value",this.userName.getValue());
                    values.put("initVector",this.userName.getIv());
                    //Update the user name in the DB
                    if(this.accountsDB.updateTable(MainActivity.getUsernameTable(),values)){
                        //Go back to previous activity
                        intent.putExtra("userNameID",this.userName.get_id());
                        result = true;
                        setResult(RESULT_OK, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The user name "+ userNameValue +" has been added into the DB through addNewUserName method in the AddUserNameActivity class.");
                    }else{
                        //Prompt the user about a DB error while updating the user name
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarUserNotAdded),Toast.LENGTH_LONG,Gravity.CENTER);
                        Log.d("onOptionsItemSelected","The user name "+ userNameValue +" has not been updated due to DB issue.");
                    }//End of if else statement to check the record has been updated
                }//End of if statement that check UI data is valid
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so caller activity can decide what to do if this is the case
                setResult(RESULT_CANCELED, null);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in EditUserNameActivity class.");
                finish();
                break;
        }//End of switch statement
        return result;
    }//End of onOptionsItemSelected method
}//End of class EditUserNameActivity
