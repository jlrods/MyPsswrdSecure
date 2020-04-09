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
        super();
        Log.d("AppLogginDef_Ent","Enter AppLoggin Default Constructor");
        this.name = "";
        this.email = "";
        this.message = "";
        this.picture = null;
        Log.d("AppLoggin_Def_Ext","Exit AppLoggin Default Constructor");
    }

    //Constructor with no password given
    public AppLoggin(UserName userName){
        Log.d("AppLoggin_NoPass_Ent","Enter AppLoggin Constructor 2");
        this.userName = userName;
        this.psswrd = null;
        this.name = "";
        this.email = "";
        this.message ="";
        this.picture = null;
        Log.d("AppLoggin_NoPass_Ext","Exit AppLoggin Constructor 2");
    }
    //Full Constructor
    public AppLoggin(int _id, UserName userName, Psswrd psswrd, String name, String email,String message, Icon picture){
        super(_id, userName, psswrd);
        Log.d("AppLog_Full_Ent","Enter AppLoggin Full Constructor");
        this.name = name;
        this.email = email;
        this.message = message;
        this.picture = picture;
        Log.d("AppLog_Full_Ext","Exit AppLoggin Full Constructor");
    }

    //Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
