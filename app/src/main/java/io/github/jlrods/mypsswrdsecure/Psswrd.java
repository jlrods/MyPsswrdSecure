package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

// Class to handle Psswrd object definition
class Psswrd extends UserName{
    //Attribute definition
    PsswrdStrength strength;// Attribute that handles how strong a password is
    //Method definition

    //Constructor




    public Psswrd(int _id, String value, PsswrdStrength strength){
        super(_id,value);
        Log.d("PsswrdFullConst","Enter Full Constructor of Psswrd class .");
        this.strength = strength;
        Log.d("PsswrdFullConst","Exit Full Constructor of Psswrd class .");
    }//End of Psswrd full constructor

    public Psswrd(int _id, String value){

        this(_id,value, PsswrdStrength.WEAK);
    }

    public Psswrd(String value){
        this(-1, value);
    }

    public Psswrd(){
        this("");
    }

    @NonNull
    @Override
    public String toString() {
        Log.d("Psswrd_ToStr_Ent","Enter Psswrd class ToString method");
        return "Password ID: " + this._id +"\nValue: " + this.value+"\nStrength: "+this.strength.toString();
    }//End of toString method

    public PsswrdStrength getStrength() {
        return strength;
    }

    public void setStrength(PsswrdStrength strength) {
        this.strength = strength;
    }

    //Method to extract a password from a cursor object
    public static Psswrd extractPsswrd(Cursor c){
        Log.d("Ent_ExtractPsswrd","Enter extractPsswrd method in the Psswrd class.");
        //Initialize local variables
        Psswrd psswrd = null;
        //Call common method from parent class to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = extractStrValue(c);
        //Create a new psswrd object by calling full constructor
        psswrd = new Psswrd((int) attributes.get(0), (String) attributes.get(1), definePsswrdStrength((String) attributes.get(1)));

        Log.d("Ext_ExtractPsswrd","Exit extractPsswrd method in the Psswrd class.");
        return psswrd;
    }// End of extractPsswrd method


    public static PsswrdStrength definePsswrdStrength(String psswrd){
        //FIXME: To be upaated with better password strength assessment algorithm
        if(psswrd.contains("!")){
            return PsswrdStrength.STRONG;
        }else if(psswrd.contains("8")){
            return  PsswrdStrength.MEDIUM;
        }else{
            return  PsswrdStrength.WEAK;
        }
    }//End of definePsswrdStrength
}// End of Psswrd class
