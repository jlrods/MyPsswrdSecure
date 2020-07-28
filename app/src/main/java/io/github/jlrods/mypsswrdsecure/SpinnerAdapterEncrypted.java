package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class SpinnerAdapterEncrypted extends SpinnerAdapter {
    public SpinnerAdapterEncrypted(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    //Method to bind the view and the data via a cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("Ent_bindView","Enter bindView method to populate spinners in SpinnerAdapterEncrypted class.");
        //Declare and instantiate a TextView object to hold the unit name and symbol
        TextView tvItem = (TextView) view.findViewById(R.id.tvItem);
        //Declare and instantiate a String to hold the name by extracting data from cursor (Column 1 will hold the name attribute)
        byte[] encryptedValue= cursor.getBlob(1);
        //In the case of not being a resource, print the text retrieved from DB
        byte[] iv = cursor.getBlob(2);
        tvItem.setText(cryptographer.decryptText(encryptedValue,new IvParameterSpec(iv)));
        Log.d("Ent_bindView","Exit bindView method to populate spinners in SpinnerAdapterEncrypted class.");
    }//End of bindBiew method
}//End of SpinnerAdapterEncrypted class
