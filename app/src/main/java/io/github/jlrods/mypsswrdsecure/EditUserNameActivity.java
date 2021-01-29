package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import javax.crypto.spec.IvParameterSpec;

public class EditUserNameActivity extends AddUserNameActivity {
    //Attribute definition
    private Bundle extras;
    private boolean toBeDeleted = false;
    //Method definition
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditUser","Enter onCreate method in the EditUserNameActivity class.");
        this.extras = getIntent().getExtras();
        //Set activity title
        getSupportActionBar().setTitle(R.string.editUserTitle);
        //Extract user name by passing in the _id attribute stored in the extras
        this.userName = UserName.extractUserName(this.accountsDB.getUserNameCursorByID(this.extras.getInt("_id")));
        //Set the edit text field with the user name value after decryption
        this.etNewItemField.setText(this.cryptographer.decryptText(userName.getValue(),new IvParameterSpec(userName.getIv())));
        this.fabDelete.setVisibility(View.VISIBLE);
        //Set up fab onClick event listener by creating a new object of the sub class which handles this event
        this.fabDelete.setOnClickListener(
                //Create the subclass object by passing in the text required to populate the AlertDialog box and the intent attribute name to be passed to caller activity
                new FabOnClickEventHandler(userName,getResources().getString(R.string.userNameDeleteTitle),
                        getResources().getString(R.string.userNameDeleteMssg),
                        cryptographer.decryptText(userName.getValue(),new IvParameterSpec(userName.getIv())),
                        "itemDeleted")
        );
        Log.d("OnCreateEditUser","Exit onCreate method in the EditUserNameActivity class.");
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
                        intent.putExtra("itemDeleted",false);
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

    private void test(){}
}//End of class EditUserNameActivity
