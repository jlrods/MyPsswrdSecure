package io.github.jlrods.mypsswrdsecure;

public interface ThemeHandler {
    //Method to retrieve the colour from current theme
    int fetchThemeColor(String colorName);
    //Method to extract current date format from the preferences
    String getDateFormat();
}//Ebd if ThemeHandler interface
