package vakoze.blomidtech.vakoze;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import vakoze.blomidtech.vakoze.models.Video;

public class PublicTimelineActivity extends AppCompatActivity implements PublicVideoFragment.OnListFragmentInteractionListener,
        PublicProfilFragment.OnFragmentInteractionListener,
        PublicCommunauteFragment.OnFragmentInteractionListener,
        PublicNotificationsFragment.OnFragmentInteractionListener {

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
                fragment = new PublicVideoFragment();
            } else if (id == R.id.profil) {
                fragment = new PublicProfilFragment();
            } else if (id == R.id.add_video_home) {
                //fragment = new AjoutVideoFragment();
                selectImage();
            } else if (id == R.id.communaute) {
                fragment = new PublicCommunauteFragment();
            } else if (id == R.id.notifications) {
                fragment = new PublicNotificationsFragment();
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
        ft.replace(R.id.container, new PublicVideoFragment());
        ft.commit();

        FloatingActionButton fabSignOut = findViewById(R.id.fab_signout);

        fabSignOut.setVisibility(View.INVISIBLE);

    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Video item) {

    }


    private void selectImage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }


}
