package io.github.jlrods.mypsswrdsecure;

import android.util.Log;

class Icon {

    //Attribute definition
    int _id;// DB unique ID
    String name; // Icon description
    String location; // File system location (URI)

    //Method definition

    //Constructors
    public Icon(){
        Log.d("Icon_Def_Ent","Enter Icon Default Constructor");
        this._id = 0;
        this.name = "";
        this.location = "";
        Log.d("Icon_Full_Ext","Exit Icon Default Constructor");
    }
    public Icon(int _id, String name, String location){
        Log.d("Icon_Full_Ent","Enter Icon Full Constructor");
        this._id = _id;
        this.name = name;
        this.location = location;
        Log.d("Icon_Full_Ext","Exit Icon Full Constructor");
    }

    //Getters and Setters
    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}// End of Icon class
