package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;
import javax.crypto.spec.IvParameterSpec;

public class EditPsswrdActivity extends AddPsswrdActivity implements EditItemActivity {
    //Attribute definition
    private Bundle extras;
    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditPsswrd","Enter onCreate method in the EditUserNameActivity class.");
        this.extras = getIntent().getExtras();
        //Extract user name by passing in the _id attribute stored in the extras
        this.psswrd = Psswrd.extractPsswrd(this.accountsDB.getPsswrdCursorByID(this.extras.getInt("_id")));
        //Set the edit text field with the user name value after decryption
        this.etNewItemField.setText(this.cryptographer.decryptText(this.psswrd.getValue(),new IvParameterSpec(this.psswrd.getIv())));
        Log.d("OnCreateEditPsswrd","Exit onCreate method in the EditUserNameActivity class.");
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
                if(this.isFieldNotEmpty(this.etNewItemField)){
                    //Check current value in the edit field isn't the same already recorded on the DB
                    if(!this.cryptographer.decryptText(this.psswrd.getValue(),new IvParameterSpec(this.psswrd.getIv())).trim().equals(this.etNewItemField.getText().toString().trim())){
                        //Check the psswrd isn't in the DB already
                        this.cursor = accountsDB.getPsswrdByName(psswrdValue);
                        if(this.cursor == null || this.cursor.getCount() ==0){
                            this.psswrd.setValue(this.cryptographer.encryptText(this.etNewItemField.getText().toString()));
                            this.psswrd.setIv(this.cryptographer.getIv().getIV());
                            ContentValues values = new ContentValues();
                            values.put("_id",this.psswrd.get_id());
                            values.put("Value",this.psswrd.getValue());
                            values.put("initVector",this.psswrd.getIv());
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
                        }else{
                            //Prompt the user the user name is already in use
                            MainActivity.displayToast(this,getResources().getString(R.string.snackBarPsswrdExists),Toast.LENGTH_LONG,Gravity.CENTER);
                            Log.d("onOptionsItemSelected","The user name "+ psswrdValue +" already exists in the DB.");
                        }//End of if else statement to check the user name is not in use
                    }else{
                        //Prompt the user the user name value has not changed
                        MainActivity.displayToast(this, getResources().getString(R.string.psswrdNotChanged), Toast.LENGTH_SHORT, Gravity.CENTER);
                    }//End of if else statement to check the user name changed
                }else{
                    //Prompt the user the user name field is empty
                    MainActivity.displayToast(this,getResources().getString(R.string.psswrdNotEntered),Toast.LENGTH_LONG,Gravity.CENTER);
                }
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

