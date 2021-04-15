package io.github.jlrods.mypsswrdsecure;

import android.database.Cursor;
import android.util.Log;
import java.util.ArrayList;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;
import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class Account extends Loggin {
    // Attribute definition

    private Category category; // Account type
    private QuestionList questionList; // List of Question objects (up to 3 questions stored in DB)
    private Icon icon; // Icon object to store URI icon location
    private boolean isFavorite; // Boolean attribute to identify the account as Favorite or not
    private long dateCreated;
    private long dateChange;
    //Method definition

    //Constructors
    public Account(int _id, String name, UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, Icon icon, boolean isFavorite, long dateCreated,long dateChange){
        super(_id, name, userName, psswrd);
        Log.d("AcctFull_Ent","Enter Account Full Constructor");
        this.category = category;
        this.questionList = questionList;
        this.icon = icon;
        this.isFavorite = isFavorite;
        this.dateCreated = dateCreated;
        this.dateChange = dateChange;
        Log.d("AcctFull_Ext","Exit Account Full Constructor");
    }
    public Account(int _id, String name, UserName userName, Psswrd psswrd, Category category,
                    QuestionList questionList, Icon icon, boolean isFavorite, long dateChange){
        super(_id, name, userName, psswrd);
        Log.d("AcctFull_Ent","Enter Account Full Constructor");
        this.category = category;
        this.questionList = questionList;
        this.icon = icon;
        this.isFavorite = isFavorite;
        this.dateCreated = System.currentTimeMillis();
        this.dateChange = dateChange;
        Log.d("AcctFull_Ext","Exit Account Full Constructor");
    }

    public Account(String name, UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, Icon icon, boolean isFavorite, long dateChange){
        this(-1,name,userName,psswrd,category,questionList,icon,isFavorite,dateChange);
        Log.d("AcctCosnt8Args","Exit Account Constructor for 8 aguments");
    }

    public Account(String name, UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, Icon icon, boolean isFavorite){
        this(-1,name,userName,psswrd,category,questionList,icon,isFavorite,0);
        Log.d("AcctCosnt7Args","Exit Account Constructor for 7 aguments");
    }

    public Account(){
        this(0,"",null,null,null,null,null,false,0);
        Log.d("AcctCosntNoArgs","Account Default Constructor called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category){
        this(_id,name,userName, psswrd,category,null,null,false,0);
        Log.d("Acct2_Ext","Account Constructor2 called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category,long dateChange){
        this(_id, name,userName, psswrd,category,null,null,false,dateChange);
        Log.d("Acct2.1_Ext","Account Constructor2.1 called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, boolean isFavorite){
        this(_id, name, userName, psswrd,category,questionList,null,isFavorite,0);
        Log.d("Acct3_Ext","Account Constructor3 called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, boolean isFavorite, long dateChange){
        this(_id, name, userName, psswrd, category, questionList,null, isFavorite,dateChange);
        Log.d("Acct3.1_Ext","Account Constructor3.1 called Full Account Constructor");
    }


    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, Icon icon){
        this(_id, name, userName, psswrd,category,questionList,icon,false,0);
        Log.d("Acct4_Ext","Account Constructor4 called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category,
                   QuestionList questionList, Icon icon, long dateChange){
        this(_id, name,userName, psswrd,category,questionList,icon,false,dateChange);
        Log.d("Acct4.1_Ext","Account Constructor4.1 called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category, Icon icon,
                   boolean isFavorite){
        this(_id, name,userName, psswrd,category,null,icon,isFavorite,0);
        Log.d("Acct5_Ext","Account Constructor5 called Full Account Constructor");
    }

    public Account(int _id, String name,UserName userName, Psswrd psswrd, Category category, Icon icon,
                   boolean isFavorite, long dateChange){
        this(_id, name,userName, psswrd,category,null,icon,isFavorite,dateChange);
        Log.d("Acct5.1_Ext","Account Constructor5.1 called Full Account Constructor");
    }

    //Getter and setter methods

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public QuestionList getQuestionList() {
        return questionList;
    }

    public void setQuestionList(QuestionList questionList) {
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

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateChange() {
        return dateChange;
    }

    public void setDateChange(long dateChange) {
        this.dateChange = dateChange;
    }

    //Other methods
    //Method to extract a Account from a cursor object
   public static Account extractAccount(Cursor c){
       Log.d("Ent_ExtractAccount","Enter extractAccount method in the Account class.");
       //Declare and initialize a db manager class to run sql queries
       AccountsDB accountsDB = LoginActivity.getAccountsDB();
       //Declare and initialize a null category object, the one to be returned by the method
       Account account = null;
       //Declare and initialize objects required to create a new account object
       UserName userName = null;
       Psswrd psswrd = null;
       Category category = null;
       QuestionList questionList = null;
       Icon icon = null;
       //Declare and initialize variable to retrieve account data from the cursor
       int catID;
       int questionListID;
       int iconID;
       boolean isFavorite;
       long dateCreated;
       long dateChange;
       //Call common method to extract basic StringValue object data from a cursor
       ArrayList<Object> attributes = Loggin.extractLoggin(c);
       //Get userName object by passing in its DB _id
       userName = accountsDB.getUserNameByID((int) attributes.get(2));
       //Get psswrd object by passing in its DB _id
       psswrd = accountsDB.getPsswrdByID((int) attributes.get(3));
       catID = c.getInt(2);
       //Get category object by passing in its DB _id
       category = MainActivity.getCategoryByID(catID);
       questionListID = c.getInt(5);
       if(questionListID > 0){
           questionList = accountsDB.getQuestionListById(questionListID);
       }
       iconID = c.getInt(6);
       //Get icon object by passing in its DB _id
       icon = accountsDB.getIconByID(iconID);
       if(icon == null){
           icon = MainActivity.getMyPsswrdSecureLogo();
       }
       isFavorite = AccountsDB.toBoolean(c.getInt(7));
       //Get date created
       dateCreated = c.getLong(8);
       //Get change password date
       dateChange = c.getLong(9);
       //Call account constructor based on the data extracted from cursor
       if(dateCreated == 0 && dateChange == 0){
           //Create new account object with data extracted from cursor and objects created to make the account object
           account = new Account((int) attributes.get(0),(String) attributes.get(1),userName, psswrd,category,questionList,icon,isFavorite,dateChange);
       }else{
           //Create new account object with data extracted from cursor and objects created to make the account object
           account = new Account((int) attributes.get(0),(String) attributes.get(1),userName, psswrd,category,questionList,icon,isFavorite,dateCreated,dateChange);
       }

       Log.d("Ext_ExtractAccount","Exit extractAccount method in the Account class.");
       //Return the category object
       return account;
    }//End of extractCategory method
}// End of Account class
