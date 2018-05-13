package com.vakoze.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vakoze.adapters.MyMesVideosRecyclerViewAdapter;
import com.vakoze.R;
import com.vakoze.adapters.PublicVideoAdapter;
import com.vakoze.adapters.VideoAdapter;
import com.vakoze.dummy.DummyContent;
import com.vakoze.dummy.DummyContent.DummyItem;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;
import com.vakoze.models.Video;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MesVideosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnTouchListener, VideoAdapter.VideoAdapterListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    User user;
    View view;
    RecyclerView recyclerView;

    private VideoAdapter adapter;

    private List<Video> videoList;
    Context context;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MesVideosFragment() {
        this.videoList = new ArrayList();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MesVideosFragment newInstance(int columnCount) {
        MesVideosFragment fragment = new MesVideosFragment();
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
        //getting the current user
        user = SharedPrefManager.getInstance(getActivity()).getUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mesvideos_list, container, false);

        context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.mesVideosList);
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new VideoAdapter(getActivity(), videoList,videoList);
        recyclerView.setAdapter(adapter);
        // Set the adapter
        /*
        if (view instanceof RecyclerView) {
            context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.mesVideosList);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new VideoAdapter(getActivity(), videoList,videoList);
            recyclerView.setAdapter(adapter);
        }*/
        searchVideosByUser(user.getId());
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        try {
            mListener = (OnListFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onStop(){
        super.onStop();

    }
    @Override
    public void onResume(){
        super.onResume();
        if(isConnected()){
            //searchVideo();
            searchVideosByUser(user.getId());
            adapter = new VideoAdapter(getActivity(), videoList,videoList);
            recyclerView.setAdapter(adapter);
        } else {
            //Toast.makeText(getContext(), "Vous n'êtes pas connecté à internet. Rafraichissez la page", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        //appellé lors de l'action Pull To Refresh
        //searchVideo();
        searchVideosByUser(user.getId());

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onVideoSelected(Video video) {

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

    private void searchVideosByUser(final Long UserId){
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/video/all/"+UserId,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject objData = null;
                        try {
                            objData = new JSONObject(new String(response.data));

                            if(!objData.getBoolean("error")){

                                if(!objData.getString("videos").equals(null)){

                                    JSONArray obj = new JSONArray(objData.getString("videos"));
                                    int length = obj.length();
                                    if(videoList!=null){
                                        videoList.clear();
                                    }

                                    for (int i=0; i<length; i++ ){
                                        Video v = new Video();
                                        v.setId(Long.parseLong(obj.getJSONObject(i).getString("id")));
                                        v.setNom(obj.getJSONObject(i).getString("nom"));
                                        v.setDescription(obj.getJSONObject(i).getString("description"));
                                        v.setCategorie(obj.getJSONObject(i).getString("categorie"));
                                        v.setUser_id(Long.parseLong(obj.getJSONObject(i).getString("user_id")));
                                        v.setSource(obj.getJSONObject(i).getString("source"));
                                        v.setType(obj.getJSONObject(i).getString("type"));
                                        v.setTags(obj.getJSONObject(i).getString("tags"));
                                        videoList.add(v);
                                    }
                                    Log.e("VakoError : ", String.valueOf(videoList.size()));
                                    adapter = new VideoAdapter(context, videoList,videoList);
                                    recyclerView.setAdapter(adapter);

                                } else {

                                }



                            } else if(objData.getBoolean("error")){

                                //displayToast("L'utilisateur n'a aucune videos.");
                            }
                        } catch (JSONException e) {
                            //Dismiss the dialog
                            //displayToast("Recherche impossible."+e.getMessage());
                            Log.e("Exception volley", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        //displayToast("Authentication failed."+error.getMessage());
                        //Log.e("Exception volley", error.getMessage());

                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "cent:capp7622argent";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);


    }

    public void displayToast(String message){
        Snackbar snackbar = Snackbar
                .make(getView(), message, Snackbar.LENGTH_LONG)
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
