package io.github.jlrods.mypsswrdsecure;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
}//End of AboutActivity class