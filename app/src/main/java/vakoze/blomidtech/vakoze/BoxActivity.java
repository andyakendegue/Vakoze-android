package vakoze.blomidtech.vakoze;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.FullScreenMediaController;
import vakoze.blomidtech.vakoze.lib.Utility;
import vakoze.blomidtech.vakoze.lib.VolleyMultipartRequest;
import vakoze.blomidtech.vakoze.models.User;
import vakoze.blomidtech.vakoze.models.Video;


public class BoxActivity extends AppCompatActivity {
    private static final String TAG = BoxActivity.class.getSimpleName();
    private Uri uri;
    private String pathToStoredVideo, nom , tags, categories;;
    private VideoView displayRecordedVideo;
    private ImageButton like, comment, share, repost, ff, forward, sendVideoButton;
    Button addVideo;
    private static final String SERVER_PATH = "";
    private ProgressDialog progressDialog;
    private EditText nomEdit, tagsEdit;
    String receivedUri;
    Uri imageUri;
    private String filePath = null;
    long totalSize = 0;
    private File sourceFile;
    private LinearLayout ajout_video_activity;
    private Video videoInfo;
    private TextView userName, description_video, titre_video, duree_video;
    private ImageView userPic;
    private ProgressBar progressBar;
    private boolean isContinuously = false;
    private View mProgressView;
    private View mBoxView;
    private User videoUser;
    private String userChoosenTask;
    private int SELECT_FILE = 0;

    private Uri fileUri; // file url to store image/video
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private Cursor cursor = null;
    private int idx = 0;

    @Override
    protected void onStart(){
        super.onStart();
        showProgress(true);
        Bundle extras = getIntent().getExtras();
        if(searchUser(extras.getString("userId"))!=null) {
            videoUser = searchUser(extras.getString("userId"));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_box);

        mBoxView = findViewById(R.id.box_layout);
        titre_video = findViewById(R.id.video_title);
        duree_video = findViewById(R.id.video_duration);
        mProgressView = findViewById(R.id.public_profile_progress);
        videoInfo = new Video();
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null) {

            if(extras == null) {
                receivedUri= null;
            } else {

                receivedUri= extras.getString("source");
                videoInfo.setSource(extras.getString("source"));
                videoInfo.setUser_id(Long.parseLong(extras.getString("userId")));
                videoInfo.setNom(extras.getString("nom"));
                videoInfo.setTags(extras.getString("tags"));
                videoInfo.setCategorie(extras.getString("categorie"));
                videoInfo.setId(Long.parseLong(extras.getString("id")));
                videoInfo.setType(extras.getString("type"));

                titre_video.setText(extras.getString("nom"));




            }
        } else {
            receivedUri= (String) savedInstanceState.getSerializable("source");
            videoInfo.setSource((String) savedInstanceState.getSerializable("source"));
            videoInfo.setUser_id(Long.parseLong((String) savedInstanceState.getSerializable("userId")));
            videoInfo.setNom((String) savedInstanceState.getSerializable("nom"));
            videoInfo.setTags((String) savedInstanceState.getSerializable("tags"));
            videoInfo.setCategorie((String) savedInstanceState.getSerializable("categorie"));
            videoInfo.setId(Long.parseLong((String) savedInstanceState.getSerializable("id")));
            videoInfo.setType((String) savedInstanceState.getSerializable("type"));
            titre_video.setText((String) savedInstanceState.getSerializable("nom"));

             }



        userName = findViewById(R.id.userName);
        //
        userPic = findViewById(R.id.userPic);


        ajout_video_activity = findViewById(R.id.ajout_video_activity);


        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }



        displayRecordedVideo = findViewById(R.id.video_display);


        //displayRecordedVideo.setVideoPath(receivedUri);
        displayRecordedVideo.setVideoURI(Uri.parse("http://"+receivedUri));
        //displayRecordedVideo.setUp(receivedUri,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, receivedUri.substring(receivedUri.lastIndexOf("/")+1));
        displayRecordedVideo.seekTo(100);
        MediaController mediaController = new FullScreenMediaController(this);
        mediaController.setAnchorView(displayRecordedVideo);
        if(isLandScape()){
            mediaController = new FullScreenMediaController(this);
        }else {
            mediaController = new MediaController(this);
        }

        displayRecordedVideo.setMediaController(mediaController);
        displayRecordedVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long duration = displayRecordedVideo.getDuration();
                duree_video.setText(String.valueOf(duration));
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.public_video_progress);
        displayRecordedVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                displayRecordedVideo.start();
                if(isContinuously){

                }
            }
        });
        displayRecordedVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
            }
        });





        // start playing
        addVideo = findViewById(R.id.add_video_public_profile);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent i = new Intent(BoxActivity.this, LoginActivity.class);

               // startActivity(i);
                selectImage();

            }
        });
        like = findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //like.setImageIcon(R.drawable.ic_like_inactive);
                //like.setBackgroundResource();
                like.setImageResource(R.drawable.ic_like_inactive);

            }
        });

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        repost = findViewById(R.id.repost);
        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
