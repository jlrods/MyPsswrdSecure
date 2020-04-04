package io.github.jlrods.mypsswrdsecure;

import android.util.Log;

class Category {

    //Attribute definition
    int _id; // DB unique ID
    String name; // Category description name
    Icon icon; // Category Icon image

    //Method definition
    //Constructors
    public Category(){
        Log.d("Cat_Def_Ent","Enter Category  DefaultConstructor");
        this._id = 0;
        this.name = "";
        this.icon = null;
        Log.d("Cat_Def_Ext","Exit Category Default Constructor");
    }

    public Category(int _id, String name){
        Log.d("Cat_NoIcon_Ent","Enter Category No Icon Constructor");
        this._id = _id;
        this.name = name;
        this.icon = null;
        Log.d("Cat_NoIcon_Ext","Enter Category No Icon Constructor");
    }

    public Category(int _id, String name, Icon icon){
        Log.d("Cat_Full_Ent","Enter Category  No Icon Constructor");
        this._id = _id;
        this.name = name;
        this.icon = icon;
        Log.d("Cat_Full_Ext","Exit Category Full Constructor");
    }


    //Getters and Setters
    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}// End of Category class
