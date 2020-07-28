package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.spec.IvParameterSpec;

public class SecurityQuestionAdapter extends UserNameAdapter {

    public SecurityQuestionAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }//End of default constructor


    @Override
    public void onBindViewHolder(@NonNull SecurityQuestionAdapter.ViewHolder holder, int position) {
        Log.d("SecQuestOnBindVH","Enter onBindViewHolder method in SecurityQuestionAdapter class.");
        AccountsDB accountsDB = new AccountsDB(getContext());
        //Move current cursor to position passed in as parameter
        cursor.moveToPosition(position);
        //super.onBindViewHolder(holder,position);
        //Extract the data from cursor to create a new User or Password
        Question question = (Question) Question.extractQuestion(cursor);
        //holder.tvStrength.setVisibility(View.VISIBLE);
        //holder.tvStrength.setText(psswrd.getStrength().toString());
        holder.imgIcon.setImageResource(R.mipmap.ic_shield_lock_black_48dp);
        holder.tvStringValue.setText(question.getValue());


        //Declare and instantiate an int to hold the string id from resources
        int questionID = context.getResources().getIdentifier(question.getValue(),"string",context.getPackageName());
        //If textID is 0, means it's not stored in the app resources
        if(questionID > 0){
            //Set text from resources by passing in its id
            holder.tvStringValue.setText(context.getString(questionID));
        }else{
            //In the case of not being a resource, print the text retrieved from DB
            holder.tvStringValue.setText(question.getValue());
        }//End of if else statement
        holder.tvDateCreated.setVisibility(View.GONE);
        holder.tvDateCreatedTag.setVisibility(View.GONE);
        //SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        //holder.tvDateCreated.setText(format.format(date));
        int timesUsed = accountsDB.getTimesUsedQuestion(question.get_id());
        holder.tvTimesUsed.setText(String.valueOf(timesUsed));
        Log.d("SecQuestOnBindVH","Exit onBindViewHolder method in SecurityQuestionAdapter class.");
    }//End of onBindViewHolder method


}//End of  SecurityQuestionAdapter class
