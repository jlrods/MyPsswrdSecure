package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

//Class to manage all DB interaction
public class AccountsDB extends SQLiteOpenHelper {

    private Context context;
    //Default constructor
    public AccountsDB(Context context){
        super(context, "Accounts Database",null, 1);
        this.context = context;
    }//End of default constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Ent_DBOncreate","Enter onCreate method in AccountsDB class.");

        //Create table to store security answers. Leave empty as user has to create their own answers
        db.execSQL("CREATE TABLE ANSWER (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT);");

        db.execSQL("INSERT INTO ANSWER VALUES(null,'Sasha');");
        db.execSQL("INSERT INTO ANSWER VALUES(null,'Machito88');");
        db.execSQL("INSERT INTO ANSWER VALUES(null,'Mamama');");
        db.execSQL("INSERT INTO ANSWER VALUES(null,'Caracas');");

        //Create table to store security questions (linked to Answer ID as Foreign key)"
        db.execSQL("CREATE TABLE QUESTION (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT,\n" +
                "AnswerID INTEGER, FOREIGN KEY (AnswerID) REFERENCES ANSWER(_id));");
        //Insert pre-defined suggested security questions in the DB. No answer associated to question yet.
        db.execSQL("INSERT INTO QUESTION VALUES(null,'petNameQuestion',1);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'firstCarQuestion',2);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'grannyNameQuestion',3);");
        db.execSQL("INSERT INTO QUESTION VALUES(null,'placeMarryQuestion',4);");
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
        // can be populated in one table. This solves QuestionID ambiguity issue.
        // Leave empty as user has to group their own questions and link them to an account.
        db.execSQL("CREATE TABLE QUESTIONASSIGNMENT (_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "QuestionListID INTEGER, QuestionID INTEGER,\n" +
                "FOREIGN KEY (QuestionListID) REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY (QuestionID) REFERENCES QUESTION(_id));");

        //Create table to store usernames for site loggins
        // Leave empty as user has to create their own user names.
        db.execSQL("CREATE TABLE USERNAME (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT, \n" +
                "DateCreated BIGINT);");

        //Create table to store passwords for site loggins
        // Leave empty as user has to create their own passwords.
        db.execSQL("CREATE TABLE PSSWRD (_id INTEGER PRIMARY KEY AUTOINCREMENT,Value TEXT,\n" +
                "DateCreated BIGINT);");

