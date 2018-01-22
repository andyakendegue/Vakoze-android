package vakoze.blomidtech.vakoze;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class StartActivity extends AppCompatActivity {
    private Button connexion, inscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // Verification si connecté


        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;


        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

        /**
         * Code pour l'enregistrement des achats
         * logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
         */
        setContentView(R.layout.activity_start);
        printhashkey();
        // Initialisation des boutons
        connexion = findViewById(R.id.connexion);
        inscription = findViewById(R.id.inscription);


        // L'utilisateur est dirigé vers la page de connexion
        connexion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }
        });
        // L'utilisateur est dirigé vers la page d'inscription
        inscription.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent signupIntent = new Intent(StartActivity.this, SignupActivity.class);
                startActivity(signupIntent);

            }
        });



    }


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


}
