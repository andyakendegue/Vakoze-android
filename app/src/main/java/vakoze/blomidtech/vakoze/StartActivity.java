package vakoze.blomidtech.vakoze;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.VolleyMultipartRequest;
import vakoze.blomidtech.vakoze.adapters.PublicVideoAdapter;
import vakoze.blomidtech.vakoze.models.Video;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class StartActivity extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int REQUEST_READ_CONTACTS = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;

    public  static final int RequestPermissionCode  = 1 ;
    private String userChoosenTask;
    private Button connexion, inscription;
    private List<Video> videoGroupList;
    private PublicVideoAdapter videosAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onStart(){
        super.onStart();
        videoGroupList = new ArrayList<>();
        searchVideo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_start);
        // Grille de vidéos
        // Set the adapter

        Context context = this;
        recyclerView = (RecyclerView) findViewById(R.id.listStart);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));




        // Ajout d'une photo de profil
        EnableRuntimePermissionToAccessCamera();
        //printhashkey();
        // Initialisation des boutons



    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
/*
        // construct a list of books you've favorited
        final ArrayList<Integer> videoNames = new ArrayList<>();
        for (Video video : videos) {
            if (video.getIsFavorite()) {
                videoNames.add(video.getNom());
            }
        }

        // save that list to outState for later
        outState.putIntegerArrayList(favoritedBookNamesKey, favoritedBookNames);
        */
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        /*
        // get our previously saved list of favorited books
        final ArrayList<Integer> favoritedBookNames =
                savedInstanceState.getIntegerArrayList(favoritedBookNamesKey);

        // look at all of your books and figure out which are the favorites
        for (int bookName : favoritedBookNames) {
            for (Video video : videos) {
                if (video.getNom() == bookName) {
                    video.setIsFavorite(true);
                    break;
                }
            }
        }
        */
    }


    public void printhashkey(){

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "vakoze.blomidtech.vakoze",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

            Log.d("NameNotFound", e.toString());

        } catch (NoSuchAlgorithmException e) {

            Log.d("NoSuchAlgorithm", e.toString());

        }

    }

    // Requesting runtime permission to access camera.

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(StartActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(StartActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(StartActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(StartActivity.this,new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(StartActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(StartActivity.this,new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }


    }




    private void searchVideo(){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/video/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();


                            //Toast.makeText(StartActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < objLength; i++)
                            {
                                JSONObject item = obj.getJSONObject(i);
                                Video video = new Video();

                                video.setId(Long.parseLong(item .getString("id")));
                                //video.setDate_ajout(Date.parse(item .getString("date_ajout")));
                                video.setCategorie(item .getString("categorie"));
                                video.setNom(item .getString("nom"));
                                video.setTags(item .getString("tags"));
                                video.setType(item .getString("type"));
                                video.setSource(item .getString("source"));
                                video.setUser_id(item .getLong("user_id"));

                                //Toast.makeText(StartActivity.this, String.valueOf(video.getUser_id()), Toast.LENGTH_SHORT).show();

                                videoGroupList.add(video);


                            }

                            //Toast.makeText(StartActivity.this, videoGroupList.toString(), Toast.LENGTH_SHORT).show();



                        } catch (JSONException e) {
                            //Dismiss the dialog


                            //Toast.makeText(getContext(), "Recherche impossible."+e.getMessage(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }

                        if (videoGroupList!=null) {
                            //Toast.makeText(getContext(), videoGroupList.get(1).getNom(), Toast.LENGTH_SHORT).show();

                            //videosAdapter = new PublicVideoAdapter(StartActivity.this, videoGroupList);
                            //timelineAdapter = new TimelineAdapter(StartActivity.this, videoGroupList);
                            recyclerView.setAdapter(videosAdapter);
                            //gridView.setAdapter(timelineAdapter);
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        //Toast.makeText(getContext(), "Connexion impossible.", Toast.LENGTH_SHORT).show();


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
        Volley.newRequestQueue(StartActivity.this).add(volleyMultipartRequest);
    }

}
