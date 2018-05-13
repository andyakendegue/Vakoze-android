package com.vakoze.chatFirebase;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.database.FirebaseDatabase;
import com.vakoze.R;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.models.User;

public class MessengerActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    private String messengerTo, name, photo;
    User user = SharedPrefManager.getInstance(this).getUser();
    RelativeLayout activity_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        activity_main = findViewById(R.id.activity_main);
        Bundle extras = getIntent().getExtras();

        if (savedInstanceState == null) {
            if(extras == null) {
                messengerTo= null;
            } else {
                messengerTo=extras.getString("messengerTo");
                name=extras.getString("name");
                photo=extras.getString("photo");

            }
        } else {
            //messengerTo= Long.parseLong(extras.getString("messengerTo"));
            messengerTo= (String) savedInstanceState.getSerializable("messengerTo");

            name=(String) savedInstanceState.getSerializable("name");
            photo=(String) savedInstanceState.getSerializable("photo");
        }

        TextView nameUser = findViewById(R.id.nameUser);
        nameUser.setText(name);
        ImageView picUser = findViewById(R.id.picUser);
        if(photo!=null&&!photo.isEmpty()&&!photo.equals("")){
            Glide.with(this)
                    .load(photo)
                    //.fitCenter()
                    .into(picUser);
        } else {
            //profile_pic.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_square);
            //profile_pic.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

            Glide.with(this)
                    //.load(user.getProfile_pic())
                    .load(Uri.parse("android.resource://com.vakoze/" + R.drawable.profile_pic))
                    //.fitCenter()
                    .into(picUser);
        }

        displayChatMessages();
        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        /*.setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(),messengerTo)*/
                        .setValue(new ChatMessage(input.getText().toString(),
                                user.getU_id(),String.valueOf(messengerTo))
                        );
                // Clear the inputuser = SharedPrefManager.getInstance(getActivity()).getUser();
                input.setText("");
            }
        });
    }
    private void displayChatMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        FirebaseDatabase.getInstance().getReference().getKey();
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                if(model.getMessageTo().equals(user.getU_id())&&model.getMessageUser().equals(messengerTo)) {
                    // Set their text

                    try{
                        messageText.setText(model.getMessageText());
                        //messageUser.setText(model.getMessageUser());
                        //messageUser.setText(String.valueOf(messengerTo));
                        if(model.getMessageUser().equals(messengerTo)){
                            messageUser.setText(name);
                            messageUser.setTextColor(getResources().getColor(R.color.brownDark));
                        } else if(model.getMessageTo().equals(user.getU_id())){
                            messageUser.setText(user.getPrenom());
                            messageUser.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        }

                        // Format the date before showing it
                        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                                model.getMessageTime()));


                    } catch(Exception e){
                        displayToast(e.getMessage());

                    }
                }
            }
        };
        listOfMessages.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable("messengerTo", messengerTo);
        outState.putSerializable("name", name);
        outState.putSerializable("photo", photo);
    }
    public void displayToast(String message){
        Snackbar snackbar = Snackbar
                .make(activity_main, message, Snackbar.LENGTH_LONG)
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
}