package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
}//End of AddItemActivity abstract class
