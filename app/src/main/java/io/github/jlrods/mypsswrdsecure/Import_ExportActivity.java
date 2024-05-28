package io.github.jlrods.mypsswrdsecure;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

public class Import_ExportActivity extends AppCompatActivity {
    ThemeUpdater themeUpdater = null;
    protected Button btnExport = null;
    protected Button btnImport = null;
    protected AccountsDB accountsDB = null;
    protected Cryptographer cryptographer = null;
    protected InputStream inputStream = null;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreate", "Enter onCreate method in the Import_ExportActivity  class.");
        this.themeUpdater = new ThemeUpdater(this);
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Set correct language
        MainActivity.setAppLanguage(this);
        //Set layout for this activity
        setContentView(R.layout.activity_export_import);
        //Set activity title
        getSupportActionBar().setTitle(R.string.import_export);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tvImportExport = findViewById(R.id.tvImportExport);
        tvImportExport.setTextColor(this.themeUpdater.fetchThemeColor("colorAccent"));

        //String psswrdList = psswrdCursor.toString()
            // Get the Export button and setup event listener
        this.btnExport = (Button) findViewById(R.id.btnExport);
        this.btnExport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                exportData();
            }//End of onClick method
        });//End of setOnClickListener method

        //Get the Import button and setup event listener
            this.btnImport = (Button) findViewById(R.id.btnImport);
            this.btnImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    importData();
                }//End of onClick method
            });//End of setOnClickListener method
            // Initialize accounts DB object and cryptographer object
            accountsDB = MainActivity.getAccountsDB();
            cryptographer = LoginActivity.getCryptographer();
        Log.d("OnCreate", "Exit onCreate method in the Import_ExportActivity  class.");
    }//End of onCreate method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Boolean writeToFile(String data, Context context, String fileName) {
        Boolean result = false;
        try {
            //String extension = ".txt";
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            result = true;
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            return result;
        }
    }

    //Method to throw a new Activity
    private void throwActivityNoExtras(Class className,int requestCode) {
        Log.d("throwActivityNoExtras", "Enter throwActivityNoExtras method in the DisplayAccountActivity class.");
        //Declare and instantiate a new intent object
        Intent i = new Intent(this,className);
        //Start the AddItemActivity class
//        i.putExtra("isActivityThrownFromDisplayAct",true);
//        //Set boolean flag to keep decrypt service running
//        this.isInnerActivityLaunched = true;
        if(requestCode > 0){
            startActivityForResult(i, requestCode);
            Log.d("throwActivityNoExtras", "startActivityForResult called by throwActivityNoExtras method in the DisplayAccountActivity class with request code: "+requestCode);
        }else{
            startActivity(i);
            Log.d("throwActivityNoExtras", "startActivity called by throwActivityNoExtras method in the DisplayAccountActivity class.");
        }
        Log.d("throwActivityNoExtras", "Exit throwActivityNoExtras method in the MainActivity class.");
    }//End of throwActivityNoExtras method

    private void exportData(){
        String outputText = "";
        Context context = getBaseContext();
        //Call method to get category list as text
        outputText = getCategories();
        if(writeToFile(outputText,context,getString(R.string.category_txt))){
            //Call method to get password list as text
            outputText = getPsswrds();
            //Call method to write Passwrods list onto a file
            if(writeToFile(outputText,context,getString(R.string.psswrd_txt))){
                outputText = getUserNames();
                if(writeToFile(outputText,context,getString(R.string.username_txt))){
                    outputText =  getQuestions();
                    if(writeToFile(outputText,context,getString(R.string.questions_txt))){
                        outputText = getAccounts();
                        if(writeToFile(outputText,context,"Accounts.txt")){
                            outputText = getQuestionLists();
                            if(writeToFile(outputText,context,"QuestionLists.txt")){
                                MainActivity.displayToast(this, "Data has been exported successfully", Toast.LENGTH_LONG, Gravity.CENTER);
                            }
                        }
                    }
                }
            }
        }
    }

    private String getCategories(){
        Cursor categoryCursor = accountsDB.getCategoryListCursor();
        String outputText = "";
        Category tempCategory = new Category();
        categoryCursor.moveToFirst();
        do {
            tempCategory = tempCategory.extractCategory(categoryCursor);
            outputText = outputText.concat(tempCategory.toString()).concat("\n");
        } while (categoryCursor.moveToNext());
        return  outputText;
    }
    private String getPsswrds(){
        Cursor psswrdCursor = accountsDB.getPsswrdList();
        String outputText = "";
        Psswrd tempPsswrd = new Psswrd();
        psswrdCursor.moveToFirst();
        do {
            tempPsswrd = tempPsswrd.extractPsswrd(psswrdCursor);
            outputText = outputText.concat(cryptographer.decryptText(tempPsswrd.getValue(),new IvParameterSpec(tempPsswrd.getIv()))).concat("\n");
        } while (psswrdCursor.moveToNext());
        return  outputText;
    }

    private String getUserNames(){
        Cursor userNamesCursor = accountsDB.getUserNameList();
        String outputText = "";
        UserName tempUserName = new UserName();
        userNamesCursor.moveToFirst();
        do {
            tempUserName = tempUserName.extractUserName(userNamesCursor);
            outputText = outputText.concat(cryptographer.decryptText(tempUserName.getValue(),new IvParameterSpec(tempUserName.getIv()))).concat("\n");
        } while (userNamesCursor.moveToNext());
        return  outputText;
    }

    private String getAccounts(){
        Cursor accountsCursor = accountsDB.getAccountsList();
        String outputText = "";
        Account tempAccount = new Account();
        accountsCursor.moveToFirst();
        do {
            tempAccount = tempAccount.extractAccount(accountsCursor);
            outputText = outputText.concat(tempAccount.toString()).concat("\n");
        } while (accountsCursor.moveToNext());
        return  outputText;
    }

    private String getQuestions(){
        Cursor questionsCursor = accountsDB.getListQuestionsAvailable();
        String outputText = "";
        Question tempQuestion = new Question();
        if (questionsCursor.getCount() > 0){
            questionsCursor.moveToFirst();
            do {
                tempQuestion = tempQuestion.extractQuestion(questionsCursor);
                outputText = outputText.concat(tempQuestion.toString()).concat("\n");
                //outputText = outputText.concat(cryptographer.decryptText(tempQuestion.getValue(),new IvParameterSpec(tempQuestion.getIv()))).concat("\n");
            } while (questionsCursor.moveToNext());
        }
        return  outputText;
    }

    private String getQuestionLists(){
        ArrayList<QuestionList> questionLists = accountsDB.getListOfQuestionLists();
        String outputText = "";
        QuestionList tempQuestionList = new QuestionList();
        if (questionLists != null && questionLists.size() > 0){
            int i = 0;
            while(i<questionLists.size()){
                tempQuestionList = questionLists.get(i);
                outputText = outputText.concat(tempQuestionList.toString()).concat("\n");
                i++;
            }
        }
        return  outputText;
    }

    private BufferedReader readFromFile(Context context, String fileName) {
        BufferedReader bufferedReader = null;
        //StringBuilder stringBuilder = null;
        try {
            this.inputStream = context.openFileInput(fileName);
            if ( this.inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                //text = stringBuilder.toString();
            }
        }catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            MainActivity.displayToast(this.getBaseContext(),"File with name: "+fileName+" does not exist. Load the fil and try again.",Toast.LENGTH_LONG,Gravity.CENTER);
        }finally {
            return bufferedReader;
        }// End of try catch block
    }// End of readFromFile method

    private Boolean importData(){
            Boolean result = false;

        //Call method to get password list as text
        //Insert pre-defined suggested security questions in the DB. No answer associated to question yet.

        //Check categories are written onto the DB
        if(writeCategories()){
            //Check user names are written onto the DB
            if(writeUserNames()){
                //Check the passwords are written onto the DB
                if (writePsswrds()) {
                    //Check answers are written onto the DB
                    if(writeAnswers()){
                        //Check questions are written onto the DB.
                        if(writeQuestions()){
                            result = true;
                        }// End of if that checks write Questions finished successfully.
                    }//End of if that checks writeAsnwers finished successfully.
                }// End of if that checks writePsswrds finished successfully
            }// End of if that check write  usernames finished successfully
        }


        String importResultText = "";
        if (result) {
            importResultText = "All data has now been imported to the database.";
        }else{
            importResultText = "Error while importing data into the database.";
        }
        MainActivity.displayToast(this.getBaseContext(),importResultText,Toast.LENGTH_SHORT,Gravity.CENTER);
         return result;
        }

    private Boolean writeCategories(){
        Boolean result =false;
        Context context = getBaseContext();
        BufferedReader bufferedReader = readFromFile(context,getString(R.string.category_txt));
        String receiveString = "";
        //StringBuilder stringBuilder = new StringBuilder();
        if(bufferedReader != null){
            try {
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //Create Category object
                    //Split the text line into an array to separate text values
                    String[] categoryArray = receiveString.split("; ");
                    String categoryValue = categoryArray[0].trim();
                    int iconID = Integer.valueOf(categoryArray[1].trim());
                    Icon catIcon = accountsDB.getIconByID(iconID);
                    //Check the category is not in the DB already.
                    Cursor cursor = this.accountsDB.getCategoryByName(categoryValue);
                    if (cursor == null || cursor.getCount() == 0) {
                        //If Category not in the list create new Psswrd object and store it in global variable used to build the account object
                        Category category = new Category(categoryValue,catIcon);
                        //Call DB method to insert  the Psswrd object into the DB
                        int categorydID = -1;
                        categorydID = this.accountsDB.addItem(category);
                        if (categorydID != -1) {
                            result = true;
                        }else {
                            result = false;
                            break;
                        }
                    } else{
                        result = true;
                    }// End of if statement to check password is not in the DB
                }// End of while loop to iterate through text file
                this.inputStream.close();
            } catch (IOException e) {
                Log.e("Import/Export activity", "Can not read file: " + e.toString());
            }// End of try catch block
        }

        return result;
    }// End of WritePsswrds method
    private Boolean writePsswrds(){
            Boolean result =false;
            Context context = getBaseContext();
            BufferedReader bufferedReader = readFromFile(context,getString(R.string.psswrd_txt));
            String receiveString = "";
            //StringBuilder stringBuilder = new StringBuilder();
            if(bufferedReader != null){
                try {
                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        //Create Psswrd object
                        String psswrdValue = receiveString.trim();
                        //Check the password is not in the DB already.
                        Cursor cursor = this.accountsDB.getPsswrdByName(psswrdValue);
                        if (cursor == null || cursor.getCount() == 0) {
                            byte[] psswrdValueEncrypted =  null;
                            //Encrypt the Psswrd
                            psswrdValueEncrypted = this.cryptographer.encryptText(psswrdValue);
                            //If Psswrd not in the list create new Psswrd object and store it in global variable used to build the account object
                            Psswrd psswrd = new Psswrd(psswrdValueEncrypted,this.cryptographer.getIv().getIV());
                            //Call DB method to insert  the Psswrd object into the DB
                            int psswrdID = -1;
                            psswrdID = this.accountsDB.addItem(psswrd);
                            if (psswrdID != -1) {
                                result = true;
                            }else {
                                result = false;
                                break;
                            }
                        } else{
                            result = true;
                        }// End of if statement to check password is not in the DB
                    }// End of while loop to iterate through text file
                    this.inputStream.close();
                } catch (IOException e) {
                    Log.e("Import/Export activity", "Can not read file: " + e.toString());
                }// End of try catch block
            }

            return result;
    }// End of WritePsswrds method

    private Boolean writeUserNames(){
        Boolean result =false;
        Context context = getBaseContext();
        BufferedReader bufferedReader = readFromFile(context,getString(R.string.username_txt));
        String receiveString = "";
        //StringBuilder stringBuilder = new StringBuilder();
        if(bufferedReader != null){
            try {
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //Create UserName object
                    String userNameValue = receiveString.trim();
                    //Check the password is not in the DB already.
                    Cursor cursor = this.accountsDB.getUserNameByName(userNameValue);
                    if (cursor == null || cursor.getCount() == 0) {
                        byte[] userNameValueEncrypted =  null;
                        //Encrypt the UserName
                        userNameValueEncrypted = this.cryptographer.encryptText(userNameValue);
                        //Create new userName object and store it in global variable used to build the account object
                        UserName userName = new UserName(userNameValueEncrypted,this.cryptographer.getIv().getIV());
                        //Call DB method to insert  the userName object into the DB
                        int userNameID = -1;
                        userNameID = this.accountsDB.addItem(userName);
                        if (userNameID != -1) {
                            result = true;
                        }else {
                            result = false;
                            break;
                        }//End of if else statement that check userName was inserted into DB
                    }else{
                        result = true;
                    }// End of if to check the user name is not in the DB already
                }// End of while loop to iterate through text fiile
                this.inputStream.close();
            } catch (IOException e) {
                Log.e("Import/Export activity", "Can not read file: " + e.toString());
            }// End of try catch block
        }
        return result;
    }// End of writeUserNames method

    private Boolean writeAnswers() {
        Boolean result = false;
        Context context = getBaseContext();
        BufferedReader bufferedReader = readFromFile(context,getString(R.string.questions_txt));
        String receiveString = "";
        if(bufferedReader != null){
            try {
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //Split the text line into an array to separate text values
                    String[] questionAnswerArray = receiveString.split("; ");
                    //Select the correct location from the array to read the answer
                    String answerValue = questionAnswerArray[3].trim();
                    //Check the password is not in the DB already.
                    Cursor cursor = this.accountsDB.getAnswerByName(answerValue);
                    if (cursor == null || cursor.getCount() == 0) {
                        byte[] answerValueEncrypted =  null;
                        //Encrypt the answer
                        answerValueEncrypted = this.cryptographer.encryptText(answerValue);
                        //Create new answer object and store it in global variable used to build the account object
                        Answer answer = new Answer(answerValueEncrypted,this.cryptographer.getIv().getIV());
                        //Call DB method to insert  the answer object into the DB
                        int answerID = -1;
                        answerID = this.accountsDB.addItem(answer);
                        if (answerID != -1) {
                            result = true;
                        }else {
                            result = false;
                            break;
                        }//End of if else statement that check userName was inserted into DB
                    }else{
                        result = true;
                    }// End of if to check the user name is not in the DB already
                }
            } catch (IOException e) {
                Log.e("Import/Export activity", "Can not read file: " + e.toString());
            }// End of try catch block
        }
        return result;
    }// End of writeAsnwers method

    private Boolean writeQuestions() {
        Boolean result = false;
        Context context = getBaseContext();
        BufferedReader bufferedReader = readFromFile(context,getString(R.string.questions_txt));
        String receiveString = "";
        if(bufferedReader != null){
            try {
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //Split the line text into an array object
                    String[] questionAnswerArray = receiveString.split("; ");
                    //Select the correct location from the array to read the question text
                    String questionValue = questionAnswerArray[1].trim();
                    //Select the correct location from the array to read the answer
                    String answerValue = questionAnswerArray[3].trim();
                    //Check the answer is in the DB already.
                    Cursor answerCursor = this.accountsDB.getAnswerByName(answerValue);
                    Answer answer = null;
                    //If Answer is in the DB (cursor not null and not empty), get the Answer object
                    if (answerCursor != null && answerCursor.getCount() > 0) {
                        answer = Answer.extractAnswer(answerCursor);
                        //Check the question is not in the DB already.
                        Cursor cursor = this.accountsDB.getQuestionByValue(questionValue);
                        if (cursor == null || cursor.getCount() == 0) {
                            //Create new question object
                            Question question = new Question(questionValue,answer);
                            //Call DB method to insert  the question object into the DB
                            int questionID = -1;
                            questionID = this.accountsDB.addItem(question);
                            //Check the question insertion was successful.
                            if (questionID != -1) {
                                result = true;
                            }else {
                                result = false;
                                break;
                            }//End of if else statement that check userName was inserted into DB
                        }else{
                            result = true;
                        }// End of if to check the user name is not in the DB already
                    }else{
                        //If answer is not in the DB, already, stop and return failure.
                        result = false;
                        break;
                    }//End of if that checks the answer is already in the DB

                }
            } catch (IOException e) {
                Log.e("Import/Export activity", "Can not read file: " + e.toString());
            }// End of try catch block
        }
        return result;
    }// End of writeQuestions method

}//End of Import_Export class
