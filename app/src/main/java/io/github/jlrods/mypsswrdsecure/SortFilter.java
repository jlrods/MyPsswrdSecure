package io.github.jlrods.mypsswrdsecure;

import android.util.Log;

public enum SortFilter {
    //Define the possible priorities in this app
    ALPHA_ASC("AlphaUp"),
    ALPHA_DES("AlphaDown"),
    DATE_ASC("DateUp"),
    DATE_DES("DateDown"),
    CATEGORY("Category"),
    TIMES_USED("TimesUsed");

    //Attributes definition
    private String name;

    //Method definition

    //Full constructor
    SortFilter(String name){
        Log.d("PsswrdStrength","Enter full constructor in the PsswrdStrength enum.");
        this.name = name;
        Log.d("PsswrdStrength","Exit full constructor in the PsswrdStrength enum.");
    }//End of Full Category constructor

    //Getters and Setters

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Method to print password strength name when a string is required
    public String toString(){
        return this.getName();
    }//End of toString method

    //Method to increase the System ordinal
    public int increaseOrdinal(){
        return this.ordinal()+1;
    }
}
