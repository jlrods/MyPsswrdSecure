package io.github.jlrods.mypsswrdsecure;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private CoordinatorLayout coordinatorLayout;

    //Declare and initialize variables to define the current app state saved on DB
    private Category currentCategory = null;
    private int currentTab = 0;
    private boolean showAllAccounts = true;
    private boolean isFavoriteFilter = false;
    private boolean isSearchFilter = false;
    private String lastSearchText ="";
    private int counter=0;
    private byte[] encrypted=null;
    private String decrypted=null;
    //private RecyclerView rv = null;
    //private AccountsDB accounts = null;

    //private RecyclerView recyclerView = null;
    //private RecyclerView.LayoutManager layoutManager;
    private TabLayout tabLayout = null;
    private int idRes;
    private static Icon myPsswrdSecureLogo = null;
    private static AccountsDB accountsDB = null;
    private static ArrayList<Category> categoryList = null;
    private static ArrayList<QuestionList> listOfQuestionLists = null;


    private static String dateFormat = "dd/MMM/yyyy";



    private static String RESOURCES = "Resources";

    private static Cryptographer cryptographer;

    private int throwAddAccountActReqCode = 5566;
    private static int throwAddQuestionActReqCode = 9876;
    private int throwAddUserNameActReqCode = 5744;
    private int throwAddPsswrdActReqCode = 9732;
    private int throwEditUserNameActReqCode = 4475;
    private int throwEditPsswrdActReqCode = 6542;
    private int throwEditQuestionActReqCode = 2456;
    private static int throwEditAccountActReqCode = 1199;

     //DB Table names
    private static final String USERNAME_TABLE = "USERNAME";
    private static final String PSSWRD_TABLE = "PSSWRD";
    private static final String QUESTION_TABLE = "QUESTION";
    private static final String ANSWER_TABLE = "ANSWER";
    private static final String QUESTIONLIST_TABLE = "QUESTIONLIST";
    private static final String QUESTIONASSIGNMENT_TABLE = "QUESTIONASSIGNMENT";
    private static final String ICON_TABLE = "ICON";
    private static final String APPLOGGIN_TABLE = "APPLOGGIN";
    private static final String CATEGORY_TABLE = "CATEGORY";
    private static final String APPSTATE_TABLE = "APPSTATE";
    private static final String ACCOUNTS_TABLE = "ACCOUNTS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Call the correct activity based on tab selection
                switch(tabLayout.getSelectedTabPosition()){
                    case 0:
                        //Call method to throw the AddAccount Activity
                        throwAddAccountActivity();
                        break;
                    case 1:
                        //Call method to throw the AddUserName Activity
                        throwAddUserNameActivity();
                        break;
                    case 2:
                        //Call method to throw the AddPsswrd Activity
                        throwAddPsswrdActivity();
                        break;
                    default:
                        //Call method to throw the AddQuestion Activity
                        throwAddQuestionActivity();
                        break;

                }//End of switch statement to check current tab selection


                //Declare and instantiate a new intent object
                //Intent i= new Intent(MainActivity.this,SelectLogoActivity.class);
                //Add extras to the intent object, specifically the current category where the add button was pressed from
                //Start the addTaskActivity class
                //startActivity(i);

            }//End of on click method implementation
        });//End of set on click listener method
        Resources r = getResources();
        this.idRes = r.getIdentifier("logo_google","drawable",getPackageName());
        if(idRes ==0) {

        }
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //RecyclerView rv = null;
                // accounts = null;
                //Cursor cursor = null;
                //rv = HomeFragment.getRv();
                //accounts = HomeFragment.getAccounts();
                switch(tab.getPosition()){
                    case 0:
                        //Consider the category selected on drawer menu to run correct sql query
                        AccountAdapter accountAdapter = new AccountAdapter(getBaseContext(),null);
                        //Setup the onclick event listener
                        accountAdapter.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                throwEditAccountActivity(v);
                            }//End of onClick method
                        });//End of setOnClickListener
                        accountAdapter.setStarImgOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toggleIsFavorite(v);
                            }
                        });
                        MainActivity.updateRecyclerViewData(accountAdapter);
                        break;
                    case 1:
                        //Create rv adapter for the user name tab
                        UserNameAdapter userNameAdapter = new UserNameAdapter(getBaseContext(),null);
                        //Setup the onclick event listener
                        userNameAdapter.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                throwEditUserNameActivity(v);
                            }//End of onClick method
                        });//End of setOnClickListener
                        MainActivity.updateRecyclerViewData(userNameAdapter);
                        break;
                    case 2:
                        PsswrdAdapter psswrdAdapter = new PsswrdAdapter(getBaseContext(),null);
                        //Setup the onclick event listener
                        psswrdAdapter.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                throwEditPsswrdActivity(v);
                            }//End of onClick method
                        });//End of setOnClickListener
                        MainActivity.updateRecyclerViewData(psswrdAdapter);
                        break;
                    case 3:
                        SecurityQuestionAdapter secQuestionAdapter = new SecurityQuestionAdapter(getBaseContext(),null);
                        MainActivity.updateRecyclerViewData(secQuestionAdapter);
                        secQuestionAdapter.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                throwEditQuestionActivity(v);
                            }
                        });//End of setOnClickListener
                        break;
                }// End of switch statement
                currentTab = tab.getPosition();
                boolean appStateUpdated = accountsDB.updateAppState(-1,tab.getPosition(), accountsDB.toInt(showAllAccounts) , accountsDB.toInt(isFavoriteFilter), accountsDB.toInt(isSearchFilter),"HelloWorld'BaBay");

                if(appStateUpdated){
                    Cursor c = accountsDB.runQuery("SELECT * FROM APPSTATE");
                    c.moveToFirst();
                    int cat = c.getInt(1);
                    int tabP = c.getInt(2);
                    int showAll = c.getInt(3);
                    int isFab = c.getInt(4);
                    int isSearch = c.getInt(5);
                    String search = c.getString(6);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, search, Snackbar.LENGTH_LONG);
                    snackbar.setAction("Action", null).show();
                }else{
                    //@FIXME: Define action in case the app fail to update state
                }//End of if else statement
            }//End of onTabSelected

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });// End of addOnTabSelectedListener method for tabLayout

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //Create and set default logo for accounts
        myPsswrdSecureLogo = new Icon(R.mipmap.ic_my_psswrd_secure,"MyPsswrdSecureIcon",String.valueOf(R.mipmap.ic_my_psswrd_secure),false);
        cryptographer = new Cryptographer();
        //Dummy encryption to get IV created
        byte[] testEncrypted = cryptographer.encryptText("DummyEncryption");
        String test2 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
