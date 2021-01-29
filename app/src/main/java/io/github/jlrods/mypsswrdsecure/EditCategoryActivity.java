package io.github.jlrods.mypsswrdsecure;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

public class EditCategoryActivity extends AddCategoryAcitivity {
    //Attribute definition
    private Bundle extras;
    //Variable to store the menu item position selected on AlertDialog box single choice, as this value is sent back to caller method
    //so the menu item can be updated on the UI accordingly
    int positionInCatList;
    //Method definition
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreateEditCat", "Enter onCreate method in the EditCategoryActivity class.");
        //Get data from caller activity
        this.extras = getIntent().getExtras();
        //Extract category by passing in the category _id sent by caller activity
        this.category = this.accountsDB.getCategoryByID(extras.getInt(MainActivity.getIdColumn()));
        //Set up UI with category data
        if(this.category != null){
            //Get the position on the list (the alertdialog list) and add up the hardcoded items in the menu
            this.positionInCatList = this.extras.getInt("positionInCatList")+ MainActivity.getIndexToGetLastTaskListItem();
            //Get the text resource id for those preloaded categories which text can be translated
            int textID = getResources().getIdentifier(MainActivity.getCategoryList().get(positionInCatList).getName(),"string",getPackageName());
            String categoryName = "";
            //If textID is 0, means it's not stored in the app resources, which means it won't be translated but it will be displayed as saved on DB
            if(textID > 0){
                //If res id number exists, set the category name as per the string text, not the string ID
                categoryName = getResources().getString(textID);
            }else{
                //In the case of not being a resource, print the text retrieved from DB
                categoryName = MainActivity.getCategoryList().get(positionInCatList).getName();
            }//End of if else statement
            //Set the text into the edit text field
            this.etNewItemField.setText(categoryName);
            //Enable the edit text field only for those categories that are not preloaded
            if(this.isCategoryPreLoaded(this.category)){
                this.etNewItemField.setEnabled(false);
            }
        }//End of safe if statement to check the category retrieved is not null
        //Remove selection color from default image (formatted bullet list)
        this.imgSelectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

        //int resID = getResources().getIdentifier(this.category.getIcon().getName(),"drawable",getPackageName());

