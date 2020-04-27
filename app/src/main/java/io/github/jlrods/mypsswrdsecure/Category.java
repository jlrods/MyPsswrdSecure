package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
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

    //Other methods
    //Method to extract a Category from a cursor object
    public static Category extractCategory(Cursor c){
        Log.d("Ent_ExtractCategory","Enter extractCategory method in the Category class.");
        //Declare and initialize a null category object, the one to be returned by the method
        Category category =null;
        //Declare an int variable to hold the Category id retrieved from the cursor
        int id;
        //Declare a string object to hold the name attribute retrieved from the cursor
        String name="";
        //Retrieve the id value from the cursor object
        id = c.getInt(0);
        //Retrieve the name value from the cursor object
        name = c.getString(1);
        int icon;
        icon = c.getInt(2);
        if(icon > 0){
            Icon iconObject = new Icon();
            category = new Category (id, name, iconObject);
        }else{
            //Create a new Category object by using the no icon constructor
            category = new Category(id,name);
        }//End of if else statement

        Log.d("Ext_ExtractCategory","Exit extractCategory method in the Category class.");
        //Return the category object
        return category;
    }//End of extractCategory method
}// End of Category class