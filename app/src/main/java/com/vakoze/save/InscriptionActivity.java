package com.vakoze.save;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vakoze.R;
import com.vakoze.TimelineActivity;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;

public class InscriptionActivity extends AppCompatActivity {


    private ImageView profil;
    private EditText nomEdit, prenomEdit, phoneEdit, passwordEdit, confirmPasswordEdit, emailEdit;
    private ProgressDialog progressDialog;
    private String TAG ="tag";
    private ConstraintLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        linearLayout = findViewById(R.id.inscription_activity);


        /*
        Initialisation des entrée de texte
         */
        nomEdit = findViewById(R.id.nomEditInscription);
        prenomEdit = findViewById(R.id.prenomEditInscription);
        //phoneEdit = findViewById(R.id.phoneEdit);
        passwordEdit = findViewById(R.id.passwordEditInscription);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEditInscription);
        emailEdit = findViewById(R.id.emailEditInscription);

        Intent thisActivity = getIntent();
        if(thisActivity.getStringExtra("email").equals("") || thisActivity.getStringExtra("password").equals("")){

        } else {
            emailEdit.setText(thisActivity.getStringExtra("email"));
            passwordEdit.setText(thisActivity.getStringExtra("password"));
        }


        /*
          initialisation des boutons
         */
        // Initialisation des boutons
        profil = findViewById(R.id.profil);
        Button cancel = findViewById(R.id.cancel);
        Button valider = findViewById(R.id.valider);


        // Annuler
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(InscriptionActivity.this);

            }
        });
        // Inscription
        valider.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(isConnected()){
                    attemptLogin();
                } else {
                    displayToast("vous n'êtes pas connecté à internet");
                }


            }
        });
    }
    private void attemptLogin() {


        // Reset errors.
        emailEdit.setError(null);
        passwordEdit.setError(null);

        // Store values at the time of the login attempt.
        UUID Uid = UUID.randomUUID();

        String UId, nom, prenom, phone, password, confirmPassword, email;
        Date date_ajout, date_modif;


        UId = Uid.toString();
        nom = nomEdit.getText().toString();
        prenom = prenomEdit.getText().toString();
        confirmPassword = confirmPasswordEdit.getText().toString();
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();


        Date currentTime = Calendar.getInstance().getTime();
        date_ajout = currentTime;
        date_modif = currentTime;

        boolean cancel = false;
        View focusView = null;
        View focusView1 = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordEdit.setError(getString(R.string.error_invalid_password));
            focusView = passwordEdit;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError(getString(R.string.error_field_required));
            focusView = emailEdit;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailEdit.setError(getString(R.string.error_invalid_email));
            focusView = emailEdit;
            cancel = true;
        }
        if (!isPasswordSimilar(password, confirmPassword)) {
            passwordEdit.setError(getString(R.string.error_not_similar_password));
            confirmPasswordEdit.setError(getString(R.string.error_not_similar_password));
            focusView = passwordEdit;

                focusView1 = confirmPasswordEdit;


            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            if(focusView1!=null){
                focusView1.requestFocus();
            }
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog = new ProgressDialog(this);
            // Setting Title
            progressDialog.setTitle("Enregistrement de vos informations");
            // Setting Message
            progressDialog.setMessage("Chargement...");
            // Progress Dialog Style Horizontal
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Progress Dialog Style Spinner
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // Progress Dialog Max Value

            progressDialog.setMax(100);
            // Fetching max value
            progressDialog.getMax();
            // Fetching current progress
            progressDialog.getProgress();
            // Incremented By Value 2
            //progressDialog.incrementProgressBy(2);
            // Cannot Cancel Progress Dialog
            progressDialog.setCancelable(false);
            progressDialog.show();
            searchEmailAccount(UId, nom, prenom, /* phone ,*/ email, password);

            //Toast.makeText(getApplicationContext(), "Enregistrement réussi"+tags+ UId+ nom+ prenom+ /* phone +*/ email+ password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
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
    private boolean isPasswordSimilar(String password, String confirmPassword) {
        //TODO: Replace this with your own logic
        return password.equals(confirmPassword);
    }



    private void uploadTOServer( final String Uid, final String nom, final String prenom, /* String final phone, */ final String email, final String password){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/add?u_id="+Uid+"&nom="+nom+"&prenom="+prenom+"&password="+password+"&profile_pic=empty&email="+email,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String obj = new String(response.data);


                        if(obj.equalsIgnoreCase("saved")) {
                            //Dismiss the dialog
                            progressDialog.dismiss();
                            User user = new User((long)1,Uid,nom,prenom,email,"", "");
                            SharedPrefManager.getInstance(getApplicationContext()).clear();
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            displayToast("Enregistrement réussi");
                            Intent timeline = new Intent(InscriptionActivity.this, TimelineActivity.class);
                            startActivity(timeline);

                        } else {
                            progressDialog.dismiss();

                            displayToast("L'enregistrement a echoué");


                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        progressDialog.dismiss();
                         Snackbar snackbar = Snackbar
                                .make(linearLayout, "Il ya eu un problème lors de votre enregistrement", Snackbar.LENGTH_LONG)
                                .setAction("Reessayer", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        attemptLogin();
                                    }
                                });
                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);
                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
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
                //params.put("phone", phone);
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

    private void updateUI(String Uid, String nom, String prenom, String email, String password) {
        // Check for existing Google Sign In account, if the user is already signed in


        if(Uid != null){

            Intent timeline = new Intent(InscriptionActivity.this, TimelineActivity.class);
            //creating a new user object
            //storing the user in shared preferences
            SharedPrefManager.getInstance(getApplicationContext()).clear();
            User user = new User((long)1,Uid,nom,prenom, email,"", "");
            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

            startActivity(timeline);
            finish();


        }
    }

    private void searchEmailAccount(final String UId, final String nom, final String prenom, final String email, final String password){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();

                            for (int i = 0; i < objLength; i++)
                            {
                                JSONObject objData=obj.getJSONObject(i);
                                if(objData.getString("email").equals(email) ){


                                    /*User user = new User(Integer.parseInt(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    Intent startTimeline = new Intent(InscriptionActivity.this, TimelineActivity.class);
                                    startActivity(startTimeline);
                                    finish();
                                    */
                                    displayToast("Un utilisateur avec cette adresse Email existe déjà.");

                                } else if(!objData.getString("email").equals(email)){
                                    displayToast("L'utilisateur n'existe pas.");
                                    uploadTOServer( UId, nom, prenom, /* phone ,*/ email, password);

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
