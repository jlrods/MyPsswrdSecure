package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

//Class to manage all DB interaction
public class AccountsDB extends SQLiteOpenHelper {
    private static final char apostropheChar = '\'';
    private static final String apostrophe ="'";
    private Context context;
    private Cryptographer cryptographer;
    //Declare and initialize constant to define the number of preloaded questions to retrieve from the DB
    private static final int NUMBER_OF_PRELOADED_QUESTIONS = 10;
    //Declare and initialize constant to define the number of preloaded categories to retrieve from the DB
    private static final int NUMBER_OF_PRELOADED_CATEGORIES = 13;
    //Default constructor
    public AccountsDB(Context context){
        super(context, "Accounts Database",null, 1);
        this.context = context;
        this.cryptographer = LoginActivity.getCryptographer();
    }//End of default constructor


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Ent_DBOncreate","Enter onCreate method in AccountsDB class.");

        //Create table to store security answers. Leave empty as user has to create their own answers
        db.execSQL("CREATE TABLE "+MainActivity.getAnswerTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+MainActivity.getValueColumn()+" BLOB, "+MainActivity.getInitVectorColumn()+" BLOB);");

        //Include test data for Answers
//        byte[] answer1 = cryptographer.encryptText("Sasha");
//        ContentValues answerValues1 = new ContentValues();
        //values.put("_id",null);
//        answerValues1.put("Value",answer1);
//        answerValues1.put("initVector",cryptographer.getIv().getIV());
//        int answerID1 = (int) db.insert("ANSWER",null,answerValues1);

        //db.execSQL("INSERT INTO ANSWER VALUES(null,'Sasha');");

//        byte[] answer2 = cryptographer.encryptText("Machito88");
//        ContentValues answerValues2 = new ContentValues();
//        //values.put("_id",null);
//        answerValues2.put("Value",answer2);
//        answerValues2.put("initVector",cryptographer.getIv().getIV());
//        int answerID2 = (int) db.insert("ANSWER",null,answerValues2);
//        //db.execSQL("INSERT INTO ANSWER VALUES(null,'Machito88');");
//
//        byte[] answer3 = cryptographer.encryptText("Mamama");
//        ContentValues answerValues3 = new ContentValues();
//        //values.put("_id",null);
//        answerValues3.put("Value",answer3);
//        answerValues3.put("initVector",cryptographer.getIv().getIV());
//        int answerID3 = (int) db.insert("ANSWER",null,answerValues3);
//
//        //db.execSQL("INSERT INTO ANSWER VALUES(null,'Mamama');");
//
//        byte[] answer4 = cryptographer.encryptText("Caracas");
//        ContentValues answerValues4 = new ContentValues();
//        //values.put("_id",null);
//        answerValues4.put("Value",answer4);
//        answerValues4.put("initVector",cryptographer.getIv().getIV());
//        int answerID4 = (int) db.insert("ANSWER",null,answerValues4);
//        //db.execSQL("INSERT INTO ANSWER VALUES(null,'Caracas');");

        //Create table to store security questions (linked to Answer ID as Foreign key)"
        db.execSQL("CREATE TABLE "+MainActivity.getQuestionTable() +" (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+MainActivity.getValueColumn()+" TEXT,\n" +
                MainActivity.getAnswerIdColumn()+" INTEGER, FOREIGN KEY ("+MainActivity.getAnswerIdColumn()+") REFERENCES ANSWER(_id));");
        //Insert pre-defined suggested security questions in the DB. No answer associated to question yet.
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatsTheNameOfYourFirstPet',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatWasTheModelOfYourFirstCar',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatsYourGrannysName',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whereDidYouGetMarried',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatsYourMothersMaidenName',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatsTheNameOfTheTownWhereYouWereBorn',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whichPhoneNumberDoYouRememberMostFromChildhood',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatStreetDidYouGrowUpOn',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatIsTheNameOfYourFirstSchool',null);");
        db.execSQL("INSERT INTO "+MainActivity.getQuestionTable() +" VALUES(null,'whatsYourFathersMiddleName',null);");


        //Create intermediate table to associate several questions to one list of questions.
        // Leave empty as user has to group their own questions and assign them to an account.
        db.execSQL("CREATE TABLE "+MainActivity.getQuestionlistTable() +" (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+MainActivity.getQuestionId1Column()+" INTEGER,\n" +
                MainActivity.getQuestionId2Column()+" INTEGER, "+MainActivity.getQuestionId3Column()+" INTEGER,\n" +
                "FOREIGN KEY ("+MainActivity.getQuestionId1Column()+") REFERENCES QUESTION(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getQuestionId2Column()+") REFERENCES QUESTION(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getQuestionId3Column()+") REFERENCES QUESTION(_id));");

        //Create intermediate table to link QuestionListID and QuestionID, this way all the questions in one list
        // can be populated in one table. This solves QuestionID ambiguity issue.
        // Leave empty as user has to group their own questions and link them to an account.
        db.execSQL("CREATE TABLE  "+MainActivity.getQuestionassignmentTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                ""+MainActivity.getQuestionListIdColumn()+" INTEGER, "+MainActivity.getQuestionIdColumn()+" INTEGER,\n" +
                "FOREIGN KEY ("+MainActivity.getQuestionListIdColumn()+") REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getQuestionIdColumn()+") REFERENCES QUESTION(_id));");

        //Create table to store usernames for site loggins
        // Leave empty as user has to create their own user names.
        db.execSQL("CREATE TABLE "+MainActivity.getUsernameTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+MainActivity.getValueColumn()+" BLOB, \n" +
                MainActivity.getInitVectorColumn()+" BLOB, "+MainActivity.getDateCreatedColumn()+" BIGINT);");

        //Create table to store passwords for site loggins
        // Leave empty as user has to create their own passwords.
        db.execSQL("CREATE TABLE "+MainActivity.getPsswrdTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+MainActivity.getValueColumn()+" BLOB,\n" +
                MainActivity.getInitVectorColumn()+" BLOB, "+MainActivity.getDateCreatedColumn()+" BIGINT);");

        //Create table to store new icon/image locations (selected by user)
        // Leave empty as pre populated icons
        db.execSQL("CREATE TABLE "+MainActivity.getIconTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+MainActivity.getNameColumn()+" TEXT,"+MainActivity.getIconLocationColumn()+" TEXT, "+MainActivity.getIconIsSelectedColumn()+" INTEGER);");

        //Create table to store MyPsswrdSecure App Logging data
        db.execSQL("CREATE TABLE "+MainActivity.getApplogginTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+MainActivity.getUserNameIdColumn()+" BLOB,"+MainActivity.getUserNameIvColumn()+" BLOB, \n" +
                MainActivity.getPsswrdIdColumn()+" BLOB,"+MainActivity.getPsswrdIvColumn()+" BLOB,"+MainActivity.getNameColumn()+" TEXT,"+MainActivity.getMessageColumn()+" TEXT, "+MainActivity.getPictureidColumn()+" INT,\n" +
                "FOREIGN KEY ("+MainActivity.getPictureidColumn()+") REFERENCES ICON(_id));");

        //Create table to store the different categories an account can be associated to
        db.execSQL("CREATE TABLE "+MainActivity.getCategoryTable()+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+MainActivity.getNameColumn()+" TEXT, \n" +
                MainActivity.getIconIdColumn()+" INT, FOREIGN KEY ("+MainActivity.getIconIdColumn()+") REFERENCES ICON(_id));");


        //Create table to store app state
        db.execSQL("CREATE TABLE "+MainActivity.getAppstateTable()+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+MainActivity.getCurrentCategoryIdColumn()+" INTEGER,\n" +
                MainActivity.getCurrentTabColumn()+" INTEGER,"+MainActivity.getIsSearchFilterColumn()+" INTEGER,\n" +
                MainActivity.getIsSearchUserFilterColumn()+" INTEGER, "+MainActivity.getIsSearchPsswrdFilterColumn()+" INTEGER,\n"+
                MainActivity.getLastSearchTextColumn()+" TEXT,"+MainActivity.getIsSortFilterColumn()+" INTEGER,"+MainActivity.getCurrentSortFilterColumn()+" INTEGER, FOREIGN KEY ("+MainActivity.getCurrentCategoryIdColumn()+") REFERENCES CATEGORY(_id));");
        //Populate default state of app
        db.execSQL("INSERT INTO "+MainActivity.getAppstateTable()+" VALUES(null,-1,1,0,0,0,'',0,-1);");

        //Create a table to store the accounts items
        // Leave empty as user has to create their accounts.
        db.execSQL("CREATE TABLE "+MainActivity.getAccountsTable()+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+MainActivity.getNameColumn()+" TEXT, \n" +
                MainActivity.getCategoryIdColumn()+" INTEGER , "+MainActivity.getUserNameIdColumn()+" INTEGER, "+MainActivity.getPsswrdIdColumn()+" INTEGER, "+MainActivity.getQuestionIdColumn()+" INTEGER,\n" +
                MainActivity.getIconIdColumn()+" INTEGER,  "+MainActivity.getIsFavoriteColumn()+" INTEGER, "+MainActivity.getDateCreatedColumn()+" BIGINT, "+MainActivity.getDateChangeColumn()+" BIGINT,\n" +
                "FOREIGN KEY ("+MainActivity.getCategoryIdColumn()+") REFERENCES CATEGORY(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getUserNameIdColumn()+") REFERENCES USERNAME(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getPsswrdIdColumn()+") REFERENCES PSSWRD(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getQuestionIdColumn()+") REFERENCES QUESTIONLIST(_id),\n" +
                "FOREIGN KEY ("+MainActivity.getIconIdColumn()+") REFERENCES ICON(_id));");


        //App resources
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_aa','Resources');");//1
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_aerlingus_green','Resources');");//2
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_aib','Resources');");//3
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_allianz','Resources');");//4
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_amazon','Resources');");//5
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_amazon_alexa','Resources');");//6
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_amazon_prime_video','Resources');");//7
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_android','Resources');");//8
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_anpost','Resources');");//9
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_apple','Resources');");//10
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_apple_vintange','Resources');");//11
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_argos','Resources');");//12
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_axa','Resources');");//13
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_booking','Resources');");//14
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_chess_com','Resources');");//15
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_conqueror','Resources');");//16
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_cpl','Resources');");//17
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_daft_ie','Resources');");//18
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_disney','Resources');");//19
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_domino','Resources');");//20
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_donedelal','Resources');");//21
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_dropbox','Resources');");//22
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_ebay','Resources');");//23
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_edx','Resources');");//24
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_eir','Resources');");//25
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_ezliving_furniture','Resources');");//26
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_facebook','Resources');");//27
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_finding_pinguins','Resources');");//28
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_firefox','Resources');");//29
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_garmin','Resources');");//30
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_garmin_connect','Resources');");//31
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_github','Resources');");//32
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_glassdoor_green','Resources');");//33
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_google','Resources');");//34
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_huawei','Resources');");//35
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_ikea','Resources');");//36
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_insomnia','Resources');");//37
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_instagram','Resources');");//38
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_instructables','Resources');");//39
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_irish_jobs','Resources');");//40
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_irish_life','Resources');");//41
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_irish_life_health','Resources');");//42
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_jellyfin','Resources');");//43
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_jobs_ie','Resources');");//44
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_kfc','Resources');");//45
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_lana','Resources');");//46
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_lidl','Resources');");//47
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_linkedin','Resources');");//48
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_lotto','Resources');");//49
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_marvin','Resources');");//50
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_medium','Resources');");//51
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_microsoft','Resources');");//52
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_microsoft_dark','Resources');");//53
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_netflix','Resources');");//54
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_netflix_black','Resources');");//55
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_nike','Resources');");//56
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_paypal','Resources');");//57
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_pinterest','Resources');");//58
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_ptsb','Resources');");//59
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_reddit','Resources');");//60
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_revenue','Resources');");//61
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_ryanair','Resources');");//62
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_ryanair_blue','Resources');");//63
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_samsung','Resources');");//64
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_shazam','Resources');");//65
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_small_world','Resources');");//66
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_sonos','Resources');");//67
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_sportify_black','Resources');");//68
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_sportify_fullblack','Resources');");//69
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_sse_airtricity','Resources');");//70
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_star_wars','Resources');");//71
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_starbucks','Resources');");//72
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_subway','Resources');");//73
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_supermacs','Resources');");//74
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_supervalue','Resources');");//75
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_tcl','Resources');");//76
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_teamviewer','Resources');");//77
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_tesco','Resources');");//78
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_tesco_mobile','Resources');");//79
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_the_range','Resources');");//80
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_three','Resources');");//81
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_twitter','Resources');");//82
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_vhi','Resources');");//83
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_virgin_media','Resources');");//84
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_vodafone','Resources');");//85
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_vue','Resources');");//86
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_youtube','Resources');");//87
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('logo_youtube_music','Resources');");//88

        //Android resources
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('facebook','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('popcorn','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('ic_cat_cellphone_wireless','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('briefcase_clock','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('web','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('shopping','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('airplane_takeoff','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('android_studio','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('food','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('currency_eur','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('shield_check','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('briefcase_search','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('tools','Resources');");
        //NavDrawer Resources
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg1','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg2','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg3','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg4','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg5','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg6','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg7','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg8','Resources');");
        db.execSQL("INSERT INTO "+MainActivity.getIconTable()+" ("+MainActivity.getNameColumn()+", "+MainActivity.getIconLocationColumn()+") VALUES('nav_menu_header_bg9','Resources');");




        //Populate the Category table with some default category items
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('SocialMedia',49);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Entertainment',50);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Communication',51);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Work',52);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Internet',53);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Shopping',54);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Travel',55);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Learning',56);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Food',57);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Finance',58);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Insurance',59);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('JobHunting',60);");
        db.execSQL("INSERT INTO "+MainActivity.getCategoryTable()+" ("+MainActivity.getNameColumn()+","+MainActivity.getIconIdColumn()+") VALUES('Utilities',61);");


        //Test and sample data
        //Question Lists
//        db.execSQL("INSERT INTO QUESTIONLIST VALUES(null,1,2,3);");
//        db.execSQL("INSERT INTO QUESTIONLIST VALUES(null,4,null,null);");
//        db.execSQL("INSERT INTO QUESTIONLIST VALUES(null,2,3,4);");

        //Question assignment
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,1,1);");
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,1,2);");
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,1,3);");
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,2,4);");
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,3,2);");
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,3,3);");
//        db.execSQL("INSERT INTO QUESTIONASSIGNMENT VALUES(null,3,4);");

        //Include test data for User names
