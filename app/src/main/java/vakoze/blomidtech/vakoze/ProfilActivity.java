package vakoze.blomidtech.vakoze;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import vakoze.blomidtech.vakoze.lib.SharedPrefManager;
import vakoze.blomidtech.vakoze.models.User;

public class ProfilActivity extends AppCompatActivity {

    private TextView textNom, textPrenom, textEmail, textPhone;
    private ImageView profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout colTool = findViewById(R.id.toolbar_layout);
        AppBarLayout appBar = findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fabProfileLogout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
                finish();
                startActivity(new Intent(getApplicationContext(), StartActivity.class));

                //SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });

        //if the user is not logged in
        //starting the login activity
        /*
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            //finish();
            //startActivity(new Intent(this, LoginActivity.class));
        }
        */


        textNom = findViewById(R.id.textNom);
        textPrenom = findViewById(R.id.textPrenom);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        profile_pic = findViewById(R.id.profile_pic);



        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        Glide.with(ProfilActivity.this)
                .load("https://ilink-app.com/v/uploads/profile_pic/"+user.getProfile_pic())
                //.fitCenter()
                .into(profile_pic);

        //setting the values to the textviews
        textNom.setText(user.getNom());
        colTool.setTitle(user.getPrenom());

        //toolbar.setBackgroundResource();
        textPrenom.setText(user.getPrenom());
        textEmail.setText(user.getEmail());
        textPhone.setText(user.getPhone());




    }
}
