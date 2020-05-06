package io.github.jlrods.mypsswrdsecure;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
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
    private TabLayout tabLayout;


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
                //Declare and instantiate a new intent object
                Intent i= new Intent(MainActivity.this,SelectLogActivity.class);
                //Add extras to the intent object, specifically the current category where the add button was pressed from
                //Start the addTaskActivity class
                startActivity(i);

            }
        });
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //RecyclerView rv = null;
                AccountsDB accounts = null;
                //Cursor cursor = null;
                //rv = HomeFragment.getRv();
                accounts = HomeFragment.getAccounts();
                switch(tab.getPosition()){
                    case 0:
                        //Consider the category selected on drawer menu to run correct sql query
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
    }


    public void testRVLogo(){
        //Declare and instantiate a new intent object
        Intent i= new Intent(MainActivity.this,SelectLogActivity.class);
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

    public static void updateRecyclerViewData(RecyclerView.Adapter adpater){
        Log.d("Ent_updateRecViewData","Enter the updateRecyclerViewData method in the MainActivity class.");
        RecyclerView rv = HomeFragment.getRv();
        AccountsDB accounts = HomeFragment.getAccounts();
        Cursor cursor = null;
        // accounts = new AccountsDB(getBaseContext());
        //cursor = db.runQuery(sql);
        //Move to first row of cursor if not empty
        if(false){

        }
        else if(adpater instanceof PsswrdAdapter){
            cursor = accounts.getPsswrdList();
            ((UserNameAdapter) adpater).setCursor(cursor);
        }
        else if(adpater instanceof UserNameAdapter){
            cursor = accounts.getUserNameList();
            ((UserNameAdapter) adpater).setCursor(cursor);
        }
        cursor.moveToFirst();
        rv.setAdapter(adpater);
        adpater.notifyDataSetChanged();
        Log.d("Ext_updateRecViewData","Exit the updateRecyclerViewData method in the MainActivity class.");
    }//End of updateRecyclerViewData method
}
