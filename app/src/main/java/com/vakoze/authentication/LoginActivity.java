package com.vakoze.authentication;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vakoze.R;
import com.vakoze.TimelineActivity;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_RECORD_AUDIO = 4;

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
    private int RC_SIGN_IN = 9001;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private FirebaseAuth mAuth;
    private boolean existedAccount;
    private FirebaseUser userAccount;
    private GoogleSignInAccount googleAccount;
    private User facebookAccount;
    private boolean onStart = false;
    private LinearLayout linearLayout;
    private AccessTokenTracker mAccessTokenTracker;
    private ImageButton facebookBtn, googleBtn;

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
                //updateUI(AccessToken.getCurrentAccessToken());
                handleFacebookAccessToken(AccessToken.getCurrentAccessToken());

            } else {

            }
            // Check if user is signed in (non-null) and update UI accordingly.
            /*
            FirebaseUser currentFacebookUser = mAuth.getCurrentUser();
            if(currentFacebookUser!=null){
                onStart = true;
                updateFacebookUI(currentFacebookUser);
            }
            */
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
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        // Set the dimensions of the sign-in button.
        /*SignInButton signInButton = findViewById(R.id.google_connect);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loginButton = (LoginButton) findViewById(R.id.facebook_connect);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(isConnected()){
                    // App code
                    onStart = false;
                    updateUI(loginResult.getAccessToken());
                    //handleFacebookAccessToken(loginResult.getAccessToken());
                } else {
                    displayToast("Vous n'êtes pas connecté à internet");
                }
            }
            @Override
            public void onCancel() {
                // App code
                displayToast("Vous avez annulé la connexion avec facebook");
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
                displayToast(exception.getMessage());
            }
        });
        */
        googleBtn = findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new OnClickListener() {
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
        facebookBtn = findViewById(R.id.facebookBtn);
        facebookBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccessTokenTracker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        // handle
                    }
                };
                mAccessTokenTracker.startTracking();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
                // Callback registration
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if(isConnected()){
                            // App code
                            onStart = false;
                            updateUI(loginResult.getAccessToken());
                            //handleFacebookAccessToken(loginResult.getAccessToken());
                        } else {
                            displayToast("Vous n'êtes pas connecté à internet");
                        }
                    }
                    @Override
                    public void onCancel() {
                        // App code
                        displayToast("Vous avez annulé la connexion avec facebook");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        displayToast(exception.getMessage());
                    }
                });

            }
        });


                




        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    if(isConnected()){
                        buttonClicked = "login";
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
                                    searchFacebookAccount(jsonObject.getString("email"), userId);
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

    private void updateFacebookUI(FirebaseUser facebookUser, AccessToken token) {
        if(facebookUser != null&& token !=null) {
            showProgress(false);
            GraphRequest graphRequest = GraphRequest.newMeRequest(token,
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
                                    searchFacebookAccount(jsonObject.getString("email"), userId);
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
        /*
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)&& shouldShowRequestPermissionRationale(CAMERA)&& shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)&&shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                            requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO);


                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
            requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }*/
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS, Manifest.permission.RECORD_AUDIO};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        return false;
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS&&requestCode == REQUEST_CAMERA&&requestCode == REQUEST_READ_EXTERNAL_STORAGE&&requestCode == REQUEST_WRITE_EXTERNAL_STORAGE&&requestCode == REQUEST_RECORD_AUDIO) {
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
                //searchEmailAccount(mEmail,mPassword);
                //our custom volley request
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/login",
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                JSONObject objData = null;
                                try {
                                    objData = new JSONObject(new String(response.data));

                                    if(!objData.getBoolean("error")){
                                        status =true;
                                        User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                        Intent startTimeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                        startActivity(startTimeline);
                                        finish();
                                    } else if(objData.getBoolean("error")){
                                        displayToast("Email ou mot de passe incorrect.");
                                        status =false;
                                    }


                                } catch (JSONException e) {
                                    //Dismiss the dialog
                                    status =false;
                                    displayToast("Connexion impossible.");
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Dismiss the dialog
                                status =false;
                                displayToast("Authentication failed."+error.getMessage());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", mEmail);
                        params.put("password", mPassword);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
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
                Volley.newRequestQueue(LoginActivity.this).add(volleyMultipartRequest);

            } else if (buttonClicked.equalsIgnoreCase("register")){

                Intent registerIntent = new Intent(LoginActivity.this, InscriptionActivity.class);
                registerIntent.putExtra("email", mEmail);
                registerIntent.putExtra("password", mPassword);
                startActivity(registerIntent);
            }

            return status;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            /*
            try {
                // Google Sign In was successful, authenticate with Firebase
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                final String[] idToken = new String[1];
                if (mUser != null) {
                    mUser.getIdToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        idToken[0] = task.getResult().getToken();
                                        // Send token to your backend via HTTPS
                                        // ...
                                    } else {
                                        // Handle error -> task.getException();
                                        displayToast("No id Token");
                                    }
                                }
                            });
                }
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account,task,idToken[0]);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }*/

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateGoogle(account);
            googleAccount = account;
            //searchAccount(account.getEmail());
            searchEmailAccountWithGoogleUid(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateGoogle(null);

            displayToast("Connexion impossible avec google."+e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct, final Task<GoogleSignInAccount> completedTask, String s) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        Log.d(TAG, "firebaseAuthWithGoogleIdToken:" + s);

        //AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        AuthCredential credential = GoogleAuthProvider.getCredential(s, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Signed in successfully, show authenticated UI.
                            //updateGoogle(account);
                            userAccount = user;
                            //searchAccount(account.getEmail());
                            GoogleSignInAccount account = null;
                            try {
                                account = completedTask.getResult(ApiException.class);

                                searchEmailAccountWithUid(account, user);
                            } catch (ApiException e) {
                                e.printStackTrace();
                                displayToast("une erreur s'est produite"+e.getMessage());
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            displayToast("Connexion impossible avec google."+task.getException());

                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateFacebookUI(user, token);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            //updateUI(null);
                            displayToast("Authentication failed : "+task.getException());
                        }

                        // ...
                    }
                });
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


            displayToast("Connexion impossible avec facebook.");

        }
    }

    private void tryRegisterGoogleAccount(final String Uid, final String nom, final String prenom, final String phone, final String email, final String profile_pic,final GoogleSignInAccount googleSignInAccount){
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("vakozesharedpref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/user/add",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject objData = null;
                        try {
                            objData = new JSONObject(new String(response.data));
                            final String message = objData.getString("message");
                            switch (objData.getString("error")) {
                                case "0":

                                    displayToast("Enregistrement réussi.");

                                    //searchAccount(userAccount.getEmail());
                                    User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                                    updateGoogle(googleSignInAccount, currentUser);
                                    break;
                                case "1":

                                    FirebaseAuth.getInstance().signOut();
                                    displayToast("L'enregistrement a echoué " + message);
                                    editor.clear();
                                    editor.apply();
                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                }
                                            });*/
                                    break;
                                case "2":

                                    FirebaseAuth.getInstance().signOut();
                                    displayToast("L'utilisateur existe déjà " + message);
                                    editor.clear();
                                    editor.apply();

                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });*/
                                    break;
                                default:

                                    FirebaseAuth.getInstance().signOut();
                                    displayToast("résultat inconnu " + message);
                                    editor.clear();
                                    editor.apply();
                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                }
                                            });*/
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            FirebaseAuth.getInstance().signOut();
                            displayToast("une erreur s'est produite ");
                            editor.clear();
                            editor.apply();
                            /*mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                        }
                                    });*/
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        //Dismiss the dialog

                        FirebaseAuth.getInstance().signOut();
                        displayToast("Authentication failed."+error.getMessage());
                        editor.clear();
                        editor.apply();
                        /*mGoogleSignInClient.signOut()
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });*/
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("u_id", Uid);
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("phone", phone);
                params.put("email", email);
                params.put("password", "");
                params.put("profile_pic", profile_pic);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
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

    private void tryRegisterFacebookAccount(final String Uid, final String nom, final String prenom, /* String final phone, */ final String email, final String password,  final String profile_pic){
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("vakozesharedpref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //our custom volley request
        StringRequest volleyMultipartRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/user/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject objData = null;
                        try {

                            objData = new JSONObject(response);
                            switch (objData.getString("error")) {
                                case "0":
                                    //Dismiss the dialog
                                    //searchFacebookAccount(facebookAccount.getEmail(), String.valueOf(facebookAccount.getId()));
                                    //User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                                    updateFacebook(facebookAccount);
                                    displayToast("Enregistrement réussi");

                                    break;
                                case "1":
                                    displayToast("L'enregistrement a echoué");
                                    Log.e("VakoError", "L'enregistrement a echoué "+ objData.getString("message"));
                                    LoginManager.getInstance().logOut();
                                    editor.clear();
                                    editor.apply();
                                    break;
                                case "2":
                                    displayToast("L'utilisateur existe déjà");
                                    Log.e("VakoError", "L'utilisateur existe déjà "+ objData.getString("message"));
                                    LoginManager.getInstance().logOut();
                                    editor.clear();
                                    editor.apply();
                                    break;
                                default:
                                    displayToast("résultat inconnu" + objData.getString("message"));
                                    Log.e("VakoError", "L'utilisateur existe déjà "+ objData.getString("message"));
                                    LoginManager.getInstance().logOut();
                                    editor.clear();
                                    editor.apply();
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("une erreur s'est produite : "+e.getMessage());
                            Log.e("VakoError", "une erreur s'est produite ");
                            LoginManager.getInstance().logOut();
                            editor.clear();
                            editor.apply();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        displayToast("Facebook registration failed."+error.getMessage());
                        LoginManager.getInstance().logOut();
                        editor.clear();
                        editor.apply();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("u_id", Uid);
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("email", email);
                params.put("password", "");
                params.put("profile_pic", profile_pic);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
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
    /*
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
                                         User user = new User(Integer.parseInt(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
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
    */
    private void searchFacebookAccount(final String facebook_email, final String facebook_u_id){
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("vakozesharedpref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/login/social",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {


                        JSONObject objData = null;
                        try {

                            objData = new JSONObject(new String(response.data));
                            switch (objData.getString("error")) {
                                case "0":
                                    //Dismiss the dialog
                                    User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    Intent timeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                    startActivity(timeline);
                                    finish();
                                    break;
                                case "1":
                                    LoginManager.getInstance().logOut();

                                    editor.clear();
                                    editor.apply();
                                    Log.e("VakoError: ", "Une erreur s'est produite"+" " + " " + facebook_email + " " + facebook_u_id + " " + objData.getString("message"));
                                    displayToast("Une erreur s'est produite");

                                    break;
                                case "2":

                                    Log.e("VakoError: ", "Utilisateur déjà connecté avec une autre méthode"+" " + " " + facebook_email + " " + facebook_u_id + " " + objData.getString("message"));
                                    LoginManager.getInstance().logOut();

                                    editor.clear();
                                    editor.apply();
                                    displayToast("Utilisateur déjà connecté avec une autre méthode");
                                    break;
                                case "3":
                                    //displayToast("Utilisateur non enregistré" + facebook_email + facebook_u_id);
                                    Log.e("VakoError: ", "Utilisateur non enregistré"+" " + " " + facebook_email + " " + facebook_u_id + " " + objData.getString("message"));
                                    User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                                    tryRegisterFacebookAccount(facebookAccount.getU_id(), currentUser.getNom(), facebookAccount.getPrenom(), facebookAccount.getEmail(), mPasswordView.getText().toString(),facebookAccount.getProfile_pic());

                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            LoginManager.getInstance().logOut();
                            editor.clear();
                            editor.apply();
                            displayToast("une erreur s'est produite : "+e.getMessage() + objData);
                            Log.e("VakoError: ", "une erreur s'est produite : "+e.getMessage() + objData);
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        LoginManager.getInstance().logOut();
                        editor.clear();
                        editor.apply();
                        displayToast("Authentication failed."+error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", facebook_email);
                params.put("id", facebook_u_id);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
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

    private void searchEmailAccountWithUid(final GoogleSignInAccount googleSignInAccount, final FirebaseUser account){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/login/social",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                         JSONObject objData = null;
                        try {

                            objData = new JSONObject(new String(response.data));
                            final String message = objData.getString("message");
                            switch (objData.getString("error")) {
                                case "0": {
                                    //Dismiss the dialog
                                    User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    //Intent startTimeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                    //startActivity(startTimeline);
                                    //finish();
                                    updateGoogle(googleSignInAccount, user);
                                    break;
                                }
                                case "1":
                                    FirebaseAuth.getInstance().signOut();
                                    Log.e("VakoError: ", "Une erreur s'est produite.");
                                    displayToast("Une erreur s'est produite"+" "+ googleSignInAccount.getEmail() + googleSignInAccount.getId()+" "+message);

                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });*/

                                    break;
                                case "2":

                                    FirebaseAuth.getInstance().signOut();
                                    displayToast("Utilisateur déjà connecté avec une autre méthode");
                                    Log.e("VakoError: ", "Utilisateur déjà connecté avec une autre méthode"+" "+ googleSignInAccount.getEmail() + googleSignInAccount.getId()+" "+message);

                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });*/

                                    break;
                                case "3": {
                                    //displayToast("Utilisateur non enregistré");
                                    Log.e("VakoError: ", "Utilisateur non enregistré"+" "+ googleSignInAccount.getEmail() + googleSignInAccount.getId()+" "+objData.getString("message"));

                                    User user = new User(2L, googleSignInAccount.getId(), googleSignInAccount.getFamilyName(), googleSignInAccount.getGivenName(), googleSignInAccount.getEmail(), "", String.valueOf(googleSignInAccount.getPhotoUrl()));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    tryRegisterGoogleAccount(googleSignInAccount.getId(), googleSignInAccount.getDisplayName(), googleSignInAccount.getFamilyName(), "", googleSignInAccount.getEmail(), String.valueOf(googleSignInAccount.getPhotoUrl()),googleSignInAccount);

                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseAuth.getInstance().signOut();
                            displayToast("une erreur s'est produite : ");
                            /*mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                             }
                                    });*/
                            Log.e("VakoError: ", "une erreur s'est produite : "+e.getMessage() + objData);



                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        //Dismiss the dialog
                        FirebaseAuth.getInstance().signOut();
                        displayToast("Authentication failed."+error.getMessage());
                        /*mGoogleSignInClient.signOut()
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });*/

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", googleSignInAccount.getEmail());
                params.put("id", googleSignInAccount.getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
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

    private void searchEmailAccountWithGoogleUid(final GoogleSignInAccount googleSignInAccount){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/login/social",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject objData = null;
                        try {

                            objData = new JSONObject(new String(response.data));
                            final String message = objData.getString("message");
                            switch (objData.getString("error")) {
                                case "0": {
                                    //Dismiss the dialog
                                    User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    //Intent startTimeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                    //startActivity(startTimeline);
                                    //finish();
                                    updateGoogle(googleSignInAccount, user);
                                    break;
                                }
                                case "1":
                                    FirebaseAuth.getInstance().signOut();
                                    Log.e("VakoError: ", "Une erreur s'est produite.");
                                    displayToast("Une erreur s'est produite"+" "+ googleSignInAccount.getEmail() + googleSignInAccount.getId()+" "+message);

                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });*/

                                    break;
                                case "2":

                                    FirebaseAuth.getInstance().signOut();
                                    displayToast("Utilisateur déjà connecté avec une autre méthode");
                                    Log.e("VakoError: ", "Utilisateur déjà connecté avec une autre méthode"+" "+ googleSignInAccount.getEmail() + googleSignInAccount.getId()+" "+message);

                                    /*mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });*/

                                    break;
                                case "3": {
                                    //displayToast("Utilisateur non enregistré");
                                    Log.e("VakoError: ", "Utilisateur non enregistré"+" "+ googleSignInAccount.getEmail() + googleSignInAccount.getId()+" "+objData.getString("message"));

                                    User user = new User(2L, googleSignInAccount.getId(), googleSignInAccount.getFamilyName(), googleSignInAccount.getGivenName(), googleSignInAccount.getEmail(), "", String.valueOf(googleSignInAccount.getPhotoUrl()));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    tryRegisterGoogleAccount(googleSignInAccount.getId(), googleSignInAccount.getDisplayName(), googleSignInAccount.getFamilyName(), "", googleSignInAccount.getEmail(), String.valueOf(googleSignInAccount.getPhotoUrl()),googleSignInAccount);

                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseAuth.getInstance().signOut();
                            displayToast("une erreur s'est produite : ");
                            /*mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                             }
                                    });*/
                            Log.e("VakoError: ", "une erreur s'est produite : "+e.getMessage() + objData);



                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        //Dismiss the dialog
                        FirebaseAuth.getInstance().signOut();
                        displayToast("Authentication failed."+error.getMessage());
                        /*mGoogleSignInClient.signOut()
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });*/

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", googleSignInAccount.getEmail());
                params.put("id", googleSignInAccount.getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
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

    private void searchEmailAccount(final String email, final String password){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/login",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject objData = null;
                        try {
                            objData = new JSONObject(new String(response.data));

                            if(objData.getString("error").equals(false)){

                                User user = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                Intent startTimeline = new Intent(LoginActivity.this, TimelineActivity.class);
                                startActivity(startTimeline);
                                finish();

                            } else if(objData.getString("error").equals(true)){
                                displayToast("L'utilisateur n'existe pas."+objData.getString("message"));

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

    private void logout(){
        LoginManager.getInstance().logOut();
        /*mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });*/
        FirebaseAuth.getInstance().signOut();

        //finish();
        SharedPrefManager.getInstance(getApplicationContext()).logout();
    }
}