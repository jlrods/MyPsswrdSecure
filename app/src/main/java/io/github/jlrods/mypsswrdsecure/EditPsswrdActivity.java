package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;
import javax.crypto.spec.IvParameterSpec;

public class EditPsswrdActivity extends AddPsswrdActivity {
    //Attribute definition
    private Bundle extras;
    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditPsswrd","Enter onCreate method in the EditPsswrdActivity class.");
        this.extras = getIntent().getExtras();
        //Extract user name by passing in the _id attribute stored in the extras
        this.psswrd = Psswrd.extractPsswrd(this.accountsDB.getPsswrdCursorByID(this.extras.getInt("_id")));
        //Set the edit text field with the user name value after decryption
        this.etNewItemField.setText(this.cryptographer.decryptText(this.psswrd.getValue(),new IvParameterSpec(this.psswrd.getIv())));
        Log.d("OnCreateEditPsswrd","Exit onCreate method in the EditPsswrdActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "Enter onOptionsItemSelected method in EditPsswrdActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in EditPsswrdActivity class.");
                //Get the new user name text
                String psswrdValue = this.etNewItemField.getText().toString().trim();
                //Check for UI data validity
                if(this.isDataValid(4,this.psswrd.get_id())){
                    //Set the new encrypted value and IV
                    this.psswrd.setValue(this.cryptographer.encryptText(this.etNewItemField.getText().toString()));
                    this.psswrd.setIv(this.cryptographer.getIv().getIV());
                    //Store data to be updated on the DB
                    ContentValues values = new ContentValues();
                    values.put("_id",this.psswrd.get_id());
                    values.put("Value",this.psswrd.getValue());
                    values.put("initVector",this.psswrd.getIv());
                    //Update the table
                    if(this.accountsDB.updateTable(MainActivity.getPsswrdTable(),values)){
                        //Go back to previous activity
                        intent.putExtra("psswrdID",this.psswrd.get_id());
                        result = true;
                        setResult(RESULT_OK, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The password "+ psswrdValue +" has been added into the DB through onOptionsItemSelected method in the AddPsswrdActivity class.");
                    }else{
                        //Prompt the user about a DB error while updating the user name
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarUserNotAdded), Toast.LENGTH_LONG, Gravity.CENTER);
                        Log.d("onOptionsItemSelected","The password "+ psswrdValue +" has not been updated due to DB issue.");
                    }//End of if else statement to check the record has been updated
                }//End of if statement that check UI data is valid
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so caller activity can decide what to do if this is the case
                setResult(RESULT_CANCELED, null);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in EditPsswrdActivity class.");
                finish();
                break;
        }//End of switch statement
        return result;
    }//End of onOptionsItemSelected method

}//End of EditPsswrdActivity