        //Create table to store new icon/image locations (selected by user)
        // Leave empty as pre populated icons
        db.execSQL("CREATE TABLE ICON (_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT,Location TEXT, isSelected INTEGER);");

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
                "currentTab INTEGER,showAllAccounts INTEGER,isFavoriteFilter INTEGER,isSearchFilter INTEGER,\n" +
                "lastSearch TEXT, FOREIGN KEY (currentCategoryID) REFERENCES CATEGORY(_id));");
        //Populate default state of app
        db.execSQL("INSERT INTO APPSTATE VALUES(null,null,1,1,0,0,'');");

        //Create a table to store the accounts items
        // Leave empty as user has to create their accounts.
        db.execSQL("CREATE TABLE ACCOUNTS(_id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, \n" +
                "CategoryID INTEGER , UserNameID INTEGER, PsswrdID INTEGER, QuestionListID INTEGER,\n" +
                "IconID INTEGER,  IsFavorite INTEGER, DateCreated BIGINT, DateChange BIGINT,\n" +
                "FOREIGN KEY (CategoryID) REFERENCES CATEGORY(_id),\n" +
                "FOREIGN KEY (UserNameID) REFERENCES USERNAME(_id),\n" +
                "FOREIGN KEY (PsswrdID) REFERENCES PSSWRD(_id),\n" +
                "FOREIGN KEY (QuestionListID) REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY (IconID) REFERENCES ICON(_id));");



        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_aa','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_aerlingus_green','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_aib','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_amazon','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_apple','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_apple_vintange','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_axa','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_booking','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_cpl','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_donedelal','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_dropbox','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_edx','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_eir','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_facebook','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_firefox','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_github','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_glassdoor_green','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_google','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_huawei','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_instagram','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_irish_life','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_irish_life_health','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_linkedin','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_marvin','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_microsoft','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_microsoft_dark','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_netflix','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_netflix_black','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_paypal','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_pinterest','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_ptsb','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_reddit','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_revenue','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_ryanair','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_ryanair_blue','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_samsung','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_small_world','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_sportify_black','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_sportify_fullblack','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_sse_airtricity','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_subway','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_teamviewer','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_three','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_twitter','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_vhi','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_virgin_media','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_vodafone','Resources');");
        db.execSQL("INSERT INTO ICON (Name, Location) VALUES('logo_vue','Resources');");


        //Test and sample data
        //Question Lists
        db.execSQL("INSERT INTO QUESTIONLIST VALUES(null,1,2,3);");
        db.execSQL("INSERT INTO QUESTIONLIST VALUES(null,4,null,null);");
        db.execSQL("INSERT INTO QUESTIONLIST VALUES(null,2,3,4);");

        //Question assignment
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,1,1);");
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,1,2);");
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,1,3);");
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,2,4);");
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,3,2);");
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,3,3);");
        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,3,4);");

        //User names
        db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('jlrods',1588167878639);");
        db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('jlrods@gmail.com',1588267878640);");
        db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('j_rodriguez',1588367878641);");
        db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('j_rodriguez@modularauto.ie',1588467878642);");

        //Passwords
        db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('Machito88',1588167878639);");
        db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('JoseLeonardo',1588267878640);");
        db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('Paracotos12!',1588367878641);");
        db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('Roraima2020!',1588467878642);");
        db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('M;Mach;to88!',1588167878639);");


        //Accounts
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Modular',4,4,4,1,-1,0,1588567878642,0);");
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Google',4,2,3,null,18,0,1588867878642,0);");
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'TMS',4,4,2,2,-1,0,1588767878642,0);");
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Facebook',1,2,1,null,14,0,1588967878642,0);");
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Instagram',1,2,5,null,20,0,1588867878642,0);");
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'AA',11,2,5,null,1,0,1588967878642,0);");
        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Integro',4,4,3,3,-1,0,1588667878642,0);");



        Log.d("Ext_DBOncreate","Exit onCreate method in AccountsDB class.");
    }//End of onCreate method

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }//End of onUpgrade method

    //Method to create a database object, a cursor, run the sql query and return the result cursor
    public Cursor runQuery(String query){
        Log.d("Ent_runQuery","Enter runQuery method in the AccountsDB class.");
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


    //Methods to query the DB

    //Method to get the list of user names from the DB
    public Cursor getIconList(){
        return  this.runQuery("SELECT * FROM ICON");
    }
    //Method to get the list of categories names from the DB
    public Cursor getCategoryListCursor(){
        return  this.runQuery("SELECT * FROM CATEGORY");
    }
    //Method to get the list of user names from the DB
    public Cursor getUserNameList(){
        return  this.runQuery("SELECT * FROM USERNAME");
    }
    //Method to get the list of accounts from the DB
    public Cursor getAccountsList(){
        return  this.runQuery("SELECT * FROM ACCOUNTS");
    }
    //Method to get the list of passwords from the DB
    public Cursor getPsswrdList(){
        return  this.runQuery("SELECT * FROM PSSWRD");
    }
    //Method to get the number of times a specific user name is being used in different accounts as per the DB
    public int getTimesUsedUserName(int userNameID){
        return this.runQuery("SELECT * FROM ACCOUNTS WHERE UserNameID = "+userNameID).getCount();
    }
    //Method to get the number of times a specific password is being used in different accounts as per the DB
    public int getTimesUsedPsswrd(int psswrd){
        return this.runQuery("SELECT * FROM ACCOUNTS WHERE PsswrdID = "+psswrd).getCount();
    }

    //Method to retrieve a specific Icon from DB by passing in it's ID
    public Icon getIconByID(int _id){
        Log.d("getIconByID","Enter the getIconByID method in the AccountsDB class.");
    Cursor cursor = this.runQuery("SELECT * FROM ICON WHERE _id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
        cursor.moveToFirst();
        Log.d("getIconByID","Exit successfully (icon with id " +_id+ " has been found) the getIconByID method in the AccountsDB class.");
        return Icon.extractIcon(cursor);
    }else{
        Log.d("getIconByID","Exit the getIconByID method in the AccountsDB class without finding the account with id: "+_id);
        return null;
    }//End of if else statement
}//End of getIconByID method

    //Method to retrieve a specific Icon from DB by passing in it's ID
    public Icon getIconByName(String name){
        Log.d("getIconByID","Enter the getIconByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM ICON WHERE Name = '"+ name+"'");
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getIconByID","Exit successfully (icon with id " +name+ " has been found) the getIconByID method in the AccountsDB class.");
            return Icon.extractIcon(cursor);
        }else{
            Log.d("getIconByID","Exit the getIconByID method in the AccountsDB class without finding the account with name: "+name);
            return null;
        }//End of if else statement
    }//End of getIconByID method


    //Method to get a specific user name, by passing in its DB _id as an argument
    public UserName getUserNameByID(int _id){
        Log.d("getUserNameByID","Enter the getUserNameByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM USERNAME WHERE _id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getUserNameByID","Exit successfully (user name with id " +_id+ " has been found) the getUserNameByID method in the AccountsDB class.");
            return UserName.extractUserName(cursor);
        }else{
            Log.d("getUserNameByID","Exit the getUserNameByID method in the AccountsDB class without finding the account with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getUserNameByID method

    //Method to get a specific password, by passing in its DB _id as an argument
    public Psswrd getPsswrdByID(int _id){
        Log.d("getPsswrdByID","Enter the getPsswrdByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM PSSWRD WHERE _id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getPsswrdByID","Exit successfully (password with id " +_id+ " has been found) the getPsswrdByID method in the AccountsDB class.");
            return Psswrd.extractPsswrd(cursor);
        }else{
            Log.d("getPsswrdByID","Exit the getPsswrdByID method in the AccountsDB class without finding the account with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getPsswrdByID method

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

    //Method to get a specific answer, by passing in its DB _id as an argument
    public Answer getAnswerByID(int _id){
        Log.d("getAnswerByID","Enter the getAnswerByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM ANSWER WHERE _id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getAnswerByID","Exit successfully (ANSWER with id " +_id+ " has been found) the getAnswerByID method in the AccountsDB class.");
            return Answer.extractAnswer(cursor);
        }else{
            Log.d("getAnswerByID","Exit the getAnswerByID method in the AccountsDB class without finding the account with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getAnswerByID method

    //Method to get the list of questions list recorded on the DB
    public ArrayList<QuestionList> getListOfQuestionLists(){
        Log.d("getListOfQuestionLists","Enter the getListOfQuestionLists method in the AccountsDB class.");
        //Declare and initialize a list to keep the QuestionList objects
        ArrayList<QuestionList> listOfQuestionLists = new ArrayList<QuestionList>();
        //Declare and initialize a cursor to hold data from DB
        Cursor listOfQuestionsLists = this.runQuery("SELECT * FROM QUESTIONLIST");
        //Check list of questions available isn't null or empty
        if(listOfQuestionsLists!= null && listOfQuestionsLists.getCount()>0){
            //Go through the list
            while(listOfQuestionsLists.moveToNext()){
                QuestionList questionList = this.getQuestionListById(listOfQuestionsLists.getInt(0));
                //If the security question list cursor isn't null or empty then add to array list
                if(questionList!=null && questionList.getSize()>0){
                    listOfQuestionLists.add(questionList);
                }//End of if statement to check list isn't null or empty
            }//End of while loop to go through the list
        }//End of if statement to check list of questions available isn't null or empty
        Log.d("getListOfQuestionLists","Exit the getListOfQuestionLists method in the AccountsDB class.");
        return listOfQuestionLists;
    }//End of getListOfQuestionLists method

    //Method to get cursor with list of questions available
    public Cursor getListQuestionsAvailable(){
        Log.d("getListOfQuestionLists","Enter the getListOfQuestionLists method in the AccountsDB class.");
        //Declare and initialize cursor to hold list of questions available from DB
        Cursor questionsAvailable = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer FROM\n" +
                "QUESTION  JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id;");
        Log.d("getListOfQuestionLists","Exit the getListOfQuestionLists method in the AccountsDB class.");
        return questionsAvailable;
    }//End of getListQuestionsAvailable method

    //Method to get a specific question, by passing in its DB _id as an argument
    public Question getQuestionByID(int _id){
        Log.d("getQuestionByID","Enter the getQuestionByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM QUESTION  JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getQuestionByID","Exit successfully (QUESTION with id " +_id+ " has been found) the getQuestionByID method in the AccountsDB class.");
            return Question.extractQuestion(cursor);
        }else{
            Log.d("getQuestionByID","Exit the getQuestionByID method in the AccountsDB class without finding the account with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getQuestionByID method

    //Method to get a question cursor by passing in its DB id as argument
    public Cursor getQuestionCursorByID(int _id){
        Log.d("getQuestionListById","Enter the getQuestionListById method in the AccountsDB class.");
        Cursor question = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer FROM " +
                "QUESTION  JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id);
        Log.d("getQuestionListById","Exit the getQuestionListById method in the AccountsDB class.");
        return question;
    }//End of getQuestionCursorByID method

    //Method to get a a two question cursor by passing in their DB ids as arguments
    public Cursor getQuestionCursorByID(int _id1, int _id2){
        Log.d("getQuestionListById","Enter the getQuestionListById method in the AccountsDB class.");
        Cursor question = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer FROM " +
                "QUESTION  JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id1 + " OR QUESTION._id = "+ _id2);
        Log.d("getQuestionListById","Exit the getQuestionListById method in the AccountsDB class.");
        return question;
    }//End of getQuestionCursorByID method

    //Method to get a a three question cursor by passing in their DB ids as arguments
    public Cursor getQuestionCursorByID(int _id1, int _id2, int _id3){
        Log.d("getQuestionListById","Enter the getQuestionListById method in the AccountsDB class.");
        Cursor question = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer FROM " +
                "QUESTION  JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id1+ " OR QUESTION._id = "+ _id2 +" OR QUESTION._id = "+ _id3);
        Log.d("getQuestionListById","Exit the getQuestionListById method in the AccountsDB class.");
        return question;
    }//End of getQuestionCursorByID method


    //Method to get a list of questions, by passing in its DB _id as an argument
    public QuestionList getQuestionListById(int _id){
        Log.d("getQuestionListById","Enter the getQuestionListById method in the AccountsDB class.");
        //Declare and initialize null questionlist object to be returned
        QuestionList questionList = null;
        //Declare and initialize cursor to hold question list data from DB
        Cursor cursor = this.runQuery("SELECT QUESTION._id AS QUESTIONID, \n" +
                "QUESTION.Value AS Q, QUESTION.AnswerID,ANSWER._id AS ANSWERID,ANSWER.Value AS A FROM\n" +
                "((QUESTION INNER JOIN QUESTIONASSIGNMENT ON QUESTION._id = QUESTIONASSIGNMENT.QuestionID)\n" +
                "INNER JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id)\n" +
                "INNER JOIN QUESTIONLIST ON QUESTIONLIST._id = QUESTIONASSIGNMENT.QuestionListID\n" +
                "WHERE QUESTIONLIST._id = "+ _id);
        //Check if cursor returned empty or null
        if(cursor != null && cursor.getCount() >0){
            //If not empty, create object from cursor and return
            questionList = new QuestionList(_id,cursor);
            Log.d("getQuestionListById","Exit successfully (QUESTION with id " +_id+ " has been found) the getQuestionListById method in the AccountsDB class.");
            return questionList;
        }else {
            Log.d("getQuestionListById", "Exit the getQuestionListById method in the AccountsDB class without finding the account with id: " + _id);
            //Return empty question list object
            return questionList;
        }//End of if else statement to check cursor is not empty
    }//End of getQuestionListById method


    //Method used to break a string down into multiple pieces when to allow the apostrophe to be part of the string
    public String includeApostropheEscapeChar(String text){
        Log.d("ApostEscCharString","Enter includeApostropheEscapeChar method in AccountsDB class.");
        String textWithEscChar = "";
        char apostrophe = '\'';
        //Iterate through the string to find the apostrophe and replace it with double apostrophe
        for(int i=0;i<text.length();i++){
            char c  = text.charAt(i);
            if(c == apostrophe){
                //If it is an apostrophe, include an extra one
                textWithEscChar += "''";
            }else{
                //Otherwise, copy th e current character to new string
                textWithEscChar+= text.charAt(i);
            }//End of if else statement to check if current char is equal to the apostrophe char
        }//End of for loop to iterate through the string
        Log.d("ApostEscCharString","Exit includeApostropheEscapeChar method in AccountsDB class.");
        return textWithEscChar;
    }//End of includeApostropheEscapeChar method


    //Method to update AppState in DB
    public boolean updateAppState(int currentCategory,int currentTab,int showAllAccounts,int isFavoriteFilter,int isSearchFilter, String lastSearchText){
        Log.d("UpdateState","Enter the updateAppState method in the AccountsDB class.");
        boolean success = false;
        Cursor appState;
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        appState = this.runQuery("SELECT * FROM APPSTATE");
        //Declare string for the fist part of sql query
        String updateState ="UPDATE APPSTATE SET ";
        //Prepare lastSearchTask and lastSearchGrocery text before sql is run --> include escape character for apostrophe
        if(lastSearchText.contains("'")){
            lastSearchText = this.includeApostropheEscapeChar(lastSearchText);
        }//End of if statement
        //Form all the query fields section
        String fields = " currentCategoryID = " + currentCategory + ","+
                " currentTab = " + currentTab+ ","+
                " showAllAccounts = "+ showAllAccounts + ","+
                " isFavoriteFilter = "+ isFavoriteFilter + ","+
                " isSearchFilter = " + isSearchFilter + ","+
                " lastSearch = '" + lastSearchText+ "'";
        //String to hold the where part of the query
        String whereId = " WHERE _id = ";
        //String to hold the complete sql query
        String sql = "";
        //get next app state (only one should be saved)
        if(appState.moveToNext()){
            sql = updateState+fields+ whereId+appState.getInt(0);
        }
        //Try Catch block to execute the sql command to update corresponding table
        try{
            //Run the query and change success to true if no issues
            db.execSQL(sql);
            success = true;
            Log.d("UpdateState","Exit successfully the updateAppState method in the Accounts class.");
        }catch (Exception e) {
            //Log the exception message
            Log.d("UpdateState","Exit the updateAppState method in the Accounts class with exception: "+e.getMessage());
        }
        finally{
            return success;
        }//End of try and catch block
    }//End of updateAppState

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
