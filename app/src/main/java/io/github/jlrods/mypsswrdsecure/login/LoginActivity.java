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
import io.github.jlrods.mypsswrdsecure.Account;
import io.github.jlrods.mypsswrdsecure.AccountsDB;
import io.github.jlrods.mypsswrdsecure.AppLoggin;
import io.github.jlrods.mypsswrdsecure.AutoLogOutService;
import io.github.jlrods.mypsswrdsecure.Cryptographer;
import io.github.jlrods.mypsswrdsecure.EditAccountActivity;
import io.github.jlrods.mypsswrdsecure.Psswrd;
import io.github.jlrods.mypsswrdsecure.R;
import io.github.jlrods.mypsswrdsecure.MainActivity;
import io.github.jlrods.mypsswrdsecure.UserName;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

//Activity to display sign up or login fields and validate credentials for login
public class LoginActivity extends AppCompatActivity {

    //Attribute definition
    private LoginViewModel loginViewModel;
    private static Cryptographer cryptographer;
    private static AccountsDB accountsDB;
    private static boolean displaySignUp;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private long BIOMETRIC_DELAY_TIME = 1500;
    private long COUNT_DOWN_INTERVAL = 250;

    //Methods definition
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
        //Get the required UI objects from activity layout
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
            displaySignUp = false;
        }else{
            displaySignUp = true;
        }//End of if else statement to check the login list isn't empty

        //Set on click listener for the login button
        btnLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Define boolean flag to determine login success or failure
                boolean loginSuccess = false;
                AppLoggin appLoggin = new AppLoggin();
                //Error flag
                boolean loginError = false;
                //Login Error message
                String errorMessageText = "";

                //Check if an applogin is already recorded on the DB
                if(displaySignUp){
                    //If no applogin data registered on DB, check the usernameID field if valid,
                    //Check the password isn't empty and the passwordConfirm field matches the password field
                    if(loginViewModel.isUserNameValid(etUsernameId.getText().toString()) && loginViewModel.isPasswordValid(etPassword.getText().toString())){
                        if(etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())){
                            //UserName object used to retrieve user name data corresponding  ot the user input
                            UserName newUserName;
                            //Cursor and Psswrd object used to retrieve and store password data corresponding to the user input
                            Psswrd newPsswrd;
                            //Add user name to UserName table
                            newUserName = new UserName(cryptographer.encryptText(etUsernameId.getText().toString()),cryptographer.getIv().getIV());
                            //Update user name id by inputting result coming from DB insertion
                            newUserName.set_id(accountsDB.addItem(newUserName));
                            newPsswrd = new Psswrd(cryptographer.encryptText(etPassword.getText().toString()),cryptographer.getIv().getIV());
                            //Update password id by inputting result coming from DB insertion
                            newPsswrd.set_id(accountsDB.addItem(newPsswrd));
                            //Add appLogin record to APPLOGGIN table
                            appLoggin = new AppLoggin(-1,etUserName.getText().toString(),newUserName,newPsswrd,etUserMessage.getText().toString(),accountsDB.getIconByID(102));
                            //Update appLogin id by inputting result coming from DB insertion
                            appLoggin.set_id(accountsDB.addItem(appLoggin));
                            if(appLoggin.get_id() != -1){
                                loginSuccess = true;
                            }
                        }else{
                            //Display error, the password doesn't match the confirmation
                            loginError = true;
                            errorMessageText = getString(R.string.psswrdsDontMatchError);
                        }//End of if else statement to check password and confirmation match
                    }//End of if statement to check the user name isn't empty and password field is valid
                }else{
                    //In case of an already signed up account registered on the DB
                    //First check the username typed in by user actually  exists in DB, otherwise show proper error message
                    Cursor inputUserNameCursor = accountsDB.getUserNameByName(etUsernameId.getText().toString().trim());
                    if(inputUserNameCursor.moveToFirst()){
                        //Extract user name object
                        UserName inputUserName = UserName.extractUserName(inputUserNameCursor);
                        //Query the DB to get the APPLOGGIN record with the userName id found for the user name typed in
                        Cursor appLoginCursor = accountsDB.getAllAppLoginCursor();

                        if(appLoginCursor.getCount() == 1 && appLoginCursor.moveToFirst()){
                            //Extract appLoggin object to conduct credential verification
                            appLoggin = AppLoggin.extractAppLoggin(appLoginCursor);
                            //Check for user name validity
                            if(appLoggin.getUserName().get_id() == inputUserName.get_id()){
                                //Then check for password validity
                                Cursor inputPsswrdCursor = accountsDB.getPsswrdByName(etPassword.getText().toString().trim());
                                if(inputPsswrdCursor.moveToFirst()){
                                    Psswrd inputPsswrd = Psswrd.extractPsswrd(inputPsswrdCursor);
                                    if(appLoggin.getPsswrd().get_id() == inputPsswrd.get_id()){
                                        loginSuccess = true;
                                    }else{
                                        loginError = true;
                                    }//End of if else statement to check password is correct
                                }else{
                                    loginError = true;
                                }//End of if else statement to check password isn't null
                            }else{
                                loginError = true;
                            }//End of if else statement to check user name is correct
                        }else{
                            //Handle error, no more than one applogin should be recorded on the DB
                            loginError = true;
                            errorMessageText =getString(R.string.moreThanOneLoginError);
                        }//End of if statement that checks the user name passed in matches applogin user name
                    }else{
                        loginError = true;
                    }//End of if statement that checks user name exists in DB
                    //Check if any error occurred and assign correct error text to be displayed
                    if(loginError && errorMessageText.equals("")){
                        errorMessageText =getString(R.string.wrongCredentialsError);
                    }
                }//End of if else statement that checks it's first time login
                //Check if login was successful
                if(loginSuccess){
                    Bundle extras = getIntent().getExtras();
                    if(extras!=null){
                        if(extras.getBoolean("isActivityCalledFromNotification")){
                            checkIsAutoLogOutActive();
                            int accountID = extras.getInt("expiredPasswordAccountID");
                            Account expiredPasswordAccount = Account.extractAccount(accountsDB.getAccountCursorByID(accountID));
                            Intent i = new Intent(getBaseContext(), EditAccountActivity.class);
                            //Add extras to the intent object, specifically the current category where the add button was pressed from
                            i.putExtra("category", expiredPasswordAccount.getCategory().get_id());
                            i.putExtra(MainActivity.getIdColumn(), expiredPasswordAccount.get_id());
                            //i.putExtra("position",itemPosition);
                            startActivity(i);

                        }else{
                            throwMainActivity(appLoggin.get_id());
                        }//End of if else statement to check if call comes from push notification
                    }else{
                        //Call throw MainActivity method
                        throwMainActivity(appLoggin.get_id());
                    }//End of if else statment to check extras aren't null

                }else{
                    //If user name not even present in the username table, throw an error message
                    MainActivity.displayToast(v.getContext(),errorMessageText,Toast.LENGTH_LONG, Gravity.CENTER);
                }//End of if else statement to check the login result flag
            }//End of onClick method
        });//End of setOnClickListener method for the signUp-signIn button
        //Check if the sign up layout is not required
        if(!displaySignUp){
            //Call method to display biometric authentication dialog if setting is active
            if(getIsBioLoginActive(this) && !displaySignUp){
                this.callBiometricAuthentication();
            }
        }//End of if statement to check sigup display isn't in use
        Log.d("Ext_onCreateLogin","Exit onCreate method in LoginActivity class.");
    }//End of onCreate method

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
        if(getIsBioLoginActive(this) && !displaySignUp){
            this.callBiometricAuthentication();
        }
        Log.d("onResume","Enter onResume method in the LoginActivity class.");
    }//End of onResume method

    @Override
    public void onStop(){
        super.onStop();
        Log.d("onStopMain", "Enter onStop method in LogingActivity class.");
        MainActivity. checkForNotificationSent(this, false);
        Log.d("onStopMain", "Exit onStop method in LogingActivity class.");
    }//End of onStop method

    private boolean encryptedCredentialValidation(byte[] encryptedCredential, byte[] credentialIV ,TextView tvCredential){
        return cryptographer.decryptText(encryptedCredential,new IvParameterSpec(credentialIV)).equals(tvCredential.getText().toString().trim());
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


    //Getter and Setter methods

    public static Cryptographer getCryptographer() {
        return cryptographer;
    }//End of getCryptographer method

    public static AccountsDB getAccountsDB() {
        return accountsDB;
    }//End of getAccountsDB method

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

                    Bundle extras = getIntent().getExtras();
                    if(extras!=null){
                        if(extras.getBoolean("isActivityCalledFromNotification")){
                            //@Fixme: There's duplication here, create a method
                            checkIsAutoLogOutActive();
                            int accountID = extras.getInt("expiredPasswordAccountID");
                            Account expiredPasswordAccount = Account.extractAccount(accountsDB.getAccountCursorByID(accountID));
                            Intent i = new Intent(getBaseContext(), EditAccountActivity.class);
                            //Add extras to the intent object, specifically the current category where the add button was pressed from
                            i.putExtra("category", expiredPasswordAccount.getCategory().get_id());
                            i.putExtra(MainActivity.getIdColumn(), expiredPasswordAccount.get_id());
                            i.putExtra("isActivityCalledFromNotification",extras.getBoolean("isActivityCalledFromNotification"));

                            //i.putExtra("position",itemPosition);
                            startActivity(i);
                        }else{
                            //Call throw MainActivity method
                            throwMainActivity(appLoginID);
                        }//End of if else statment to check activity was called by push notification
                    }else{
                        //Call throw MainActivity method
                        throwMainActivity(appLoginID);
                    }//End of if else statement to check extras aren't null
                }//End of onAuthenticationSucceeded

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
        }//End of if statemenet to check it can be authenticated
        Log.d("biometricAuthentication","Exit biometricAuthentication method in the LoginActivity class.");
    }//End of biometricAuthentication method



    //Method to throw new MainActivity method
    private void throwMainActivity(int appLoginId){
        Log.d("ThrowMain","Enter throwMainActivity method in the LoginActivity class.");
        //Start service if required
        this.checkIsAutoLogOutActive();
        //Create new intent to launch MainActivity
        Intent i= new Intent(LoginActivity.this, MainActivity.class);
        //Put appLogin
        i.putExtra("appLoginID",appLoginId);
        i.putExtra("isMainActCalledFromLoginAct",true);
        //Start the MainActivity class
        startActivity(i);
        Log.d("ThrowMain","Exit throwMainActivity method in the LoginActivity class.");
    }//End of throwAddTaskActivity

    private boolean checkIsAutoLogOutActive(){
        Log.d("checkAutoLog","Enter checkIsAutoLogOutActive method in the LoginActivity class.");
        boolean result = false;
        try{
            if(MainActivity.isAutoLogOutActive(this)){
                Intent autoLogOutService = new Intent(this, AutoLogOutService.class);
                startService(autoLogOutService);
            }//End of if statement to check auto logout feature is on
            result = true;
            Log.d("checkAutoLog","Exit checkIsAutoLogOutActive method successfully in the LoginActivity class.");
        }catch (Exception e){
            Log.d("checkAutoLog","Exit checkIsAutoLogOutActive method  in the LoginActivity class with error: " + e.getMessage());
        }finally{
            return result;
        }//End of try catch finally block
    }//End of checkIsAutoLogOutActive method

    //Method to delay and wait for login activity then call biometric authentication method
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

    //Method to check biometric authentication setting status in the app preferences
    public static boolean getIsBioLoginActive(Context context) {
        Log.d("getIsBioLoginActive", "Enter getIsBioLoginActive method in MainActivity class.");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isBioLoginActive = pref.getBoolean("isBiometricLoginActive", true);
        Log.d("getIsBioLoginActive", "Exit getIsBioLoginActive method in MainActivity class.");
        return isBioLoginActive;
    }//End of setAppTheme method
}//End of LoginActivity