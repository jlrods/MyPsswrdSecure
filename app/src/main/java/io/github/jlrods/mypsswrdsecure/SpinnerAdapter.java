package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * Created by rodjose1 on 05/09/2018.
 */

//Class to handle the adapter objects to link spinners UI and data
public class SpinnerAdapter extends CursorAdapter {
    //Adapter constructor method
    public SpinnerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        Log.d("Adapter_Constructor","Leaves SpinnerAdapter constructor.");
    }//End of constructor method

    //Method to create a new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.spinner_item_string_value,parent,false);
    }

    //Method to bind the view and the data via a cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("Ent_bindView","Enter bindView method to populate spinners in SpinnerAdapter class.");
        //Declare and instantiate a TextView object to hold the unit name and symbol
        TextView tvItem = (TextView) view.findViewById(R.id.tvItem);
        //Declare and instantiate a String to hold the name by extracting data from cursor (Column 1 will hold the name attribute)
        String stringName= cursor.getString(1);
        //Declare and instantiate an int to hold the string id from resources
        int textID = context.getResources().getIdentifier(stringName,"string",context.getOpPackageName());
        //If textID is 0, means it's not stored in the app resources
        if(textID > 0){
            tvItem.setText(context.getString(textID));
        }else{
            //In the case of not being a resource, print the text retrieved from DB
            tvItem.setText(stringName);
        }//End of if else statement

        Log.d("Ent_bindView","Exit bindView method to populate spinners in SpinnerAdapter class.");
    }//End of bindView method

    //Method to return int number when its name is passed as argumen
    public int findItemPosition(String itemName){
        Log.d("Ent_findspItem","Enter  findItemPosition to find sp item by name in SpinnerAdapter class.");
        boolean found = false;
        int position=-1;
        Cursor c = this.getCursor();
        do{Log.d("Ent_bindView","Enter  findItemPosition to find sp item by name in SpinnerAdapter class.");
            if(c.getString(1).toLowerCase().equals(itemName.toLowerCase())){
                found = true;
                position = c.getPosition();
                Log.d("Found_spItem","sp item with name " + itemName +" has been found in SpinnerAdapter class.");
            }
        }while(c.moveToNext() && !found);
        Log.d("Ent_findspItem","Exit  findItemPosition to find sp item by name in SpinnerAdapter class.");
        return position;
    }//End of findItemPosition method
}//End of SpinnerAdapter class