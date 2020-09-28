package io.github.jlrods.mypsswrdsecure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView imgSelectedIcon = null;
    private LinearLayout catIconListSubLayout = null;
    private TableLayout categoryIconTable = null;
    private Category category = null;
    private Icon categoryIcon = null;
    //Method definiton

    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateAddQuest","Enter onCreate method in the AddUserNameActivity class.");
        //Update layout fields according to Add Security question layout
        this.categoryIcon = new Icon();
        this.imgAddActivityIcon.setImageResource(R.drawable.format_list_bulleted);
        this.tvAddActivityTag.setText(R.string.account_cat);
        this.catIconListSubLayout = findViewById(R.id.layout_add_item_categoryIcon);
        this.catIconListSubLayout.setVisibility(View.VISIBLE);
        ScrollView catIconList = findViewById(R.id.categoryIconScrallView);
        TableLayout categoryIconTable = findViewById(R.id.categoryIconTable);
        imgSelectedIcon = findViewById(R.id.format_list_bulleted);
        imgSelectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
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
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in AddQuestionActivity class.");
        //Boolean to return method result
        boolean result = false;
        int categoryID = -1;
        int iconID = -1;
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
                        Icon iconInUse;
                        if((iconInUse = this.accountsDB.getIconByName(this.categoryIcon.getName())) != null){
                            this.categoryIcon = iconInUse;
                        }else{
                            if((iconID = this.accountsDB.addItem(this.categoryIcon))>0) {
                                this.categoryIcon.set_id(iconID);
                            }else{
                                dbTransCompleted = false;
                            }
                        }
                        this.category = new Category(this.etNewItemField.getText().toString(),this.categoryIcon);
                        if((categoryID = this.accountsDB.addItem(this.category))>0){
                            this.category.set_id(categoryID);
                        }else{
                            dbTransCompleted = false;
                        }
                        if(dbTransCompleted){
                            Log.d("onOptionsItemSelected","Successful save Category transaction on onOptionsItemSelected method in AddCategoryActivity class.");
                            intent.putExtra("categoryID",this.category.get_id());
                            intent.putExtra("categoryName",this.category.getName());
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            //Otherwise prompt the user the question already exists
                            MainActivity.displayToast(this,getResources().getString(R.string.catNotAdded), Toast.LENGTH_LONG, Gravity.CENTER);
                        }
                    }//End of if statement to check the question is valid


                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in AddQuestionActivity class.");
                finish();
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in AddQuestionActivity class.");
        return result;
    }//End of onOptionsItemSelected

    public void toggleCatIconSelection(View v){

        if(imgSelectedIcon.getId() != v.getId()){
            this.categoryIcon.setName(getResources().getResourceEntryName(v.getId()));
            this.categoryIcon.setResourceID(v.getId());
            ((ImageView) v).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSelectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            imgSelectedIcon = (ImageView) v;
        }


    }


}//End of AddCategoryAcitivity class
