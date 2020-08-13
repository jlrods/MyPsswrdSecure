package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public abstract class AddItemActivity extends AppCompatActivity {
    //Attributes
    protected ImageView imgAddActivityIcon;
    protected TextView tvAddActivityTag;
    protected EditText etNewItemField;
    //DB
    protected AccountsDB accountsDB;
    protected Cursor cursor;
    //Cryptographer object
    protected Cryptographer cryptographer = MainActivity.getCryptographer();

    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddItemActivity abstract class.");
        //Set layout for this activity
        setContentView(R.layout.activity_add_item);
        this.accountsDB = HomeFragment.getAccountsDB();
        //Find common view elements on the generic layout
        this.imgAddActivityIcon = (ImageView) findViewById(R.id.imgAddActivityIcon);
        this.tvAddActivityTag = (TextView) findViewById(R.id.tvAddActivityTag);
        this.etNewItemField = (EditText) findViewById(R.id.etNewItemField);
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddItemActivity class.");
    }//End of onCreate method

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the AddItemActivity class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the AddItemActivity class.");
        return true;
    }//End of onCreateOptionsMenu method

    // Method to check the menu item selected and execute the corresponding actions depending on the
    // item to be added (User name, password of question)
    @Override
    public  boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected boolean isFieldNotEmpty(View view){
        Log.d("isFieldNotEmpty","Enter isFieldNotEmpty method in the AddItemActivity abstract class.");
        boolean isNotEmpty = false;
        if(!((EditText)view).getText().toString().equals("")){
            isNotEmpty = true;
        }
        Log.d("isFieldNotEmpty","Exit isFieldNotEmpty method in the AddItemActivity abstract class.");
        return isNotEmpty;
    }//End of checkForEmptyField

    //Method to validate UI data before adding or updating an item (user name, psswrd, question)
    protected boolean isDataValid(int type, int itemID){
        Log.d("isDataValid","Enter isDataValid method in the AddItemActivity abstract class.");
        //Declare and initialize variables to be used in the method
        //Boolean flag to be returned
        boolean isValid = false;
        //Boolean flag to check for no change in the input value
        boolean checkItemValueChange = false;
        //Object used to check encrypted data against input value
        UserName item = null;
        //String Id values to change text in the error messages Toasts
        int itemValueNotEnteredStringID = -1;
        int itemValueNotChangedStringID = -1;
        int itemValueExistsStringID = -1;
        //Extract input value from edit text view
        String inputValue = this.etNewItemField.getText().toString().trim();
        //Switch statement to check the type of object passed in as parameter. This way proper data will be set up
        switch(type){
            case 1:
                this.cursor = accountsDB.getUserNameByName(inputValue);
                itemValueNotEnteredStringID = R.string.userNameNotEntered;
                itemValueExistsStringID = R.string.snackBarUserExists;
                break;
            case 2:
                this.cursor = accountsDB.getPsswrdByName(inputValue);
                itemValueNotEnteredStringID = R.string.psswrdNotEntered;
                itemValueExistsStringID = R.string.snackBarPsswrdExists;
                break;
            case 3:
                this.cursor = accountsDB.getUserNameByName(inputValue);
                itemValueNotEnteredStringID = R.string.userNameNotEntered;
                itemValueExistsStringID = R.string.snackBarUserExists;
                if(itemID != -1){
                    checkItemValueChange = true;
                    item = this.accountsDB.getUserNameByID(itemID);
                    itemValueNotChangedStringID = R.string.userNameNotChanged;
                }//End of if statement

                break;
            case 4:
                this.cursor = accountsDB.getPsswrdByName(inputValue);
                itemValueNotEnteredStringID = R.string.psswrdNotEntered;
                itemValueExistsStringID = R.string.snackBarPsswrdExists;
                if(itemID != -1){
                    checkItemValueChange = true;
                    item = this.accountsDB.getPsswrdByID(itemID);
                    itemValueNotChangedStringID = R.string.psswrdNotChanged;
                }//End of if statement
                break;
            default:
                break;
        }//End of switch statement
        //Start the validation checks
        //Check field isn't empty
        if(this.isFieldNotEmpty(this.etNewItemField)){
            //Check if changes in the value is required (where validation request comes from Edit Activities)
            if(checkItemValueChange){
                //Check current value in the edit field isn't the same already recorded on the DB
                if(!this.cryptographer.decryptText(item.getValue(),new IvParameterSpec(item.getIv())).trim().equals(inputValue)){
                    //Check the input value isn't in the DB already
                    if(this.cursor == null || this.cursor.getCount() ==0){
                        isValid = true;
                    }else{
                        //Prompt the user the user name input already exists in the list
                        MainActivity.displayToast(this,getResources().getString(itemValueExistsStringID),Toast.LENGTH_LONG,Gravity.CENTER);
                        Log.d("onOptionsItemSelected","The input value already exists in the DB.");
                    }
                }else{
                    //Prompt the user the user name value has not changed
                    MainActivity.displayToast(this, getResources().getString(itemValueNotChangedStringID), Toast.LENGTH_SHORT, Gravity.CENTER);
                }//End of if else statement to check the user name changed
            }else{
                if(this.cursor == null || this.cursor.getCount() ==0){
                    isValid = true;
                }else{
                    //Prompt the user the user name input already exists in the list
                    MainActivity.displayToast(this,getResources().getString(itemValueExistsStringID),Toast.LENGTH_LONG,Gravity.CENTER);
                    Log.d("onOptionsItemSelected","The input value already exists in the DB.");
                }
            }
        }else{
            //Display toast to prompt user the input field is empty
            MainActivity.displayToast(this,getResources().getString(itemValueNotEnteredStringID), Toast.LENGTH_LONG, Gravity.CENTER);
        }
        Log.d("isDataValid","Exit isDataValid method in the AddItemActivity abstract class.");
        return isValid;
    }//End of isDataValid method

    protected boolean isDataValid(int type){
        Log.d("isDataValid","Enter-Exit isDataValid overloaded method in the AddItemActivity abstract class.");
        return this.isDataValid(type,-1);
    }//End of isDataValid overloaded method
}//End of AddItemActivity abstract class