//        byte[] user1 = cryptographer.encryptText("jlrods");
//        ContentValues values1 = new ContentValues();
//        values1.put("Value",user1);
//        values1.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values1.put("initVector",cryptographer.getIv().getIV());
//        int userID1 = (int) db.insert("USERNAME",null,values1);
//
//        byte[] user2 = cryptographer.encryptText("jlrods@gmail.com");
//        ContentValues values2 = new ContentValues();
//        //values.put("_id",null);
//        values2.put("Value",user2);
//        values2.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values2.put("initVector",cryptographer.getIv().getIV());
//        int userID2 = (int) db.insert("USERNAME",null,values2);
//        //String user2 = cryptographer.encryptText("jlrods@gmail.com").toString();
//        //db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('"+user2+"',1588267878640);");
//        byte[] user3 = cryptographer.encryptText("j_rodriguez");
//        ContentValues values3 = new ContentValues();
//        //values.put("_id",null);
//        values3.put("Value",user3);
//        values3.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values3.put("initVector",cryptographer.getIv().getIV());
//        int userID3 = (int) db.insert("USERNAME",null,values3);
//        //String user3 = cryptographer.encryptText("j_rodriguez").toString();
//        //db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('"+user3+"',1588367878641);");
//        byte[]  user4 = cryptographer.encryptText("j_rodriguez@modularauto.ie");
//        ContentValues values4 = new ContentValues();
//        //values.put("_id",null);
//        values4.put("Value",user4);
//        values4.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values4.put("initVector",cryptographer.getIv().getIV());
//        int userID4 = (int) db.insert("USERNAME",null,values4);
//        //String user4 = cryptographer.encryptText("j_rodriguez@modularauto.ie").toString();
//        //db.execSQL("INSERT INTO USERNAME (Value, DateCreated) VALUES('"+user4+"',1588467878642);");
//
//        ////Include test data for passwords
//        byte[] psswrd1 = cryptographer.encryptText("Machito88");
//        ContentValues values11 = new ContentValues();
//        //values.put("_id",null);
//        values11.put("Value",psswrd1);
//        values11.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values11.put("initVector",cryptographer.getIv().getIV());
//        int psswrdID1 = (int) db.insert("PSSWRD",null,values11);
//        //String psswrd1 = cryptographer.encryptText("Machito88").toString();
//        //db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('"+psswrd1+"',1588167878639);");
//        byte[] psswrd2 = cryptographer.encryptText("JoseLeonardo");
//        ContentValues values12 = new ContentValues();
//        //values.put("_id",null);
//        values12.put("Value",psswrd2);
//        values12.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values12.put("initVector",cryptographer.getIv().getIV());
//        int psswrdID2 = (int) db.insert("PSSWRD",null,values12);
//        //String psswrd2 = cryptographer.encryptText("JoseLeonardo").toString();
//        //db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('"+psswrd2+"',1588267878640);");
//        byte[]  psswrd3 = cryptographer.encryptText("Paracotos12!");
//        ContentValues values13 = new ContentValues();
//        //values.put("_id",null);
//        values13.put("Value",psswrd3);
//        values13.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values13.put("initVector",cryptographer.getIv().getIV());
//        int psswrdID3 = (int) db.insert("PSSWRD",null,values13);
//        //String psswrd3 = cryptographer.encryptText("Paracotos12!").toString();
//        //db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('"+psswrd3+"',1588367878641);");
//        byte[] psswrd4 = cryptographer.encryptText("Roraima2020!");
//        ContentValues values14 = new ContentValues();
//        //values.put("_id",null);
//        values14.put("Value",psswrd4);
//        values14.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values14.put("initVector",cryptographer.getIv().getIV());
//        int psswrdID4 = (int) db.insert("PSSWRD",null,values14);
//        //String psswrd4 = cryptographer.encryptText("Roraima2020!").toString();
//        //db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('"+psswrd4+"',1588467878642);");
//        byte[] psswrd5 = cryptographer.encryptText("M;Mach;to88!");
//        ContentValues values15 = new ContentValues();
//        //values.put("_id",null);
//        values15.put("Value",psswrd5);
//        values15.put("DateCreated", Calendar.getInstance().getTimeInMillis());
//        values15.put("initVector",cryptographer.getIv().getIV());
//        int psswrdID5 = (int) db.insert("PSSWRD",null,values15);
//        String psswrd5 = cryptographer.encryptText("M;Mach;to88!").toString();
//        db.execSQL("INSERT INTO PSSWRD (Value, DateCreated) VALUES('"+psswrd5+"',1588167878639);");

//        Cursor psswrdRow5 = db.rawQuery("SELECT * FROM PSSWRD WHERE _id = "+psswrdID5,null);
//        psswrdRow5.moveToFirst();
//        int idPsswrd5 = psswrdRow5.getInt(0);
//        long datePsswrd5 = psswrdRow5.getLong(3);
//        byte[] encryptedPsswrd5 = psswrdRow5.getBlob(1);
//        String psswrd15 = cryptographer.decryptText(encryptedPsswrd5,cryptographer.getIv());

//        Cursor psswrdRow1 = db.rawQuery("SELECT * FROM PSSWRD WHERE _id = "+psswrdID1,null);
//        psswrdRow1.moveToFirst();
//        int idPsswrd1 = psswrdRow1.getInt(0);
//        long datePsswrd1 = psswrdRow1.getLong(2);
//        byte[] encryptedPsswrd1 = psswrdRow1.getBlob(1);
//        byte[] ivPsswrd1 = psswrdRow1.getBlob(3);
//        String psswrd11 = cryptographer.decryptText(encryptedPsswrd1,new IvParameterSpec(ivPsswrd1));
//
//        Cursor userRow3 = db.rawQuery("SELECT * FROM USERNAME WHERE _id = "+userID3,null);
//        userRow3.moveToFirst();
//        int idUser3 = userRow3.getInt(0);
//        long dateUser3 = userRow3.getLong(2);
//        byte[] encryptedUser3 = userRow3.getBlob(1);
//        byte[] ivUser3 = userRow3.getBlob(3);
//        String user13 = cryptographer.decryptText(encryptedUser3,new IvParameterSpec(ivUser3));


        //Accounts
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Modular',4,4,4,1,-1,0,1588567878642,0);");
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Google',4,2,3,null,18,0,1588867878642,0);");
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'TMS',4,4,2,2,-1,0,1588767878642,0);");
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Facebook',1,2,1,null,14,0,1588967878642,0);");
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Instagram',1,2,5,null,20,0,1588867878642,0);");
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'AA',11,2,5,null,1,0,1588967878642,0);");
//        db.execSQL("INSERT INTO ACCOUNTS VALUES(null,'Integro',4,4,3,3,-1,0,1588667878642,0);");

        Log.d("Ext_DBOncreate","Exit onCreate method in AccountsDB class.");
    }//End of onCreate method

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }//End of onUpgrade method

    //Method to create a database object, a cursor, run the sql query and return the result cursor
    public Cursor runQuery(String query){
        Log.d("Ent_runQuery","Enter runQuery method in the AccountsDB class.");
        //Get DB object
        SQLiteDatabase db = getReadableDatabase();
        //Initialize cursor variable to be returned
        Cursor cursor = null;
        try{
            // Extract cursor by running raw query, the one passed in as parameter
            cursor = db.rawQuery(query,null);
            Log.d("Ext_runQuery","Exit runQuery method successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Ext_runQuery","Exit runQuery method with exception: "+e.getMessage()+" .");
        } finally {
            //db.close();
            return cursor;
        }//End of try catch block
    }//End of runQuery method



    //Methods to update the DB
    //Method to update AppState in DB
