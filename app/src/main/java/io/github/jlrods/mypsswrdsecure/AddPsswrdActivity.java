package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

public class AddPsswrdActivity extends AddItemActivity {
    //Attributes to be used while saving the new question
    protected Psswrd psswrd = null;
    //Method definition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddPsswrdActivity class.");
//        if(this.extras.getBoolean("isActivityThrownFromDisplayAct")){
//            this.isRunDecryptService = this.extras.getBoolean("isActivityThrownFromDisplayAct",false);
//        }
        //Set activity title
        getSupportActionBar().setTitle(R.string.addPsswrdTitle);
        //Update layout fields according to Add Security question layout
        this.imgAddActivityIcon.setImageResource(R.mipmap.ic_pencil_lock_black_48dp);
        this.tvAddActivityTag.setText(R.string.account_psswrd);
        this.etNewItemField.setHint(R.string.alertBoxNewPsswrdMssg);
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddPsswrdActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddPsswrdActivity class.");
        //Boolean to return method result
        boolean result = false;
        int psswrdID = -1;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in AddPsswrdActivity class.");
                //Create Psswrd object
                String psswrdValue = this.etNewItemField.getText().toString().trim();
                byte[] psswrdValueEncrypted =  null;
                //Check for UI data validation
                if(this.isDataValid(2)){
                    //Encrypt the Psswrd
                    psswrdValueEncrypted = this.cryptographer.encryptText(psswrdValue);
                    //If Psswrd not in the list create new Psswrd object and store it in global variable used to build the account object
                    this.psswrd = new Psswrd(psswrdValueEncrypted,this.cryptographer.getIv().getIV());
                    //Call DB method to insert  the Psswrd object into the DB
                    psswrdID = this.accountsDB.addItem(this.psswrd);
                    if(psswrdID > 0 ){
//                        if(this.isRunDecryptService){
//                            Intent decryptDataService = new Intent(this, DecryptDataService.class);
//                            startService(decryptDataService);
//                        }
                        //Update the Psswrd object ID and prepare data to exit the activity
                        this.psswrd.set_id(psswrdID);
                        intent.putExtra("psswrdID",this.psswrd.get_id());
                        intent.putExtra("psswrdValue",psswrdValue);
                        setResult(RESULT_OK, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The password "+ psswrdValue +" has been added into the DB through onOptionsItemSelected method in the AddPsswrdActivity class.");
                    }else{
                        //Prompt the Psswrd input failed to be inserted in the DB
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarPsswrdNotAdded), Toast.LENGTH_LONG, Gravity.CENTER);
                        //Set activity result as cancelled so caller activity can decide what to do if this is the case
                        setResult(RESULT_CANCELED, intent);
                        finish();
                        Log.d("onOptionsItemSelected","The password "+ psswrdValue +" has NOT been added into the DB through onOptionsItemSelected method in the AddPsswrdActivity class, due to DB problem.");
                    }//End of if else statement to check  password was saved in DB
                }//End of if statement that check UI data is valid
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so caller activity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddPsswrdActivity class.");
                finish();
                break;
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in AddPsswrdActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement to check the menu option selected
        Log.d("onOptionsItemSelected","Exit onOptionsItemSelected method in AddPsswrdActivity class.");
        return result;
    }//End of onOptionsItemSelected method
}//End of AddPsswrdActivity class