/*
 Spinner categorieVideo = findViewById(R.id.categorieVideo);
        String[] arraySpinner = new String[] {
                "Humour", "Politique", "Education", "Ecologie", "Communauté"
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
                        categories = "Communauté";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categories = "Communauté";

            }
        });



        rew = findViewById(R.id.rew);
        rew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayRecordedVideo.seekTo(0);
                //displayRecordedVideo.start();

            }
        });
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayRecordedVideo.pause();
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
        sendVideoButton = findViewById(R.id.send);
        sendVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nom = nomEdit.getText().toString();
                tags = tagsEdit.getText().toString();
                if(nom.equals("")||tags.equals("")||categories.equals("")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BoxActivity.this);
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
                    //uploadVideos(nom, tags, categories);
                    //uploadMp4(nom, tags, categories);
                }

            }
        });
*/

    }

    private boolean isLandScape(){
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();

        if (rotation == Surface.ROTATION_90
                || rotation == Surface.ROTATION_270) {
            return true;
        }
        return false;
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

            mBoxView.setVisibility(show ? View.GONE : View.VISIBLE);
            mBoxView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBoxView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mBoxView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public User searchUser(final String UserId){
        final User[] user = new User[1];



        //user = new User();

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
                                Long id = objData.getLong("id");
                                if(id == Long.parseLong(UserId)){


                                    user[0] = new User(Integer.parseInt(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));


                                }

                            }

                            userName.setText(user[0].getNom());

        if(user[0].getProfile_pic()!=null||!user[0].getProfile_pic().isEmpty()){
            Glide.with(BoxActivity.this)
                    .load(user[0].getProfile_pic())
                    //.fitCenter()
                    .into(userPic);
        } else {
            //profile_pic.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_square);
            userPic.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }






                            showProgress(false);



                        } catch (JSONException e) {
                            //Dismiss the dialog
                            showProgress(false);


                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        //Dismiss the dialog


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
        Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);


        if(user[0] == null){
            return null;
        } else {
            return user[0];
        }

    }

    // Video Taking Actions

    private void selectImage() {
        final CharSequence[] items = { "Prendre une video", "Choisir depuis le téléphone",
                "Annuler" };

        AlertDialog.Builder builder = new AlertDialog.Builder(BoxActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(BoxActivity.this);

                if (items[item].equals("Prendre une video")) {
                    userChoosenTask ="Prendre une video";
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
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }
        else
        {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Sélectionner une video"),SELECT_FILE);
    }

    private void cameraIntent()
    {

        Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);


        // set video quality
        videoCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // name
        if(videoCaptureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(videoCaptureIntent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                //onSelectFromGalleryResult(data);
                String selectedPath = getPath(data.getData());
                String videoType =data.getType();

                Intent i = new Intent(BoxActivity.this, AjoutVideoActivity.class);
                i.putExtra("filePath", selectedPath);
                //i.putExtra("fileUri", videoToSend);
                i.putExtra("type", "gallery");
                i.putExtra("videoType", videoType);
                startActivity(i);
            }

            else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                onCaptureVideoResult(data);
            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {


        }
    }
    private void onCaptureVideoResult(Intent data) {

        Uri videoToSend = data.getData();

        String videoType =data.getType();

        Intent i = new Intent(BoxActivity.this, AjoutVideoActivity.class);
        i.putExtra("filePath", getRealPathFromURIPath(videoToSend, BoxActivity.this));

        i.putExtra("type", "capture");
        i.putExtra("videoType", videoType);
        startActivity(i);


    }


    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {

        cursor = null;
        String[] projection = { MediaStore.Video.Media.DATA };
        cursor = getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            //return null;
            return contentURI.getPath();
        }
    }
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }
}
