package io.github.jlrods.mypsswrdsecure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.crypto.spec.IvParameterSpec;
import io.github.jlrods.mypsswrdsecure.login.LoginActivity;
import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private CoordinatorLayout coordinatorLayout;

    //Declare and initialize variables to define the current app state saved on DB
    private static Cursor appState = null;
    private static Category currentCategory = null;
    private static int currentTab = 0;
    private static boolean isSearchFilter = false;
    private static boolean isSearchUserNameFilter = false;
    private static boolean isSearchPsswrdFilter = false;
    private static String lastSearchText = "";
    private static boolean isSortFilter = false;
    private static SortFilter currentSortFilter = null;
    private static boolean isFirstRun = false;
    private static TabLayout tabLayout = null;
    private int idRes;
    private static Icon myPsswrdSecureLogo = null;
    private static AccountsDB accountsDB = null;
    private static ArrayList<Category> categoryList = null;
    //Hardcoded categories that cannot be deleted by user
    private static Category homeCategory = null;
    private static Category favCategory = null;
    private static ArrayList<QuestionList> listOfQuestionLists = null;
    private static String dateFormat;
    private static Cryptographer cryptographer;
    private boolean goodResultDelivered = false;
    private int itemPosition = -1;
    private boolean mainActLauchedFromLoginAct;

    //Set notify change type to insert item type
    private NotifyChangeType changeType = NotifyChangeType.DATA_SET_CHANGED;
    //Set proper text to display item insertion with a Toast
    String toastText = "";

    //CONSTANT VALUES
    private static final int INDEX_TO_GET_LAST_TASK_LIST_ITEM = 2;
    private static final String CHANNEL_ID = "TESTCHANNEL";

    //Throw intent codes
    private static final int THROW_IMAGE_GALLERY_REQ_CODE = 1642;
    private static final  int THROW_IMAGE_CAMERA_REQ_CODE = 2641;
    private static final int GALLERY_ACCESS_REQUEST = 5196;
    private static final int CAMERA_ACCESS_REQUEST = 3171;

    ///Throw activity request codes
    private final int THROW_ADD_ACCOUNT_ACT_REQCODE = 5566;
    private static final int THROW_ADD_QUESTION_ACT_REQCODE = 9876;
    private static final int THROW_ADD_USERNAME_ACT_REQCODE = 5744;
    private static final int THROW_ADD_PSSWRD_ACT_REQCODE = 9732;
    private final int TRHOW_ADD_CATEGORY_REQCODE = 5673;
    private final int THROW_EDIT_USERNAME_ACT_REQCODE = 4475;
    private final int THROW_EDIT_PSSWRD_ACT_REQCODE = 6542;
    private final int THROW_EDIT_QUESTIONS_ACT_REQCODE = 2456;
    private static final int THROW_EDIT_CATEGORY_ACT_REQCODE = 2002;
    private static final int THROW_EDIT_ACCOUNT_ACT_REQCODE = 1199;
    private static final int THROW_SELECT_NAVDRAWERBCKGRND_ACT_REQCODE = 4473;
    private final int THROW_UPDATE_APPLOGIN_ACT_REQCODE = 1983;

    private static final String ACCOUNTS_LOGOS = "logo_";
    private static final String NAV_DRAWER_BCKGRNDS = "nav_menu_header_bg";
    private static String RESOURCES = "Resources";

    //CONSTANTS: DB Table names
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

    //CONSTANTS: DB Column names
    private static final String CATEGORY_ID_COLUMN = "CategoryID";
    private static final String USER_NAME_ID_COLUMN = "UserNameID";
    private static final String USER_NAME_IV_COLUMN = "UserNameIV";
    private static final String PSSWRD_ID_COLUMN = "PsswrdID";
    private static final String PSSWRD_IV_COLUMN = "PsswrdIV";
    private static final String QUESTION_LIST_ID_COLUMN = "QuestionListID";
    private static final String QUESTION_ID_COLUMN = "QuestionID";
    private static final String QUESTION_ID_1_COLUMN = "QuestionID1";
    private static final String QUESTION_ID_2_COLUMN = "QuestionID2";
    private static final String QUESTION_ID_3_COLUMN = "QuestionID3";
    private static final String ICON_ID_COLUMN = "IconID";
    private static final String ICON_LOCATION_COLUMN="Location";
    private static final String ICON_IS_SELECTED_COLUMN="isSelected";
    private static final String ID_COLUMN = "_id";
    private static final String IS_FAVORITE_COLUMN = "IsFavorite";
    private static final String NAME_COLUMN = "Name";
    private static final String CURRENT_CATEGORY_ID_COLUMN = "currentCategoryID";
    private static final String CURRENT_TAB_COLUMN = "currentTab";
    private static final String ANSWER_ID_COLUMN ="AnswerID";
    private static final String IS_SEARCH_FILTER_COLUMN = "isSearchFilter";
    private static final String IS_SEARCH_USER_FILTER_COLUMN = "isSearchUserInAccountsFilter";
    private static final String IS_SEARCH_PSSWRD_FILTER_COLUMN = "isSearchPsswrdInAccountsFilter";
    private static final String LAST_SEARCH_TEXT_COLUMN = "lastSearch";
    private static final String IS_SORT_FILTER_COLUMN = "isSortFilter";
    private static final String CURRENT_SORT_FILTER_COLUMN = "currentSortFilter";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private static final String DATE_CREATED_COLUMN = "DateCreated";
    private static final String DATE_CHANGE_COLUMN = "DateChange";
    private static final String VALUE_COLUMN = "Value";
    private static final String MESSAGE_COLUMN = "Message";
    private static final String PICTUREID_COLUMN = "PictureID";
    private static final String INIT_VECTOR_COLUMN="initVector";

    private static Uri uriCameraImage = null;
    private static final String EXTERNAL_IMAGE_STORAGE_CLUE = "content://";
    private static final String USER_NAME = "user name";
    private static final String PASSWORD = "password";
    private static final String QUESTION = "question";
    private static final String QUESTION_LIST = "question list";

    //Object used to retrieve theme colors
    private static ThemeUpdater themeUpdater;

    //Login - Logout variables
    private Bundle extras;
    private static AppLoggin currentAppLoggin;
    private static boolean isAutoLogOutActive;
    private static boolean isLoggedOut;
    private static boolean isPushNotificationSent = false;
    private static int expiredPasswordAccountID = -1;
    private static NotificationManagerCompat notificationManager = null;
    private static NotificationCompat.Builder notificationBuilder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get default current app theme from preferences
        int appThemeSelected = setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        themeUpdater = new ThemeUpdater(this);
        //Call super on create
        super.onCreate(savedInstanceState);
        Log.d("Ent_onCreateMain", "Enter onCreate method in MainActivity class.");
        //Call method to setup language based on app preferences
        setAppLanguage(this);
        //Call method to setup date format based on app preferences
        dateFormat = themeUpdater.getDateFormat();
        //Get the logout timeout from preferences
        isAutoLogOutActive = isAutoLogOutActive(this);
        //Set the main activity layout
        setContentView(R.layout.activity_main);
        //Get the coordinator layout off layout
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //Get extras coming from LogginActivity (AppLoggin ID)
        //Extract extra data from owner Activity
        this.extras = getIntent().getExtras();
        if(this.extras !=null){
            if(extras.getBoolean("isMainActCalledFromLoginAct")){
                mainActLauchedFromLoginAct = true;
            }
        }
        //Get the tool bar off layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        //Change fab + icon color based on app theme
        fab.setImageTintList(ColorStateList.valueOf(themeUpdater.fetchThemeColor("colorPrimary")));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call the correct activity based on tab selection
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        //Call method to throw the AddAccount Activity
                        throwAddAccountActivity();
                        break;
                    case 1:
                        //Call method to throw the AddUserName Activity
                        throwActivityNoExtras(MainActivity.this,AddUserNameActivity.class,THROW_ADD_USERNAME_ACT_REQCODE);
                        Intent i = new Intent(MainActivity.this, AddUserNameActivity.class);
                        break;
                    case 2:
                        //Call method to throw the AddPsswrd Activity
                        throwActivityNoExtras(MainActivity.this,AddPsswrdActivity.class,THROW_ADD_PSSWRD_ACT_REQCODE);
                        break;
                    default:
                        //Call method to throw the AddQuestion Activity
                        throwActivityNoExtras(MainActivity.this,AddQuestionActivity.class,THROW_ADD_QUESTION_ACT_REQCODE);
                        break;
                }//End of switch statement to check current tab selection
            }//End of on click method implementation
        });//End of set on click listener method
        //Get the tablayout from layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        //Declare and initialize a context object to hold the MainActivity context within the onTabSelectedListener method
        //Used to update password strength colors based on app theme
        final Context mainActivityContext = this;
        //Set up the onclick behaviour for each tab in the tablayout object
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        //Consider the category selected on drawer menu to run correct sql query
                        AccountAdapter accountAdapter = new AccountAdapter(getBaseContext(), null);
                        //Setup the onclick event listener
                        accountAdapter.setOnClickListener(new View.OnClickListener() {
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
                        if (!isFirstRun) {
                            clearSearchFilter();
                            clearSortFilter();
                        }
                        updateRecyclerViewData(accountAdapter,-1, NotifyChangeType.DATA_SET_CHANGED);
                        break;
                    case 1:
                        //Create rv adapter for the user name tab
                        UserNameAdapter userNameAdapter = new UserNameAdapter(getBaseContext(), null);
                        //Setup the onclick event listener
                        userNameAdapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                throwEditUserNameActivity(v);
                            }//End of onClick method
                        });//End of setOnClickListener
                        if (!isFirstRun) {
                            clearSearchFilter();
                            clearSortFilter();
                        }
                        updateRecyclerViewData(userNameAdapter,-1,NotifyChangeType.DATA_SET_CHANGED);
                        break;
                    case 2:
                        PsswrdAdapter psswrdAdapter = new PsswrdAdapter(mainActivityContext, null);
                        //Setup the onclick event listener
                        psswrdAdapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                throwEditPsswrdActivity(v);
                            }//End of onClick method
                        });//End of setOnClickListener
                        if (!isFirstRun) {
                            clearSearchFilter();
                            clearSortFilter();
                        }
                        updateRecyclerViewData(psswrdAdapter,-1,NotifyChangeType.DATA_SET_CHANGED);
                        break;
                    case 3:
                        SecurityQuestionAdapter secQuestionAdapter = new SecurityQuestionAdapter(getBaseContext(), null);
                        if (!isFirstRun) {
                            clearSearchFilter();
                            clearSortFilter();
                        }
                        updateRecyclerViewData(secQuestionAdapter,-1,NotifyChangeType.DATA_SET_CHANGED);
                        secQuestionAdapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                throwEditQuestionActivity(v);
                            }
                        });//End of setOnClickListener
                        break;
                }// End of switch statement
                currentTab = tab.getPosition();
                //Declare and instantiate values object to store the app state attributes to be passed into table update method
                ContentValues appStateValues = new ContentValues();
                appStateValues.put(ID_COLUMN, accountsDB.getMaxItemIdInTable(APPSTATE_TABLE));
                appStateValues.put(CURRENT_TAB_COLUMN, currentTab);
                //Call DB update method
                boolean appStateUpdated = accountsDB.updateTable(APPSTATE_TABLE, appStateValues);
                //If update goes well, get values from DB???
                if (appStateUpdated) {
                    Cursor c = accountsDB.getAppState();
                    c.moveToFirst();
                } else {
                    //Display error message
                    displayAlertDialogNoInput(getBaseContext(),getString(R.string.appStateError),getString(R.string.appStateErrorMssg));
                }//End of if else statement
            }//End of onTabSelected

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                clearSearchFilter();
                this.onTabSelected(tab);
            }
        });// End of addOnTabSelectedListener method for tabLayout

        //Create and set default logo for accounts
        myPsswrdSecureLogo = new Icon(R.mipmap.ic_my_psswrd_secure_new, "MyPsswrdSecureIcon", String.valueOf(R.mipmap.ic_my_psswrd_secure_new), false);
        cryptographer = LoginActivity.getCryptographer();
        //Create a new object to manage all DB interaction
        accountsDB = LoginActivity.getAccountsDB();
        //Get the category list from DB
        this.homeCategory = new Category("Home", new Icon("Home", MainActivity.getRESOURCES(), R.drawable.home));
        this.favCategory = new Category(-2, "Favorites", new Icon("Favorites", MainActivity.getRESOURCES(), android.R.drawable.star_big_on));
        this.categoryList = accountsDB.getCategoryList();

        //Retrieve App state from DB and update app variable appropriately
        this.appState = accountsDB.getAppState();
        if (this.appState != null && this.appState.getCount() > 0) {
            this.currentTab = this.appState.getInt(2);
            if (currentTab != 0) {
                isFirstRun = true;
            }
        } else {
            //Set default app state values
            accountsDB.getWritableDatabase().execSQL("INSERT INTO APPSTATE VALUES(null,-1,1,0,0,0,'',0,-1);");
            this.appState = accountsDB.getAppState();
            this.currentTab = this.appState.getInt(2);
        }//End of if statement to check and extract the app state from dB
        //Get the current category and tab stored from app state
        this.currentCategory = this.getCategoryByID(this.appState.getInt(1));
        //Counter measure to avoid NullException when current category recorded on app state was deleted, but never updated to
        // new current category
        if(this.currentCategory == null){
            //Set home as default
            this.currentCategory = this.categoryList.get(0);
            //update app state
            ContentValues values = new ContentValues();
            values.put(CURRENT_CATEGORY_ID_COLUMN,-1);
            accountsDB.updateTable(APPSTATE_TABLE,values);
        }//End of if statement to check the current category isn't null

        this.isSearchFilter = this.accountsDB.toBoolean(this.appState.getInt(3));
        this.isSearchUserNameFilter = this.accountsDB.toBoolean(this.appState.getInt(4));
        this.isSearchPsswrdFilter = this.accountsDB.toBoolean(this.appState.getInt(5));
        this.lastSearchText = this.appState.getString(6);
        this.isSortFilter = this.accountsDB.toBoolean(this.appState.getInt(7));
        this.currentSortFilter = SortFilter.getSortFilterByOrdinal(this.appState.getInt(8));
        //Set boolean flag to identify first run of the onCreate method
        //This flag is a work around to avoid clearSearch filter when calling onTabSelected method when
        //Changing tab during first run of program

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.mobile_navigation)
                .setDrawerLayout(drawer)
                .build();
        //Get the menu in the navigation view
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        this.setUpLowerCategoryMenu(navigationView.getMenu());
        this.updateNavMenu(navigationView.getMenu(), INDEX_TO_GET_LAST_TASK_LIST_ITEM);
        //Once the nave menu has been fully set up, select the item that represents the current category and deselect the Home item, which is selected by default

        if (this.currentCategory.get_id() != -1) {
            navigationView.getMenu().getItem(0).setCheckable(false);
            navigationView.getMenu().getItem(0).setChecked(false);
            navigationView.getMenu().getItem(getCategoryPositionByID(currentCategory.get_id())).setCheckable(true);
            navigationView.getMenu().getItem(getCategoryPositionByID(currentCategory.get_id())).setChecked(true);
            //Check if if current category is favorites
            if (this.currentCategory.get_id() == -2) {
                //For favorite set toolbar name directly
                toolbar.setTitle(R.string.menu_favorites);
            } else {
                //Otherwise, set the toolbar title by getting translation when available
                toolbar.setTitle(getCategoryNameFromRes(currentCategory.getName()));
            }//End of if statement to check the category to be selected
        } else {
            //Set toolbar title based on home category
            toolbar.setTitle(R.string.menu_home);
        }

        //Create current AppLoggin by passing extra information coming from login activity. This was intended to support multiple user login.
        //Workaround to fix push notification task stack reset
        if(mainActLauchedFromLoginAct){
            //If extras are sent from LogginActivity, get ID from extras
            this.currentAppLoggin = AppLoggin.extractAppLoggin(accountsDB.getAppLoginCursor(this.extras.getInt("appLoginID")));
        }else{
            //Otherwise, get applogin ID from DB
            this.currentAppLoggin = AppLoggin.extractAppLoggin(accountsDB.getAppLoginCursor(accountsDB.getMaxItemIdInTable(APPLOGGIN_TABLE)));
        }

        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throwSelectNavDrawerBackgroundActivity();
            }
        });
        //Set up user data on the nav drawer

        TextView tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserProfileText(1, (TextView) v);
            }
        });
        TextView tvUserMessage = (TextView) headerView.findViewById(R.id.tvUserMessage);
        tvUserMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserProfileText(2, (TextView) v);
            }
        });
        if (this.currentAppLoggin != null) {
            tvUserName.setText(this.currentAppLoggin.getName());
            tvUserMessage.setText(this.currentAppLoggin.getMessage());
            Icon navDrawerBackground = accountsDB.getIconByID(this.currentAppLoggin.getPicture().get_id());
            int idRes;
            Resources r = this.getResources();
            idRes = r.getIdentifier(navDrawerBackground.getName(), "drawable", this.getPackageName());
            //imgLogo.setImageResource(idRes);
            headerView.setBackground(getResources().getDrawable(idRes));
        } else {
            //Call logout method and display error message???
            displayToast(((Activity)this).getParent(),"Log in error, the app loggin supplied doesn't match the one in the app records!",Toast.LENGTH_LONG,Gravity.CENTER);
            //Call method to check for notification sent and update if required
            MainActivity.checkForNotificationSent(this,true);
            //Call method to throw LoginActivity and clear activity stack.
            logout();
        }//End of if statement to check user cursor is not empty
        //Check for expired password accounts to display push notifications
        //Get from DB a list of accounts with password due for renewal
        Cursor expiredPsswrdAccounts = accountsDB.getExpiredPsswrdAccounts();
        //Check the list returned at least one item in it
        if(expiredPsswrdAccounts.moveToFirst()){
            //Extract first item in the list of accounts with expired password
            Account expiredPsswrdAccount = Account.extractAccount(expiredPsswrdAccounts);
            // Create an explicit intent for an Activity
            Intent intent = new Intent(this, EditAccountActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            //Set up extra information into the intent to be sent the the EditAccountActivity
            intent.putExtra("category", expiredPsswrdAccount.getCategory().get_id());
            intent.putExtra(ID_COLUMN, expiredPsswrdAccount.get_id());
            intent.putExtra("isActivityCalledFromNotification",true);
            intent.putExtra("notifiCationIssuedFromMainAct",true);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(THROW_EDIT_ACCOUNT_ACT_REQCODE, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_my_psswrd_secure_full)
                    .setContentTitle(getString(R.string.pushNotificationPsswrdExpMssg))
                    .setContentText(getString(R.string.pushNotPsswrdHasExp1)+" "+expiredPsswrdAccount.getName()+" "+getString(R.string.pushNotPsswrHasExp2))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    //.setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                    .setAutoCancel(true);
            this.createNotificationChannel();
            notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            isPushNotificationSent = true;
            expiredPasswordAccountID = expiredPsswrdAccount.get_id();
            notificationManager.notify(expiredPasswordAccountID, notificationBuilder.build());
        }//End of if statement to check at least one account has expired password
        Log.d("Ext_onCreateMain", "Exit onCreate method in MainActivity class.");
    }//End of onCreate method
    @Override
    public void onPause(){
        super.onPause();
    }//End of onPause method

    @Override
    public void onStop(){
        super.onStop();
        //Call method to check for notification sent and update if required. Pass in as argument the flag to identify if auto log out has timed out
        checkForNotificationSent(this,checkIsAppLoggedOut());
        Log.d("onStopMain", "Exit onStop method in MainActivity class.");
    }//End of onStop method

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResumeMain", "Enter onResume method in MainActivity class.");
        //Call MainActivity static method to check for logout timeout to display logout prompt accordingly
        this.checkLogOutTimeOut(this);
        Log.d("onResumeMain", "Exit onResume method in MainActivity class.");
    }//End of onResume method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Call method to check for notification sent and update if required. Pass in as argument the flag to identify if auto log out has timed out
        checkForNotificationSent(this,checkIsAppLoggedOut());
        //Call method to check for notification sent and update if required
        Log.d("onDestroyMain", "Enter/Exit onDestroy method in MainActivity class.");
    }//End of onDestroy method

    public static boolean checkIsAppLoggedOut(){
        Log.d("checkIsAppLoggedOut", "Enter checkIsAppLoggedOut method in MainActivity class.");
        boolean isAppLogged = false;
        //Check if auto logout is being used
        if(MainActivity.isAutoLogOutActive()){
            //Get logout timer object
            LogOutTimer logOutTimer = (LogOutTimer)AutoLogOutService.getLogOutTimer();
            //Check if logout time out has been reached and if inner timer finished too
            //Meaning the app has now logged out
            isAppLogged = logOutTimer.isLogOutTimeout() && !logOutTimer.isPromptIdleTimerRunning();
        }else{
            //If not auto logout function not in use, just check and return the isLoggedOut static flag
            isAppLogged = MainActivity.isLoggedOut;
        }
        Log.d("checkIsAppLoggedOut", "Exit checkIsAppLoggedOut method in MainActivity class.");
        return isAppLogged;
    }//End of checkIsAppLoggedOut method

    public static void checkForNotificationSent(Context context, boolean isLogOutCalled) {
        Log.d("onStopMain", "Enter checkForNotificationSent method in MainActivity class.");
        //Check if app has been logged out. In that case, update the notification intent to open from login display instead.
        if(isLogOutCalled){
            updateNotificationIntent(context);
        }//End of if statement to check the isLogOutCalled parameter

//        if(!isLogOutCalled){
//            if(isPushNotificationSent && !LogOutTimer.isAppInForeground()){
//                updateNotificationIntent(context);
//            }//End of if statement to check if push notification has been sent to OS and app not in foreground
//        }else{
//            if(isPushNotificationSent && LogOutTimer.isAppInForeground()){
//                updateNotificationIntent(context);
//            }//End of if statement to check push notification has been sent and app in foreground
//        }//End of if else statement to check manual or out logout method was called
        Log.d("onStopMain", "Exit checkForNotificationSent method in MainActivity class.");
    }//End of checkForNotificationSent method


    public static void updateNotificationIntent(Context context) {

        //Here needs to be check notification manager was created, that means a notificaiton was sent during onCreate activity.
        //Therefore, the update is required.
        if (notificationManager != null) {
            //Update the intent so it calls the login screen
            Intent intent = new Intent(context, LoginActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //Set up extra information into the intent to be sent the the EditAccountActivity
            //intent.putExtra("category", expiredPsswrdAccount.getCategory().get_id());
            intent.putExtra("isActivityCalledFromNotification", true);
            intent.putExtra("expiredPasswordAccountID", expiredPasswordAccountID);
            intent.putExtra("notifiCationIssuedFromMainAct", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(THROW_EDIT_ACCOUNT_ACT_REQCODE, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationManager.notify(expiredPasswordAccountID, notificationBuilder.build());
        }//End of if statement to check if push notification has been sent to Android OS
    }//End of updateNotificationIntent method


    @Override
    public void onBackPressed(){
        Log.d("onBackPressedMain", "Enter onBackPressed method in MainActivity class.");
        //Call logout method to launch login screen and clear stack
        this.logout(this);
        super.onBackPressed();
        Log.d("onBackPressedMain", "Exit onBackPressed method in MainActivity class.");
    }//End of onBackPressed method

    //Method to call android finish method
    public void callFinish(){
        Log.d("callFinish", "Enter|Exit logout method in MainActivity class.");
        this.finish();
    }//End of callFinish method

    //Method to get category name from the resource id
    private String getCategoryNameFromRes(String name) {
        Log.d("getCategoryNameFromRes", "Enter getCategoryNameFromRes method in MainActivity class.");
        //Declare and instantiate an int to hold the string id from resources and a String variable to hold the actual category name
        int textID = getResources().getIdentifier(name, "string", getPackageName());
        String categoryName = "";
        //If textID is 0, means it's not stored in the app resources, which means it won't be translated but it will be displayed as saved on DB
        if (textID > 0) {
            //If res id number exists, set the category name as per the string text, not the string ID
            categoryName = getResources().getString(textID);
        } else {
            //In the case of not being a resource, print the text retrieved from DB
            categoryName = name;
        }//End of if else statement
        Log.d("getCategoryNameFromRes", "Exit getCategoryNameFromRes method in MainActivity class.");
        return categoryName;
    }//End of getCategoryNameFromRes method


    @Override
    //Method to create top menu
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu", "Enter onCreateOptionsMenu method in MainActivity class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (this.isSearchFilter) {
            menu.getItem(0).getIcon().setTintList(ColorStateList.valueOf(themeUpdater.fetchThemeColor("colorAccent")));
        } else if (this.isSortFilter) {
            menu.getItem(1).getIcon().setTintList(ColorStateList.valueOf(themeUpdater.fetchThemeColor("colorAccent")));
        }
        Log.d("onCreateOptionsMenu", "Exit onCreateOptionsMenu method in MainActivity class.");
        return true;
    }//End of onCreateOptionsMenu method

    @Override
    //Menu to handle menu selections
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "Enter the onOptionsItemSelected method in the MainActivity class.");
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_applogin:
                //Call method to update app login
                this.throwUpdateAppLoginActivity();
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with App Login option selected.");
                return true;
            case R.id.action_logout:
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                logout(this);
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with Logout option selected.");
                return true;
            case R.id.action_about:
                //Call method to throw AboutActivity activity
                this.throwActivityNoExtras(MainActivity.this,AboutActivity.class,-1);
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with About option selected.");
                return true;
            case R.id.action_settings:
                this.throwActivityNoExtras(MainActivity.this,PreferencesActivity.class,-1);
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with Preferences option selected.");
                return true;
            case R.id.action_sort:
                this.sort(item);
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with Sort option selected.");
                return true;
            case R.id.action_search:
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with Search option selected.");
                this.search(item);
                return true;
            default:
                Log.d("onOptionsItemSelected", "Exit the onOptionsItemSelected method in the MainActivity class with Default option selected.");
                return super.onOptionsItemSelected(item);
        }//End fo switch statement
    }//End of onOptionsItemSelected method

    @Override
    public boolean onSupportNavigateUp() {
        Log.d("onSupportNavigateUp", "Enter the onSupportNavigateUp method in the MainActivity class.");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }//End of onSupportNavigateUp

    //Method to return current tab the app is displaying at the moment
    public TabLayout.Tab getCurrentTab() {
        Log.d("getCurrentTab", "Enter|Exit the getCurrentTab method in the MainActivity class.");
        return this.tabLayout.getTabAt(this.tabLayout.getSelectedTabPosition());
    }//End of getCurrentTab method

    //Method to return a category by passing in its DB id
    public static Category getCategoryByID(int _id) {
        Log.d("getCatByID", "Enter the getCategoryByID method in the MainActivity class.");
        Category category = null;
        boolean found = false;
        int i = 0;
        while (i < categoryList.size() && !found) {
            if (categoryList.get(i).get_id() == _id) {
                category = categoryList.get(i);
                found = true;
                Log.d("getCatByID", "A category with ID " + _id + " has been found in the categoryList object in MainActivity class.");
            }
            i++;
        }// End of while loop
        Log.d("getCatByID", "Exit the getCategoryByID method in the MainActivity class.");
        return category;
    }// End of getCategoryID method

    //Method to update data displayed on RecyclerView. This new method includes propriate notification for insertion, deletion or change of items
    //and/or full data set change.
    public static void updateRecyclerViewData(RecyclerView.Adapter adapter, int position, NotifyChangeType changeType){
        Log.d("updateRecViewDataNew", "Enter new the updateRecyclerViewData method in the MainActivity class.");
        //Call method to get updated cursor for current app state (current category, search and sort filter status).
        Cursor cursor = getCursorToUpdateRV(adapter);
        //Check the class of the adapter passed in as argument#
        if (adapter instanceof AccountAdapter) {
            //Set new data set (cursor) to the accounts adapter
            ((AccountAdapter) adapter).setCursor(cursor);
        }else if(adapter instanceof PsswrdAdapter){
            ((PsswrdAdapter) adapter).setCursor(cursor);
        }else if(adapter instanceof SecurityQuestionAdapter){
            ((SecurityQuestionAdapter) adapter).setCursor(cursor);
        }else if(adapter instanceof UserNameAdapter){
            ((UserNameAdapter) adapter).setCursor(cursor);
        }//End of if else chain to check type of adapter passed in

        //Check the changeType variable to determine what notifyChange method will be called for the RV
        switch (changeType.ordinal()){
            case 0:
                //0 corresponds to Item in "postion" has been changed
                adapter.notifyItemChanged(position);
                break;
            case 1:
                //1 corresponds to item removed in "position"
                adapter.notifyItemRemoved(position);
                break;
            case 2:
                //2 corresponds to item inserted in "position"
                adapter.notifyItemInserted(position);
                break;
            default:
                //Default would correspond to full data set change
                if(cursor != null){
                    cursor.moveToFirst();
                }
                RecyclerView rv = HomeFragment.getRv();
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                break;
        }//End of switch statment to check change type variable
        Log.d("updateRecViewDataNew", "Exit new the updateRecyclerViewData method in the MainActivity class.");
        isFirstRun = false;
    }//End of updateItemInRecyclerView method

    //Method to get cursor with data to be populated on the RV object
    public static Cursor getCursorToUpdateRV(RecyclerView.Adapter adapter){
        Log.d("getCursorToUpdateRV", "Enter the getCursorToUpdateRV method in the MainActivity class.");
        //Declare and initialize cursor to be returned by method
        Cursor cursor = null;
        if (adapter instanceof AccountAdapter) {
            //Check search filer boolean flag
            if (isSearchFilter) {
                //Check if specific user name or password search filter is used
                if (isSearchUserNameFilter) {
                    //If user name search filter required, get username from DB
                    Cursor userName = accountsDB.getUserNameByName(lastSearchText);
                    if (userName != null && userName.getCount() > 0) {
                        //If username exists, get cursor with all the accounts that hold that user name
                        cursor = accountsDB.getAccountsWithSpecifcValueAndCategory(USER_NAME_ID_COLUMN, userName.getInt(0), MainActivity.getCurrentCategory().get_id());
                    } else {
                        //Otherwise return:
                        //a work around to get a Non null cursor with no data as null cursor crashes app when getCount method is called by RV
                        cursor = accountsDB.getAccountsWithSpecifcValue(USER_NAME_ID_COLUMN, -1);
                    }//En of if else statement to check the user name retrieved isn't null
                }else if (isSearchPsswrdFilter) {
                    //If password search filter required, get password from DB
                    Cursor psswrd = accountsDB.getPsswrdByName(lastSearchText);
                    if (psswrd != null && psswrd.getCount() > 0) {
                        //If password exists, get cursor with all the accounts that hold that password
                        cursor = accountsDB.getAccountsWithSpecifcValueAndCategory(PSSWRD_ID_COLUMN, psswrd.getInt(0), MainActivity.getCurrentCategory().get_id());
                    }else {
                        //Otherwise return:
                        //a work around to get a Non null cursor with no data as null cursor crashes app when getCount method is called by RV
                        cursor = accountsDB.getAccountsWithSpecifcValue(PSSWRD_ID_COLUMN, -1);
                    }//End of if else statement to check password retrieved isn't nulll
                } else {
                    //Since user name and password specific search is not category limited, if the search text is searched in the account name
                    //include the current category in the search criteria
                    cursor = accountsDB.getAccountsThatContainsThisTextInName(lastSearchText, currentCategory.get_id());
                }//End of if else statement to check if the Account special search feature is being used
            } else if (isSortFilter) {
                //In case sort filter is the one used, check type of sorting used
                if (currentSortFilter == SortFilter.ALPHA_ASC) {
                    //AlphabeticalAscendant
                    cursor = accountsDB.getAccountsSortedByColumnUpOrDown(NAME_COLUMN, ASC);
                } else if (currentSortFilter == SortFilter.ALPHA_DES) {
                    //Alphabetical Descendant
                    cursor = accountsDB.getAccountsSortedByColumnUpOrDown(NAME_COLUMN, DESC);
                } else if (currentSortFilter == SortFilter.DATE_ASC) {
                    //Date created ascendant
                    cursor = accountsDB.getAccountsSortedByColumnUpOrDown(DATE_CREATED_COLUMN, ASC);
                } else if (currentSortFilter == SortFilter.DATE_DES) {
                    //Date created descendant
                    cursor = accountsDB.getAccountsSortedByColumnUpOrDown(DATE_CREATED_COLUMN, DESC);
                } else if (currentSortFilter == SortFilter.CATEGORY) {
                    //Category
                    cursor = accountsDB.getAccountsSortedByColumnUpOrDown(CATEGORY_ID_COLUMN, ASC);
                }//End of if statement for sort filter flag
            } else {
                //If not filter is used at the moment, check only for current category
                //Check current category variable to call method that retrieves proper account list
                if (MainActivity.getCurrentCategory().get_id() == homeCategory.get_id()) {
                    cursor = accountsDB.getAccountsList();
                } else if (MainActivity.getCurrentCategory().get_id() == favCategory.get_id()) {
                    cursor = accountsDB.getAccountsWithSpecifcValue(IS_FAVORITE_COLUMN, 1);
                } else {
                    cursor = accountsDB.getAccountsWithSpecifcValue(CATEGORY_ID_COLUMN,currentCategory.get_id());
                }//End of if else statements to check current category
            }//End of if else statement to check if the search filter is active
        }else if(adapter instanceof PsswrdAdapter){
            //Check the isSearch filter flag to define the correct cursor to retrieve from the DB
            if (isSearchFilter) {
                cursor = accountsDB.getPsswrdByName(lastSearchText);
            } else if (isSortFilter) {
                if (currentSortFilter == SortFilter.DATE_ASC) {
                    cursor = accountsDB.getPsswrdsSortedColumnUpDown(DATE_CREATED_COLUMN, ASC);
                } else if (currentSortFilter == SortFilter.DATE_DES) {
                    cursor = accountsDB.getPsswrdsSortedColumnUpDown(DATE_CREATED_COLUMN, DESC);
                } else if (currentSortFilter == SortFilter.TIMES_USED) {
                    cursor = accountsDB.getPsswrdsSortedByTimesUsed();
                }
            } else {
                cursor = accountsDB.getPsswrdList();
            }
        }else if (adapter instanceof SecurityQuestionAdapter) {
            //Check the isSearch filter flag to define the correct cursor to retrieve from the DB
            if (isSearchFilter) {
                //Call DB method to retrieve the questions that holds specific value in the question text
                cursor = accountsDB.getQuestionsWithThisTextInValue(lastSearchText);
            } else if (isSortFilter) {
                if (currentSortFilter == SortFilter.ALPHA_ASC) {
                    cursor = accountsDB.getQuestionsSortedColumnUpDown(ASC);
                } else if (currentSortFilter == SortFilter.ALPHA_DES) {
                    cursor = accountsDB.getQuestionsSortedColumnUpDown(DESC);
                } else if (currentSortFilter == SortFilter.TIMES_USED) {
                    cursor = accountsDB.getQuestionsSortedByTimesUsed();
                }
            } else {
                cursor = accountsDB.getListQuestionsAvailableNoAnsw();
            }
        }else if (adapter instanceof UserNameAdapter) {
            //Check the isSearch filter flag to define the correct cursor to retrieve from the DB
            if (isSearchFilter) {
                cursor = accountsDB.getUserNameByName(lastSearchText);
            } else if (isSortFilter) {
                if (currentSortFilter == SortFilter.DATE_ASC) {
                    cursor = accountsDB.getUserNamesSortedColumnUpDown(DATE_CREATED_COLUMN, ASC);
                } else if (currentSortFilter == SortFilter.DATE_DES) {
                    cursor = accountsDB.getUserNamesSortedColumnUpDown(DATE_CREATED_COLUMN, DESC);
                } else if (currentSortFilter == SortFilter.TIMES_USED) {
                    cursor = accountsDB.getUserNameCursorSortedByTimesUsed();
                }
            } else {
                cursor = accountsDB.getUserNameList();
            }
        }//End of if else statement that checks the instance of the adapter
        Log.d("getCursorToUpdateRV", "Enter the getCursorToUpdateRV method in the MainActivity class.");
        return cursor;
    }//End of getCursorToUpdateRV method

    //Generic method to throw a new Activity with no extras added to intent
    private void throwActivityNoExtras(Activity activity, Class className,int requestCode) {
        Log.d("throwActivityNoExtras", "Enter throwActivityNoExtras method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i = new Intent(activity,className);
        //Start the AddItemActivity class
        if(requestCode > 0){
            startActivityForResult(i, requestCode);
            Log.d("throwActivityNoExtras", "startActivityForResult called by throwActivityNoExtras method in the MainActivity class with request code: "+requestCode);
        }else{
            startActivity(i);
            Log.d("throwActivityNoExtras", "startActivity called by throwActivityNoExtras method in the MainActivity class.");
        }//End of if else statement to check request code is greater than 0
        Log.d("throwActivityNoExtras", "Exit throwActivityNoExtras method in the MainActivity class.");
    }//End of throwActivityNoExtras method

    private void throwUpdateAppLoginActivity() {
        Log.d("throwUpdateAppLog", "Enter throwActivityNoExtras method in the MainActivity class.");
        //Launch decrypt service
        Intent decryptDataService = new Intent(this, DecryptDataService.class);
        startService(decryptDataService);
        //Declare and instantiate a new intent object
        Intent i = new Intent(this,UpdateAppLoginActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("isActivityCalledFromNotification", false);
        i.putExtra("notifiCationIssuedFromMainAct",false);
        //Start the addTaskActivity class
        startActivityForResult(i, THROW_UPDATE_APPLOGIN_ACT_REQCODE);
        //Start the AddItemActivity class
//        if(requestCode > 0){
//            startActivityForResult(i, requestCode);
//            Log.d("throwActivityNoExtras", "startActivityForResult called by throwActivityNoExtras method in the MainActivity class with request code: "+requestCode);
//        }else{
//            startActivity(i);
//            Log.d("throwActivityNoExtras", "startActivity called by throwActivityNoExtras method in the MainActivity class.");
//        }//End of if else statement to check request code is greater than 0
        Log.d("throwUpdateAppLog", "Exit throwActivityNoExtras method in the MainActivity class.");
    }//End of throwActivityNoExtras method

    //Method to throw new AddTaskActivity
    private void throwAddAccountActivity() {
        Log.d("ThrowAddAcc", "Enter throwAddAccountActivity method in the MainActivity class.");
        //Launch decrypt service
        Intent decryptDataService = new Intent(this, DecryptDataService.class);
        startService(decryptDataService);
        //Declare and instantiate a new intent object
        Intent i = new Intent(this, AddAccountActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("category", this.currentCategory.get_id());
        //Start the addTaskActivity class
        startActivityForResult(i, THROW_ADD_ACCOUNT_ACT_REQCODE);
        Log.d("ThrowAddAcc", "Exit throwAddAccountActivity method in the MainActivity class.");
    }//End of throwAddAccountActivity

    //Method to carry out all required prep before throwing EditAccountActivity
    public static Intent prepareThrowEditAccountActivity(Context context, View v) {
        Log.d("PrepThrowEdAcc", "Enter prepareThrowEditAccountActivity method in the MainActivity class.");
        //Get RV object from home fragment
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((AccountAdapter) rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        Account account = Account.extractAccount(cursor);
        //Declare and instantiate a new intent object
        Intent i = new Intent(context, EditAccountActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        i.putExtra("category", currentCategory.get_id());
        i.putExtra(ID_COLUMN, account.get_id());
        i.putExtra("position",itemPosition);
        Log.d("PrepThrowEdAcc", "Exit prepareThrowEditAccountActivity method in the MainActivity class.");
        return i;
    }//End of prepareThrowEditAccountActivity method

    //Method to throw new EditAccountActivity
    private void throwEditAccountActivity(View v) {
        Log.d("ThrowEditAcc", "Enter throwEditAccountActivity method in the MainActivity class.");
        //Launch decrypt service
        Intent decryptDataService = new Intent(this, DecryptDataService.class);
        startService(decryptDataService);
        Intent i  = prepareThrowEditAccountActivity(MainActivity.this,v);
        //Start the AddItemActivity class
        startActivityForResult(i, THROW_EDIT_ACCOUNT_ACT_REQCODE);
        Log.d("ThrowEditAcc", "Exit throwEditAccountActivity method in the MainActivity class.");
    }//End of throwEditAccountActivity method


    //Method to throw new EditUserNameActivity
    private void throwEditUserNameActivity(View v) {
        Log.d("ThrowEditUser", "Enter throwEditUserNameActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((UserNameAdapter) rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        UserName userName = UserName.extractUserName(cursor);
        //Declare and instantiate a new intent object
        Intent i = new Intent(MainActivity.this, EditUserNameActivity.class);
        //Add extras to the intent object
        i.putExtra(ID_COLUMN, userName.get_id());
        i.putExtra("position",itemPosition);
        //Start the AddItemActivity class
        startActivityForResult(i, this.THROW_EDIT_USERNAME_ACT_REQCODE);
        Log.d("ThrowEditUser", "Exit throwEditUserNameActivity method in the MainActivity class.");
    }//End of ThrowEditUser method

    //Method to throw new EditPsswrdActivity
    private void throwEditPsswrdActivity(View v) {
        Log.d("ThrowEditPss", "Enter throwEditPsswrdActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((PsswrdAdapter) rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        Psswrd psswrd = Psswrd.extractPsswrd(cursor);
        //Declare and instantiate a new intent object
        Intent i = new Intent(MainActivity.this, EditPsswrdActivity.class);
        //Add extras to the intent object
        i.putExtra(ID_COLUMN, psswrd.get_id());
        i.putExtra("position",itemPosition);
        //Start the AddItemActivity class
        startActivityForResult(i, this.THROW_EDIT_PSSWRD_ACT_REQCODE);
        Log.d("ThrowAddUser", "Exit throwEditPsswrdActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    //Method to throw new EditQuestionActivity
    private void throwEditQuestionActivity(View v) {
        Log.d("ThrowEditPss", "Enter throwEditQuestionActivity method in the MainActivity class.");
        //rv
        RecyclerView rv = HomeFragment.getRv();
        //Get the item position in the adapter
        int itemPosition = rv.getChildAdapterPosition(v);
        //move the cursor to the task position in the adapter
        Cursor cursor = ((SecurityQuestionAdapter) rv.getAdapter()).getCursor();
        cursor.moveToPosition(itemPosition);
        //Extract the task object from the cursor row
        Question question = Question.extractQuestion(cursor);
        //Declare and instantiate a new intent object
        Intent i = new Intent(MainActivity.this, EditQuestionActivity.class);
        //Add extras to the intent object
        i.putExtra(ID_COLUMN, question.get_id());
        i.putExtra("position",itemPosition);
        //Start the EditQuestionActivity class
        startActivityForResult(i, this.THROW_EDIT_QUESTIONS_ACT_REQCODE);
        Log.d("ThrowAddUser", "Exit throwEditQuestionActivity method in the MainActivity class.");
    }//End of throwAddTaskActivity

    private void throwEditCategoryActivity(int _id, int listPosition) {
        Log.d("ThrowEditCat", "Enter throwEditCategoryActivity method in the MainActivity class.");
        //Declare and instantiate a new intent object
        Intent i = new Intent(MainActivity.this, EditCategoryActivity.class);
        i.putExtra(ID_COLUMN, _id);
        i.putExtra("positionInCatList", listPosition);
        //Start the addTaskActivity class
        startActivityForResult(i, THROW_EDIT_CATEGORY_ACT_REQCODE);
        Log.d("ThrowEditCat", "Exit throwEditCategoryActivity method in the MainActivity class.");
    }//End of throwEditCategoryActivity method

    //Method to throw the SelectLogoActivity
    protected void throwSelectNavDrawerBackgroundActivity() {
        Log.d("throwSelectBckActivity", "Enter the throwSelectNavDrawerBackgroundActivity method in the DisplayAccountActivity class.");
        //Declare and instantiate a new intent object
        Intent i = new Intent(this, SelectNavDrawerBckGrndActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        // the current logo data which is sent back if select logo is cancel or updated if new logo has been selected
        i.putExtra("selectedImgPosition", -1);
        i.putExtra("selectedImgLocation", RESOURCES);
        //Start the addTaskActivity and wait for result
        startActivityForResult(i, this.THROW_SELECT_NAVDRAWERBCKGRND_ACT_REQCODE);
        Log.d("throwSelectBckActivity", "Exit the throwSelectNavDrawerBackgroundActivity method in the DisplayAccountActivity class.");
    }//End of throwSelectLogoActivity method


    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "Enter the onActivityResult method in the DisplayAccountActivity class.");
        //Check if result comes from AddAccountActivity
        //Flag to handle nave drawer menu update when a category has been added, deleted or edited
        boolean categoryMenuUpdate = false;
        RecyclerView recyclerView = HomeFragment.getRv();
        RecyclerView.Adapter adapter = null;
        //Get adapter from rRV
        adapter = recyclerView.getAdapter();
        if (requestCode == this.THROW_ADD_ACCOUNT_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from AddAccountActivity (received by MainActivity).");
            //Call generic method to handle item insertion  and update RV accordingly
            this.handleAddItemActivityResult(data,adapter,"accountName",ACCOUNTS_TABLE);
        } else if (requestCode == this.THROW_ADD_ACCOUNT_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from AddAccountActivity (received by MainActivity).");
        } else if (requestCode == THROW_ADD_USERNAME_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from AddUserNameActivity (received by MainActivity).");
            //Call generic method to handle item insertion  and update RV accordingly
            this.handleAddItemActivityResult(data,adapter,"userNameValue",USERNAME_TABLE);
        } else if (requestCode == THROW_ADD_USERNAME_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from AddUserNameActivity (received by MainActivity).");
        } else if (requestCode == THROW_ADD_PSSWRD_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from AddPsswrdActivity (received by MainActivity).");
            //Call generic method to handle item insertion  and update RV accordingly
            this.handleAddItemActivityResult(data,adapter,"psswrdValue",PSSWRD_TABLE);
        } else if (requestCode == THROW_ADD_PSSWRD_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from AddPsswrdActivity (received by MainActivity).");
        } else if (requestCode == THROW_ADD_QUESTION_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from AddAccountActivity (received by MainActivity).");
            //Call generic method to handle item insertion  and update RV accordingly
            this.handleAddItemActivityResult(data,adapter,"questionValue",QUESTION_TABLE);
        } else if (requestCode == THROW_ADD_QUESTION_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from AddQuestionActivity (received by MainActivity).");
        } else if (requestCode == TRHOW_ADD_CATEGORY_REQCODE && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from AddCategoryActivity (received by HomeFragment).");
            this.goodResultDelivered = true;
            categoryMenuUpdate = true;
            this.toastText = data.getExtras().getString("categoryName") + " " + getResources().getString(R.string.catAdded);
        } else if (requestCode == TRHOW_ADD_CATEGORY_REQCODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from AddCategoryActivity received by MainAcitvity.");
        } else if (requestCode == THROW_EDIT_USERNAME_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from EditUserNameActivity (received by MainActivity).");
            //Call generic method to handle item edition and update RV accordingly
            handleEditItemActivityResult(data,"userNameID");
        } else if (requestCode == THROW_EDIT_USERNAME_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from EditUserNameActivity (received by MainActivity).");
        } else if (requestCode == THROW_EDIT_PSSWRD_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from EditPsswrdActivity (received by MainActivity).");
            //Call generic method to handle item edition and update RV accordingly
            handleEditItemActivityResult(data,"psswrdID");
        } else if (requestCode == THROW_EDIT_PSSWRD_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from EditUserNameActivity (received by MainActivity).");
        } else if (requestCode == THROW_EDIT_QUESTIONS_ACT_REQCODE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from EditQuestionActivity (received by MainActivity).");
            //Call generic method to handle item edition and update RV accordingly
            handleEditItemActivityResult(data,"questionID");
        } else if (requestCode == THROW_EDIT_QUESTIONS_ACT_REQCODE && resultCode == RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from EditQuestionActivity (received by MainActivity).");
        } else if (requestCode == THROW_EDIT_ACCOUNT_ACT_REQCODE && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from EditAccountActivity (received by HomeFragment).");
            //Call method to get change type for EditAccountActivity
            changeType = MainActivity.handleEditAccountActivityResult(data);
            //Call MainActivity's method to get the toast text to be displayed based on the type of RV notification type
            toastText = MainActivity.setToastText(data,"accountName",changeType,getResources());
            //Set item position in the RV sent back by EditAccountActivity
            itemPosition = data.getExtras().getInt("position");
            //Set good result variable to display Toast appropriate toast text
            goodResultDelivered = true;
        } else if (requestCode == THROW_EDIT_ACCOUNT_ACT_REQCODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from EditAccountActivity (received by HomeFragment).");
        } else if (requestCode == THROW_EDIT_CATEGORY_ACT_REQCODE && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from EditCategoryActivity received by MainAcitvity.");
            //Set overall good result flag to true and the category update menu flag to true too
            goodResultDelivered = true;
            categoryMenuUpdate = true;
            //Setup propper message for the category edition
            toastText = data.getExtras().getString("categoryName") + " " + getResources().getString(R.string.catUpdated);
        } else if (requestCode == THROW_EDIT_CATEGORY_ACT_REQCODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from EditCategoryActivity received by MainAcitvity.");
        } else if (requestCode == THROW_SELECT_NAVDRAWERBCKGRND_ACT_REQCODE && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", "Received GOOD result from SelectNavDrawerBckGrndActivity received by MainAcitvity.");
            //Get the nadvigationView object
            NavigationView navigationView = findViewById(R.id.nav_view);
            //Get the header view object
            View headerView = navigationView.getHeaderView(0);
            //Set the background with the proper resource file
            headerView.setBackground(getResources().getDrawable(data.getExtras().getInt("selectedImgResourceID"), null));
            //Populate the values to be updated on the applogin table
            ContentValues values = new ContentValues();
            values.put(ID_COLUMN, currentAppLoggin.get_id());
            values.put(PICTUREID_COLUMN, data.getExtras().getInt("selectedImgID"));
            //Call DB method to updated APPLOGIN table with new background selected
            accountsDB.updateTable(APPLOGGIN_TABLE, values);
        } else if (requestCode == THROW_SELECT_NAVDRAWERBCKGRND_ACT_REQCODE && resultCode == Activity.RESULT_CANCELED) {
            Log.d("onActivityResult", "Received BAD result from SelectNavDrawerBckGrndActivity received by MainAcitvity.");
        }else if(requestCode == THROW_UPDATE_APPLOGIN_ACT_REQCODE && resultCode == Activity.RESULT_OK){
            Log.d("onActivityResult", "Received GOOD result from UpdateAppLoginActivity received by MainAcitvity.");
            //Update current appLogin so it matches same data from DB, otherwise it will keep old value during current session
            currentAppLoggin = AppLoggin.extractAppLoggin(accountsDB.getAppLoginCursor(currentAppLoggin.get_id()));
            toastText = "App Login credentials have been successfully updated. Next time you login, use your new credentials.";
            //Display toast when applogin credentials were updated and no need to update RV data set
            displayToast(this, toastText, Toast.LENGTH_LONG, Gravity.CENTER);
        }else if(requestCode == THROW_UPDATE_APPLOGIN_ACT_REQCODE && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult", "Received BAD result from UpdateAppLoginActivity received by MainAcitvity.");
        }//End of if else statement chain to check activity results

        //Check if category menu required updating and result from previous activity is good
        if (categoryMenuUpdate && goodResultDelivered) {
            //Check if toast would be displayed
            //Get the updated list of categories
            this.categoryList = this.accountsDB.getCategoryList();
            //Get the navigation view to access the menu object
            NavigationView navigationView = findViewById(R.id.nav_view);
            //Get the position number of the updated menu item, which is already fixed on EditCategoryActivity
            //for the AlartDialog numbering issue
            int positionInCatList = -1;
            MenuItem menuItem = null;
            if (requestCode == TRHOW_ADD_CATEGORY_REQCODE) {
                positionInCatList = navigationView.getMenu().size() - 1;
                this.updateNavMenu(navigationView.getMenu(), positionInCatList);
            } else if (requestCode == THROW_EDIT_CATEGORY_ACT_REQCODE) {
                positionInCatList = data.getExtras().getInt("positionInCatList");
                //Get the menu item in the same position as the one in the categor list
                menuItem = navigationView.getMenu().getItem(positionInCatList);
                //Create category object pointing to category list position retrieved above
                Category updatedCategory = this.categoryList.get(positionInCatList);
                //Set up the proper name, as this might be updated on previous activity
                menuItem.setTitle(updatedCategory.getName());
                //Set up the proper icon for each category (icon data comes from DB), as this might be updated from previous activity
                idRes = this.getResources().getIdentifier(categoryList.get(positionInCatList).getIcon().getName(), "drawable", this.getPackageName());
                menuItem.setIcon(idRes);
                //Check if  category updated is the one already selected, so RV heading is updated with correct name
                if(navigationView.getMenu().getItem(positionInCatList ).isChecked()){
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle(updatedCategory.getName());
                }
            }//End of if else statement to check Category activity thrown
            //Display Toast to confirm the account has been added
            displayToast(this, toastText, Toast.LENGTH_LONG, Gravity.CENTER);
            //Reset good result boolean flag
            this.goodResultDelivered = false;
        } else {
            //Check if toast would be displayed
            if (this.goodResultDelivered) {
                //Call method to update RV, pass in the item position and set the notify change method to inset a new item in the position passed in
                updateRecyclerViewData(adapter,itemPosition,changeType);
                //As RV to scroll up to the item position in case isn't on display
                recyclerView.scrollToPosition(itemPosition);
                //Display Toast to confirm the account has been added
                displayToast(this, toastText, Toast.LENGTH_LONG, Gravity.CENTER);
                //Reset good result boolean flag
                this.goodResultDelivered = false;
            }//End of if statement to check good result was delivered
        }//End of if else statement that checks if nav drawer menu has to be updated
        Log.d("onActivityResult", "Exit the onActivityResult method in the DisplayAccountActivity class.");
    }//End of onActivityResult method

    //Generic method to handle item insertion into RV
    private void handleAddItemActivityResult(@Nullable Intent data,RecyclerView.Adapter adapter,String key,String dbTable){
        Log.d("handleAddAccRes", "Enter the handleAddItemActivityResult method in the DisplayAccountActivity class.");
        boolean requiresRVUpdate = false;
        //Update RV data set
        //Get current item position in the updated cursor. Use getCursorToUpdateRV with the most up to date data set
        Account addedAccount = null;
        UserName addedUserName = null;
        Psswrd addedPsswrd = null;
        Question addedQuestion = null;
        //Get item position in the current data set to be displayed on the RV
        this.itemPosition = accountsDB.findItemPositionInCursor(getCursorToUpdateRV(adapter),accountsDB.getMaxItemIdInTable(dbTable));
        //Check if key is for Account object
        if(key.equals("accountName")){
            //Retrieve the added account from DB
            addedAccount = accountsDB.getAccountByID(data.getExtras().getInt("accountID"));
            //Call method to check if account is congruent with data set criteria for RV
            if(isAccountCongruentWithRVData(addedAccount)){
                this.changeType = NotifyChangeType.ITEM_INSERTED;
                requiresRVUpdate = true;
            }else{
                //If not congruent, the data set should not be changed, so set result deliverd to false
                //That way the RV will not be updated.
                this.goodResultDelivered = false;
                //Display the toast manually as
                MainActivity.displayToast(this,data.getExtras().getString("accountName")+ " "+ getString(R.string.accountAdded),Toast.LENGTH_LONG,Gravity.BOTTOM);
            }//End of if else statement to check added item is congruent with RV
        //Check if it's user name object
        }else if(key.equals("userNameValue")){
            //Retrieve the added user name from DB
            addedUserName = accountsDB.getUserNameByID(data.getExtras().getInt("userNameID"));
            if(!(isSearchFilter && !cryptographer.decryptText(addedUserName.getValue(), new IvParameterSpec(addedUserName.getIv())).equals(lastSearchText))){
                this.changeType = NotifyChangeType.ITEM_INSERTED;
                requiresRVUpdate = true;
            }else{
                //If not congruent, the data set should not be changed, so set result delivered to false
                //That way the RV will not be updated.
                this.goodResultDelivered = false;
                //Display the toast manually as
                MainActivity.displayToast(this,data.getExtras().getString("userNameValue")+ " "+ getString(R.string.userNameAdded),Toast.LENGTH_LONG,Gravity.BOTTOM);
            }//End of if else statement to check added item is congruent with RV
        //Check if it's psswrd object
        }else if(key.equals("psswrdValue")){
            //Retrieve the added user name from DB
            addedPsswrd = accountsDB.getPsswrdByID(data.getExtras().getInt("psswrdID"));
            if(!(isSearchFilter && !cryptographer.decryptText(addedPsswrd.getValue(), new IvParameterSpec(addedPsswrd.getIv())).equals(lastSearchText))){
                this.changeType = NotifyChangeType.ITEM_INSERTED;
                requiresRVUpdate = true;
            }else{
                //If not congruent, the data set should not be changed, so set result deliverd to false
                //That way the RV will not be updated.
                this.goodResultDelivered = false;
                //Display the toast manually as
                MainActivity.displayToast(this,data.getExtras().getString("psswrdValue")+ " "+ getString(R.string.psswrdAdded),Toast.LENGTH_LONG,Gravity.BOTTOM);
            }//End of if else statement to check added item is congruent with RV
        //Check if it's question object
        }else if(key.equals("questionValue")){
            //Retrieve the added user name from DB
            addedQuestion = accountsDB.getQuestionByID(data.getExtras().getInt("questionID"));
            if(!(isSearchFilter && !addedQuestion.getValue().toLowerCase().contains(lastSearchText.toLowerCase()))){
                this.changeType = NotifyChangeType.ITEM_INSERTED;
                requiresRVUpdate = true;
            }else{
                //If not congruent, the data set should not be changed, so set result deliverd to false
                //That way the RV will not be updated.
                this.goodResultDelivered = false;
                //Display the toast manually as
                MainActivity.displayToast(this,data.getExtras().getString("questionValue")+ " "+ getString(R.string.questionAdded),Toast.LENGTH_LONG,Gravity.BOTTOM);
            }//End of if else statement to check added item is congruent with RV
        }//End of if else chain to check the type of object added
        //Final check to set boolean flag for RV updated and Toast display
        if(requiresRVUpdate){
            this.toastText = setToastText(data,key,changeType,getResources());
            this.goodResultDelivered = true;
        }//End of if statement to check RV update flag
        Log.d("handleAddAccRes", "Exit the handleAddItemActivityResult method in the DisplayAccountActivity class.");
    }//End of handleAddItemActivityResult method

    //Generic method to handle item edition into RV
    private void handleEditItemActivityResult(@Nullable Intent data,String idKey){
        Log.d("handleAddAccRes", "Enter the handleAddItemActivityResult method in the DisplayAccountActivity class.");
        //Check data back from Activity to see if item was deleted or just changed to set up proper message text and notify change type
        if (data.getExtras().getBoolean("itemDeleted")) {
            //If no actual account id is returned, means the account was deleted
            //Set the NotifyChangeType variable to Item removed
            this.changeType = NotifyChangeType.ITEM_REMOVED;
            //Set text to item removed
            this.toastText = setToastText(data,"",this.changeType,getResources());// data.getExtras().getString("itemDeletedName") + " " + getResources().getString(R.string.userNameDeleted);
        } else {
            //In case user name changed flag is returned, get the user name from DB
            Object editedItem = null;
            int itemID = data.getExtras().getInt(idKey);
            String toastKey = "";
            switch(idKey){
                case "userNameID":
                    editedItem = accountsDB.getUserNameByID(itemID);
                    if(editedItem != null){
                        this.changeType = getNotifyChangeType((UserName) editedItem);
                    }
                    toastKey = "userNameValue";
                    break;
                case "psswrdID":
                    editedItem = accountsDB.getPsswrdByID(itemID);
                    if(editedItem != null){
                        this.changeType = getNotifyChangeType((Psswrd) editedItem);
                    }
                    toastKey = "psswrdValue";
                    break;
                case "questionID":
                    editedItem = accountsDB.getQuestionByID(itemID);
                    if(editedItem != null){
                        this.changeType = getNotifyChangeType((Question) editedItem);
                    }
                    toastKey = "questionValue";
                    break;
                default:
                    this.changeType = NotifyChangeType.DATA_SET_CHANGED;
            }//End of switch statement
            //Set text to display Toast to confirm the user name has been UPDATED
            this.toastText = setToastText(data,toastKey,changeType,getResources());// data.getExtras().getString("userNameValue") + " " + getResources().getString(R.string.userNameUpdated);
        }//End of if else statement to check the boolean value retrieved from extra data
        //Set item position in the RV
        this.itemPosition = data.getExtras().getInt("position");
        //Set variable to display Toast and update RV
        this.goodResultDelivered = true;
        Log.d("handleAddAccRes", "Exit the handleAddItemActivityResult method in the DisplayAccountActivity class.");
    }//End of handleAddItemActivityResult method

    //Specific method to handle account edition and RV update
    public static NotifyChangeType handleEditAccountActivityResult(@Nullable Intent data){
        Log.d("handleEditAccRes", "Enter the handleEditAccountActivityResult method in the DisplayAccountActivity class.");
        NotifyChangeType changeType;
        //Check type of edit returned byt EditAccountActivity: Delete account or edit it
        if (data.getExtras().getInt("accountID") == -1) {
            //If no actual account id is returned, means the account was deleted
            //Set the NotifyChangeType variable to Item removed
            changeType = NotifyChangeType.ITEM_REMOVED;
        } else {
            //In case actual account id is returned, get the account from DB
            Account editedAccount = accountsDB.getAccountByID(data.getExtras().getInt("accountID"));
            //Check if account not null,set up the NotifyChangeType variable
            if(editedAccount != null){
                //To define what type of notify change, call method that will determine it
                changeType = getNotifyChangeType(editedAccount);
            }else{
                //Set default notify change type to Data set change
                changeType = NotifyChangeType.DATA_SET_CHANGED;
            }
        }//End of if else statement to check account id
        Log.d("handleEditAccRes", "Exit the handleEditAccountActivityResult method in the DisplayAccountActivity class.");
        return changeType;
    }//End of handleEditAccountActivityResult method

    //Method to setup ToastText for onActivityResult method when updating  RV accordingly
    public static String setToastText(@Nullable Intent data,String key,NotifyChangeType changeType, Resources res){
        Log.d("handleEditAccRes", "Enter the setToastText method in the MainActivity class.");
        String toastText= "";
        //Check the key text since when method is called from delete item section, the key must change to itemDeletedName
        if(key.equals("accountName") || key.equals("userNameValue") || key.equals("psswrdValue") || key.equals("questionValue")){
            toastText = data.getExtras().getString(key);
        }else{
            toastText = data.getExtras().getString("itemDeletedName");
            key = data.getExtras().getString("itemDeletedType");
        }//End of if else statement to check key text

        //Check key value to set up Toast text accordingly based on type of item and RV type of update
        switch (key){
            case "accountName":
                Log.d("handleEditAccRes", "accountName key used in the setToastText method in the MainActivity class.");
                if(changeType.equals(NotifyChangeType.ITEM_REMOVED)){
                    //Set text to display Toast to confirm the account has been DELETED
                    toastText += " " + res.getString(R.string.accountDeleted);
                    //Set text to display Toast to confirm the account has been ADDED
                }else if(changeType.equals(NotifyChangeType.ITEM_INSERTED)){
                    toastText += " " + res.getString(R.string.accountAdded);
                    //Set text to display Toast to confirm the account has been UPDATED
                }else if(changeType.equals(NotifyChangeType.ITEM_CHANGED)){
                    toastText += " " + res.getString(R.string.accountUpdated);
                }
                break;
            case "userNameValue":
                Log.d("handleEditAccRes", "userNameValue key used in the setToastText method in the MainActivity class.");
                if(changeType.equals(NotifyChangeType.ITEM_REMOVED)){
                    //Set text to display Toast to confirm the user name has been DELETED
                    toastText += " " + res.getString(R.string.userNameDeleted);
                    //Set text to display Toast to confirm the user name has been ADDED
                }else if(changeType.equals(NotifyChangeType.ITEM_INSERTED)){
                    toastText += " " + res.getString(R.string.userNameAdded);
                    //Set text to display Toast to confirm the user name has been UPDATED
                }else if(changeType.equals(NotifyChangeType.ITEM_CHANGED)){
                    toastText += " " + res.getString(R.string.userNameUpdated);
                }
                break;
            case "psswrdValue":
                Log.d("handleEditAccRes", "psswrdValue key used in the setToastText method in the MainActivity class.");
                if(changeType.equals(NotifyChangeType.ITEM_REMOVED)){
                    //Set text to display Toast to confirm the psswrd has been DELETED
                    toastText += " " + res.getString(R.string.psswrdDeleted);
                    //Set text to display Toast to confirm the psswrd has been ADDED
                }else if(changeType.equals(NotifyChangeType.ITEM_INSERTED)){
                    toastText += " " + res.getString(R.string.psswrdAdded);
                    //Set text to display Toast to confirm the psswrd has been UPDATED
                }else if(changeType.equals(NotifyChangeType.ITEM_CHANGED)){
                    toastText += " " + res.getString(R.string.psswrdUpdated);
                }
                break;
            case "questionValue":
                Log.d("handleEditAccRes", "questionValue key used in the setToastText method in the MainActivity class.");
                if(changeType.equals(NotifyChangeType.ITEM_REMOVED)){
                    //Set text to display Toast to confirm the question has been DELETED
                    toastText += " "  + res.getString(R.string.questionDeleted);
                    //Set text to display Toast to confirm the psswrd has been ADDED
                }else if(changeType.equals(NotifyChangeType.ITEM_INSERTED)){
                    toastText += " " + res.getString(R.string.questionAdded);
                    //Set text to display Toast to confirm the psswrd has been UPDATED
                }else if(changeType.equals(NotifyChangeType.ITEM_CHANGED)){
                    toastText += " " + res.getString(R.string.questionUpdated);
                }
                break;
        }//End of switch statement
        Log.d("handleEditAccRes", "Enter the setToastText method in the DisplayAccountActivity class.");
        return toastText;
    }//End of setToastText method

    //Method to check if account passed in is congruent with current data displayed on RV
    public static boolean isAccountCongruentWithRVData(Account account){
        Log.d("handleAddAccRes", "Enter|Exit the handleAddItemActivityResult method in the DisplayAccountActivity class.");
        //false if any of the following conditions is met:
        //1.-The current category is Fav, but the account isn't fav
        //2.-There's a search filter for user name and current account user name doesn't match filter
        //3.-There's a search filter for password and current account password doesn't match filter
        //4.-There's a search filter for account name and current account name doesn't contain the filter text
        //5.- Current category is other than home or fav and account category isn't the same as the current category
        return !((currentCategory == favCategory && !account.isFavorite())
                || (isSearchUserNameFilter && !cryptographer.decryptText(account.getUserName().getValue(),new IvParameterSpec(account.getUserName().getIv())).equals(lastSearchText))
                || (isSearchPsswrdFilter && !cryptographer.decryptText(account.getPsswrd().getValue(),new IvParameterSpec(account.getPsswrd().getIv())).equals(lastSearchText))
                ||(isSearchFilter && !isSearchUserNameFilter && !isSearchPsswrdFilter && !account.getName().toLowerCase().contains(lastSearchText.toLowerCase()))
                ||(currentCategory != homeCategory && currentCategory != favCategory && currentCategory != account.getCategory()));
    }//End of isAccountCongruent method

    //Method to get the notify change enum for edited accounts
    public static NotifyChangeType getNotifyChangeType(Account editedAccount){
        Log.d("getNotifyChangeType", "Enter getNotifyChangeType method in the MainActivity class.");
        //Declare and initialize the chaneType variable to be returned by method
        NotifyChangeType changeType = null;
        //Check if any of the following three conditions is met:
        //Current category is favorites and the edited account isn't anymore a favorite account
        //UserName search filter active and the account user name doesn't match the search criteria anymore
        //Password search filter active and the account password doesn't match the search criteria anymore
        //Account name search filter active and the account name doesn't match the search criteria anymore
            if(!isAccountCongruentWithRVData(editedAccount)){
                changeType = MainActivity.NotifyChangeType.ITEM_REMOVED;
            Log.d("getNotifyChangeType", "ITEM_REMOVED selected in getNotifyChangeType method in the MainActivity class for ."+ editedAccount.getName());
        }else {
            //Any other edition, the account item must be notified as changed
            changeType = MainActivity.NotifyChangeType.ITEM_CHANGED;
            Log.d("getNotifyChangeType", "ITEM_CHANGED selected in getNotifyChangeType method in the MainActivity class for ."+ editedAccount.getName());
        }//End of if else statement that checks the specific cases where change type requires to be set to item removed
        Log.d("getNotifyChangeType", "Exit getNotifyChangeType method in the MainActivity class.");
        return changeType;
    }//End of getNotifyChangeType method

    //Method to get the notify change enum for edited user names
    public static NotifyChangeType getNotifyChangeType(UserName editedUserName) {
        Log.d("getNotifyChangeType", "Enter getNotifyChangeType method ifor UserName class n the MainActivity class.");
        //Declare and initialize the chaneType variable to be returned by method
        NotifyChangeType changeType = null;
        if (isSearchFilter && !cryptographer.decryptText(editedUserName.getValue(), new IvParameterSpec(editedUserName.getIv())).equals(lastSearchText)) {
            changeType = NotifyChangeType.ITEM_REMOVED;
            Log.d("getNotifyChangeType", "ITEM_REMOVED selected in getNotifyChangeType method for UserName class in the MainActivity class for user name with id:  " + editedUserName.get_id());
        }else{
            //Any other edition, the account item must be notified as changed
            changeType = MainActivity.NotifyChangeType.ITEM_CHANGED;
            Log.d("getNotifyChangeType", "ITEM_CHANGED selected in getNotifyChangeType method for UserName class in the MainActivity class for user name with id: "+ editedUserName.get_id());
        }//End of if else statement to check cases where notify change type must be set to item removed
        Log.d("getNotifyChangeType", "Exit getNotifyChangeType method for UserName class in the MainActivity class.");
        return changeType;
    }//End of getNotifyChangeType method for UserName class

    //Method to get the notify change enum for edited passwords
    public static NotifyChangeType getNotifyChangeType(Psswrd editedPsswrd) {
        Log.d("getNotifyChangeType", "Enter getNotifyChangeType method for Psswrd class in the MainActivity class.");
        //Declare and initialize the chaneType variable to be returned by method
        NotifyChangeType changeType = null;
        if (isSearchFilter && !cryptographer.decryptText(editedPsswrd.getValue(), new IvParameterSpec(editedPsswrd.getIv())).equals(lastSearchText)) {
            changeType = NotifyChangeType.ITEM_REMOVED;
            Log.d("getNotifyChangeType", "ITEM_REMOVED selected in getNotifyChangeType method for Psswrd class in the MainActivity class for user name with id:  " + editedPsswrd.get_id());
        }else{
            //Any other edition, the account item must be notified as changed
            changeType = MainActivity.NotifyChangeType.ITEM_CHANGED;
            Log.d("getNotifyChangeType", "ITEM_CHANGED selected in getNotifyChangeType method for Psswrd class in the MainActivity class for user name with id: "+ editedPsswrd.get_id());
        }//End of if else statement to check cases where notify change type must be set to item removed
        Log.d("getNotifyChangeType", "Exit getNotifyChangeType method for Psswrd class in the MainActivity class.");
        return changeType;
    }//End of getNotifyChangeType method for UserName class

    //Method to get the notify change enum for edited questions
    public static NotifyChangeType getNotifyChangeType(Question editedQuestion) {
        Log.d("getNotifyChangeType", "Enter getNotifyChangeType method for Psswrd class in the MainActivity class.");
        //Declare and initialize the chaneType variable to be returned by method
        NotifyChangeType changeType = null;
        if (isSearchFilter && !editedQuestion.getValue().toLowerCase().contains(lastSearchText.toLowerCase())) {
            changeType = NotifyChangeType.ITEM_REMOVED;
            Log.d("getNotifyChangeType", "ITEM_REMOVED selected in getNotifyChangeType method for Psswrd class in the MainActivity class for user name with id:  " + editedQuestion.get_id());
        }else{
            //Any other edition, the account item must be notified as changed
            changeType = MainActivity.NotifyChangeType.ITEM_CHANGED;
            Log.d("getNotifyChangeType", "ITEM_CHANGED selected in getNotifyChangeType method for Psswrd class in the MainActivity class for user name with id: "+ editedQuestion.get_id());
        }//End of if else statement to check cases where notify change type must be set to item removed
        Log.d("getNotifyChangeType", "Exit getNotifyChangeType method for Psswrd class in the MainActivity class.");
        return changeType;
    }//End of getNotifyChangeType method for UserName class


    //Method to display a generic new Dialog Alert view from any activity.
    public static AlertDialog.Builder displayAlertDialogWithInput(Context context, EditText inputField, String title, String message, String hint) {
        Log.d("displayAlertDialog", "Enter displayAlertDialog method in the MainActivity class.");
        //final EditText inputField = new EditText(context);
        if (inputField != null && hint != null) {
            inputField.setText("");
            inputField.setHint(hint);
        }//End of if statement to check the input field and the hint aren't null
        Log.d("displayAlertDialog", "Enter displayAlertDialog method in the MainActivity class.");
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(inputField)
                .setNegativeButton(R.string.cancel, null);
    }//End of displayAlertDialog

    //Method to display a generic new Dialog Alert view from any activity.
    public static AlertDialog.Builder displayAlertDialogNoInput(Context context, String title, String message) {
        Log.d("displayAlertDialog", "Enter/Exit displayAlertDialog method in the MainActivity class.");
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, null);
    }//End of displayAlertDialog

    //Getter and setter methods
    public static Cryptographer getCryptographer() {
        return cryptographer;
    }

    public static AccountsDB getAccountsDB() {
        return accountsDB;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static String getRESOURCES() {
        return RESOURCES;
    }

    public static Icon getMyPsswrdSecureLogo() {
        return myPsswrdSecureLogo;
    }

    public static ArrayList<QuestionList> getListOfQuestionLists() {
        return listOfQuestionLists;
    }

    public static int getThrowAddQuestionActReqcode() {
        return THROW_ADD_QUESTION_ACT_REQCODE;
    }

    public static int getThrowEditAccountActReqcode() {
        return THROW_EDIT_ACCOUNT_ACT_REQCODE;
    }

    public int getTHROW_EDIT_USERNAME_ACT_REQCODE() {
        return THROW_EDIT_USERNAME_ACT_REQCODE;
    }

    public static int getThrowAddUsernameActReqcode() {
        return THROW_ADD_USERNAME_ACT_REQCODE;
    }

    public static int getThrowAddPsswrdActReqcode() {
        return THROW_ADD_PSSWRD_ACT_REQCODE;
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

    public static String getUserNameIvColumn() {
        return USER_NAME_IV_COLUMN;
    }

    public static String getPsswrdIvColumn() {
        return PSSWRD_IV_COLUMN;
    }

    public static String getQuestionListIdColumn() {
        return QUESTION_LIST_ID_COLUMN;
    }

    public static String getCategoryIdColumn() {
        return CATEGORY_ID_COLUMN;
    }

    public static String getQuestionIdColumn() {
        return QUESTION_ID_COLUMN;
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

    public static String getAnswerIdColumn() {
        return ANSWER_ID_COLUMN;
    }

    public static String getIsSearchFilterColumn() {
        return IS_SEARCH_FILTER_COLUMN;
    }

    public static String getIsSearchUserFilterColumn() {
        return IS_SEARCH_USER_FILTER_COLUMN;
    }

    public static String getIsSearchPsswrdFilterColumn() {
        return IS_SEARCH_PSSWRD_FILTER_COLUMN;
    }

    public static String getLastSearchTextColumn() {
        return LAST_SEARCH_TEXT_COLUMN;
    }

    public static String getIsSortFilterColumn() {
        return IS_SORT_FILTER_COLUMN;
    }

    public static String getCurrentSortFilterColumn() {
        return CURRENT_SORT_FILTER_COLUMN;
    }

    public static AppLoggin getCurrentAppLoggin() {
        return currentAppLoggin;
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

    public static String getNameColumn() {
        return NAME_COLUMN;
    }

    public static String getIconLocationColumn() {
        return ICON_LOCATION_COLUMN;
    }

    public static String getIconIsSelectedColumn() {
        return ICON_IS_SELECTED_COLUMN;
    }

    public static String getIsFavoriteColumn() {
        return IS_FAVORITE_COLUMN;
    }

    public static String getDateCreatedColumn() {
        return DATE_CREATED_COLUMN;
    }

    public static String getDateChangeColumn() {
        return DATE_CHANGE_COLUMN;
    }

    public static String getValueColumn() {
        return VALUE_COLUMN;
    }

    public static String getMessageColumn() {
        return MESSAGE_COLUMN;
    }

    public static String getPictureidColumn() {
        return PICTUREID_COLUMN;
    }

    public static String getInitVectorColumn() {
        return INIT_VECTOR_COLUMN;
    }

    public static int getCurrentTabID() {
        return currentTab;
    }

    public static Category getCurrentCategory() {
        return currentCategory;
    }

    public static String getCurrentCategoryIdColumn() {
        return CURRENT_CATEGORY_ID_COLUMN;
    }

    public static String getCurrentTabColumn() {
        return CURRENT_TAB_COLUMN;
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

    public static String getAccountsLogos() {
        return ACCOUNTS_LOGOS;
    }

    public static String getNavDrawerBckgrnds() {
        return NAV_DRAWER_BCKGRNDS;
    }

    public static TabLayout getTabLayout() {
        return tabLayout;
    }

    public static boolean isSearchFilter() {
        return isSearchFilter;
    }

    public String getLastSearchText() {
        return lastSearchText;
    }

    public static boolean isSearchUserNameFilter() {
        return isSearchUserNameFilter;
    }

    public static boolean isSearchPsswrdFilter() {
        return isSearchPsswrdFilter;
    }

    public static boolean isFirstRun() {
        return isFirstRun;
    }

    public static boolean isAutoLogOutActive() {
        return isAutoLogOutActive;
    }
    
    public static boolean isPushNotificationSent() {
        return isPushNotificationSent;
    }

    public static void setPushNotificationSent(boolean isPushNotificationSent) {
        MainActivity.isPushNotificationSent = isPushNotificationSent;
    }

    public static int getExpiredPasswordAccountID() {
        return expiredPasswordAccountID;
    }

    public static void setExpiredPasswordAccountID(int expiredPasswordAccountID) {
        MainActivity.expiredPasswordAccountID = expiredPasswordAccountID;
    }

    public int getTHROW_UPDATE_APPLOGIN_ACT_REQCODE() {
        return THROW_UPDATE_APPLOGIN_ACT_REQCODE;
    }

    public static NotificationManagerCompat getNotificationManager() {
        return notificationManager;
    }

    public static void setNotificationManager(NotificationManagerCompat notificationManager) {
        MainActivity.notificationManager = notificationManager;
    }

    public static NotificationCompat.Builder getNotificationBuilder() {
        return notificationBuilder;
    }

    public static void setNotificationBuilder(NotificationCompat.Builder notificationBuilder) {
        MainActivity.notificationBuilder = notificationBuilder;
    }

    public int getTHROW_ADD_ACCOUNT_ACT_REQCODE() {
        return THROW_ADD_ACCOUNT_ACT_REQCODE;
    }

    public static boolean isLoggedOut() {
        return isLoggedOut;
    }

    public static void setIsLoggedOut(boolean isLoggedOut) {
        MainActivity.isLoggedOut = isLoggedOut;
    }

    //Generic method to display a toast with control vocer the duritation length and the gravity  position
    public static void displayToast(Context context, String text, int toastLength, int gravity) {
        Log.d("displayToast", "Enter displayToast method in the MainActivity class.");
        Toast toast = Toast.makeText(context, text, toastLength);
        toast.setGravity(gravity, 0, 0);
        toast.show();
        Log.d("displayToast", "Exit displayToast method in the MainActivity class.");
    }//End of displayToast method

    //Method to set account logo resource image
    public static void setAccountLogoImageFromRes(ImageView imgLogo, Context context, String iconResName) {
        Log.d("setAccLogoFromRes", "Enter setAccountLogoImageFromRes method in the MainActivity class.");
        //Extract all the logos from the app resources
        int idRes;
        Resources r = context.getResources();
        idRes = r.getIdentifier(iconResName, "drawable", context.getPackageName());
        imgLogo.setImageResource(idRes);
        Log.d("setAccLogoFromRes", "Exit setAccountLogoImageFromRes method in the MainActivity class.");
    }//End of setAccountLogoImageFromRes method

    //Method to set account logo resource image
    public static void setAccountLogoImageFromGallery(ImageView imgLogo, String uri) {
        Log.d("setAccLogoFromGal", "Enter setAccountLogoImageFromGallery method in the MainActivity class.");
        //Extract all the logos from the app resources
        imgLogo.setImageURI(Uri.parse(uri));
        Log.d("setAccLogoFromGal", "Exit setAccountLogoImageFromGallery method in the MainActivity class.");
    }//End of setAccountLogoImageFromGallery method

    //Method to be setup within OnClick event listener for the star icon within each Account item
    public static boolean toggleIsFavorite(View v) {
        Log.d("toggleIsFavorite", "Enter toggleIsFavorite method in the MainActivity class.");
        //Declare and initialize vatiables to be used and returned by the method
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
        if (account.isFavorite()) {
            account.setFavorite(false);
        } else {
            account.setFavorite(true);
        }//End of if else statement that checks the isFavorite attribute state
        //Call DB method to update the account item in the Accounts table
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, account.get_id());
        values.put(IS_FAVORITE_COLUMN, account.isFavorite());
        //Call DB method to update the accounts table
        if (accountsDB.updateTable(ACCOUNTS_TABLE, values)) {
            //Check if current category is the favorites category to setup corrent notifyChange action for RV update
            if(currentCategory.equals(favCategory)){
                updateRecyclerViewData(accountAdapter,adapterPosition,NotifyChangeType.ITEM_REMOVED);
            }else{
                updateRecyclerViewData(accountAdapter,adapterPosition,NotifyChangeType.ITEM_CHANGED);
            }//End of if else statement to check current category
            //set boolean flag to be returned to true
            update = true;
        } else {
            //Otherwise, prompt the user about DB problem
            MainActivity.displayToast(v.getContext(),v.getContext().getString(R.string.accountUpdateError),Toast.LENGTH_SHORT,Gravity.CENTER);
        }//End of if else statement to check the item was updated
        Log.d("toggleIsFavorite", "Exit toggleIsFavorite method in the MainActivity class.");
        return update;
    }//End of toggleIsFavorite method

    //Method to load ad picture from gallery app
    public static void loadPictureFromGallery(Intent intent) {
        Log.d("LoadGalPicture", "Enter loadPictureFromGallery method in the MainActivity class.");
        //Check SDK version
        if (Build.VERSION.SDK_INT < 19) {
            //Log the current verison
            Log.i("Build.VERSION", "< 19");
            //Initialize the intent object and set it up for calling the Gallery app
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //startActivityForResult(intent, RESULT_PROFILE_IMAGE_GALLERY);
        } else {
            //Log the current version
            Log.i("Build.VERSION", ">= 19");
            //Initialize the intent object and set it up for calling the Gallery app
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }//End of if else statement that checks the SDK version
        intent.setType("image/*");
        Log.d("LoadGalPicture", "Exit loadPictureFromGallery method in the MainActivity class.");
    }//End of loadPicture method

    ////Method To take a picture via intent
    public static void loadPictureFromCamera(Intent intent, Activity activity) {
        Log.d("LoadCamPicture", "Enter loadPictureFromCamera method in the MainActivity class.");
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //Check the PackageManager is not null
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            uriCameraImage = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCameraImage);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= 19) {
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            }
        } else {
            //Display error message
            MainActivity.displayToast(activity, activity.getString(R.string.errorLoadCamera), Toast.LENGTH_LONG, Gravity.BOTTOM);
        }//End of if else statement
        Log.d("LoadCamPicture", "Exit loadPictureFromCamera method in the MainActivity class.");
    }//End of loadPicture method

    //Method to display alert dialog to request permission for access rights
    public static void permissionRequest(final String permit, String justify, final int requestCode, final Activity activity) {
        Log.d("permissionRequest", "Enter permissionRequest method in the MainActivity class.");
        //Check the permission request needs formal explanation
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permit)) {
            //Display alert with justification about why permit is necessary
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.generalPermitRqst)
                    .setMessage(justify)
                    .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Call method to request permission
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permit}, requestCode);
                        }
                    })
                    .show();
        } else {
            //Otherwise, proceed to request permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{permit}, requestCode);
        }//End of if else statement to check the permission request must be displayed
        Log.d("permissionRequest", "Exit permissionRequest method in the MainActivity class.");
    }//End of permissionRequest method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    private void updateNavMenu(final Menu navMenu, int startPosition) {
        Log.d("Ent_UpdateNaveMenu", "Enter the updateNavMenu method in MainActivity class.");
        if (startPosition == INDEX_TO_GET_LAST_TASK_LIST_ITEM) {
            //Set up onclick listeners for the first two items (home and favorites, which cannot be removed)
            navMenu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //Since the navigation item controls the Home menu item, it's necessary to overwrite it's behaviour and set the home item as selected
                    item.setChecked(true);
                    item.setCheckable(true);
                    MenuItem previousItem = navMenu.findItem(currentCategory.get_id());
                    //Set the previous menu item clicked on as the one as not selected
                    if (previousItem != null && previousItem.getItemId() != item.getItemId()) {
                        previousItem.setChecked(false);
                        previousItem.setCheckable(false);
                    }
                    //When home button is clicked, the transition to HomeFragment is controlled via navigation
                    //But the current category still need to be set to Home category
                    currentCategory = categoryList.get(0);
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    toolbar.setTitle(R.string.menu_home);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    //Close the drawer and display the HomeFragment which will load proper data based on the currentCategory variable
                    drawer.closeDrawer(Gravity.LEFT);
                    clearSearchFilter();
                    return updateCategoryInAppState();
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
                    if (previousItem != null && previousItem.getItemId() != item.getItemId()) {
                        previousItem.setChecked(false);
                        previousItem.setCheckable(false);
                    }
                    //When home button is clicked, the transition to HomeFragment is controlled via navigation
                    //But the current category still need to be set to Home category
                    currentCategory = categoryList.get(1);
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    toolbar.setTitle(R.string.menu_favorites);
                    //Get the drawer from layout
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    //Close the drawer and display the HomeFragment which will load proper data based on the currentCategory variable
                    drawer.closeDrawer(Gravity.LEFT);
                    clearSearchFilter();
                    return updateCategoryInAppState();
                }//End of onMenuItemClick method
            });//End of setOnMenuItemClickListener method call
        }//End of if statement that check start position variable
        //Declare and initialize variables to be used during method
        //int to store each menu item order in the menu
        int order = 0;

        //Get the nav controller to so HomeFragment navigation can be possible
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //Iterate through the category list (skipping first two categories: Home and Favorites) so each category menu item
        //can be added to Nav drawer menu
        while (startPosition < categoryList.size()) {
            //set up new item's order in the menu
            order = navMenu.getItem(navMenu.size() - INDEX_TO_GET_LAST_TASK_LIST_ITEM).getOrder() + 1;
            //Add the new item to the menu
            //Declare and instantiate an int to hold the string id from resources and a String variable to hold the actual category name
            int textID = getResources().getIdentifier(categoryList.get(startPosition).getName(), "string", getPackageName());
            String categoryName = "";
            //If textID is 0, means it's not stored in the app resources, which means it won't be translated but it will be displayed as saved on DB
            if (textID > 0) {
                //If res id number exists, set the category name as per the string text, not the string ID
                categoryName = getResources().getString(textID);
            } else {
                //In the case of not being a resource, print the text retrieved from DB
                categoryName = categoryList.get(startPosition).getName();
            }//End of if else statement
            navMenu.add(R.id.categoryListMenu, categoryList.get(startPosition).get_id(), order, categoryName);
            //Create menu item object so it can be accessed and modified
            final MenuItem newItem = navMenu.getItem(navMenu.size() - INDEX_TO_GET_LAST_TASK_LIST_ITEM);
            //Set up the proper icon for each category (icon data comes from DB)
            idRes = this.getResources().getIdentifier(categoryList.get(startPosition).getIcon().getName(), "drawable", this.getPackageName());
            newItem.setIcon(idRes);
            //Set up the behaviour when category menu item is clicked on
            newItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d("onMenuItemClick", "Enter the onMenuItemClick method defined for each category menu item in the MainActivity class.");
                    MenuItem homeItem = navMenu.getItem(0);
                    //Set proper variables for the HomeFragment to handle the correct accounts list to be displayed: All categories, favorites or a specific category
                    currentCategory = getCategoryInListByID(item.getItemId());
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                    //Ask nav controller to load the HomeFragment class
                    navController.navigate(R.id.nav_home);
                    Toolbar toolbar = findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    toolbar.setTitle(item.getTitle());
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
                    clearSearchFilter();
                    Log.d("onMenuItemClick", "Exit the onMenuItemClick method defined for each category menu item in the MainActivity class.");
                    return updateCategoryInAppState();
                }//End of onMenuItemClick method
            });//End of setOnMenuItemClickListener method call
            startPosition++;
        }//End of while loop
        Log.d("Ext_UpdateNaveMenu", "Exit the updateNavMenu method in MainActivity class.");
    }//End of updateNavMenu method

    //Method to give nav drawer lower menu actual functionality for adding, deleting and editing a category
    private void setUpLowerCategoryMenu(final Menu navMenu) {
        Log.d("setUpLowerCategoryMenu", "Enter the setUpLowerCategoryMenu method in MainActivity class.");
        //Get the add category button and assign onclick event listener
        navMenu.findItem(R.id.nav_addCategory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Call generic method to start an activity, AddCategoryActivity in this case
                throwActivityNoExtras(MainActivity.this,AddCategoryAcitivity.class,TRHOW_ADD_CATEGORY_REQCODE);
                return false;
            }//End of onMenuItemClick method
        });//End of setOnMenuItemClickListener method call
        //Get the edit category button and assign onclick event listener
        navMenu.findItem(R.id.nav_editCategory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final int[] selectedCategoryID = {0};
                final int[] positionInList = {0};
                //Iterate through the category list to transform into a charsequence list
                final CharSequence[] categories = new CharSequence[categoryList.size() - INDEX_TO_GET_LAST_TASK_LIST_ITEM];
                //another one to hold the isChecked attribute
                //final boolean[] editableCategories = new boolean[categoryList.size()-INDEX_TO_GET_LAST_TASK_LIST_ITEM];
                //For loop to populate the char-sequence array with the category names coming from category list
                for (int i = INDEX_TO_GET_LAST_TASK_LIST_ITEM; i < categoryList.size(); i++) {
                    //For each item in the list, extract name and save it in the string array
                    int textID = getResources().getIdentifier(categoryList.get(i).getName(), "string", getPackageName());
                    CharSequence categoryName = "";
                    //Get the name from the cursor
                    if (textID > 0) {
                        //If res id number exists, set the category name as per the string text, not the string ID
                        categoryName = getResources().getString(textID);
                    } else {
                        //In the case of not being a resource, print the text retrieved from DB
                        categoryName = MainActivity.getCategoryList().get(i).getName();
                    }//End of if else statement
                    //String categoryName = categoryList.get(i).getName();
                    //Save the name into the array to be passed into the AlertDialog constructor
                    categories[i - INDEX_TO_GET_LAST_TASK_LIST_ITEM] = categoryName;
                    //Set the isChecked to false for all the categories
                    //editableCategories[i]= false;
                }//End of for loop to populate the taskList array
                //Create a dialog box to display the grocery types
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.editCategoryTitle))
                        .setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positionInList[0] = which;
                                selectedCategoryID[0] = categoryList.get(which + INDEX_TO_GET_LAST_TASK_LIST_ITEM).get_id();
                            }
                        })
                        .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (positionInList[0] == 0) {
                                    selectedCategoryID[0] = categoryList.get(INDEX_TO_GET_LAST_TASK_LIST_ITEM).get_id();
                                }
                                throwEditCategoryActivity(selectedCategoryID[0], positionInList[0]);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
                return false;
            }//End of onMenuItemClick method
        });//End of setOnMenuItemClickListener method call
        //Get the delete category button and assign onclick event listener
        navMenu.findItem(R.id.nav_deleteCategory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Iterate through the category list to transform into a charsequence list
                final CharSequence[] categories = new CharSequence[categoryList.size() - INDEX_TO_GET_LAST_TASK_LIST_ITEM];
                //another one to hold the isChecked attribute
                final boolean[] deletableCategories = new boolean[categoryList.size() - INDEX_TO_GET_LAST_TASK_LIST_ITEM];
                //For loop to populate the char-sequence array with the category names coming from category list
                for (int i = INDEX_TO_GET_LAST_TASK_LIST_ITEM; i < categoryList.size(); i++) {
                    //For each item in the list, extract name and save it in the string array
                    CharSequence categoryName = "";
                    categoryName = getCategoryNameFromRes(categoryList.get(i).getName());
                    //Save the name into the array to be passed into the AlertDialog constructor
                    categories[i - INDEX_TO_GET_LAST_TASK_LIST_ITEM] = categoryName;
                }//End of for loop to populate the taskList array
                //Create a dialog box to display the grocery types
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.deleteCategoryTitle))
                        .setMultiChoiceItems(categories, deletableCategories, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                //When a category is selected, save it in the boolean array
                                deletableCategories[which] = isChecked;
                            }
                        })
                        .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Declare boolean flag to check if list with items to delete is empty or not
                                boolean notEmpty = false;
                                //Check the taskList is not empty
                                //Declare and initialize an empty array list to hold the categories to be deleted
                                final ArrayList<Category> categoriesToBeDeleted = new ArrayList<Category>();
                                if (categories.length > 0) {
                                    //If not empty  get the name of list to be deleted
                                    for (int i = 0; i < categories.length; i++) {
                                        //Check the category was selected to be deleted
                                        if (deletableCategories[i]) {
                                            categoriesToBeDeleted.add(categoryList.get(i + INDEX_TO_GET_LAST_TASK_LIST_ITEM));
                                            notEmpty = true;
                                        }///End of for loop to go through the deletableTasks list
                                    }//End of for loop to iterate through the list of Categories
                                }//End of if statement that checks at least one category was selected
                                //Check at least one category was selected for deletion, otherwise display an error message
                                if (notEmpty) {
                                    //Declare and initialize a boolean flag to confirm the categories have been deleted
                                    final boolean[] isCategoryDeleteProcessWithoutFault = {true};
                                    //Declare and instantiate a string object to dynamically include the names of lists to be deleted in message
                                    String deleteConfirmationMessage = getResources().getString(R.string.wantToDeleteCatList);
                                    final String bulletPoint = "❌";
                                    if (categoriesToBeDeleted.size() > 1) {
                                        //Make the text plural if more than one category will be deleted
                                        deleteConfirmationMessage += "ies: \n\t" + bulletPoint;
                                    } else {
                                        //Make the text singular if only one category will be deleted
                                        deleteConfirmationMessage += "y: \n\t" + bulletPoint;
                                    }//End of if else statement fo selected the proper warning message to display
                                    //For loop to go through the list of categories to be deleted and add every list's name into the warning message
                                    for (int i = 0; i < categoriesToBeDeleted.size(); i++) {
                                        //Add the current list name to the text
                                        deleteConfirmationMessage += categoriesToBeDeleted.get(i).getName();
                                        //Check this is not the last item in the list
                                        if (i + 1 < categoriesToBeDeleted.size()) {
                                            //If it is not the last one, add an extra line and bullet
                                            deleteConfirmationMessage += "\n\t" + bulletPoint;
                                        }//End of if statement to check if it's the last one item in the list
                                    }//End of for loop to include the list names to be deleted
                                    //Display a final warning message summarizing  all the lists to be deleted and informing all the tasks in that lis will be deleted
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle(R.string.deleteCategory)
                                            .setMessage(deleteConfirmationMessage)
                                            .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    //If clicked Ok, delete the accounts associated to the selected category
                                                    int i = 0;
                                                    while (i < categoriesToBeDeleted.size() && isCategoryDeleteProcessWithoutFault[0]) {
                                                        //Get a cursor list of accounts which category is the current one to be deleted
                                                        ArrayList accountsToBeDeleted = accountsDB.getAccountsIDListUsingItemWithID(MainActivity.getCategoryIdColumn(), categoriesToBeDeleted.get(i).get_id());
                                                        int j = 0;
                                                        Account account = null;
                                                        while (j < accountsToBeDeleted.size() && isCategoryDeleteProcessWithoutFault[0]) {
                                                            account = accountsDB.getAccountByID((int) accountsToBeDeleted.get(j));
                                                            //Delete the current account in the list
                                                            if (EditAccountActivity.deleteAccount(accountsDB, account)) {
                                                                isCategoryDeleteProcessWithoutFault[0] = true;
                                                            } else {
                                                                isCategoryDeleteProcessWithoutFault[0] = false;
                                                                break;
                                                            }//End of if else statement that checks the deletion of current account was successful
                                                            j++;
                                                        }//End of account list while loop
                                                        //Check the deletion process went smoothly for the account list
                                                        if (isCategoryDeleteProcessWithoutFault[0]) {
                                                            //Once the accounts associated to this category has been deleted, delete the category itself
                                                            if (accountsDB.deleteItem(categoriesToBeDeleted.get(i))) {
                                                                isCategoryDeleteProcessWithoutFault[0] = true;
                                                            } else {
                                                                isCategoryDeleteProcessWithoutFault[0] = false;
                                                            }//End of if else statement that checks the deletion of current category was successful
                                                        } else {
                                                            //Display error message to notify an account was not deleted and the category deletion
                                                            //process was interrupted and will not continue
                                                            MainActivity.displayToast(MainActivity.this, getResources().getString(R.string.deleteCategoryAccDelFailed1) + account + " " + getResources().getString(R.string.deleteCategoryFailed2), Toast.LENGTH_SHORT, Gravity.CENTER);
                                                        }//End of if else statement to check account deletion was successful
                                                        i++;
                                                    }//End of Category list while loop
                                                    //Check why while loop ended, delete process finished correctly?
                                                    if (isCategoryDeleteProcessWithoutFault[0]) {
                                                        //Update the list of current categories
                                                        categoryList = accountsDB.getCategoryList();
                                                        //Update the Nav drawer menu to display correct list of categories
                                                        for (int k = 0; k < categoriesToBeDeleted.size(); k++) {
                                                            navMenu.removeItem(categoriesToBeDeleted.get(k).get_id());
                                                        }//End of for loop to delete all menu items
                                                        //Check if the current category is one of the categories just deleted
                                                        if (isCurrentCategoryInListToBeDeleted(currentCategory.get_id(), categoriesToBeDeleted)) {
                                                            //If that the case, move current category to Home
                                                            currentCategory = categoryList.get(0);
                                                            //Update app state in DB
                                                            updateCategoryInAppState();
                                                            //Then move Nav drawer menu item to Home
                                                            navMenu.getItem(0).setCheckable(true);
                                                            navMenu.getItem(0).setChecked(true);
                                                            NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                                                            //Ask nav controller to load the HomeFragment class
                                                            navController.navigate(R.id.nav_home);
                                                        }//End of if statement that checks if current category has been deleted
                                                        //Finally, display toast to confirm category was deleted
                                                        //Check the number of categories that were deleted
                                                        String toastText = "";
                                                        if (categoriesToBeDeleted.size() > 1) {
                                                            //Set text for multiple categories and iterate through the categories to be deleted list to add each category name
                                                            toastText = getResources().getString(R.string.deleteCategoriesSuccessful);
                                                            for (int l = 0; l < categoriesToBeDeleted.size(); l++) {
                                                                toastText += "\n\t" + bulletPoint + categoriesToBeDeleted.get(l).getName();
                                                            }//End of for loop to iterate through categories to be deleted list
                                                        } else {
                                                            //If only one category was delete, set up proper message for singular category deleted
                                                            toastText = categoriesToBeDeleted.get(0).getName() + " " + getResources().getString(R.string.deleteCategorySuccessful);
                                                        }//End of if statement that checks number of categories deleted
                                                        //Display message to confirm category deletion process was successful
                                                        displayToast(MainActivity.this, toastText, Toast.LENGTH_SHORT, Gravity.CENTER);
                                                    } else {
                                                        //Display error message to notify an the current category failed to be deleted and the deletion
                                                        //process was interrupted and will not continue if  more categories were selected for deletion
                                                        displayToast(MainActivity.this, getResources().getString(R.string.deleteCategoryFailed1) + categoriesToBeDeleted.get(i).getName() + " " + getResources().getString(R.string.deleteCategoryFailed2), Toast.LENGTH_SHORT, Gravity.CENTER);
                                                    }//End of if else statement to check category deletion was successful
                                                }//End of Onclick method
                                            })//End of setPositiveButton method
                                            .setNegativeButton(R.string.cancel, null)
                                            .show();
                                } else {
                                    MainActivity.displayToast(MainActivity.this, getResources().getString(R.string.noCatSelected), Toast.LENGTH_SHORT, Gravity.CENTER);
                                }// End of if else statement to check the list of categories is not empty
                            }// End of onClick method
                        })//End of setPositiveButton onClick listener method
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
                return false;
            }//End of onMenuItemClick method
        });//End of setOnMenuItemClickListener method call
        Log.d("setUpLowerCategoryMenu", "Enter the updateNavMenu method in MainActivity class.");
    }//End of setUpLowerCategoryMenu method


    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    public static int getCategoryPositionByID(int _id,ArrayList<Category>  categoryList) {
        Log.d("getCategoryByName", "Enter the getCategoryByName 1.1 method in MainActivity class.");
        boolean found = false;
        int i = 0;
        while (i < categoryList.size() && !found) {
            if (categoryList.get(i).get_id() == _id) {
                found = true;
                break;
            }//End of if statement to check the category id
            i++;
        }//End of while loop to iterate through the category list
        //Check if found, if not, retrun -1 instead of category id
        if(!found){
            i = -1;
        }
        Log.d("getCategoryByName", "Exit the getCategoryByName 1.1 method in MainActivity class.");
        return i;
    }//End of getCategoryPositionByID method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    public static int getCategoryPositionByID(int _id) {
        Log.d("getCategoryByName", "Enter the getCategoryByName 1.2 method in MainActivity class.");
        int i = getCategoryPositionByID(_id,categoryList);
        Log.d("getCategoryByName", "Exit the getCategoryByName 1.2 method in MainActivity class.");
        return i;
    }//End of getCategoryPositionByID method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    public static Category getCategoryInListByID(int _id) {
        Log.d("getCategoryPositionByID", "Enter the getCategoryPositionByID method in MainActivity class.");
        //Call method above to get position in the category list by passing it's _id
        int position = getCategoryPositionByID(_id);
        //Set the category object to be return to match the object in the position found
        Category category = categoryList.get(position);
        Log.d("getCategoryPositionByID", "Exit the getCategoryPositionByID method in MainActivity class.");
        return category;
    }//End of getCategoryPositionByID method

    //Method to update the Nav Menu items when new task list are created or deleted. Used to populate the menu on onCreate method too
    public static boolean isCurrentCategoryInListToBeDeleted(int _id, ArrayList<Category> categoriesToBeDeleted) {
        Log.d("isCatInListToBeDeleted", "Enter the isCurrentCategoryInListToBeDeleted static method in MainActivity class.");
        boolean found = false;
        //Call method above to get position in the category list by passing it's _id
        int position = getCategoryPositionByID(_id,categoriesToBeDeleted);
        if(position >= 0){
            found = true;
        }
        Log.d("isCatInListToBeDeleted", "Exit the isCurrentCategoryInListToBeDeleted static method in MainActivity class.");
        return found;
    }//End of isCurrentCategoryInListToBeDeleted method

    //Method to update User Profile Name
    private void setUserProfileText(int type, final TextView tvUserText) {
        Log.d("Ent_setProfName", "Enter setUserProfileName method in the MainActivity class.");
        //Declare and instantiate a new EditText object
        final EditText input = new EditText(this);
        //Populate current name in the input text and get focus
        input.setText(tvUserText.getText());
        input.requestFocus();
        String title = "";
        String message = "";
        final String[] dbErrorMessage = {""};
        final String[] emptyFieldErrorMessage = {""};
        final String[] columnToBeUpdated = {""};
        switch (type) {
            case 1:
                title = getResources().getString(R.string.setUserName);
                message = getResources().getString(R.string.inputUserName);
                dbErrorMessage[0] = getResources().getString(R.string.unableUpdateUserName);
                emptyFieldErrorMessage[0] = getResources().getString(R.string.blankUserName);
                columnToBeUpdated[0] = NAME_COLUMN;
                break;
            case 2:
                title = getResources().getString(R.string.setUserMessage);
                message = getResources().getString(R.string.inputUserMessage);
                dbErrorMessage[0] = getResources().getString(R.string.unableUpdateUserMessage);
                emptyFieldErrorMessage[0] = getResources().getString(R.string.blankUserMessage);
                columnToBeUpdated[0] = "Message";
                break;
        }//End of switch statement
        //Display a Dialog to ask for the List name (New Category)
        new AlertDialog.Builder(this)
                .setTitle(title)//Set title
                .setMessage(message)// Set the message that clarifies the requested action
                .setView(input)
                .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String userText = input.getText().toString();
                        //Check the input field is not empty
                        if (!userText.trim().equals("")) {
                            ContentValues values = new ContentValues();
                            values.put(ID_COLUMN, currentAppLoggin.get_id());
                            values.put(columnToBeUpdated[0], userText);
                            if (accountsDB.updateTable(APPLOGGIN_TABLE, values)) {
                                tvUserText.setText(input.getText());
                            } else {
                                //Display error message if the boolean received from DB is false
                                displayToast(MainActivity.this, dbErrorMessage[0], Toast.LENGTH_SHORT, Gravity.CENTER);
                            }//End of if else statement to update the user data and receive result of that DB action
                        } else {
                            //If input field is empty, display an error message
                            displayToast(MainActivity.this, emptyFieldErrorMessage[0], Toast.LENGTH_SHORT, Gravity.CENTER);
                        }//End of if else statement to check the input field is not left blank
                    }//Define the positive button
                })//End of AlertDialog Builder
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
        Log.d("Ext_setProfName", "Exit setUserProfileName method in the MainActivity class.");
    }//End of setUserProfileName method

    //Method to filter task or groceries by description content
    private void sort(final MenuItem item) {
        Log.d("Ent_sort", "Enter the sort method in the MainActivity class.");
        final int[] selectedSortCriteriID = {0};
        final int[] positionInList = {0};
        final CharSequence[] sortCriteria;
        if (this.currentTab == 0) {
            sortCriteria = new CharSequence[5];
            sortCriteria[0] = getString(R.string.sort_alpha_up);
            sortCriteria[1] = getString(R.string.sort_alpha_down);
            sortCriteria[2] = getString(R.string.sort_date_up);
            sortCriteria[3] = getString(R.string.sort_date_down);
            sortCriteria[4] = getString(R.string.sort_category);
        } else if (this.currentTab == 1 || this.currentTab == 2) {
            sortCriteria = new CharSequence[3];
            sortCriteria[0] = getString(R.string.sort_date_up);
            sortCriteria[1] = getString(R.string.sort_date_down);
            sortCriteria[2] = getString(R.string.sort_times_used);
        } else {
            sortCriteria = new CharSequence[3];
            sortCriteria[0] = getString(R.string.sort_alpha_up);
            sortCriteria[1] = getString(R.string.sort_alpha_down);
            sortCriteria[2] = getString(R.string.sort_times_used);
        }//End of if else statement to define the sort criteria options based on the current tab selected
        //Check if the sort filter is already in use. If that is the case, select the that one as the checked item when creating the dialog
        int checkedItem = 0;
        if (isSortFilter) {
            //Then check current filter selected
            if (currentSortFilter.equals(SortFilter.ALPHA_ASC)) {
                checkedItem = 0;
            } else if (currentSortFilter.equals(SortFilter.ALPHA_DES)) {
                checkedItem = 1;
            } else if (currentSortFilter.equals(SortFilter.CATEGORY)) {
                checkedItem = 4;
            } else if (currentSortFilter.equals(SortFilter.DATE_ASC)) {
                if (currentTab == 0) {
                    checkedItem = 2;
                } else {
                    checkedItem = 0;
                }
            } else if (currentSortFilter.equals(SortFilter.DATE_DES)) {
                if (currentTab == 0) {
                    checkedItem = 3;
                } else {
                    checkedItem = 1;
                }
            } else if (currentSortFilter.equals(SortFilter.TIMES_USED)) {
                if (currentTab != 0) {
                    checkedItem = 2;
                }
            }//End of if else chain to check current sort filter selected
        }//End of if statement to check the sort filter is in use
        positionInList[0] = checkedItem;
        //Display alert dialog to select sor filter type
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.sort_title))
                .setSingleChoiceItems(sortCriteria, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        positionInList[0] = which;
                    }
                })
                .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        switch (positionInList[0]) {
                            case 0:
                                if (currentTab == 1 || currentTab == 2) {
                                    currentSortFilter = SortFilter.DATE_ASC;
                                } else {
                                    currentSortFilter = SortFilter.ALPHA_ASC;
                                }
                                break;
                            case 1:
                                if (currentTab == 1 || currentTab == 2) {
                                    currentSortFilter = SortFilter.DATE_DES;
                                } else {
                                    currentSortFilter = SortFilter.ALPHA_DES;
                                }
                                break;
                            case 2:
                                if (currentTab == 0) {
                                    currentSortFilter = SortFilter.DATE_ASC;
                                } else {
                                    currentSortFilter = SortFilter.TIMES_USED;
                                }
                                break;
                            case 3:
                                if (currentTab == 0) {
                                    currentSortFilter = SortFilter.DATE_DES;
                                } else {
                                    currentSortFilter = null;
                                }
                                break;
                            case 4:
                                if (currentTab == 0) {
                                    currentSortFilter = SortFilter.CATEGORY;
                                } else {
                                    currentSortFilter = null;
                                }
                                break;
                            default:
                                currentSortFilter = null;
                                break;
                        }
                        //Clear Sort filter if applied
                        if (isSearchFilter) {
                            clearSearchFilter();
                        }
                        isSortFilter = true;
                        //Call method to update RV data
                        //Call method to update the adapter and the recyclerView
                        updateRecyclerViewData(HomeFragment.getRv().getAdapter(),-1,NotifyChangeType.DATA_SET_CHANGED);
                        item.getIcon().setTintList(ColorStateList.valueOf(themeUpdater.fetchThemeColor("colorAccent")));
                        //Update App State
                        updateSortFilterInAppState();
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }//End of onClick method)
                })//End of set negative button
                .create()
                .show();
        Log.d("Ent_sort", "Exit the sort method in the MainActivity class.");
    }//End of the sort method

    //Inner Enum to define the possible searches available for the search filter
    public enum SearchType {
        //Define the possible priorities in this app
        ACCOUNT_WITH_USERNAME("Account with user name"),
        ACCOUNT_WITH_PSSWRD("Account with password"),
        ACCOUNTS("Accounts"),
        USER_NAME("User name"),
        PSSWRD("Password");

        String name;

        //Full constructor
        SearchType(String name) {
            Log.d("EntFullCategory", "Enter full constructor in the Category class.");
            this.name = name;
            Log.d("ExtFullCategory", "Exit full constructor in the Category class.");
        }//End of Full Category constructor
    }//End of SearchType enum

    //Inner Enum to define the possible searches available for the search filter
    public enum NotifyChangeType {
        //Define the possible priorities in this app
        ITEM_CHANGED("Item changed"),
        ITEM_REMOVED("Item removed"),
        ITEM_INSERTED("Item inserted"),
        DATA_SET_CHANGED("Data set changed");
        String name;

        //Full constructor
        NotifyChangeType(String name) {
            Log.d("NotifyChangeType", "Enter full constructor in the NotifyChangeType enum MainActivity class.");
            this.name = name;
            Log.d("NotifyChangeType", "Exit full constructor in the NotifyChangeType enum in MainActivity class.");
        }//End of Full Category constructor
    }//End of NotifyChangeType enum


    //Method to filter task or groceries by description content
    private void search(final MenuItem item) {
        Log.d("Ent_serach", "Enter the search method in the MainActivity class.");
        //Declare and instantiate a new View objects to be used on the AlertDialog box: Two switch views and one editText view.
        //All of them under a LinearLayout parent
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //Declare and instantiate the switch to turn on Accounts with specif user name search
        final Switch isSearchAccountWithUserName = new Switch(this);
        //Declare and instantiate the switch to turn on Accounts with specif password search
        final Switch isSearchAccountWithPsswrd = new Switch(this);
        //Declare and instantiate the EditText object to allow user to input the searched text
        final EditText input = new EditText(this);
        if (this.isSearchFilter) {
            input.setText(this.lastSearchText);
            if (this.isSearchUserNameFilter) {
                isSearchAccountWithUserName.setChecked(true);
            } else if (this.isSearchPsswrdFilter) {
                isSearchAccountWithPsswrd.setChecked(true);
            }
        } else {
            //Set text to empty text
            input.setText("");
        }
        if (currentTab == 0) {
            //Set up the proper texts for this switch
            isSearchAccountWithUserName.setTextOff(getResources().getString(R.string.searchAccountsWithUserNameTextOff));
            isSearchAccountWithUserName.setTextOn(getResources().getString(R.string.searchAccountsWithUserNameTextOn));
            isSearchAccountWithUserName.setText(getResources().getString(R.string.searchAccountsWithUserNameTextOff));

            //Set up the proper texts for this switch
            isSearchAccountWithPsswrd.setTextOff(getResources().getString(R.string.searchAccountsWithPsswrdTextOff));
            isSearchAccountWithPsswrd.setTextOn(getResources().getString(R.string.searchAccountsWithPsswrdTextOn));
            isSearchAccountWithPsswrd.setText(getResources().getString(R.string.searchAccountsWithPsswrdTextOff));
            //Set up the onClick event listener
            isSearchAccountWithUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Call method to confirm if the switch is checked or onChecked
                    switchOnOff((Switch) v, isSearchAccountWithPsswrd, input);
                }//End of onClick method
            });//End of setOnClickListener method

            //Set up the onClick event listener
            isSearchAccountWithPsswrd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Call method to confirm if the switch is checked or onChecked
                    switchOnOff((Switch) v, isSearchAccountWithUserName, input);
                }//End of onClick method
            });//End of setOnClickListener method
            linearLayout.addView(isSearchAccountWithUserName);
            linearLayout.addView(isSearchAccountWithPsswrd);
        }//End of if statement to check the accounts tab is the one selected

        linearLayout.addView(input);
        input.requestFocus();
        //Check the current tab and category to set up the correct search criteria
        int searchHintText = -1;
        int searchTitle = -1;
        switch (this.tabLayout.getSelectedTabPosition()) {
            default:
                searchTitle = R.string.searchAccountTitle;
                if (isSearchAccountWithUserName.isChecked()) {
                    searchHintText = R.string.hintSearchAccount1;
                } else if (isSearchAccountWithPsswrd.isChecked()) {
                    searchHintText = R.string.hintSearchAccount2;
                } else {
                    searchHintText = R.string.hintSearchAccount3;
                }
                break;
            case 1:
                searchTitle = R.string.searchUserNameTitle;
                searchHintText = R.string.hintSearchUserName;
                break;
            case 2:
                searchTitle = R.string.searchPsswrdTitle;
                searchHintText = R.string.hintSearchPsswrd;
                break;
            case 3:
                searchTitle = R.string.searchQuestionTitle;
                searchHintText = R.string.hintSearchQuestion;
                break;
        }//End of switch statement
        //Set the hint message to be displayed
        input.setHint(searchHintText);
        new AlertDialog.Builder(this)
                .setTitle(searchTitle)
                .setView(linearLayout)
                .setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Check if switch views were included
                        int etViewPosition;
                        if (linearLayout.getChildCount() > 1) {
                            etViewPosition = 2;
                        } else {
                            etViewPosition = 0;
                        }
                        lastSearchText = ((EditText) linearLayout.getChildAt(etViewPosition)).getText().toString().trim();

                        if (!lastSearchText.equals("")) {
                            //Set isSearchFilter, isSearchAccountWithUserName and isSearchAccountWithPsswrd boolean to true if applicable
                            isSearchFilter = true;
                            if (isSearchAccountWithUserName.isChecked()) {
                                isSearchUserNameFilter = true;
                            } else {
                                isSearchUserNameFilter = false;
                            }

                            if (isSearchAccountWithPsswrd.isChecked()) {
                                isSearchPsswrdFilter = true;
                            } else {
                                isSearchPsswrdFilter = false;
                            }
                            //Check the input text has apostrophe
                            if (lastSearchText.contains("'")) {
                                //If it does, call method to include escape character
                                lastSearchText = accountsDB.includeApostropheEscapeChar(lastSearchText);
                            }//End of if statement to check the search text has apostrophe
                            //Clear Sort filter if applied
                            if (isSortFilter) {
                                clearSortFilter();
                            }
                            //Check the switch statuses to define the type of search to be performed: Accounts with this user, Accounts with this passowrd
                            //or Accounts with this name
                            //Check if switch views were added to linearLayout object
                            if (linearLayout.getChildCount() > 1) {
                                //Call method to update the adapter and the recyclerView
                                updateRecyclerViewData(HomeFragment.getRv().getAdapter(),-1,NotifyChangeType.DATA_SET_CHANGED);
                            } else {
                                //Call method to update the adapter and the recyclerView
                                updateRecyclerViewData(HomeFragment.getRv().getAdapter(),-1,NotifyChangeType.DATA_SET_CHANGED);
                            }//End of if else statement to check the children count in the linear layout, this defines what tab is being used
                            item.getIcon().setTintList(ColorStateList.valueOf(themeUpdater.fetchThemeColor("colorAccent")));
                            //Update app state in DB
                            ContentValues values = new ContentValues();
                            values.put(ID_COLUMN, accountsDB.getMaxItemIdInTable(APPSTATE_TABLE));
                            values.put(IS_SEARCH_FILTER_COLUMN, accountsDB.toInt(isSearchFilter));
                            values.put(IS_SEARCH_USER_FILTER_COLUMN, accountsDB.toInt(isSearchAccountWithUserName.isChecked()));
                            values.put(IS_SEARCH_PSSWRD_FILTER_COLUMN, accountsDB.toInt(isSearchAccountWithPsswrd.isChecked()));
                            values.put(LAST_SEARCH_TEXT_COLUMN, lastSearchText);
                            accountsDB.updateTable(APPSTATE_TABLE, values);
                        } else {
                            //Clear serach filter and display error message
                            clearSearchFilter();
                            displayToast(MainActivity.this, getString(R.string.errorSearchTextEmpty), Toast.LENGTH_SHORT, Gravity.CENTER);
                        }
                    }//End of Onclick method
                })//End of setPossitiveButton method
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Update the RV list
                        if (isSearchFilter) {
                            //If search filter already in use, keep current search instead of clearing it when canceling a new search
                            updateRecyclerViewData(HomeFragment.getRv().getAdapter(),-1,NotifyChangeType.DATA_SET_CHANGED);
                        }
                    }//End of onClick method
                })//End of set negative button
                .show();
    }//End of the search method

    //Method to update Search UI switch when clicked on
    private void switchOnOff(Switch switchView1, Switch switchView2, EditText input) {
        Log.d("switchOnOff", "Enter the switchOnOff method in the MainActivity class.");
        //Check if the switch 1 is checked
        if ((switchView1).isChecked()) {
            //Set the On Text if checked
            switchView1.setText(switchView1.getTextOn());
        } else {
            //Otherwise, set the Off text
            switchView1.setText(switchView1.getTextOff());
        }//End of if else statement to check switch 1 is checked
        //Check if the other switch is check, since they are mutually exclusive it must be switched off
        if (switchView2.isChecked()) {
            switchView2.setChecked(false);
            switchView2.setText(switchView2.getTextOff());
        }//End of if statement to check if switch 2 is checked
        //Identify if switchView1 is the one for looking for specif user name or specific password
        if (switchView1.getTextOn().equals(getResources().getString(R.string.searchAccountsWithUserNameTextOn))) {
            //If On text is the one for user name, set proper hint for looking for accounts with specific user name
            if (switchView1.isChecked()) {
                input.setHint(R.string.hintSearchAccount1);
            } else {
                input.setHint(R.string.hintSearchAccount3);
            }//End of if else statement that checks the switch is on
        } else if (switchView1.getTextOn().equals(getResources().getString(R.string.searchAccountsWithPsswrdTextOn))) {
            //If On text is the one for password, set proper hint for looking for accounts with specific password
            if (switchView1.isChecked()) {
                input.setHint(R.string.hintSearchAccount2);
            } else {
                input.setHint(R.string.hintSearchAccount3);
            }//End of if else statement that checks the switch is on
        }//End of if else statement to identify the two switch views and set up proper input hint text
        Log.d("switchOnOff", "Exit the switchOnOff method in the MainActivity class.");
    }//End of switchOnOff method

    //Method to clear search filter
    private boolean clearSearchFilter() {
        Log.d("clearSearchFilter", "Enter the clearSearchFilter method in the MainActivity class.");
        boolean isSearchFilterCleared = false;
        this.isSearchFilter = false;
        this.isSearchUserNameFilter = false;
        this.isSearchPsswrdFilter = false;
        this.lastSearchText = "";
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar.getMenu().size() > 0) {
            toolbar.getMenu().getItem(0).getIcon().setTintList(null);
        }//End of if statement to check the menu size is greater than 0
        //Update app state to remove all search related fields
        ContentValues values = new ContentValues();
        values.put(this.ID_COLUMN, accountsDB.getMaxItemIdInTable(APPSTATE_TABLE));
        values.put(this.IS_SEARCH_FILTER_COLUMN, this.isSearchFilter);
        values.put(this.IS_SEARCH_USER_FILTER_COLUMN, this.isSearchPsswrdFilter);
        values.put(this.LAST_SEARCH_TEXT_COLUMN, this.lastSearchText);
        //Call DB method to update the APPSTATE table
        if (this.accountsDB.updateTable(APPSTATE_TABLE, values)) {
            //Set boolean flag to true if update result is good
            isSearchFilterCleared = true;
        }//End of if statement to check the app state has been successfully updated
        Log.d("clearSearchFilter", "Exit the clearSearchFilter method in the MainActivity class.");
        return isSearchFilterCleared;
    }//End of clearSearchFilter method

    //Method to clear search filter
    private boolean clearSortFilter() {
        boolean isSortFilterCleared = false;
        Log.d("clearSortFilter", "Enter the clearSortFilter method in the MainActivity class.");
        this.isSortFilter = false;
        this.currentSortFilter = null;
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar.getMenu().size() > 0) {
            toolbar.getMenu().getItem(1).getIcon().setTintList(null);
        }//End of if statement to check the menu size is greater than 0
        //Update sort filter in app state
        this.updateSortFilterInAppState();
        Log.d("clearSortFilter", "Exit the clearSortFilter method in the MainActivity class.");
        isSortFilterCleared = true;
        return isSortFilterCleared;
    }//End of clearSearchFilter method

    //Method to update the current category in the app state
    private boolean updateCategoryInAppState() {
        Log.d("updateCatInAppState", "Enter updateCategoryInAppState method in the MainActivity class.");
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, accountsDB.getMaxItemIdInTable(APPSTATE_TABLE));
        values.put(CURRENT_CATEGORY_ID_COLUMN, currentCategory.get_id());
        this.appState = accountsDB.getAppState();
        Log.d("updateCatInAppState", "Exit updateCategoryInAppState method in the MainActivity class.");
        return accountsDB.updateTable(APPSTATE_TABLE, values);
    }//End of updateCategoryInAppState method

    //Method to update the sort filter selection and the current sort filter in the app state
    private boolean updateSortFilterInAppState() {
        Log.d("updateSortInAppState", "Enter updateSortFilterInAppState method in the MainActivity class.");
        //Update app state in DB
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, accountsDB.getMaxItemIdInTable(this.APPSTATE_TABLE));
        values.put(IS_SORT_FILTER_COLUMN, accountsDB.toInt(this.isSortFilter));
        if (this.currentSortFilter != null) {
            values.put(CURRENT_SORT_FILTER_COLUMN, this.currentSortFilter.ordinal());
        } else {
            values.put(CURRENT_SORT_FILTER_COLUMN, -1);
        }//End of if else statement to check sort filter type
        //Log.d("updateCatInAppState","Exit updateCategoryInAppState method in the MainActivity class.");
        Log.d("updateSortInAppState", "Exit updateSortFilterInAppState method in the MainActivity class.");
        return accountsDB.updateTable(APPSTATE_TABLE, values);
    }//End of updateSortFilterInAppState method

    //Static method to setup the APP theme to the caller activity
    public static int setAppTheme(Context context) {
        Log.d("Ent_setAppTheme", "Enter setAppTheme method in MainActivity class.");
        //Get prefered app theme from preferences xml file
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String preferedThemeID = pref.getString("appTheme", "0");
        //Declare int variable to return the theme id
        int themeId;
        //Check if method got called by MainActivity or any other activity
        if (context instanceof MainActivity) {
            //If called by MainActivty, set up theme with NoActionBar
            switch (preferedThemeID) {
                case "1":
                    themeId = R.style.AppThemeIronMan;
                    break;
                case "2":
                    themeId = R.style.AppThemeCapAmerica;
                    break;
                case "3":
                    themeId = R.style.AppThemeHulk;
                    break;
                case "4":
                    themeId = R.style.AppThemeCapMarvel;
                    break;
                case "5":
                    themeId = R.style.AppThemeBatman;
                    break;
                default:
                    themeId = R.style.AppTheme;
                    break;
            }//End of switch statement to check

        } else {
            //Otherwise, if any other activity called the method, set up theme with action bar
            switch (preferedThemeID) {
                case "1":
                    themeId = R.style.AppThemeIronManOthers;
                    break;
                case "2":
                    themeId = R.style.AppThemeCapAmericaOthers;
                    break;
                case "3":
                    themeId = R.style.AppThemeHulkOthers;
                    break;
                case "4":
                    themeId = R.style.AppThemeCapMarvelOthers;
                    break;
                case "5":
                    themeId = R.style.AppThemeBatmanOthers;
                    break;
                default:
                    themeId = R.style.AppThemeOthers;
                    break;
            }//End of switch statement
        }//End of if else statement to check what activity called the method
        Log.d("Ext_setAppTheme", "Exit setAppTheme method in MainActivity class.");
        return themeId;
    }//End of setAppTheme method

    //Static method to setup language to the caller activity
    public static void setAppLanguage(Context context) {
        Log.d("Ent_setAppLang", "Enter setAppLanguage method in MainActivity class.");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String languageValue = pref.getString("languages", "0");
        String language;
        if (languageValue.equals("0")) {
            language = "en";
        } else {
            language = "es";
        }//End of if else statement to check the language value
        // Change locale settings in the app.
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language.toLowerCase())); // API 17+ only.
        res.updateConfiguration(conf, dm);
        Log.d("Ext_setAppLang", "Exit setAppLanguage method in MainActivity class.");
    }//End of setAppTheme method


    public static long getLogOutTime(Context context) {
        Log.d("getLogOutTime", "Enter getLogOutTime method in MainActivity class.");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String timeOutValue = pref.getString("logOutTime", "1");
        long logOutTime;
        switch(timeOutValue){
            case "1":
                logOutTime = 3;
                break;
            case "2":
                logOutTime = 5;
                break;
            default:
                logOutTime= 1;
                break;
        }
        logOutTime = logOutTime*10*1000;
        Log.d("getLogOutTime", "Exit getLogOutTime method in MainActivity class.");
        return logOutTime;
    }//End of setAppTheme method


    public static boolean isAutoLogOutActive(Context context) {
        Log.d("getIsLogOut", "Enter getIsLogOut method in MainActivity class.");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isLogOut = pref.getBoolean("isAutoLogOutActive", false);
        Log.d("getIsLogOut", "Exit getIsLogOut method in MainActivity class.");
        return isLogOut;
    }//End of setAppTheme method

    //Method to logout app
    //@TODO: What's this? to be removed?
    private void logout() {
        Log.d("logout", "Enter logout method in MainActivity class.");
        //Display alert with justification about why permit is necessary
        AlertDialog.Builder alert = displayAlertDialogNoInput(this, "Logout", "Log out Timeout!");
        alert.setPositiveButton("Continue",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((LogOutTimer)AutoLogOutService.getLogOutTimer()).start();
            }

        }).setNegativeButton("LogOut",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callFinish();
            }//End of on Click method

        }).show();
        Log.d("logout", "Exit logout method in MainActivity class.");
    }//End of logout method

    public static void logout(Context context){
        Log.d("logout", "Enter logout method in MainActivity class called by: "+ context.toString());
        if(isAutoLogOutActive()){
            Intent iService = new Intent(context,AutoLogOutService.class);
            context.stopService(iService);
        }
        if(LogOutTimer.isAppInForeground()){

            Intent i = new Intent(context, LoginActivity.class);
            //Add intent falgs to clear the back stack, so pressing back button on LoginActivity will not go into the app activities without logging in.
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //Set boolean flag to confirm app has logged out
            MainActivity.isLoggedOut = true;
            (context).startActivity(i);

        }else{
            //Call method to recursively finish all activities in the back stack
            ((Activity)context).finishAffinity();
            Log.d("finishApp", "Finish all activities in the back stack.");
        }//End of if else to check app is in the foreground

        Log.d("logout", "Exit logout method in MainActivity class called by: "+ context.toString());
    }//End of logout method

    //Method to create Push notification channel
    private void createNotificationChannel() {
        Log.d("createNotChannel", "Enter createNotificationChannel method in MainActivity class.");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyPsswrdNotificationChannel";
            String description = "Password update required!";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager  notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }//End of if statement to check SDK version
        Log.d("createNotChannel", "Exit createNotificationChannel method in MainActivity class.");
    }//End of createNotificationChannel method

    public static void checkLogOutTimeOut(Context context){
        Log.d("checkLogOut", "Enter checkLogOutTimeOut method in MainActivity class.");
        if(MainActivity.isAutoLogOutActive()){
            LogOutTimer logOutTimer = (LogOutTimer)AutoLogOutService.getLogOutTimer();
            //Set current activity context for the Logout timer in order to display auto logout prompt
            logOutTimer.setContext(context);
            if(AutoLogOutDialogActivity.isPopUpToBeDisplayed()){
                logOutTimer.logOutTimeOut();
            }//End of if statement to check the popup can be displayed
        }//End of if statement to check auto logout is active
        Log.d("checkLogOut", "Exit checkLogOutTimeOut method in MainActivity class.");
    }//End of checkLogOutTimeOut method


}//End of MainActivity class.
