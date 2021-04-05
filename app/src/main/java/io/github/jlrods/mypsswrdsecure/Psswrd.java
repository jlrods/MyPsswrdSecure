package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

// Class to handle Psswrd object definition
public class Psswrd extends UserName{
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
        int minLength = 7;
        int mediumLength = 9;
        int goodLength = 12;
        //Declare and initialize cryptographer object to decrypt password passed in as argument
        Cryptographer cryptographer = LoginActivity.getCryptographer();
        String password = cryptographer.decryptText(value,new IvParameterSpec(iv));
        //Check password length
        if(password.length() >= goodLength){
            strengthRating += 3;
        }else if(password.length() >= mediumLength && password.length() < goodLength){
            strengthRating += 2;
        }else if(password.length() >= minLength && password.length() < mediumLength){
            strengthRating += 1;
        }
        //Check if password has one special char at least
        if(password.matches(".*[!-/].*") || password.matches(".*[:-@].*")){
            strengthRating += 1;
            //Check if password has one special char or more
            if(password.matches(".*[!-/].*[!-/]+.*") || password.matches(".*[:-@].*[:-@]+.*") ||
                    (password.matches(".*[!-/].*") && password.matches(".*[:-@].*"))){
                strengthRating += 1;
            }
        }
        //Check if password has a combination of capital letters and lower case letters
        if(password.matches(".*[a-z]+.*") && password.matches(".*[A-Z]+.*")){
            strengthRating += 3;
        }else if(password.matches(".*[A-Z]+.*") || password.matches(".*[a-z]+.*")){
            strengthRating += 2;
        }
        //Check if password has one number at least
        if(password.matches(".*\\d.*")){
            strengthRating += 1;
            //Check if password has one number or more
            if(password.matches(".*\\d.*\\d.*")){
                strengthRating += 1;
            }
        }//End of if chain to check password composition

        //Check the result of assessment against corresponding values in the scale to assign final password strength
        switch (strengthRating){
            case 4:
            case 5:
                strength = PsswrdStrength.WEAK;
                break;
            case 6:
            case 7:
                strength = PsswrdStrength.MEDIUM;
                break;
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
