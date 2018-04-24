package com.vakoze.video;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vakoze.R;
import com.vakoze.authentication.LoginActivity;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.vakoze.fragments.EditImageFragment;
import com.vakoze.fragments.FilterListFragment;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;
import com.vakoze.utils.BitmapUtils;

public class EditActivity extends AppCompatActivity implements FilterListFragment.FilterListFragmentListener, EditImageFragment.EditImageFragmentListener{

    private Uri uri;
    private String pathToStoredVideo, nom , tags, categories;;
    private VideoView displayRecordedVideo;
    private ImageButton previous, rew, play, pause, ff, forward, sendVideoButton;
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


    private static final String TAG = EditActivity.class.getSimpleName();

    public static final String IMAGE_NAME = "dog.jpg";

    public static final int SELECT_GALLERY_IMAGE = 101;

    @BindView(R.id.image_preview)
    ImageView imagePreview;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FilterListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_title_edit));

        loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("videoType", fileType);
        outState.putSerializable("filePath", receivedUri);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FilterListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        imagePreview.setImageBitmap(myFilter.processFilter(finalImage.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        // once the editing is done i.e seekbar is drag is completed,
        // apply the values on to filtered image
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalImage = myFilter.processFilter(bitmap);
    }

    /**
     * Resets image edit controls to normal when new filter
     * is selected
     */
    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if (id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            // clear bitmap memory
            originalImage.recycle();
            finalImage.recycle();
            finalImage.recycle();

            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            imagePreview.setImageBitmap(originalImage);
            bitmap.recycle();

            // render selected image thumbnails
            filtersListFragment.prepareThumbnail(originalImage);
        }
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, SELECT_GALLERY_IMAGE);
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    /*
    * saves image to camera gallery
    * */
    private void saveImageToGallery() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            final String path = BitmapUtils.insertImage(getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                            if (!TextUtils.isEmpty(path)) {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                        .setAction("OPEN", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openImage(path);
                                            }
                                        });

                                snackbar.show();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }


    /**
     * Video functions
     */


    private void setVideo(Uri videoUri){
        //displayRecordedVideo.setVideoURI(videoUri);
        //displayRecordedVideo.start();

        pathToStoredVideo = getRealPathFromURIPath(videoUri, EditActivity.this);
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

    private class uploadVideoToServer extends AsyncTask<Void, Integer, Boolean> {

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
                                    Intent intent = new Intent(EditActivity.this,LoginActivity.class);
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

            Volley.newRequestQueue(EditActivity.this).add(volleyMultipartRequest);
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



    private void uploadMp4(final String nom, final String tags, final String categorie) {

        loadProgress();

        //getting the current user
        User user = SharedPrefManager.getInstance(EditActivity.this).getUser();
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
                                Intent intent = new Intent(EditActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                displayToast("Upload Non Réussi :"+obj.getString("message"));
                                Log.e("VakoError : Upload Non Réussi",obj.getString("message"));
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
                        Log.e("VakoError : Volley Error",error.getMessage());
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

