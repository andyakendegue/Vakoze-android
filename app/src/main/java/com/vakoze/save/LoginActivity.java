package com.vakoze.save;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vakoze.authentication.InscriptionActivity;
import com.vakoze.R;
import com.vakoze.TimelineActivity;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String TAG = "tag";
    private String buttonClicked;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private boolean existedAccount;
    private GoogleSignInAccount userAccount;
    private User facebookAccount;
    private boolean onStart = false;
    private LinearLayout linearLayout;

    @Override
    public void onStart() {
        super.onStart();

        if(isConnected()){
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            if(account != null){
                updateGoogle(account, currentUser);
            }
            else {

            }
            Profile.getCurrentProfile();

            boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
            if(!loggedIn){
                onStart = true;
                updateUI(AccessToken.getCurrentAccessToken());

            } else {

            }

            boolean loggedin= SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn();
            if(loggedin){

                startloginActivity();

            }

        } else {
            displayToast("Vous n'êtes pas connecté à internet");
        }

    }
    private void startloginActivity(){
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        Intent loguser = new Intent(LoginActivity.this, TimelineActivity.class);
        startActivity(loguser);
        finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        linearLayout = (LinearLayout) findViewById(R.id
                .login_activity);

        // Configure sign-in to request the user's ID, email address, and basic
//      profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        /*
        SignInButton signInButton = findViewById(R.id.google_connect);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    displayToast("Vous n'êtes pas connecté à internet");
                }


            }
        });

        // Passing MainActivity in Facebook SDK.
        FacebookSdk.sdkInitialize(LoginActivity.this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_connect);
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if(isConnected()){
                    // App code
                    onStart = false;
                    updateUI(loginResult.getAccessToken());
                } else {
                    displayToast("Vous n'êtes pas connecté à internet");
                }


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });




