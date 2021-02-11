package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
//import android.os.Bundle;
//import android.preference.Preference;
//import android.preference.PreferenceFragment;
//import android.support.v7.preference.ListPreference;
//import android.support.v7.preference.PreferenceFragmentCompat;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

/**
 * Created by rodjose1 on 18/07/2018.
 */

//Class to handle the fragment to be set into the PreferencesActivity
public class PreferencesFragment extends androidx.preference.PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
/*    @Override
    public void onCreate(Bundle savedInstanceState) {

    }// End of constructor method*/

//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        //Call the super method
//        //super.onCreate(savedInstanceState);
//        //Set the layout
//        setPreferencesFromResource(R.xml.preferences, rootKey);
//        //addPreferencesFromResource(R.xml.preferences);
//
//        ListPreference themePreference = (ListPreference) findPreference("appTheme");
//        themePreference.setOnPreferenceChangeListener(this);
//        ListPreference dateFormatPreference = (ListPreference) findPreference("dateFormat");
//        dateFormatPreference.setOnPreferenceChangeListener(this);
//        ListPreference language = (ListPreference) findPreference("languages");
//        language.setOnPreferenceChangeListener(this);
//
//    }

//    @Override
//    public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object newValue) {
//        Log.e("preference", "Pending Preference value is: " + newValue);
//        if(preference.equals(findPreference("appTheme")) || preference.equals(findPreference("languages")) ){
//            Intent intent = new Intent(this.getContext(),MainActivity.class);
//            startActivity(intent);
//        }else if(preference.equals(findPreference("dateFormat"))){
//            MainActivity.setDateFormatChanged(true);
//        }
//        return true;
//    }

//    @Override
//    public boolean onPreferenceChange(Preference preference, Object newValue) {
//        Log.e("preference", "Pending Preference value is: " + newValue);
//        if(preference.equals(findPreference("appTheme")) || preference.equals(findPreference("languages")) ){
//            Intent intent = new Intent(this.getContext(),MainActivity.class);
//            startActivity(intent);
//        }else if(preference.equals(findPreference("dateFormat"))){
//            MainActivity.setDateFormatChanged(true);
//        }
//        return true;
//    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

                //Call the super method
        //super.onCreate(savedInstanceState);
        //Set the layout
        setPreferencesFromResource(R.xml.preferences, rootKey);
        //addPreferencesFromResource(R.xml.preferences);

        ListPreference themePreference = (ListPreference) findPreference("appTheme");
        themePreference.setOnPreferenceChangeListener(this);
        //ListPreference dateFormatPreference = (ListPreference) findPreference("dateFormat");
        //dateFormatPreference.setOnPreferenceChangeListener(this);
        ListPreference language = (ListPreference) findPreference("languages");
        language.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.e("preference", "Pending Preference value is: " + newValue);
        if(preference.equals(findPreference("appTheme")) || preference.equals(findPreference("languages")) ){
            Intent intent = new Intent(this.getContext(),MainActivity.class);
            startActivity(intent);
        }
        return true;
    }
}// End of PreferencesFragment class