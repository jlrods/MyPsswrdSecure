package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

class Account extends Loggin {
    // Attribute definition
    Category category; // Account type
    ArrayList<Question> questionList; // List of Question objects (up to 3 questions stored in DB)
    Icon icon; // Icon object to store URI icon location
    boolean isFavorite; // Boolean attribute to identify the account as Favorite or not

    //Method definition

    //Constructors
    public Account(int _id, UserName userName, Psswrd psswrd, Category category,
                   ArrayList<Question> questionList, Icon icon, boolean isFavorite){
        super(_id,userName, psswrd);
        Log.d("AcctFull_Ent","Enter Account Full Constructor");
        this.category = category;
        this.questionList = questionList;
        this.icon = icon;
        this.isFavorite = isFavorite;
        Log.d("AcctFull_Ext","Exit Account Full Constructor");
    }

    public Account(){
        this(0,null,null,null,null,null,false);
        Log.d("AcctDef_Ext","Account Default Constructor called Full Account Constructor");
    }

    public Account(int _id, UserName userName, Psswrd psswrd, Category category){
        this(_id,userName, psswrd,category,null,null,false);
        Log.d("Acct2_Ext","Account Constructor2 called Full Account Constructor");
    }

    public Account(int _id, UserName userName, Psswrd psswrd, Category category,
                   ArrayList<Question> questionList, boolean isFavorite){
        this(_id,userName, psswrd,category,questionList,null,isFavorite);
        Log.d("Acct3_Ext","Account Constructor3 called Full Account Constructor");
    }

    public Account(int _id, UserName userName, Psswrd psswrd, Category category,
                   ArrayList<Question> questionList, Icon icon){
        this(_id,userName, psswrd,category,questionList,icon,false);
        Log.d("Acct4_Ext","Account Constructor4 called Full Account Constructor");
    }

    public Account(int _id, UserName userName, Psswrd psswrd, Category category, Icon icon,
                   boolean isFavorite){
        this(_id,userName, psswrd,category,null,icon,isFavorite);
        Log.d("Acct5_Ext","Account Constructor5 called Full Account Constructor");
    }

    //Getter and setter methods

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    //Other methods
    //Method to extract a Account from a cursor object
   public static Account extractAccount(Cursor c){
        Log.d("Ent_ExtractAccount","Enter extractAccount method in the Account class.");
        //Declare and initialize a null category object, the one to be returned by the method
        Account account = null;
       //Call common method to extract basic StringValue object data from a cursor
       ArrayList<Object> attributes = Loggin.extractLoggin(c);
        String name="";
        int catID;
        int questionListID;
        int iconID;
        boolean isFavorite;
        name = c.getString(3);
        catID = c.getInt(4);
       questionListID = c.getInt(5);
       iconID = c.getInt(6);
        isFavorite = AccountsDB.toBoolean(c.getInt(7));
        if(iconID > 0){
            Icon iconObject = new Icon();
        }
        if(questionListID > 0){

        }
        //account = new Account(int attributes.get(0); String attributes.get(1), attributes.get(2),catID,questionListID,);

        Log.d("Ext_ExtractAccount","Exit extractAccount method in the Account class.");
        //Return the category object
        return account;
    }//End of extractCategory method
}// End of Account class
