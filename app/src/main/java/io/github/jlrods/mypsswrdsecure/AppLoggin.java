package io.github.jlrods.mypsswrdsecure;

import android.util.Log;

class AppLoggin extends Loggin {

    //Attribute expansion definition

    String name; // Name to be displayed on the drawer nave menu
    String email; // User's app email
    String message; //Message to be display on the drawer nav menu
    Icon picture; //Picture location of image to be displayed on the drawer nav menu


    //Constructors
    //Default Constructor
    public AppLoggin(){
        Log.d("AppLogginDef_Ent","Enter AppLoggin Default Constructor");
        this.userName = "";
        this.psswrd = "";
        this.name = "";
        this.email = "";
        this.message = "";
        this.picture = null;
        Log.d("AppLoggin_Def_Ext","Exit AppLoggin Default Constructor");
    }
    //Constructor with no password given
    public AppLoggin(String userName){
        Log.d("AppLoggin_NoPass_Ent","Enter AppLoggin Constructor 2");
        this.userName = userName;
        this.psswrd = "";
        this.name = "";
        this.email = "";
        this.message ="";
        this.picture = null;
        Log.d("AppLoggin_NoPass_Ext","Exit AppLoggin Constructor 2");
    }
    //Full Constructor
    public AppLoggin(String userName, String psswrd, String name, String email,String message, Icon picture){
        Log.d("AppLog_Full_Ent","Enter AppLoggin Full Constructor");
        this.userName = userName;
        this.psswrd = psswrd;
        this.name = name;
        this.email = email;
        this.message = message;
        this.picture = picture;
        Log.d("AppLog_Full_Ext","Exit AppLoggin Full Constructor");
    }

    //Getter and Setter methods

    public String getMessage() {
        return message;
    }

    public Icon getPicture() {
        return picture;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPicture(Icon picture) {
        this.picture = picture;
    }
}// End of AppUser
