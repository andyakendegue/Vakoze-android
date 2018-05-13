package com.vakoze.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.vakoze.R;
import com.vakoze.authentication.LoginActivity;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;
import com.vakoze.save.MainActivity;

import net.qiujuer.genius.graphics.Blur;


public class AjoutVideoActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Uri uri;
    private String pathToStoredVideo, nom , tags, categories;;
    private VideoView displayRecordedVideo;
    private ImageButton previous, rew, play, pause, ff, forward, sendVideoButton;
    private ImageView imgBlur, imgNormal;
    private Bitmap thumb, overlay;
    private static final String SERVER_PATH = "";
    // Taille maximale du téléchargement

    public final static int MAX_SIZE = 100;

    // Identifiant de la boîte de dialogue

    public final static int ID_DIALOG = 0;
    private uploadVideoToServer mProgress = null;
    private static ProgressDialog progressDialog;
    private EditText nomEdit, tagsEdit;
    String receivedUri, fileType;
    Uri imageUri;
    private String filePath = null;
    long totalSize = 0;
    private File sourceFile;
    private ScrollView ajout_video_activity;
    private int[] pix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                receivedUri= null;
            } else {

                receivedUri= extras.getString("filePath");
                fileType= extras.getString("videoType");

            }
        } else {
            receivedUri= (String) savedInstanceState.getSerializable("filePath");
            fileType= (String) savedInstanceState.getSerializable("videoType");
        }
        setContentView(R.layout.activity_ajout_video);
        ajout_video_activity = findViewById(R.id.ajout_video_activity);
        nomEdit = findViewById(R.id.nomVideo);

        tagsEdit = findViewById(R.id.tagsVideo);
        tagsEdit.setText(fileType);


        displayRecordedVideo = findViewById(R.id.video_display);
        Spinner categorieVideo = findViewById(R.id.categorieVideo);
        String[] arraySpinner = new String[] {
                "Humour", "Politique", "Education", "Ecologie", "Amour"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        categorieVideo.setAdapter(adapter);
        categorieVideo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position) {

                    case 0:
                        categories = "Humour";
                    case 1:
                        categories = "Politique";
                    case 2:
                        categories = "Education";
                    case 3:
                        categories = "Ecologie";
                    case 4:
                        categories = "Amour";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categories = "Humour";

            }
        });



        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(displayRecordedVideo);
        displayRecordedVideo.setMediaController(mediaController);
        displayRecordedVideo.setVideoPath(receivedUri);
        //displayRecordedVideo.setUp(receivedUri,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, receivedUri.substring(receivedUri.lastIndexOf("/")+1));
        displayRecordedVideo.seekTo(100);
        imgNormal = findViewById(R.id.imgNormal);
        imgBlur = findViewById(R.id.imgBlur);

        thumb = ThumbnailUtils.createVideoThumbnail(getVideoFilePath(), MediaStore.Video.Thumbnails.MINI_KIND);

        if (thumb == null) {
            //videoFilePreview.setVisibility(View.INVISIBLE);
        } else {

            imgNormal.setImageBitmap(thumb);
            overlay = thumb.copy(thumb.getConfig(), true);


            int w = overlay.getWidth();
            int h = overlay.getHeight();
            int[] pix = new int[w * h];
            overlay.getPixels(pix, 0, w, 0, 0, w, h);



            pix = Blur.onStackBlurPixels(pix, w, h, (int) 3);
            overlay.setPixels(pix, 0, w, 0, 0, w, h);
// Bitmap JNI Native

            overlay = Blur.onStackBlur(overlay, (int) 20);
            imgBlur.setImageBitmap(overlay);


        }



        // start playing
