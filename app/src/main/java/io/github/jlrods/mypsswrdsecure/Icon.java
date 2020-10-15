package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

class Icon {

    //Attribute definition
    private int _id;
    private String name;
    private String location; // File system location (URI)
    private boolean isSelected; // Bool flat to determine if icon has been selected on RecyclerView
    private int resourceID;

    //Method definition

    //Constructors
    public Icon(int _id, String name, String location,boolean isSelected, int resourceID){
        Log.d("Icon_Full_Ent","Enter Icon Full Constructor");
        this._id = _id;
        this.name = name;
        this.location = location;
        this.isSelected = isSelected;
        this.resourceID = resourceID;
        Log.d("Icon_Full_Ext","Exit Icon Full Constructor");
    }
    public Icon(String name, String location){
        this(-1,name,location,false,-1);
        Log.d("Icon_Full_Ext","Exit Icon Constructor with no arguments");
    }

    public Icon(String name, String location, int resourceID){
        this(-1,name,location,false,resourceID);
        Log.d("Icon_Full_Ext","Exit Icon Constructor with no arguments");
    }

    public Icon(String name, String location, boolean isSelected,int resourceID){
        this(-1,name,location,isSelected,resourceID);
        Log.d("Icon_Full_Ext","Exit Icon Constructor with no arguments");
    }

    public Icon(int _id, String name, String location, boolean isSelected){
        this(_id,name,location,isSelected,-1);
        Log.d("Icon_Full_Ext","Exit Icon Constructor with no arguments");
    }

    public Icon(){
        this(-1,"","",false,-1);
        Log.d("Icon_Full_Ext","Exit Icon Constructor with no arguments");
    }



    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
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
        return this._id;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    //Other methods
    public static Icon extractIcon(Cursor c){
        Log.d("Ent_ExtractIcon","Enter extractIcon method in the Icon class.");
        //Initialize local variables
        int _id;
        String name;
        Icon icon = null;
        String location ="";
        int isSelected;
        _id = c.getInt(0);
        name = c.getString(1);
        location = c.getString(2);
        isSelected = c.getInt(3);
        //Create a new Icon object by calling full constructor
        icon = new Icon(_id, name, location,AccountsDB.toBoolean(isSelected));
        Log.d("Ext_ExtractIcon","Exit extractIcon method in the Icon class.");
        return icon;
    }// End of extractIcon method

}// End of Icon class
