package vakoze.blomidtech.vakoze;

import android.content.Intent;
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

import vakoze.blomidtech.vakoze.fragments.PublicCommunauteFragment;
import vakoze.blomidtech.vakoze.fragments.PublicNotificationsFragment;
import vakoze.blomidtech.vakoze.fragments.PublicProfilFragment;
import vakoze.blomidtech.vakoze.fragments.PublicVideoFragment;
import vakoze.blomidtech.vakoze.models.Video;

public class PublicTimelineActivity extends AppCompatActivity implements PublicVideoFragment.OnListFragmentInteractionListener,
        PublicProfilFragment.OnFragmentInteractionListener,
        PublicCommunauteFragment.OnFragmentInteractionListener,
        PublicNotificationsFragment.OnFragmentInteractionListener {


    ActionBar actionBar;

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
                actionBar.show();
            } else if (id == R.id.profil) {
                fragment = new PublicProfilFragment();
                actionBar.hide();
            } else if (id == R.id.add_video_home) {
                //fragment = new AjoutVideoFragment();
                selectImage();
            } else if (id == R.id.communaute) {
                fragment = new PublicCommunauteFragment();
                actionBar.hide();
            } else if (id == R.id.notifications) {
                fragment = new PublicNotificationsFragment();
                actionBar.hide();
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
        actionBar = getSupportActionBar();
        //actionBar.hide();

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
