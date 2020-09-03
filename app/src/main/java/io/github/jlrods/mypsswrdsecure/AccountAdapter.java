package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{
    //Attribute definition
    private View.OnClickListener listener;
    //private static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private Context context;
    private Cursor cursor;// List to hold all User or Password data coming from the database
    private View.OnClickListener starImgOnClickListener;

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
        }//End of try catch block
    }//End of IconAdapter Constructor

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("AccountAdaptVHOnCre","Enter onCreateViewHolder method in the AccountAdapter class.");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View view = inflater.inflate(R.layout.element_account,parent,false);
        view.setOnClickListener(this.listener);
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
        if(account.getIcon().getLocation().startsWith(MainActivity.getExternalImageStorageClue())){
            //Set image if it comes from phone URI
            holder.imgIcon.setImageURI(Uri.parse(account.getIcon().getLocation()));
        }else if(account.getIcon().getLocation().equals(MainActivity.getRESOURCES())){
            //Call static method that will get the resource by passing in it's name and set it as the resource image of the ImageView passed in
            MainActivity.setAccountLogoImageFromRes(holder.imgIcon,context,account.getIcon().getName());
        }else{
            //Any other case, use the default app logo
            holder.imgIcon.setImageResource(R.mipmap.ic_my_psswrd_secure);
        }//End of if else statement
        if(account.isFavorite()){
            holder.imgFavoriteStar.setImageResource(android.R.drawable.btn_star_big_on);
        }else{
            holder.imgFavoriteStar.setImageResource(android.R.drawable.btn_star_big_off);
        }
        //Set the isFav star onclik event listener
        holder.imgFavoriteStar.setOnClickListener(this.starImgOnClickListener);
        //Set the account name
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

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnClickListener(View.OnClickListener listener) {
        Log.d("AccAdapterVHOnClick","Enter setOnClickListener in the AccountAdapter class.");
        this.listener= listener;
    }//End of setOnItemClickListener

    // Define the method that allows the parent activity or fragment to define the listener
    public void setStarImgOnClickListener(View.OnClickListener listener) {
        Log.d("AccAdapterVHOnClick","Enter setOnClickListener in the AccountAdapter class.");
        this.starImgOnClickListener= listener;
    }//End of setOnItemClickListener

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
}//End of AccountAdapter class
