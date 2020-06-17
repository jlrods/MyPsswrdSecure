package io.github.jlrods.mypsswrdsecure;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    int CATEGORY_SPINNER = 0;
    int USERNAME_SPINNER = 1;
    int PSSWRD_SPINNER = 2;

    AccountsDB accounts;
    //Layout attributes
    protected Bundle extras;
    protected ImageView imgAccLogo;
    protected EditText etAccountName;
    protected NoDefaultSpinner spCategory;
    protected NoDefaultSpinner spAccUserName;
    protected EditText etAccNewUserName;
    protected Button btnAccNewUserName;
    protected NoDefaultSpinner spAccPsswrd;
    protected EditText etAccNewPsswrd;
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

    //Declare five spinner adapters, one for the category spinner, one for userName, one for password and two for question list spinners
    //And respective cursors to hold data from DB
    protected int spCategoryPosition;
    SpinnerAdapter adapterCategory;
    Cursor cursorCategory;
    SpinnerAdapter adapterUserName;
    Cursor cursorUserName;
    SpinnerAdapter adapterPsswrd;
    Cursor cursorPsswrd;
    SpinnerAdapter adapterQuestionList;
    Cursor cursorQuestionList;
    SpinnerAdapter adapterListOfQuestionsAvailable;
    Cursor cursorListOfQuestionsAvailable;

    //Other attributes
    boolean isFavorite = false;
    IconAdapter iconAdapter = null;
    int throwSelectLogoActReqCode = 5555;
    Icon logo = null;
    int selectedPosition = -1;
    String dateFormat = "dd/MMM/yyyy";


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
        this.accounts = HomeFragment.getAccounts();

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
        //this.etAccNewUserName = (EditText) findViewById(R.id.etAccNewUserName);
        this.btnAccNewUserName = (Button) findViewById(R.id.btnAccNewUserName);

        //Initialize Psswrd spinner related variables
        this.spAccPsswrd = (NoDefaultSpinner) findViewById(R.id.spAccPsswrd);
        //this.etAccNewPsswrd = (EditText) findViewById(R.id.etAccNewPssrd);
        this.btnAccNewPsswrd = (Button) findViewById(R.id.btnAccNewPsswrd);

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
                btnAccAddQuestion.setEnabled(true);
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
        this.cursorCategory = this.accounts.getCategoryListCursor();
        this.setUpSpinnerData(cursorCategory,adapterCategory,spCategory,CATEGORY_SPINNER);
        //Setup the UserName spinner and populate with data
        this.cursorUserName = this.accounts.getUserNameList();
        this.setUpSpinnerData(cursorUserName,adapterUserName,spAccUserName,USERNAME_SPINNER);
        //Setup the Psswrd spinner and populate with data
        this.cursorPsswrd = this.accounts.getPsswrdList();
        this.setUpSpinnerData(cursorPsswrd,adapterPsswrd, spAccPsswrd,PSSWRD_SPINNER);
        //Setup the Security Question List spinner
        //Use a Dummy cursor to be able to setup prompt. This dummy cursor will held one question item but wont be displayed
        this.cursorQuestionList = accounts.getQuestionCursorByID(1);
        this.spAccSecQuestionList.setPrompt(getBaseContext().getResources().getString(R.string.account_quest_list_spinner_prompt));
        this.setUpQuestionListSpinnerData(cursorQuestionList,adapterQuestionList,spAccSecQuestionList);
        //Disable the Security question spinner so user wont be able to see dummy item in spinner
        this.spAccSecQuestionList.setEnabled(false);

        //Setup the Questions Available spinner and populate with data
        this.cursorListOfQuestionsAvailable = accounts.getListQuestionsAvailable();
        this.spQuestionsAvailable.setPrompt(getBaseContext().getResources().getString(R.string.account_quest_avilab_spinner_prompt));
        this.setUpQuestionListSpinnerData(cursorListOfQuestionsAvailable,adapterListOfQuestionsAvailable,spQuestionsAvailable);


        //Initialize the iconAdapter to be able to communicate with SelectLogoAct and find the selected logo in this Activity
        this.iconAdapter = new IconAdapter(this);
        Log.d("OnCreateDispAcc","Exit onCreate method in the DisplayAccountActivity abstract class.");
    }//End of onCreate method

    //Method to set up spinner data by passing in a cursor and the adapter as arguments
    protected void setUpSpinnerData(Cursor cursor,SpinnerAdapter adapter,Spinner sp,int type){
        Log.d("setUpSpinnerData","Enter the setUpSpinnerData method in the DisplayAccountActivity class.");
        //Get resources for displying spinner
        String spHint = "";
        String etHint = "";
        Resources res = getResources();
        switch(type){
            case 0:
                Log.d("setUpSpinnerData","CATEGORY_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectCatHint);
                break;
            case 1:
                Log.d("setUpSpinnerData","USERNAME_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectUserHint);
                etHint = res.getString(R.string.selectUserHint);
                break;
            case 2:
                Log.d("setUpSpinnerData","PSSWRD_SPINNER passed in as spinner type in setUpSpinnerData method call  in the DisplayAccountActivity class.");
                spHint = res.getString(R.string.selectPsswrdHint);
                etHint = res.getString(R.string.selectPsswrdHint);
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
            if(type == USERNAME_SPINNER){
                //this.etAccNewUserName.setHint(etHint);
                this.etAccNewUserName.setEnabled(true);
            }else if(type == PSSWRD_SPINNER){
                this.etAccNewPsswrd.setHint(etHint);
                this.etAccNewPsswrd.setEnabled(true);
            }//End of if else statement to check the spinner type
        }// End of if else statement to check cursor isn't null or empty
        //Create new spinner adapter object
        adapter = new SpinnerAdapter(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
        //Set the adapter for the Category spinner
        sp.setAdapter(adapter);
        Log.d("setUpSpinnerData","Exit the setUpSpinnerData method in the DisplayAccountActivity class.");
    }//End of setUpSpinnerData method

    //Method to setup question list spinner data
    protected void setUpQuestionListSpinnerData(Cursor cursor,SpinnerAdapter adapter,Spinner sp){
        Log.d("setUpQuestListSpData","Enter the setUpQuestionListSpinnerData method in the DisplayAccountActivity class.");
        adapter = new SpinnerAdapterQuestion(this,cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) ;
        sp.setAdapter(adapter);
        Log.d("setUpQuestListSpData","Exit the setUpQuestionListSpinnerData method in the DisplayAccountActivity class.");
    }//End of setUpQuestionListSpinnerData method

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
                c = accounts.getQuestionCursorByID(selectedQuestionID);
                break;
                // 1 Question in the security question list
            case 1:
                //Check if spinner is disabled, if it is then the item in list is a dummy one
                Log.d("addQuestionToSecList","1 question in the Security Question list while adding a question on addQuestionToSecList method in the DisplayAccountActivity class.");
                //Find id of question already in the list
                int _id1 = (int) this.spAccSecQuestionList.getAdapter().getItemId(0);
                //Check the question in the security question list is not the same selected form question available list spinner
                if(_id1 != selectedQuestionID){
                    c = accounts.getQuestionCursorByID(_id1,selectedQuestionID);
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
                    c = accounts.getQuestionCursorByID(_id1,_id2,selectedQuestionID);
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
            this.setUpQuestionListSpinnerData(c,this.adapterQuestionList,this.spAccSecQuestionList);
            this.spAccSecQuestionList.setEnabled(true);
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
                c = accounts.getQuestionCursorByID(_id1);
                break;
            case 3:
                Log.d("removeQuestFromSecList","3 questions in the Security Question list while removing a question on removeQuestFromSecList method in the DisplayAccountActivity class.");
                //Call method to get not selected questions' DB id
                _id1 = (int) this.getNewQuestionList(selectedQuestionID).get(0);
                int _id2 = (int) this.getNewQuestionList(selectedQuestionID).get(1);
                //Populate cursor with two questions left in the spinner
                c = accounts.getQuestionCursorByID(_id1,_id2);
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
            this.setUpQuestionListSpinnerData(c,this.adapterQuestionList,this.spAccSecQuestionList);
        }else{
            //Use a Dummy cursor again to display spinner prompt
            c = accounts.getQuestionCursorByID(selectedQuestionID);
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
            if(data.getExtras().getString("selectedImgLocation").equals("Resources")){
                //this.imgAccLogo.setImageResource(data.getExtras().getInt("selectedImgID"));
                this.selectedPosition = data.getExtras().getInt("selectedImgPosition");
                        //iconAdapter.getIconList().get().get_id();
                this.logo = iconAdapter.getIconList().get(this.selectedPosition);
                this.imgAccLogo.setImageResource(this.logo.get_id());
            }else if(data.getExtras().getString("selectedImgLocation").equals(String.valueOf(R.mipmap.ic_my_psswrd_secure))){
                this.imgAccLogo.setImageResource(R.mipmap.ic_my_psswrd_secure);
            }//End of if else statement to check if logo comes from app resources
        }else if(requestCode==this.throwSelectLogoActReqCode && resultCode==RESULT_CANCELED){
            //In the event of receiving a cancel result, no change to be done on the current account, no logo change to be applied
            Log.d("onActivityResult","Received CANCEL result from SelectLogoActivity in the DisplayAccountActivity class.");
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
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
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
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
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
}//End of DisplayAccountActivity
