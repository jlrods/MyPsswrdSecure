package io.github.jlrods.mypsswrdsecure;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
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

    //private RecyclerView recyclerView;
    //private RecyclerView.LayoutManager layoutManager;
    private TabLayout tabLayout = null;
    private int idRes;
    private static Icon myPsswrdSecureLogo = null;
    private static AccountsDB accounts = null;
    private static ArrayList<Category> categoryList = null;
    private static ArrayList<QuestionList> listOfQuestionLists = null;



    Cryptographer cryptographer = new Cryptographer();


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
                        throwAddAccountActivity(null);
                        break;
                    case 1:
                        //Call method to throw the AddUserName Activity
                        break;
                    case 2:
                        //Call method to throw the AddPsswrd Activity
                        break;
                    default:
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
                        MainActivity.updateRecyclerViewData(accountAdapter);
                        break;
                    case 1:
                        UserNameAdapter userNameAdapter = new UserNameAdapter(getBaseContext(),null);
                        MainActivity.updateRecyclerViewData(userNameAdapter);
                        break;
                    case 2:
                        PsswrdAdapter psswrdAdapter = new PsswrdAdapter(getBaseContext(),null);
                        MainActivity.updateRecyclerViewData(psswrdAdapter);
                        break;
                }// End of switch statement
                currentTab = tab.getPosition();
                boolean appStateUpdated = accounts.updateAppState(-1,tab.getPosition(),accounts.toInt(showAllAccounts) ,accounts.toInt(isFavoriteFilter),accounts.toInt(isSearchFilter),"HelloWorld'BaBay");

                if(appStateUpdated){
                    Cursor c = accounts.runQuery("SELECT * FROM APPSTATE");
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
//                    Snackbar snackbar = Snackbar.make(null, new String(encrypted), Snackbar.LENGTH_LONG);
//                    snackbar.setAction("Action", null).show();
                }//End of if else statement
            }//End of onTabSelected

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



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
        accounts = new AccountsDB(this);
        this.categoryList = accounts.getCategoryList();
        this.currentCategory = categoryList.get(0);
        //this.listOfQuestionLists = accounts.getListOfQuestionLists();
    }


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

    public static Icon getMyPsswrdSecureLogo() {
        return myPsswrdSecureLogo;
    }

    public static ArrayList<QuestionList> getListOfQuestionLists(){
        return listOfQuestionLists;
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
        AccountsDB accounts = HomeFragment.getAccounts();
        Cursor cursor = null;
        //Check the class of the adapter passed in as argument
        if(adapter instanceof AccountAdapter){
            cursor = accounts.getAccountsList();
            ((AccountAdapter) adapter).setCursor(cursor);
        }
        else if(adapter instanceof PsswrdAdapter){
            cursor = accounts.getPsswrdList();
            ((UserNameAdapter) adapter).setCursor(cursor);
        }
        else if(adapter instanceof UserNameAdapter){
            cursor = accounts.getUserNameList();
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
    private void throwAddAccountActivity(View view){
        Log.d("ThrowAddAcc","Enter throwAddAccountActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,AddAccountActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("category",this.currentCategory.toString());
        //i.putExtra("sql",this.getSQLForRecyclerView());
        //Start the addTaskActivity class
        startActivity(i);
        Log.d("ThrowAddAcc","Exit throwAddAccountActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity method

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
}//End of MainActivity class.
