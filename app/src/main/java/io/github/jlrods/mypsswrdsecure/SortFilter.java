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

    //Method to get the corresponding SortFilter type based on the ordinal passed in as argument
    public static SortFilter getSortFilterByOrdinal(int ordinal){
        SortFilter sortFilter;
        int i = 0;
        if(ordinal == SortFilter.ALPHA_ASC.ordinal()){
            sortFilter = SortFilter.ALPHA_ASC;
        }else if(ordinal == SortFilter.ALPHA_DES.ordinal()){
            sortFilter = SortFilter.ALPHA_DES;
        }else if(ordinal == SortFilter.DATE_ASC.ordinal()){
            sortFilter = SortFilter.DATE_ASC;
        }else if(ordinal == SortFilter.DATE_DES.ordinal()){
            sortFilter = SortFilter.DATE_DES;
        }else if(ordinal == SortFilter.CATEGORY.ordinal()){
            sortFilter = SortFilter.CATEGORY;
        }else if(ordinal == SortFilter.TIMES_USED.ordinal()){
            sortFilter = SortFilter.TIMES_USED;
        }else{
            sortFilter = null;
        }
        return sortFilter;
    }//End of getSortFilterByOrdinal method
}//End of SortFilter enum definition
