package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    ThemeUpdater themeUpdater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreate", "Enter onCreate method in the AboutActivity abstract class.");
        this.themeUpdater = new ThemeUpdater(this);
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Set correct language
        MainActivity.setAppLanguage(this);
        //Set layout for this activity
        setContentView(R.layout.activity_about);
        TextView tvSwInfo = findViewById(R.id.tvSwInfo);
        tvSwInfo.setTextColor(this.themeUpdater.fetchThemeColor("colorAccent"));
        TextView tvAboutAuthor = findViewById(R.id.tvAboutAuthor);
        tvAboutAuthor.setTextColor(this.themeUpdater.fetchThemeColor("colorAccent"));
        TextView tvAuthorWebsite = findViewById(R.id.tvAuthorWebsite);
        tvAuthorWebsite.setMovementMethod(LinkMovementMethod.getInstance());
        TextView tvAuthorWebsiteDownloads = findViewById(R.id.tvAuthorWebsiteDownloads);
        tvAuthorWebsiteDownloads.setMovementMethod(LinkMovementMethod.getInstance());
        Log.d("OnCreate", "Exit onCreate method in the AboutActivity abstract class.");
    }//End of onCreate method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in AboutActivity abstract class.");
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        Log.d("onCreateOptionsMenu","Exit onCreateOptionsMenu method in AboutActivity abstract class.");
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AboutActivity abstract class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.action_logout:
                //Call method to throw LoginActivity and clear activity stack.
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in AboutActivity abstract class.");
                MainActivity.logout(this);
        }
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AboutActivity class.");
        finish();
        return result;
    }//End of onOptionsItemSelected method
}//End of AboutActivity class