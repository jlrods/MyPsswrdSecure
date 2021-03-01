package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class AddCategoryAcitivity extends AddItemActivity{
    //Attribute definition
    protected ImageView imgSelectedIcon = null;
    protected LinearLayout catIconListSubLayout = null;
    protected TableLayout categoryIconTable = null;
    protected Category category = null;
    protected Icon categoryIcon = null;
    protected ThemeUpdater themeUpdater;
    //Method definiton

    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddUserNameActivity class.");
        //Set activity title
        getSupportActionBar().setTitle(R.string.addCatTitle);
        this.themeUpdater = new ThemeUpdater(this);
        //Update layout fields according to Add Security question layout
        this.categoryIcon = new Icon();
        this.imgAddActivityIcon.setImageResource(R.drawable.format_list_bulleted);
        this.tvAddActivityTag.setText(R.string.account_cat);
        this.catIconListSubLayout = findViewById(R.id.layout_add_item_categoryIcon);
        this.catIconListSubLayout.setVisibility(View.VISIBLE);
        ScrollView catIconList = findViewById(R.id.categoryIconScrallView);
        this.categoryIconTable = findViewById(R.id.categoryIconTable);
        this.imgSelectedIcon = findViewById(R.id.format_list_bulleted);
        this.imgSelectedIcon.setColorFilter(this.themeUpdater.fetchThemeColor("colorAccent"), android.graphics.PorterDuff.Mode.SRC_IN);
        //this.imgSelectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
        for(int i=0;i< categoryIconTable.getChildCount();i++){
            TableRow row = (TableRow) categoryIconTable.getChildAt(i);
            for(int j=0;j< row.getChildCount();j++){
                ImageView image = (ImageView) row.getChildAt(j);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleCatIconSelection(v);
                    }
                });
            }
        }
        this.categoryIcon = new Icon(getResources().getResourceEntryName(this.imgSelectedIcon.getId()),MainActivity.getRESOURCES(),true, this.imgSelectedIcon.getId());
        Log.d("OnCreateAddQuest","Exit onCreate method in the AddUserNameActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddCategoryActivity class.");
        //Boolean to return method result
        boolean result = false;
        int categoryID = -1;

        Intent intent = new Intent();
        //Flag to make sure all data was added on DB
        boolean dbTransCompleted = true;
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in AddCategoryActivity class.");
                //Check if Category attributes are  valid
                    if(this.isDataValid(8)){
                    //Create the category object
                        //Check if icon is already in use in the DB, if not, insert the new icon in the Icon table
                        //Declare and initialize a new icon, populating its value with result from querying the DB for an Icon with the name of the
                        //icon selected by the user
                        if(!isIconAssigned()){
                            dbTransCompleted = false;
                        }//End of if else statement tha checks whether the icons is being assigned or not
                        if(dbTransCompleted){
                            //Create new category object with the icon object and category name
                            this.category = new Category(this.etNewItemField.getText().toString(),this.categoryIcon);
                            //Run DB query to insert new category and check for result
                            if((categoryID = this.accountsDB.addItem(this.category))>0){
                                //If id number from DB returns greater that 0, means category insertion was successful
                                this.category.set_id(categoryID);
                            }else{
                                //Otherwise, raise boolean flag to prompt user about DB problem
                                dbTransCompleted = false;
                            }//End of if else statement to check the category insertion was successful
                            //Check for DB transaction result
                            if(dbTransCompleted){
                                Log.d("onOptionsItemSelected","Successful save Category transaction on onOptionsItemSelected method in AddCategoryActivity class.");
                                intent.putExtra("categoryID",this.category.get_id());
                                intent.putExtra("categoryName",this.category.getName());
                                setResult(RESULT_OK, intent);
                                finish();
                            }else{
                                //Otherwise prompt the user the question already exists
                                MainActivity.displayToast(this,getResources().getString(R.string.catNotAdded), Toast.LENGTH_LONG, Gravity.CENTER);
                            }//End of if else statement that checks for DB insertion results
                        }//End of if statement that check Icon Db action was successful
                    }//End of if statement to check the question is valid
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddCategoryActivity class.");
                finish();
                break;
            case R.id.action_logout:
                //Call method to throw LoginActivity and clear activity stack.
                Log.d("onOptionsItemSelected","Logout option selected on onOptionsItemSelected method in AddCategoryActivity class.");
                MainActivity.logout(this);
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddCategoryActivity class.");
        return result;
    }//End of onOptionsItemSelected

    private void toggleCatIconSelection(View v){
        Log.d("toggleCatIconSelection","Enter toggleCatIconSelection method in AddCategoryActivity class.");
        if(imgSelectedIcon.getId() != v.getId()){
            this.categoryIcon.setName(getResources().getResourceEntryName(v.getId()));
            this.categoryIcon.setResourceID(v.getId());
            //@Fixme: setColorFilter method does not required min API21, this might fix the setTintFilter issue in other activities
            ((ImageView) v).setColorFilter(this.themeUpdater.fetchThemeColor("colorAccent"), android.graphics.PorterDuff.Mode.SRC_IN);
            //((ImageView) v).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSelectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSelectedIcon = (ImageView) v;
        }//End of if statement to check the selected image isn't the same
        Log.d("toggleCatIconSelection","Exit toggleCatIconSelection method in AddCategoryActivity class.");
    }//End of toggleCatIconSelection method

    protected boolean isIconAssigned(){
        Log.d("isIconAssigned","Enter isIconAssigned method in AddCategoryActivity class.");
        boolean isIconAssigned = false;
        int iconID = -1;
        Icon iconInUse;
        if((iconInUse = this.accountsDB.getIconByName(this.categoryIcon.getName())) != null){
            this.categoryIcon = iconInUse;
            isIconAssigned = true;
        }else{
            //If the icon returns null, means it has to be stored in the DB
            if((iconID = this.accountsDB.addItem(this.categoryIcon))>0) {
                //If id number from DB returns greater that 0, means icon insertion was successful
                this.categoryIcon.set_id(iconID);
                isIconAssigned = true;
            }//End of if else statement to check the icon insertion was successful
        }//End of if else statement tha checks whether the icons is being used or not
        Log.d("isIconAssigned","Enter isIconAssigned method in AddCategoryActivity class.");
        return isIconAssigned;
    }//End of isIconAssigned method
}//End of AddCategoryAcitivity class
