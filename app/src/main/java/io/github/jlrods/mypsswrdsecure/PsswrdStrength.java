package io.github.jlrods.mypsswrdsecure;

import android.util.Log;

enum PsswrdStrength {
    //Define the possible priorities in this app
    WEAK("Weak"),
    MEDIUM("Medium"),
    STRONG("Strong"),
    VERY_STRONG("Very Strong");

    //Attributes definition
    private String name;

    //Method definition

    //Full constructor
    PsswrdStrength(String name){
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

    //Find a system by receiving the ordinal
    public static PsswrdStrength findPsswrdStrengthById(int ordinal){
        Log.d("FindStrengthById","Ent findPsswrdStrengthById method in the PsswrdStrength enum.");
        //Declare and instantiate Unit_System object to be returned by method
        PsswrdStrength strength = null;
        //Check the ordinal against the possible ordinals corresponding to each enum item
        switch (ordinal){
            case 2:
                strength = PsswrdStrength.MEDIUM;
                break;
            case 3:
                strength = PsswrdStrength.STRONG;
                break;
            case 4:
                strength = PsswrdStrength.VERY_STRONG;
                break;
            default:
                strength = PsswrdStrength.WEAK;
                break;
        }//End of switch statement
        Log.d("FindStrengthById","Exit findPsswrdStrengthById method in the PsswrdStrength enum.");
        return strength;
    }// End of findProperty
}//End of PsswrdStrength enum
