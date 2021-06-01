package io.github.jlrods.mypsswrdsecure;

//Interface to define an app theme handler object
public interface ThemeHandler {
    //Method to retrieve the colour from current theme
    int fetchThemeColor(String colorName);
    //Method to extract current date format from the preferences
    String getDateFormat();
}//End if ThemeHandler interface
