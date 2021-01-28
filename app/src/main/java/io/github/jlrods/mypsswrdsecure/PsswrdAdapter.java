package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class PsswrdAdapter extends UserNameAdapter {
    ThemeHandler themeHandler;
    public PsswrdAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        if(context instanceof ThemeHandler){
            this.themeHandler = (ThemeHandler) context;
        }
    }

    @Override
    public UserNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d("PsswrdAdaptVHOnCre","Enter onCreateViewHolder method in the PsswrdAdapter class.");
        // Return a new holder instance
        PsswrdAdapter.ViewHolder viewHolder = super.onCreateViewHolder(parent,viewType);
        Log.d("PsswrdAdaptVHOnCre","Exit onCreateViewHolder method in the PsswrdAdapter class.");
        return viewHolder;
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(@NonNull UserNameAdapter.ViewHolder holder, int position) {
        Log.d("PsswrdOnBindVH","Enter onBindViewHolder method in PsswrdAdapter class.");
        AccountsDB accountsDB = new AccountsDB(getContext());
        //Move current cursor to position passed in as parameter
        cursor.moveToPosition(position);
        //Extract the data from cursor to create a new User or Password
        Psswrd psswrd = (Psswrd) Psswrd.extractPsswrd(cursor);
        //Setup the text view for password strength
        holder.tvStrength.setVisibility(View.VISIBLE);
        //Change colour based on password strength
        holder.tvStrength.setText(psswrd.getStrength().toString());
//        TypedValue typedValue = new TypedValue();
//        Resources.Theme theme = context.getTheme();
//        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int colorAccent = this.themeHandler.fetchThemeColor("colorAccent");
//        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int colorPrimary =  this.themeHandler.fetchThemeColor("colorPrimary");
//        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int colorPrimaryDark =  this.themeHandler.fetchThemeColor("colorPrimaryDark");

        if(psswrd.getStrength().equals(PsswrdStrength.VERY_WEEK) || psswrd.getStrength().equals(PsswrdStrength.WEAK)){
            holder.tvStrength.setTextColor(Color.RED);
        }else if(psswrd.getStrength().equals(PsswrdStrength.MEDIUM)){
            //holder.tvStrength.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
            holder.tvStrength.setTextColor(colorPrimaryDark);
        }else if((psswrd.getStrength().equals(PsswrdStrength.STRONG))){
            //holder.tvStrength.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            holder.tvStrength.setTextColor(colorPrimary);
        }else{
            //holder.tvStrength.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            holder.tvStrength.setTextColor(colorAccent);
        }//End of if else chain to change colour tv password strength
        //Set image for password icon
        holder.imgIcon.setImageResource(R.mipmap.ic_pencil_lock_black_48dp);
        //Decrypt the password and displayed on text view
        holder.tvStringValue.setText(cryptographer.decryptText(psswrd.getValue(),new IvParameterSpec(psswrd.getIv())));
        Date date = new Date(psswrd.getDateCreated());
        SimpleDateFormat format = new SimpleDateFormat(MainActivity.getDateFormat());
        holder.tvDateCreated.setText(format.format(date));
        //Calculate the number of times the password has been used and display it
        int timesUsed = accountsDB.getTimesUsedPsswrd(psswrd.get_id());
        holder.tvTimesUsed.setText(String.valueOf(timesUsed));
        Log.d("PsswrdOnBindVH","Exit onBindViewHolder method in PsswrdAdapter class.");
    }//End of onBindViewHolder method

    private int fetchColor(String colorName) {
//        TypedValue typedValue = new TypedValue();
        int attributeColor = 0;
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
        }
//        TypedArray a = this.context.obtainStyledAttributes(typedValue.data, new int[] {attributeColor });
//        int color = a.getColor(0, 0);
//        a.recycle();
//        return color;
        //attributeColor = context.getResources().getIdentifier(colorName, "attr", context.getPackageName());
        TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (attributeColor, value, true);
        return value.data;
    }
}
