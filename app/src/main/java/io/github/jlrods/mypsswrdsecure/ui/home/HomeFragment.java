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
import io.github.jlrods.mypsswrdsecure.AccountAdapter;
import io.github.jlrods.mypsswrdsecure.AccountsDB;
import io.github.jlrods.mypsswrdsecure.DecryptDataService;
import io.github.jlrods.mypsswrdsecure.MainActivity;
import io.github.jlrods.mypsswrdsecure.R;

public class HomeFragment extends Fragment {

    //Attribute declaration
    private static AccountsDB accountsDB; // Object to manage all DB actions
    private static RecyclerView rv = null; // RV object to populate data on the Fragment

    //Method declaration
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("HomeFragOnCreate","Enter onCreate method in HomeFragment class.");
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
                //Launch decrypt service
                Intent decryptDataService = new Intent(v.getContext(), DecryptDataService.class);
                v.getContext().startService(decryptDataService);
                //Call method to get intent setup to throw EditAccountActivity
                Intent i = MainActivity.prepareThrowEditAccountActivity(getContext(),v);
                //Start the AddItemActivity class
                startActivityForResult(i,MainActivity.getThrowEditAccountActReqcode());
                Log.d("ThrowEditAcc","Exit throwEditAccountActivity method in the MainActivity class.");
            }//End of onClick method
        });//End of setOnClickListener
        accountAdapter.setStarImgOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.toggleIsFavorite(v);
            }
        });
        //Check if current tab retrieved form DB on MainActivity has been changed
        if(MainActivity.getCurrentTabID() != 0){
            MainActivity.getTabLayout().selectTab(MainActivity.getTabLayout().getTabAt(MainActivity.getCurrentTabID()));
        }else {
            MainActivity.updateRecyclerViewData(accountAdapter,-1, MainActivity.NotifyChangeType.DATA_SET_CHANGED);
        }//End of if else statement that checks the tab to be displayed
    }//End of onActivityCreated method

    //Getters and setters method
    public static RecyclerView getRv() {
        return rv;
    }//End of getRv method

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }//End of setRv method

    public static AccountsDB getAccountsDB() {
        return accountsDB;
    }//End of getAccountsDB method

    public void setAccounts(AccountsDB accounts) {
        this.accountsDB = accounts;
    }//End of setAccounts method

    //Method to get the list of user names from the DB
    public Cursor getUserNameList(){
        Log.d("HomeFragOnCreate","Call getUserNameList method in HomeFragment class.");
        return  this.accountsDB.getUserNameList();
    }//End of getUserNameList method

    //Method to get the list of accounts from the DB
    public Cursor getAccountsList(){
        Log.d("HomeFragOnCreate","Call getAccountsList method in HomeFragment class.");
        return  this.accountsDB.getAccountsList();
    }//End of getAccountsList method

    //Method to get the list of passwords from the DB
    public Cursor getPsswrdList(){
        Log.d("HomeFragOnCreate","Call getPsswrdList method in HomeFragment class.");
        return  this.accountsDB.getPsswrdList();
    }//End of getPsswrdList method

    //Method to get the number of times a specific user name is being used in different accounts as per the DB
    public int getTimesUsedUserName(int userNameID){
        Log.d("HomeFragOnCreate","Call getPsswrdList method in HomeFragment class.");
        return this.accountsDB.getTimesUsedUserName(userNameID);
    }//End of getTimesUsedUserName method

    //Method to get the number of times a specific password is being used in different accounts as per the DB
    public int getTimesPsswrd(int psswrd){
        Log.d("HomeFragOnCreate","Call getPsswrdList method in HomeFragment class.");
        return this.accountsDB.getTimesUsedUserName(psswrd);
    }//End of getTimesPsswrd method

    //Method to receive and handle data coming from other activities such as: SelectLogoActivity,
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult","Enter the onActivityResult method in the HomeFragment class.");
        if (requestCode == MainActivity.getThrowEditAccountActReqcode() && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult","Received GOOD result from EditAccountActivity (received by HomeFragment).");
            //Declare and initialize variable to hold the type of change to be notified to RV update
            MainActivity.NotifyChangeType changeType = null;
            //Call MainActivity's method to get the change type based on the data sent back from EditAccountActivity
            changeType = MainActivity.handleEditAccountActivityResult(data);
            //Call MainActivity's method to get the toast text to be displayed based on the type of RV notification type
            String toastText = MainActivity.setToastText(data,"accountName",changeType,getResources());
            //Get current adapter used by RV
            AccountAdapter adapter = (AccountAdapter) rv.getAdapter();
            //Call MainActivity's method to update RV data
            MainActivity.updateRecyclerViewData(adapter,data.getExtras().getInt("position"),changeType);
            //Call MainActivity's method to display the corresponding toast text
            MainActivity.displayToast(getContext(), toastText, Toast.LENGTH_LONG, Gravity.CENTER);
        }else if(requestCode == MainActivity.getThrowEditAccountActReqcode() && resultCode == Activity.RESULT_CANCELED){
            Log.d("onActivityResult","Received BAD result from EditAccountActivity (received by HomeFragment).");
        }//End of if else chain to check request code and result
        Log.d("onActivityResult","Exit the onActivityResult method in the DisplayAccountActivity class.");
    }//End of onActivityResult method
}//End of HomeFragment class