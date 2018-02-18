package vakoze.blomidtech.vakoze;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.Utility;
import vakoze.blomidtech.vakoze.lib.VolleyMultipartRequest;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SignupActivity extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int REQUEST_READ_CONTACTS = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    private ImageView profil;
    private EditText nomEdit, prenomEdit, phoneEdit, passwordEdit, confirmPasswordEdit, emailEdit;
    public  static final int RequestPermissionCode  = 1 ;
    private String userChoosenTask;
    private Bitmap bitmap;
    private Bitmap bitmapData;
    private ExifInterface exifObject;
    private String selectedImagePath;
    private Uri capturedImageUri;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        /*
        Initialisation des entrée de texte
         */
        nomEdit = findViewById(R.id.nomEdit);
        prenomEdit = findViewById(R.id.prenomEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        emailEdit = findViewById(R.id.emailEdit);


        /*
          initialisation des boutons
         */
        // Initialisation des boutons
        profil = findViewById(R.id.profil);
        Button btnImage = findViewById(R.id.btnimage);
        Button cancel = findViewById(R.id.cancel);
        Button valider = findViewById(R.id.valider);


        // Ajout d'une photo de profil
        EnableRuntimePermissionToAccessCamera();
        //checking the permission
        //if the permission is not given we will open setting to add permission
        //else app will not open
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }*/

        btnImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                selectImage();

            }
        });

        // Annuler
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(SignupActivity.this);

            }
        });
        // Inscription
        valider.setOnClickListener(new View.OnClickListener() {
            String nom, prenom, phone, password, confirmPassword, email;
            public void onClick(View v) {
                nom = nomEdit.getText().toString();
                prenom = prenomEdit.getText().toString();
                phone = phoneEdit.getText().toString();
                password = passwordEdit.getText().toString();
                confirmPassword = confirmPasswordEdit.getText().toString();
                email = emailEdit.getText().toString();

                if(nom.isEmpty() || prenom.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {

                    final CharSequence[] items = { "OK" };
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle("Informations manquantes!");
                    builder.setMessage("Certains champs sont vides. \n Vous devez remplir tout le formulaire pour pouvoir terminer l\'inscription.");
                    builder.setCancelable(true);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();;
                        }


                    });

                    builder.show();
                } else {
                    if(password.length()< 6) {
                        final CharSequence[] items = { "OK" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                        builder.setTitle("Mot de passe trop court!");
                        builder.setMessage("Vous devez saisir un mot de passe de 6 caractères au moins.");
                        builder.setCancelable(true);
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();;
                            }


                        });

                        builder.show();
                    } else {
                        if(!password.equals(confirmPassword)) {
                            final CharSequence[] items = { "OK" };
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            builder.setTitle("Mots de passe différents!");
                            builder.setMessage("Les mots de passe sont différents. Vous devez mettre le même mot de passe.");
                            builder.setCancelable(true);
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();;
                                }


                            });

                            builder.show();

                        } else {

                            if(bitmap != null) {


                                uploadBitmap(bitmap, nom, prenom, phone, email, password);
                            } else {
                                final CharSequence[] items = { "OK" };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setTitle("Aucune Photo!");
                                builder.setMessage("Aucune Photo n\'a été sélectionnée. \n Vous devez vous prendre en photo ou sélectionner une photo");
                                builder.setCancelable(true);
                                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();;
                                    }


                                });

                                builder.show();
                            }

                        }

                    }




                }






            }
        });
    }

    // Requesting runtime permission to access camera.

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(SignupActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(SignupActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this, Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(SignupActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(SignupActivity.this,new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(SignupActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(SignupActivity.this,new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Prendre une photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choisir depuis le téléphone"))
                        galleryIntent();
                } else {
                    //code for deny
                    Log.d("Permission Error", "Erreur de permission");
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Prendre une photo", "Choisir depuis le téléphone",
                "Annuler" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(SignupActivity.this);

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


        selectedImagePath = getRealPathFromURIPath(tempUri, SignupActivity.this);

        profil.setImageBitmap(thumbnail);

        if(profil.getDrawable() != null){
            try {
                exifObject = new ExifInterface(selectedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exifObject.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap imageRotate = rotateBitmap(thumbnail,orientation);
            String textInt = String.valueOf(orientation);
            profil.setImageBitmap(imageRotate);
        }else{
            Toast.makeText(SignupActivity.this, "Photo non définie", Toast.LENGTH_LONG).show();
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

        profil.setImageBitmap(bitmap);
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

    private void uploadBitmap(final Bitmap bitmap, final String nom, final String prenom, final String phone, final String email, final String password) {

        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();
        final String tags = "capp";
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

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));


                            if(obj.getString("error").equalsIgnoreCase("true")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                //Dismiss the dialog
                                progressDialog.dismiss();
                            } else {
                                //Dismiss the dialog
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Enregistrement réussi", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }


                        } catch (JSONException e) {
                            //Dismiss the dialog
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("tags", tags);
                params.put("tag", "register");
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("phone", phone);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique namex
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }



}
