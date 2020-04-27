package io.github.jlrods.mypsswrdsecure;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

// Activity class to manage logo selection display
public class SelectLogActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SelLogOnCreate","Enter onCreate method in SelectLogoActivity class.");
        setContentView(R.layout.activity_select_logo);
        final RecyclerView rvLogos = (RecyclerView) findViewById(R.id.layout_rec_view_logo);
        final IconAdapter iconAdapter = new IconAdapter(this);
        //Set the logo OnClickListener object so the Logo is selected when clicked on it
        iconAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IconSetOnClickList","Enter onClick method of setOnItemClickListener in SelectLogoActivity class.");
                //Retrieve the position of current item in the RecyclerView
                //This position is in line with the SparseBoolean array to hold the selected state of each item
                int adapterPosition = rvLogos.getChildAdapterPosition(v);
                // Check if Icon object in the retrieved position has attribute isSelected set to
                // true or false to revert to toggle the selection state
                if(iconAdapter.getIconList().get(adapterPosition).isSelected){
                    //If that's the case, set it to false
                    iconAdapter.getIconList().get(adapterPosition).setSelected(false);
                }else{
                    iconAdapter.getIconList().get(adapterPosition).setSelected(true);
                    for(int i=0;i<iconAdapter.getIconList().size();i++){
                        if(i!=adapterPosition){
                            iconAdapter.getIconList().get(i).setSelected(false);
                        }//End if statement
                    }// End of for loop
                };// End of if else statement to check if logo is selected

                iconAdapter.updateItemIsSelected(adapterPosition,true);
                //iconAdapter.getIconList().get(adapterPosition).setSelected(true);
                iconAdapter.notifyDataSetChanged();
                Log.d("IconSetOnClickList","Exit onClick method of setOnItemClickListener in SelectLogoActivity class.");
            }//End of OnClick method
        });
        rvLogos.setAdapter(iconAdapter);
        //rvLogos.setLayoutManager(new LinearLayoutManager(this));
        rvLogos.setLayoutManager(new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL));
        RecyclerView.ItemDecoration itemDecoration =  new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvLogos.addItemDecoration(itemDecoration);
        //rvLogos.setItemAnimator(new FadeInAnimator());
        Log.d("SelLogOnCreate","Exit  onCreate method in SelectLogoActivity class.");
    }//End of onCreate

    //Method to inflate the menu into the addTaskActivity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_logo, menu);
        return true;
    }// Find fe OnCreateOptionsMenu
}//End of SelectLogoActivity class
