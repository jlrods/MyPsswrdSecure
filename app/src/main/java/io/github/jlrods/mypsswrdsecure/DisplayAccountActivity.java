package io.github.jlrods.mypsswrdsecure;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private static Cryptographer cryptographer;

    //Objects required to build an account
    Category category = null;
    UserName userName = null;
    Psswrd psswrd = null;
    //int objectType = -1;

    //Layout attributes
    protected Bundle extras;
    protected ImageView imgAccLogo;
    protected EditText etAccountName;
    protected NoDefaultSpinner spCategory;
    protected NoDefaultSpinner spAccUserName;
    //protected EditText etAccNewUserName;
    protected Button btnAccNewUserName;
    protected NoDefaultSpinner spAccPsswrd;
    //protected EditText etAccNewPsswrd;
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



    //Method definition
    //Other methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateDispAcc","Enter onCreate method in the DisplayAccountActivity abstract class.");
        //Set layout for this activity
        setContentView(R.layout.activity_add_account);
        //Extract extra data from Bundle object
        //extras = getIntent().getExtras();
        //Get DB handler cass from the home fragment
        this.accountsDB = HomeFragment.getAccountsDB();

        cryptographer = MainActivity.getCryptographer();

        //Initialize layout coordinator required to use Snackbar
        coordinatorLayoutAccAct = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutAccAct);

        //Initialize view objects from layout to have access to them and set different texts and other properties
        //Logo related variables
        this.imgAccLogo = (ImageView) findViewById(R.id.imgAccLogo);
        this.imgAccLogo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                throwSelectLogoActivity();;
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
                addNewUserName();
            }//End of onClick method
        });//End of setOnClickListener method

        //Initialize Psswrd spinner related variables
        this.spAccPsswrd = (NoDefaultSpinner) findViewById(R.id.spAccPsswrd);
        this.btnAccNewPsswrd = (Button) findViewById(R.id.btnAccNewPsswrd);
        this.btnAccNewPsswrd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addNewPsswrd();
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
                addNewQuestion();
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
                addQuestionToSecList();
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
        this.setUpSpinnerData(cursorCategory,spCategory,CATEGORY_SPINNER);
        //Setup the UserName spinner and populate with data
        this.cursorUserName = this.accountsDB.getUserNameList();
        this.setUpSpinnerData(cursorUserName,spAccUserName,USERNAME_SPINNER);
        //Setup the Psswrd spinner and populate with data
        this.cursorPsswrd = this.accountsDB.getPsswrdList();
        this.setUpSpinnerData(cursorPsswrd, spAccPsswrd,PSSWRD_SPINNER);
        //Setup the Security Question List spinner
        //Call method to configure security question list spinner. Use a Dummy cursor to be able to setup prompt.
        //This dummy cursor will held one question item but wont be displayed
        this.initSecQuestionListSpinner();
        //Call method to setup the Questions Available spinner and populate with data
        this.initQuesitonAvailableListSpinner();
        //Initialize the iconAdapter to be able to communicate with SelectLogoAct and find the selected logo in this Activity
        this.iconAdapter = new IconAdapter(this);
        Log.d("OnCreateDispAcc","Exit onCreate method in the DisplayAccountActivity abstract class.");
    }//End of onCreate method

    //Method to setup dummy cursor for security question spinner when initializing it
    protected void initSecQuestionListSpinner(){
        this.cursorQuestionList = accountsDB.getQuestionCursorByID(1);
        this.spAccSecQuestionList.setPrompt(getBaseContext().getResources().getString(R.string.account_quest_list_spinner_prompt));
        this.setUpQuestionListSpinnerData(cursorQuestionList,spAccSecQuestionList);
        //Disable the Security question spinner so user wont be able to see dummy item in spinner
        this.spAccSecQuestionList.setEnabled(false);
    }

    protected void initQuesitonAvailableListSpinner(){
        this.cursorListOfQuestionsAvailable = accountsDB.getListQuestionsAvailable();
        this.spQuestionsAvailable.setPrompt(getBaseContext().getResources().getString(R.string.account_quest_avilab_spinner_prompt));
        this.setUpQuestionListSpinnerData(cursorListOfQuestionsAvailable,spQuestionsAvailable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the DisplayAccountActivity abstract class.");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        Log.d("onCreateOptionsMenu","Enter onCreateOptionsMenu method in the DisplayAccountActivity abstract class.");
        return true;
    }//End of onCreateOptionsMenu method



    //Method to set up spinner data by passing in a cursor and the adapter as arguments
    protected void setUpSpinnerData(Cursor cursor,Spinner sp,int type){
        SpinnerAdapter adapter = null;
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
                adapter = new SpinnerAdapter(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
                break;
            case USERNAME_SPINNER:
                Log.d("setUpSpinnerData","USERNAME_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectUserHint);
                etHint = res.getString(R.string.selectUserHint);
                //Create new spinner adapter object
                adapter = new SpinnerAdapterEncrypted(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
                break;
            case PSSWRD_SPINNER:
                Log.d("setUpSpinnerData","PSSWRD_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectPsswrdHint);
                etHint = res.getString(R.string.selectPsswrdHint);
                //Create new spinner adapter object
                adapter = new SpinnerAdapterEncrypted(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
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
            //Check if spinner type is username or psswrd spinner to setup texts and enable respective buttons
//            if(type == USERNAME_SPINNER){
//                this.etAccNewUserName.setEnabled(true);
//            }else if(type == PSSWRD_SPINNER){
//                this.etAccNewPsswrd.setHint(etHint);
//                this.etAccNewPsswrd.setEnabled(true);
//            }//End of if else statement to check the spinner type
        }// End of if else statement to check cursor isn't null or empty

        //adapter = new SpinnerAdapter(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
        //Set the adapter for the Category spinner
        sp.setAdapter(adapter);
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

    //Method to add a new userName to the user name dropdown menu
    protected void addNewUserName(){
        Log.d("addNewUserName","Enter the addNewUserName method in the DisplayAccountActivity class.");
        //Declare an instantiate a new editText view to be passed in as parameter for the AlertDialog builder method
        final EditText inputField = new EditText(this);
        //Call MainMethod AlertDialog display method
         androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = MainActivity.displayAlertDialog(this, inputField,
                 getResources().getString(R.string.alertBoxNewUser),getResources().getString(R.string.alertBoxNewUserMssg),
                 getResources().getString(R.string.alertBoxNewUserHint));
         alertDialogBuilder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int whichButton){
                        //Get user name text from AlertDialog text input field
                        String userNameValue = inputField.getText().toString().trim();
                        byte[] userNameValueEncrypted;
                        //Check the user name is not in the user name list already
                        Cursor userNameCursor = accountsDB.getUserNameByName(userNameValue);
                        if(userNameCursor == null || userNameCursor.getCount() ==0){
                            //Encrypt the user name
                            userNameValueEncrypted = cryptographer.encryptText(userNameValue);
                            //If user name not in the list create new user object and store it in global variable used to build the account object
                            userName = new UserName(userNameValueEncrypted,cryptographer.getIv().getIV());
                            //Call DB method to insert  the user name object into the DB
                            int userNameID = accountsDB.addItem(userName);
                            if(userNameID > 0 ){
                                //Update the userName object ID
                                userName.set_id(userNameID);
                                cursorUserName = accountsDB.getUserNameList();
                                //Populate the user name spinner with new data set
                                setUpSpinnerData(cursorUserName,spAccUserName,USERNAME_SPINNER);
                                //Move spinner to new user name just inserted
                                spAccUserName.setSelection(spAccUserName.getAdapter().getCount()-1);
                                //Prompt the user the user name has been added and give option to undo
                                Snackbar snackbar = Snackbar.make(coordinatorLayoutAccAct, R.string.snackBarUserAdded, Snackbar.LENGTH_LONG);
                                snackbar.setAction(R.string.snackBarUndo,new SnackBarClickHandler(userName,userNameCursor,spAccUserName));
                                snackbar.show();
                                Log.d("addNewUserName","The user name "+ userName.getValue()+" has been added into the DB through addNewUserName method in the DisplayAccountActivity class.");
                            }
                            else {
                                //Prompt the user the user name input failed to be inserted in the DB
                               Toast toast = Toast.makeText(getBaseContext(), R.string.snackBarUserNotAdded,Toast.LENGTH_LONG);
                               toast.setGravity(Gravity.CENTER,0,0);
                               toast.show();
                                Log.d("addNewUserName","The user name "+ userName.getValue()+" has NOT been added into the DB through addNewUserName method in the DisplayAccountActivity class, due to DB problem.");
                            }//End of if else statement to check user name in DB
                        }else {
                            //Prompt the user the user name input already exists in the list
                            Toast toast = Toast.makeText(getBaseContext(), R.string.snackBarUserExists,Toast.LENGTH_LONG)     ;
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                            Log.d("addNewUserName","The user name "+ userNameValue +" already exists in the DB.");
                        }//End of if else statement to check user name in DB
                    }//End of Onclick method
                })//End of setPositiveButton method
        .show();//End of AlertDialog builder
        Log.d("addNewUserName","Exit the addNewUserName method in the DisplayAccountActivity class.");
    }//End of addNewUserName

    //Method to add a new password to the password dropdown menu
    protected void addNewPsswrd(){
        Log.d("addNewPsswrd","Enter the addNewPsswrd method in the DisplayAccountActivity class.");
        //Declare an instantiate a new editText view to be passed in as parameter for the AlertDialog builder method
        final EditText inputField = new EditText(this);
        //Call MainMethod AlertDialog display method
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = MainActivity.displayAlertDialog(this, inputField,
                getResources().getString(R.string.alertBoxNewPsswrd),getResources().getString(R.string.alertBoxNewPsswrdMssg),
                getResources().getString(R.string.alertBoxNewPsswrdHint));
        alertDialogBuilder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int whichButton){
                //Get user name text from AlertDialog text input field
                String psswrdValue = inputField.getText().toString().trim();
                byte[] psswrdValueEncrypted;
                //Check the user name is not in the user name list already
                Cursor psswrdCursor = accountsDB.getPsswrdByName(psswrdValue);
                if(psswrdCursor == null || psswrdCursor.getCount() == 0){
                    //Encrypt the psswrd
                    psswrdValueEncrypted = cryptographer.encryptText(psswrdValue);
                    //If user name not in the list create new user object and store it in global variable used to build the account object
                    psswrd = new Psswrd(psswrdValueEncrypted,cryptographer.getIv().getIV());
                    //Call DB method to insert  the user name object into the DB
                    int psswrdID = accountsDB.addItem(psswrd);
                    if(psswrdID > 0 ){
                        //Update the userName object ID
                        psswrd.set_id(psswrdID);
                        psswrdCursor = accountsDB.getPsswrdList();
                        //Populate the user name spinner with new data set
                        setUpSpinnerData(psswrdCursor,spAccPsswrd,PSSWRD_SPINNER);
                        //Move spinner to new user name just inserted
                        spAccPsswrd.setSelection(spAccPsswrd.getAdapter().getCount()-1);
                        //Prompt the user the user name has been added and give option to undo
                        Snackbar snackbar = Snackbar.make(coordinatorLayoutAccAct, R.string.snackBarUserAdded, Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.snackBarUndo,new SnackBarClickHandler(psswrd,psswrdCursor,spAccPsswrd));
                        snackbar.show();
                        Log.d("addNewPsswrd","The password "+ psswrd.getValue()+" has been added into the DB through addNewPsswrd method in the DisplayAccountActivity class.");
                    }
                    else {
                        //Prompt the user the user name input failed to be inserted in the DB
                        Toast toast = Toast.makeText(getBaseContext(), R.string.snackBarPsswrdNotAdded,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        Log.d("addNewPsswrd","The password "+ psswrd.getValue()+" has NOT been added into the DB through addNewPsswrd method in the DisplayAccountActivity class, due to DB problem.");
                    }//End of if else statement to check user name in DB
                }else {
                    //Prompt the user the user name input already exists in the list
                    Toast toast = Toast.makeText(getBaseContext(), R.string.snackBarPsswrdExists,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    Log.d("addNewPsswrd","The password "+ psswrdValue +" already exists in the DB.");
                }//End of if else statement to check user name in DB
            }//End of Onclick method
        })//End of setPositiveButton method
                .show();//End of AlertDialog builder
        Log.d("addNewPsswrd","Exit the addNewPsswrd method in the DisplayAccountActivity class.");
    }//End of addNewPsswrd method

    //Method to add a new question to the security question  dropdown menu
    protected void addNewQuestion(){
        Log.d("addNewQuestion","Enter the addNewQuestion method in the DisplayAccountActivity class.");
        //Declare and instantiate a new intent object
        Intent i= new Intent(this, AddQuestionActivity.class);
        //Add extras to the intent object, specifically the current category where the add button was pressed from
        // the current logo data which is sent back if select logo is cancel or updated if new logo has been selected
        //i.putExtra("selectedImgPosition",selectedPosition);
        //i.putExtra("selectedImgLocation",logo.getLocation());
        //Start the addTaskActivity and wait for result
        startActivityForResult(i,MainActivity.getThrowAddQuestionActReqCode());
        Log.d("addNewPsswrd","Exit the addNewQuestion method in the DisplayAccountActivity class.");
    }//End of addNewQuestion method

    //Method to add a question from question available list to security question list spinner
    protected void addQuestionToSecList(){
        Log.d("addQuestionToSecList","Enter the addQuestionToSecList method in the DisplayAccountActivity class.");
        Cursor c = null;
        boolean repeatedQuestion = false;
        //Find the question selected
        int selectedQuestionPosition = this.spQuestionsAvailable.getSelectedItemPosition() ;
        //Get its DB _id number
        int selectedQuestionID = (int) this.spQuestionsAvailable.getAdapter().getItemId(selectedQuestionPosition);
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
                    //FIXME: Ideally a snackbar or toast to be configured, but it crashes here???
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
                //Return Toast to warn user the max number of sec questions is 3
                //FIXME: Ideally a snackbar or toast to be configured, but it crashes here???
                //Toast.makeText(getBaseContext(),"Max number reached",Toast.LENGTH_LONG).show();
                break;
        }//End of switch statement
        //Populate the questionList spinner with Question data
        if(!repeatedQuestion){
            //Setup spinner prompt by getting proper text after calling method to get text
            this.spAccSecQuestionList.setPrompt(this.getSecQuestListPrompt(c.getCount()));
            //Setup the spinner data
            this.setUpQuestionListSpinnerData(c,this.spAccSecQuestionList);
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
            c = accountsDB.getQuestionCursorByID(selectedQuestionID);
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
        //SpinnerAdapterQuestion adapter = (SpinnerAdapterQuestion) this.spAccSecQuestionList.getAdapter();
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
            //this.layoutHour.setVisibility(View.VISIBLE);
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

    //Method to throw the SelectLogoActivity
    protected void throwSelectLogoActivity(){
        Log.d("throwSelectLogoActivity","Enter the throwSelectLogoActivity method in the DisplayAccountActivity class.");
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
        Log.d("onActivityResult","Enter the onActivityResult method in the DisplayAccountActivity class.");
        if (requestCode==this.throwSelectLogoActReqCode && resultCode==RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from SelectLogoActivity in the DisplayAccountActivity class.");
            //Check icon location
            if(data.getExtras().getString("selectedImgLocation").equals(MainActivity.getRESOURCES())){
                //this.imgAccLogo.setImageResource(data.getExtras().getInt("selectedImgID"));
                this.selectedPosition = data.getExtras().getInt("selectedImgPosition");
                        //iconAdapter.getIconList().get().get_id();
                this.logo = iconAdapter.getIconList().get(this.selectedPosition);
                this.imgAccLogo.setImageResource(this.logo.getResourceID());
            }else if(data.getExtras().getString("selectedImgLocation").equals(String.valueOf(R.mipmap.ic_my_psswrd_secure))){
                this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure);
            }//End of if else statement to check if logo comes from app resources
        }else if(requestCode==this.throwSelectLogoActReqCode && resultCode==RESULT_CANCELED){
            //In the event of receiving a cancel result, no change to be done on the current account, no logo change to be applied
            Log.d("onActivityResult","Received CANCEL result from SelectLogoActivity in the DisplayAccountActivity class.");
        }else if(requestCode== MainActivity.getThrowAddQuestionActReqCode() && resultCode==RESULT_OK){
            //Setup the Questions Available spinner and populate with data
            this.cursorListOfQuestionsAvailable = accountsDB.getListQuestionsAvailable();
            this.spQuestionsAvailable.setPrompt(getBaseContext().getResources().getString(R.string.account_quest_avilab_spinner_prompt));
            this.setUpQuestionListSpinnerData(cursorListOfQuestionsAvailable,spQuestionsAvailable);
            this.spQuestionsAvailable.setSelection(spQuestionsAvailable.getAdapter().getCount()-1);
            this.addQuestionToSecList();
        }else if(requestCode== MainActivity.getThrowAddQuestionActReqCode() && resultCode==RESULT_CANCELED){

        }//End of if else statement to check the data comes SelectLogoActivity
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

    //Getter and setter methods

    //Private and Protected inner classes

    //Class to handle snackbar undo click events
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
            accountsDB.deleteItem(item);
            //Check the type of object passed in to setup variables required to populate the respective spinner correctly
            if(item instanceof Psswrd){
                cursor =  accountsDB.getPsswrdList();
                spinnerType = PSSWRD_SPINNER;
                Log.d("SnackBarOnClick","PSSWRD object passed into the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
            }else if(item instanceof UserName){
                cursor = accountsDB.getUserNameList();
                spinnerType = USERNAME_SPINNER;
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
                setUpQuestionListSpinnerData(cursor,spinner);
            }
            Log.d("SnackBarOnClick","Exit the onClick method method in the SnackBarClickHandler subclass from DisplayTaskActivity abstract class.");
        }//End of onClick method
    }//End of SnackBarClickHandler subclass

    protected Account getItemFromUIData(){
        Log.d("getItemFromUIData","Enter the getItemFromUIData method method in the DisplayTaskActivity abstract class.");
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
        //@FIXME check if including apostrophe esc char is required when using insert android method
//        if(accountName.contains(""/)){
//            accountName = accounts.includeApostropheEscapeChar(accountName);
//        }
        category = accountsDB.getCategoryByID((int) this.spCategory.getSelectedItemId());
        userName = accountsDB.getUserNameByID((int) this.spAccUserName.getSelectedItemId());
        psswrd = accountsDB.getPsswrdByID((int) this.spAccPsswrd.getSelectedItemId());
        securityQuestionList = this.extractQuestionsFromSpinner(spAccSecQuestionList);
        //Check the security question list isn't empty
        if(securityQuestionList != null){
            //Insert the question list in the DB so the DB _id is retrieved and can be passed in to insert the new account later on
            securityQuestionList.set_id(accountsDB.addItem(securityQuestionList));
        }
        if(this.cbHasToBeChanged.isChecked() && psswrdChangeDate > 0){
            psswrdChangeDate = this.getPsswrdRenewDate();
            //Once all required objects have been created, the account object can be created
            account = new Account(accountName,userName,psswrd,category,securityQuestionList,this.logo,this.isFavorite,psswrdChangeDate);
        }else{
            account = new Account(accountName,userName,psswrd,category,securityQuestionList,this.logo,this.isFavorite);
        }
        Log.d("getItemFromUIData","Exit the getItemFromUIData method  in the DisplayTaskActivity abstract class.");
        return account;
    }//End of getItemFromUIData method

    //Method to get all the appointment date details coming from date and hour text views
    protected long getPsswrdRenewDate(){
        Log.d("Ent_getAppointmentDate","Enter the getAppointmentDate method in the DisplayActivity abstract class.");
        //Declare and initialize a new calendar with current time
        Calendar calendar = Calendar.getInstance();
        //Declare a new date object
        Date date;
        //Check if date text view is empty
        if(this.tvAccDateRenewValue.getText().equals("")){
            //if that is the case, add an argument to hold the current time in millisecs
            //date = Calendar.getInstance().getTime();
            date = null;
        }else{
            //Otherwise, declare and instantiate a new DateFormat object to define the date format
            SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
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

        if(cursorQuestionList != null && cursorQuestionList.getCount() > 0){
            questionList = new QuestionList();
            while(cursorQuestionList.moveToNext()){
               Question question = Question.extractQuestion(cursorQuestionList);
               questionList.addQuestion(question);
           }//End of while loop to go through the question list cursor
        }//End of if statement that checks cursor isn't empty or null
        Log.d("XtrctQuestFromSpinner","Enter the extractQuestionsFromSpinner method in the DisplayActivity abstract class.");
        return questionList;
    }//End of extractQuestionsFromSpinner method

}//End of AddAccountActivity