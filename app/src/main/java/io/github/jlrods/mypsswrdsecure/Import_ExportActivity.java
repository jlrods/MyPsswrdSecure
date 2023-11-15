package io.github.jlrods.mypsswrdsecure;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Import_ExportActivity extends AppCompatActivity {
    ThemeUpdater themeUpdater;
    protected Button btnExport;

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
        btnExport = (Button) findViewById(R.id.btnExport);
        this.btnExport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Call method to write a file
                writeToFile( "This is a test",getBaseContext());
            }//End of onClick method
        });//End of setOnClickListener method
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

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("exportData.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
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
}
