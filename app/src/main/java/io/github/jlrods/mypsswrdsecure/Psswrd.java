package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

import java.util.ArrayList;

// Class to handle Psswrd object definition
class Psswrd extends UserName{
    //Attribute definition
    PsswrdStrength strength;// Attribute that handles how strong a password is
    //Method definition

    //Constructor
    public Psswrd(int _id, byte[] value,long dateCreated , byte[] iv,PsswrdStrength strength){
        super(_id,value,dateCreated,iv);
        Log.d("PsswrdFullConst","Enter Full Constructor of Psswrd class.");
        this.strength = strength;
        Log.d("PsswrdFullConst","Exit Full Constructor of Psswrd class.");
    }//End of Psswrd full constructor

    public Psswrd(int _id, byte[] value , byte[] iv,PsswrdStrength strength){
        super (_id,value,iv);
        this.strength = strength;
        Log.d("PsswrdConst","Exit Constructor of Psswrd class with 4 arguments.");
    }//End of Psswrd full constructor

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
        psswrd = new Psswrd((int) attributes.get(0), (byte[]) attributes.get(1), (byte[]) attributes.get(2),PsswrdStrength.VERY_STRONG);

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
