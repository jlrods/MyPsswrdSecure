package io.github.jlrods.mypsswrdsecure.login;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import javax.crypto.spec.IvParameterSpec;

import io.github.jlrods.mypsswrdsecure.AccountsDB;
import io.github.jlrods.mypsswrdsecure.AppLoggin;
import io.github.jlrods.mypsswrdsecure.Cryptographer;
import io.github.jlrods.mypsswrdsecure.Psswrd;
import io.github.jlrods.mypsswrdsecure.R;
import io.github.jlrods.mypsswrdsecure.MainActivity;
import io.github.jlrods.mypsswrdsecure.UserName;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private static Cryptographer cryptographer;
    private static AccountsDB accountsDB;
    private static boolean displaySignUp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Ent_onCreateLogin","Enter onCreate method in LoginActivity class.");
        //Get default current app theme from preferences
        int appThemeSelected = MainActivity.setAppTheme(this);
        //Set the theme by passing theme id number coming from preferences
        setTheme(appThemeSelected);
        //Call method to setup language based on app preferences
        MainActivity.setAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText etUsernameId = findViewById(R.id.etUsernameId);
        final EditText etPassword = findViewById(R.id.etPassword);
        final Button btnLoginButton = findViewById(R.id.btnLogin);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        final EditText etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        final EditText etUserName = findViewById(R.id.etUserName);
        final EditText etUserMessage = findViewById(R.id.etUserMessage);
        final TextView tvSignUp = findViewById(R.id.tvSignUp);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                btnLoginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    etUsernameId.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    etPassword.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(etUsernameId.getText().toString(),
                        etPassword.getText().toString());
            }
        };
        etUsernameId.addTextChangedListener(afterTextChangedListener);
        etPassword.addTextChangedListener(afterTextChangedListener);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(etUsernameId.getText().toString(),
                            etPassword.getText().toString());
                }
                return false;
            }
        });

        btnLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Define boolean flag to determine login success or failure
                boolean loginSuccess = false;
                AppLoggin appLoggin = new AppLoggin();;
                String errorMessageText = "";
                //Check if an applogin is already recorded on the DB
                if(displaySignUp){
                    Cursor inputUserNameCursor;
                    UserName newUserName;
                    Cursor inputPsswrdCursor;
                    Psswrd newPsswrd;
                    AppLoggin newAppLoggin = new AppLoggin();
                    //MainActivity.displayToast(v.getContext(),"This is weird, should exist a user at least!",Toast.LENGTH_LONG, Gravity.CENTER);
                    //If no data registered on DB or Signup request sent by user, Check the usernameID field if valid,
                    //Check the password isn't empty and the passwordConfirm field matches the password field
                    if(loginViewModel.isUserNameValid(etUsernameId.getText().toString()) && loginViewModel.isPasswordValid(etPassword.getText().toString())){
                        if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())){
                            //First check the username typed in by user  does not exist in DB, otherwise show proper error message
                            inputUserNameCursor = accountsDB.getUserNameByName(etUsernameId.getText().toString());
                            if(!inputUserNameCursor.moveToFirst()){

                                //Add user name to UserName table
                                newUserName = new UserName(cryptographer.encryptText(etUserName.getText().toString()),cryptographer.getIv().getIV());
                                newUserName.set_id(accountsDB.addItem(newUserName));
                                //Check password doesn't exist in DB, otherwise keep PsswrdID
                                inputPsswrdCursor = accountsDB.getPsswrdByName(etPassword.getText().toString());
                                if(!inputPsswrdCursor.moveToFirst()){
                                    newPsswrd = new Psswrd(cryptographer.encryptText(etPassword.getText().toString()),cryptographer.getIv().getIV());
                                    newPsswrd.set_id(accountsDB.addItem(newPsswrd));
                                }else{
                                    newPsswrd = Psswrd.extractPsswrd(inputPsswrdCursor);
                                }

                                //Add appLogin record to APPLOGGIN table
                                newAppLoggin = new AppLoggin(-1,etUserName.getText().toString(),newUserName,newPsswrd,etUsernameId.getText().toString(),etUserMessage.getText().toString(),accountsDB.getIconByID(62));
                                newAppLoggin.set_id(accountsDB.addItem(newAppLoggin));
                                appLoggin = newAppLoggin;

                            }else{
                                //The user name exists in DB, check is not being used as appLogin user name
                                //Extract user name object
                                newUserName = UserName.extractUserName(inputUserNameCursor);
                                //Query the DB to get the APPLOGGIN record with the userName id found for the user name typed in
                                Cursor appLoginCursor = accountsDB.getAppLoginCursorUserAndPsswrdData(newUserName.get_id());
                                //Check cursor returned Empty, which means the user name typed in isn't used as appLogin user
                                if(!appLoginCursor.moveToFirst()){
                                    //Check password doesn't exist in DB, otherwise keep PsswrdID
                                    inputPsswrdCursor = accountsDB.getPsswrdByName(etPassword.getText().toString());
                                    if(!inputPsswrdCursor.moveToFirst()){
                                        newPsswrd = new Psswrd(cryptographer.encryptText(etPassword.getText().toString()),cryptographer.getIv().getIV());
                                        newPsswrd.set_id(accountsDB.addItem(newPsswrd));
                                    }else{
                                        newPsswrd = Psswrd.extractPsswrd(inputPsswrdCursor);
                                    }

                                    //Add appLogin record to APPLOGGIN table
                                    newAppLoggin = new AppLoggin(-1,etUserName.getText().toString(),newUserName,newPsswrd,etUsernameId.getText().toString(),etUserMessage.getText().toString(),accountsDB.getIconByID(62));
                                    newAppLoggin.set_id(accountsDB.addItem(newAppLoggin));
                                    appLoggin = newAppLoggin;

                                }else{
                                    //Display error, The user name typed in is already in use
                                    errorMessageText = "Sorry, the user name typed in is alredy in use. Try again...";
                                }
                            }
                        }else{
                            //Display error, the password doesn't match the confirmation
                            errorMessageText = "Sorry, the password and it's confirmation do not match. Try again...";

                        }//End of if else statement to check password and confirmation match
                        if(newAppLoggin.get_id() != -1){
                            loginSuccess = true;
                        }
                    }//End of if statement to check the user name isn't empty and password field is valid

                    //loginSuccess = accountsDB.addItem(appLogin???)
                }else{
                    //In case of an already signed up account registered on the DB
                    //First check the username typed in by user actually  exists in DB, otherwise show proper error message
                    Cursor inputUserNameCursor = accountsDB.getUserNameByName(etUsernameId.getText().toString());
                    if(inputUserNameCursor.moveToFirst()){
                        //Extract user name object
                        UserName inputUserName = UserName.extractUserName(inputUserNameCursor);
                        //Query the DB to get the APPLOGGIN record with the userName id found for the user name typed in
                        Cursor appLoginCursor = accountsDB.getAppLoginCursorUserAndPsswrdData(inputUserName.get_id());
                        //If APPLOGGIN record with same user name is found on the DB, check the password
                        if(appLoginCursor.moveToNext()){
                            //Only then check for password validity
                            if(cryptographer.decryptText(appLoginCursor.getBlob(7),new IvParameterSpec(appLoginCursor.getBlob(8))).equals(etPassword.getText().toString().trim()) ){
                                loginSuccess = true;
                                appLoggin = AppLoggin.extractAppLoggin(appLoginCursor);
                            }else{
                                errorMessageText ="Sorry, the user name and password combination is invalid.Try again...";
                            }//End of if statement that checks password passed in matches applogin password
                        }//End of if statement that checks the user name passed in matches applogin user name

                    }

                }//End of if else statement that checks it's first time login

                if(loginSuccess){
                    //@Fixme if successful loggin, the APPLOGGIN id should be passed in to MainActivity
                    //Create new intent to launch MainActivity
                    Intent i= new Intent(LoginActivity.this, MainActivity.class);
                    //Put appLogin
                    i.putExtra("appLoginID",appLoggin.get_id());
                    //Start the MainActivity class
                    startActivity(i);
                }else{
                    //If user name not even present in the username table, throw an error message
                    MainActivity.displayToast(v.getContext(),errorMessageText,Toast.LENGTH_LONG, Gravity.CENTER);
                }
                //loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(etUsernameId.getText().toString(),
                 //       etPassword.getText().toString());
            }
        });

        //My stuff from here
        //Create crypto object to handle encryption and decryption
        cryptographer = new Cryptographer();
        //Dummy encryption to get IV created
        byte[] testEncrypted = cryptographer.encryptText("DummyEncryption");
        String test2 = cryptographer.decryptText(testEncrypted,cryptographer.getIv());
        //Create a new object to manage all DB interaction
        accountsDB = new AccountsDB(this);

        //Check if there's an account already registered in the DB. If there's at least one, show username and password fields only
        Cursor appLoginList = accountsDB.getAllAppLoginCursor();
        if(appLoginList.moveToNext()){
            displaySignUp = false;
            tvSignUp.setVisibility(View.VISIBLE);
            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etPasswordConfirm.setVisibility(View.VISIBLE);
                    etUserName.setVisibility(View.VISIBLE);
                    etUserMessage.setVisibility(View.VISIBLE);
                }
            });
            //Make the extra fields gone from UI
            etPasswordConfirm.setVisibility(View.GONE);
            etUserName.setVisibility(View.GONE);
            etUserMessage.setVisibility(View.GONE);
            btnLoginButton.setText(R.string.action_sign_in);
        }else{
            displaySignUp = true;
            tvSignUp.setVisibility(View.INVISIBLE);
            //Otherwise, show rest of fields for full sign up
            //Check all fields are valid
            //Enable sign up button when values are entered
            //
        }




        Log.d("Ext_onCreateLogin","Exit onCreate method in LoginActivity class.");
    }//End of onCreate method

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
//        Intent i = new Intent(this,MainActivity.class);
//        this.startActivity(i);
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    //Getter and Setter methods

    public static Cryptographer getCryptographer() {
        return cryptographer;
    }

    public static AccountsDB getAccountsDB() {
        return accountsDB;
    }
}//End of LoginActivity
