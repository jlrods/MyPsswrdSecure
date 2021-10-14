package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

// Activity class to manage logo selection display
public class SelectLogoActivity extends AppCompatActivity {
    private Icon selectedIcon = null;
    private int selectedPosition = -1;
    private String selectedImgLocation ="";
    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SelLogOnCreate","Enter onCreate method in SelectLogoActivity class.");
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Set language as per preferences
        MainActivity.setAppLanguage(this);
        setContentView(R.layout.activity_select_logo);
        //Set activity title
        getSupportActionBar().setTitle(R.string.addLogoTitle);
        final RecyclerView rvLogos = (RecyclerView) findViewById(R.id.layout_rec_view_logo);
        final IconAdapter iconAdapter = new IconAdapter(this, MainActivity.getAccountsLogos());
        //Extract extra data from Bundle object
        extras = getIntent().getExtras();
        selectedPosition = extras.getInt("selectedImgPosition");
        if(selectedPosition >= 0){
            selectedIcon = iconAdapter.getIconList().get(selectedPosition);
            iconAdapter.getIconList().get(this.selectedPosition).setSelected(true);
        }else{
            selectedIcon = MainActivity.getMyPsswrdSecureLogo();
        }//End of if else statement to check selected position is positive value
        selectedImgLocation = extras.getString("selectedImgLocation");
        //Set the logo OnClickListener object so the Logo is selected when clicked on it
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
                    selectedPosition = adapterPosition;
                };// End of if else statement to check if logo is selected
                //Update the SparseBooleanArray to keep record of logo selection
                iconAdapter.updateItemIsSelected(adapterPosition,true);
                iconAdapter.notifyDataSetChanged();
                Log.d("IconSetOnClickList","Exit onClick method of setOnItemClickListener in SelectLogoActivity class.");
            }//End of OnClick method
        });//End of setOnItemClickListener method
        rvLogos.setAdapter(iconAdapter);
        rvLogos.setLayoutManager(new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL));
        //Check if RV position is beyond first 6 items
        if(this.selectedPosition>=5){
            //If so then check if position is even (right hand side) or odd (left hand side)
            if(this.selectedPosition%2==0){
                //If even move rv to that position
                rvLogos.getLayoutManager().scrollToPosition(this.selectedPosition);
            }else{
                //In case of odd move to position even position before the item to keep all items in the same column
                rvLogos.getLayoutManager().scrollToPosition(this.selectedPosition-1);
            }//End of if else statement
        }//End of if statement to check position is out of screen
        Log.d("SelLogOnCreate","Exit  onCreate method in SelectLogoActivity class.");
    }//End of onCreate

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResumeMain", "Enter onResume method in SelectLogoActivity class.");
        //Call MainActivity static method to check for logout timeout to display logout prompt accordingly
        MainActivity.checkLogOutTimeOut(this);
        Log.d("onResumeMain", "Exit onResume method in SelectLogoActivity class.");
    }//End of onResume method

    public void onStop(){
        super.onStop();
        Log.d("onStopMain", "Enter onStop method in SelectLogoActivity class.");
        MainActivity. checkForNotificationSent(this,false);
        Log.d("onStopMain", "Exit onStop method in SelectLogoActivity class.");
    }//End of onStop method

    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateOptionsMenu","Enter|Exit  onCreate method in SelectLogoActivity class.");
        getMenuInflater().inflate(R.menu.activity_menu_save_cancel, menu);
        return true;
    }// Find fe OnCreateOptionsMenu

    //Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in SelectLogoActivity class.");
        //Boolean to return method result
        boolean result = false;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                //Load the extra info to be sent back to caller activity
                intent.putExtra("selectedImgID",this.selectedIcon.get_id());
                intent.putExtra("selectedImgLocation",this.selectedIcon.getLocation());
                intent.putExtra("selectedImgPosition",this.selectedPosition);
                setResult(RESULT_OK, intent);
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in SelectLogoActivity class.");
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in SelectLogoActivity class.");
                break;
            case R.id.action_logout:
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in SelectLogoActivity class.");
                //Call method to check for notification sent and update if required
                MainActivity.checkForNotificationSent(this,true);
                //Call method to throw LoginActivity and clear activity stack.
                MainActivity.logout(this);
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in SelectLogoActivity class.");
        finish();
        return result;
    }//End of onOptionsItemSelected method
}//End of SelectLogoActivity class