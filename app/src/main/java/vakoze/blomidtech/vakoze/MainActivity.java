package vakoze.blomidtech.vakoze;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import vakoze.blomidtech.vakoze.lib.Utility;
import vakoze.blomidtech.vakoze.models.Video;

public class MainActivity extends AppCompatActivity implements videoFragment.OnListFragmentInteractionListener,
        ProfilFragment.OnFragmentInteractionListener,
        CommunauteFragment.OnFragmentInteractionListener,
        NotificationsFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String userChoosenTask;
    private int SELECT_FILE = 0;
    private int REQUEST_VIDEO_CAPTURE = 1;
    private Uri fileUri; // file url to store image/video
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                //startActivity(new Intent(getApplicationContext(), AjoutVideoActivity.class));
                selectImage();
            }
        });

    }

    private void selectImage() {
        final CharSequence[] items = { "Prendre une video", "Choisir depuis le téléphone",
                "Annuler" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(MainActivity.this);

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
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
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
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
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

        Intent i = new Intent(MainActivity.this, AjoutVideoActivity.class);
        i.putExtra("filePath", getRealPathFromURIPath(videoToSend, MainActivity.this));
        startActivity(i);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Uri videoToSend = data.getData();

        Intent i = new Intent(MainActivity.this, AjoutVideoActivity.class);
        i.putExtra("filePath", getRealPathFromURIPath(videoToSend, MainActivity.this));
        startActivity(i);




/*
        if (data != null) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        profil.setImageBitmap(bitmap);

        */
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

    /**
     * ------------ Helper Methods ----------------------
     * */



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Video item) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            //this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
             Fragment fm = null;

            switch(position){

                case 0:
                    fm = new videoFragment();
                    return fm;
                case 1:
                    fm = new ProfilFragment();
                    return fm;
                case 2:
                    fm = new CommunauteFragment();
                    return fm;
                case 3:
                    fm = new NotificationsFragment();
                    return fm;
                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}
