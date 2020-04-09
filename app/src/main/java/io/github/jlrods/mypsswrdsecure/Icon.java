package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

class Icon extends StringValue{

    //Attribute definition
    String location; // File system location (URI)

    //Method definition

    //Constructors
    public Icon(){
        super();
        Log.d("Icon_Def_Ent","Enter Icon Default Constructor");
        this.location = "";
        Log.d("Icon_Full_Ext","Exit Icon Default Constructor");
    }
    public Icon(int _id, String name, String location){
        super(_id,name);
        Log.d("Icon_Full_Ent","Enter Icon Full Constructor");
        this.location = location;
        Log.d("Icon_Full_Ext","Exit Icon Full Constructor");
    }

    //Getters and Setters
    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.value = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int get_id() {
        return _id;
    }

    public String getName() {
        return this.value;
    }

    public String getLocation() {
        return location;
    }

    //Other methods
    public Icon extractIcon(Cursor c){
        Log.d("Ent_ExtractIcon","Enter extractIcon method in the Icon class.");
        //Initialize local variables
        Icon icon = null;
        String location ="";
        //Call common method from parent class to extract basic StringValue object data from a cursor
        ArrayList<Object> attributes = this.extractStrValue(c);
        location = c.getString(2);
        //Create a new Icon object by calling full constructor
        icon = new Icon((int) attributes.get(0), (String) attributes.get(1), location);
        Log.d("Ext_ExtractIcon","Exit extractIcon method in the Icon class.");
        return icon;
    }// End of extractIcon method

}// End of Icon class
