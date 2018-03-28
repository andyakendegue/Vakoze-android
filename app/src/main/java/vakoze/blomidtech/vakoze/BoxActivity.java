package vakoze.blomidtech.vakoze;

import android.*;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import vakoze.blomidtech.vakoze.fragments.RepostFragment;
import vakoze.blomidtech.vakoze.fragments.ShareFragment;
import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.FullScreenMediaController;
import vakoze.blomidtech.vakoze.lib.SharedPrefManager;
import vakoze.blomidtech.vakoze.lib.Utility;
import vakoze.blomidtech.vakoze.lib.VolleyMultipartRequest;
import vakoze.blomidtech.vakoze.models.User;
import vakoze.blomidtech.vakoze.models.Video;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static vakoze.blomidtech.vakoze.SplashActivity.RequestPermissionCode;


public class BoxActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private String userChoosenTask;
    private int SELECT_FILE = 0;

    private Uri fileUri; // file url to store image/video
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private Cursor cursor = null;
    private int idx = 0;
    private static final String TAG = PublicBoxActivity.class.getSimpleName();
    private Uri uri;
    private String pathToStoredVideo, nom , tags, categories;;
    private VideoView displayRecordedVideo;
    private ImageButton like, comment, share, repost, ff, forward, sendVideoButton, hideBtn;
    Button addVideo, follow;
    private static final String SERVER_PATH = "";
    private ProgressDialog progressDialog;
    private EditText nomEdit, tagsEdit;
    String receivedUri;
    Uri imageUri;
    private String filePath = null;
    long totalSize = 0;
    private File sourceFile;
    private Video videoInfo;
    private TextView userName, description_video, titre_video, duree_video, curLocPos, videoDuration;
    private ImageView userPic;
    private ProgressBar progressBar;
    private boolean isContinuously = false;
    private View mProgressView;
    private ScrollView mBoxView;
    private User videoUser;
    RelativeLayout layoutBox;
    LinearLayout buttonBox;
    Long userid, videoid, profileid;
    boolean hasLike, hasFollow;
    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private ToggleButton playPauseButton;
    private SeekBar musicSeekBar;
    private int percentageBuffer;
    private boolean musicThreadFinished;
    private boolean isLayoutHidden;

    @Override
    protected void onStart(){
        super.onStart();
        showProgress(true);
        Bundle extras = getIntent().getExtras();
        userid = SharedPrefManager.getInstance(BoxActivity.this).getUser().getId();
        profileid = Long.parseLong(extras.getString("userId"));
        videoid = Long.parseLong(extras.getString("id"));
        if(profileid!=null) {
            videoUser = searchUser(profileid);
            searchUserHasLike(userid, videoid);
            searchUserHasFollow(userid, profileid);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_box);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        layoutBox = findViewById(R.id.layout_box_activity);
        playPauseButton = findViewById(R.id.playPauseButton);
        hideBtn = findViewById(R.id.hideBtn);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLayoutHidden){
                    Log.e("VakoError layout: ", "Shown");
                    showLayout();
                } else {
                    Log.e("VakoError layout: ", "Hidden");
                    hideLayout();
                }

            }
        });
        musicSeekBar = findViewById(R.id.musicSeekBar);
        musicSeekBar.setOnSeekBarChangeListener(this);
        curLocPos = findViewById(R.id.musicCurrentLoc);
        videoDuration = findViewById(R.id.musicDuration);
        mBoxView = findViewById(R.id.box_layout);
        buttonBox = findViewById(R.id.buttonBox);
        titre_video = findViewById(R.id.video_title);
        description_video = findViewById(R.id.videoDescription);
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
                videoInfo.setDescription(extras.getString("description"));
                videoInfo.setId(Long.parseLong(extras.getString("id")));
                videoInfo.setType(extras.getString("type"));
                titre_video.setText(extras.getString("nom"));
                description_video.setText(extras.getString("description"));
            }
        } else {
            receivedUri= (String) savedInstanceState.getSerializable("source");
            videoInfo.setSource((String) savedInstanceState.getSerializable("source"));
            videoInfo.setUser_id((Long) savedInstanceState.getSerializable("userId"));
            videoInfo.setNom((String) savedInstanceState.getSerializable("nom"));
            videoInfo.setTags((String) savedInstanceState.getSerializable("tags"));
            videoInfo.setCategorie((String) savedInstanceState.getSerializable("categorie"));
            videoInfo.setDescription((String) savedInstanceState.getSerializable("description"));
            videoInfo.setId((Long) savedInstanceState.getSerializable("id"));
            videoInfo.setType((String) savedInstanceState.getSerializable("type"));
            titre_video.setText((String) savedInstanceState.getSerializable("nom"));
            description_video.setText((String) savedInstanceState.getSerializable("description"));
        }

        userName = findViewById(R.id.userName);
        //
        userPic = findViewById(R.id.userPic);

        mSurfaceView = (SurfaceView)findViewById(R.id.box_surface_view);

        mSurfaceView.setOnTouchListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(BoxActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.public_video_progress);
