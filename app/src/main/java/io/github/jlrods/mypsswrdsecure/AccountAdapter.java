package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.content.res.Resources;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;


class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{
    //Attribute definition
    private View.OnClickListener listener;
    private static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private Context context;
    private Cursor cursor;// List to hold all User or Password data coming from the database

    // Pass in the contact array into the constructor
    public AccountAdapter(Context context, Cursor cursor) {
        Log.d("AccountAdapt","Enter Full Constructor in AccountAdapter class.");
        this.context = context;
        this.cursor = cursor;
        //Extract all the user/password from the app resources
        try{
            Log.d("AccountAdapt","Exit successfully Full Constructor in AccountAdapter class.  ");
        }catch(Exception e){
            Log.e("Error","Exit Full Constructor in AccountAdapter class with error: "+ e.getMessage());
        }
    }//End of IconAdapter Constructor

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("AccountAdaptVHOnCre","Enter onCreateViewHolder method in the AccountAdapter class.");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.element_account,parent,false);
        view.setOnClickListener(listener);
        // Return a new holder instance
        AccountAdapter.ViewHolder viewHolder = new AccountAdapter.ViewHolder(view);
        Log.d("AccountAdaptVHOnCre","Exit onCreateViewHolder method in the AccountAdapter class.");
        return viewHolder;
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("AccountOnBindVH","Enter onBindViewHolder method in AccountAdapter class.");
        //AccountsDB accounts = new AccountsDB(getContext());
        //Move current cursor to position passed in as parameter
        this.cursor.moveToPosition(position);
        //Extract the data from cursor to create a new User or Password
        Account account = Account.extractAccount(this.cursor);
        if(account.getIcon().get_id() == R.mipmap.ic_my_psswrd_secure){
            holder.imgIcon.setImageResource(R.mipmap.ic_my_psswrd_secure);
        }else{
            //Extract all the logos from the app resources
            int idRes;
            Resources r = context.getResources();
            idRes = r.getIdentifier(account.getIcon().getName(),"drawable",context.getOpPackageName());
            holder.imgIcon.setImageResource(idRes);
        }//FIXME: Here there will be another possibility: The icon comes  from URI in phone
        holder.tvAccountName.setText(account.getName());
        Log.d("AccountOnBindVH","Exit onBindViewHolder method in AccountAdapter class.");
    }//End of onBindViewHolder method

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }// End of getItemCount method

    //Getter and setter methods
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

    //Inner class to inherit from RV ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ///Declare UI objects  from the element_string_value layout to represent them within the logic
        ImageView imgIcon;
        TextView tvAccountName;
        ImageView imgFavoriteStar;
        //Define the ViewHolder constructor
        public ViewHolder(View itemView) {
            //Call super constructor method
            super(itemView);
            Log.d("AccountVHCreator","Enter ViewHolder constructor in the AccountAdapter class.");
            //DInstantiate variables to represent logically the item layout components
            this.imgIcon = (ImageView) itemView.findViewById(R.id.imgAccElemLogo);
            this.tvAccountName = (TextView) itemView.findViewById(R.id.tvAccElmName);
            this.imgFavoriteStar = (ImageView) itemView.findViewById(R.id.imgFavoriteStar);
            Log.d("AccountVHCreator","Exit ViewHolder constructor in the AccountAdapter class.");
        }//End of ViewHolder
    }//End of ViewHolder inner class
}
