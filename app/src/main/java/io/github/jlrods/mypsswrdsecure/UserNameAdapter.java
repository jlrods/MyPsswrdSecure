package io.github.jlrods.mypsswrdsecure;

import androidx.recyclerview.widget.RecyclerView;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.jlrods.mypsswrdsecure.ui.home.HomeFragment;

public class UserNameAdapter extends RecyclerView.Adapter<UserNameAdapter.ViewHolder>  {

    //Attribute definition
    protected Context context;
    protected Cursor cursor;// List to hold all User or Password data coming from the database
    protected View.OnClickListener listener; // Click listener to take actions when item is clicked on Rec Viewer
    protected static SparseBooleanArray itemStateArray= new SparseBooleanArray();

    // Pass in the contact array into the constructor
    public UserNameAdapter(Context context, Cursor cursor) {
        Log.d("UserNameAdapt","Enter Full Constructor in UserNameAdapter class.");
        this.context = context;
        this.cursor = cursor;
        //Extract all the user/password from the app resources
        try{
            Log.d("UserNameAdapt","Exit successfully Full Constructor in UserNameAdapter class.  ");
        }catch(Exception e){
            Log.e("Error","Exit Full Constructor in UserNameAdapter class with error: "+ e.getMessage());
        }
    }//End of IconAdapter Constructor

    @NonNull
    @Override
    public UserNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("UserNameAdaptVHOnCre","Enter onCreateViewHolder method in the UserNamedAdapter class.");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.element_string_value,parent,false);
        view.setOnClickListener(listener);
        // Return a new holder instance
        UserNameAdapter.ViewHolder viewHolder = new UserNameAdapter.ViewHolder(view);
        Log.d("UserNameAdaptVHOnCre","Exit onCreateViewHolder method in the UserNamedAdapter class.");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserNameAdapter.ViewHolder holder, int position) {
        Log.d("UsrNameOnBindVH","Enter onBindViewHolder method in UserNameAdapter class.");
        AccountsDB accounts = new AccountsDB(getContext());
        //Move current cursor to position passed in as parameter
        this.cursor.moveToPosition(position);
        //Extract the data from cursor to create a new User or Password
        UserName userName = UserName.extractUserName(this.cursor);
        //holder.tvStrength.setVisibility(View.GONE);
        holder.imgIcon.setImageResource(R.mipmap.ic_account_black_48dp);
        holder.tvStringValue.setText(userName.getValue());
        Date date = new Date(userName.getDateCreated());
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        holder.tvDateCreated.setText(format.format(date));
        int timesUsed = accounts.getTimesUsedUserName(userName.get_id());
        holder.tvTimesUsed.setText(String.valueOf(timesUsed));
        Log.d("UsrNameOnBindVH","Exit onBindViewHolder method in UserNameAdapter class.");
    }//End of onBindViewHolder method

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    public Context getContext() {

        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Cursor getCursor() {

        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(View.OnClickListener listener) {
        Log.d("UserAdapterVHOnClick","Enter setOnItemClickListener in the UserNameAdapter class.");
        this.listener = listener;
    }

    //Inner class to inherit from RV ViewHolder class
    protected static class ViewHolder extends RecyclerView.ViewHolder{
        //Declare UI objects  from the task_element layout to represent them within the logic
        ImageView imgIcon;
        TextView tvStrength;
        TextView tvStringValue;
        TextView tvTimesUsed;
        TextView tvDateCreated;
        //Define the ViewHolder constructor
        ViewHolder(View itemView) {
            //Call super constructor method
            super(itemView);
            //itemView.setOnClickListener(this);
            Log.d("UserNameVHCreator","Enter ViewHolder constructor in the UserNameAdapter class.");
            //DInstantiate variables to represent logically the item layout components
            this.tvStrength = (TextView) itemView.findViewById(R.id.tvPsswrdStength);
            this.imgIcon = (ImageView) itemView.findViewById(R.id.imgStringValue);
            this.tvStringValue = (TextView) itemView.findViewById(R.id.tvStringValue);
            this.tvTimesUsed = (TextView) itemView.findViewById(R.id.tvStrValTimesUsed);
            this.tvDateCreated = (TextView) itemView.findViewById(R.id.tvStrValDate);
            Log.d("UserNameVHCreator","Exit ViewHolder constructor in the UserNameAdapter class.");
        }//End of ViewHolder
    }//End of ViewHolder inner class
}//End of UserNameAdapter
