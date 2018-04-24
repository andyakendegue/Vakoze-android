package com.vakoze.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import com.vakoze.adapters.MyCommentRecyclerViewAdapter;
import com.vakoze.R;
import com.vakoze.adapters.VideoAdapter;
import com.vakoze.dummy.DummyContent;
import com.vakoze.dummy.DummyContent.DummyItem;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.FullScreenMediaController;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.Video;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CommentFragment extends DialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CommentFragment newInstance(int columnCount) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyCommentRecyclerViewAdapter(DummyContent.ITEMS, mListener));
        }
        return view;
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
            searchVideo();
        } else {
            //Toast.makeText(getContext(), "Vous n'êtes pas connecté à internet. Rafraichissez la page", Toast.LENGTH_SHORT).show();
        }


        try {
            mListener = (OnListFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mListener = null;

    }
    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;


    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_comment_list, null);



        String source = getArguments().getString("source");
        Long user_id = getArguments().getLong("user_id");
        String nom = getArguments().getString("nom");
        String tags = getArguments().getString("tags");
        String categorie = getArguments().getString("categorie");
        Long id = getArguments().getLong("id");
        String type = getArguments().getString("type");




        builder
                .setView(v)

                .setPositiveButton("Reposter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
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


                            //Toast.makeText(getContext(), obj.toString(), Toast.LENGTH_SHORT).show();
/*

                            int i = 0; videoGroupList.clear();

                            for (i = 0; i < objLength; i++)
                            {
                                Video video = new Video();

                                video.setId(Long.parseLong(obj.getJSONObject(i).getString("id")));
                                //video.setDate_ajout(Date.parse(objData.getString("date_ajout")));
                                video.setCategorie(obj.getJSONObject(i).getString("categorie"));
                                video.setNom(obj.getJSONObject(i).getString("nom"));
                                video.setTags(obj.getJSONObject(i).getString("tags"));
                                video.setType(obj.getJSONObject(i).getString("type"));
                                video.setSource(obj.getJSONObject(i).getString("source"));
                                video.setUser_id(Long.parseLong(obj.getJSONObject(i).getString("user_id")));
                                videoGroupList.add(video);

                            }



                            if (videoGroupList!=null) {
                                //Toast.makeText(getContext(), videoGroupList.get(1).getNom(), Toast.LENGTH_SHORT).show();
                                adapter = new VideoAdapter( getActivity(), videoGroupList);
                            }

                            recyclerView.setAdapter(adapter);


*/

                        } catch (JSONException e) {
                            //Dismiss the dialog


                            //Toast.makeText(getContext(), "Recherche impossible."+e.getMessage(), Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
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
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);
    }
}
