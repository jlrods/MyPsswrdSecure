package io.github.jlrods.mypsswrdsecure;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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
import androidx.core.app.ActivityCompat;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class  MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private CoordinatorLayout coordinatorLayout;

    //Declare and initialize variables to define the current app state saved on DB
    private static Category currentCategory = null;
    private static int currentTab = 0;
    private boolean showAllAccounts = true;
    private boolean isFavoriteFilter = false;
    private boolean isSearchFilter = false;
    private String lastSearchText ="";
    private int counter=0;
    private byte[] encrypted=null;
    private String decrypted=null;
    private TabLayout tabLayout = null;
    private int idRes;
    private static Icon myPsswrdSecureLogo = null;
    private static AccountsDB accountsDB = null;
    private static ArrayList<Category> categoryList = null;
    private static ArrayList<QuestionList> listOfQuestionLists = null;


    private static String dateFormat = "dd/MMM/yyyy";



    private static String RESOURCES = "Resources";

    private static Cryptographer cryptographer;

    private final static int INDEX_TO_GET_LAST_TASK_LIST_ITEM = 2;

    private final static int THROW_IMAGE_GALLERY_REQ_CODE = 1642;
    private final static int THROW_IMAGE_CAMERA_REQ_CODE = 2641;
    private static final int GALLERY_ACCESS_REQUEST = 5196;
    private static final int CAMERA_ACCESS_REQUEST = 3171;

    private int throwAddAccountActReqCode = 5566;
    private static int throwAddQuestionActReqCode = 9876;
    private static int throwAddUserNameActReqCode = 5744;
    private static int throwAddPsswrdActReqCode = 9732;
    private int throwAddCategoryReqCode = 5673;
    private int throwEditUserNameActReqCode = 4475;
    private int throwEditPsswrdActReqCode = 6542;
    private int throwEditQuestionActReqCode = 2456;
    private static int throwEditCategoryActReqCode = 2002;
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

    private static final String CATEGORY_ID_COLUMN ="CategoryID";
    private static final String USER_NAME_ID_COLUMN ="UserNameID";
    private static final String PSSWRD_ID_COLUMN ="PsswrdID";
    private static final String QUESTION_LIST_ID_COLUMN ="QuestionListID";
    private static final String QUESTION_ID_1_COLUMN= "QuestionID1";
    private static final String QUESTION_ID_2_COLUMN= "QuestionID2";
    private static final String QUESTION_ID_3_COLUMN= "QuestionID3";
    private static final String ICON_ID_COLUMN ="IconID";
    private static final String ID_COLUMN = "_id";
    private static final String IS_FAVORITE_COLUMN = "IsFavorite";

    private static Uri uriCameraImage = null;
    private static final String EXTERNAL_IMAGE_STORAGE_CLUE = "content://";

    private static final String USER_NAME = "user name";
    private static final String PASSWORD = "password";
    private static final String QUESTION = "question";
    private static final String QUESTION_LIST ="question list";

    private static Category homeCategory = null;
    private static Category favCategory = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Call super on create
        super.onCreate(savedInstanceState);
        //Set the main activity layout
        setContentView(R.layout.activity_main);
        //Get the coordinator layout off layout
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //Get the tool bar off layout
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
            }//End of on click method implementation
        });//End of set on click listener method
        //Get the tablayout from layout
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        //Set up the onclick behaviour for each tab in the tablayout object
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
                    Cursor c = accountsDB.runQuery("SELECT * FROM "+ APPSTATE_TABLE);
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

        //Create and set default logo for accounts
        myPsswrdSecureLogo = new Icon(R.mipmap.ic_my_psswrd_secure,"MyPsswrdSecureIcon",String.valueOf(R.mipmap.ic_my_psswrd_secure),false);
        cryptographer = new Cryptographer();
        //Dummy encryption to get IV created
        byte[] testEncrypted = cryptographer.encryptText("DummyEncryption");
        String test2 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
        //Create a new object to manage all DB interaction
        accountsDB = new AccountsDB(this);
        //Get the category list from DB
        this.homeCategory = new Category("Home",new Icon("Home",MainActivity.getRESOURCES(),R.drawable.home));
        this.favCategory = new Category(-2,"Favorites",new Icon("Favorites",MainActivity.getRESOURCES(),android.R.drawable.star_big_on));
        this.categoryList = accountsDB.getCategoryList();
        //Set the Home category as the default one
        this.currentCategory = categoryList.get(0);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setDrawerLayout(drawer)
