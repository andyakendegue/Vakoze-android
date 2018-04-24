package com.vakoze.public_profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.Date;

import com.vakoze.R;
import com.vakoze.models.Video;

public class CommentairesActivity extends AppCompatActivity {

    private Video videoSent;
    private Date date;
    private ListView commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentaires);

        commentList = findViewById(R.id.commentList);

        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });





        Bundle extra = getIntent().getExtras();

        videoSent = new Video();

        if (savedInstanceState == null) {

            if (extra == null) {
                //receivedUri = null;
            } else {


                videoSent.setId(extra.getLong("id"));
                videoSent.setUser_id(extra.getLong("user_id"));
                videoSent.setNom(extra.getString("nom"));
                videoSent.setDescription(extra.getString("description"));
                videoSent.setTags(extra.getString("tags"));
                videoSent.setSource(extra.getString("source"));
                videoSent.setType(extra.getString("type"));
                videoSent.setCategorie(extra.getString("categorie"));
                videoSent.setDate_ajout(Date.valueOf(extra.getString("date_ajout")));

                /*
                String source = extra.getString("source");
                Long user_id = extra.getLong("user_id");
                String nom = extra.getString("nom");
                String tags = extra.getString("tags");
                String categorie = extra.getString("categorie");
                Long id = extra.getLong("id");
                String type = extra.getString("type");
                */

            }
        } else {

        }


    }
}
