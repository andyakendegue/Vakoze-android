package vakoze.blomidtech.vakoze;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import vakoze.blomidtech.vakoze.lib.SharedPrefManager;
import vakoze.blomidtech.vakoze.lib.Utility;
import vakoze.blomidtech.vakoze.models.Video;

public class TimelineActivity extends AppCompatActivity implements videoFragment.OnListFragmentInteractionListener,
        ProfilFragment.OnFragmentInteractionListener,
        CommunauteFragment.OnFragmentInteractionListener,
        NotificationsFragment.OnFragmentInteractionListener,AjoutVideoFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private GoogleSignInClient mGoogleSignInClient;

    private String userChoosenTask;
    private int SELECT_FILE = 0;

    private Uri fileUri; // file url to store image/video
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private Cursor cursor = null;
    private int idx = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            /*
            switch (item.getItemId()) {
                case R.id.actualite:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.profil:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.add_video_home:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
            */

            int id = item.getItemId();
            Fragment fragment = null;
            //CharSequence title = getString(R.string.app_name);
            if (id == R.id.actualite) {
                fragment = new videoFragment();
            } else if (id == R.id.profil) {
                fragment = new ProfilFragment();
            } else if (id == R.id.add_video_home) {
                //fragment = new AjoutVideoFragment();
                selectImage();
            } else if (id == R.id.communaute) {
                fragment = new CommunauteFragment();
            } else if (id == R.id.notifications) {
                //logout();
            }
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.commit();
            }


            return true;


        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // Initialize Firebase Auth
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new videoFragment());
        ft.commit();

        FloatingActionButton fabSignOut = findViewById(R.id.fab_signout);

        fabSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();


            }
        });

    }

    private void logout() {
        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        finish();
        SharedPrefManager.getInstance(getApplicationContext()).logout();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Video item) {

    }
    public void signOut(){


        finish();

    }

    private void selectImage() {
        final CharSequence[] items = { "Prendre une video", "Choisir depuis le téléphone",
                "Annuler" };

        AlertDialog.Builder builder = new AlertDialog.Builder(TimelineActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(TimelineActivity.this);

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

                Intent i = new Intent(TimelineActivity.this, AjoutVideoActivity.class);
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

        Intent i = new Intent(TimelineActivity.this, AjoutVideoActivity.class);
        i.putExtra("filePath", getRealPathFromURIPath(videoToSend, TimelineActivity.this));

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
