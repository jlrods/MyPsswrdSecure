package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//Class to handle the nav drawer background selection. Unlike SelectLogoActivity, this activity does not retrieved
//the current selection or background used at the moment, it displays the list of available backgrounds from the top.
public class SelectNavDrawerBckGrndActivity extends AppCompatActivity {
    Icon selectedIcon = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SelBckgrndOnCreate", "Enter onCreate method in SelectNavDrawerBckGrndActivity class.");
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Set language as per preferences
        MainActivity.setAppLanguage(this);
        //Set activity title
        getSupportActionBar().setTitle(R.string.selectBckgrd);
        setContentView(R.layout.activity_select_logo);
        TextView tvHeading = findViewById(R.id.tvLogoHeading);
        tvHeading.setText(R.string.selectBckgrdHeading);
        final RecyclerView rvLogos = (RecyclerView) findViewById(R.id.layout_rec_view_logo);
        final IconAdapter iconAdapter = new IconAdapter(this, MainActivity.getNavDrawerBckgrnds());
        iconAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IconSetOnClickList","Enter onClick method of setOnItemClickListener in SelectLogoActivity class.");
                //Retrieve the position of current item in the RecyclerView
                //This position is in line with the SparseBoolean array to hold the selected state of each item
                int adapterPosition = rvLogos.getChildAdapterPosition(v);
                // Check if Icon object in the retrieved position has attribute isSelected set to
                // true or false to revert to toggle the selection state
                if(iconAdapter.getIconList().get(adapterPosition).isSelected()){
                    //If that's the case, set it to false
                    iconAdapter.getIconList().get(adapterPosition).setSelected(false);
                }else{
                    //Otherwise, set to true and iterate through the rest of the icon list to set any other icon to not selected
                    iconAdapter.getIconList().get(adapterPosition).setSelected(true);
                    for(int i=0;i<iconAdapter.getIconList().size();i++){
                        if(i!=adapterPosition){
                            iconAdapter.getIconList().get(i).setSelected(false);
                        }//End if statement
                    }// End of for loop
                    selectedIcon = iconAdapter.getIconList().get(adapterPosition);
                };// End of if else statement to check if logo is selected
                //Update the SparseBooleanArray to keep record of logo selection
                iconAdapter.updateItemIsSelected(adapterPosition,true);
                //iconAdapter.getIconList().get(adapterPosition).setSelected(true);
                iconAdapter.notifyDataSetChanged();
                Log.d("IconSetOnClickList","Exit onClick method of setOnItemClickListener in SelectLogoActivity class.");
            }//End of OnClick method
        });//End of setOnItemClickListener method
        rvLogos.setAdapter(iconAdapter);
        rvLogos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.VERTICAL);
        rvLogos.addItemDecoration(itemDecoration);
        Log.d("SelBckgrndOnCreate", "Exit onCreate method in SelectNavDrawerBckGrndActivity class.");
        //Set the logo OnClickListener object so the Logo is selected when clicked on it
    }//End of onCreate method

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResumeSelectNav", "Enter onResume method in SelectNavDrawerBckGrndActivity class.");
        //Call MainActivity static method to check for logout timeout to display logout prompt accordingly
        MainActivity.checkLogOutTimeOut(this);
        Log.d("onResumeSelectNav", "Exit onResume method in SelectNavDrawerBckGrndActivity class.");
    }//End of onResume method

    public void onStop(){
        super.onStop();
        Log.d("onStopNavBck", "Enter onStop method in SelectNavDrawerBckGrndActivity class.");
        //Call method to check for notification sent and update if required. Pass in as argument the flag to identify if auto log out has timed out
        MainActivity.checkForNotificationSent(this,MainActivity.checkIsAppLoggedOut());
        Log.d("onStopNavBck", "Exit onStop method in SelectNavDrawerBckGrndActivity class.");
    }//End of onStop method

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Call method to check for notification sent and update if required. Pass in as argument the flag to identify if auto log out has timed out
        MainActivity.checkForNotificationSent(this,MainActivity.checkIsAppLoggedOut());
        Log.d("onDestroyNavBck", "Enter/Exit onDestroy method in SelectNavDrawerBckGrndActivity class.");
    }//End of onDestroy method

    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in SelectNavDrawerBckGrndActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                if(selectedIcon != null){
                    intent.putExtra("selectedImgID",this.selectedIcon.get_id());
                    intent.putExtra("selectedImgResourceID",this.selectedIcon.getResourceID());
                    setResult(RESULT_OK, intent);
                }//End of if statement to check the selected icon isn't null
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in SelectNavDrawerBckGrndActivity class.");
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in SelectNavDrawerBckGrndActivity class.");
                break;
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in SelectNavDrawerBckGrndActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in SelectNavDrawerBckGrndActivity class.");
        finish();
        return result;
    }//End of onOptionsItem selected method
}//End of SelectNavDrawerBckGrd class
