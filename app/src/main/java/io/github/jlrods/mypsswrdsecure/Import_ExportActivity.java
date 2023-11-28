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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.login.LoginActivity;

public class Import_ExportActivity extends AppCompatActivity {
    ThemeUpdater themeUpdater = null;
    protected Button btnExport = null;
    protected AccountsDB accountsDB = null;
    protected Cryptographer cryptographer = null;
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

        //String psswrdList = psswrdCursor.toString();
        btnExport = (Button) findViewById(R.id.btnExport);
        this.btnExport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                exportData();
            }//End of onClick method
        });//End of setOnClickListener method
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
        //Call method to get password list as text
        outputText = getPsswrds();
        //Call method to write Passwrods list onto a file
        if(writeToFile(outputText,context,"Psswrd.txt")){
            outputText = getUserNames();
            if(writeToFile(outputText,context,"UserName.txt")){
                outputText =  getQuestions();
                if(writeToFile(outputText,context,"Questions.txt")){
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
        questionsCursor.moveToFirst();
        do {
            tempQuestion = tempQuestion.extractQuestion(questionsCursor);
            outputText = outputText.concat(tempQuestion.toString()).concat("\n");
            //outputText = outputText.concat(cryptographer.decryptText(tempQuestion.getValue(),new IvParameterSpec(tempQuestion.getIv()))).concat("\n");
        } while (questionsCursor.moveToNext());
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
}