//                .build();
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.mobile_navigation)
                .setDrawerLayout(drawer)
                .build();
        //Get the menu in the navigation view
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        this.setUpLowerCategoryMenu(navigationView.getMenu());
        this.updateNavMenu(navigationView.getMenu(),INDEX_TO_GET_LAST_TASK_LIST_ITEM);

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
            //Check current category variable to call method that retrieves proper account list
            if(MainActivity.getCurrentCategory().get_id() == -1){
                cursor = accountsDB.getAccountsList();
            }else if(MainActivity.getCurrentCategory().get_id() == -2){
                cursor = accountsDB.getAccountsWithSpecifcValue(MainActivity.getIsFavoriteColumn(),1);
            }else{
                cursor = accountsDB.getAccountsWithSpecifcValue(MainActivity.getCategoryIdColumn(),MainActivity.getCurrentCategory().get_id());
            }
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
        i.putExtra("category",this.currentCategory.get_id());
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

    private void throwAddCategoryActivity(){
        Log.d("ThrowAddCatt","Enter throwAddCategoryActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,AddCategoryAcitivity.class);
        //Start the addTaskActivity class
        startActivityForResult(i,throwAddCategoryReqCode);
        Log.d("ThrowAddCatt","Exit throwAddCategoryActivity method in the MainActivity class.");
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
        i.putExtra("category",this.currentCategory.get_id());
        i.putExtra(ID_COLUMN,account.get_id());
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
        i.putExtra(ID_COLUMN,userName.get_id());
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
        i.putExtra(ID_COLUMN,psswrd.get_id());
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
        i.putExtra(ID_COLUMN,question.get_id());
        //Start the AddItemActivity class
        startActivityForResult(i,this.throwEditQuestionActReqCode);
        Log.d("ThrowAddUser","Exit throwEditQuestionActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    private void throwEditCategoryActivity(int _id,int listPosition){
        Log.d("ThrowEditCat","Enter throwEditCategoryActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,EditCategoryActivity.class);
        i.putExtra(ID_COLUMN,_id);
        i.putExtra("positionInCatList",listPosition);
        //Start the addTaskActivity class
        startActivityForResult(i,throwEditCategoryActReqCode);
        Log.d("ThrowEditCat","Exit throwEditCategoryActivity method in the MainActivity class.");
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
        //Flag to handle nave drawer menu update when a category has been added, deleted or edited
        boolean categoryMenuUpdate = false;
        RecyclerView recyclerView = HomeFragment.getRv();
        RecyclerView.Adapter adapter = null;
        if (requestCode ==this.throwAddAccountActReqCode && resultCode == RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from AddAccountActivity (received by MainActivity).");
            //Update RV data set
            //Consider the category selected on drawer menu to run correct sql query
            //@FIXME: Investigate--> What's best option? notify adapter about data set change or set up new adapter with method created??
            //AccountAdapter accountAdapter = new AccountAdapter(getBaseContext(),null);
            //updateRecyclerViewData(accountAdapter);
            //((AccountAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getAccountsList());
            //adapter =  recyclerView.getAdapter();
            //Define text to display Toast to confirm the account has been added
            //Set variable to display Toast
            goodResultDelivered = true;
            toastText = data.getExtras().getString("accountName") + " " + getResources().getString(R.string.accountAdded);
        }else if(requestCode ==this.throwAddAccountActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddAccountActivity (received by MainActivity).");
            //Check if result comes from AddAccountActivity
        }else if(requestCode == throwAddUserNameActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddUserNameActivity (received by MainActivity).");
            //((UserNameAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getUserNameList());

            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.userNameAdded);
        }else if(requestCode == throwAddUserNameActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddUserNameActivity (received by MainActivity).");
        }else if(requestCode == throwAddPsswrdActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddPsswrdActivity (received by MainActivity).");
            //((PsswrdAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getPsswrdList());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.psswrdAdded);
        }else if(requestCode == throwAddPsswrdActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddPsswrdActivity (received by MainActivity).");
        }else if(requestCode == throwAddQuestionActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddAccountActivity (received by MainActivity).");
            //((SecurityQuestionAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getListQuestionsAvailableNoAnsw());
            //Set variable to display Toast
            goodResultDelivered = true;
            //Define text to display Toast to confirm the account has been added
            toastText = getResources().getString(R.string.questionAdded);
        }else if(requestCode == throwAddQuestionActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddQuestionActivity (received by MainActivity).");
        }else if(requestCode == throwEditUserNameActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditUserNameActivity (received by MainActivity).");
            //Define text to display Toast to confirm the account has been added
            if(data.getExtras().getBoolean("itemDeleted")){
                toastText = data.getExtras().getString("itemDeletedName") + " " + getResources().getString(R.string.userNameDeleted);
            }else{
                toastText = getResources().getString(R.string.userNameUpdated);
            }
            //((UserNameAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getUserNameList());
            //Set variable to display Toast
            goodResultDelivered = true;
        }else if(requestCode == throwEditUserNameActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditUserNameActivity (received by MainActivity).");
        }else if(requestCode == throwEditPsswrdActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditUserNameActivity (received by MainActivity).");
            //Define text to display Toast to confirm the account has been added
            if(data.getExtras().getBoolean("itemDeleted")){
                toastText = data.getExtras().getString("itemDeletedName") + " " + getResources().getString(R.string.psswrdDeleted);
            }else{
                toastText = getResources().getString(R.string.psswrdUpdated);
            }
            //((PsswrdAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getPsswrdList());
            //Set variable to display Toast
            goodResultDelivered = true;
        }else if(requestCode == throwEditPsswrdActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditUserNameActivity (received by MainActivity).");
        }else if(requestCode == throwEditQuestionActReqCode && resultCode == RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditQuestionActivity (received by MainActivity).");
            //Define text to display Toast to confirm the account has been added
            if(data.getExtras().getBoolean("itemDeleted")){
                toastText = data.getExtras().getString("itemDeletedName") + " " + getResources().getString(R.string.questionDeleted);
            }else{
                toastText = getResources().getString(R.string.questionUpdated);
            }
            //((SecurityQuestionAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getListQuestionsAvailableNoAnsw());
            //Set variable to display Toast
            goodResultDelivered = true;
        }else if(requestCode == throwEditQuestionActReqCode && resultCode == RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditQuestionActivity (received by MainActivity).");
        }else if (requestCode == throwEditAccountActReqCode && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from EditAccountActivity (received by HomeFragment).");
            //((AccountAdapter) recyclerView.getAdapter()).setCursor(accountsDB.getAccountsList());
            //Define text to display Toast to confirm the account has been added
            //Set variable to display Toast
            goodResultDelivered = true;
            if(data.getExtras().getInt("accountID") == -1){
                toastText = data.getExtras().getString("accountName") + " " + getResources().getString(R.string.accountDeleted);
            }else{
                toastText = data.getExtras().getString("accountName") + " " + getResources().getString(R.string.accountUpdated);
            }

        }else if(requestCode == throwEditAccountActReqCode && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditAccountActivity (received by HomeFragment).");
        }else if(requestCode == throwAddCategoryReqCode && resultCode == Activity.RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddCategoryActivity (received by HomeFragment).");
            goodResultDelivered = true;
            categoryMenuUpdate = true;
            toastText = data.getExtras().getString("categoryName") + " " + getResources().getString(R.string.catAdded);
        }else if(requestCode == throwAddCategoryReqCode && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddCategoryActivity received by MainAcitvity.");
        }else if(requestCode == throwEditCategoryActReqCode && resultCode == Activity.RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from EditCategoryActivity received by MainAcitvity.");
            goodResultDelivered = true;
            categoryMenuUpdate = true;
            toastText = data.getExtras().getString("categoryName") + " " + getResources().getString(R.string.catUpdated);
        }else if(requestCode == throwEditCategoryActReqCode && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditCategoryActivity received by MainAcitvity.");
        }//End of if else statement chain to check activity results

        if(categoryMenuUpdate){
            //Check if toast would be displayed
            if(goodResultDelivered){
                //Get the updated list of categories
                this.categoryList = this.accountsDB.getCategoryList();
                //Get the navigation view to access the menu object
                NavigationView navigationView = findViewById(R.id.nav_view);
                //Get the position number of the updated menu item, which is already fixed on EditCategoryActivity
                //for the AlartDialog numbering issue
                int positionInCatList = data.getExtras().getInt("positionInCatList");
                //Create category object pointing to category list position retrieved above
                Category updatedCategory = this.categoryList.get(positionInCatList);
                //Get the menu item in the same position as the one in the categor list
                MenuItem menuItem = navigationView.getMenu().getItem(positionInCatList);
                //@Fixme: this might cause problem with preloaded categories, as their name comes from the string resources, not form DB directly
                //Set up the proper name, as this might be updated on previous activity
                menuItem.setTitle(updatedCategory.getName());
                //Set up the proper icon for each category (icon data comes from DB), as this might be updated from previous activity
                idRes = this.getResources().getIdentifier(categoryList.get(positionInCatList).getIcon().getName(),"drawable",this.getPackageName());
                menuItem.setIcon(idRes);
                //Display Toast to confirm the account has been added
                displayToast(this,toastText,Toast.LENGTH_LONG, Gravity.CENTER);
            }//End of if statement to check good result was delivered
        }else{
            //Check if toast would be displayed
            if(goodResultDelivered){
                adapter = recyclerView.getAdapter();
                //recyclerView.getAdapter().notifyDataSetChanged();
                updateRecyclerViewData(adapter);
                //Move to new account position
                //Display Toast to confirm the account has been added
                displayToast(this,toastText,Toast.LENGTH_LONG, Gravity.CENTER);
            }//End of if statement to check good result was delivered
        }//End of if else statement that checks if nav drawer menu has to be updated
        //End of if else statement to check the data comes from one of the thrown activities
        Log.d("onActivityResult","Exit the onActivityResult method in the DisplayAccountActivity class.");
    }//End of onActivityResult method


    //Method to display a generic new Dialog Alert view from any activity.
    public static AlertDialog.Builder displayAlertDialogWithInput(Context context, EditText inputField, String title, String message, String hint){
        Log.d("displayAlertDialog","Enter displayAlertDialog method in the MainActivity class.");
        //final EditText inputField = new EditText(context);
        if(inputField != null && hint != null){
            inputField.setText("");
            inputField.setHint(hint);
        }
        Log.d("displayAlertDialog","Enter displayAlertDialog method in the MainActivity class.");
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(inputField)
                .setNegativeButton(R.string.cancel,null);
    }//End of displayAlertDialog

    //Method to display a generic new Dialog Alert view from any activity.
    public static AlertDialog.Builder displayAlertDialogNoInput(Context context, String title, String message){
        Log.d("displayAlertDialog","Enter displayAlertDialog method in the MainActivity class.");
        Log.d("displayAlertDialog","Enter displayAlertDialog method in the MainActivity class.");
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel,null);
    }//End of displayAlertDialog

    public static Cryptographer getCryptographer(){
        return cryptographer;
    }

    public static AccountsDB getAccountsDB() {
        return accountsDB;
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

    public static int getThrowAddUserNameActReqCode() {
        return throwAddUserNameActReqCode;
    }

    public static int getThrowAddPsswrdActReqCode() {
        return throwAddPsswrdActReqCode;
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

    public static String getUserNameIdColumn() {
        return USER_NAME_ID_COLUMN;
    }

    public static String getPsswrdIdColumn() {
        return PSSWRD_ID_COLUMN;
    }

    public static String getQuestionListIdColumn() {
        return QUESTION_LIST_ID_COLUMN;
    }

    public static String getCategoryIdColumn() {
        return CATEGORY_ID_COLUMN;
    }

    public static String getQuestionId1Column() {
        return QUESTION_ID_1_COLUMN;
    }

    public static String getQuestionId2Column() {
        return QUESTION_ID_2_COLUMN;
    }

    public static String getQuestionId3Column() {
        return QUESTION_ID_3_COLUMN;
    }

    public static int getThrowImageGalleryReqCode() {
        return THROW_IMAGE_GALLERY_REQ_CODE;
    }

    public static int getThrowImageCameraReqCode() {
        return THROW_IMAGE_CAMERA_REQ_CODE;
    }

    public static int getGalleryAccessRequest() {
        return GALLERY_ACCESS_REQUEST;
    }

    public static int getCameraAccessRequest() {
        return CAMERA_ACCESS_REQUEST;
    }

    public static Uri getUriCameraImage() {
        return uriCameraImage;
    }

    public static String getExternalImageStorageClue() {
        return EXTERNAL_IMAGE_STORAGE_CLUE;
    }

    public static String getUserName() {
        return USER_NAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static String getQUESTION() {
        return QUESTION;
    }

    public static String getQuestionList() {
        return QUESTION_LIST;
    }

    public static String getIconIdColumn() {
        return ICON_ID_COLUMN;
    }

    public static String getIdColumn() {
        return ID_COLUMN;
    }

    public static String getIsFavoriteColumn() {
        return IS_FAVORITE_COLUMN;
    }

    public static int getCurrentTabID(){
        return currentTab;
    }

    public static Category getCurrentCategory() {
        return currentCategory;
    }

    public static Category getHomeCategory() {
        return homeCategory;
    }

    public static Category getFavCategory() {
        return favCategory;
    }

    public static ArrayList<Category> getCategoryList() {
        return categoryList;
    }

    public static int getIndexToGetLastTaskListItem() {
        return INDEX_TO_GET_LAST_TASK_LIST_ITEM;
    }

    public static void displayToast(Context context, String text, int toastLength, int gravity){
        Log.d("displayToast","Enter displayToast method in the MainActivity class.");
        Toast toast = Toast.makeText(context,text,toastLength);
        toast.setGravity(gravity,0,0);
        toast.show();
        Log.d("displayToast","Exit displayToast method in the MainActivity class.");
    }//End of displayToast method

    //Method to set account logo resource image
    public static void setAccountLogoImageFromRes(ImageView imgLogo, Context context, String iconResName){
        //Extract all the logos from the app resources
        int idRes;
        Resources r = context.getResources();
        idRes = r.getIdentifier(iconResName,"drawable",context.getPackageName());
        imgLogo.setImageResource(idRes);
    }

    //Method to set account logo resource image
    public static void setAccountLogoImageFromGallery(ImageView imgLogo, String uri){
        //Extract all the logos from the app resources
        imgLogo.setImageURI(Uri.parse(uri));
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
        }//End of if else statement to check the item was updated
    return update;
    }//End of toggleIsFavorite method

    //Method to load ad picture from gallery app
    public static void loadPictureFromGallery(Intent intent) {
        Log.d("LoadGalPicture","Enter loadPictureFromGallery method in the MainActivity class.");
        //Declare a new intent
        //Intent intent;
        //Check SDK version
        if (Build.VERSION.SDK_INT < 19){
            //Log the current verison
            Log.i("Build.VERSION", "< 19");
            //Initialize the intent object and set it up for calling the Gallery app
            //intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            //startActivityForResult(intent, RESULT_PROFILE_IMAGE_GALLERY);
        } else {
            //Log the current version
            Log.i("Build.VERSION", ">= 19");
            //Initialize the intent object and set it up for calling the Gallery app
            //intent = new Intent();
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            //startActivityForResult(intent, RESULT_PROFILE_IMAGE_GALLERY);
        }//End of if else statement that checks the SDK version
        Log.d("LoadGalPicture","Exit loadPictureFromGallery method in the MainActivity class.");
    }//End of loadPicture method

    ////Method To take a picture via intent
    public static void loadPictureFromCamera(Intent intent, Activity activity) {
        Log.d("LoadCamPicture","Enter loadPictureFromCamera method in the MainActivity class.");
            //Declare and initialize a new Intent object to call camera app
//            intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            //Check the PackageManager is not null
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                uriCameraImage = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCameraImage);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //startActivityForResult(intent, RESULT_PROFILE_IMAGE_CAMERA);
            } else {
                MainActivity.displayToast(activity,"",Toast.LENGTH_LONG,Gravity.BOTTOM);
            }//End of if else statement
        Log.d("LoadCamPicture","Exit loadPictureFromCamera method in the MainActivity class.");
    }//End of loadPicture method

    //Method to display alert dialog to request permission for access rights
    public static void permissionRequest(final String permit,String justify,final int requestCode,final Activity activity) {
        Log.d("permissionRequest","Enter permissionRequest method in the MainActivity class.");
        //Check the permission request needs formal explanation
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permit)){
            //Display alert with justification about why permit is necessary
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.generalPermitRqst)
                    .setMessage(justify)
                    .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Call method to request permission
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permit}, requestCode);
                        }})
                    .show();
        } else {
            //Otherwise, proceed to request permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{permit}, requestCode);
        }//End of if else statement to check the permission request must be displayed
        Log.d("permissionRequest","Exit permissionRequest method in the MainActivity class.");
    }//End of permissionRequest method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    private void updateNavMenu(final Menu navMenu, int startPosition){
        Log.d("Ent_UpdateNaveMenu","Enter the updateNavMenu method in MainActivity class.");

        if(startPosition == INDEX_TO_GET_LAST_TASK_LIST_ITEM){
            //Set up onclick listeners for the first two items (home and favorites, which cannot be removed)
            navMenu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //Since the navigation item controls the Home menu item, it's necessary to overwrite it's behaviour and set the home item as selected
                    item.setChecked(true);
                    item.setCheckable(true);
                    MenuItem previousItem = navMenu.findItem(currentCategory.get_id());
                    //Set the previous menu item clicked on as the one as not selected
                    if(previousItem != null && previousItem.getItemId()!= item.getItemId()){
                        previousItem.setChecked(false);
                        previousItem.setCheckable(false);
                    }
                    //When home button is clicked, the transition to HomeFragment is controlled via navigation
                    //But the current category still need to be set to Home category
                    currentCategory = categoryList.get(0);
                    tabLayout.selectTab( tabLayout.getTabAt(0));
                    return false;
                }//End of onMenuItemClick method
            });//End of setOnMenuItemClickListener method call
            navMenu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //When home button is clicked, the transition to HomeFragment is controlled via navigation
                    //But the current category still need to be set to Favorites category
                    //Since the navigation item controls the Home menu item, it's necessary to overwrite it's behaviour and set the home item as selected
                    item.setChecked(true);
                    item.setCheckable(true);
                    MenuItem previousItem = navMenu.findItem(currentCategory.get_id());
                    //Set the previous menu item clicked on as the one as not selected
                    if(previousItem != null && previousItem.getItemId()!= item.getItemId()){
                        previousItem.setChecked(false);
                        previousItem.setCheckable(false);
                    }
                    //When home button is clicked, the transition to HomeFragment is controlled via navigation
                    //But the current category still need to be set to Home category
                    currentCategory = categoryList.get(1);
                    tabLayout.selectTab( tabLayout.getTabAt(0));
                    return false;
                }//End of onMenuItemClick method
            });//End of setOnMenuItemClickListener method call

        }
        //Declare and initialize variables to be used during method
        //int to store each menu item order in the menu
        int order =0;
        //Iterator. Starts at 2 because there are two menus already in the hard coded menu layout
        //startPosition = 2;
        //Get the nav controller to so HomeFragment navigation can be possible
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //Iterate through the category list (skipping first two categories: Home and Favorites) so each category menu item
        //can be added to Nav drawer menu
        while(startPosition < categoryList.size()){
            //set up new item's order in the menu
            order = navMenu.getItem(navMenu.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM).getOrder()+1;
            //Add the new item to the menu
            //Declare and instantiate an int to hold the string id from resources and a String variable to hold the actual category name
            int textID = getResources().getIdentifier(categoryList.get(startPosition).getName(),"string",getPackageName());
            String categoryName = "";
            //If textID is 0, means it's not stored in the app resources, which means it won't be translated but it will be displayed as saved on DB
            if(textID > 0){
                //If res id number exists, set the category name as per the string text, not the string ID
                categoryName = getResources().getString(textID);
            }else{
                //In the case of not being a resource, print the text retrieved from DB
                categoryName = categoryList.get(startPosition).getName();
            }//End of if else statement
            navMenu.add(R.id.categoryListMenu,categoryList.get(startPosition).get_id(),order,categoryName);
            //Create menu item object so it can be accessed and modified
            final MenuItem newItem = navMenu.getItem(navMenu.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM);
            //Set up the proper icon for each category (icon data comes from DB)
            idRes = this.getResources().getIdentifier(categoryList.get(startPosition).getIcon().getName(),"drawable",this.getPackageName());
            newItem.setIcon(idRes);
            //Set up the behaviour when category menu item is clicked on
            newItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d("onMenuItemClick","Enter the onMenuItemClick method defined for each category menu item in the MainActivity class.");
                    MenuItem homeItem = navMenu.getItem(0);
                    //Set proper variables for the HomeFragment to handle the correct accounts list to be displayed: All categories, favorites or a specific category
                    currentCategory = getCategoryPositionByID(item.getItemId());
                    tabLayout.selectTab( tabLayout.getTabAt(0));
                    //Ask nav controller to load the HomeFragment class
                    navController.navigate(R.id.nav_home);
                    //Get the drawer from layout
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    //Since the navigation item controls the Home menu item, it's necessary to overwrite it's bahaviour and set the home item as not selected
                    homeItem.setChecked(false);
                    homeItem.setCheckable(false);
                    //Set the item clicked on as the one selected
                    item.setChecked(true);
                    item.setCheckable(true);
                    //Close the drawer and display the HomeFragment which will load proper data based on the currentCategory variable
                    drawer.closeDrawer(Gravity.LEFT);
                    Log.d("onMenuItemClick","Exit the onMenuItemClick method defined for each category menu item in the MainActivity class.");
                    return false;
                }//End of onMenuItemClick method
            });//End of setOnMenuItemClickListener method call
            startPosition++;
        }//End of while loop
        Log.d("Ext_UpdateNaveMenu","Exit the updateNavMenu method in MainActivity class.");
    }//End of updateNavMenu method

    //Method to give nav drawer lower menu actual functionality for adding, deleting and editing a category
    private void setUpLowerCategoryMenu(final Menu navMenu){
        Log.d("setUpLowerCategoryMenu","Enter the updateNavMenu method in MainActivity class.");
        //Get the add category button and assign onclick event listener
        navMenu.findItem(R.id.nav_addCategory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Navigate to AddCategoryActivity
                //Check the category name doesn't exist
                    //If it does, display prompt

                //Add extras to the intent object, specifically the current category where the add button was pressed from
                //i.putExtra("category",this.currentCategory.toString());
                //i.putExtra("_id",question.get_id());
                //Start the AddItemActivity class
                throwAddCategoryActivity();
                return false;
            }//End of onMenuItemClick method
        });//End of setOnMenuItemClickListener method call
        //Get the edit category button and assign onclick event listener
        navMenu.findItem(R.id.nav_editCategory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //final Cursor categories = accountsDB.getCategoryListCursor();
                final int[] selectedCategoryID = {0};
                final int[] positionInList ={0};
                //Iterate through the category list to transform into a charsequence list
                final CharSequence[] categories = new CharSequence[categoryList.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM];
                //another one to hold the isChecked attribute
                //final boolean[] editableCategories = new boolean[categoryList.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM];
                //For loop to populate the char-sequence array with the category names coming from category list
                for(int i=INDEX_TO_GET_LAST_TASK_LIST_ITEM;i<categoryList.size();i++){
                    //For each item in the list, extract name and save it in the string array
                    int textID = getResources().getIdentifier(categoryList.get(i).getName(),"string",getPackageName());
                    CharSequence categoryName = "";
                    //Get the name from the cursor
                    if(textID > 0){
                        //If res id number exists, set the category name as per the string text, not the string ID
                        categoryName = getResources().getString(textID);
                    }else{
                        //In the case of not being a resource, print the text retrieved from DB
                        categoryName = MainActivity.getCategoryList().get(i).getName();
                    }//End of if else statement
                    //String categoryName = categoryList.get(i).getName();
                    //Save the name into the array to be passed into the AlertDialog constructor
                    categories[i-INDEX_TO_GET_LAST_TASK_LIST_ITEM]=  categoryName;
                    //Set the isChecked to false for all the categories
                    //editableCategories[i]= false;
                }//End of for loop to populate the taskList array
                //Create a dialog box to display the grocery types
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Category to edit")
                        .setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positionInList[0] = which;
                                selectedCategoryID[0] = categoryList.get(which+INDEX_TO_GET_LAST_TASK_LIST_ITEM).get_id();
                            }
                        })
                        .setPositiveButton(R.string.dialog_OK,new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int whichButton){
                                if(positionInList[0] == 0){
                                    selectedCategoryID[0] = categoryList.get(INDEX_TO_GET_LAST_TASK_LIST_ITEM).get_id();
                                }
                                throwEditCategoryActivity(selectedCategoryID[0],positionInList[0]);
                            }

                        })
                        .setNegativeButton(R.string.cancel,null)
                        .create()
                        .show();
                return false;
            }//End of onMenuItemClick method
        });//End of setOnMenuItemClickListener method call
        //Get the delete category button and assign onclick event listener
        navMenu.findItem(R.id.nav_deleteCategory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Display warning pop up
                //Delete all accounts associated to this category
                //Delete the category itself
                return false;
            }//End of onMenuItemClick method
        });//End of setOnMenuItemClickListener method call


        Log.d("setUpLowerCategoryMenu","Enter the updateNavMenu method in MainActivity class.");
    }//End of setUpLowerCategoryMenu method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    public Category getCategoryPositionByID(int _id) {
        Log.d("getCategoryPositionByID", "Enter the getCategoryPositionByID method in MainActivity class.");
        boolean found = false;
        int i = 0;
        while(i<categoryList.size() && !found){
            if(categoryList.get(i).get_id() == _id){
                found = true;
                break;
            }
            i++;
        }
        Log.d("getCategoryPositionByID", "Exit the getCategoryPositionByID method in MainActivity class.");
        return categoryList.get(i);
    }//End of getCategoryPositionByID method

}//End of MainActivity class.
