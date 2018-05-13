package com.vakoze;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vakoze.authentication.LoginActivity;
import com.vakoze.fragments.PublicCommunauteFragment;
import com.vakoze.fragments.PublicNotificationsFragment;
import com.vakoze.fragments.PublicProfilFragment;
import com.vakoze.fragments.PublicVideoFragment;
import com.vakoze.models.Video;

public class PublicTimelineActivity extends AppCompatActivity implements PublicVideoFragment.OnListFragmentInteractionListener,
        PublicProfilFragment.OnFragmentInteractionListener,
        PublicCommunauteFragment.OnFragmentInteractionListener,
        PublicNotificationsFragment.OnFragmentInteractionListener {
    //ActionBar actionBar;
    private Toolbar toolbar;
    private ImageButton btnActu, btnCommunaute, btnShot, btnProfil, btnNotifications;
    private TextView mTitle, textview;


    private RelativeLayout.LayoutParams layoutparams;
    public boolean fragmentManager(int btn){
        int id = btn;
        Fragment fragment = null;
        //CharSequence title = getString(R.string.app_name);
        if (id == R.id.btnActu) {
            fragment = new PublicVideoFragment();
            toolbar.setTitle("Actualités");
            toolbar.setTitleMarginStart(30);
            toolbar.setTitleMarginEnd(30);
            //mTitle.setText("Actualités");
            //actionBar.show();
            getSupportActionBar().show();
        } else if (id == R.id.btnProfil) {
            fragment = new PublicProfilFragment();
            //actionBar.hide();
            getSupportActionBar().hide();
            //appBarLayout.setVisibility(View.GONE);
        } else if (id == R.id.btnShot) {
            //fragment = new AjoutVideoFragment();
            selectImage();
        } else if (id == R.id.btnCommunaute) {
            fragment = new PublicCommunauteFragment();
            //actionBar.hide();
            getSupportActionBar().hide();
            //appBarLayout.setVisibility(View.GONE);
        } else if (id == R.id.btnNotifications) {
            fragment = new PublicNotificationsFragment();
            //actionBar.hide();
            getSupportActionBar().hide();
            //appBarLayout.setVisibility(View.GONE);
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment);
            ft.commit();
        }
        return true;
    }
/*
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();
            Fragment fragment = null;
            //CharSequence title = getString(R.string.app_name);
            if (id == R.id.actualite) {
                fragment = new PublicVideoFragment();
                //actionBar.show();
                getSupportActionBar().show();
            } else if (id == R.id.profil) {
                fragment = new PublicProfilFragment();
                //actionBar.hide();
                getSupportActionBar().hide();
                //appBarLayout.setVisibility(View.GONE);
            } else if (id == R.id.add_video_home) {
                //fragment = new AjoutVideoFragment();
                selectImage();
            } else if (id == R.id.communaute) {
                fragment = new PublicCommunauteFragment();
                //actionBar.hide();
                getSupportActionBar().hide();
                //appBarLayout.setVisibility(View.GONE);
            } else if (id == R.id.notifications) {
                fragment = new PublicNotificationsFragment();
                //actionBar.hide();
                getSupportActionBar().hide();
                //appBarLayout.setVisibility(View.GONE);
            }
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.commit();
            }
            return true;
        }
    };
    */
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Actualités");
        toolbar.setTitleMarginStart(30);
        toolbar.setTitleMarginEnd(30);
        //mTitle.setText("Actualités");

        //mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        //appBarLayout = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        //actionBar = getSupportActionBar();
        //actionBar.hide();
        /*
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        for (int i = 0; i < navigation.getChildCount(); i++) {
            final View iconView = navigation.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }*/

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new PublicVideoFragment());
        ft.commit();

        FloatingActionButton fabSignOut = findViewById(R.id.fab_signout);
        fabSignOut.setVisibility(View.INVISIBLE);
        btnActu = findViewById(R.id.btnActu);
        btnActu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager(R.id.btnActu);

            }
        });
        btnCommunaute = findViewById(R.id.btnCommunaute);
        btnCommunaute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager(R.id.btnCommunaute);
            }
        });
        btnNotifications = findViewById(R.id.btnNotifications);
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager(R.id.btnNotifications);
            }
        });
        btnShot = findViewById(R.id.btnShot);
        btnShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager(R.id.btnShot);
            }
        });
        btnProfil = findViewById(R.id.btnProfil);
        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager(R.id.btnProfil);
            }
        });
        //setAppBarHeight();
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
