package vakoze.blomidtech.vakoze.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.FullScreenMediaController;
import vakoze.blomidtech.vakoze.lib.SharedPrefManager;
import vakoze.blomidtech.vakoze.lib.VolleyMultipartRequest;
import vakoze.blomidtech.vakoze.models.User;

import static android.content.Context.WINDOW_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RepostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RepostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepostFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText msgRepost;
    private TextView duree_video,  titre_video;
    private VideoView videoRepost;

    private ProgressBar progressBar;
    private boolean isContinuously = false;
    Long video_id;



    // Identifiant de la boîte de dialogue

    private static ProgressDialog progressDialog;
    String receivedUri;
    View v ;

    public RepostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepostFragment newInstance(String param1, String param2) {
        RepostFragment fragment = new RepostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repost, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isConnected()){
            //searchVideo();
        } else {
            //Toast.makeText(getContext(), "Vous n'êtes pas connecté à internet. Rafraichissez la page", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v= inflater.inflate(R.layout.fragment_repost, null);

        duree_video = v.findViewById(R.id.video_duration);
        titre_video = v.findViewById(R.id.video_title);
        msgRepost = v.findViewById(R.id.msgRepost);
        videoRepost = v.findViewById(R.id.repostVid);

            String source = getArguments().getString("source");
            Long user_id = getArguments().getLong("user_id");
            String nom = getArguments().getString("nom");
            String tags = getArguments().getString("tags");
            String categorie = getArguments().getString("categorie");
            video_id = getArguments().getLong("id");
            String type = getArguments().getString("type");

            titre_video.setText(nom);



        //displayRecordedVideo.setVideoPath(receivedUri);
        videoRepost.setVideoURI(Uri.parse("http://"+source));
        //displayRecordedVideo.setUp(receivedUri,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, receivedUri.substring(receivedUri.lastIndexOf("/")+1));
        videoRepost.seekTo(100);
        MediaController mediaController = new FullScreenMediaController(getActivity());
        mediaController.setAnchorView(videoRepost);

        if(isLandScape()){
            mediaController = new FullScreenMediaController(getActivity());
        }else {
            mediaController = new MediaController(getActivity());
        }

        videoRepost.setMediaController(mediaController);
        videoRepost.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long duration = videoRepost.getDuration();
                duree_video.setText(String.valueOf(duration));
            }
        });

        progressBar = v.findViewById(R.id.public_video_progress);
        videoRepost.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoRepost.start();
                if(isContinuously){

                }
            }
        });
        videoRepost.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
            }
        });


        builder
        .setView(v)

                .setPositiveButton("Reposter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        repost(video_id,msgRepost.getText().toString());
                        //displayToast("Repost en cours...");
                        Log.e("test log", "ok");
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private boolean isLandScape(){
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();
        if (rotation == Surface.ROTATION_90
                || rotation == Surface.ROTATION_270) {
            return true;
        }
        return false;
    }

    private void repost(final Long videoId, final String commentaire) {
        loadProgress();
        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        final Long userId = user.getId();
        //our custom volley request
        StringRequest volleyMultipartRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_URL+"/repost/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                displayToast("Upload Réussi");
                                Log.e("VakoError : Upload Réussi",obj.getString("message"));
                            } else {
                                displayToast("Upload Non Réussi :"+obj.getString("message"));
                                Log.e("VakoError : Upload Non Réussi",obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("erreur"+e.getMessage());
                            Log.e("VakoError : "+e.getMessage(),response);
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
                params.put("video_id", String.valueOf(videoId));;
                params.put("commentaire", commentaire);
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
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);
    }

    private void loadProgress(){
        //getting the tag from the edittext
        //final String tags = editTextTags.getText().toString().trim();
        progressDialog = new ProgressDialog(getActivity());
        // Setting Title
        progressDialog.setTitle("Repost de la vidéo");
        // Setting Message
        progressDialog.setMessage("Patientez...");
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
    public void displayToast(String message){
        Snackbar snackbar = Snackbar
                .make(v, message, Snackbar.LENGTH_LONG)
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
