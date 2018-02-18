package vakoze.blomidtech.vakoze;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import vakoze.blomidtech.vakoze.lib.SharedPrefManager;
import vakoze.blomidtech.vakoze.models.User;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2;

    public  static final int RequestPermissionCode  = 1 ;

    @Override
    public void onStart() {
        super.onStart();
        mayRequestContacts();
        EnableRuntimePermissionToAccessCamera();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }

        if(isConnected()){
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            User currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
            boolean loggedin= SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn();

            if(account != null){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                printhashkey();
                finish();
            } else if(!loggedIn){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                printhashkey();
                finish();
            } else if(loggedin){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                printhashkey();
                finish();
            }  else {
                Intent intent = new Intent(this, PublicTimelineActivity.class);
                startActivity(intent);
                printhashkey();
                finish();
             }

        } else {

            Toast.makeText(SplashActivity.this, "Vous n'êtes pas connecté à internet", Toast.LENGTH_SHORT).show();
        }

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void printhashkey(){

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "vakoze.blomidtech.vakoze",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {

            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    // Requesting runtime permission to access camera.

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,
                android.Manifest.permission.CAMERA))
        {
            // Printing toast message after enabling runtime permission.
            Toast.makeText(SplashActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{android.Manifest.permission.CAMERA}, RequestPermissionCode);

        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(SplashActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(SplashActivity.this,"Cette permission nous permet d\'accéder à votre appareil photo", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }

    }

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;


    }


}
