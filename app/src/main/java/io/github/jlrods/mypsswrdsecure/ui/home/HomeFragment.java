package io.github.jlrods.mypsswrdsecure.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.jlrods.mypsswrdsecure.Account;
import io.github.jlrods.mypsswrdsecure.AccountAdapter;
import io.github.jlrods.mypsswrdsecure.AccountsDB;
import io.github.jlrods.mypsswrdsecure.EditAccountActivity;
import io.github.jlrods.mypsswrdsecure.MainActivity;
import io.github.jlrods.mypsswrdsecure.PsswrdAdapter;
import io.github.jlrods.mypsswrdsecure.R;
import io.github.jlrods.mypsswrdsecure.UserNameAdapter;

public class HomeFragment extends Fragment {

    //Attribute declaration
    //private HomeViewModel homeViewModel;
    private static AccountsDB accountsDB; // Object to manage all DB actions
    private static RecyclerView rv = null; // RV object to populate data on the Fragment

    //Method declaration
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFragOnCreate","Enter onCreate method in HomeFragment class.");
        //Pre-conditions to bear in mind when creating the Home view:
        //The current category (Even the one saved on the DB)
        //The current tab that has been selected
        //Possible the current date format if included in app preferences

        //Other points to define:
        //What class is going to handle the DB manager class? this one or MainActivity?
        //Few classes were made public so this fragment could assigned
        //

        //homeViewModel =
        //ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        rv = root.findViewById(R.id.layout_rec_view_main);
        this.rv.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL);
        this.rv.addItemDecoration(itemDecoration);
        this.accountsDB = MainActivity.getAccountsDB();

        Log.d("HomeFragOnCreate","Exit onCreate method in HomeFragment class.");
        return root;
    }//End of onCreateView method

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        AccountAdapter accountAdapter = new AccountAdapter(getActivity().getBaseContext(),null);
        accountAdapter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("ThrowEditAcc","Enter throwEditAccountActivity method in the MainActivity class.");
                //rv
                //RecyclerView rv = HomeFragment.getRv();
                //Get the item position in the adapter
                int itemPosition = rv.getChildAdapterPosition(v);
                //move the cursor to the task position in the adapter
                Cursor cursor = ((AccountAdapter)rv.getAdapter()).getCursor();
                cursor.moveToPosition(itemPosition);
                //Extract the task object from the cursor row
                Account account = Account.extractAccount(cursor);
                //Declare and instantiate a new intent object
                Intent i= new Intent(getContext(), EditAccountActivity.class );
                //Add extras to the intent object, specifically the current category where the add button was pressed from
                //i.putExtra("category",this.currentCategory.toString());
                i.putExtra("_id",account.get_id());
                //Start the AddItemActivity class
                startActivityForResult(i,MainActivity.getThrowEditAccountActReqCode());
                Log.d("ThrowEditAcc","Exit throwEditAccountActivity method in the MainActivity class.");
            }//End of onClick method
        });//End of setOnClickListener
        accountAdapter.setStarImgOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.toggleIsFavorite(v);
            }
        });
        MainActivity.updateRecyclerViewData(accountAdapter);
    }//End of onActivityCreated method

    //Getters and setters method
    public static RecyclerView getRv() {
        return rv;
    }

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }

    public static AccountsDB getAccountsDB() {
        return accountsDB;
    }

    public void setAccounts(AccountsDB accounts) {
        this.accountsDB = accounts;
    }

    //Other methods
    public void displayAcounts(){

    }

    public void displayUserNames(){
        Cursor cursor = this.getUserNameList();
        //Move to first row of cursor if not empty
        cursor.moveToFirst();
        UserNameAdapter userNameAdapter = new UserNameAdapter(getActivity().getBaseContext(),cursor);
        this.rv.setAdapter(userNameAdapter);
        userNameAdapter.notifyDataSetChanged();
    }

    public void displayPsswrds(){
        this.accountsDB = new AccountsDB(getActivity().getBaseContext());
        Cursor cursor = this.getAccountsList();
        //Move to first row of cursor if not empty
        cursor.moveToFirst();
        PsswrdAdapter psswrdAdapter = new PsswrdAdapter(getActivity().getBaseContext(),cursor);
        this.rv.setAdapter(psswrdAdapter);
        psswrdAdapter.notifyDataSetChanged();
    }


    //Method to get the list of user names from the DB
    public Cursor getUserNameList(){
        Log.d("HomeFragOnCreate","Call getUserNameList method in HomeFragment class.");
        return  this.accountsDB.getUserNameList();
    }
    //Method to get the list of accounts from the DB
    public Cursor getAccountsList(){
        Log.d("HomeFragOnCreate","Call getAccountsList method in HomeFragment class.");
        return  this.accountsDB.getAccountsList();
    }
    //Method to get the list of passwords from the DB
    public Cursor getPsswrdList(){
        Log.d("HomeFragOnCreate","Call getPsswrdList method in HomeFragment class.");
        return  this.accountsDB.getPsswrdList();
    }
    //Method to get the number of times a specific user name is being used in different accounts as per the DB
    public int getTimesUsedUserName(int userNameID){
        Log.d("HomeFragOnCreate","Call getPsswrdList method in HomeFragment class.");
        return this.accountsDB.getTimesUsedUserName(userNameID);
    }
    //Method to get the number of times a specific password is being used in different accounts as per the DB
    public int getTimesPsswrd(int psswrd){
        Log.d("HomeFragOnCreate","Call getPsswrdList method in HomeFragment class.");
        return this.accountsDB.getTimesUsedUserName(psswrd);
    }

    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","Enter the onActivityResult method in the HomeFragment class.");
        //Check if result comes from AddAccountActivity
        String toastText = "";
        //Flag to display Toast and update RV
        boolean goodResultDelivered = false;
        if (requestCode == MainActivity.getThrowEditAccountActReqCode() && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from EditAccountActivity (received by HomeFragment).");
            //((AccountAdapter) this.rv.getAdapter()).setCursor(accountsDB.getAccountsList());
            //Define text to display Toast to confirm the account has been added
            //Set variable to display Toast
            goodResultDelivered = true;
            toastText = data.getExtras().getString("accountName") + " " + getResources().getString(R.string.accountUpdated);
        }else if(requestCode == MainActivity.getThrowEditAccountActReqCode() && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditAccountActivity (received by HomeFragment).");
        }
        //Check if toast would be displayed
        if(goodResultDelivered){
            MainActivity.updateRecyclerViewData(this.rv.getAdapter());
            //Move to new account position
            //Display Toast to confirm the account has been added
            MainActivity.displayToast(getContext(),toastText, Toast.LENGTH_LONG, Gravity.CENTER);
        }//End of if statement to check good result was delivered
        //End of if else statement to check the data comes from one of the thrown activities
        Log.d("onActivityResult","Exit the onActivityResult method in the DisplayAccountActivity class.");
    }//End of onActivityResult method
}//End of HomeFragment class
