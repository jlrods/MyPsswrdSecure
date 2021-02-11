package io.github.jlrods.mypsswrdsecure;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Created by rodjose1 on 18/07/2018.
 */

//Activity to handle the AboutActivity app info
public class PreferencesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get default current property from preferences
        //SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
        //String preferedThemeID = pref.getString("appTheme","0");
        /*int themeId;
        if(preferedThemeID.equals("1")){
            themeId = R.style.AppTheme1;
        }else if(preferedThemeID.equals("2")){
            themeId = R.style.AppTheme2;
        }else{
            themeId = R.style.AppTheme;
        }
        setTheme(themeId);*/
        //setTheme(R.style.AppThemeHulk);
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


        /*getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();*/
    }// End of constructor method


}//End of PreferencesActivity class