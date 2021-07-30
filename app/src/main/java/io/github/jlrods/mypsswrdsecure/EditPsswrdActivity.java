package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
        //Set activity title
        getSupportActionBar().setTitle(R.string.editPsswrdTitle);
        int position = this.extras.getInt("position");
        //Extract password by passing in the _id attribute stored in the extras
        Cursor cursorPsswrd = this.accountsDB.getPsswrdCursorByID(this.extras.getInt("_id"));
        if(cursorPsswrd != null && cursorPsswrd.getCount() > 0){
            this.psswrd = Psswrd.extractPsswrd(cursorPsswrd);
        }
        //Set the edit text field with the password value after decryption
        this.etNewItemField.setText(this.cryptographer.decryptText(this.psswrd.getValue(),new IvParameterSpec(this.psswrd.getIv())));
        AppLoggin appLoggin = MainActivity.getCurrentAppLoggin();
        if(this.psswrd.get_id() != appLoggin.getPsswrd().get_id()){
            this.fabDelete.setVisibility(View.VISIBLE);
            //Set up fab onClick event listener by creating a new object of the sub class which handles this event
            this.fabDelete.setOnClickListener(
                    //Create the subclass object by passing in the text required to populate the AlertDialog box and the intent attribute name to be passed to caller activity
                    new FabOnClickEventHandler(psswrd,getResources().getString(R.string.psswrdDeleteTitle),
                            getResources().getString(R.string.psswrdDeleteMssg),
                            cryptographer.decryptText(psswrd.getValue(),new IvParameterSpec(psswrd.getIv())),
                            "itemDeleted",position)
            );
        }else{
            MainActivity.displayToast(this,getString(R.string.appLoginPsswrdPrompt),Toast.LENGTH_LONG,Gravity.CENTER);
        }//End of if else statement to check the current password display on the activity isn't part of app credentials
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
                //Get the new password text
                String psswrdValue = this.etNewItemField.getText().toString().trim();
                //Check for UI data validity
                if(this.isDataValid(4,this.psswrd.get_id())){
                    //Set the new encrypted value and IV
                    this.psswrd.setValue(this.cryptographer.encryptText(this.etNewItemField.getText().toString()));
                    this.psswrd.setIv(this.cryptographer.getIv().getIV());
                    //Store data to be updated on the DB
                    ContentValues values = new ContentValues();
                    values.put(MainActivity.getIdColumn(),this.psswrd.get_id());
                    values.put(MainActivity.getValueColumn(),this.psswrd.getValue());
                    values.put(MainActivity.getInitVectorColumn(),this.psswrd.getIv());
                    //Update the table
                    if(this.accountsDB.updateTable(MainActivity.getPsswrdTable(),values)){
                        //Go back to previous activity
                        intent.putExtra("psswrdID",this.psswrd.get_id());
                        intent.putExtra("psswrdValue",psswrdValue);
                        intent.putExtra("itemDeleted",false);
                        intent.putExtra("position",extras.getInt("position"));
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
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in EditPsswrdActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement
        return result;
    }//End of onOptionsItemSelected method
}//End of EditPsswrdActivity