/*
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
        });*/

        // start playing
        addVideo = findViewById(R.id.add_video_public_profile);
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        follow = findViewById(R.id.follow);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasFollow) {
                    new followUser(userid,profileid).execute();
                } else {
                    new unfollowUser(userid).execute();
                }
            }
        });
        like = findViewById(R.id.like);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasLike) {
                    new like(userid,videoid).execute();
                } else {
                    new dislike(videoid).execute();
                }
            }
        });

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(BoxActivity.this, LoginActivity.class);
                //startActivity(i);
            }
        });

        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("source", videoInfo.getSource());
                args.putLong("user_id", videoInfo.getUser_id());
                args.putString("nom", videoInfo.getNom());
                args.putString("tags", videoInfo.getTags());
                args.putString("categorie", videoInfo.getCategorie());
                args.putLong("id", videoInfo.getId());
                args.putString("type", videoInfo.getType());
                DialogFragment newFragment = new ShareFragment();
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "repost");
            }
        });

        repost = findViewById(R.id.repost);
        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("source", videoInfo.getSource());
                args.putLong("user_id", videoInfo.getUser_id());
                args.putString("nom", videoInfo.getNom());
                args.putString("tags", videoInfo.getTags());
                args.putString("categorie", videoInfo.getCategorie());
                args.putLong("id", videoInfo.getId());
                args.putString("type", videoInfo.getType());
                DialogFragment newFragment = new RepostFragment();
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "repost");
            }
        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("source", videoInfo.getSource());
        outState.putSerializable("userId", videoInfo.getUser_id());
        outState.putSerializable("nom", videoInfo.getNom());
        outState.putSerializable("tags", videoInfo.getTags());
        outState.putSerializable("categorie", videoInfo.getCategorie());
        outState.putSerializable("description", videoInfo.getDescription());
        outState.putSerializable("id", videoInfo.getId());
        outState.putSerializable("type", videoInfo.getType());
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

    public User searchUser(final Long UserId){
        final User[] user = new User[1];

        //user = new User();

        //our custom volley request
        JsonObjectRequest volleyMultipartRequest = new JsonObjectRequest(Request.Method.GET,  EndPoints.UPLOAD_URL+"/user/"+UserId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {/*
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
                            */

                            if(response.getString("user")!=null){
                                JSONObject obj = new JSONObject(response.getString("user"));

                                user[0] = new User(Long.parseLong(obj.getString("id")), obj.getString("u_id"), obj.getString("nom"), obj.getString("prenom"), obj.getString("email"), obj.getString("phone"), obj.getString("profile_pic"));

                                userName.setText(user[0].getNom());

                                if(user[0].getProfile_pic()!=null&&!user[0].getProfile_pic().isEmpty()&&user[0].getProfile_pic().equals("")){
                                    Glide.with(BoxActivity.this)
                                            .load(user[0].getProfile_pic())
                                            //.fitCenter()
                                            .into(userPic);
                                } else {
                                    userPic.setImageResource(R.drawable.profile_pic);
                                }

                            }


                        } catch (JSONException e) {
                            //Dismiss the dialog
                            e.printStackTrace();
                            displayToast("VakoError: JsonException Error - "+e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        displayToast( "VakoError: Response Error - "+error.getMessage());
                        //Dismiss the dialog

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cent:capp7622argent";
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
                boolean cameraRequest = EnableRuntimePermissionToAccessCamera();

                if (items[item].equals("Prendre une video")) {
                    userChoosenTask ="Prendre une video";

                    if(cameraRequest)
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
                //Intent i = new Intent(BoxActivity.this, AjoutVideoActivity.class);
                Intent i = new Intent(BoxActivity.this, EditActivity.class);
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

        //Intent i = new Intent(BoxActivity.this, AjoutVideoActivity.class);
        Intent i = new Intent(BoxActivity.this, EditActivity.class);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDisplay(mSurfaceHolder);
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                percentageBuffer = percent;
            }
        });

        try {
            mMediaPlayer.setDataSource(String.valueOf(Uri.parse("http://"+receivedUri)));
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(100);
            mMediaPlayer.setOnPreparedListener(BoxActivity.this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            long duration = mMediaPlayer.getDuration();
            duree_video.setText(String.valueOf(duration));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideLayout() {
        hideSystemUI();
        mBoxView.setVisibility(View.GONE);
        for ( int i = 0; i < mBoxView.getChildCount();  i++ ){
            View view = mBoxView.getChildAt(i);
            view.setVisibility(View.GONE);
        }
        buttonBox.setVisibility(View.GONE);
        addVideo.setVisibility(View.GONE);
        isLayoutHidden = false;
    }
    private void showLayout() {

        mBoxView.setVisibility(View.VISIBLE);
        for ( int i = 0; i < mBoxView.getChildCount();  i++ ){
            View view = mBoxView.getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }

        buttonBox.setVisibility(View.VISIBLE);
        addVideo.setVisibility(View.VISIBLE);
        isLayoutHidden = true;

    }
    private void layoutMedia(int currentPosition) {

        if(currentPosition == 1500){
            hideLayout();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        progressBar.setVisibility(View.GONE);
        mMediaPlayer.start();
        hideLayout();
        playPauseButton.setChecked(false);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on clicks
                if (playPauseButton.isChecked()) { // Checked - Pause icon visible
                    mMediaPlayer.start();
                } else { // Unchecked - Play icon visible
                    mMediaPlayer.pause();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;

                while (!musicThreadFinished) {

                    try {
                        Thread.sleep(1000);
                        currentPosition = mMediaPlayer.getCurrentPosition();
                        //layoutMedia(currentPosition);

                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }

                    final int total = mMediaPlayer.getDuration();
                    final String totalTime = getAsTime(total);
                    final String curTime = getAsTime(currentPosition);

                    musicSeekBar.setMax(total); //song duration
                    musicSeekBar.setProgress(currentPosition);  //for current song progress
                    //musicSeekBar.setSecondaryProgress(getBufferPercentage());   // for buffer progress
                    musicSeekBar.setSecondaryProgress(percentageBuffer);   // for buffer progress
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isPlaying()) {
                                if (!playPauseButton.isChecked()) {
                                    playPauseButton.setChecked(true);
                                    playPauseButton.setTextOff("");
                                    playPauseButton.setTextOff("");
                                }
                            } else {
                                if (playPauseButton.isChecked()) {
                                    playPauseButton.setChecked(false);
                                    playPauseButton.setTextOff("");
                                }
                            }

                            videoDuration.setText(totalTime);
                            curLocPos.setText(curTime);
                            duree_video.setText(totalTime);
                        }
                    });
                    if(total == currentPosition){
                        musicThreadFinished = true;

                        try {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    showSystemUI();
                                    showLayout();
                                }
                            });
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        musicThreadFinished = false;
                    }
                }
            }
        }).start();
    }
    private String getAsTime(int total) {
        return String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(total),
                TimeUnit.MILLISECONDS.toSeconds(total) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(total)));
    }
    private boolean isPlaying() {

        if(mMediaPlayer.isPlaying()){
            return true;
        } else {
            return false;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(playPauseButton.getVisibility()== View.GONE){
            playPauseButton.setVisibility(View.VISIBLE);

        } else if(playPauseButton.getVisibility()== View.VISIBLE) {
            playPauseButton.setVisibility(View.GONE);

        }

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        layoutBox.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        /*layoutBox.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_VISIBLE);*/
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        layoutBox.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            layoutBox.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/


    public class like extends AsyncTask<Void, Void, Boolean> {
        private final Long mUserId;
        private final Long mVideoId;
        private boolean status = false;

        like(Long userId, Long videoId) {
            mUserId = userId;
            mVideoId = videoId;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            StringRequest volleyMultipartRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/video_like/add",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {



                            JSONObject objData = null;
                            try {

                                objData = new JSONObject(response);
                                if (!objData.getBoolean("error")) {
                                    //Dismiss the dialog

                                    displayToast("Enregistrement réussi");
                                    like.setImageResource(R.drawable.ic_like_active);

                                } else if(objData.getBoolean("error")){
                                    displayToast("Impossible d'aimer la video");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayToast("Impossible d'aimer la video");
                            }




                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Dismiss the dialog

                            displayToast("Impossible d'aimer la video");

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
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(mUserId));
                    params.put("video_id", String.valueOf(mVideoId));
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "cent:capp7622argent";
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    //headers.put("Content-Type", "multipart/form-data");
                    //headers.put("Content-Type", "multipart/form-data");
                    headers.put("Authorization", auth);
                    return headers;
                }

            };

            //adding the request to volley
            Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);


            return null;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }

    public class dislike extends AsyncTask<Void, Void, Boolean> {
        private final Long mVideoId;

        dislike(Long videoId) {
            mVideoId = videoId;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            StringRequest volleyMultipartRequest = new StringRequest(Request.Method.DELETE, EndPoints.UPLOAD_URL+"/video_like/delete/"+mVideoId,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {



                            JSONObject objData = null;
                            try {

                                objData = new JSONObject(response);
                                if (!objData.getBoolean("error")) {
                                    //Dismiss the dialog

                                    hasLike=false;
                                    like.setImageResource(R.drawable.ic_like_inactive);

                                } else if(objData.getBoolean("error")){
                                    hasLike=true;
                                    like.setImageResource(R.drawable.ic_like_active);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayToast("Impossible d'aimer la video");
                            }




                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Dismiss the dialog

                            displayToast("Impossible d'aimer la video");

                        }
                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "cent:capp7622argent";
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    //headers.put("Content-Type", "multipart/form-data");
                    //headers.put("Content-Type", "multipart/form-data");
                    headers.put("Authorization", auth);
                    return headers;
                }

            };

            //adding the request to volley
            Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);


            return null;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }
    private void searchUserHasLike(final Long UserId, final Long VideoId){

        StringRequest volleyMultipartRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/video_like/userHasLike",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        showProgress(false);



                        JSONObject objData = null;
                        try {

                            objData = new JSONObject(response);
                            if (!objData.getBoolean("error")) {
                                //Dismiss the dialog
                                hasLike = true;
                                like.setImageResource(R.drawable.ic_like_active);



                            } else if(objData.getBoolean("error")){
                                hasLike = false;
                                like.setImageResource(R.drawable.ic_like_inactive);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("Impossible de récupérer le satut du like de la video");
                        }




                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        showProgress(false);

                        displayToast("Impossible d'aimer la video");

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(UserId));
                params.put("video_id", String.valueOf(VideoId));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cent:capp7622argent";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "multipart/form-data");
                //headers.put("Content-Type", "multipart/form-data");
                headers.put("Authorization", auth);
                return headers;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);

    }

    public class followUser extends AsyncTask<Void, Void, Boolean> {
        private final Long mUserId;
        private final Long mProfileId;
        private boolean status = false;

        followUser(Long userId, Long profileId) {
            mUserId = userId;
            mProfileId = profileId;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            StringRequest volleyMultipartRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/fans/add",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {



                            JSONObject objData = null;
                            try {

                                objData = new JSONObject(response);
                                if (!objData.getBoolean("error")) {
                                    //Dismiss the dialog

                                    displayToast("Enregistrement réussi");
                                    follow.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_followed, 0, 0, 0);


                                } else if(objData.getBoolean("error")){
                                    displayToast("Impossible d'aimer la video");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayToast("Impossible d'aimer la video");
                            }




                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Dismiss the dialog

                            displayToast("Impossible d'aimer la video");

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
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(mUserId));
                    params.put("profile_id", String.valueOf(mProfileId));
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "cent:capp7622argent";
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    //headers.put("Content-Type", "multipart/form-data");
                    //headers.put("Content-Type", "multipart/form-data");
                    headers.put("Authorization", auth);
                    return headers;
                }

            };

            //adding the request to volley
            Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);


            return null;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }
    public class unfollowUser extends AsyncTask<Void, Void, Boolean> {
        private final Long mUserId;

        unfollowUser(Long userId) {
            mUserId = userId;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            StringRequest volleyMultipartRequest = new StringRequest(Request.Method.DELETE, EndPoints.UPLOAD_URL+"/fans/delete/"+mUserId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject objData = null;
                            try {

                                objData = new JSONObject(response);
                                if (!objData.getBoolean("error")) {
                                    //Dismiss the dialog

                                    hasFollow=false;
                                    follow.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_follow, 0, 0, 0);
                                    follow.setText("Suivre");


                                } else if(objData.getBoolean("error")){
                                    hasFollow=true;
                                    follow.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_followed, 0, 0, 0);
                                    follow.setText("Suivi");

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayToast("Impossible d'aimer la video");
                            }




                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Dismiss the dialog

                            displayToast("Impossible d'aimer la video");

                        }
                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    String credentials = "cent:capp7622argent";
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    //headers.put("Content-Type", "multipart/form-data");
                    //headers.put("Content-Type", "multipart/form-data");
                    headers.put("Authorization", auth);
                    return headers;
                }

            };

            //adding the request to volley
            Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);


            return null;
        }
        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }

    private void searchUserHasFollow(final Long UserId, final Long ProfileId){

        StringRequest volleyMultipartRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/follow/userHasFollow",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        showProgress(false);



                        JSONObject objData = null;
                        try {

                            objData = new JSONObject(response);
                            if (!objData.getBoolean("error")) {
                                //Dismiss the dialog
                                hasFollow = true;
                                follow.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_followed, 0, 0, 0);
                                follow.setText("Suivre");



                            } else if(objData.getBoolean("error")){
                                hasFollow = false;
                                follow.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_follow, 0, 0, 0);
                                follow.setText("Suivi");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("Impossible de récupérer le satut du like de la video");
                        }




                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        showProgress(false);

                        displayToast("Impossible d'aimer la video");

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(UserId));
                params.put("profile_id", String.valueOf(ProfileId));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cent:capp7622argent";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "multipart/form-data");
                //headers.put("Content-Type", "multipart/form-data");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(BoxActivity.this).add(volleyMultipartRequest);
    }
    private void searchLikes(){

    }
    public void displayToast( String message){
        Snackbar snackbar = Snackbar
                .make(layoutBox, message, Snackbar.LENGTH_LONG)
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
    // Requesting runtime permission to access camera.
    public boolean EnableRuntimePermissionToAccessCamera(){
        if (ContextCompat.checkSelfPermission(BoxActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) BoxActivity.this, android.Manifest.permission.CAMERA)) {
                android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(BoxActivity.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("Camera access permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) BoxActivity.this, new String[]{android.Manifest.permission.CAMERA}, RequestPermissionCode);
                    }
                });
                android.support.v7.app.AlertDialog alert = alertBuilder.create();
                alert.show();

            } else {
                ActivityCompat.requestPermissions((Activity) BoxActivity.this, new String[]{android.Manifest.permission.CAMERA}, RequestPermissionCode);
            }
            return false;
        } else {
            return true;
        }
    }


}