//    public boolean updateAppStateOld(int currentCategory, int currentTab, int showAllAccounts, int isFavoriteFilter, int isSearchFilter, String lastSearchText){
//        Log.d("UpdateState","Enter the updateAppState method in the AccountsDB class.");
//        boolean success = false;
//        Cursor appState;
//        //Declare and instantiate a new database object to handle the database operations
//        SQLiteDatabase db = getWritableDatabase();
//        appState = this.runQuery("SELECT * FROM APPSTATE");
//        //Declare string for the fist part of sql query
//        String updateState ="UPDATE APPSTATE SET ";
//        //Prepare lastSearchTask and lastSearchGrocery text before sql is run --> include escape character for apostrophe
//        if(lastSearchText.contains(apostrophe)){
//            lastSearchText = this.includeApostropheEscapeChar(lastSearchText);
//        }//End of if statement
//        //Form all the query fields section
//        String fields = " currentCategoryID = " + currentCategory + ","+
//                " currentTab = " + currentTab+ ","+
//                " showAllAccounts = "+ showAllAccounts + ","+
//                " isFavoriteFilter = "+ isFavoriteFilter + ","+
//                " isSearchFilter = " + isSearchFilter + ","+
//                " lastSearch = '" + lastSearchText+ "'";
//        //String to hold the where part of the query
//        String whereId = " WHERE _id = ";
//        String whereClause = "_id = ";
//        //String to hold the complete sql query
//        String sql = "";
//        //get next app state (only one should be saved)
//        if(appState.moveToNext()){
//            sql = updateState+fields+ whereId+appState.getInt(0);
//        }
//        //Try Catch block to execute the sql command to update corresponding table
//        try{
//            //Run the query and change success to true if no issues
//            db.execSQL(sql);
//            success = true;
//            Log.d("UpdateState","Exit successfully the updateAppState method in the Accounts class.");
//        }catch (Exception e) {
//            //Log the exception message
//            Log.d("UpdateState","Exit the updateAppState method in the Accounts class with exception: "+e.getMessage());
//        }
//        finally{
//            db.close();
//            return success;
//        }//End of try and catch block
//    }//End of updateAppState

    //Method to update AppState in DB
