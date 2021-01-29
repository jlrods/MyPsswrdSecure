package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

public class ThemeUpdater  implements ThemeHandler{
    private Context context;
    public ThemeUpdater(Context context){
        this.context = context;
    }
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
}//End of ThemeUpdater class
