package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by rodjose1 on 18/07/2018.
 */

//Activity to handle the AboutActivity app info
public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Call method to setup current app theme
        setTheme( MainActivity.setAppTheme(this));
        //Set language as per preferences
        MainActivity.setAppLanguage(this);
        //Call super method
        super.onCreate(savedInstanceState);
        //set fragment for preferences

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();

    }// End of constructor method

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResumeMain", "Enter onResume method in PreferencesActivity class.");
        //Call MainActivity static method to check for logout timeout to display logout prompt accordingly
        MainActivity.checkLogOutTimeOut(this);
        Log.d("onResumeMain", "Exit onResume method in PreferencesActivity class.");
    }//End of onResume method

    public void onStop(){
        super.onStop();
        Log.d("onStopPref", "Enter onStop method in PreferencesActivity class.");
        //Call method to check for notification sent and update if required. Pass in as argument the flag to identify if auto log out has timed out
        MainActivity. checkForNotificationSent(this,MainActivity.checkIsAppLoggedOut());
        Log.d("onStopPref", "Exit onStop method in PreferencesActivity class.");
    }//End of onStop method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Call method to check for notification sent and update if required. Pass in as argument the flag to identify if auto log out has timed out
        MainActivity.checkForNotificationSent(this,MainActivity.checkIsAppLoggedOut());
        Log.d("onDestroyPref", "Enter/Exit onDestroy method in PreferencesActivity class.");
    }//End of onDestroy method

    //Method to populate menu object and configure menu items visibility
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in PreferencesActivity  class.");
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        Log.d("onCreateOptionsMenu","Exit onCreateOptionsMenu method in PreferencesActivity  class.");
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in PreferencesActivity  class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in PreferencesActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in PreferencesActivity class.");
        finish();
        return result;
    }//End of  onOptionsItemSelected method
}//End of PreferencesActivity class