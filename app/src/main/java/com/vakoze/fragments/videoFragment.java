package com.vakoze.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vakoze.R;
import com.vakoze.adapters.VideoAdapter;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.MyDividerItemDecoration;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.Video;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class videoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnTouchListener, VideoAdapter.VideoAdapterListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnFragmentInteractionListener mListener;
    private List<Video> videoGroupList;
    private VideoAdapter adapter;
    private RecyclerView recyclerView;
    private List<Video> video;
    private List<Video> mVideos ;
    private SearchView searchView;
    private ProgressBar progressBar;
    View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public videoFragment() {
        this.videoGroupList = new ArrayList();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PublicVideoFragment newInstance(int columnCount) {
        PublicVideoFragment fragment = new PublicVideoFragment();
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
        setHasOptionsMenu(true);
        videoGroupList = search();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video_list, container, false);
        Context context = view.getContext();
        progressBar = view.findViewById(R.id.videoListProgress);

        // white background notification bar
        //whiteNotificationBar(recyclerView);

        // Set the adapter
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 36));

        /*
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //recyclerView.setAdapter(new MyvideoRecyclerViewAdapter(Video.ITEMS, mListener));

        }*/

        recyclerView.setOnTouchListener(this);
        if(isConnected()){
            //searchVideo();
            videoGroupList = search();
            adapter = new VideoAdapter(getActivity(), videoGroupList,videoGroupList);
            recyclerView.setAdapter(adapter);
        } else {
            //Toast.makeText(getContext(), "Vous n'êtes pas connecté à internet. Rafraichissez la page", Toast.LENGTH_SHORT).show();
            displayToast("Problèmes de connexion internet");
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //adapter.filter(query);
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                //adapter.filter(query);
                adapter.getFilter().filter(query);
                return false;
            }


        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.getFilter().filter("");
                //videoGroupList = search();
                //adapter = new PublicVideoAdapter(getActivity(), videoGroupList,videoGroupList);
                //recyclerView.setAdapter(adapter);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                // do s.th.
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
            videoGroupList = search();
            adapter = new VideoAdapter(getActivity(), videoGroupList,videoGroupList);
            recyclerView.setAdapter(adapter);
        } else {
            //Toast.makeText(getContext(), "Vous n'êtes pas connecté à internet. Rafraichissez la page", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        getView().getParent().requestDisallowInterceptTouchEvent(true); // Pour Le Scroll pour qu'il soit opérationnel
        return false;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Video item);


    }
    @Override
    public void onRefresh() {
        //appellé lors de l'action Pull To Refresh
        //searchVideo();
        videoGroupList = search();

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
                                //adapter = new PublicVideoAdapter( getActivity(), videoGroupList, this);
                            }
                            //recyclerView.setAdapter(adapter);
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
    private List<Video> search(){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/video/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();
                            //Toast.makeText(getContext(), obj.toString(), Toast.LENGTH_SHORT).show();
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
                                adapter = new VideoAdapter( getActivity(), videoGroupList, videoGroupList);
                            }
                            recyclerView.setAdapter(adapter);

                            Log.e("VakoTest", "Video Loaded");
                            //displayToast("Video Loaded");
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            //Dismiss the dialog


                            //Toast.makeText(getContext(), "Recherche impossible."+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("VakoTest", "Video not Loaded for JsonException"+e.getMessage());
                            displayToast("Video not Loaded for JsonException"+e.getMessage());
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog
                        //Toast.makeText(getContext(), "Connexion impossible.", Toast.LENGTH_SHORT).show();
                        Log.e("VakoTest", "Video not Loaded for VolleyError"+error.getMessage());
                        displayToast("Video not Loaded for VolleyError"+error.getMessage());
                        progressBar.setVisibility(View.GONE);
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
        return videoGroupList;
    }
    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getActivity().getWindow().setStatusBarColor(Color.WHITE);
        }
    }
    @Override
    public void onVideoSelected(Video video) {
        //Toast.makeText(getActivity(), "Selected: " + video.getNom() + ", " + video.getDescription(), Toast.LENGTH_LONG).show();
    }

    public void displayToast(String message){
        /*
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
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
        /*
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();*/
    }
}