/*
        previous = findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        rew = findViewById(R.id.rew);
        rew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ff = findViewById(R.id.ff);
        ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        forward = findViewById(R.id.next);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRecordedVideo.seekTo(0);
                displayRecordedVideo.start();

            }
        });
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayRecordedVideo.pause();
            }
        });

        sendVideoButton = findViewById(R.id.send);
        sendVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = nomEdit.getText().toString();
                tags = tagsEdit.getText().toString();
                User user = SharedPrefManager.getInstance(AjoutVideoActivity.this).getUser();
                if(nom.equals("")||tags.equals("")||categories.equals("")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AjoutVideoActivity.this);
                    builder.setTitle("Complétez les informations!");
                    builder.setMessage("Vous n'avez pas saisi toutes les informations de la vidéo");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(true);
                    builder.show();

                } else {

                    //new uploadVideoToServer(nom, tags, categories, receivedUri, user, AjoutVideoActivity.this).execute();

                    if(thumb != null&& overlay !=null) {
                        uploadMp4(nom, tags, overlay, thumb, categories);
                    } else {
                        final CharSequence[] items = { "OK" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AjoutVideoActivity.this);
                        builder.setTitle("Aucune image!");
                        builder.setMessage("Aucune image n\'a été générée.");
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
        });

        //Toast.makeText(AjoutVideoActivity.this, receivedUri, Toast.LENGTH_LONG).show();
        Log.d("Filepath", receivedUri);



    }

    private File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    public String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/capture.mp4";
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("videoType", fileType);
        outState.putSerializable("filePath", receivedUri);
    }


    private void setVideo(Uri videoUri){
        //displayRecordedVideo.setVideoURI(videoUri);
        //displayRecordedVideo.start();

        pathToStoredVideo = getRealPathFromURIPath(videoUri, AjoutVideoActivity.this);
        Log.d(TAG, "Recorded Video Path " + pathToStoredVideo);
        //Store the video to your server
        //uploadVideoToServer(pathToStoredVideo);
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

    private class uploadVideoToServer extends AsyncTask<Void, Integer, Boolean>{

        private final User mUser;
        // Référence faible à l'activité
        private WeakReference<AjoutVideoActivity> mActivity = null;
        // Progression du téléchargement
        private int mProgression = 0;
        String mNom, mTags, mCategories, mUri;
        byte[] videoToSend;
        Long userId;
        boolean retour = false;
        String message;
        //getting the current user
        //User user = SharedPrefManager.getInstance(getActivity()).getUser();




        public uploadVideoToServer(String nom, String tags, String categories, String uri, User user, AjoutVideoActivity pActivity) {
            this.mNom = nom;
            this.mTags = tags;
            this.mCategories = categories;
            this.mUri = uri;
            this.mUser = user;
            link(pActivity);
        }



        @Override

        protected void onPreExecute () {
            videoToSend = videoBytes(mUri);
            userId = mUser.getId();

            // Au lancement, on affiche la boîte de dialogue
            if(mActivity.get() != null)

                //mActivity.get().showDialog(ID_DIALOG);
                loadProgress();

        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/video/add",
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {

                            JSONObject obj = null;
                            try {
                                obj= new JSONObject(new String(response.data));
                                if(!obj.getBoolean("error")){
                                    Intent intent = new Intent(AjoutVideoActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    retour = true;
                                } else {
                                    message = "Server"+obj.getString("message");
                                    retour = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                message = "Catch"+e.getMessage();
                                retour = false;
                            }
                            //String obj = new String(response.data);

                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Dismiss the dialog
                            //progressDialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "Erreur reponse serveur "+error, Toast.LENGTH_LONG).show();
                            retour = false;
                            message = "request"+error.getMessage();

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
                    params.put("user_id", String.valueOf(userId));
                    params.put("description", mTags);
                    params.put("nom", mNom);
                    params.put("categorie", mCategories);
                    params.put("tags", mNom);
                    params.put("type", "mp4");
                    return params;
                }

                /*
                * Here we are passing image by renaming it with a unique name
                * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    final DataPart file = new DataPart(imagename + ".mp4", videoToSend);
                    params.put("file", file);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "cent:capp7622argent";
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    headers.put("Authorization", auth);
                    return headers;
                }
            };

            //adding the request to volley

            Volley.newRequestQueue(AjoutVideoActivity.this).add(volleyMultipartRequest);
            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return retour;
        }

        @Override

        protected void onPostExecute (Boolean result) {
            progressDialog.dismiss();

            if (mActivity.get() != null) {

                if(result)

                    displayToast("Téléchargement terminé");



                else

                    displayToast("Echec du téléchargement"+message);

            }

        }
        @Override

        protected void onProgressUpdate (Integer... prog) {

            // À chaque avancement du téléchargement, on met à jour la boîte de dialogue

            if (mActivity.get() != null)

                mActivity.get().updateProgress(prog[0]);

        }


        @Override

        protected void onCancelled () {

            if(mActivity.get() != null)

                //Toast.makeText(mActivity.get(), "Annulation du téléchargement", Toast.LENGTH_SHORT).show();
                displayToast("Annulation du téléchargement");

        }


        public void link (AjoutVideoActivity pActivity) {

            mActivity = new WeakReference<AjoutVideoActivity>(pActivity);

        }


        public int download() {

            if(mProgression <= MAX_SIZE) {

                mProgression++;

                return mProgression;

            }

            return MAX_SIZE;

        }
    }



    private void uploadMp4(final String nom, final String tags, final Bitmap blur, final Bitmap normal, final String categorie) {

        loadProgress();

        //getting the current user
        User user = SharedPrefManager.getInstance(AjoutVideoActivity.this).getUser();
        final Long userId = user.getId();

        final byte[] videoToSend = videoBytes(receivedUri);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/video/add",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                            //JSONObject obj = new JSONObject(new String(response.data));
                        /*
                            String obj = new String(response.data);


                            if(!obj.equalsIgnoreCase("saved")) {
                                Toast.makeText(getApplicationContext(), "Impossible d'enregistrer la vidéo", Toast.LENGTH_LONG).show();
                                //Dismiss the dialog

                                progressDialog.dismiss();
                            } else {
                                //Dismiss the dialog
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Enregistrement réussi", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AjoutVideoActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            */
                        JSONObject obj = null;
                        progressDialog.dismiss();
                        try {
                            obj= new JSONObject(new String(response.data));
                            if(!obj.getBoolean("error")){
                                displayToast("Upload Réussi");
                                Intent intent = new Intent(AjoutVideoActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                displayToast("Upload Non Réussi :"+obj.getString("message"));
                                Log.e("VakoError: Upload error",obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("erreur"+e.getMessage());
                            Log.e("VakoError : "+e.getMessage(),new String(response.data));

                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        progressDialog.dismiss();
                        displayToast("Upload Non Réussi"+error.getMessage());
                        //Log.e("VakoError :Volley Error",error.getMessage());
                    }
                }) {

            ///
            // If you want to add more parameters with the image
            // you can do it here
            // here we have only one parameter with the image
            // which is tags
            ///
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("tags", tags);
                params.put("nom", nom);
                params.put("categorie", categorie);
                params.put("description", tags);
                params.put("type", "mp4");
                params.put("img_type", "png");
                return params;
            }

            ///
            // Here we are passing image by renaming it with a unique name
            ///
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                final DataPart file = new DataPart(imagename + ".mp4", videoToSend);
                params.put("file", file);
                params.put("file", new DataPart("blur_"+imagename + ".png", getFileDataFromDrawable(blur)));
                params.put("file", new DataPart("normal_"+imagename + ".png", getFileDataFromDrawable(normal)));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cent:capp7622argent";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "multipart/form-data");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void loadProgress(){
        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();
        progressDialog = new ProgressDialog(this);
        // Setting Title
        progressDialog.setTitle("Enregistrement de la vidéo");
        // Setting Message
        progressDialog.setMessage("Chargement...");
        // Progress Dialog Style Horizontal
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Progress Dialog Style Spinner
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // Progress Dialog Max Value
        //progressDialog.setMax(100);
        // Fetching max value
        //progressDialog.getMax();
        // Fetching current progress
       //progressDialog.getProgress();
        // Incremented By Value 2
        //progressDialog.incrementProgressBy(2);
        // Cannot Cancel Progress Dialog
        progressDialog.setCancelable(false);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override

            public void onCancel(DialogInterface arg0) {

                progressDialog.cancel();

            }

        });
        progressDialog.show();
    }

    /*
    @Override
    public Object onRetainNonConfigurationInstance() {

        return mProgress;

    }
    */


    // Met à jour l'avancement dans la boîte de dialogue

    void updateProgress(int progress) {

        progressDialog.setProgress(progress);

    }
    public void displayToast(String message){
        Snackbar snackbar = Snackbar
                .make(ajout_video_activity, message, Snackbar.LENGTH_LONG)
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



    private static byte[] videoBytes(String receivedUri) {



        byte[] videoByte;
        int bytesRead, bytesAvailable, bufferSize;

        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(receivedUri);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            //videoByte = new byte[bufferSize];
            videoByte = readBytes(fileInputStream);
            return videoByte;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }




    }
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }



}
