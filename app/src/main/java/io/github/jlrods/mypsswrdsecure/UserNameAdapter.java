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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.spec.IvParameterSpec;

public class UserNameAdapter extends RecyclerView.Adapter<UserNameAdapter.ViewHolder> {

    //Attribute definition
    protected Context context;
    protected Cursor cursor;// List to hold all User or Password data coming from the database
    protected View.OnClickListener listener; // Click listener to take actions when item is clicked on Rec Viewer
    protected Cryptographer cryptographer;
    protected static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    protected ThemeUpdater themeUpdater;
    // Pass in the contact array into the constructor
    public UserNameAdapter(Context context, Cursor cursor) {
        Log.d("UserNameAdapt","Enter Full Constructor in UserNameAdapter class.");
        this.context = context;
        this.cursor = cursor;
        this.cryptographer = MainActivity.getCryptographer();
        //Set up theme updater object to retrieve theme colors and date format
        this.themeUpdater = new ThemeUpdater(context);
        //Extract all the user/password from the app resources
        try{
            Log.d("UserNameAdapt","Exit successfully Full Constructor in UserNameAdapter class.  ");
        }catch(Exception e){
            Log.e("Error","Exit Full Constructor in UserNameAdapter class with error: "+ e.getMessage());
        }//End of try catch block
    }//End of IconAdapter Constructor

    @NonNull
    @Override
    public UserNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("UserNameAdaptVHOnCre","Enter onCreateViewHolder method in the UserNamedAdapter class.");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.element_string_value,parent,false);
        view.setOnClickListener(this.listener);
        // Return a new holder instance
        UserNameAdapter.ViewHolder viewHolder = new UserNameAdapter.ViewHolder(view);
        Log.d("UserNameAdaptVHOnCre","Exit onCreateViewHolder method in the UserNamedAdapter class.");
        return viewHolder;
    }//End of onCreateViewHolder method


    @Override
    public void onBindViewHolder(@NonNull UserNameAdapter.ViewHolder holder, int position) {
        Log.d("UsrNameOnBindVH","Enter onBindViewHolder method in UserNameAdapter class.");
        AccountsDB accountsDB = new AccountsDB(getContext());
        //Move current cursor to position passed in as parameter
        this.cursor.moveToPosition(position);
        //Extract the data from cursor to create a new User or Password
        UserName userName = UserName.extractUserName(this.cursor);
        //holder.tvStrength.setVisibility(View.GONE);
        holder.imgIcon.setImageResource(R.mipmap.ic_account_black_48dp);
        holder.tvStringValue.setText(cryptographer.decryptText(userName.getValue(),new IvParameterSpec(userName.getIv())));
        Date date = new Date(userName.getDateCreated());
        SimpleDateFormat format = new SimpleDateFormat(this.themeUpdater.getDateFormat());
        holder.tvDateCreated.setText(format.format(date));
        int timesUsed = accountsDB.getTimesUsedUserName(userName.get_id());
        holder.tvTimesUsed.setText(String.valueOf(timesUsed));
        Log.d("UsrNameOnBindVH","Exit onBindViewHolder method in UserNameAdapter class.");
    }//End of onBindViewHolder method

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }//End of getItemCount method

    public Context getContext() {
        return context;
    }//End of getContext method

    public void setContext(Context context) {
        this.context = context;
    }//End of setContext method

    public Cursor getCursor() {
        return cursor;
    }//End of getCursor method

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }//End of setCursor method

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnClickListener(View.OnClickListener listener) {
        Log.d("UserAdapterVHOnClick","Enter setOnClickListener in the UserNameAdapter class.");
        this.listener= listener;
    }//End of setOnItemClickListener

    //Inner class to inherit from RV ViewHolder class
    protected static class ViewHolder extends RecyclerView.ViewHolder{
        //Declare UI objects  from the element_string_value layout to represent them within the logic
        ImageView imgIcon;
        TextView tvStrength;
        TextView tvStringValue;
        TextView tvTimesUsed;
        TextView tvDateCreated;
        TextView tvDateCreatedTag;
        //Define the ViewHolder constructor
        ViewHolder(View itemView) {
            //Call super constructor method
            super(itemView);
            Log.d("UserNameVHCreator","Enter ViewHolder constructor in the UserNameAdapter class.");
            //DInstantiate variables to represent logically the item layout components
            this.tvStrength = (TextView) itemView.findViewById(R.id.tvPsswrdStength);
            this.imgIcon = (ImageView) itemView.findViewById(R.id.imgStringValue);
            this.tvStringValue = (TextView) itemView.findViewById(R.id.tvStringValue);
            this.tvTimesUsed = (TextView) itemView.findViewById(R.id.tvStrValTimesUsed);
            this.tvDateCreated = (TextView) itemView.findViewById(R.id.tvStrValDate);
            this.tvDateCreatedTag = (TextView) itemView.findViewById(R.id.tvStrValDateTag);
            Log.d("UserNameVHCreator","Exit ViewHolder constructor in the UserNameAdapter class.");
        }//End of ViewHolder
    }//End of ViewHolder inner class
}//End of UserNameAdapter