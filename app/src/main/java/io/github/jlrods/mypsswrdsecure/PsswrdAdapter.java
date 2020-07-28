package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class PsswrdAdapter extends UserNameAdapter {
    public PsswrdAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(@NonNull UserNameAdapter.ViewHolder holder, int position) {
        Log.d("PsswrdOnBindVH","Enter onBindViewHolder method in PsswrdAdapter class.");
        AccountsDB accountsDB = new AccountsDB(getContext());
        //Move current cursor to position passed in as parameter
        cursor.moveToPosition(position);
        //super.onBindViewHolder(holder,position);
        //Extract the data from cursor to create a new User or Password
        Psswrd psswrd = (Psswrd) Psswrd.extractPsswrd(cursor);
        holder.tvStrength.setVisibility(View.VISIBLE);
        holder.tvStrength.setText(psswrd.getStrength().toString());
        holder.imgIcon.setImageResource(R.mipmap.ic_pencil_lock_black_48dp);
        holder.tvStringValue.setText(cryptographer.decryptText(psswrd.getValue(),new IvParameterSpec(psswrd.getIv())));
        Date date = new Date(psswrd.getDateCreated());
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        holder.tvDateCreated.setText(format.format(date));
        int timesUsed = accountsDB.getTimesUsedPsswrd(psswrd.get_id());
        holder.tvTimesUsed.setText(String.valueOf(timesUsed));
        Log.d("PsswrdOnBindVH","Exit onBindViewHolder method in PsswrdAdapter class.");
    }//End of onBindViewHolder method
}
