package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

//Class to manage all DB interaction
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

        //Create table to store security answers. Leave empty as user has to create their own answers
        db.execSQL("CREATE TABLE ANSWER (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        //Create table to store security questions (linked to Answer ID as Foreign key)"
        db.execSQL("CREATE TABLE QUESTION (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT,\n" +
                "AnswerID INTEGER, FOREIGN KEY (AnswerID) REFERENCES ANSWER(_id));");
        //Insert pre-defined suggested security questions in the DB. No answer associated to question yet.
        db.execSQL("INSERT INTO QUESTION VALUES(null,'petNameQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'firstCarQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'grannyNameQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'placeMarryQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'motherNameQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'placeBirthQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'phoneNumberQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'schoolNameQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'streetNameQuestion',null);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'dadMiddleNameQuestion',null);");

        //Create intermediate table to associate several questions to one list of questions.
        // Leave empty as user has to group their own questions and assign them to an account.
        db.execSQL("CREATE TABLE QUESTIONLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT,QuestionID1 INTEGER,\n" +
                "QuestionID2 INTEGER, QuestionID3 INTEGER,\n" +
                "FOREIGN KEY (QuestionID1) REFERENCES QUESTION(_id),\n" +
                "FOREIGN KEY (QuestionID2) REFERENCES QUESTION(_id),\n" +
                "FOREIGN KEY (QuestionID3) REFERENCES QUESTION(_id));");

        //Create intermediate table to link QuestionListID and QuestionID, this way all the questions in one list
        // can be populated in one table. This solve QuestionID ambiguity issue.
        // Leave empty as user has to group their own questions and link them to an account.
        db.execSQL("CREATE TABLE QUESTIONASSIGNMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "QuestionListID INTEGER, QuestionID INTEGER,\n" +
                "FOREIGN KEY (QuestionListID) REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY (QuestionID) REFERENCES QUESTION(_id));");

        //Create table to store usernames for site loggins
        // Leave empty as user has to create their own user names.
        db.execSQL("CREATE TABLE USERNAME (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        //Create table to store passwords for site loggins
        // Leave empty as user has to create their own passwords.
        db.execSQL("CREATE TABLE PSSWRD (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        //Create table to store new icon/image locations (selected by user)
        // Leave empty as pre populated icons
        db.execSQL("CREATE TABLE ICON (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT,Location TEXT);");

        //Create table to store MyPsswrdSecure App Logging data
        db.execSQL("CREATE TABLE APPLOGGIN (_id INTEGER PRIMARY KEY AUTOINCREMENT,UserNameID TEXT, \n" +
                "PsswrdID TEXT,Name TEXT,Email TEXT,Message TEXT, PictureID INT,\n" +
                "FOREIGN KEY (UserNameID) REFERENCES USERNAME(_id),\n" +
                "FOREIGN KEY (PsswrdID) REFERENCES PSSWRD(_id),\n" +
                "FOREIGN KEY (PictureID) REFERENCES ICON(_id));");

        //Create table to store the different categories an account can be associated to
        db.execSQL("CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, \n" +
                "IconID INT, FOREIGN KEY (IconID) REFERENCES ICON(_id));");
        //Populate the Category table with some default category items
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('SocialMedia');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Entertainment');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Communication');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Work');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Internet');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Shopping');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Travel');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Learning');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Food');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Finance');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Insurance');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('JobHunting');");
        db.execSQL("INSERT INTO CATEGORY (Name) VALUES('Utilities');");

        //Create table to store app state
        db.execSQL("CREATE TABLE APPSTATE(_id INTEGER PRIMARY KEY AUTOINCREMENT,currentCategoryID INTEGER,\n" +
                "showAllAccounts INTEGER,isFavoriteFilter INTEGER,isSearchFilter INTEGER,\n" +
                "lastSearch TEXT, FOREIGN KEY (currentCategoryID) REFERENCES CATEGORY(_id));");
        //Populate default state of app
        db.execSQL("INSERT INTO APP VALUES(null,null,1,0,0,'');");

        //Create a table to store the accounts items
        // Leave empty as user has to create their accounts.
        db.execSQL("CREATE TABLE ACCOUNTS(_id INTEGER PRIMARY KEY AUTOINCREMENT, UserNameID INTEGER, \n" +
                "PsswrdID INTEGER, Name TEXT, CategoryID INTEGER, QuestionListID INTEGER,\n" +
                "IconID INTEGER,  IsFavorite INTEGER, \n" +
                "FOREIGN KEY (UserNameID) REFERENCES USERNAME(_id),\n" +
                "FOREIGN KEY (PsswrdID) REFERENCES PSSWRD(_id),\n" +
                "FOREIGN KEY (CategoryID) REFERENCES CATEGORY(_id),\n" +
                "FOREIGN KEY (QuestionListID) REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY (IconID) REFERENCES ICON(_id));");

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
                item = Category.extractCategory(c);
                //Add category to the Array list
                list.add(item);
            }//End of while loop
        }//End of if statement to check cursor is not null or empty
        Log.d("Ext_getCategoryList","Exit getCategoryList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method


    //Method to internally convert a boolean into a int number 1 or  0
    public static int toInt(boolean bool){
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
    public static boolean toBoolean (int valueToConvert){
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
