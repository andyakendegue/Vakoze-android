package com.vakoze.video;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.vakoze.R;
import com.vakoze.fragments.PublicCommunauteFragment;
import com.vakoze.fragments.PublicNotificationsFragment;
import com.vakoze.fragments.PublicProfilFragment;
import com.vakoze.fragments.PublicVideoFragment;
import com.vakoze.fragments.RepostFragment;
import com.vakoze.video.fragments.VideoEffectsFragment;

public class VideoEditActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            /*
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            */
            int id = item.getItemId();
            Fragment fragment = null;
            //CharSequence title = getString(R.string.app_name);
            if (id == R.id.actualite) {
                Bundle args = new Bundle();
                args.putString("source", "");
                args.putString("user_id", "");
                args.putString("nom", "");
                args.putString("tags", "");
                args.putString("categorie", "");
                args.putString("id", "");
                args.putString("type", "");
                fragment = new VideoEffectsFragment();

                fragment.setArguments(args);
                //actionBar.show();
                //getSupportActionBar().show();
            } else if (id == R.id.profil) {
                fragment = new PublicProfilFragment();
                //actionBar.hide();
                //getSupportActionBar().hide();
            } else if (id == R.id.communaute) {
                fragment = new PublicCommunauteFragment();
                //actionBar.hide();
                //getSupportActionBar().hide();
            } else if (id == R.id.notifications) {
                fragment = new PublicNotificationsFragment();
                //actionBar.hide();
                //getSupportActionBar().hide();
            }
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.video_edit_container, fragment);
                ft.commit();
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.video_edit_container, new VideoEffectsFragment());
        ft.commit();
    }

}