//    public boolean updateAppState(int currentCategory, int currentTab, int showAllAccounts, int isFavoriteFilter, int isSearchFilter, String lastSearchText){
//        Log.d("UpdateState","Enter the updateAppState method in the AccountsDB class.");
//        boolean updated = false;
//        int _id = -1;
//        String whereClause = "_id = ";
//        boolean success = false;
//        //Declare and instantiate a new database object to handle the database operations
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("currentCategoryID",currentCategory);
//        values.put("currentTab",currentTab);
//        values.put("showAllAccounts",showAllAccounts);
//        values.put("isFavoriteFilter",isFavoriteFilter);
//        values.put("isSearchFilter",isSearchFilter);
//        values.put("lastSearch",lastSearchText);
//        try{
//            _id = this.getMaxItemIdInTable(MainActivity.getAppstateTable());
//            whereClause += _id;
//            if(db.update(MainActivity.getAppstateTable(),values,whereClause,null) > 0){
//                updated = true;
//            }
//            Log.d("updateTable","Exit successfully the updateTable  method in the Accounts class.");
//        }catch (Exception e){
//            Log.d("updateTable","Exit the updateTable  method in the Accounts class with exception "+e.getMessage());
//        }finally{
//            db.close();
//            return updated;
//        }//End of try catch block
//    }//End of updateAppState

    public boolean updateTable(String table, ContentValues values){
        Log.d("updateTable","Enter the updateTable method in the AccountsDB class.");
        boolean updated = false;
        int _id = -1;
        String whereClause = MainActivity.getIdColumn()+" = ";
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        try{
            _id = values.getAsInteger(MainActivity.getIdColumn());
            whereClause += _id;
            if(db.update(table,values,whereClause,null) > 0){
                updated = true;
            }
            Log.d("updateTable","Exit successfully the updateTable  method in the Accounts class.");
        }catch (Exception e){
            Log.d("updateTable","Exit the updateTable  method in the Accounts class with exception "+e.getMessage());
        }finally{
            db.close();
            return updated;
        }//End of try catch block
    }//End of updateAppState overloaded method

    //Method to get latest item inserted in a table
    public int getMaxItemIdInTable(String table){
        Log.d("getMaxItemInTable","Enter the getMaxItemInTable method in the AccountsDB class.");
        int _id =-1;
        Cursor cursor =null;
        //SQLiteDatabase db = getReadableDatabase();
        try{
            cursor = this.runQuery("SELECT MAX(_id) FROM "+table);
            if(cursor.moveToNext()){
                _id = cursor.getInt(0);
                Log.d("getMaxItemInTable","Exit successfully the getMaxItemInTable method in the AccountsDB class.");
            }
        }catch(Exception e){
            Log.d("getMaxItemInTable","Exit the getMaxItemInTable method in the AccountsDB class with exception: "+e.getMessage());
        }finally{
            return _id;
        }//End of try catch block
    }//End of getMaxItemIdInTable

    //Method to add a new item into the database
    public int addItem(Object item) {
        Log.d("Ent_addItem","Enter addItem method in AccountsDB class.");
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        //Declare and initialize a query string variables
        String table ="";
        ContentValues fields = new ContentValues();
        String itemName ="";
        byte[] itemNameEcrypted = null;
        //Declare and initialize int variable to hold the item id to be returned. Default value is -1
        int id =-1;
        //Declare and initialize variables to be used in the insert statement (Values got from account object parameter)
        //String description;
        int category;
        long dateCreated;

        if(item instanceof Category){
            //if item is a Category object, update the Task table where the id corresponds
            table = MainActivity.getCategoryTable();
            itemName = ((Category)item).getName();
            fields.put(MainActivity.getNameColumn(),itemName);
            Icon icon = ((Category)item).getIcon();
            if(icon != null){
                fields.put(MainActivity.getIconIdColumn(),(icon.get_id()));
            }
            Log.d("addCategory","Category to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof Psswrd){
            table = MainActivity.getPsswrdTable();
            itemNameEcrypted = ((Psswrd)item).getValue();
            fields.put(MainActivity.getValueColumn(),itemNameEcrypted);
            fields.put(MainActivity.getDateCreatedColumn(),((Psswrd) item).getDateCreated());
            fields.put(MainActivity.getInitVectorColumn(),((Psswrd) item).getIv());
            Log.d("addPassword","Password to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof UserName ) {
            itemNameEcrypted = ((UserName)item).getValue();
            table = MainActivity.getUsernameTable();
            fields.put(MainActivity.getValueColumn(),itemNameEcrypted);
            fields.put(MainActivity.getDateCreatedColumn(),((UserName) item).getDateCreated());
            fields.put(MainActivity.getInitVectorColumn(),((UserName) item).getIv());
            Log.d("addUserName", "User name to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof Answer){
            table = MainActivity.getAnswerTable();
            fields.put(MainActivity.getValueColumn(),((Answer) item).getValue());
            fields.put(MainActivity.getInitVectorColumn(),((Answer) item).getIv());
            Log.d("addAnswer", "Answer to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof Question){
            itemName = ((Question)item).getValue();
            table = MainActivity.getQuestionTable();
            fields.put(MainActivity.getValueColumn(),itemName);
            fields.put(MainActivity.getAnswerIdColumn(),((Question) item).getAnswer().get_id());
            Log.d("addQuestion", "Question to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof QuestionList){
            QuestionList questionList = (QuestionList) item;
            table = MainActivity.getQuestionlistTable();
            for(int i=0;i< questionList.getSize();i++){
                fields.put(MainActivity.getQuestionIdColumn()+(i+1),questionList.getQuestions().get(i).get_id());
            }//End of for loop to add all question ids in the question list
            Log.d("addQuestionList","QuestionList to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof Icon){
            table = MainActivity.getIconTable();
            fields.put(MainActivity.getNameColumn(),((Icon)item).getName());
            fields.put(MainActivity.getIconLocationColumn(),((Icon)item).getLocation());
            //fields.put("ResourceID",((Icon)item).getResourceID());
            fields.put(MainActivity.getIconIsSelectedColumn(),  toInt(((Icon)item).isSelected()));
            Log.d("addIcon","Icon to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof AppLoggin){
            table = MainActivity.getApplogginTable();
            fields.put(MainActivity.getUserNameIdColumn(),cryptographer.encryptText(String.valueOf(((AppLoggin)item).getUserName().get_id())));
            fields.put(MainActivity.getUserNameIvColumn(),cryptographer.getIv().getIV());
            fields.put(MainActivity.getPsswrdIdColumn(),cryptographer.encryptText(String.valueOf(((AppLoggin)item).getPsswrd().get_id())));
            fields.put(MainActivity.getPsswrdIvColumn(),cryptographer.getIv().getIV());
            fields.put(MainActivity.getNameColumn(),((AppLoggin)item).getName());
            fields.put(MainActivity.getMessageColumn(),((AppLoggin)item).getMessage());
            fields.put(MainActivity.getPictureidColumn(),((AppLoggin)item).getPicture().get_id());
            Log.d("addAppLoggin","AppLoggin to be added in the addItem method in AccountsDB class.");
        }else if(item instanceof Account){
            table = MainActivity.getAccountsTable();
            Account account = (Account) item;
            fields.put(MainActivity.getNameColumn(),account.getName());
            fields.put(MainActivity.getCategoryIdColumn(),account.getCategory().get_id());
            fields.put(MainActivity.getUserNameIdColumn(),account.getUserName().get_id());
            fields.put(MainActivity.getPsswrdIdColumn(),account.getPsswrd().get_id());
            //Check if security question list is being used for this account
            if(account.getQuestionList() != null && account.getQuestionList().getSize()>0){
                fields.put(MainActivity.getQuestionListIdColumn(),account.getQuestionList().get_id());
            }
            fields.put(MainActivity.getIconIdColumn(),account.getIcon().get_id());
            fields.put(MainActivity.getIsFavoriteColumn(),account.isFavorite());
            fields.put(MainActivity.getDateCreatedColumn(),account.getDateCreated());
            //A password renew date is always given,either an actual long number or 0 if not required
            fields.put(MainActivity.getDateChangeColumn(),account.getDateChange());
            Log.d("addAccount","Account to be added in the addItem method in AccountsDB class.");
        }//End of if else statements
        try{
            id = (int) db.insert(table,null,fields);
            Log.d("addItem","Item added successfully in the addItem method in AccountsDB class.");
            //Final insertion for question assignment in case the item just inserted was QuestionList object
            if(item instanceof QuestionList){
                //Check the returned id is valid
                if(id > 0){
                    QuestionList questionList = (QuestionList) item;
                    //Insert in the DB the question assignment items for the just added list
                    for(int i=0;i<questionList.getSize();i++){
                        table = MainActivity.getQuestionassignmentTable();
                        fields = new ContentValues();
                        fields.put(MainActivity.getQuestionListIdColumn(),id);
                        fields.put(MainActivity.getQuestionIdColumn(),(questionList.getQuestions().get(i).get_id()));
                        db.insert(table,null,fields);
                    }//End of for loop
                }//End of if statement to check the id is valid
            }//End of if statement to check the item type
        }catch(Exception e){
            Log.d("addItem","Item failed to be added into the DB in the addItem method in AccountsDB class due to error: "+e.getMessage());
        }finally{
            db.close();
            Log.d("Ext_addItem","Exit addItem method in AccountsDB class.");
            //Return id of item just added into database
            return id;
        }
    }//End of addTask method

    //Method to delete a task within the database
    public boolean deleteItem(Object item) {
        Log.d("Ent_deleteItem","Enter deleteItem method in AccountsDB class.");
        boolean result = false;
        int id=-1;
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        //Declare and initialize a query string
        String deleteFrom = "DELETE FROM ";
        String whereID =" WHERE "+MainActivity.getIdColumn()+" = ";
        String table="";
        if(item instanceof Category){
            table = MainActivity.getCategoryTable();
            //Delete all items from TASK table where Category ID = id
            // db.execSQL(deleteFrom+"TASK WHERE Category = "+ ((Category) item).getId());
            id = ((Category) item).get_id();
            Log.d("deleteCATEGORY","CATEGORY to be deleted.");
        }else if(item instanceof Psswrd){
            table = MainActivity.getPsswrdTable();
            id = ((Psswrd) item).get_id();
            Log.d("deletePsswrd","PSSWRD to be deleted.");
        }else if(item instanceof UserName){
            table = MainActivity.getUsernameTable();
            //Delete all items from USERNAME table where type id is equal to id
            id= ((UserName) item).get_id();
            Log.d("deleteUserName","USERNAME to be deleted.");
        }else if(item instanceof Answer){
            table = MainActivity.getAnswerTable();
            //Delete all items from USERNAME table where type id is equal to id
            id= ((Answer) item).get_id();
            Log.d("deleteAnswer","ANSWER to be deleted.");
        }else if(item instanceof Question){
            table = MainActivity.getQuestionTable();
            //Delete all items from USERNAME table where type id is equal to id
            id= ((Question) item).get_id();
            Log.d("deleteQuestion","QUESTION to be deleted.");
        }else if(item instanceof  QuestionList){
            table = MainActivity.getQuestionlistTable();
            id = ((QuestionList) item).get_id();
            Log.d("deleteQuestionList","QUESTIONLIST to be deleted.");
        }else if(item instanceof  Icon){
            table = MainActivity.getIconTable();
            id = ((Icon) item).get_id();
            Log.d("deleteIcon","ICON to be deleted.");
        }else if(item instanceof Account){
            table = MainActivity.getAccountsTable();
            id = ((Account) item).get_id();
            Log.d("deleteAccount","ACCOUNT to be deleted.");
        }//End of if else statements
        try{
            //Run SQL statement to delete the task with id x from the TASK table
            db.execSQL(deleteFrom + table + whereID + id);
            result = true;
        }catch(Exception e){
            Log.d("deleteItem","Item failed to be deleted from DB in the deleteItem method in AccountsDB class due to error: "+e.getMessage());
        }finally{
            db.close();
            Log.d("Ext_deleteItem","Exit deleteItem method in AccountsDB class.");
            return result;
        }//End fo try catch finally block
    }//End of deleteTask method

    //Methods to query the DB

    //Method to get the list of user names from the DB
    public Cursor getIconList(){
        return  this.runQuery("SELECT * FROM "+MainActivity.getIconTable());
    }

    //Method to get tha app state cursor
    public Cursor getAppState(){
        Log.d("getAppState","Enter the getAppState method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getAppstateTable() +" WHERE "+MainActivity.getIdColumn() +" = "
                + this.getMaxItemIdInTable(MainActivity.getAppstateTable()));
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
        }
        Log.d("getAppState","Exit the getAppState method in the AccountsDB class.");
        return cursor;
    }//End of getAppState method

    //Method to get a specific Category, by passing in its DB _id as an argument
    public Cursor getAppLoginCursor(int _id){
        Log.d("getAppLogin","Enter the getCategoryByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getApplogginTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
        }
        return cursor;
    }//End of getUserNameByID method

//    public Cursor getAppLoginCursorUserAndPsswrdData(int _id){
//        Log.d("getAppLogin","Enter the getCategoryByID method in the AccountsDB class.");
//        Cursor cursor = this.runQuery(
//                "SELECT APPLOGGIN.*, Psswrd.Value as PsswrdValue, Psswrd.initVector as PsswrdIV\n" +
//                        "FROM APPLOGGIN INNER JOIN PSSWRD\n" +
//                        "ON APPLOGGIN.PsswrdID = Psswrd._id\n" +
//                        "WHERE  APPLOGGIN.UserNameID = "+ _id);
//        return cursor;
//    }

    public Cursor getAllAppLoginCursor(){
        Cursor loginList = this.runQuery("SELECT * FROM "+MainActivity.getApplogginTable());
        return loginList;
    }

    //Method to get a specific Category, by passing in its DB _id as an argument
    public Category getCategoryByID(int _id){
        Log.d("getCategoryByID","Enter the getCategoryByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getCategoryTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        if(cursor.moveToFirst()){
            Log.d("getCategoryByID","Exit successfully (category with id " +_id+ " has been found) the getCategoryByID method in the AccountsDB class.");
            return Category.extractCategory(cursor);
        }else{
            Log.d("getCategoryByID","Exit the getCategoryByID method in the AccountsDB class without finding the category with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getUserNameByID method

    //Method to get the list of categories names from the DB
    public Cursor getCategoryListCursor(){
        return  this.runQuery("SELECT * FROM "+MainActivity.getCategoryTable());
    }

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getCategoryByName(String category){
        Log.d("getCategoryByName","Enter the getCategoryByName method in the AccountsDB class.");
        if(category.contains(apostrophe)){
            category = includeApostropheEscapeChar(category);
        }
        Cursor cursor = this.runQuery("SELECT * FROM "+ MainActivity.getCategoryTable()+" WHERE "+MainActivity.getNameColumn()+" = "+ "'"+category+"'");
        if(cursor.moveToFirst()){
            Log.d("getCategoryByName","Exit successfully (CATEGORY with value " +category+ " has been found) the getCategoryByName method in the AccountsDB class.");
        }else{
            Log.d("getCategoryByName","Exit the getQuestionByID method in the AccountsDB class without finding the account with value: "+category);
        }//End of if else statement
        return cursor;
    }//End of getUserNameByID method

    //Method to get the list of user names from the DB
    public Cursor getUserNameList(){
        return  this.runQuery("SELECT * FROM "+MainActivity.getUsernameTable());
    }

    //Method to get a specific Category, by passing in its DB _id as an argument
    public Account getAccountByID(int _id){
        Log.d("getAccountByID","Enter the getAccountByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        Account account = null;
        if(cursor.moveToFirst()){
            account = Account.extractAccount(cursor);
            Log.d("getAccountByID","Exit successfully (account with id " +_id+ " has been found) the getAccountByID method in the AccountsDB class.");
        }else{
            Log.d("getAccountByID","Exit the getAccountByID method in the AccountsDB class without finding the category with id: "+_id);
        }//End of if else statement
        return account;
    }//End of getAccountByID method

    //Method to get a specific Category, by passing in its DB _id as an argument
    public Cursor getAccountCursorByID(int _id){
        Log.d("getAccountByID","Enter the getAccountByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        //Cursor cursor = this.runQuery("SELECT * FROM ACCOUNTS WHERE _id = "+ _id);
        if(cursor.moveToFirst()){
            Log.d("getAccountByID","Exit successfully (account with id " +_id+ " has been found) the getAccountByID method in the AccountsDB class.");
        }else{
            Log.d("getAccountByID","Exit the getAccountByID method in the AccountsDB class without finding the category with id: "+_id);
        }//End of if else statement
        return cursor;
    }//End of getAccountByID method

    //Method to get a specific Category, by passing in its DB _id as an argument
    public Cursor getAccountCursorByName(String accountName){
        Log.d("getAccountCursorByName","Enter the getAccountCursorByName method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE lower(ACCOUNTS.Name) = '"
                + accountName.toLowerCase() + "'");
        if(cursor.moveToFirst()){
            Log.d("getAccountCursorByName","Exit successfully (account with name " +accountName+ " has been found) the getAccountCursorByName method in the AccountsDB class.");
        }else{
            Log.d("getAccountCursorByName","Exit the getAccountCursorByName method in the AccountsDB class without finding the account with name: "+accountName);
        }//End of if else statement
        return cursor;
    }//End of getAccountByName method

    //Method to get the list of accounts from the DB
    public Cursor getAccountsList(){
        Log.d("getAccountsList","Enter/Exit the getAccountsList method in the AccountsDB class.");
        return  this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable());
    }

    //Method to get the number of times a specific user name is being used in different accounts as per the DB
    public int getTimesUsedUserName(int userNameID){
        Log.d("getTimesUsedUserName","Enter/Exit the getTimesUsedUserName method in the AccountsDB class.");
        return this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE "+MainActivity.getUserNameIdColumn()+" = "+userNameID).getCount();
    }

    //Method to return a cursor with all the UserNames items sorted by number of times used
    public Cursor getUserNameCursorSortedByTimesUsed(){
        Log.d("getUserNameSorted","Enter/Exit the getUserNameCursorSortedByTimesUsed method in the AccountsDB class.");
        return this.runQuery("SELECT USERNAME.*, COUNT(ACCOUNTS.UserNameID) AS times_used\n" +
                "FROM USERNAME LEFT JOIN ACCOUNTS \n" +
                "ON ACCOUNTS.UserNameID =  USERNAME._id\n" +
                "GROUP BY USERNAME._id\n" +
                "ORDER BY times_used DESC");
    }//End of getUserNameCursorSortedByTimesUsed method

    //Method to get the number of times a specific password is being used in different accounts as per the DB
    public int getTimesUsedPsswrd(int psswrdID){
        Log.d("getTimesUsedPsswrd","Enter/Exit the getTimesUsedPsswrd method in the AccountsDB class.");
        return this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE "+MainActivity.getPsswrdIdColumn()+" = "+psswrdID).getCount();
    }

    //Method to return a cursor with all the UserNames items sorted by number of times used
    public Cursor getPsswrdsSortedByTimesUsed(){
        Log.d("getPsswrdSorted","Enter/Exit the getPsswrdsSortedByTimesUsed method in the AccountsDB class.");
        return this.runQuery("SELECT PSSWRD.*,COUNT(ACCOUNTS.PsswrdID) AS times_used\n" +
                "FROM PSSWRD LEFT JOIN ACCOUNTS\n" +
                "ON PSSWRD._id =  ACCOUNTS.PsswrdID\n" +
                "GROUP BY PSSWRD._id\n" +
                "ORDER BY times_used DESC");
    }//End of getUserNameCursorSortedByTimesUsed method

    //Method to get the number of times a specific question is being used in different accounts as per the DB
    public int getTimesUsedQuestion(int questionID){
        Log.d("getTimesUsedQuestion","Enter the getTimesUsedQuestion method in the AccountsDB class.");
        int timesUsed = 0;
        //Get a list of questionLists that hold the question to be deleted
        Cursor questionListsWithThisQuestion = this.getQuestionAssignmentCursorFor1QuestionID(questionID); //this.runQuery("SELECT * FROM QUESTIONASSIGNMENT WHERE QUESTIONASSIGNMENT.QuestionID = "+questionID);
        //Now get the list of accounts using the those question lists
        //It's necessary to check a questionList that holds the specific questionID is being used more than once (a questionList can be assigned to multiple accounts)
        if(questionListsWithThisQuestion.moveToFirst()){
            //If that is the case, more than one list holding the question, check for each list how many accounts are using the list
            Cursor accountListUsingQuestionList = null;
            do{
                accountListUsingQuestionList =  this.getAccountsWithSpecifcValue(MainActivity.getQuestionListIdColumn(),questionListsWithThisQuestion.getInt(1)); //this.runQuery("SELECT * FROM ACCOUNTS WHERE QuestionListID = " + );
                if(accountListUsingQuestionList.moveToFirst()){
                    timesUsed += accountListUsingQuestionList.getCount();
                }
            }while(questionListsWithThisQuestion.moveToNext());
        }//End of if statement that check the cursor with the question lists move to first position and can be iterated
        Log.d("getTimesUsedQuestion","Exit the getTimesUsedQuestion method in the AccountsDB class.");
        return timesUsed;
    }//End of getTimesUsedQuestion method

    //Method to get the number of times a specific question list is being used in different accounts as per the DB
    public int getTimesUsedQuestionList(int questionListID){
        Log.d("getTimesUsedQuestList","Enter the getTimesUsedQuestionList method in the AccountsDB class.");
        int timesUsed = 0;
        //Get a list of questionLists that hold the question to be deleted
        Cursor accountsWithThisQuestionList = this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE " + MainActivity.getQuestionListIdColumn()+" = "+questionListID);
        //Now get the list of accounts using the those question lists
        //It's necessary to check a questionList that holds the specific questionID is being used more than once (a questionList can be assigned to multiple accounts)
        if(accountsWithThisQuestionList.moveToFirst()){
            timesUsed = accountsWithThisQuestionList.getCount();
        }//End of if statement that check the cursor with the question lists move to first position and can be iterated
        Log.d("getTimesUsedQuestList","Exit the getTimesUsedQuestionList method in the AccountsDB class.");
        return  timesUsed;
    }//End of getTimesUsedQuestionList method

    //Method to return a cursor with all the UserNames items sorted by number of times used
    public Cursor getQuestionsSortedByTimesUsed(){
        Log.d("getQuestSorted","Enter/Exit the getQuestionsSortedByTimesUsed method in the AccountsDB class.");
        return this.runQuery("SELECT *, COUNT(QUESTIONASSIGNMENT.QuestionID) AS times_used\n" +
                "FROM (SELECT QUESTION._id AS id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer, \n" +
                "ANSWER.initVector AS initVector \n" +
                "FROM QUESTION LEFT JOIN ANSWER \n" +
                "ON QUESTION.AnswerID = ANSWER._id) LEFT JOIN QUESTIONASSIGNMENT\n" +
                "ON id =  QUESTIONASSIGNMENT.QuestionID\n" +
                "GROUP BY id\n" +
                "ORDER BY times_used DESC");
    }//End of getUserNameCursorSortedByTimesUsed method

    //Method to get the number of times a specific question list is being used in different accounts as per the DB
    public int getTimesUsedIconInAccounts(int iconID){
        Log.d("getTimesUsedIconInAcc","Enter the getTimesUsedIconInAccounts method in the AccountsDB class.");
        int timesUsed = 0;
        //Get a list of questionLists that hold the question to be deleted
        Cursor accountsWithThisQuestionList = this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE " + MainActivity.getIconIdColumn()+" = "+iconID);
        //Now get the list of accounts using the those question lists
        //It's necessary to check a questionList that holds the specific questionID is being used more than once (a questionList can be assigned to multiple accounts)
        if(accountsWithThisQuestionList.moveToFirst()){
            timesUsed = accountsWithThisQuestionList.getCount();
        }//End of if statement that check the cursor with the question lists move to first position and can be iterated
        Log.d("getTimesUsedIconInAcc","Exit the getTimesUsedIconInAccounts method in the AccountsDB class.");
        return  timesUsed;
    }//End of getTimesUsedQuestionList method

    //Method to get the number of times a specific question list is being used in different accounts as per the DB
    public int getTimesUsedIconInCategory(int iconID){
        Log.d("getTimesUsedQuestList","Enter the getTimesUsedIconInCategory method in the AccountsDB class.");
        int timesUsed = 0;
        //Get a list of categories that hold the icon
        Cursor categoriesWithThisIcon = this.runQuery("SELECT * FROM "+MainActivity.getCategoryTable()+" WHERE " + MainActivity.getIconIdColumn()+" = "+iconID);
        //Now get the list of categories using the those question lists
        if(categoriesWithThisIcon.moveToFirst()){
            timesUsed = categoriesWithThisIcon.getCount();
        }//End of if statement that check the cursor with the question lists move to first position and can be iterated
        Log.d("getTimesUsedQuestList","Exit the getTimesUsedIconInCategory method in the AccountsDB class.");
        return  timesUsed;
    }//End of getTimesUsedQuestionList method


    //Method to retrieve a specific Icon from DB by passing in it's ID
    public Icon getIconByID(int _id){
        Log.d("getIconByID","Enter the getIconByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getIconTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
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
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getIconTable()+" WHERE "+MainActivity.getNameColumn()+" = '"+ name+"'");
        if(cursor.moveToFirst()){
            //cursor.moveToFirst();
            Log.d("getIconByID","Exit successfully (icon with id " +name+ " has been found) the getIconByID method in the AccountsDB class.");
            return Icon.extractIcon(cursor);
        }else{
            Log.d("getIconByID","Exit the getIconByID method in the AccountsDB class without finding the account with name: "+name);
            return null;
        }//End of if else statement
    }//End of getIconByID method

    //Method to retrieve a specific Icon from DB by passing in it's ID
    public Icon getIconByUriLocation(String uri){
        Log.d("getIconByID","Enter the getIconByUriLocation method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getIconTable()+"  WHERE "+MainActivity.getIconLocationColumn()+" = '"+ uri + "'");
        if(cursor.moveToFirst()){
            //cursor.moveToFirst();
            Log.d("getIconByID","Exit successfully (icon with location: " +uri+ " has been found) the getIconByUriLocation method in the AccountsDB class.");
            return Icon.extractIcon(cursor);
        }else{
            Log.d("getIconByID","Exit the getIconByUriLocation method in the AccountsDB class without finding the account with name: "+uri);
            return null;
        }//End of if else statement
    }//End of getIconByID method


    //Method to get a specific user name, by passing in its DB _id as an argument
    public UserName getUserNameByID(int _id){
        Log.d("getUserNameByID","Enter the getUserNameByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getUsernameTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getUserNameByID","Exit successfully (user name with id " +_id+ " has been found) the getUserNameByID method in the AccountsDB class.");
            return UserName.extractUserName(cursor);
        }else{
            Log.d("getUserNameByID","Exit the getUserNameByID method in the AccountsDB class without finding the user name with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getUserNameByID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getUserNameCursorByID(int _id){
        Log.d("getUserNameByID","Enter the getUserNameByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getUsernameTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getUserNameByID","Exit successfully (user name with id " +_id+ " has been found) the getUserNameByID method in the AccountsDB class.");
        }else{
            Log.d("getUserNameByID","Exit the getUserNameByID method in the AccountsDB class without finding the user name with id: "+_id);
        }//End of if else statement
        return cursor;
    }//End of getUserNameByID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getUserNameByName(String userName){
        Log.d("getUserNameByName","Enter the getUserNameByName method in the AccountsDB class.");
        //Declare and initialize a boolean flag to inform the name was found
        boolean found = false;
        String userNameDecrypted = "";
        //Get all data from USERNAME table
        Cursor userNameCursor = this.runQuery("SELECT * FROM "+MainActivity.getUsernameTable());
        //Iterate through it, decrypt user name values and compare against value passed in as parameter
        while(!found && userNameCursor.moveToNext()){
            //Decrypt the user name value coming form DB
            userNameDecrypted = cryptographer.decryptText(userNameCursor.getBlob(1),new IvParameterSpec(userNameCursor.getBlob(2)));
            //Compare the decrypted user name user name being looked for
            if(userName.trim().equals(userNameDecrypted.trim())){
                found = true;
            }//End of if statement to compare user names
        }//End of while loop to iterate through list of user names
        //Make adjustments to return proper value based on the found boolean flag
        if(found){
            Log.d("getUserNameByName","Exit successfully (user name with value " +userName + " has been found) the getUserNameByID method in the AccountsDB class.");
            return this.getUserNameCursorByID(userNameCursor.getInt(0));
        }else{
            Log.d("getUserNameByName","Exit the getUserNameByName method in the AccountsDB class without finding the user name with value: "+userName);
            //This is a work around to avoid null excemption when the user name being looked for does not exist. Down the road the returned cursor
            //can be checked that count is > 0.
            return this.getUserNameCursorByID(-1);
        }//End of if else statement
    }//End of getUserNameByID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getUserNamesSortedColumnUpDown(String column, String order){
        Log.d("getUserSorted","Enter the getUserNamesSortedColumnUpDown method in the AccountsDB class.");
        Cursor  listOfUserNamesSorted = null;
        listOfUserNamesSorted = runQuery("SELECT * FROM "+MainActivity.getUsernameTable()+ " ORDER BY " + column + " " +order);
        Log.d("getUserSorted","Exit the getUserNamesSortedColumnUpDown method in the AccountsDB class.");
        return listOfUserNamesSorted;
    }//End of getUserNameByID method

    //Method to get a specific password, by passing in its DB _id as an argument
    public Psswrd getPsswrdByID(int _id){
        Log.d("getPsswrdByID","Enter the getPsswrdByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getPsswrdTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getPsswrdByID","Exit successfully (password with id " +_id+ " has been found) the getPsswrdByID method in the AccountsDB class.");
            return Psswrd.extractPsswrd(cursor);
        }else{
            Log.d("getPsswrdByID","Exit the getPsswrdByID method in the AccountsDB class without finding the account with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getPsswrdByID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getPsswrdCursorByID(int _id){
        Log.d("getPsswrdCursorByID","Enter the getPsswrdCursorByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getPsswrdTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getPsswrdCursorByID","Exit successfully (password with id " +_id+ " has been found) the getPsswrdCursorByID method in the AccountsDB class.");
        }else{
            Log.d("getPsswrdCursorByID","Exit the getUserNameByID method in the AccountsDB class without finding the password with id: "+_id);
        }//End of if else statement
        return cursor;
    }//End of getUserNameByID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getPsswrdByName(String psswrd){
        Log.d("getUserNameByName","Enter the getUserNameByName method in the AccountsDB class.");
        //Declare and initialize a boolean flag to inform the name was found
        boolean found = false;
        String psswrdDecrypted = "";
        //Get all data from PSSWRD table
        Cursor psswrdCursor = this.runQuery("SELECT * FROM "+MainActivity.getPsswrdTable());
        //Iterate through it, decrypt user name values and compare against value passed in as parameter
        while(!found && psswrdCursor.moveToNext()){
            //Decrypt the user name value coming form DB
            psswrdDecrypted = cryptographer.decryptText(psswrdCursor.getBlob(1),new IvParameterSpec(psswrdCursor.getBlob(2)));
            //Compare the decrypted user name user name being looked for
            if(psswrd.trim().equals(psswrdDecrypted.trim())){
                found = true;
            }//End of if statement to compare user names
        }//End of while loop to iterate through list of user names
        if(found){
            Log.d("getPsswrdByName","Exit successfully (password with value " +psswrd + " has been found) the getPsswrdByName method in the AccountsDB class.");
            return this.getPsswrdCursorByID(psswrdCursor.getInt(0));
         }else{
            Log.d("getPsswrdByName","Exit the getPsswrdByName method in the AccountsDB class without finding the password with value: "+psswrd);
            //This is a work around to avoid null exception when the password being looked for does not exist. Down the road the returned cursor
            //can be checked that count is > 0.
            return  this.getPsswrdCursorByID(-1);
        }//End of if else statement
    }//End of getUserNameByID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getPsswrdsSortedColumnUpDown(String column, String order){
        Log.d("getPsswrdsSorted","Enter the getPsswrdsSortedColumnUpDown method in the AccountsDB class.");
        Cursor  listOfAccountsSortedAlpha = null;
        listOfAccountsSortedAlpha = runQuery("SELECT * FROM "+MainActivity.getPsswrdTable()+ " ORDER BY " + column + " " +order);
        Log.d("getPsswrdsSorted","Exit the getPsswrdsSortedColumnUpDown method in the AccountsDB class.");
        return listOfAccountsSortedAlpha;
//        Log.d("getPsswrdsSorted","Enter the getUserNameByName method in the AccountsDB class.");
//        //Declare and initialize a boolean flag to inform the name was found
//        boolean found = false;
//        String psswrdDecrypted = "";
//        //Get all data from PSSWRD table
//        Cursor psswrdCursor = this.runQuery("SELECT * FROM PSSWRD");
//        //Iterate through it, decrypt user name values and compare against value passed in as parameter
//        while(!found && psswrdCursor.moveToNext()){
//            //Decrypt the user name value coming form DB
//            psswrdDecrypted = cryptographer.decryptText(psswrdCursor.getBlob(1),new IvParameterSpec(psswrdCursor.getBlob(2)));
//            //Compare the decrypted user name user name being looked for
//            if(psswrd.trim().equals(psswrdDecrypted.trim())){
//                found = true;
//            }//End of if statement to compare user names
//        }//End of while loop to iterate through list of user names
//        if(found){
//            Log.d("getPsswrdByName","Exit successfully (password with value " +psswrd + " has been found) the getPsswrdByName method in the AccountsDB class.");
//            return this.getPsswrdCursorByID(psswrdCursor.getInt(0));
//        }else{
//            Log.d("getPsswrdByName","Exit the getPsswrdByName method in the AccountsDB class without finding the password with value: "+psswrd);
//            //This is a work around to avoid null exception when the password being looked for does not exist. Down the road the returned cursor
//            //can be checked that count is > 0.
//            return  this.getPsswrdCursorByID(-1);
//        }//End of if else statement
    }//End of getUserNameByID method

    //Method to get the list of passwords from the DB
    public Cursor getPsswrdList(){
        return  this.runQuery("SELECT * FROM "+MainActivity.getPsswrdTable());
    }

    //Method to retrieve the list of categories stored on the database
    public ArrayList<Category> getCategoryList(){
        Log.d("Ent_getCategoryList","Enter getCategoryList method in the AccountsDB class.");
        //Declare and instantiate Array list of Category objects
        ArrayList<Category> list = new ArrayList<Category>();
        //Define a string to hold the sql query
        String query = "SELECT * FROM "+MainActivity.getCategoryTable();
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
        //Modify the list to be kept on memory and add two new categories, which are not stored in DB: Home and Favorites categories
        list.add(0,MainActivity.getHomeCategory());
        list.add(1,MainActivity.getFavCategory());
        Log.d("Ext_getCategoryList","Exit getCategoryList method in the TaskDB class.");
        return list;
    }//End of getGroceryList method

    //Method to get a specific answer, by passing in its DB _id as an argument
    public Answer getAnswerByID(int _id){
        Log.d("getAnswerByID","Enter the getAnswerByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT * FROM "+MainActivity.getAnswerTable()+" WHERE "+MainActivity.getIdColumn()+" = "+ _id);
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
        Cursor listOfQuestionsLists = this.runQuery("SELECT * FROM "+MainActivity.getQuestionlistTable());
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
    public Cursor getPreLoadedQuestions(){
        Log.d("getPreLoadedQuestions","Enter the getPreLoadedQuestions method in the AccountsDB class.");
        //Declare and initialize cursor to hold list of questions available from DB
        Cursor preLoadedQuestions = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS \n" +
                "Answer, ANSWER.initVector AS initVector FROM QUESTION LEFT JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id <= "+this.NUMBER_OF_PRELOADED_QUESTIONS);
        Log.d("getPreLoadedQuestions","Exit the getPreLoadedQuestions method in the AccountsDB class.");
        return preLoadedQuestions;
    }//End of getListQuestionsAvailable method

    //Method to get cursor with list of questions available
    public Cursor getPreLoadedCategories(){
        Log.d("getPreLoadedCategories","Enter the getPreLoadedCategories method in the AccountsDB class.");
        //Declare and initialize cursor to hold list of categories available from DB
        Cursor preLoadedQuestions = this.runQuery("SELECT * FROM " +MainActivity.getCategoryTable() + " WHERE "+ MainActivity.getIdColumn()+" <= "+this.NUMBER_OF_PRELOADED_CATEGORIES);
        Log.d("getPreLoadedQuestions","Exit the getPreLoadedCategories method in the AccountsDB class.");
        return preLoadedQuestions;
    }//End of getListQuestionsAvailable method

    //Method to get cursor with list of questions available
    public Cursor getListQuestionsAvailable(){
        Log.d("getListOfQuestionLists","Enter the getListOfQuestionLists method in the AccountsDB class.");
        //Declare and initialize cursor to hold list of questions available from DB
        Cursor questionsAvailable = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer," +
                "ANSWER.initVector AS initVector FROM QUESTION JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id;");
        Log.d("getListOfQuestionLists","Exit the getListOfQuestionLists method in the AccountsDB class.");
        return questionsAvailable;
    }//End of getListQuestionsAvailable method

    //Method to get cursor with list of questions available
    public Cursor getListQuestionsAvailableNoAnsw(){
        Log.d("getListOfQuestionLists","Enter the getListQuestionsAvailableNoAnsw method in the AccountsDB class.");
        //Declare and initialize cursor to hold list of questions available from DB
        Cursor questionsAvailable = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer," +
                "ANSWER.initVector AS initVector FROM QUESTION LEFT JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id;");
        Log.d("getListOfQuestionLists","Exit the getListQuestionsAvailableNoAnsw method in the AccountsDB class.");
        return questionsAvailable;
    }//End of getListQuestionsAvailable method

    //Method to get a specific question, by passing in its DB _id as an argument
    public Question getQuestionByID(int _id){
        Log.d("getQuestionByID","Enter the getQuestionByID method in the AccountsDB class.");
        //Cursor cursor = this.runQuery("SELECT * FROM QUESTION  JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id);
        Cursor cursor = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer, " +
                "ANSWER.initVector AS initVector FROM QUESTION  " +
                "LEFT JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getQuestionByID","Exit successfully (QUESTION with id " +_id+ " has been found) the getQuestionByID method in the AccountsDB class.");
            return Question.extractQuestion(cursor);
        }else{
            Log.d("getQuestionByID","Exit the getQuestionByID method in the AccountsDB class without finding the account with id: "+_id);
            return null;
        }//End of if else statement
    }//End of getQuestionByID method

    //Method to get a specific question, by passing in its DB _id as an argument
    public Cursor getQuestionByValue(String value){
        Log.d("getQuestionByValue","Enter the getQuestionByValue method in the AccountsDB class.");
        if(value.contains(apostrophe)){
            value = includeApostropheEscapeChar(value);
        }
        //Cursor cursor = this.runQuery("SELECT * FROM QUESTION WHERE QUESTION.Value = '"+ value+"'");
        Cursor cursor = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer, " +
                "ANSWER.initVector AS initVector FROM QUESTION  " +
                "JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION.Value = '"+  value+"'");
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getQuestionByValue","Exit successfully (QUESTION with value " +value+ " has been found) the getQuestionByValue method in the AccountsDB class.");
        }else{
            cursor = null;
            Log.d("getQuestionByID","Exit the getQuestionByID method in the AccountsDB class without finding the account with value: "+value);
        }//End of if else statement
        return cursor;
    }//End of getQuestionByID method

    //Method to get a specific question, by passing in its DB _id as an argument
    public Cursor getListOfQuestionsExceptTheOnePassedIn(String value){
        Log.d("getQuestExceptPassIn","Enter the getQuestionByValue method in the AccountsDB class.");
        if(value.contains(apostrophe)){
            value = includeApostropheEscapeChar(value);
        }
        Cursor cursor = this.runQuery("SELECT * FROM QUESTION WHERE QUESTION.Value <> '"+ value+"'");
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getQuestExceptPassIn","Exit successfully (QUESTION with value " +value+ " has been found) the getQuestionByValue method in the AccountsDB class.");
        }else{
            cursor = null;
            Log.d("getQuestExceptPassIn","Exit the getQuestionByID method in the AccountsDB class without finding the account with value: "+value);
        }//End of if else statement
        return cursor;
    }//End of getQuestionByID method

    //Method to get a question cursor by passing in its DB id as argument
    public Cursor getQuestionCursorByID(int _id){
        Log.d("getQuestionCursorByID0","Enter the getQuestionCursorByID method in the AccountsDB class.");
        Cursor cursor = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer, " +
                "ANSWER.initVector AS initVector FROM QUESTION  " +
                "LEFT JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            Log.d("getPsswrdCursorByID","Exit successfully (password with id " +_id+ " has been found) the getPsswrdCursorByID method in the AccountsDB class.");
        }else{
            cursor = null;
            Log.d("getPsswrdCursorByID","Exit the getUserNameByID method in the AccountsDB class without finding the password with id: "+_id);
        }//End of if else statement
        return cursor;
    }//End of getQuestionCursorByID method

    //Method to get a a two question cursor by passing in their DB ids as arguments
    public Cursor getQuestionCursorByID(int _id1, int _id2){
        Log.d("getQuestionCursorByID1","Enter the overloaded getQuestionCursorByID method in the AccountsDB class.");
        Cursor question = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer," +
                "ANSWER.initVector AS initVector FROM QUESTION" +
                " JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id1 + " OR QUESTION._id = "+ _id2);
        Log.d("getQuestionCursorByID1","Exit the overloaded getQuestionCursorByID method in the AccountsDB class.");
        return question;
    }//End of getQuestionCursorByID method

    //Method to get a a three question cursor by passing in their DB ids as arguments
    public Cursor getQuestionCursorByID(int _id1, int _id2, int _id3){
        Log.d("getQuestionCursorByID2","Enter the overloaded getQuestionCursorByID method in the AccountsDB class.");
        Cursor question = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer, " +
                "ANSWER.initVector AS initVector FROM QUESTION " +
                "JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION._id = "+ _id1+ " OR QUESTION._id = "+ _id2 +" OR QUESTION._id = "+ _id3);
        Log.d("getQuestionCursorByID2","Exit the overloaded getQuestionCursorByID method in the AccountsDB class.");
        return question;
    }//End of getQuestionCursorByID method

    //Method to get a list of questions, by passing in its DB _id as an argument
    public QuestionList getQuestionListById(int _id){
        Log.d("getQuestionListById","Enter the getQuestionListById method in the AccountsDB class.");
        //Declare and initialize null questionlist object to be returned
        QuestionList questionList = null;
        //Declare and initialize cursor to hold question list data from DB
        Cursor cursor = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID," +
                "ANSWER.Value AS Answer, ANSWER.initVector AS initVector FROM\n" +
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

    public Cursor getQuestionAssignmentCursorFor1QuestionID(int _id){
        Log.d("questAssgCur1QuestID","Enter the getQuestionAssignmentCursorFor1QuestionID method in the AccountsDB class.");
        Cursor cursor = null;
        cursor = this.runQuery("SELECT * FROM "+MainActivity.getQuestionassignmentTable()+"  WHERE "+MainActivity.getQuestionassignmentTable()+ ".QuestionID = "+ _id);
        Log.d("questAssgCur1QuestID","Enter the getQuestionAssignmentCursorFor1QuestionID method in the AccountsDB class.");
        return cursor;
    }//End of the getQuestionAssignmentCursorFor1QuestionID method


    //Method that returns a security question list ID by passing in the questions that make up the list as argument
    public int getSecQuestionListID(QuestionList questionList){
        Log.d("getSecQuestionListID","Enter the getSecQuestionListID method in the AccountsDB class.");
        int _id = -1;
        Cursor questionCursor = null;
        switch(questionList.getSize()){
            case 1:
                questionCursor = this.runQuery("SELECT * FROM QUESTIONLIST WHERE QuestionID1 = " + questionList.getQuestions().get(0).get_id()
                        + " AND QuestionID2 IS NULL"
                        + " AND QuestionID3 IS NULL");
                Log.d("getSecQuestionListID","1 question detected by getSecQuestionListID method in the AccountsDB class.");
                break;
            case 2:
                questionCursor = this.runQuery("SELECT * FROM QUESTIONLIST WHERE QuestionID1 = " + questionList.getQuestions().get(0).get_id()
                                                +" AND QuestionID2 = " + questionList.getQuestions().get(1).get_id()
                                                +" AND QuestionID3 IS NULL");
                Log.d("getSecQuestionListID","2 questions detected by getSecQuestionListID method in the AccountsDB class.");
                break;
            case 3:
                questionCursor = this.runQuery("SELECT * FROM QUESTIONLIST WHERE QuestionID1 = " + questionList.getQuestions().get(0).get_id()
                        + " AND QuestionID2 = " + questionList.getQuestions().get(1).get_id()
                        + " AND QuestionID3 = " + questionList.getQuestions().get(2).get_id());
                Log.d("getSecQuestionListID","3 questions detected by getSecQuestionListID method in the AccountsDB class.");
                break;
            default:
                _id = -1;
                Log.d("getSecQuestionListID","no question list detected by getSecQuestionListID method in the AccountsDB class.");
        }
        if(questionCursor != null && questionCursor.getCount() > 0){
            questionCursor.moveToFirst();
            _id = questionCursor.getInt(0);
        }
        Log.d("getSecQuestionListID","Exit the getSecQuestionListID method in the AccountsDB class.");
        return _id;
    }//End of getSecQuestionListID method

    //Method to get a specific user name, by passing in its DB _id as an argument
    public Cursor getQuestionsSortedColumnUpDown(String order){
        Log.d("getQuestSorted","Enter the getQuestionsSortedColumnUpDown method in the AccountsDB class.");
        Cursor  listOfAccountsSortedAlpha = null;

        listOfAccountsSortedAlpha = runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer," +
                "ANSWER.initVector AS initVector FROM QUESTION LEFT JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id ORDER BY QUESTION.Value "+order);
        Log.d("getQuestSorted","Exit the getQuestionsSortedColumnUpDown method in the AccountsDB class.");
        return listOfAccountsSortedAlpha;
    }//End of getUserNameByID method

    public ArrayList getAccountsIDListUsingItemWithID(String itemType, int itemID){
        Log.d("getAccUsingItemWithID","Enter the getAccountsUsingItemWithID method in the AccountsDB class.");
        Cursor listOfAccountUsingTheItem = null;
        ArrayList listOfQuestionListIDsUsingTheItem = new ArrayList();
        ArrayList listOfAccountIDsUsingTheItem = new ArrayList();
        String column = "";
        //Check the itemType to assign proper column to be used in the Where condition of the SQL query
        if(itemType.equals(MainActivity.getUserName())){
            column = MainActivity.getUserNameIdColumn();
        }else if(itemType.equals(MainActivity.getPASSWORD())){
            column = MainActivity.getPsswrdIdColumn();
        }else if(itemType.equals(MainActivity.getQuestionList())){
            column = MainActivity.getQuestionListIdColumn();
        }else if(itemType.equals(MainActivity.getCategoryIdColumn())){
            column = MainActivity.getCategoryIdColumn();
        }
        if(itemType.equals(MainActivity.getQUESTION())){
            //In case of questions, the SQL must be run differently as the accounts hold the question list ID and not the question ID itself
            //First, it's necessary to check all the questionLists that have the question to be deleted
            Cursor questionListsWithQuestionToBeDeleted = this.getQuestionAssignmentCursorFor1QuestionID(itemID); //runQuery("SELECT * FROM QUESTIONASSIGNMENT WHERE QUESTIONASSIGNMENT.QuestionID = "+ itemID);
            //Now query the DB to find all the accounts with each questionListID that holds the question to be deleted
            column = MainActivity.getQuestionListIdColumn();
            //Get the list of questionList ids that holds the question and store it in ArrayList

            while(questionListsWithQuestionToBeDeleted.moveToNext()){
                //Every account using any of the lists stored in questionListsWithQuestionToBeDeleted, should be stored
                listOfQuestionListIDsUsingTheItem.add(questionListsWithQuestionToBeDeleted.getInt(1));
            }
            //Call method to get account row from ACCOUNTS table that contains any of the values passed in
            listOfAccountUsingTheItem = this.getRowsThatMeetMultipleValuesCriteria(MainActivity.getAccountsTable(),column, listOfQuestionListIDsUsingTheItem);
            // listOfAccountUsingTheItem = this.runQuery("SELECT * FROM ACCOUNTS WHERE "+ column + " = " + questionListsWithQuestionToBeDeleted.getInt(1));
        }else{
            listOfAccountUsingTheItem = this.runQuery("SELECT * FROM "+MainActivity.getAccountsTable()+" WHERE "+ column + " = " + itemID);
        }//End of if else statement to check if item type is question

        //Add the each account id to the list of accounts using the item
        if(listOfAccountUsingTheItem.moveToFirst()){
            do{
                listOfAccountIDsUsingTheItem.add(listOfAccountUsingTheItem.getInt(0));
            }while(listOfAccountUsingTheItem.moveToNext());
        }//End of while loop to fill up list of accounts id that have the passed in item
        Log.d("getAccUsingItemWithID","Exit the getAccountsUsingItemWithID method in the AccountsDB class.");
        return listOfAccountIDsUsingTheItem;
    }//End of getAccountsUsingItemWithID method

    //Method to return cursor with rows from the accounts table with specific value in the column name passed in as argument
    public Cursor getAccountsWithSpecifcValue(String column, int itemID){
        Log.d("getAccWithSpecifcValue","Enter the getAccountCursorByName method in the AccountsDB class.");
        Cursor  listOfAccountsThatHoldsASpecificValue = null;
        listOfAccountsThatHoldsASpecificValue = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + column + " = " + itemID);
        Log.d("getAccWithSpecifcValue","Exit the getAccountCursorByName method in the AccountsDB class.");
        return listOfAccountsThatHoldsASpecificValue;
    }//End of getAccountsWithSpecifcValue method

    //Method to return cursor with rows from the accounts table with specific value in the column name passed in as argument
    public Cursor getAccountsWithSpecifcValueAndCategory(String column, int itemID, int categoryID){
        Log.d("getAccWithSpecifcValue","Enter the getAccountCursorByName method in the AccountsDB class.");
        Cursor  listOfAccountsThatHoldsASpecificValue = null;
        if(categoryID == -1){
            listOfAccountsThatHoldsASpecificValue = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + column + " = " + itemID);
        }else if(categoryID ==-2){
            listOfAccountsThatHoldsASpecificValue = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + column + " = " + itemID
                    + " AND "+MainActivity.getIsFavoriteColumn() +"= 1" );
        }else{
            listOfAccountsThatHoldsASpecificValue = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + column + " = " + itemID
                    +" AND "+MainActivity.getCategoryIdColumn() +" = "+categoryID);
        }
        Log.d("getAccWithSpecifcValue","Exit the getAccountCursorByName method in the AccountsDB class.");
        return listOfAccountsThatHoldsASpecificValue;
    }//End of getAccountsWithSpecifcValue method

    //Method to return cursor with rows from the accounts table with specific value in the column name passed in as argument
    public Cursor getAccountsSortedByColumnUpOrDown(String column, String order){
        Log.d("getAccountsSortedAlpUp","Enter the getAccountsSortedAlphaUp method in the AccountsDB class.");
        Cursor  listOfAccountsSortedAlpha = null;
        Category currentCategory = MainActivity.getCurrentCategory();
        switch(currentCategory.get_id()){
            case -2:
                listOfAccountsSortedAlpha = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+  " WHERE "+ MainActivity.getIsFavoriteColumn() +" = 1"
                        +" ORDER BY " + column + " " +order);
                break;
            case -1:
                listOfAccountsSortedAlpha = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+" ORDER BY " + column + " " +order);
                break;
            default:
                listOfAccountsSortedAlpha = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+  " WHERE "+ MainActivity.getCategoryIdColumn() +" = "
                        +MainActivity.getCurrentCategory().get_id()+" ORDER BY " + column + " " +order);
                break;
        }

        Log.d("getAccountsSortedAlpUp","Exit the getAccountsSortedAlphaUp method in the AccountsDB class.");
        return listOfAccountsSortedAlpha;
    }//End of getAccountsWithSpecifcValue method

    //Method to return cursor with rows from the accounts table with specific value in the column name passed in as argument
    public Cursor getAccountsThatContainsThisTextInName(String searchText,int categoryID){
        Log.d("getAccWithSpecValInName","Enter the getAccountsThatContainsThisTextInName method in the AccountsDB class.");
        Cursor  listOfAccountsThatContainsThisTexInName = null;
        if(categoryID == -1){
            listOfAccountsThatContainsThisTexInName = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + MainActivity.getNameColumn() + " LIKE '%" + searchText + "%'");
        }else if(categoryID ==-2){
            listOfAccountsThatContainsThisTexInName = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + MainActivity.getNameColumn() + " LIKE '%" + searchText + "%'"
                    +" AND "+MainActivity.getIsFavoriteColumn() +"= 1" );
        }else{
            listOfAccountsThatContainsThisTexInName = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + MainActivity.getNameColumn() + " LIKE '%" + searchText + "%'"
                    +" AND "+MainActivity.getCategoryIdColumn() +" = "+categoryID);
        }
        Log.d("getAccWithSpecValInName","Exit the getAccountsThatContainsThisTextInName method in the AccountsDB class.");
        return listOfAccountsThatContainsThisTexInName;
    }//End of getAccountsWithSpecifcValue method

    //Method to return cursor with rows from the accounts table with specific value in the column name passed in as argument
    public Cursor getQuestionsWithThisTextInValue(String searchText){
        Log.d("getQuestWithSpecValue","Enter the getQuestionsWithThisTextInValue method in the AccountsDB class.");
        Cursor  listOfQuestionsThatContainsThisTexInValue = null;

        listOfQuestionsThatContainsThisTexInValue = this.runQuery("SELECT QUESTION._id, QUESTION.Value AS Q, ANSWER._id AS AnswerID,ANSWER.Value AS Answer," +
                "ANSWER.initVector AS initVector FROM QUESTION LEFT JOIN ANSWER ON QUESTION.AnswerID = ANSWER._id WHERE QUESTION.Value LIKE '%"+searchText+"%'");
        Log.d("getQuestWithSpecValue","Exit the getQuestionsWithThisTextInValue method in the AccountsDB class.");
        return listOfQuestionsThatContainsThisTexInValue;
    }//End of getAccountsWithSpecifcValue method

    public Cursor getAccountsThatContainsThisTextInNameAndIsFav(String searchText,int categoryID){
        Log.d("getAccWithSpecValInName","Enter the getAccountsThatContainsThisTextInName method in the AccountsDB class.");
        Cursor  listOfAccountsThatContainsThisTexInName = null;
        if(categoryID == -1){
            listOfAccountsThatContainsThisTexInName = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + MainActivity.getNameColumn() + " LIKE '%" + searchText + "%'");
        }else if(categoryID ==-2){
            listOfAccountsThatContainsThisTexInName = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + MainActivity.getNameColumn() + " LIKE '%" + searchText + "%'"
                    +" AND "+MainActivity.getIsFavoriteColumn() +"= 1" );
        }else{
            listOfAccountsThatContainsThisTexInName = runQuery("SELECT * FROM "+ MainActivity.getAccountsTable()+ " WHERE " + MainActivity.getNameColumn() + " LIKE '%" + searchText + "%'"
                    +" AND "+MainActivity.getCategoryIdColumn() +" = "+categoryID);
        }

        Log.d("getAccWithSpecValInName","Exit the getAccountsThatContainsThisTextInName method in the AccountsDB class.");
        return listOfAccountsThatContainsThisTexInName;
    }//End of getAccountsWithSpecifcValue method



    //Method to return rows from a table that meet multiple possible values
    private Cursor getRowsThatMeetMultipleValuesCriteria(String table, String column, ArrayList values){
        Log.d("rowThatMeetCriteria","Enter the getRowsThatMeetMultipleValuesCriteria method in the AccountsDB class.");
        //Declare and initialize variables to be used and return by the method
        Cursor cursor = null;
        String inClause = " IN (";
        //Iterate through values list to build the SQL statement closing section (i.e. IN (x,y,z))
        for(int i=0;i< values.size();i++){
            inClause += values.get(i);
            if(i!= values.size()-1){
                inClause += ",";
            }else{
                inClause += ")";
            }
        }//End of for loop
        //Run the query and retrieve the cursor with all the matching rows
        cursor = this.runQuery("SELECT * FROM "+ table +" WHERE "+ column + inClause);
        Log.d("rowThatMeetCriteria","Enter the getRowsThatMeetMultipleValuesCriteria method in the AccountsDB class.");
        return cursor;
    }//End of getRowsThatMeetMultipleValuesCriteria method

    public String getQuestionListColumnNameThatHoldsQuestion(int questionListID,int questionID){
        Log.d("questListColumnName","Enter the getQuestionListColumnNameThatHoldsQuestion method in the AccountsDB class.");
        //Declare and initialize variables to be used and return by the method
        String column = MainActivity.getQuestionIdColumn();
        //Extract the full question list object from cursor by passing in questionListID
        QuestionList questionList = this.getQuestionListById(questionListID);
        //Iterate through the question list
        int i =0;
        boolean found = false;
        while(i<= questionList.getSize() && !found){
            //Check if current question has the same id as the question being searched
            if(questionList.getQuestions().get(i).get_id() == questionID){
                column += (i+1);
                found = true;
            }
            //Increment iterator
            i++;
        }//End of while loop
        Log.d("questListColumnName","Enter the getQuestionListColumnNameThatHoldsQuestion method in the AccountsDB class.");
        //Return the QuestionID and append the iterator value which corresponds to the column number holding the searched question
        return column;
    }//End of getQuestionListColumnNameThatHoldsQuestion method

    public int re_sctructureQuestionList(QuestionList questionListUsingTheQuestionToBeDeleted, int questionID){
        Log.d("re_sctrctrQuestionList","Enter the re_sctructureQuestionList method in the AccountsDB class.");
        //Boolean flag to define the question list re-structure was successful and value to be returned by method
        boolean re_structuredListAlreadyExists = false;
        int initPosition = -1;
        final String questionID1 = MainActivity.getQuestionId1Column();
        final String questionID2 = MainActivity.getQuestionId2Column();
        final String questionID3 = MainActivity.getQuestionId3Column();
        ContentValues values;
        //Extract the full question list object from cursor by passing in questionListID
        int questionListID = questionListUsingTheQuestionToBeDeleted.get_id();

        String column = this.getQuestionListColumnNameThatHoldsQuestion(questionListID,questionID);
        //Check the column where question is being stored in DB
        if(column.equals(questionID1)){
            //Set QuestionID1 column to null
            initPosition = 0;
        }else if(column.equals(questionID2)){
            initPosition = 1;
        }else if(column.equals(questionID3)){
            initPosition = 2;
        }//End of if else statements that check the question position that is going to be removed, this will allow to re-structure the question list if isn't left empty
        //Before updating the questionList table it's necessary to see if the resulting questionList is not in the table already, in order to avoid duplication
        QuestionList newQuestionListAfterRemovingQuestion = new QuestionList();
        //Check what the new list size will be and base on that populate the newQuestionListAfterRemovingQuestion variable with the remainder questions
        switch (questionListUsingTheQuestionToBeDeleted.getSize()-1){
            case 1:
                int remainderQuestionPosition1 = -1;
                if(initPosition == 0){
                    remainderQuestionPosition1 = 1;
                }else{
                    remainderQuestionPosition1 = 0;
                }
                newQuestionListAfterRemovingQuestion.addQuestion(questionListUsingTheQuestionToBeDeleted.getQuestions().get(remainderQuestionPosition1));
                //newQuestionListAfterRemovingQuestion = this.getQuestionCursorByID((questionListUsingTheQuestionToBeDeleted.getQuestions().get(remainderQuestionPosition1).get_id()));
                break;
            case 2:
                int remainderQuestionPosition2 = -1;
                if(initPosition == 0){
                    remainderQuestionPosition1 = 1;
                    remainderQuestionPosition2 = 2;
                }else if(initPosition == 1){
                    remainderQuestionPosition1 = 0;
                    remainderQuestionPosition2 = 2;
                }else{
                    remainderQuestionPosition1 = 0;
                    remainderQuestionPosition2 = 1;
                }
                newQuestionListAfterRemovingQuestion.addQuestion(questionListUsingTheQuestionToBeDeleted.getQuestions().get(remainderQuestionPosition1));
                newQuestionListAfterRemovingQuestion.addQuestion(questionListUsingTheQuestionToBeDeleted.getQuestions().get(remainderQuestionPosition2));
                break;
            default:
                break;
        }//End of switch statement that populates newQuestionListAfterRemovingQuestion with remainder questions

        //Check is the modified question list already exists on the DB
        int re_structuredQuestionListID = this.getSecQuestionListID(newQuestionListAfterRemovingQuestion);
        if( re_structuredQuestionListID == -1){
            //If the list id returns -1, means the resulting list doesn't exist in the DB, therefore updating the current list ID will do
            values = this.moveValuesBetweenColumns(initPosition,questionListUsingTheQuestionToBeDeleted);
            //Store the _id attribute of the question list to be updated
            values.put(MainActivity.getIdColumn(),questionListID);
            if(this.updateTable(MainActivity.getQuestionlistTable(),values)){
                //If the question list updated is successful, the id to be returned by method should be the ID from question list holding the question
                //to be deleted
                re_structuredQuestionListID = questionListUsingTheQuestionToBeDeleted.get_id();
            }
        }//End of if statement to  check the re-structured list isn't in the DB
        Log.d("re_sctrctrQuestionList","Exit the re_sctructureQuestionList method in the AccountsDB class.");
        return re_structuredQuestionListID;
    }//End of the re_sctructureQuestionList method

    //Method to assign new column name and question ids when a question is removed form the list
    private ContentValues moveValuesBetweenColumns(int initPosition, QuestionList questionListUsingTheQuestionToBeDeleted){
        Log.d("movValuesBetweenColumns","Enter the moveValuesBetweenColumns method in the AccountsDB class.");
        ContentValues values = new ContentValues();
        for(int i=initPosition;i < questionListUsingTheQuestionToBeDeleted.getSize();i++){
            if(i!=questionListUsingTheQuestionToBeDeleted.getSize()-1){
                values.put(MainActivity.getQuestionIdColumn()+(i+1),questionListUsingTheQuestionToBeDeleted.getQuestions().get(i+1).get_id());
            }else{
                values.put(MainActivity.getQuestionIdColumn()+(i+1),"(null)");
            }//End of if else statement to check the question size after removing one item
        }//End of for loop to iterate through the question list
        Log.d("movValuesBetweenColumns","Exit the moveValuesBetweenColumns method in the AccountsDB class.");
        return values;
    }//End of moveValuesBetweenColumns method

    public boolean deleteRowFromTable(String table, String column, int columnValue){
        Log.d("deleteRowFromTable","Enter deleteRowFromTable method in AccountsDB class.");
        boolean result = false;
        //Declare and instantiate a new database object to handle the database operations
        SQLiteDatabase db = getWritableDatabase();
        //Declare and initialize a query string
        String deleteFrom = "DELETE FROM ";
//        table= MainActivity.getQuestionassignmentTable();
        String whereClause =" WHERE "+ column +" = ";
        //Run SQL statement to delete the task with id x from the TASK table
        try{
            db.execSQL(deleteFrom + table + whereClause + columnValue);
            result = true;
            Log.d("deleteRowFromTable","Exit successfully deleteRowFromTable method in AccountsDB class.");
        }catch (Exception e) {
            Log.d("deleteRowFromTable","Exit the deleteRowFromTable method in the AccountsDB class with exception: "+e.getMessage());
        }finally{
            db.close();
            return result;
        }//End of try catch finally block
    }//End of deleteQuestionAssignment method

    //Method to return item position within cursor passed in as parameter
    public int findItemPositionInCursor(Cursor cursor, int _id){
        Log.d("findPositionInCursor","Enter the findItemPositionInCursor method in the AccountsDB class.");
        int itemPosition = -1;

        if(cursor!=null){
            cursor.moveToFirst();
        }
        boolean found = false;
        do{
            if(cursor.getInt(0) == _id){
                itemPosition = cursor.getPosition();
                found = true;
            }//Endo of if else statement to check the _id
        }while(cursor.moveToNext() && !found);
        Log.d("findPositionInCursor","Exit the findItemPositionInCursor method in the AccountsDB class.");
        return itemPosition;
    }//End of findItemPositionInCursor method

    //Method used to break a string down into multiple pieces when to allow the apostrophe to be part of the string
    public String includeApostropheEscapeChar(String text){
        Log.d("ApostEscCharString","Enter includeApostropheEscapeChar method in AccountsDB class.");
        String textWithEscChar = "";
        //Iterate through the string to find the apostrophe and replace it with double apostrophe
        for(int i=0;i<text.length();i++){
            char c  = text.charAt(i);
            if(c == apostropheChar){
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
