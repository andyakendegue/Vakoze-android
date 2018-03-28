package vakoze.blomidtech.vakoze;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import vakoze.blomidtech.vakoze.fragments.RepostFragment;
import vakoze.blomidtech.vakoze.fragments.ShareFragment;
import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.FullScreenMediaController;
import vakoze.blomidtech.vakoze.lib.VolleyMultipartRequest;
import vakoze.blomidtech.vakoze.models.User;
import vakoze.blomidtech.vakoze.models.Video;


public class PublicBoxActivity extends AppCompatActivity implements RepostFragment.OnFragmentInteractionListener, ShareFragment.OnFragmentInteractionListener {
    private static final String TAG = PublicBoxActivity.class.getSimpleName();
    private Uri uri;
    private String pathToStoredVideo, nom , tags, categories;;
    private VideoView displayRecordedVideo;
    private ImageButton like, comment, share, repost, ff, forward, sendVideoButton;
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
    private TextView userName, description_video, titre_video, duree_video;
    private ImageView userPic;
    private ProgressBar progressBar;
    private boolean isContinuously = false;
    private View mProgressView;
    private View mBoxView;
    private User videoUser;
    RelativeLayout layoutBox;

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
        layoutBox = findViewById(R.id.layout_box_activity);
        mBoxView = findViewById(R.id.box_layout);
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
            videoInfo.setUser_id(Long.parseLong((String) savedInstanceState.getSerializable("userId")));
            videoInfo.setNom((String) savedInstanceState.getSerializable("nom"));
            videoInfo.setTags((String) savedInstanceState.getSerializable("tags"));
            videoInfo.setCategorie((String) savedInstanceState.getSerializable("categorie"));
            videoInfo.setDescription((String) savedInstanceState.getSerializable("description"));
            videoInfo.setId(Long.parseLong((String) savedInstanceState.getSerializable("id")));
            videoInfo.setType((String) savedInstanceState.getSerializable("type"));
            titre_video.setText((String) savedInstanceState.getSerializable("nom"));
            description_video.setText((String) savedInstanceState.getSerializable("description"));
        }

        userName = findViewById(R.id.userName);
        //
        userPic = findViewById(R.id.userPic);

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

                Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);

                startActivity(i);

            }
        });
        follow = findViewById(R.id.follow);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);

                startActivity(i);

            }
        });
        like = findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //like.setImageIcon(R.drawable.ic_like_inactive);
                //like.setBackgroundResource();
                //like.setImageResource(R.drawable.ic_like_inactive);
                Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);

                startActivity(i);

            }
        });

        comment = findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);

                startActivity(i);



            }
        });

        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);
                startActivity(i);
                */

                Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);
                startActivity(i);
                /*
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
                */


            }
        });

        repost = findViewById(R.id.repost);
        repost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(PublicBoxActivity.this, LoginActivity.class);
                startActivity(i);
                /*
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
                */

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

    public User searchUser(final String UserId){
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

                                if(user[0].getProfile_pic()!=null||!user[0].getProfile_pic().isEmpty()){
                                    Glide.with(PublicBoxActivity.this)
                                            .load(user[0].getProfile_pic())
                                            //.fitCenter()
                                            .into(userPic);
                                } else {
                                    userPic.setImageResource(R.drawable.profile_pic);
                                }

                            }


                            showProgress(false);

                        } catch (JSONException e) {
                            //Dismiss the dialog
                            showProgress(false);
                            e.printStackTrace();
                            displayToast(layoutBox, "VakoError: JsonException Error - "+e.getMessage());

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        displayToast(layoutBox, "VakoError: Response Error - "+error.getMessage());
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
        Volley.newRequestQueue(PublicBoxActivity.this).add(volleyMultipartRequest);


        if(user[0] == null){
            return null;
        } else {
            return user[0];
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void displayToast(View view, String message){
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
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

}
