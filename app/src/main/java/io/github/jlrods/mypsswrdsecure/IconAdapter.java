package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.ArrayList;

class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private ArrayList<Icon> iconList;
    private View.OnClickListener listener;
    //int selectedPosition=  RecyclerView.NO_POSITION;
    protected LayoutInflater inf;
    protected static SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private Context context;

    // Pass in the contact array into the constructor
    public IconAdapter(Context context) {
        Log.d("IconAdapt","Enter IconAdapter Full Constructor");
        this.context = context;
        iconList = new ArrayList<Icon>();// List to hold all Icon objects created from logos in app sources
        //Extract all the logos from the app resources
        Field[] images = io.github.jlrods.mypsswrdsecure.R.drawable.class.getFields();
        try{
            //Iterate through all the drawable files
            for(Field image: images){
                //Check the drawable file is a logo
                if(image.getName().startsWith("logo_")){
                    //Add to the list a new Icon object, passing down the R number as _id, name as value and R number as location
                    iconList.add(new Icon(image.getInt(null),image.getName(),String.valueOf(image.getInt(null)),false));
                }
            }//End of for each loop
            Log.d("IconAdapt","Exit successfully IconAdapter Full Constructor in Icon Adapter class.  ");
        }catch(Exception e){
            Log.e("Error","Exit IconAdapter Full Constructor in Icon Adapter class with error: "+ e.getMessage());
        }
    }//End of IconAdapter Constructor

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("OnCreatVH","Enter onCreateViewHolder method in IconAdapter class.");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View iconView = inflater.inflate(R.layout.element_logo,parent,false);
        iconView.setOnClickListener(listener);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(iconView);
        Log.d("OnCreatVH","Exit onCreateViewHolder method in IconAdapter class.");
        return viewHolder;
    }//End of onCreateViewHolder method

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int  position) {
        Log.d("OnBindVH","Enter onBindViewHolder method in IconAdapter class.");
        // Get the data model based on position
        Icon logo = iconList.get(position);
        // Set item views based on your views and data model
        ImageView tvLogo = holder.icon;
        //Set image resource file by passing in the resource number, which was stored in the _id attribute
        tvLogo.setImageResource(logo._id);
        //Check if the logo isSelected attribute is true
        if(logo.isSelected){
            //If that is the case set background color to itemSelected colour from Color values file
            tvLogo.setBackgroundColor(ContextCompat.getColor(this.context, R.color.itemSelected));
        }else{
            //Otherwise, set background to white background
            tvLogo.setBackgroundColor(ContextCompat.getColor(this.context, R.color.whiteBackground));
        }
        //Put the int boolean pair in the SparseBooleanArray that stores the item selected in the RecyclerViewer object
        this.itemStateArray.put(position,logo.isSelected());
        Log.d("OnBindVH","Exit onBindViewHolder method in IconAdapter class.");
    }//End of onBindViewHolder method

    @Override
    public int getItemCount() {
        Log.d("getItemCount","Enter getItemCount method in IconAdapter class and return icon list size.");
        return this.iconList.size();
    }//End of getItemCount method


    public ArrayList<Icon> getIconList() {
        return iconList;
    }

    public void setIconList(ArrayList<Icon> iconList) {
        this.iconList = iconList;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(View.OnClickListener listener) {
        Log.d("IconVHOnClick","Enter IconAdapter setOnItemClickListerner in the IconAdapter class.");
        this.listener = listener;
    }
    //Method to update the key value list that keeps record of current state of each icon selection
    public void updateItemIsSelected(int position,boolean isSelected){
        Log.d("updateItemIsSel","Enter updateItemIsSelected method in the IconAdapter class.");
        //Iterate through the SparseBooleanArray and mark the item in "position" to selected. Otherwise set as not selected
        for (int i=0;i<itemStateArray.size();i++) {
            if(i == position){
                itemStateArray.put(position,isSelected);
            }else{
                itemStateArray.put(i,false);
            } //End of if else statement to check item position
        }// End of for loop
        Log.d("updateItemIsSel","Exit updateItemIsSelected method in the IconAdapter class.");
    }//End of updateItemIsSelected method

    //Inner class to inherit from RV ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder{
        //Declare UI objects  from the task_element layout to represent them within the logic
        public ImageView icon;
        //Define the ViewHolder constructor
        public ViewHolder(View itemView) {
            //Call super constructor method
            super(itemView);
            //itemView.setOnClickListener(this);
            Log.d("IconVHCreator","Enter ViewHolder constructor in the IconAdapter class.");
            //DInstantiate variables to represent logically the item layout components
            icon = (ImageView) itemView.findViewById(R.id.imgLogo);
            Log.d("IconVHCreator","Exit ViewHolder constructor in the IconAdapter class.");
        }//End of ViewHolder
    }//End of ViewHolder inner class
}//End of IconAdapter class