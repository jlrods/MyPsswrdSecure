package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

// Class to handle Psswrd object definition
class Psswrd extends UserName{
    //Attribute definition
    PsswrdStrength strength;// Attribute that handles how strong a password is
    //Method definition

    //Constructor
    public Psswrd(int _id, byte[] value, byte[] iv,long dateCreated ,PsswrdStrength strength){
        super(_id,value,iv,dateCreated);
        Log.d("PsswrdFullConst","Enter Full Constructor of Psswrd class.");
        this.strength = strength;
        Log.d("PsswrdFullConst","Exit Full Constructor of Psswrd class.");
    }//End of Psswrd full constructor

    public Psswrd(int _id, byte[] value , byte[] iv,PsswrdStrength strength){
        super (_id,value,iv);
        this.strength = strength;
        Log.d("PsswrdConst","Exit Constructor of Psswrd class with 4 arguments.");
    }//End of Psswrd full constructor

    public Psswrd(int _id, byte[] value,byte[] iv, long dateCreated){
        super(_id,value,iv,dateCreated);
        //Call inner method to calculate the password strength to be displayed on the RV
        this.strength = calculatePsswrdStrength(value, iv);
        Log.d("PsswrdConst2","Exit Constructor of Psswrd class with 3 arguments.");
    }

    public Psswrd(int _id, byte[] value,byte[] iv){
        super(_id,value,iv);
        this.strength = PsswrdStrength.WEAK;
        Log.d("PsswrdConst2","Exit Constructor of Psswrd class with 3 arguments.");
    }

    public Psswrd(byte[] value, byte[] iv){
        this(-1, value,iv);
        Log.d("PsswrdConst3","Exit Constructor of Psswrd class with 2 arguments.");
    }

    public Psswrd(){
        this(-1,null,null);
        Log.d("PsswrdConst4","Exit Constructor of Psswrd class with no arguments.");
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
        psswrd = new Psswrd((int) attributes.get(0), (byte[]) attributes.get(1), (byte[]) attributes.get(2), c.getLong(3));
        Log.d("Ext_ExtractPsswrd","Exit extractPsswrd method in the Psswrd class.");
        return psswrd;
    }// End of extractPsswrd method


    //Find a system by receiving the ordinal
    public static PsswrdStrength calculatePsswrdStrength(byte[] value, byte[] iv){
        Log.d("calculatePsswrdStrength","Enter calculatePsswrdStrength method in the Psswrd class.");
        //Declare and instantiate strength object to be returned by method
        PsswrdStrength strength = null;
        //Declare and initialize variables to be used as criteria for assessment
        int strengthRating = 0;
        int minLength = 10;
        //Declare and initialize cryptographer object to decrypt password passed in as argument
        Cryptographer cryptographer = MainActivity.getCryptographer();
        String password = cryptographer.decryptText(value,new IvParameterSpec(iv));
        //Check password length
        if(password.length() >= minLength){
            strengthRating += 3;
        }else if(password.length() > 5 && password.length() < minLength){
            strengthRating += 1;
        }
        //Check if password has one special char at least
        if(password.matches(".*[!-/]+.*")){
            strengthRating += 2;
        }
        //Check if password has a combination of capital letters and lower case letters
        if(password.matches(".*[A-Z]+.*") && password.matches(".*[a-z]+.*")){
            strengthRating += 3;
        }
        //Check if password has one number at least
        if(password.matches(".*\\d+.*")){
            strengthRating += 2;
        }//End of if chain to check password composition

        //Check the result of assessment against corresponding values in the scale to assign final password strength
        switch (strengthRating){
            case 3:
            case 4:
                strength = PsswrdStrength.WEAK;
                break;
            case 5:
            case 6:
                strength = PsswrdStrength.MEDIUM;
                break;
            case 7:
            case 8:
            case 9:
                strength = PsswrdStrength.STRONG;
                break;
            case 10:
                strength = PsswrdStrength.VERY_STRONG;
                break;
            default:
                strength = PsswrdStrength.VERY_WEEK;
                break;
        }//End of switch statement
        Log.d("calculatePsswrdStrength","Exit calculatePsswrdStrength method in the Psswrd class.");
        return strength;
    }// End of findProperty
}// End of Psswrd class
