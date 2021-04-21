package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

//Implementation of the ThemeHandler interface
public class ThemeUpdater  implements ThemeHandler{
    //Attributes definition
    private Context context;
    //Constructor method
    public ThemeUpdater(Context context){
        this.context = context;
    }//Endof ThemeUpdater constructor
    @Override
    //Method to retrieve the theme color resource id of the color name passed in as argument
    public int fetchThemeColor(String colorName) {
        Log.d("fetchThemeColor","Enter the fetchThemeColor method in the MainActivity class.");
        //Declare and initialize attribute color id
        int attributeColor = 0;
        //Check color name passed in as argument and assign it resource id to attributeColor variable
        switch(colorName){
            case "colorAccent":
                attributeColor = R.attr.colorAccent;
                break;
            case "colorPrimary":
                attributeColor = R.attr.colorPrimary;
                break;
            case "colorPrimaryDark":
                attributeColor = R.attr.colorPrimaryDark;
                break;
        }//End of switch statement
        //Create TypedValue object to hold the theme attribute data
        TypedValue value = new TypedValue ();
        //Call method to retrieve theme attribute data
        this.context.getTheme().resolveAttribute (attributeColor, value, true);
        Log.d("fetchThemeColor","Exit the fetchThemeColor method in the MainActivity class.");
        //return the data for the color required
        return value.data;
    }//End of fetchThemeColor method

    @Override
    public String getDateFormat(){
        Log.d("Ent_setDateFormat","Enter setDateFormat method in ThemeUpdater class.");
        //Get shared references info
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this.context);
        //Get the preference selected for date format
        String preferredDateFormat = pref.getString("dateFormat","0");
        Log.d("Ext_setDateFormat","Exit setDateFormat method in ThemeUpdater class.");
        //Assign the preferred value to the global variable
        return this.context.getResources().getStringArray(R.array.dateFormats)[Integer.parseInt(preferredDateFormat)];
    }//End of setDateFormat method
}//End of ThemeUpdater class
