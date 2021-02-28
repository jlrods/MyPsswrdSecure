package io.github.jlrods.mypsswrdsecure.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import java.util.concurrent.Executor;
import javax.crypto.spec.IvParameterSpec;
import io.github.jlrods.mypsswrdsecure.AccountsDB;
import io.github.jlrods.mypsswrdsecure.AppLoggin;
import io.github.jlrods.mypsswrdsecure.Cryptographer;
import io.github.jlrods.mypsswrdsecure.Psswrd;
import io.github.jlrods.mypsswrdsecure.R;
import io.github.jlrods.mypsswrdsecure.MainActivity;
import io.github.jlrods.mypsswrdsecure.UserName;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private static Cryptographer cryptographer;
    private static AccountsDB accountsDB;
    private static boolean displaySignUp;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private long BIOMETRIC_DELAY_TIME = 1500;
    private long COUNT_DOWN_INTERVAL = 250;
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
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(etUsernameId.getText().toString(),
//                            etPassword.getText().toString());
//                }
                return false;
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
        final Cursor appLoginList = accountsDB.getAllAppLoginCursor();
        if(appLoginList.moveToNext()){
            etPasswordConfirm.setVisibility(View.GONE);
            etUserName.setVisibility(View.GONE);
            etUserMessage.setVisibility(View.GONE);
            btnLoginButton.setText(R.string.action_sign_in);
        }else{
            displaySignUp = true;
        }

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
                                newUserName = new UserName(cryptographer.encryptText(etUsernameId.getText().toString()),cryptographer.getIv().getIV());
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
                    }else{
                        errorMessageText ="Sorry, the user name and password combination is invalid.Try again...";
                    }//End of if statement that checks user name exists in DB

                }//End of if else statement that checks it's first time login

                if(loginSuccess){
                    throwMainActivity(appLoggin.get_id());
                }else{
                    //If user name not even present in the username table, throw an error message
                    MainActivity.displayToast(v.getContext(),errorMessageText,Toast.LENGTH_LONG, Gravity.CENTER);
                }
                //loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(etUsernameId.getText().toString(),
                 //       etPassword.getText().toString());
            }//End of onClick method
        });//End of setOnClickListener method for the signUp-signIn button

        if(!displaySignUp){
            //Call method to display biometric authentication dialog if setting is active
            if(getIsBioLoginActive(this)){
                this.callBiometricAuthentication();
            }
        }//End of if statement to check sigup display isn't in use
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

    //Method to handle biometric authentication
    private void biometricAuthentication(final int appLoginID){
        Log.d("biometricAuthentication","Enter biometricAuthentication method in the LoginActivity class.");
        boolean canAuthenticate = false;
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                canAuthenticate = true;
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
//                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
//                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
//                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }
        //@TODO: Use user preferences to activate or deactivate this option
        if(canAuthenticate){
            executor = ContextCompat.getMainExecutor(this);
            biometricPrompt = new BiometricPrompt(LoginActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.authenErr) + errString, Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.authenSuccessful), Toast.LENGTH_SHORT).show();
                    //Call throw MainActivity method
                    throwMainActivity(appLoginID);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), getString(R.string.authenFailed),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometLoginTitle))
                    .setSubtitle(getString(R.string.biometLoginSubTitle))
                    .setNegativeButtonText(getString(R.string.biometLoginCancelMssg))
                    .setAllowedAuthenticators(BIOMETRIC_STRONG)
                    .build();
            //Prompt fingerprint authentication straight away.
            biometricPrompt.authenticate(promptInfo);
        }
        Log.d("biometricAuthentication","Exit biometricAuthentication method in the LoginActivity class.");
    }//End of biometricAuthentication method

    @Override
    //User on resume method to call biometric authentication method again
    public void onResume(){
        super.onResume();
        Log.d("onResume","Enter onResume method in the LoginActivity class.");
        //Clear previous entered data
        EditText etUsernameId = findViewById(R.id.etUsernameId);
        EditText etPassword = findViewById(R.id.etPassword);
        etUsernameId.setText("");
        etPassword.setText("");
        //Call method to display biometric authentication dialog if setting is active
        if(getIsBioLoginActive(this)){
            this.callBiometricAuthentication();
        }
        Log.d("onResume","Enter onResume method in the LoginActivity class.");
    }//End of onResume method

    //Method to throw new MainActivit ymethod
    private void throwMainActivity(int appLoginId){
        Log.d("ThrowMain","Enter throwMainActivity method in the LoginActivity class.");
        //Create new intent to launch MainActivity
        Intent i= new Intent(LoginActivity.this, MainActivity.class);
        //Put appLogin
        i.putExtra("appLoginID",appLoginId);
        //Start the MainActivity class
        startActivity(i);
        Log.d("ThrowMain","Exit throwMainActivity method in the LoginActivity class.");
    }//End of throwAddTaskActivity

    private void callBiometricAuthentication(){
        Log.d("callBiometAuthenT","Enter callBiometricAuthentication method in the LoginActivity class.");
        CountDownTimer biometricDelay = new CountDownTimer(BIOMETRIC_DELAY_TIME, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("onTick","Log remainder time in  callBiometricAuthentication method in the LoginActivity class.");
            }

            @Override
            public void onFinish() {
                Log.d("onFinish","Log onFinish method call as delay time in callBiometricAuthentication method in the LoginActivity class has finished.");
                biometricAuthentication(accountsDB.getAppLoginCursor(accountsDB.getMaxItemIdInTable(MainActivity.getApplogginTable())).getInt(0));
            }
        };
        biometricDelay.start();
        Log.d("callBiometAuthenT","Exit callBiometricAuthentication method in the LoginActivity class.");
    }//End of callBiometricAuthentication method

    public static boolean getIsBioLoginActive(Context context) {
        Log.d("getIsBioLoginActive", "Enter getIsBioLoginActive method in MainActivity class.");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isBioLoginActive = pref.getBoolean("isBiometricLoginActive", true);
        Log.d("getIsBioLoginActive", "Exit getIsBioLoginActive method in MainActivity class.");
        return isBioLoginActive;
    }//End of setAppTheme method
}//End of LoginActivity
