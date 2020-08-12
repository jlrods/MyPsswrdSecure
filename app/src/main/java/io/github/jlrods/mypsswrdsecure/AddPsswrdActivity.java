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
                //Check the password field isn't empty
                if(this.isFieldNotEmpty(this.etNewItemField)){
                    //Check the Psswrd isn't in the DB already
                    this.cursor = accountsDB.getPsswrdByName(psswrdValue);
                    if(this.cursor == null || this.cursor.getCount() ==0){
                        //Encrypt the Psswrd
                        psswrdValueEncrypted = this.cryptographer.encryptText(psswrdValue);
                        //If Psswrd not in the list create new Psswrd object and store it in global variable used to build the account object
                        this.psswrd = new Psswrd(psswrdValueEncrypted,this.cryptographer.getIv().getIV());
                        //Call DB method to insert  the Psswrd object into the DB
                        psswrdID = this.accountsDB.addItem(this.psswrd);
                        if(psswrdID > 0 ){
                            //Update the Psswrd object ID and prepare data to exit the activity
                            this.psswrd.set_id(psswrdID);
                            intent.putExtra("answerID",this.psswrd.get_id());
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
                    }//End of if else statement to check password isn't in the DB
                    else {
                        //Prompt password input already exists in the list
                        MainActivity.displayToast(this,getResources().getString(R.string.snackBarPsswrdExists),Toast.LENGTH_LONG,Gravity.CENTER);
                        Log.d("onOptionsItemSelected","The password "+ psswrdValue +" already exists in the DB.");
                    }//End of if else statement to check password in DB
                }else{
                    //Display toast to prompt user the input field is empty
                    MainActivity.displayToast(this,getResources().getString(R.string.psswrdNotEntered),Toast.LENGTH_LONG,Gravity.CENTER);
                }//End of if else statement to check input field isn't empty
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so caller activity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddPsswrdActivity class.");
                finish();
                break;
        }//End of switch statement to check the menu option selected
        Log.d("onOptionsItemSelected","Exit onOptionsItemSelected method in AddPsswrdActivity class.");
        return result;
    }//End of onOptionsItemSelected method
}//End of AddPsswrdActivity class
