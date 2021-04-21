package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

/**
 * Created by rodjose1 on 18/07/2018.
 */

//Class to handle the fragment to be set into the PreferencesActivity
public class PreferencesFragment extends androidx.preference.PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Set the layout
        setPreferencesFromResource(R.xml.preferences, rootKey);
        //addPreferencesFromResource(R.xml.preferences);

        ListPreference themePreference = (ListPreference) findPreference("appTheme");
        themePreference.setOnPreferenceChangeListener(this);
        //ListPreference dateFormatPreference = (ListPreference) findPreference("dateFormat");
        //dateFormatPreference.setOnPreferenceChangeListener(this);
        ListPreference language = (ListPreference) findPreference("languages");
        language.setOnPreferenceChangeListener(this);

        SwitchPreference isLogOutActive = (SwitchPreference) findPreference("isAutoLogOutActive");
        ListPreference timeOutTime = (ListPreference) findPreference("logOutTime");
        timeOutTime.setEnabled(isLogOutActive.isChecked());
        isLogOutActive.setOnPreferenceChangeListener(this);
    }//End of onCreatePreferences method

    //Method to catch changes on preferences
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("preference", "Pending Preference value is: " + newValue);
        if(preference.equals(findPreference("appTheme")) || preference.equals(findPreference("languages")) ){
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(((SwitchPreference)preference).equals(findPreference("isAutoLogOutActive"))){
            ListPreference timeOutTime = (ListPreference) findPreference("logOutTime");
            //Set new state of switch as thenew time out time preference visibility
            timeOutTime.setEnabled((boolean) newValue);
        }// End of if else statements to check what preference changed
        return true;
    }//End of onPreferenceChange method
}// End of PreferencesFragment class