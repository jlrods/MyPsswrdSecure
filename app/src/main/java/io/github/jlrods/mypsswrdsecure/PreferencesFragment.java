package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

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
            MainActivity.logout(this.getContext());
        }else if(((SwitchPreference)preference).equals(findPreference("isAutoLogOutActive"))){
            ListPreference timeOutTime = (ListPreference) findPreference("logOutTime");
            //Set new state of switch as the new time out time preference visibility
            timeOutTime.setEnabled((boolean) newValue);
            if((boolean)newValue){
                Intent autoLogOutService = new Intent(getContext(), AutoLogOutService.class);
                getActivity().startService(autoLogOutService);
            }else{
                Intent autoLogOutService = new Intent(getContext(), AutoLogOutService.class);
                getActivity().stopService(autoLogOutService);
            }
            CountDownTimer delayTimer = new CountDownTimer(2000,250) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("onTick", "Enter/Exit  CountDownTimer onTick method  in PreferenceFragment class.");
                }//End of onTick method

                @Override
                public void onFinish() {
                    ((LogOutTimer)AutoLogOutService.getLogOutTimer()).setContext(getActivity());
                    Log.d("onFinish", "Enter/Exit  CountDownTimer onFinish method  in PreferenceFragment class.");
                }//End of onFinish method
            };
            delayTimer.start();
        }// End of if else statements to check what preference changed
        return true;
    }//End of onPreferenceChange method
}// End of PreferencesFragment class