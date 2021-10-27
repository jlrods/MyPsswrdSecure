package io.github.jlrods.mypsswrdsecure;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

abstract class DisplayAccountActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //Attribute definition
    //Constants
    //Spinner types constants
    static final int CATEGORY_SPINNER = 0;
    static final int USERNAME_SPINNER = 1;
    static final int PSSWRD_SPINNER = 2;
    static final int QUESTION_SPINNER = 3;
    //DB
    AccountsDB accountsDB;
    //Objects required to build an account
    UserName userName = null;
    Psswrd psswrd = null;
    Account account = null;

    //Layout attributes
    protected Bundle extras;
    protected ImageView imgAccLogo;
    protected EditText etAccountName;
    protected NoDefaultSpinner spCategory;
    protected NoDefaultSpinner spAccUserName;
    protected Button btnAccNewUserName;
    protected NoDefaultSpinner spAccPsswrd;
    protected Button btnAccNewPsswrd;
    protected NoDefaultSpinner spAccSecQuestionList;
    protected NoDefaultSpinner spQuestionsAvailable;
    protected Button btnAccNewSecQuestion;
    protected Button btnAccAddQuestion;
    protected Button btnAccRemoveQuestion;
    protected ImageView imgAccIsFavorite;
    protected TextView tvAccIsFavTag;
    protected CheckBox cbHasToBeChanged;
    protected RelativeLayout subLayout_accDateChangeInt;
    protected TextView tvAccDateCreated;
    protected ImageView imgAccDateRenew;
    protected TextView tvAccDateRenewTag;
    protected TextView tvAccDateRenewValue;
    protected CoordinatorLayout coordinatorLayoutAccAct;

    //Declare five spinner adapters, one for the category spinner, one for userName, one for password and two for question list spinners
    //And respective cursors to hold data from DB
    protected int spCategoryPosition;
    //SpinnerAdapter adapterCategory;
    Cursor cursorCategory;
    //SpinnerAdapter adapterUserName;
    Cursor cursorUserName;
    //SpinnerAdapter adapterPsswrd;
    Cursor cursorPsswrd;
    //SpinnerAdapter adapterQuestionList;
    Cursor cursorQuestionList;
    //SpinnerAdapter adapterListOfQuestionsAvailable;
    Cursor cursorListOfQuestionsAvailable;

    //Other attributes
    boolean isFavorite = false;
    IconAdapter iconAdapter = null;
    int throwSelectLogoActReqCode = 5555;
    Icon logo = null;
    int selectedPosition = -1;
    protected boolean isInnerActivityLaunched = false;


    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateDispAcc","Enter onCreate method in the DisplayAccountActivity abstract class.");

        this.extras = getIntent().getExtras();
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Set correct language
        MainActivity.setAppLanguage(this);
        //Set layout for this activity
        setContentView(R.layout.activity_add_account);
        ActionBar toolbar = getActionBar();
        //Get DB handler cass from the home fragment
        this.accountsDB = HomeFragment.getAccountsDB();

        //Initialize layout coordinator required to use Snackbar
        coordinatorLayoutAccAct = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutAccAct);

        //Initialize view objects from layout to have access to them and set different texts and other properties
        //Logo related variables
        this.imgAccLogo = (ImageView) findViewById(R.id.imgAccLogo);
        this.imgAccLogo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                displayAlertDialogForImgSource();
            }
        });//End of setOnClickListener
        //Initialize logo with default app logo
        this.logo = MainActivity.getMyPsswrdSecureLogo();

        //Initialize Account related variables
        //Account name attribute
        this.etAccountName = (EditText) findViewById(R.id.etAccountName);
        //Set the description input text hint to refer to an account name
        this.etAccountName.setHint(R.string.addEditAccName);
        //Initialize Category spinner related variables
        this.spCategory = (NoDefaultSpinner) findViewById(R.id.spCategory);
        //Initialize User name spinner related variables
        this.spAccUserName = (NoDefaultSpinner) findViewById(R.id.spAccUserName);
        this.btnAccNewUserName = (Button) findViewById(R.id.btnAccNewUserName);
        this.btnAccNewUserName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Call method to throw AddUserNameActivity
                throwActivityNoExtras(AddUserNameActivity.class, MainActivity.getThrowAddUsernameActReqcode());
            }//End of onClick method
        });//End of setOnClickListener method

        //Initialize Psswrd spinner related variables
        this.spAccPsswrd = (NoDefaultSpinner) findViewById(R.id.spAccPsswrd);
        this.btnAccNewPsswrd = (Button) findViewById(R.id.btnAccNewPsswrd);
        this.btnAccNewPsswrd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Call method to throw AddPssswrdActivity
                throwActivityNoExtras(AddPsswrdActivity.class, MainActivity.getThrowAddPsswrdActReqcode());
            }//End of onClick method
        });//End of setOnClickListener method
        //Initialize QuestionList relates variables
        this.btnAccRemoveQuestion = findViewById(R.id.btnAccRemoveQuestion);
        this.btnAccRemoveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call method to remove question from Security Question list
                removeQuestionFromSecList();
            }
        });//End of setOnClickListener
        this.btnAccNewSecQuestion = findViewById(R.id.btnAccNewSecQuestion);
        this.btnAccNewSecQuestion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Call method to throw AddQuestionActivity
                throwActivityNoExtras(AddQuestionActivity.class,MainActivity.getThrowAddQuestionActReqcode());
            }
        });//End of setOnClickListener method
        this.spAccSecQuestionList = (NoDefaultSpinner) findViewById(R.id.spAccSecQuestionList);
        this.spAccSecQuestionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Make the remove question button visible
                btnAccRemoveQuestion.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Make the remove question button invisible
                btnAccRemoveQuestion.setVisibility(View.INVISIBLE);
            }
        });//End of setOnItemSelectedListener method
        this.spQuestionsAvailable = (NoDefaultSpinner) findViewById(R.id.spQuestionsAvailable);
        this.spQuestionsAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Enable the add question button
                if(spAccSecQuestionList.getAdapter().getCount()<3){
                    btnAccAddQuestion.setEnabled(true);
                }else{
                    btnAccAddQuestion.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Disable the add question button
                btnAccAddQuestion.setEnabled(false);
            }
        });//End of setOnItemSelectedListener method
        this.btnAccAddQuestion = (Button) findViewById(R.id.btnAccAddQuestion);
        this.btnAccAddQuestion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Call method to add question from question available list to security question list
                addQuestionToSecList(spQuestionsAvailable.getSelectedItemPosition());
            }//End of onClick method
        });//End of setOnClickListener method


        //Initialize isFavorite related variables
        this.imgAccIsFavorite = (ImageView) findViewById(R.id.imgAccIsFavorite);
        this.imgAccIsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call method to toggle boolean value and set proper star icon
                toggleIsFavorite();
            }
        });//End of setOnClickListener method
        this.tvAccIsFavTag = (TextView) findViewById(R.id.tvAccIsFavTag);
        this.tvAccIsFavTag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Call method to toggle boolean value and set proper star icon
                toggleIsFavorite();
            }
        });//End of setOnClickListener method


        //Initialize Date created related variables
        this.tvAccDateCreated = findViewById(R.id.tvAccDateCreated);

        //Initialize Date change related variables
        this.cbHasToBeChanged = (CheckBox) findViewById(R.id.cbHasToBeChanged);
        this.cbHasToBeChanged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showChangeDateLayout(isChecked);
            }
        });
        this.subLayout_accDateChangeInt = findViewById(R.id.subLayout_accDateChangeInt);
        this.tvAccDateRenewTag = findViewById(R.id.tvAccDateRenewTag);
        this.tvAccDateRenewTag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });
        this.tvAccDateRenewValue = findViewById(R.id.tvAccDateRenewValue);
        this.tvAccDateRenewValue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });
        this.imgAccDateRenew = findViewById(R.id.imgAccDateRenew);
        this.imgAccDateRenew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeDate();
            }
        });

        //Setup the Category spinner and populate with data
        this.cursorCategory = this.accountsDB.getCategoryListCursor();
        this.setUpSpinnerData(this.cursorCategory,spCategory,CATEGORY_SPINNER);
        //Setup the UserName spinner and populate with data
        this.cursorUserName = this.accountsDB.getUserNameList();
        this.setUpSpinnerData(this.cursorUserName,spAccUserName,USERNAME_SPINNER);
        //Setup the Psswrd spinner and populate with data
        this.cursorPsswrd = this.accountsDB.getPsswrdList();
        this.setUpSpinnerData(this.cursorPsswrd, spAccPsswrd,PSSWRD_SPINNER);
        //Setup the Security Question List spinner
        //Call method to configure security question list spinner. Use a Dummy cursor to be able to setup prompt.
        //This dummy cursor will held one question item but wont be displayed
        this.initSecQuestionListSpinner();
        //Call method to setup the Questions Available spinner and populate with data
        this.initQuesitonAvailableListSpinner();
        //Initialize the iconAdapter to be able to communicate with SelectLogoAct and find the selected logo in this Activity
        this.iconAdapter = new IconAdapter(this,MainActivity.getAccountsLogos());
        Log.d("OnCreateDispAcc","Exit onCreate method in the DisplayAccountActivity abstract class.");
    }//End of onCreate method

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResumeMain", "Enter onResume method in DisplayAccountActivity class.");
        //Call MainActivity static method to check for logout timeout to display logout prompt accordingly
        MainActivity.checkLogOutTimeOut(this);
        Log.d("onResumeMain", "Exit onResume method in DisplayAccountActivity class.");
    }//End of onResume method

    public void onStop(){
        super.onStop();
        Log.d("onStopMain", "Enter onStop method in DisplayAccountActivity class.");
        MainActivity. checkForNotificationSent(this,false);
        //Check flag to see if current activity is closing to open any AddItemActivity children
        //In those cases, decrypt data service should not be stopped.
        if(!this.isInnerActivityLaunched){
            Intent iService = new Intent(this,DecryptDataService.class);
            this.stopService(iService);
        }
        Log.d("onStopMain", "Exit onStop method in DisplayAccountActivity class.");
    }//End of onStop method


    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        //Call super method
        super.onSaveInstanceState(saveState);
        Log.d("onSaveInstanceState", "Enter onSaveInstanceState method in the a AddAccountActivity class.");
        //Record input data that is not saved by OS already
        //Fields save by OS: Account Name edit text,
        //The date created is generated itself during onCreate method
        //Save all the input fields first
        //Save te Account name input in edit text field
        saveState.putString("etAccountNameText",this.etAccountName.getText().toString());
        //Save te Category spinner item position selected
        saveState.putInt("spCategorySelectedPosition",this.spCategory.getSelectedItemPosition());
        //Save te UserName spinner item position selected
        saveState.putInt("spUserNameSelectedPosition",this.spAccUserName.getSelectedItemPosition());
        //Save te Password spinner item position selected
        saveState.putInt("spPsswrdSelectedPosition",this.spAccPsswrd.getSelectedItemPosition());
        saveState.putBoolean("spAccSecQuestionListIsEnabled",this.spAccSecQuestionList.isEnabled());
        //Check if Security question spinner is enable
        if(this.spAccSecQuestionList.isEnabled()){
            //Check number of questions and store their QuestionIDs
            QuestionList questionList = this.extractQuestionsFromSpinner(this.spAccSecQuestionList);
            saveState.putInt("numberOfQuestionsInList",questionList.getSize());
            for(int i=0;i<questionList.getSize();i++){
                saveState.putInt("questionID"+(i+1),questionList.getQuestions().get(i).get_id());
            }
            //If so, save the current position
            saveState.putInt("spSecQuestListSelectedPos",this.spAccSecQuestionList.getSelectedItemPosition());
        }else{
            //Otherwise, save -1 (even though there's a dummy item selected)
            saveState.putInt("spSecQuestListSelectedPos",-1);
        }//End of if else statement to check if question list dropdown is enabled
        //Save the Question Available spinner item position selected
        saveState.putInt("spQuestAvailableSelectedPos",this.spQuestionsAvailable.getSelectedItemPosition());
        //Save the current isFavorite attribute state
        saveState.putBoolean("isFavorite",this.isFavorite);
        //Save the checkbox state of "Has to be changed?" checkbox
        saveState.putBoolean("cbHasToBeChanged",this.cbHasToBeChanged.isChecked());
        //If the checkbox is ticked, save the password renew date
        if(this.cbHasToBeChanged.isChecked()){
            saveState.putString("tvDateRenewValueText",this.tvAccDateRenewValue.getText().toString());
        }
        //Save the current icon
        if(this.logo.getLocation().equals(MainActivity.getRESOURCES())){
            //If logo comes from resources save the logoLocation and selected position, so the same logo can be retrieved later on
            saveState.putString("logoLocation",MainActivity.getRESOURCES());
            saveState.putInt("logoListPosition",this.selectedPosition);
            saveState.putString("logoName",this.logo.getName());
        }else if(this.logo.getLocation().equals(String.valueOf(R.mipmap.ic_my_psswrd_secure_new))){
            saveState.putString("logoLocation",String.valueOf(R.mipmap.ic_my_psswrd_secure_new));
            saveState.putInt("logoListPosition",R.mipmap.ic_my_psswrd_secure_new);
        }else if(this.logo.getLocation().startsWith(MainActivity.getExternalImageStorageClue())){
            saveState.putString("logoName",this.logo.getName());
            saveState.putString("logoLocation",this.logo.getLocation());
        }//End of if else statement to check the logo location
        Log.d("onSaveInstanceState", "Exit onSaveInstanceState method in the a AddAccountActivity class.");
    }//End of onSaveInstanceState

    @Override
    protected void onRestoreInstanceState(Bundle restoreState) {
        //Call the super method
        super.onRestoreInstanceState(restoreState);
        Log.d("onRestoreInstanceState", "Enter onRestoreInstanceState method in the a AddAccountActivity class.");
        //Check the stored state is not null
        if (restoreState != null){
            //Populate Account name
            this.etAccountName.setText(restoreState.getString("etAccountNameText"));
            //Set up current Category spinner position
            int spCategorySelectedPosition = restoreState.getInt("spCategorySelectedPosition");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spCategorySelectedPosition != -1){
                this.spCategory.setSelection(spCategorySelectedPosition);
            }else{
                //Set up Category spinner from beginning
                //Setup the Category spinner and populate with data
                this.cursorCategory = this.accountsDB.getCategoryListCursor();
                this.setUpSpinnerData(this.cursorCategory,this.spCategory,this.CATEGORY_SPINNER);
            }//End of if else statement  that checks the selected position in the category  spinner

            //Set up current UserName spinner position
            int spUserNameSelectedPosition = restoreState.getInt("spUserNameSelectedPosition");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spUserNameSelectedPosition != -1){
                this.spAccUserName.setSelection(spUserNameSelectedPosition);
            }else{
                //Set up UserName spinner from beginning
                this.cursorUserName = this.accountsDB.getUserNameList();
                this.setUpSpinnerData(this.cursorUserName,this.spAccUserName,this.USERNAME_SPINNER);
            }//End of if else statement that checks the selected position in the user name spinner

            //Set up current Password spinner position
            int spPsswrdSelectedPosition = restoreState.getInt("spPsswrdSelectedPosition");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spPsswrdSelectedPosition != -1){
                this.spAccPsswrd.setSelection(spPsswrdSelectedPosition);
            }else{
                //Set up UserName spinner from beginning
                this.cursorPsswrd = this.accountsDB.getPsswrdList();
                this.setUpSpinnerData(this.cursorPsswrd,this.spAccPsswrd,this.PSSWRD_SPINNER);
            }//End of if else statement that checks the selected position in the password spinner

            //Check the sec question list spinner is enabled (ignores dummy question cursor)
            if(restoreState.getBoolean("spAccSecQuestionListIsEnabled")){
                //Set up current Security question spinner position
                int spSecQuestListSelectedPos = restoreState.getInt("spSecQuestListSelectedPos");
                //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
                //Set up Sec question list spinner from beginning passing the questionsID to get the cursor
                switch(restoreState.getInt("numberOfQuestionsInList")){
                    case 1:
                        this.cursorQuestionList = this.accountsDB.getQuestionCursorByID(restoreState.getInt("questionID1"));
                        break;
                    case 2:
                        this.cursorQuestionList = this.accountsDB.getQuestionCursorByID(restoreState.getInt("questionID1"),restoreState.getInt("questionID2"));
                        break;
                    case 3:
                        this.cursorQuestionList = this.accountsDB.getQuestionCursorByID(restoreState.getInt("questionID1"),restoreState.getInt("questionID2"),restoreState.getInt("questionID3"));
                        break;
                }//End of switch statement
                //Set up the spinner data once the cursor has been generated based on number of questions in list
                this.setUpQuestionListSpinnerData(this.cursorQuestionList,this.spAccSecQuestionList);
                //Check spinner position selected (The spinner could hold questions but none is selected, display proper prompt in each case)
                if(spSecQuestListSelectedPos != -1){
                    //Just move spinner to correct item position
                    this.spAccSecQuestionList.setSelection(spSecQuestListSelectedPos);
                }else{
                    //Setup spinner prompt by getting proper text after calling method to get text
                    this.spAccSecQuestionList.setPrompt(this.getSecQuestListPrompt(this.cursorQuestionList.getCount()));
                }//End of if else statement to check spinner position selected (The spinner could hold questions but none is selected)
            }else{
                this.initSecQuestionListSpinner();
            }//End of if statement that checks the sec question list spinner is enabled (ignores dummy question cursor)

            //Set up current Question available  spinner position
            int spQuestAvailableSelectedPos = restoreState.getInt("spQuestAvailableSelectedPos");
            //Check the position, if set to -1 then reconfigure spinner from scratch to get prompt, otherwise move to position
            if(spQuestAvailableSelectedPos != -1){
                this.spQuestionsAvailable.setSelection(spQuestAvailableSelectedPos);
            }else{
                //Set up Question available spinner from beginning
                this.initQuesitonAvailableListSpinner();
            }//End of if else statement to check the selected position for the list of question available

            //Setup the isFav image accordingly
            this.isFavorite = !restoreState.getBoolean("isFavorite");
            this.toggleIsFavorite();
            //Setup the checkbox state
            boolean isChecked = restoreState.getBoolean("cbHasToBeChanged");
            if(isChecked){
                this.cbHasToBeChanged.setChecked(isChecked);
                this.tvAccDateRenewValue.setText(restoreState.getString("tvDateRenewValueText"));
            }//End of if statement that checks the checkbox is ticked

            //Setup the account logo icon
            String logoLocation = restoreState.getString("logoLocation");
            if(logoLocation.equals(MainActivity.getRESOURCES())){
                //If current logo comes from resources update this.logo attribute with proper icon object from icon adapter
                this.selectedPosition = restoreState.getInt("logoListPosition");
                if(this.selectedPosition == -1){
                    MainActivity.setAccountLogoImageFromRes(this.imgAccLogo,getBaseContext(),account.getIcon().getName());
                    this.logo = this.accountsDB.getIconByName(account.getIcon().getName());
                }else{
                    this.logo = this.iconAdapter.getIconList().get(this.selectedPosition);
                    //set up the image view with correct logo image
                    this.imgAccLogo.setImageResource(this.iconAdapter.getIconList().get(restoreState.getInt("logoListPosition")).getResourceID());
                }
            }else if(logoLocation.startsWith(MainActivity.getExternalImageStorageClue())){
                //If current logo object is an external image, store logo name and logo uri location, to recreate the object again
                this.logo = new Icon(restoreState.getString("logoName"),logoLocation);
                //set up the image view with correct logo image
                this.imgAccLogo.setImageURI(Uri.parse(logoLocation));
            }else{
                this.logo = MainActivity.getMyPsswrdSecureLogo();
                this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure_new);
            }//End of if else statement to check the logo location and set up logo object and img accordingly based on the source.
            //No need to set up the app logo, as this is set up during onCreate method


            //Buttons configuration
            //Setup Remove button visibility
            if(this.spAccSecQuestionList.getSelectedItemPosition() != -1){
                this.btnAccRemoveQuestion.setVisibility(View.VISIBLE);
            }else{
                this.btnAccRemoveQuestion.setVisibility(View.INVISIBLE);
            }//End of if else statement to setup remove question button visibility

            //Setup New Question button enable property
            if(this.spAccSecQuestionList.getAdapter().getCount() == 3){
                this.btnAccNewSecQuestion.setEnabled(false);
            }//End of if statement to setup new question button enable property
        }//End of if statement to check the restore state isn't null
        Log.d("onRestoreInstanceState", "Exit onRestoreInstanceState method in the a AddAccountActivity class.");
    }//End of onRestoreInstanceState method

    //Method to setup dummy cursor for security question spinner when initializing it
    protected void initSecQuestionListSpinner(){
        Log.d("initSQListSpinner","Enter initSecQuestionListSpinner method in the DisplayAccountActivity abstract class.");
        this.cursorQuestionList = accountsDB.getQuestionCursorByID(1);
        this.spAccSecQuestionList.setPrompt(getString(R.string.account_quest_list_spinner_prompt));
        this.setUpQuestionListSpinnerData(cursorQuestionList,spAccSecQuestionList);
        //Disable the Security question spinner so user wont be able to see dummy item in spinner
        this.spAccSecQuestionList.setEnabled(false);
        Log.d("initSQListSpinner","Enter initSecQuestionListSpinner method in the DisplayAccountActivity abstract class.");
    }//End of initSecQuestionListSpinner method


    protected void initQuesitonAvailableListSpinner(){
        Log.d("initQAListSpinner","Enter initQuesitonAvailableListSpinner method in the DisplayAccountActivity abstract class.");
        this.cursorListOfQuestionsAvailable = accountsDB.getListQuestionsAvailable();
        if(cursorListOfQuestionsAvailable.moveToFirst()){
            this.spQuestionsAvailable.setPrompt(getString(R.string.account_quest_avilab_spinner_prompt));
            this.setUpQuestionListSpinnerData(cursorListOfQuestionsAvailable,spQuestionsAvailable);
        }else{
            //Workaround to display prompt when no answers are available
            this.cursorListOfQuestionsAvailable = accountsDB.getQuestionCursorByID(1);
            this.spQuestionsAvailable.setPrompt(getString(R.string.account_quest_avilab_noAnswers));
            this.setUpQuestionListSpinnerData(cursorListOfQuestionsAvailable,spQuestionsAvailable);
            //Disable the Security question spinner so user wont be able to see dummy item in spinner
            this.spQuestionsAvailable.setEnabled(false);
        }//End of if else statement that checks the cursor isn't empty or null
        Log.d("initQAListSpinner","Exit initQuesitonAvailableListSpinner method in the DisplayAccountActivity abstract class.");
    }//End of initQuesitonAvailableListSpinner method


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the DisplayAccountActivity abstract class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_delete_save_cancel, menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the DisplayAccountActivity abstract class.");
        return true;
    }//End of onCreateOptionsMenu method



    //Method to set up spinner data by passing in a cursor and the adapter as arguments
    protected void setUpSpinnerData(Cursor cursor,Spinner sp,int type){
        SpinnerAdapter adapterCategory = null;
        SpinnerArrayAdapter adapterUserNamePsswrd = null;
        Log.d("setUpSpinnerData","Enter the setUpSpinnerData method in the DisplayAccountActivity class.");
        //Get resources for displaying spinner
        String spHint = "";
        String etHint = "";
        Resources res = getResources();
        switch(type){
            case CATEGORY_SPINNER:
                Log.d("setUpSpinnerData","CATEGORY_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectCatHint);
                //Create new spinner adapter object
                adapterCategory = new SpinnerAdapter(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
                break;
            case USERNAME_SPINNER:
                Log.d("setUpSpinnerData","USERNAME_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectUserHint);
                etHint = res.getString(R.string.selectUserHint);
                //Create new spinner adapter object
                //adapter = new SpinnerAdapterEncrypted(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
                adapterUserNamePsswrd = new SpinnerArrayAdapter(this,R.layout.spinner_item_string_value, R.id.tvItem,DecryptDataService.getDecryptedUserNameList().toArray());
                break;
            case PSSWRD_SPINNER:
                Log.d("setUpSpinnerData","PSSWRD_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectPsswrdHint);
                etHint = res.getString(R.string.selectPsswrdHint);
                //Create new spinner adapter object
                //adapterUserNamePsswrd = new SpinnerArrayAdapter(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
                adapterUserNamePsswrd = new SpinnerArrayAdapter(this,R.layout.spinner_item_string_value, R.id.tvItem,DecryptDataService.getDecryptedPsswrdList().toArray());
                break;
            default:
                spHint = "";
                break;
        }//End of switch statement
        //Check the cursor isn't empty or is null to setup the prompt
        if(cursor != null && cursor.getCount() >0){
            sp.setPrompt(spHint);
        }else{
            //Disable the spinner if cursor has no data
            sp.setEnabled(false);
        }// End of if else statement to check cursor isn't null or empty
        //Set the adapter for the Category spinner
        if(type == USERNAME_SPINNER || type == PSSWRD_SPINNER){
            sp.setAdapter(adapterUserNamePsswrd);
        }else{
            sp.setAdapter(adapterCategory);
        }//End of if else statement to set the proper adapter based on the type of spinner
        Log.d("setUpSpinnerData","Exit the setUpSpinnerData method in the DisplayAccountActivity class.");
    }//End of setUpSpinnerData method

    //Method to setup question list spinner data
    protected void setUpQuestionListSpinnerData(Cursor cursor,Spinner sp){
        SpinnerAdapterQuestion adapter = null;
        Log.d("setUpQuestListSpData","Enter the setUpQuestionListSpinnerData method in the DisplayAccountActivity class.");
        adapter = new SpinnerAdapterQuestion(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
        sp.setAdapter(adapter);
        sp.setEnabled(true);
        Log.d("setUpQuestListSpData","Exit the setUpQuestionListSpinnerData method in the DisplayAccountActivity class.");
    }//End of setUpQuestionListSpinnerData

    //Method to throw a new Activity
    private void throwActivityNoExtras(Class className,int requestCode) {
        Log.d("throwActivityNoExtras", "Enter throwActivityNoExtras method in the DisplayAccountActivity class.");
        //Declare and instantiate a new intent object
        Intent i = new Intent(this,className);
        //Start the AddItemActivity class
        i.putExtra("isActivityThrownFromDisplayAct",true);
        //Set boolean flag to keep decrypt service running
        this.isInnerActivityLaunched = true;
        if(requestCode > 0){
            startActivityForResult(i, requestCode);
            Log.d("throwActivityNoExtras", "startActivityForResult called by throwActivityNoExtras method in the DisplayAccountActivity class with request code: "+requestCode);
        }else{
            startActivity(i);
            Log.d("throwActivityNoExtras", "startActivity called by throwActivityNoExtras method in the DisplayAccountActivity class.");
        }
        Log.d("throwActivityNoExtras", "Exit throwActivityNoExtras method in the MainActivity class.");
    }//End of throwActivityNoExtras method

    //Method to add a question from question available list to security question list spinner
    protected void addQuestionToSecList(int questionPositionInAvailableList){
        Log.d("addQuestionToSecList","Enter the addQuestionToSecList method in the DisplayAccountActivity class.");
        Cursor c = null;
        boolean repeatedQuestion = false;
        //Get its DB _id number
        int selectedQuestionID = (int) this.spQuestionsAvailable.getAdapter().getItemId(questionPositionInAvailableList);
        //Check if spinner is holding dummy item by checking if spinner is disabled
        if(!this.spAccSecQuestionList.isEnabled()){
            //If spinner is disabled and holding a dummy item, set new adapter with null cursor
            this.spAccSecQuestionList.setAdapter(new SpinnerAdapterQuestion(this,c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
        }//End of if statement to check for dummy items
        //Check how many questions the list already has. Max number allowed is 3 questions
        switch(this.spAccSecQuestionList.getCount()){
            //No question in the security question list
            case 0:
                Log.d("addQuestionToSecList","No questions in the Security Question list while adding a question on addQuestionToSecList method in the DisplayAccountActivity class.");
                //Use new cursor to get question for that specific DB _id
                c = accountsDB.getQuestionCursorByID(selectedQuestionID);
                break;
                // 1 Question in the security question list
            case 1:
                //Check if spinner is disabled, if it is then the item in list is a dummy one
                Log.d("addQuestionToSecList","1 question in the Security Question list while adding a question on addQuestionToSecList method in the DisplayAccountActivity class.");
                //Find id of question already in the list
                int _id1 = (int) this.spAccSecQuestionList.getAdapter().getItemId(0);
                //Check the question in the security question list is not the same selected form question available list spinner
                if(_id1 != selectedQuestionID){
                    c = accountsDB.getQuestionCursorByID(_id1,selectedQuestionID);
                }else{
                    //In case it is, set the boolean flag for repeated question to true
                    repeatedQuestion = true;
                }//End of if else statement
                break;
                //2 questions in the security question list
            case 2:
                Log.d("addQuestionToSecList","2 questions in the Security Question list while adding a question on addQuestionToSecList method in the DisplayAccountActivity class.");
                //Find id of two questions already in the list
                _id1 = (int) this.spAccSecQuestionList.getAdapter().getItemId(0);
                int _id2 = (int) this.spAccSecQuestionList.getAdapter().getItemId(1);
                //Check the questions in the security question list is not the same selected form question available list spinner
                if(_id1 != selectedQuestionID && _id2 != selectedQuestionID){
                    c = accountsDB.getQuestionCursorByID(_id1,_id2,selectedQuestionID);
                    //When list reaches it max number disable the questions available spinner and Add question button
                    this.spQuestionsAvailable.setEnabled(false);
                    this.btnAccAddQuestion.setEnabled(false);
                    //Disable the New Question button too
                    this.btnAccNewSecQuestion.setEnabled(false);
                }else{
                    //if it's the same set boolean flag for repeated question to true
                    repeatedQuestion = true;
                }//End of if else statement to check for repeated questions
                break;
            default:
                break;
        }//End of switch statement
        //Populate the questionList spinner with Question data
        if(!repeatedQuestion){
            //Setup spinner prompt by getting proper text after calling method to get text
            this.spAccSecQuestionList.setPrompt(this.getSecQuestListPrompt(c.getCount()));
            //Setup the spinner data
            this.setUpQuestionListSpinnerData(c,this.spAccSecQuestionList);
        }else{
            //If question is repeated, display a toast indicating so
            MainActivity.displayToast(this,getString(R.string.accountRepeatQuestion),Toast.LENGTH_SHORT,Gravity.CENTER);
        }//End of if statement to check it's not a repeated question
        Log.d("addQuestionToSecList","Exit the addQuestionToSecList method in the DisplayAccountActivity class.");
    }//End of addQuestionToSecList method

    //Method used to remove a selected question from Security Question list spinner
    protected void removeQuestionFromSecList(){
        Log.d("removeQuestFromSecList","Enter the removeQuestionFromSecList method in the DisplayAccountActivity class.");
        //Find the position of question selected in the Sec Question spinner
        int selectedQuestionPosition = this.spAccSecQuestionList.getSelectedItemPosition();
        //Initialize a cursor, used to hold the new list of questions once selected question is removed from spinner
        Cursor c = null;
        //Get DB _id number of question selected in the security question list spinner
        int selectedQuestionID = (int) this.spAccSecQuestionList.getAdapter().getItemId(selectedQuestionPosition);
        //Check the current number of questions on the security question list spinner
        switch(this.spAccSecQuestionList.getCount()){
            //no question is not possible as the Remove question button is not visible in that case
            //1 question in the list
            case 1:
                Log.d("removeQuestFromSecList","1 question in the Security Question list while removing a question on removeQuestFromSecList method in the DisplayAccountActivity class.");
                //Once question is removed, make the remove question button invisible as there's nothing to remove
                break;
            case 2:
                Log.d("removeQuestFromSecList","2 questions in the Security Question list while removing a question on removeQuestFromSecList method in the DisplayAccountActivity class.");
                //Call method to get not selected question DB id
                int _id1 = (int) this.getNewQuestionList(selectedQuestionID).get(0);
                //Initialize cursor with the only question left in the spinner
                c = accountsDB.getQuestionCursorByID(_id1);
                break;
            case 3:
                Log.d("removeQuestFromSecList","3 questions in the Security Question list while removing a question on removeQuestFromSecList method in the DisplayAccountActivity class.");
                //Call method to get not selected questions' DB id
                _id1 = (int) this.getNewQuestionList(selectedQuestionID).get(0);
                int _id2 = (int) this.getNewQuestionList(selectedQuestionID).get(1);
                //Populate cursor with two questions left in the spinner
                c = accountsDB.getQuestionCursorByID(_id1,_id2);
                //Enable the questions available spinner, the add question button and the new question button
                //As the max number of question isn't reached any longer
                this.spQuestionsAvailable.setEnabled(true);
                this.btnAccAddQuestion.setEnabled(true);
                this.btnAccNewSecQuestion.setEnabled(true);
                break;
            default:
                //In any other case make the remove button invisible
                this.btnAccRemoveQuestion.setVisibility(View.INVISIBLE);
                break;
        }//End of switch statement
        //Check cursor to populate security question list spinner isn't null
        if(c != null){
            //Setup spinner prompt by getting proper text after calling method to get text
            this.spAccSecQuestionList.setPrompt(getSecQuestListPrompt(c.getCount()));
            //Setup the spinner data
            this.setUpQuestionListSpinnerData(c,this.spAccSecQuestionList);
        }else{
            //Use a Dummy cursor again to display spinner prompt
            c = accountsDB.getQuestionCursorByID(1);
            this.spAccSecQuestionList.setPrompt(getResources().getString(R.string.account_quest_list_spinner_prompt));
            //setup new adapter for the security question list spinner, with a null cursor so it doesn't display any question
            this.spAccSecQuestionList.setAdapter(new SpinnerAdapterQuestion(this,c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
            this.spAccSecQuestionList.setEnabled(false);
        }//End of if statement to check cursor isn't null
        this.btnAccRemoveQuestion.setVisibility(View.INVISIBLE);
        Log.d("removeQuestFromSecList","Exit the removeQuestionFromSecList method in the DisplayAccountActivity class.");
    }//End of removeQuestFromSecList method

    //Method to setup correctly security question list spinner prompt
    protected String getSecQuestListPrompt(int numberOfQuestions){
        Log.d("getSecQuestListPrompt","Enter the getSecQuestListPrompt method in the DisplayAccountActivity class.");
        String prompt = "";
        String questionText = "";
        if(numberOfQuestions > 1){
            questionText = getResources().getString(R.string.account_quest_list_spinner_prompt3);
        }else{
            questionText = getResources().getString(R.string.account_quest_list_spinner_prompt2);
        }
        prompt = getResources().getString(R.string.account_quest_list_spinner_prompt1) +" " + numberOfQuestions + " "+ questionText;
        Log.d("getSecQuestListPrompt","Exit the getSecQuestListPrompt method in the DisplayAccountActivity class.");
        return prompt;
    }//End of getSecQuestListPrompt

    //Method to return DB ids of questions left in the security question list, when removing a question
    protected ArrayList getNewQuestionList(int selectedID){
        Log.d("getNewQuestionList","Enter the getNewQuestionList method in the DisplayAccountActivity class.");
        ArrayList questionsIds = new ArrayList();
        boolean found = false;
        for(int i=0;i< this.spAccSecQuestionList.getAdapter().getCount();i++){
            if(selectedID != this.spAccSecQuestionList.getAdapter().getItemId(i) ){
                questionsIds.add((int)this.spAccSecQuestionList.getAdapter().getItemId(i));
            }//End of if statement to check the current question isn't the one to be removed
        }//End of for loop to keep questions to be left in the security question list spinner
        Log.d("getNewQuestionList","Exit successfully the getNewQuestionList method in the DisplayAccountActivity class.");
        return questionsIds;
    }//End of getNewQuestionList method

    //Method to show/hide the Change date layout
    protected void showChangeDateLayout(boolean isChecked){
        Log.d("showChangeDateLay","Enter the showChangeDateLayout method in the DisplayAccountActivity class.");
        //Check the isChecked argument is true
        if(isChecked){
            //If it is true, make hour and date layouts visible
            this.subLayout_accDateChangeInt.setVisibility(View.VISIBLE);
        }else{
            //Otherwise, make hour and date layouts invisible and not use any layout room
            this.subLayout_accDateChangeInt.setVisibility(View.GONE);
        }//End of if else statement
        Log.d("showChangeDateLay","Exit the showChangeDateLayout method in the DisplayAccountActivity class.");
    }//End of hideAppointmentDetails method

    //Method to toggle isFavorite attribute and display star accordingly
    protected void toggleIsFavorite(){
        Log.d("toggleIsFavorite","Enter the toggleIsFavorite method in the DisplayAccountActivity class.");
        //Change icon image
        if(!this.isFavorite){
            //Change image to colour star icon
            this.imgAccIsFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            this.tvAccIsFavTag.setText(R.string.account_isFavoriteChecked);
            //Update the isFavorite attribute to true
            this.isFavorite = true;
        }else{
            //Change image to colourless star icon
            this.imgAccIsFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            this.tvAccIsFavTag.setText(R.string.account_isFavoriteUnchecked);
            //Update the isFavorite attribute to false
            this.isFavorite = false;
        }//End of if else statement to check isFavorite state
        Log.d("toggleIsFavorite","Exit the toggleIsFavorite method in the DisplayAccountActivity class.");
    }// End of toggleIsFavorite method

    //Method to set text of Date created field
    protected void setDateText(TextView tvDate,long timeInMills){
        Log.d("setDateText","Enter the setDateText method in the DisplayAccountActivity class.");
        //Set up current date into the Created date field
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeInMills));
        tvDate.setText(new SimpleDateFormat(MainActivity.getDateFormat()).format(calendar.getTime()));
        Log.d("setDateText","Enter the setDateText method in the DisplayAccountActivity class.");
    }//End of setDateText method

    //Method to throw the SelectLogoActivity
    protected void throwSelectLogoActivity(){
        Log.d("throwSelectLogoActivity","Enter the throwSelectLogoActivity method in the DisplayAccountActivity class.");
        //Set boolean flag to keep decrypt service running
        this.isInnerActivityLaunched = true;
        //Declare and instantiate a new intent object
        Intent i= new Intent(this, SelectLogoActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        // the current logo data which is sent back if select logo is cancel or updated if new logo has been selected
        i.putExtra("selectedImgPosition",selectedPosition);
        i.putExtra("selectedImgLocation",logo.getLocation());
        //Start the addTaskActivity and wait for result
        startActivityForResult(i,this.throwSelectLogoActReqCode);
        Log.d("throwSelectLogoActivity","Exit the throwSelectLogoActivity method in the DisplayAccountActivity class.");
    }//End of throwSelectLogoActivity method

    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Reset flag that prevents decrypt service from being stopped when going to AddItemActivity. This way, service can be stopped when necessary.
        this.isInnerActivityLaunched = false;
        Log.d("onActivityResult","Enter the onActivityResult method in the DisplayAccountActivity class.");
        if (requestCode==this.throwSelectLogoActReqCode && resultCode==RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from SelectLogoActivity in the DisplayAccountActivity class.");
            //Check icon location
            if(data.getExtras().getString("selectedImgLocation").equals(MainActivity.getRESOURCES())){
                this.selectedPosition = data.getExtras().getInt("selectedImgPosition");
                this.logo = this.iconAdapter.getIconList().get(this.selectedPosition);
                this.imgAccLogo.setImageResource(this.logo.getResourceID());
            }else if(data.getExtras().getString("selectedImgLocation").equals(String.valueOf(R.mipmap.ic_my_psswrd_secure_new))){
                this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure_new);
            }//End of if else statement to check if logo comes from app resources
        }else if(requestCode==this.throwSelectLogoActReqCode && resultCode==RESULT_CANCELED){
            //In the event of receiving a cancel result, no change to be done on the current account, no logo change to be applied
            Log.d("onActivityResult","Received CANCEL result from SelectLogoActivity received by the DisplayAccountActivity class.");
        }else if(requestCode== MainActivity.getThrowAddUsernameActReqcode() && resultCode==RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddUserNameActivity received by the DisplayAccountActivity class.");
            int userNameID = data.getExtras().getInt("userNameID");
            //Update the userName object ID
            this.userName = this.accountsDB.getUserNameByID(userNameID);
            //Update decrypted list of user names
            DecryptDataService.updateList(DecryptDataService.getListTypeUserName());
            this.cursorUserName = this.accountsDB.getUserNameList();
            //Populate the user name spinner with new data set
            this.setUpSpinnerData(this.cursorUserName,this.spAccUserName,this.USERNAME_SPINNER);
            //Move spinner to new user name just inserted
            this.spAccUserName.setSelection(this.spAccUserName.getAdapter().getCount()-1);
            //Prompt the user the user name has been added and give option to undo
            Snackbar snackbar = Snackbar.make(coordinatorLayoutAccAct, R.string.snackBarUserAdded, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.snackBarUndo,new SnackBarClickHandler(this.userName,this.cursorUserName,spAccUserName));
            snackbar.show();
        }else if(requestCode== MainActivity.getThrowAddUsernameActReqcode() && resultCode==RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddUserNameActivity received by the DisplayAccountActivity class.");
        }else if(requestCode== MainActivity.getThrowAddPsswrdActReqcode() && resultCode==RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddPsswrdActivity received by the DisplayAccountActivity class.");
            int psswrdID = data.getExtras().getInt("psswrdID");
            //Update the password object
            this.psswrd = this.accountsDB.getPsswrdByID(psswrdID);
            this.cursorPsswrd = this.accountsDB.getPsswrdList();
            //Update decrypted list of passwords
            DecryptDataService.updateList(DecryptDataService.getListTypePsswrd());
            //Populate the password spinner with new data set
            this.setUpSpinnerData(this.cursorPsswrd,this.spAccPsswrd,this.PSSWRD_SPINNER);
            //Move spinner to new password just inserted
            this.spAccPsswrd.setSelection(this.spAccPsswrd.getAdapter().getCount()-1);
            //Prompt the user the user name has been added and give option to undo
            Snackbar snackbar = Snackbar.make(coordinatorLayoutAccAct, R.string.snackBarPsswrdAdded, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.snackBarUndo,new SnackBarClickHandler(this.psswrd,this.cursorPsswrd,spAccPsswrd));
            snackbar.show();
        }else if(requestCode== MainActivity.getThrowAddPsswrdActReqcode() && resultCode==RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddPsswrdActivity received by the DisplayAccountActivity class.");
        }else if(requestCode== MainActivity.getThrowAddQuestionActReqcode() && resultCode==RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from AddQuestionActivity received by the DisplayAccountActivity class.");
            //Setup the Questions Available spinner and populate with data
            this.cursorListOfQuestionsAvailable = accountsDB.getListQuestionsAvailable();
            this.spQuestionsAvailable.setPrompt(getBaseContext().getResources().getString(R.string.account_quest_avilab_spinner_prompt));
            this.setUpQuestionListSpinnerData(cursorListOfQuestionsAvailable,spQuestionsAvailable);
            this.spQuestionsAvailable.setSelection(spQuestionsAvailable.getAdapter().getCount()-1);
            this.addQuestionToSecList(this.spQuestionsAvailable.getSelectedItemPosition());
            int newQuestionPosition = this.spAccSecQuestionList.getAdapter().getCount() -1;
            Snackbar snackbar = Snackbar.make(coordinatorLayoutAccAct, R.string.snackBarQuestionAdded, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.snackBarUndo,new SnackBarClickHandler(this.extractQuestionsFromSpinner(this.spAccSecQuestionList).getQuestions().get(newQuestionPosition),this.cursorQuestionList,this.spAccSecQuestionList));
            snackbar.show();
        }else if(requestCode== MainActivity.getThrowAddQuestionActReqcode() && resultCode==RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from AddQuestionActivity received by the DisplayAccountActivity class.");
        }else if(requestCode == MainActivity.getThrowImageGalleryReqCode() && resultCode == Activity.RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from Gallery intent received by the DisplayAccountActivity class.");
            //Set the image as per path coming from the intent. The data can be parsed as an uri
            String uri = data.getDataString();
            //Set up the flags required to setup persisted permission for the URI
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //Get the list of permissions for URIs from thr contentResolver
            List<UriPermission> permissions = getContentResolver().getPersistedUriPermissions();
            //Iterate through the list of permissions to check the current URI is present
            if(permissions != null){
                int i =0;
                boolean found = false;
                while(i<permissions.size() && !found){
                    //Check each URI stored in the list of permissions
                    if(permissions.get(i).getUri().toString().equals(uri)){
                        //if current URI is in the list, check the Persisted access is granted (time > 0)
                        if(permissions.get(i).getPersistedTime() == UriPermission.INVALID_TIME){
                            //If access has not been granted, call method to setup persisted permission
                            getContentResolver().takePersistableUriPermission(Uri.parse(uri), takeFlags);
                        }//End of if statement to check persisted permission for this URI
                        //Exit loop even if permit already granted
                        found = true;
                    }//End of if statement to check URI value
                    i++;
                }//End of while loop
                //If URI not in the list, include with persisted permission
                if(!found){
                    getContentResolver().takePersistableUriPermission(Uri.parse(uri), takeFlags);
                }
            }//End of if statement to check permission list isn't null
            //Check if external file is already in use within the app, and files location is already saved on DB
            Icon selectedLogo = this.accountsDB.getIconByUriLocation(uri);
            if(selectedLogo == null){
                this.logo = new Icon("galleryImage_"+System.currentTimeMillis(),uri);
            }else{
                this.logo = selectedLogo;
            }
            this.imgAccLogo.setImageURI(Uri.parse(uri));
        }else if(requestCode == MainActivity.getThrowImageGalleryReqCode() && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from Gallery intent received by the DisplayAccountActivity class.");
        }else if(requestCode == MainActivity.getThrowImageCameraReqCode() && resultCode == Activity.RESULT_OK){
            Log.d("onActivityResult","Received GOOD result from Camera intent received by the DisplayAccountActivity class.");
            //Set the image as per path coming from the intent. The data can be parsed as an uri
            Uri uri = MainActivity.getUriCameraImage();
            if(uri != null && !uri.equals("")){
                //Check if external file is already in use within the app, and files location is already saved on DB
                Icon selectedLogo = this.accountsDB.getIconByUriLocation(uri.toString());
                if(selectedLogo == null){
                    this.logo = new Icon("CameraImage_"+System.currentTimeMillis(),uri.toString());
                }else{
                    this.logo = selectedLogo;
                }
                this.imgAccLogo.setImageURI(uri);
            }else{
                MainActivity.displayToast(this,"Error with camera", Toast.LENGTH_SHORT, Gravity.BOTTOM);
            }//End of if else statement to check uri isn't null or empty string
        }else if(requestCode == MainActivity.getThrowImageCameraReqCode() && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from Camera intent received by the DisplayAccountActivity class.");
        }//End of if statement that checks the resultCode is OK
        Log.d("onActivityResult","Exit the onActivityResult method in the DisplayAccountActivity class.");
    }//End of onActivityResult method

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("onDateSet","Enter the onDateSet method in the AddTaskActivity class.");
        //Declare and initialize a new Calendar object with current date and time
        Calendar calendar = Calendar.getInstance();
        //Set the year, month and day, which were passed in as params
        calendar.set(year,month,dayOfMonth);
        tvAccDateRenewValue.setText(getDateString(calendar));
        Log.d("onDateSet","Exit the onDateSet method in the AddTaskActivity class.");
    }// End of onDateSet method

    //Method to get the date string to be displayed in the tvDate view
    protected String getDateString(Calendar calendar){
        Log.d("Ent_getDateStr","Enter the getDateString method in the DisplayTaskActivity abstract class.");
        //Declare and instantiate a new DateFormat object to display date in current format (This format may change based on the app settings)
        SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        //Declare and instantiate a new date object, using the date set in calendar. Get the time in millisecs
        Date date = new Date(calendar.getTimeInMillis());
        Log.d("Ext_getDateStr","Exit the getDateString method in the DisplayTaskActivity abstract class.");
        return format.format(date);
    }//End of getDateString method

    //Method to change date on the DialogDateSelector class
    public void changeDate() {
        Log.d("Ent_chageDate","Enter changeDate method in the AddTaskActivity class.");
        //Declare and instantiate a new dialogDateSelector object
        DialogDateSelector dialogBox = new DialogDateSelector();
        //Set this activity to be the on date selected listener
        dialogBox.setOnDateSetListener(this);
        //Declare and instantiate arguments as a bundle object so data can be transferred (date info)
        Bundle args = new Bundle();
        //Display the dialog box for the date picker
        dialogBox.show(this.getSupportFragmentManager(), "dateSelector");
        //Check the date text is empty
        if(tvAccDateRenewValue.getText().equals("")){
            //if that is the case, add an argument to hold the current time in millisecs
            args.putLong("date",Calendar.getInstance().getTimeInMillis());
            //Set the arguments just created
            dialogBox.setArguments(args);
        }else{
            //Otherwise, declare and instantiate a new DateFormat object to define the date format
            SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
            //Declare a new date object
            Date date;
            //Use try catch block to try to read date from date  text field
            try {
                //set the date to be the one parsed from the text in the tvDate view
                date = format.parse(tvAccDateRenewValue.getText().toString());
            } catch (ParseException e) {
                //if an error comes up when parsing the expression, create a new date with current time
                date = new Date();
                //Print trace error message
                e.printStackTrace();
            }//End of try catch block
            //Add new argument with date parsed from tvDate view or the one created in case of error
            args.putLong("date",date.getTime());
            //Set the arguments into the dialog box object
            dialogBox.setArguments(args);
        }//End of if else statement to check the tvDate text is empty or not
        Log.d("Ext_chageDate","Exit changeDate method in the AddTaskActivity class.");
    }//End of changeDate method

    //Private and Protected inner classes

    //Inner Class to handle snackbar undo click events
    protected class SnackBarClickHandler implements View.OnClickListener{
        //Attributes
        Object item;
        Cursor cursor;
        SpinnerAdapter adapter;
        NoDefaultSpinner spinner;
        //Constructor method
        SnackBarClickHandler(Object item,Cursor cursor, NoDefaultSpinner spinner){
            Log.d("SnackBarClickHandler","Enter the SnackBarClickHandler constructor method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
            this.item = item;
            this.cursor = cursor;
            this.spinner = spinner;
            Log.d("SnackBarClickHandler","Enter the SnackBarClickHandler constructor method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
        }//End of constructor method

        @Override
        //Method to handle click events on snackbar UNDO button
        public void onClick(View v) {
            Log.d("SnackBarOnClick","Enter the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
            int spinnerType;
            //Call DB method to delete item from DB
            if(item instanceof Question){
                //Delete the answer first
                accountsDB.deleteItem(((Question) item).getAnswer());
            }
            //Then delete the item
            accountsDB.deleteItem(item);
            //Check the type of object passed in to setup variables required to populate the respective spinner correctly
            if(item instanceof Psswrd){
                cursor =  accountsDB.getPsswrdList();
                spinnerType = PSSWRD_SPINNER;
                DecryptDataService.updateList(DecryptDataService.getListTypePsswrd());
                Log.d("SnackBarOnClick","PSSWRD object passed into the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
            }else if(item instanceof UserName){
                cursor = accountsDB.getUserNameList();
                spinnerType = USERNAME_SPINNER;
                DecryptDataService.updateList(DecryptDataService.getListTypeUserName());
                Log.d("SnackBarOnClick","USERNAME object passed into the onClick method method in the SnackBarClickHandler subclass form DisplayTaskActivity abstract class.");
            }else if(item instanceof Question){
                cursor = accountsDB.getListQuestionsAvailable();
                spinnerType = QUESTION_SPINNER;
                Log.d("SnackBarOnClick","QUESTION object passed into the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
            }else{
                cursor = null;
                spinnerType = -1;
                Log.d("SnackBarOnClick","No object type detected in the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
            }
            //Check what type of spinner is to be used and populate the respective spinner with new data set
            if(spinnerType != QUESTION_SPINNER){
                setUpSpinnerData(cursor, spinner, spinnerType);
            }else{
                spAccSecQuestionList.setSelection(spAccSecQuestionList.getAdapter().getCount()-1);
                removeQuestionFromSecList();
                setUpQuestionListSpinnerData(cursor,spQuestionsAvailable);
                btnAccAddQuestion.setEnabled(false);
            }
            Log.d("SnackBarOnClick","Exit the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
        }//End of onClick method
    }//End of SnackBarClickHandler subclass

    protected Account getAccountFromUIData(){
        Log.d("getAccountFromUIData","Enter the getAccountFromUIData method method in the DisplayTaskActivity abstract class.");
        //Declare and initialize account object to be returned
        Account account = null;
        //Declare and initialize variables to be used to construct different objects that will be used to create new account object
        String accountName = "";
        Category category = null;
        UserName userName = null;
        Psswrd psswrd = null;
        QuestionList securityQuestionList = null;
        long psswrdChangeDate = 0;
        //Start extracting data from UI
        accountName = this.etAccountName.getText().toString();
        category = accountsDB.getCategoryByID((int) this.spCategory.getSelectedItemId());
        //userName = accountsDB.getUserNameByID((int) this.spAccUserName.getSelectedItemId());
        userName = accountsDB.getUserNameByID((int) DecryptDataService.getSelectedItemID( this.spAccUserName.getSelectedItem().toString(),DecryptDataService.getListTypeUserName()));
        //psswrd = accountsDB.getPsswrdByID((int) this.spAccPsswrd.getSelectedItemId());
        psswrd = accountsDB.getPsswrdByID((int) DecryptDataService.getSelectedItemID(this.spAccPsswrd.getSelectedItem().toString(),DecryptDataService.getListTypePsswrd()));
        securityQuestionList = this.extractQuestionsFromSpinner(spAccSecQuestionList);
        psswrdChangeDate = this.getPsswrdRenewDate();
        //Check the security question list isn't empty
        if(securityQuestionList != null){
            //Check if security question list already exists in the DB. If it does exist, set it's id number to the account object.
            int secQuestionListID = getSecQuestionListID(securityQuestionList);
            if(secQuestionListID != -1){
                securityQuestionList.set_id(secQuestionListID);
            }else{
                //If the account data extraction is done from EditAccountActivity, it will be required to check what to do with
                //the old security question list, as it must be removed from DB if not in use
                if(this.account != null && this.account.get_id() > 0){
                    //Check if old securityQuestion list isn't being used, if that is the case, remove from DB and remove QuestionAssignments link to that list
                    QuestionList oldSecurityQuestionList = this.account.getQuestionList();
                    if(oldSecurityQuestionList != null){
                        ArrayList listOfAccountsUsingQuestionList = null;
                        listOfAccountsUsingQuestionList = accountsDB.getAccountsIDListUsingItemWithID(MainActivity.getQuestionList(),oldSecurityQuestionList.get_id());
                        if(listOfAccountsUsingQuestionList.size() == 0 || (listOfAccountsUsingQuestionList.size() == 1 && (int) listOfAccountsUsingQuestionList.get(0) == this.account.get_id())){
                            //This means the old question list is not being used by other account(s)
                            //Thefore the list and the questionassignments related to this list must be deleted from the DB
                            //Call generic method to delete rows from table that matches column value
                            accountsDB.deleteRowFromTable(MainActivity.getQuestionassignmentTable(),MainActivity.getQuestionListIdColumn(),oldSecurityQuestionList.get_id());
                            //Remove the question list from DB
                            accountsDB.deleteItem(oldSecurityQuestionList);
                        }//End of if statement that checks the old question list is not being used
                    }//End of if statement to check the old question list was not null or empty
                }// End of if statement to check if data extraction comes from EditAccountActivity, which means the this.account attribute won't be null
                securityQuestionList.set_id(accountsDB.addItem(securityQuestionList));
            }//End of if else statement that check the new security question list does already exists in the DB
        }//End of if statement that checks the new security list isn't null
        if(this.cbHasToBeChanged.isChecked() && psswrdChangeDate > 0){
            //Once all required objects have been created, the account object can be created
            account = new Account(accountName,userName,psswrd,category,securityQuestionList,this.logo,this.isFavorite,psswrdChangeDate);
        }else{
            account = new Account(accountName,userName,psswrd,category,securityQuestionList,this.logo,this.isFavorite);
        }
        Log.d("getAccountFromUIData","Exit the getAccountFromUIData method  in the DisplayTaskActivity abstract class.");
        return account;
    }//End of getItemFromUIData method

    //Method to get all the appointment date details coming from date and hour text views
    protected long getPsswrdRenewDate(){
        Log.d("Ent_getAppointmentDate","Enter the getAppointmentDate method in the DisplayActivity abstract class.");
        //Declare and initialize a new calendar with current time
        Calendar calendar = Calendar.getInstance();
        //Declare a new date object
        Date date;
        //Check if date text view is empty or the checkbox isn't ticked anymore, date must be set to null and therefore return 0
        if(this.tvAccDateRenewValue.getText().equals("") || !this.cbHasToBeChanged.isChecked()){
            //Set date object to null
            date = null;
        }else{
            //Otherwise, declare and instantiate a new DateFormat object to define the date format
            SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
            //Use try catch block to try to read date from date  text field
            try {
                //set the date to be the one parsed from the text in the tvDate view
                date = format.parse(tvAccDateRenewValue.getText().toString());
            } catch (ParseException e){
                //if an error comes up when parsing the expression, create a new date with current time
                date = new Date();
                //Print trace error message
                e.printStackTrace();
            }//End of try catch block
        }//End of if else statement to check the tvDate text is empty or not
        if(date != null){
            //Set date recorded in text view into the calendar object
            calendar.setTime(date);
            //Initialize the date object with date and time extracted from text views
            Log.d("Ext_getAppointmentDate","Exit successfully the getAppointmentDate method returning a renew date in the DisplayActivity abstract class.");
            return date.getTime();
        }else{
            Log.d("Ext_getAppointmentDate","Exit successfully the getAppointmentDate method returning 0 as no renew date entered in the DisplayActivity abstract class.");
            return 0;
        }//End of if else state to check the date isn't null
    }//End of getAppointmentDate method

    //Method to check password renew date is valid
    protected boolean isValidRenewDate(long renewDate){
        Log.d("isValidRenewDate","Enter the isValidRenewDate method in the DisplayActivity abstract class.");
        boolean isValid = false;
        if(this.cbHasToBeChanged.isChecked()){
            //The criteria to be valid is the renew date is in the future so it must be greater than current time in millseconds
            if(renewDate > System.currentTimeMillis()){
                isValid = true;
                Log.d("isValidRenewDate","Exit with valid date the isValidRenewDate method in the DisplayActivity abstract class.");
            }else{
                Log.d("isValidRenewDate","Exit with invalid date the isValidRenewDate method in the DisplayActivity abstract class.");
            }//End of if else statement to check the check box is ticked
        }else{
            //If checkbox not ticked, check the value of data is zero to make it valid
            if(renewDate == 0){
                isValid = true;
                Log.d("isValidRenewDate","Exit with valid date (0) the isValidRenewDate method in the DisplayActivity abstract class.");
            }
            Log.d("isValidRenewDate","Exit with invalid date the isValidRenewDate method in the DisplayActivity abstract class.");
        }//End of if else statement to check checkbox is ticked
        return isValid;
    }//End of isValidRenewDate

    //Method to extract security questions from the dropdown menu and store them in a QuestionList object
    protected QuestionList extractQuestionsFromSpinner(NoDefaultSpinner sp){
        Log.d("XtrctQuestFromSpinner","Enter the extractQuestionsFromSpinner method in the DisplayActivity abstract class.");
        //Declare and initialize object to be returned by method
        QuestionList questionList = null;
        android.widget.SpinnerAdapter adapter = sp.getAdapter();
        ArrayList questionIDs = new ArrayList<>();
        Cursor cursorQuestionList = null;
        if(sp.isEnabled()){
            for(int i=0;i<adapter.getCount();i++){
                questionIDs.add((int)adapter.getItemId(i));
            }
            switch(questionIDs.size()){
                case 1:
                    int id1 = (int) questionIDs.get(0);
                    cursorQuestionList = accountsDB.getQuestionCursorByID(id1);
                    Log.d("XtrctQuestFromSpinner","1 question in Security question list in extractQuestionsFromSpinner method in the DisplayActivity abstract class.");
                    break;
                case 2:
                    id1 = (int) questionIDs.get(0);
                    int id2 = (int) questionIDs.get(1);
                    cursorQuestionList = accountsDB.getQuestionCursorByID(id1,(int) id2);
                    Log.d("XtrctQuestFromSpinner","2 questions in Security question list in extractQuestionsFromSpinner method in the DisplayActivity abstract class.");
                    break;
                case 3:
                    id1 = (int) questionIDs.get(0);
                    id2 = (int) questionIDs.get(1);
                    int id3 = (int) questionIDs.get(2);
                    cursorQuestionList = accountsDB.getQuestionCursorByID(id1,id2,id3);
                    Log.d("XtrctQuestFromSpinner","3 questions in Security question list in extractQuestionsFromSpinner method in the DisplayActivity abstract class.");
                    break;
            }//End of switch statement
        }//If disabled, means no security question in list
        //Check cursro is not null or empty
        if(cursorQuestionList != null && cursorQuestionList.getCount() > 0){
            questionList = new QuestionList();
            cursorQuestionList.moveToFirst();
            do{
               Question question = Question.extractQuestion(cursorQuestionList);
               questionList.addQuestion(question);
           }while(cursorQuestionList.moveToNext());//End of while loop to go through the question list cursor
        }//End of if statement that checks cursor isn't empty or null
        Log.d("XtrctQuestFromSpinner","Enter the extractQuestionsFromSpinner method in the DisplayActivity abstract class.");
        return questionList;
    }//End of extractQuestionsFromSpinner method

    protected int getSecQuestionListID(QuestionList questionList){
        Log.d("getSecQuestionListID","Enter|Exit the getSecQuestionListID method in the DisplayActivity abstract class.");
        return this.accountsDB.getSecQuestionListID(questionList);
    }//End of getSecQuestionListID method

    //Method to find Item position in a cursor by passing in it's text value
    protected int getItemPositionInSpinner(Cursor cursor, int _id){
        Log.d("getPositionInSpinner","Enter the getItemPositionInSpinner method in the DisplayActivity abstract class.");
        //Declare and initialize the variables to be used and return by the method
        int position = -1;
        int i = 0;
        //Check the cursor passed in isn't null
        if(cursor !=null){
            //If not null, move to first item, it could be in any other position
            cursor.moveToFirst();
        }//End of if statement to check cursor isn't null
        //Iterate through the cursor while position is not found
        do{
            //Check the item id is iqual to the one passed in
            if(cursor.getInt(0) == _id){
                //if found, make the position equal to current iterator
                position = i;
            }else{
                //Increase counter by 1
                i++;
            }//End of if else statement to check ids
        }while (cursor.moveToNext() && position == -1);
        Log.d("getPositionInSpinner","Exit the getItemPositionInSpinner method in the DisplayActivity abstract class.");
        return position;
    }//End of the getItemPositionInSpinner method

    //Method to check if Account name is in use
    protected boolean isAccNameUsed(String accountName,int _id){
        Log.d("isAccNameUsed","Enter isAccNameUsed method in AddAccountActivity class.");
        boolean isAccNameUsed = false;
        //Cursor cursorListOfAccounts = this.accountsDB.getAccountsList();
        Cursor accountCursor = this.accountsDB.getAccountCursorByName(accountName);
        if(accountCursor != null && accountCursor.getCount() > 0){
            Boolean accNameFound = accountName.toLowerCase().trim().equals(accountCursor.getString(1).toLowerCase().trim());
            //In case the _id passed in is not -1, means the EditAccountActivity call method
            //Which means the account name of the account being edited must be ignore from the search, it's ok to have the same account name
            //But not other exiting account name
            if((_id == -1 && accNameFound) || ((_id != accountCursor.getInt(0)) && (accNameFound) )){
                isAccNameUsed = true;
                Log.d("isAccNameUsed","Account name "+ accountName+" found in account list by isAccNameUsed method in AddAccountActivity class.");
            }//End of if else statement to check the _id passed in is equal to -1
        }//End of if that checks the cursor isn't null. Null means the account name isn't being used, hence return false
        return isAccNameUsed;
    }//End of isAccNameUsed method

    //Overloaded method to check if Account name is in use
    protected boolean isAccNameUsed(String accountName) {
        Log.d("isAccNameUsed", "Enter/Exit isAccNameUsed overloaded method in AddAccountActivity class.");
        return this.isAccNameUsed(accountName,-1);
    }//End of isAccNameUsed overloaded method

    protected int displayAlertDialogForImgSource(){
        Log.d("AlertDiagImgSource", "Enter displayAlertDialogForImgSource  method in DisplayAccountActivity abstract class.");
        final int[] selectedValue = {1};
        new AlertDialog.Builder(this)
                .setTitle(R.string.imgSourceTitle)
                //.setMessage("Select an option to load a profile picture:")
                .setSingleChoiceItems(R.array.profileImageSources,0, null)//End of setSingleChoice method
                .setPositiveButton(R.string.dialog_OK,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        //Check the option selected by user
                        int selectedOption = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        switch(selectedOption){
                            case 0:
                                //Set boolean flag to keep decrypt service running
                                isInnerActivityLaunched = true;
                                setExternalImageAsAccLogo(Manifest.permission.WRITE_EXTERNAL_STORAGE,getResources().getString(R.string.cameraAccesRqst),
                                        MainActivity.getThrowImageCameraReqCode(),MainActivity.getCameraAccessRequest());
                                break;
                            case 1:
                                //Set boolean flag to keep decrypt service running
                                isInnerActivityLaunched = true;
                                //Call method to set up an image from the phone gallery
                                setExternalImageAsAccLogo(Manifest.permission.READ_EXTERNAL_STORAGE,getResources().getString(R.string.galleryAccesRqst),
                                                            MainActivity.getThrowImageGalleryReqCode(),MainActivity.getGalleryAccessRequest());
                                break;
                            case 2:
                                //Call method to throw the select logo activity, where logos are stored as app resources
                                throwSelectLogoActivity();
                                break;
                            default:
                                //Any other case, set up the app logo for the current account
                                setAppLogoAsAccIcon();
                                break;
                        }//End of switch statement
                        selectedValue[0] = selectedOption;
                    }//End of Onclick method
                })
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();
        Log.d("AlertDiagImgSource", "Exit displayAlertDialogForImgSource  method in DisplayAccountActivity abstract class.");
        return selectedValue[0];
    }//End of displayAlertDialogForImgSource

    //Method to set the Default app logo as the account icon
    protected void setAppLogoAsAccIcon(){
        Log.d("setAppLogoAsAccIcon", "Enter setAppLogoAsAccIcon overloaded method in DisplayAccountActivity abstract class.");
        this.logo = MainActivity.getMyPsswrdSecureLogo();
        this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure_new);
        Log.d("setAppLogoAsAccIcon", "Exit setAppLogoAsAccIcon overloaded method in DisplayAccountActivity abstract class.");
    }//End of setAppLogoAsAccIcon method

    //Method to set up an image from gallery as the account icon
    protected void setGalleryImageAsAccLogo(){
        Log.d("setGalImgAsAccLogo", "Enter setAppLogoAsAccIcon overloaded method in DisplayAccountActivity abstract class.");
        if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                //If permit has been granted Call method to get access to gallery app via new intent
                                Intent intent = new Intent();
                                MainActivity.loadPictureFromGallery(intent);
                                startActivityForResult(intent, MainActivity.getThrowImageGalleryReqCode());
                            } else {
                                //Otherwise, call method to display justification for this permit and request access to it
                                MainActivity.permissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE, getResources().getString(R.string.galleryAccesRqst),
                                        MainActivity.getGalleryAccessRequest(), this);
                            }//end of if else statement to check the read storage access rights has been granted or not
        Log.d("setGalImgAsAccLogo", "Exit setAppLogoAsAccIcon overloaded method in DisplayAccountActivity abstract class.");
    }//End of setGalleryImageAsAccLogo method

    //Method to set up an image from gallery as the account icon
    protected void setExternalImageAsAccLogo(String permissionType, String permissionReason, int requestCode, int accessRequestCode){
        Log.d("setGalImgAsAccLogo", "Enter setAppLogoAsAccIcon overloaded method in DisplayAccountActivity abstract class.");
        if (ContextCompat.checkSelfPermission(this,permissionType) == PackageManager.PERMISSION_GRANTED) {
            //If permit has been granted Call method to get access to gallery app via new intent
            Intent intent = new Intent();
            if(requestCode == MainActivity.getThrowImageGalleryReqCode()){
                MainActivity.loadPictureFromGallery(intent);
            }else if(requestCode == MainActivity.getThrowImageCameraReqCode()){
                MainActivity.loadPictureFromCamera(intent,this);
            }//End of if else statement to check the request code
            startActivityForResult(intent, requestCode);
        } else {
            //Otherwise, call method to display justification for this permit and request access to it
            MainActivity.permissionRequest(permissionType, permissionReason,
                    accessRequestCode, this);
        }//End of if else statement to check the read storage access rights has been granted or not
        Log.d("setGalImgAsAccLogo", "Exit setAppLogoAsAccIcon overloaded method in DisplayAccountActivity abstract class.");
    }//End of setGalleryImageAsAccLogo method

    //Method to add new icon into the DB if icon isn't the app logo or comes from the app resources
    protected int isAddIconRequired(Account account){
        Log.d("isAddIconRequired", "Enter isAddIconRequired method in DisplayAccountActivity abstract class.");
        //Declare and initialize variables to be used in method and the return variable
        int logoID = -1;
        //Check the account icon isn't null
        if(account.getIcon()!=null){
            //Check the icon doesn't come from app resources or isn't the default app logo
            if(!account.getIcon().getLocation().equals(MainActivity.getRESOURCES())  && !account.getIcon().equals(MainActivity.getMyPsswrdSecureLogo())){
                //Check if external file is already in use within the app, and files location is already saved on DB
                Icon selectedLogo = this.logo; //This was defined on the onActivityResult method when logo is received from Galley or Camera Intent
                if(selectedLogo.get_id() == -1){
                    //If icon isn't coming from either option, and id is -1 save the logo uri in the DB
                    logoID = this.accountsDB.addItem(account.getIcon());
                }else{
                    logoID = selectedLogo.get_id();
                }//End of if else statement to check logo id
                //Check the DB insertion was successful
                if(logoID != -1){
                    //Update the account object Icon ID
                    account.getIcon().set_id(logoID);
                }else{
                    //Otherwise, set the account attribute's icon id for the account object
                    account.getIcon().set_id(this.account.getIcon().get_id());
                }//End of if statement to check the logoID is valid
            }//End of if statement to check the icon doesn't come from resources or isn't the app logo
        }//End of if statement to check the icon isn't null
        Log.d("isAddIconRequired", "Exit isAddIconRequired method in DisplayAccountActivity abstract class.");
        return logoID;
    }//End of isAddIconRequired

    protected boolean rollBackQuestionListInsertion(QuestionList questionList) {
        Log.d("rollbackQuestionList","Enter  questionList method in AddAccountActivity class.");
        //Check the number of times the list is used, if more than once do nothing, if 1 or 0 then delete the list
        boolean rollbackResult = true;
        int timesUsedQuestionList = accountsDB.getTimesUsedQuestionList(questionList.get_id());
        if(timesUsedQuestionList < 1){
            rollbackResult = accountsDB.deleteItem(questionList);
        }
        Log.d("rollbackQuestionList","Exit  questionList method in AddAccountActivity class.");
        return rollbackResult;
    }//End of rollBackQuestionListInsertion
}//End of AddAccountActivity