*/



        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    if(isConnected()){
                        attemptLogin();
                        return true;
                    } else {
                        displayToast("Vous n'êtes pas connecté à internet");
                        return false;
                    }

                }
                return false;
            }
        });
        Button mEmailLoginButton = (Button) findViewById(R.id.email_login_button);
        mEmailLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isConnected()){
                    buttonClicked = "login";
                    attemptLogin();
                } else {
                    displayToast("Vous n'êtes pas connecté à internet");
                }

            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){

                    buttonClicked = "register";
                    new UserLoginTask(mEmailView.getText().toString(),mPasswordView.getText().toString()).execute();
                    //attemptLogin();
                } else {
                    displayToast("Vous n'êtes pas connecté à internet");
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);




    }

    private void updateUI(AccessToken accessToken) {
        if(accessToken != null) {
            showProgress(false);
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                            try {
                                showProgress(false);


                                //creating a new user object
                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).clear();



                                String userId = jsonObject.getString("id");
                                String profilePicture = "https://graph.facebook.com/" + userId + "/picture?width=500&height=500";


                                 User user = new User((long)1, jsonObject.getString("id"), jsonObject.getString("last_name"), jsonObject.getString("first_name"), jsonObject.getString("email"), "", profilePicture);
                                facebookAccount = user;
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                if(onStart){
                                    Intent timeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                    startActivity(timeline);
                                    finish();
                                } else {
                                    searchFacebookAccount(jsonObject.getString("email"));
                                }




                                // Adding all user info one by one into TextView.
                                /*
                                FacebookDataTextView.setText("ID: " + jsonObject.getString("id"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nName : " + jsonObject.getString("name"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nFirst name : " + jsonObject.getString("first_name"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLast name : " + jsonObject.getString("last_name"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nEmail : " + jsonObject.getString("email"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nGender : " + jsonObject.getString("gender"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLink : " + jsonObject.getString("link"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nTime zone : " + jsonObject.getString("timezone"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nLocale : " + jsonObject.getString("locale"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nUpdated time : " + jsonObject.getString("updated_time"));

                                FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nVerified : " + jsonObject.getString("verified"));
                                */
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showProgress(false);

                                displayToast("Connexion impossible");
                            }
                        }
                    });

            Bundle bundle = new Bundle();
            bundle.putString(
                    "fields",
                    "id,name,link,email,gender,last_name,first_name,locale,timezone,updated_time,verified, picture{url}"
            );
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();
        } else {
            showProgress(false);

            displayToast("Connexion impossible");

        }


    }




    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        Pattern pattern;

        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean status = false;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.



            if (buttonClicked.equalsIgnoreCase("login") ) {
                searchEmailAccount(mEmail,mPassword);

            } else if (buttonClicked.equalsIgnoreCase("register")){

                Intent registerIntent = new Intent(LoginActivity.this, InscriptionActivity.class);
                registerIntent.putExtra("email", mEmail);
                registerIntent.putExtra("password", mPassword);
                startActivity(registerIntent);



            }





            // TODO: register the new account here.
            return status;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            showProgress(false);
            if (success) {



            } else {

/*
                mEmailView.setError(getString(R.string.error_incorrect_email));
                mEmailView.requestFocus();
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();

                */


                displayToast("Email ou mot de passe incorrect");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }



    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateGoogle(account);
            userAccount = account;
            searchAccount(account.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateGoogle(null);

            displayToast("Connexion impossible avec google.");
        }
    }
    private void updateGoogle(GoogleSignInAccount currentUser, User userInfo) {
        // Check for existing Google Sign In account, if the user is already signed in



        if(currentUser != null){
            showProgress(false);
            Intent timeline = new Intent(LoginActivity.this, TimelineActivity.class);

            currentUser.getEmail();

            //creating a new user object
            //storing the user in shared preferences
            SharedPrefManager.getInstance(getApplicationContext()).clear();
            if(currentUser.getPhotoUrl()!=null){
                User user = new User(userInfo.getId(),currentUser.getId(),currentUser.getFamilyName(),currentUser.getGivenName(),currentUser.getEmail(),"", currentUser.getPhotoUrl().toString());
                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);


                 } else {
                User user = new User(userInfo.getId(),currentUser.getId(),currentUser.getFamilyName(),currentUser.getGivenName(),currentUser.getEmail(),"", "");
                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);


            }

            startActivity(timeline);
            finish();


        } else {
            showProgress(false);


            displayToast("Connexion impossible avec google.");

        }
    }
    private void updateFacebook(User userInfo) {
        // Check for existing Google Sign In account, if the user is already signed in



        if(userInfo != null){
            showProgress(false);
            Intent timeline = new Intent(LoginActivity.this, TimelineActivity.class);

            userInfo.getEmail();

            //creating a new user object
            //storing the user in shared preferences
            SharedPrefManager.getInstance(getApplicationContext()).clear();
            if(userInfo.getProfile_pic()!=null){
                User user = new User(userInfo.getId(),userInfo.getU_id(),userInfo.getNom(),userInfo.getPrenom(),userInfo.getEmail(),"", userInfo.getProfile_pic());
                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);


            } else {
                User user = new User(userInfo.getId(),userInfo.getU_id(),userInfo.getNom(),userInfo.getPrenom(),userInfo.getEmail(),"", "");
                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);


            }

            startActivity(timeline);
            finish();


        } else {
            showProgress(false);


            displayToast("Connexion impossible avec google.");

        }
    }

    private void tryRegisterGoogleAccount(final String Uid, final String nom, final String prenom, /* String final phone, */ final String email, final String profile_pic){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/add?u_id="+Uid+"&nom="+nom+"&prenom="+prenom+"&password=empty&profile_pic="+profile_pic+"&email="+email,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String obj = new String(response.data);


                        if(obj.equalsIgnoreCase("saved")) {
                            //Dismiss the dialog

                            displayToast("Enregistrement réussi.");

                            searchAccount(userAccount.getEmail());
                            User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                            updateGoogle(userAccount, currentUser);


                        } else {

                            displayToast("L'enregistrement a echoué");
                            //Dismiss the dialog



                        }


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog

                        displayToast("Authentication failed."+error);
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("u_id", Uid);
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("email", email);
                params.put("password", profile_pic);
                params.put("profile_pic", profile_pic);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "admin:admin";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void tryRegisterFacebookAccount(final String Uid, final String nom, final String prenom, /* String final phone, */ final String email, final String password,  final String profile_pic){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/add?u_id="+Uid+"&nom="+nom+"&prenom="+prenom+"&password="+password+"&profile_pic="+profile_pic+"&email="+email,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String obj = new String(response.data);


                        if(obj.equalsIgnoreCase("saved")) {
                            //Dismiss the dialog
                            searchFacebookAccount(facebookAccount.getEmail());
                            User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                            updateFacebook(currentUser);



                        } else {
                            displayToast("L'enregistrement a echoué");
                            //Dismiss the dialog



                        }


                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog

                        displayToast("Authentication failed."+error);
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("u_id", Uid);
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("email", email);
                params.put("password", password);
                params.put("profile_pic", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "admin:admin";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    private void searchAccount(final String email){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();
                            boolean emailExist=false;

                             for (int i = 0; i < objLength; i++)
                                 {
                                     JSONObject objData=obj.getJSONObject(i);
                                     if(objData.getString("email").equals(email)){

                                         emailExist=true;
                                         User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                         SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                     } else {

                                         emailExist = false;
                                         displayToast("L'email n'existe pas.");
                                     }

                            }

                            if(!emailExist){



                                tryRegisterGoogleAccount(userAccount.getId(),userAccount.getFamilyName(),userAccount.getDisplayName(),userAccount.getEmail(),userAccount.getPhotoUrl().toString());

                            } else {


                                User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                                updateGoogle(userAccount, currentUser);
                            }





                        } catch (JSONException e) {
                            //Dismiss the dialog

                            displayToast("Recherche impossible."+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog

                        displayToast("Authentication failed."+error.getMessage());
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "admin:admin";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    private void searchFacebookAccount(final String email){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();
                            boolean emailExist=false;

                            for (int i = 0; i < objLength; i++)
                            {
                                JSONObject objData=obj.getJSONObject(i);
                                if(objData.getString("email").equals(email)){
                                    emailExist = true;
                                    User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                } else {

                                    emailExist = false;
                                    displayToast("L'email n'existe pas.");
                                }
                            }

                            if(!emailExist){
                                User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                                tryRegisterFacebookAccount(currentUser.getU_id(),currentUser.getNom(),currentUser.getPrenom(),currentUser.getEmail(),mPasswordView.getText().toString(), currentUser.getProfile_pic());

                            } else {
                                Intent timeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                startActivity(timeline);
                                finish();
                            }

                        } catch (JSONException e) {
                            //Dismiss the dialog

                            displayToast("Recherche impossible."+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog

                        displayToast("Authentication failed."+error.getMessage());
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "admin:admin";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void searchEmailAccount(final String email, final String password){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/login",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();

                            for (int i = 0; i < objLength; i++)
                            {
                                JSONObject objData=obj.getJSONObject(i);
                                if(objData.getString("email").equals(email) && password.equals(objData.getString("password"))){


                                    User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    Intent startTimeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                    startActivity(startTimeline);
                                    finish();

                                } else if(!objData.getString("email").equals(email)){
                                    displayToast("L'utilisateur n'existe pas.");

                                } else if(!password.equals(objData.getString("password"))) {

                                displayToast("Le mot de passe est incorrect.");


                            }

                            }







                        } catch (JSONException e) {
                            //Dismiss the dialog

                            displayToast("Recherche impossible."+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog


                        displayToast("Authentication failed."+error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cent:capp7622argent";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public void displayToast(String message){
        Snackbar snackbar = Snackbar
                .make(linearLayout, message, Snackbar.LENGTH_LONG)
                /*.setAction("Reessayer", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                buttonClicked = "login";
                                attemptLogin();
                            }
                        })*/;
        // Changing message text color
        //snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

}