        //Assign selection to category icon by passing in its ID, found via the resource name
        this.imgSelectedIcon = this.findIconImageByName(this.category.getIcon().getName());
        //Set up the selection color to the new selected icon
        if(this.imgSelectedIcon != null){
            this.imgSelectedIcon.setColorFilter(this.fetchThemeColor("colorAccent"), android.graphics.PorterDuff.Mode.SRC_IN);
            //this.imgSelectedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);
            this.categoryIcon = new Icon(getResources().getResourceEntryName(this.imgSelectedIcon.getId()),MainActivity.getRESOURCES(),true, this.imgSelectedIcon.getId());
        }//End of if statement to check the image selected image view isn't null
        Log.d("OnCreateEditCat", "Enter onCreate method in the EditCategoryActivity class.");
    }//End of onCreate method

    // Method to check the menu item selected and execute the corresponding actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected","Enter onOptionsItemSelected method in EditCategoryActivity class.");
        //Boolean to return method result
        boolean result = false;
        int categoryID = -1;
        int iconID = -1;
        Intent intent = new Intent();
        //Check the id of item selected in menu
        switch (item.getItemId()) {
            case R.id.select_logo_save:
                Log.d("onOptionsItemSelected","Save option selected on onOptionsItemSelected method in EditCategoryActivity class.");
                //Flag to make sure all data was added on DB
                boolean dbTransCompleted = true;
                //Check if Category attributes are  valid

                //Boolean flag to be checked later in code, if true continue with update process
                boolean isIconAndCatNameValid;
                //Check what piece of data changed during the edition process and the category name is valid by calling method to do the check
                if(!(isIconAndCatNameValid=this.checkForCategoryChanges())){
                    //If no change, Throw user notification toast
                    //Prompt the user the category name or icon value have not changed
                    MainActivity.displayToast(this, getResources().getString(R.string.categoryAndIconNoTChanged), Toast.LENGTH_SHORT, Gravity.CENTER);
                }//End of if else statement that check what piece of that changed

                //If data changed and is valid, proceed with DB transactions
                if(isIconAndCatNameValid){
                    //Check if icon is already in use in the DB, if not, insert the new icon in the Icon table
                    //Declare and initialize a new icon, populating its value with result from querying the DB for an Icon with the name of the
                    //icon selected by the user
                    if(!isIconAssigned()){
                        dbTransCompleted = false;
                    }//End of if else statement tha checks whether the icons is being assigned
                    //Check if old icon still in use, if not, delete from the DB
                    if(!this.category.getIcon().getName().equals(this.categoryIcon.getName())){
                        if(this.accountsDB.getTimesUsedIconInCategory(this.category.getIcon().get_id()) == 1){
                            if(!this.accountsDB.deleteItem(this.category.getIcon())){
                                dbTransCompleted = false;
                            }//End of if statement that checks the icon was actually deleted from DB successfully
                        }//End of if statement that checks the number of times the icon is used
                    }//End of if statement that checks the icon changed from original value from DB
                    //Check if all DB transaction have been completed successfully so far
                    if(dbTransCompleted){
                        //Update the category object retrieved by passing its id during onCreate method
                        this.category.setName(this.etNewItemField.getText().toString());
                        this.category.setIcon(this.categoryIcon);
                        //Store data to be updated on the DB
                        ContentValues values = new ContentValues();
                        values.put(MainActivity.getIdColumn(),this.category.get_id());
                        values.put(MainActivity.getNameColumn(),this.category.getName());
                        values.put(MainActivity.getIconIdColumn(),this.category.getIcon().get_id());
                        //Run DB query to update new category and check for result
                        if(!this.accountsDB.updateTable(MainActivity.getCategoryTable(),values)){
                            //If id number from DB returns false, means category update was successful
                            dbTransCompleted = false;
                        }//End of if statement to check the category update was successful
                        //Check for DB transaction result
                        if(dbTransCompleted){
                            Log.d("onOptionsItemSelected","Successful save Category transaction on onOptionsItemSelected method in EditCategoryActivity class.");
                            intent.putExtra(MainActivity.getIdColumn(),this.category.get_id());
                            intent.putExtra("categoryName",this.category.getName());
                            intent.putExtra("positionInCatList",this.positionInCatList);
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            //Otherwise prompt the user the question already exists
                            MainActivity.displayToast(this,getResources().getString(R.string.catNotAdded), Toast.LENGTH_LONG, Gravity.CENTER);
                        }//End of if else statement that checks for DB insertion results
                    }else{
                        //Otherwise prompt the user the question already exists
                        MainActivity.displayToast(this,getResources().getString(R.string.catNotAdded), Toast.LENGTH_LONG, Gravity.CENTER);
                    }//End of if statement to check the Icon DB transaction was successful
                }//End of if statement to check the question is valid
                break;
            case R.id.select_logo_cancel:
                //Set activity result as cancelled so DisplayAccActivity can decide what to do if this is the case
                setResult(RESULT_CANCELED, intent);
                Log.d("onOptionsItemSelected","Cancel option selected on onOptionsItemSelected method in EditCategoryActivity class.");
                finish();
                break;
        }//End of switch statement
        Log.d("onOptionsItemSelected","Exit successfully onOptionsItemSelected method in EditCategoryActivity class.");
        return result;
    }//End of onOptionsItemSelected


    //Method to find image view by tag in the edit category layout as R.id for resources is not the same as the R.id for layout view object, even
    //if the id in plain english is the same. Therefore, a tag attribute is used withing the view xml definition
    private ImageView findIconImageByName(String  iconName){
        Log.d("findIconImageByName", "Enter findIconImageByName method in the EditCategoryActivity class.");
        int i =0;
        boolean found = false;
        ImageView imageToReturn = null;
        while(i<this.categoryIconTable.getChildCount() && !found){
            TableRow row = (TableRow) this.categoryIconTable.getChildAt(i);
            int j =0;
            while(j< row.getChildCount() && !found){
                ImageView image = (ImageView) row.getChildAt(j);
                int id = image.getId();
                String tag = image.getTag().toString();
                if( tag.equals(iconName)){
                    imageToReturn = image;
                    found = true;
                }//End of if 
                //Increase row iterator
                j++;
            }//End of inner while loop
            //Increase table iterator
            i++;
        }//End of outer while loop
        Log.d("findIconImageByName", "Enter findIconImageByName method in the EditCategoryActivity class.");
        return imageToReturn;
    }//End of findIconImageByName method

    //Method to define if the category passed in for edition is a preloaded one, that way category name field can be locked from editing
    protected boolean isCategoryPreLoaded(Category category){
        Log.d("isQuestionPreLoaded","Enter the isQuestionPreLoaded method in AddQuestionActivity class.");
        //Declare and instantiate variables to look for the questions
        Resources res = getResources();// Used to get preloaded sting data
        Cursor preLoadedCategories = this.accountsDB.getPreLoadedCategories(); // list of preloaded categories (stored with id in the DB and not the string value)
        //Booloean flag to be returned by method
        boolean isPreloaded = false;
        //While loop to iterate through the list of preloaded questions and check their text against parameter value
        while(!isPreloaded && preLoadedCategories.moveToNext()){
            if(category.getName().equals(preLoadedCategories.getString(1))){
                isPreloaded = true;
                Log.d("isQuestionPreLoaded","The question has been found as preloaded question in the AddQuestionActivity class.");
            }//End of if statement that checks the name is the same as current row in the cursor
        }//End of while loop to iterate through
        Log.d("isQuestionPreLoaded","Exit the isQuestionPreLoaded method in AddQuestionActivity class.");
        return isPreloaded;
    }//End of isQuestionPreLoaded

    private boolean checkForCategoryChanges(){
        Log.d("checkForCategoryChanges","Enter checkForCategoryChanges method in EditCategoryActivity class.");
        //Boolean flags to know what changed, the category name, the icon or both
        boolean catNameChanged = !this.category.getName().equals(etNewItemField.getText().toString());
        boolean iconChanged = !this.category.getIcon().getName().equals(this.categoryIcon.getName());
        boolean isIconAndCatNameValid = false;
        //Check what piece of data changed during the edition process
        if(iconChanged || catNameChanged){
            //If at least one changed, check if the category name changed, to check for validity
            if(catNameChanged){
                //Check the name is valid
                if(this.isDataValid(9,this.category.get_id())){
                    //If valid, set flag to true
                    isIconAndCatNameValid = true;
                }//End of is statement to check cat name is valid
            }else{
                //If name didn't change, no need for validation, set flag to true to continue with process
                isIconAndCatNameValid = true;
            }//End of if else statement to check if category name changed
        }
        Log.d("checkForCategoryChanges","Exit checkForCategoryChanges method in EditCategoryActivity class.");
        return isIconAndCatNameValid;
    } //End of checkForCategoryChanges method
}//End of EditCategoryActivity class