//        byte[] joseleoEncrypt = cryptographer.encryptText("joseleo");
//        String joseleoDecrypt = cryptographer.decryptText(joseleoEncrypt, cryptographer.getIv());
        accountsDB = new AccountsDB(this);

//        String test3 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
        //Save it IV in the APPSTATE Table
//        ContentValues appStateValues = new ContentValues();
//        String test4 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
//        appStateValues.put("_id",accounts.getMaxItemIdInTable("APPSTATE"));
//        String test5 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
//        String joseleo2 = cryptographer.decryptText(joseleoEncrypt,cryptographer.getIv());
//        IvParameterSpec ivFromCrypt = cryptographer.getIv();
//        String test6 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
//        byte [] initVector = cryptographer.getIv().getIV();
//        appStateValues.put("initVector",initVector);
//        accounts.updateTable("APPSTATE",appStateValues);
//        String test9 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
       //accounts.getWritableDatabase().update("APPSTATE",appStateValues,"_id = 1",null);

//        Cursor c = accounts.runQuery("SELECT * FROM APPSTATE WHERE _id = "+appStateValues.getAsInteger("_id"));
//        c.moveToNext();
//        int appID = c.getInt(0);
//        int abc = c.getInt(2);
//        int abcd = c.getInt(3);
//        int abcde = c.getInt(4);
//        int abcdef = c.getInt(5);
//        String abcdefg = c.getString(6);
//        byte[] iv = c.getBlob(7);
//        IvParameterSpec a = new IvParameterSpec(iv);
//        String test = cryptographer.decryptText(testEncrypted,a);
//        String test1 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());


        this.categoryList = accountsDB.getCategoryList();
        this.currentCategory = categoryList.get(0);
        //this.listOfQuestionLists = accounts.getListOfQuestionLists();

        //Consider the category selected on drawer menu to run correct sql query
        //Cursor accountListCursor = accountsDB.getAccountsList();

    }//End of onCreate method


    public void testRVLogo(){
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, SelectLogoActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //Start the addTaskActivity class
        startActivity(i);
    }

    private void testCriptogrpher(){
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
        counter++;
        if(counter<=1){
            encrypted =cryptographer.encryptText("jlrods@gmail.com");
            try {
                Toast.makeText(MainActivity.this, new String(encrypted,"UTF8"), Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
//                    encrypted =cryptographer.encryptText("jlrods@gmail.com");
//                    Snackbar snackbar = Snackbar.make(view, new String(encrypted), Snackbar.LENGTH_LONG);
//                    snackbar.setAction("Action", null).show();
            //Toast.makeText(MainActivity.this, new String(encrypted), Toast.LENGTH_LONG).show();
        }else{
            decrypted = cryptographer.decryptText(encrypted,cryptographer.getIv());
            Toast.makeText(MainActivity.this, decrypted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public TabLayout.Tab getCurrentTab(){
        return this.tabLayout.getTabAt(this.tabLayout.getSelectedTabPosition());
    }



    //Method to return a category by passing in its DB id
    public static Category getCategoryByID(int _id){
        Log.d("getCatByID","Enter the getCategoryByID method in the MainActivity class.");
        Category category = null;
        boolean found = false;
        int i =0;
        while(i<categoryList.size() && !found){
            if(categoryList.get(i).get_id() == _id){
                category = categoryList.get(i);
                found = true;
                Log.d("getCatByID","A category with ID " +_id+ " has been found in the categoryList object in MainActivity class.");
            }
            i++;
        }// End of while loop
        Log.d("getCatByID","Exit the getCategoryByID method in the MainActivity class.");
        return category;
    }// End of getCategoryID method

    public static void updateRecyclerViewData(RecyclerView.Adapter adapter){
        Log.d("Ent_updateRecViewData","Enter the updateRecyclerViewData method in the MainActivity class.");
        RecyclerView rv = HomeFragment.getRv();
        AccountsDB accountsDB = HomeFragment.getAccountsDB();
        Cursor cursor = null;
        //Check the class of the adapter passed in as argument
        if(adapter instanceof AccountAdapter){
            cursor = accountsDB.getAccountsList();
            ((AccountAdapter) adapter).setCursor(cursor);
        }else if(adapter instanceof PsswrdAdapter){
            cursor = accountsDB.getPsswrdList();
            ((PsswrdAdapter) adapter).setCursor(cursor);
        }else if(adapter instanceof SecurityQuestionAdapter){
            cursor = accountsDB.getListQuestionsAvailableNoAnsw();
            ((SecurityQuestionAdapter) adapter).setCursor(cursor);
        }else if(adapter instanceof UserNameAdapter){
            cursor = accountsDB.getUserNameList();
            ((UserNameAdapter) adapter).setCursor(cursor);
        }
        //Move to first row of cursor if not empty
        if (cursor != null){
            cursor.moveToFirst();
        }
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d("Ext_updateRecViewData","Exit the updateRecyclerViewData method in the MainActivity class.");
    }//End of updateRecyclerViewData method

    //Method to throw new AddTaskActivity
    private void throwAddAccountActivity(){
        Log.d("ThrowAddAcc","Enter throwAddAccountActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,AddAccountActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("category",this.currentCategory.toString());
        //i.putExtra("sql",this.getSQLForRecyclerView());
        //Start the addTaskActivity class
        startActivityForResult(i,throwAddAccountActReqCode);
        Log.d("ThrowAddAcc","Exit throwAddAccountActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to throw new AddTaskActivity
    private void throwAddUserNameActivity(){
        Log.d("ThrowAddUser","Enter throwAddUserNameActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, AddUserNameActivity.class );
        //Start the AddItemActivity class
        startActivityForResult(i,throwAddUserNameActReqCode);
        Log.d("ThrowAddUser","Exit throwAddUserNameActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    private void throwAddPsswrdActivity(){
        Log.d("ThrowAddPsswrd","Enter throwAddPsswrdActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, AddPsswrdActivity.class );
        //Start the AddItemActivity class
        startActivityForResult(i,throwAddPsswrdActReqCode);
        Log.d("ThrowAddPsswrd","Exit throwAddPsswrdActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    private void throwAddQuestionActivity(){
        Log.d("ThrowAddQuest","Enter throwAddQuestionActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,AddQuestionActivity.class);
        //Start the addTaskActivity class
        startActivityForResult(i,throwAddQuestionActReqCode);
        Log.d("ThrowAddQuest","Exit throwAddQuestionActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to throw new AddTaskActivity
    private void throwEditAccountActivity(View v){
        Log.d("ThrowEditAcc","Enter throwEditAccountActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((AccountAdapter)rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        Account account = Account.extractAccount(cursor);
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, EditAccountActivity.class );
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //i.putExtra("category",this.currentCategory.toString());
        i.putExtra("_id",account.get_id());
        //Start the AddItemActivity class
        startActivityForResult(i,throwEditAccountActReqCode);
        Log.d("ThrowEditAcc","Exit throwEditAccountActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to throw new AddTaskActivity
    private void throwEditUserNameActivity(View v){
        Log.d("ThrowEditUser","Enter throwEditUserNameActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((UserNameAdapter)rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        UserName userName = UserName.extractUserName(cursor);
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, EditUserNameActivity.class );
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //i.putExtra("category",this.currentCategory.toString());
        i.putExtra("_id",userName.get_id());
        //Start the AddItemActivity class
        startActivityForResult(i,this.throwEditUserNameActReqCode);
        Log.d("ThrowEditUser","Exit throwEditUserNameActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to throw new EditPsswrdActivity
    private void throwEditPsswrdActivity(View v){
        Log.d("ThrowEditPss","Enter throwEditPsswrdActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((PsswrdAdapter)rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        Psswrd psswrd = Psswrd.extractPsswrd(cursor);
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, EditPsswrdActivity.class );
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //i.putExtra("category",this.currentCategory.toString());
        i.putExtra("_id",psswrd.get_id());
        //Start the AddItemActivity class
        startActivityForResult(i,this.throwEditPsswrdActReqCode);
        Log.d("ThrowAddUser","Exit throwEditPsswrdActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to throw new EditQuestionActivity
    private void throwEditQuestionActivity(View v){
        Log.d("ThrowEditPss","Enter throwEditQuestionActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((SecurityQuestionAdapter)rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        Question question = Question.extractQuestion(cursor);
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this, EditQuestionActivity.class );
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        //i.putExtra("category",this.currentCategory.toString());
        i.putExtra("_id",question.get_id());
        //Start the AddItemActivity class
        startActivityForResult(i,this.throwEditQuestionActReqCode);
        Log.d("ThrowAddUser","Exit throwEditQuestionActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","Enter the onActivityResult method in the DisplayAccountActivity class.");
        //Check if result comes from AddAccountActivity
        String toastText = "";
        //Flag to display Toast and update RV
        boolean goodResultDelivered = false;
        RecyclerView recyclerView = HomeFragment.getRv();
        RecyclerView.Adapter adapter = null;
        if (requestCode ==this.throwAddAccountActReqCode && resultCode == RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from AddAccountActivity (received by MainActivity).");
            //Update RV data set
            //Consider the category selected on drawer menu to run correct sql query
            //@FIXME: Investigate--> What's best option? notify adapter about data set change or set up new adapter with method created??
            //AccountAdapter accountAdapter = new AccountAdapter(getBaseContext(),null);
            //updateRecyclerViewData(accountAdapter);
            ((AccountAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getAccountsList());
            //Define text to display Toast to confirm the account has been added
            //Set variable to display Toast
            goodResultDelivered = true;
            toastText = data.getExtras().getString("accountName") + " " + getResources().getString(R.string.accountAdded);
        }else if(requestCode ==this.throwAddAccountActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddAccountActivity (received by MainActivity).");
            //Check if result comes from AddAccountActivity
        }else if(requestCode == throwAddUserNameActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddUserNameActivity (received by MainActivity).");
            ((UserNameAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getUserNameList());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.userNameAdded);
        }else if(requestCode == throwAddUserNameActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddUserNameActivity (received by MainActivity).");
        }else if(requestCode == throwAddPsswrdActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddPsswrdActivity (received by MainActivity).");
            ((PsswrdAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getPsswrdList());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.psswrdAdded);
        }else if(requestCode == throwAddPsswrdActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddPsswrdActivity (received by MainActivity).");
        }else if(requestCode == throwAddQuestionActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddAccountActivity (received by MainActivity).");
            ((SecurityQuestionAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getListQuestionsAvailableNoAnsw());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.questionAdded);
        }else if(requestCode == throwAddQuestionActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddQuestionActivity (received by MainActivity).");
        }else if(requestCode == throwEditUserNameActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditUserNameActivity (received by MainActivity).");
            ((UserNameAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getUserNameList());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.userNameUpdated);
        }else if(requestCode == throwEditUserNameActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditUserNameActivity (received by MainActivity).");
        }else if(requestCode == throwEditPsswrdActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditUserNameActivity (received by MainActivity).");
            ((PsswrdAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getPsswrdList());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.psswrdUpdated);
        }else if(requestCode == throwEditPsswrdActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditUserNameActivity (received by MainActivity).");
        }else if(requestCode == throwEditQuestionActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditQuestionActivity (received by MainActivity).");
            ((SecurityQuestionAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getListQuestionsAvailableNoAnsw());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.questionUpdated);
        }else if(requestCode == throwEditQuestionActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditQuestionActivity (received by MainActivity).");
        }else if (requestCode == throwEditAccountActReqCode && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from EditAccountActivity (received by HomeFragment).");
            ((AccountAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getAccountsList());
            //Define text to display Toast to confirm the account has been added
            //Set variable to display Toast
            goodResultDelivered = true;
            toastText = data.getExtras().getString("accountName") + " " + getResources().getString(R.string.accountUpdated);
        }else if(requestCode == throwEditAccountActReqCode && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditAccountActivity (received by HomeFragment).");
        }//End of if else statement chain to check activity results

        //Check if toast would be displayed
        if(goodResultDelivered){
            recyclerView.getAdapter().notifyDataSetChanged();
            //Move to new account position
            //Display Toast to confirm the account has been added
            displayToast(this,toastText,Toast.LENGTH_LONG, Gravity.CENTER);
        }//End of if statement to check good result was delivered
        //End of if else statement to check the data comes from one of the thrown activities
        Log.d("onActivityResult","Exit the onActivityResult method in the DisplayAccountActivity class.");
    }//End of onActivityResult method


    //Method to display a generic new Dialog Alert view from any activity.
    public static AlertDialog.Builder displayAlertDialog(Context context, EditText inputField,String title, String message, String hint){
        Log.d("displayAlertDialog","Enter displayAlertDialog method in the MainActivity class.");
        //final EditText inputField = new EditText(context);
        inputField.setText("");
        inputField.setHint(hint);
        Log.d("displayAlertDialog","Enter displayAlertDialog method in the MainActivity class.");
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(inputField)
                .setNegativeButton(R.string.cancel,null);
    }//End of displayAlertDialog

    public static Cryptographer getCryptographer(){
        return cryptographer;
    }
    public static String getDateFormat(){
        return dateFormat;
    }

    public static String getRESOURCES() {
        return RESOURCES;
    }

    public static Icon getMyPsswrdSecureLogo() {
        return myPsswrdSecureLogo;
    }

    public static ArrayList<QuestionList> getListOfQuestionLists(){
        return listOfQuestionLists;
    }

    public static int getThrowAddQuestionActReqCode() {
        return throwAddQuestionActReqCode;
    }

    public static int getThrowEditAccountActReqCode() {
        return throwEditAccountActReqCode;
    }

    public int getThrowEditUserNameActReqCode() {
        return throwEditUserNameActReqCode;
    }

    public static String getUsernameTable() {
        return USERNAME_TABLE;
    }

    public static String getPsswrdTable() {
        return PSSWRD_TABLE;
    }

    public static String getQuestionTable() {
        return QUESTION_TABLE;
    }

    public static String getAnswerTable() {
        return ANSWER_TABLE;
    }

    public static String getQuestionlistTable() {
        return QUESTIONLIST_TABLE;
    }

    public static String getQuestionassignmentTable() {
        return QUESTIONASSIGNMENT_TABLE;
    }

    public static String getIconTable() {
        return ICON_TABLE;
    }

    public static String getApplogginTable() {
        return APPLOGGIN_TABLE;
    }

    public static String getCategoryTable() {
        return CATEGORY_TABLE;
    }

    public static String getAppstateTable() {
        return APPSTATE_TABLE;
    }

    public static String getAccountsTable() {
        return ACCOUNTS_TABLE;
    }

    public static void displayToast(Context context, String text, int toastLength, int gravity){
        Log.d("displayToast","Enter displayToast method in the MainActivity class.");
        Toast toast = Toast.makeText(context,text,toastLength);
        toast.setGravity(gravity,0,0);
        toast.show();
        Log.d("displayToast","Exit displayToast method in the MainActivity class.");
    }//End of displayToast method

    //Method to set account logo resource image
    public static void setAccountLogoImage(ImageView imgLogo, Context context, String iconResName){
        //Extract all the logos from the app resources
        int idRes;
        Resources r = context.getResources();
        idRes = r.getIdentifier(iconResName,"drawable",context.getPackageName());
        imgLogo.setImageResource(idRes);
    }

    //Method to be setup within OnClick event listener for the star icon within each Account item
    public static boolean toggleIsFavorite(View v){
        boolean update = false;
        RecyclerView recyclerView = HomeFragment.getRv();
        AccountAdapter accountAdapter = (AccountAdapter) recyclerView.getAdapter();
        Cursor cursor = accountAdapter.getCursor();
        //Find the position of parent recyclerview item in the adapter and store it in an int variable
        int adapterPosition = recyclerView.getChildAdapterPosition((View) v.getParent().getParent());
        //Move the cursor to the Account position in the adapter
        cursor.moveToPosition(adapterPosition);
        //Extract the account object from the cursor row
        Account account = Account.extractAccount(cursor);
        //Update the isFav attribute in the account object
        if(account.isFavorite()){
            account.setFavorite(false);
        }else{
            account.setFavorite(true);
        }
        //accountAdapter.updateItemIsFavorite(adapterPosition,account.isFavorite());
        //Call DB method to update the account item in the Accounts table
        ContentValues values = new ContentValues();
        values.put("_id",account.get_id());
        values.put("isFavorite",account.isFavorite());
        if(accountsDB.updateTable(ACCOUNTS_TABLE,values)){
            //If DB update was successful, call method to update the recyclerview
            updateRecyclerViewData(accountAdapter);
            recyclerView.scrollToPosition(adapterPosition);
            update = true;
        }else{
            //Prompt the user about DB problem
            //MainActivity.displayToast(this.getBaseContext(),"DB Error",Toast.LENGTH_SHORT,Gravity.CENTER);
        }

    return update;
    }

}//End of MainActivity class.
