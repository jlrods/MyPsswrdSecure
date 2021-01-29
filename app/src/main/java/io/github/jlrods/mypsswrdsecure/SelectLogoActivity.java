package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

// Activity class to manage logo selection display
public class SelectLogoActivity extends AppCompatActivity implements ThemeHandler{
    Icon selectedIcon = null;
    int selectedPosition = -1;
    String selectedImgLocation ="";
    Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SelLogOnCreate","Enter onCreate method in SelectLogoActivity class.");
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
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
        }
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
                    iconAdapter.getIconList().get(adapterPosition).setSelected(true);
                    for(int i=0;i<iconAdapter.getIconList().size();i++){
                        if(i!=adapterPosition){
                            iconAdapter.getIconList().get(i).setSelected(false);
                        }//End if statement
                    }// End of for loop
                    selectedIcon = iconAdapter.getIconList().get(adapterPosition);
                    selectedPosition = adapterPosition;
                };// End of if else statement to check if logo is selected

                iconAdapter.updateItemIsSelected(adapterPosition,true);
                //iconAdapter.getIconList().get(adapterPosition).setSelected(true);
                iconAdapter.notifyDataSetChanged();
                Log.d("IconSetOnClickList","Exit onClick method of setOnItemClickListener in SelectLogoActivity class.");
            }//End of OnClick method
        });
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

    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }//End of switch statement

        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in SelectLogoActivity class.");
        finish();
        return result;
    }

    @Override
    //Method to retrieve the theme color resource id of the color name passed in as argument
    public int fetchThemeColor(String colorName) {
        Log.d("fetchThemeColor","Enter the fetchThemeColor method in the MainActivity class.");
        //Declare and initialize attribute color id
        int attributeColor = 0;
        //Check color name passed in as argument and assign it resource id to attributeColor variable
        switch(colorName){
            case "colorAccent":
                attributeColor = R.attr.colorAccent;
                break;
            case "colorPrimary":
                attributeColor = R.attr.colorPrimary;
                break;
            case "colorPrimaryDark":
                attributeColor = R.attr.colorPrimaryDark;
                break;
        }//End of switch statement
        //Create TypedValue object to hold the theme attribute data
        TypedValue value = new TypedValue ();
        //Call method to retrieve theme attribute data
        this.getTheme().resolveAttribute (attributeColor, value, true);
        Log.d("fetchThemeColor","Exit the fetchThemeColor method in the MainActivity class.");
        //return the data for the color required
        return value.data;
    }//End of fetchThemeColor method
}//End of SelectLogoActivity class
