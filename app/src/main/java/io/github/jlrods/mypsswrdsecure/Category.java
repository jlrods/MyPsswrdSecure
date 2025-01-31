package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

public class Category {

    //Attribute definition
    private int _id; // DB unique ID
    private String name; // Category description name
    private Icon icon; // Category Icon image

    //Method definition
    //Constructors
    public Category(int _id, String name, Icon icon){
        Log.d("Cat_Full_Ent","Enter Category  No Icon Constructor in Category class.");
        this._id = _id;
        this.name = name;
        this.icon = icon;
        Log.d("Cat_Full_Ext","Exit Category Full Constructor in Category class.");
    }//End of Category constructor

    public Category(){
        this(-1,"",null);
        Log.d("Cat_Def_Ent","Enter Category  DefaultConstructor in Category class.");
    }//End of Category constructor

    public Category(int _id, String name){
        this(_id,name,null);
        Log.d("Cat_NoIcon_Ent","Enter Category No Icon Constructor in Category class.");
    }//End of Category constructor

    public Category(String name, Icon icon){
        this(-1,name,icon);
        Log.d("Cat_NoIcon_Ent","Enter Category No Icon Constructor in Category class.");
    }//End of Category constructor


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
        int iconID;
        iconID = c.getInt(2);
        if(iconID > 0){
            AccountsDB accountsDB = MainActivity.getAccountsDB();
            Icon icon = accountsDB.getIconByID(iconID);
            category = new Category (id, name, icon);
        }else{
            //Create a new Category object by using the no icon constructor
            category = new Category(id,name);
        }//End of if else statement

        Log.d("Ext_ExtractCategory","Exit extractCategory method in the Category class.");
        //Return the category object
        return category;
    }//End of extractCategory method

    @Override
    public String toString(){

        Log.d("Ent_ToStringCategory","Enter ToString method in the Category class.");
        String separator = "; ";
        String categoryString = "";
        //categoryString = super.toString();
        categoryString = categoryString.concat(this.getName())
                .concat(separator).concat(String.valueOf(this.icon.get_id()))
                .concat(separator).concat(this.icon.getName());
        Log.d("Ext_ToStringCategory","Exit ToString method in the Category class.");
        return categoryString;
    }// End of ToString method

}// End of Category class