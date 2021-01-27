package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class PsswrdAdapter extends UserNameAdapter {
    public PsswrdAdapter(Context context, Cursor cursor) {
        super(context, cursor);
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
        if(psswrd.getStrength().equals(PsswrdStrength.VERY_WEEK) || psswrd.getStrength().equals(PsswrdStrength.WEAK)){
            holder.tvStrength.setTextColor(Color.RED);
        }else if(psswrd.getStrength().equals(PsswrdStrength.MEDIUM)){
            holder.tvStrength.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        }else if((psswrd.getStrength().equals(PsswrdStrength.STRONG))){
            holder.tvStrength.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        }else{
            holder.tvStrength.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
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
}
