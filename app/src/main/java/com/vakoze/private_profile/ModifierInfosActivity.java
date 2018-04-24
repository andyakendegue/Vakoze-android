package com.vakoze.private_profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.vakoze.R;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.Utility;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ModifierInfosActivity extends AppCompatActivity {


    private EditText nomEdit, prenomEdit, phoneEdit, passwordEdit, confirmPasswordEdit, emailEdit;
    private ProgressDialog progressDialog;
    private User user;
    private ImageView profile_pic;
    public  static final int RequestPermissionCode  = 1 ;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int REQUEST_READ_CONTACTS = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    private String userChoosenTask;
    private Bitmap bitmap;
    private Bitmap bitmapData;
    private ExifInterface exifObject;
    private String selectedImagePath;
    private Uri capturedImageUri;
    private ConstraintLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_infos);
        user = SharedPrefManager.getInstance(ModifierInfosActivity.this).getUser();


        linearLayout = findViewById(R.id.modifier_layout);

        //displayToast(user.toString());

                /*
        Initialisation des entrée de texte
         */
        nomEdit = findViewById(R.id.nomModify);
        prenomEdit = findViewById(R.id.prenomModify);
        //phoneEdit = findViewById(R.id.phoneEdit);
        emailEdit = findViewById(R.id.emailModify);

        nomEdit.setText(user.getNom());
        prenomEdit.setText(user.getPrenom());
        emailEdit.setText(user.getEmail());

        Button modifyPassword = findViewById(R.id.modifyPassword);
        Button btnImage = findViewById(R.id.btnimage);
        Button cancel = findViewById(R.id.cancel);
        Button valider = findViewById(R.id.valider);
        profile_pic = findViewById(R.id.profil);
        profile_pic.setBackgroundResource(R.mipmap.ic_logo);

        if(user.getProfile_pic()!=null||user.getProfile_pic().isEmpty()|| user.getProfile_pic()==""){
            Glide.with(ModifierInfosActivity.this)
                    .load(user.getProfile_pic())
                    //.fitCenter()
                    .into(profile_pic);
        }

        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifypassword = new Intent(ModifierInfosActivity.this, ModifyPasswordActivity.class);
                startActivity(modifypassword);
                finish();

            }
        });


        // Annuler
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ModifierInfosActivity.this);

            }
        });
        // Inscription
        valider.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptModify();

            }
        });
        // Ajout d'une photo de profil
        EnableRuntimePermissionToAccessCamera();
        btnImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //selectImage();

            }
        });

    }
    // Requesting runtime permission to access camera.

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(ModifierInfosActivity.this,
                android.Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(ModifierInfosActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(ModifierInfosActivity.this,new String[]{android.Manifest.permission.CAMERA}, RequestPermissionCode);

        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(ModifierInfosActivity.this, android.Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(ModifierInfosActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(ModifierInfosActivity.this,new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(ModifierInfosActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(ModifierInfosActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(ModifierInfosActivity.this,new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void attemptModify() {


        String UId, nom, prenom, phone, password, confirmPassword, email;

        // Reset errors.
        emailEdit.setError(null);
        //passwordEdit.setError(null);


        nom = nomEdit.getText().toString();
        prenom = prenomEdit.getText().toString();
        //confirmPassword = confirmPasswordEdit.getText().toString();
        email = emailEdit.getText().toString();
        //password = passwordEdit.getText().toString();



        boolean cancel = false;
        View focusView = null;
        View focusView1 = null;
/*
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordEdit.setError(getString(R.string.error_invalid_password));
            focusView = passwordEdit;
            cancel = true;
        }
        */

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
        /*
        if (!isPasswordSimilar(password, confirmPassword)) {
            passwordEdit.setError(getString(R.string.error_not_similar_password));
            confirmPasswordEdit.setError(getString(R.string.error_not_similar_password));
            focusView = passwordEdit;

            focusView1 = confirmPasswordEdit;


            cancel = true;
        }
*/
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
            //uploadTOServer( nom, prenom, /* phone ,*/ email, password);
            searchAccount(nom, prenom, email/*, password*/);
            //Toast.makeText(getApplicationContext(), "Enregistrement réussi"+tags+ UId+ nom+ prenom+ /* phone +*/ email+ password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isPasswordSimilar(String password, String confirmPassword) {
        //TODO: Replace this with your own logic
        return password.equals(confirmPassword);
    }



    private void uploadTOServer(final String id, final String nom, final String prenom, /* String final phone, */ final String email/*, final String password*/){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, EndPoints.UPLOAD_URL+"user/"+user.getId(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));


                            if(obj.getString("error").equalsIgnoreCase("true")) {
                                Toast.makeText(getApplicationContext(), obj.getString("Erreur :")+ obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                                //Dismiss the dialog
                                progressDialog.dismiss();
                            } else {
                                //Dismiss the dialog
                                progressDialog.dismiss();
                                NavUtils.navigateUpFromSameTask(ModifierInfosActivity.this);

                                Toast.makeText(getApplicationContext(), "Enregistrement réussi", Toast.LENGTH_LONG).show();
                                finish();


                            }


                        } catch (JSONException e) {
                            //Dismiss the dialog
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed."+e.getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), response.data.toString(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Authentication failed."+error.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("nom", nom);
                params.put("prenom", prenom);
                //params.put("phone", phone);
                params.put("email", email);
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

    private void searchAccount(final String nom, final String prenom, final String email/*, final String password*/){

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();
                            boolean emailExist=false;
                            String id = null;

                            Toast.makeText(getApplicationContext(), "search reussie.", Toast.LENGTH_LONG).show();
                            for (int i = 0; i < objLength; i++)
                            {
                                JSONObject objData=obj.getJSONObject(i);
                                if(objData.getString("email").equals(email)){
                                    Toast.makeText(getApplicationContext(), "L'email existe.", Toast.LENGTH_LONG).show();

                                    emailExist=false;
                                    id = objData.getString("id");
                                } else {
                                    Toast.makeText(getApplicationContext(), "L'email n'existe pas.", Toast.LENGTH_LONG).show();
                                    emailExist = true;
                                }

                            }

                            if(emailExist){
                                uploadTOServer(id, nom,prenom, email/*, password*/);



                            } else {

                            }





                        } catch (JSONException e) {
                            //Dismiss the dialog

                            Toast.makeText(getApplicationContext(), "Recherche impossible."+e.getMessage(), Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        Toast.makeText(getApplicationContext(), "Authentication failed."+error.getMessage(), Toast.LENGTH_LONG).show();

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

    // Image Processing

    private void selectImage() {
        final CharSequence[] items = { "Prendre une photo", "Choisir depuis le téléphone",
                "Annuler" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ModifierInfosActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(ModifierInfosActivity.this);

                if (items[item].equals("Prendre une photo")) {
                    userChoosenTask ="Prendre une photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choisir depuis le téléphone")) {
                    userChoosenTask ="Choisir depuis le téléphone";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Annuler")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Sélectionner une photo"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);

            }

            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);


            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {


        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (thumbnail != null) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri tempUri = getImageUri(getApplicationContext(), thumbnail);


        selectedImagePath = getRealPathFromURIPath(tempUri, ModifierInfosActivity.this);

        profile_pic.setImageBitmap(thumbnail);

        if(profile_pic.getDrawable() != null){
            try {
                exifObject = new ExifInterface(selectedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap imageRotate = rotateBitmap(thumbnail,orientation);
            String textInt = String.valueOf(orientation);
            profile_pic.setImageBitmap(imageRotate);
        }else{
            Toast.makeText(ModifierInfosActivity.this, "Photo non définie", Toast.LENGTH_LONG).show();
        }
        bitmap = thumbnail;




    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profile_pic.setImageBitmap(bitmap);
    }


    /**
     * uploading tasks
     */

    /*
    * The method is taking Bitmap as an argument
    * then it will return the byte[] array for the given bitmap
    * and we will send this array to the server
    * here we are using PNG Compression with 80% quality
    * you can give quality between 0 to 100
    * 0 means worse quality
    * 100 means best quality
    * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
