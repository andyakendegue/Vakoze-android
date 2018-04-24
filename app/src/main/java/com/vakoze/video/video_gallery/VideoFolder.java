package com.vakoze.video.video_gallery;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.VideoView;

import com.vakoze.R;
import com.vakoze.video.adapter.Adapter_VideoFolder;

import java.util.ArrayList;

public class VideoFolder extends AppCompatActivity implements Adapter_VideoFolder.OnItemClicked {
    Adapter_VideoFolder obj_adapter;
    ArrayList al_video = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    VideoView thmbnail1, thmbnail2, thmbnail3, thmbnail4, thmbnail5, thmbnail6;
    Button btnMontage;
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videofolder);

        init();
    }

    private void init(){
        thmbnail1 = findViewById(R.id.thumbnail1);
        thmbnail2 = findViewById(R.id.thumbnail2);
        thmbnail3 = findViewById(R.id.thumbnail3);
        thmbnail4 = findViewById(R.id.thumbnail4);
        thmbnail5 = findViewById(R.id.thumbnail5);
        thmbnail6 = findViewById(R.id.thumbnail6);
        btnMontage = findViewById(R.id.btnSendToEffects);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        fn_video();


    }






    public void fn_video() {

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name,column_id,thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));

            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));

            al_video.add(obj_model);

        }


        obj_adapter = new Adapter_VideoFolder(getApplicationContext(),al_video,VideoFolder.this);
        obj_adapter.setOnClick(this);

        recyclerView.setAdapter(obj_adapter);




    }
/*
    @Override
    public void onVideoSelected(Model_Video video) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(video.getStr_path());
            int timeInSeconds = 30;
            Bitmap myBitmap = retriever.getFrameAtTime(timeInSeconds * 1000000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            thmbnail1.setImageBitmap(myBitmap);
            Toast.makeText(this, "selected", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.i("MyDebugCode", "MediaMetadataRetriever got exception:" + ex);
        }

    }
    */


    @Override
    public void onItemClick(Model_Video video) {
        thmbnail1.setVideoURI(Uri.parse("file://"+video.getStr_path()));
        thmbnail1.seekTo(100);
        thmbnail2.setVideoURI(Uri.parse("file://"+video.getStr_path()));
        thmbnail2.seekTo(500);
        thmbnail3.setVideoURI(Uri.parse("file://"+video.getStr_path()));
        thmbnail3.seekTo(1000);
        thmbnail4.setVideoURI(Uri.parse("file://"+video.getStr_path()));
        thmbnail4.seekTo(1500);
        thmbnail5.setVideoURI(Uri.parse("file://"+video.getStr_path()));
        thmbnail5.seekTo(1500);
        thmbnail6.setVideoURI(Uri.parse("file://"+video.getStr_path()));
        thmbnail6.seekTo(1500);


    }
}
