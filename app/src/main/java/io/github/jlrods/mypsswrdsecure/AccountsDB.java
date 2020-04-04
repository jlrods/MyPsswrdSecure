package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

class AccountsDB extends SQLiteOpenHelper {

    private Context context;
    //Default constructor
    public AccountsDB(Context context){
        super(context, "Task Database",null, 1);
        this.context = context;
    }//End of default constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Ent_DBOncreate","Enter onCreate method in AccountsDB class.");
        //Create table to store app state
        db.execSQL("CREATE TABLE APP (_id INTEGER PRIMARY KEY AUTOINCREMENT,currentCategory INTEGER ,\n" +
                "showAllAccounts INTEGER,isFavoriteFilter INTEGER,isSearchFilter INTEGER,lastSearch TEXT);");
        //Populate default state of app
        db.execSQL("INSERT INTO APP VALUES(null,null,1,0,0,'')");

        //Create table to store security answers
        db.execSQL("CREATE TABLE ANSWER (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        //Create table to store security questions
        db.execSQL("CREATE TABLE QUESTION (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");


        //Create intermediate table to associate several questions to one list of questions
        db.execSQL("CREATE TABLE QUESTIONLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT,Question1 INTEGER,\n" +
                "                Question2 INTEGER, Question3 INTEGER,\n" +
                "                FOREIGN KEY (Question1) REFERENCES QUESTION(_id),\n" +
                "                FOREIGN KEY (Question2) REFERENCES QUESTION(_id),\n" +
                "                FOREIGN KEY (Question3) REFERENCES QUESTION(_id));");

        //Create intermediate table to link QuestionListID and QuestionID, this way all the questions in one list
        // can be populated in one table.
        db.execSQL("CREATE TABLE QUESTIONASSIGNMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "QuestionListID INTEGER, QuestionID INTEGER,\n" +
                "FOREIGN KEY (QuestionListID) REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY (QuestionID) REFERENCES QUESTION(_id));");

        //Create table to store usernames for site loggins
        db.execSQL("CREATE TABLE USERNAME (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        //Create table to store passwords for site loggins
        db.execSQL("CREATE TABLE PSSWRD (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        //Create table to store new icon/image locations (selected by user)
        db.execSQL("CREATE TABLE ICON (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT,Location TEXT);");

        //Create table to store MyPsswrdSecure App user data
        db.execSQL("CREATE TABLE APPLOGGIN (_id INTEGER PRIMARY KEY AUTOINCREMENT,UserName INTEGER, " +
                "Psswrd INTEGER, Name TEXT, Email TEXT, Message TEXT, Picture INT);");

        //Create table to store the different categories an account can be associated to
        db.execSQL("CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT, Icon INTEGER);");
        //Populate the Category table with some default category items
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Social Media');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Entertainment');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Communication');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Internet');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Shopping');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Travel');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Learning');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Food');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Finance');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Insurance');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Job Hunting');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Utilities');");


        //Create a table to store the accounts items
        db.execSQL("");
        Log.d("Ext_DBOncreate","Exit onCreate method in AccountsDB class.");
    }//End of onCreate method

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }//End of onUpgrade method

    //Method to create a database object, a cursor, run the sql query and return the result cursor
    public Cursor runQuery(String query){
        Log.d("Ent_runQuery","Enter runQuery method.");
        try{
            //Initialize cursor variable to be returned
            Cursor cursor = null;
            //Get DB object
            SQLiteDatabase db = getReadableDatabase();
            // Extract cursor by running raw query, the one passed in as parameter
            cursor = db.rawQuery(query,null);
            Log.d("Ext_runQuery","Exit runQuery method successfully.");
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Ext_runQuery","Exit runQuery method with exception: "+e.getMessage()+" .");
            return null;
        }//End of try catch block
    }//End of runQuery method

    //Method to extract a Category from a cursor object
    public Icon extractIcon(Cursor c){
        Log.d("Ent_ExtractIcon","Enter extractIcon method in the AccountsDB class.");
        //Initialize local variables
        Icon icon = null;
        int _id;
        String name ="";
        String location ="";
        //Retrieve the values from the cursor and assign them appropriately
        _id = c.getInt(0);
        name = c.getString(1);
        location = c.getString(2);
        //Create a new Icon object by calling full constructor
        icon = new Icon(_id, name, location);
        Log.d("Ext_ExtractIcon","Exit extractIcon method in the AccountsDB class.");
        return icon;
    }// End of extractIcon method

    //Method to extract a Category from a cursor object
    public Category extractCategory(Cursor c){
        Log.d("Ent_ExtractCategory","Enter extractCategory method in the AccountsDB class.");
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

        Log.d("Ext_ExtractCategory","Exit extractCategory method in the AccountsDB class.");
        //Return the category object
        return category;
    }//End of extractCategory method

    //Method to extract a Account from a cursor object
   /* public void extractAccount(Cursor c){
        Log.d("Ent_ExtractAccount","Enter extractAccount method in the AccountsDB class.");
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

        Log.d("Ext_ExtractAccount","Exit extractAccount method in the AccountsDB class.");
        //Return the category object
        return category;
    }//End of extractCategory method*/

    //Method to retrieve the list of categories stored on the database
    public ArrayList<Category> getCategoryList(){
        Log.d("Ent_getCategoryList","Enter getCategoryList method in the AccountsDB class.");
        //Declare and instantiate Array list of Category objects
        ArrayList<Category> list = new ArrayList<Category>();
        //Define a string to hold the sql query
        String query = "SELECT * FROM CATEGORY ";
        //Declare a category object to hold temporarily the Category objects to be created
        Category item;
        //Declare and instantiate a cursor object to hold data retrieved from sql query
        Cursor c = this.runQuery(query);
        //Check the cursor is not null or empty
        if(c !=null && c.getCount()>0){
            //Loop through the cursor and extract each row as a Category object
            while(c.moveToNext()){
                //Call method to extract the category
                item = extractCategory(c);
                //Add category to the Array list
                list.add(item);
            }//End of while loop
        }//End of if statement to check cursor is not null or empty
        Log.d("Ext_getCategoryList","Exit getCategoryList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method


    //Method to internally convert a boolean into a int number 1 or  0
    public int toInt(boolean bool){
        Log.d("Ent_toInt","Enter toInt method in TasksDB class.");
        if(bool){
            Log.d("Ext_toInt","Exit toInt method in TasksDB class (Returned value 1 ).");
            return 1;
        }else{
            Log.d("Ext_toInt","Exit toInt method in TasksDB class (Returned value 0).");
            return 0;
        }//End of if else statement
    }//End of toInt

    //Method to internally convert an int into a boolean true or false. Any value different from 0 will be true
    public boolean toBoolean (int valueToConvert){
        Log.d("Ent_toBool","Enter toBoolean method in the TaskDB class.");
        boolean bool;
        if(valueToConvert==0){
            bool = false;
        }else{
            bool = true;
        }//End of if else statement
        Log.d("Ext_toBool","Exit toBoolean method in the TaskDB class.");
        return bool;
    }//End of toBoolean method

}// End of AccountsDB Class
