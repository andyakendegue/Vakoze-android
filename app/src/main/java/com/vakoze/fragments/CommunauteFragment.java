package com.vakoze.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vakoze.adapters.ItemChatAdapter;
import com.vakoze.chatFirebase.MessengerActivity;
import com.vakoze.R;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.MyDividerItemDecoration;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.UserListChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommunauteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommunauteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunauteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnTouchListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private List<UserListChat> chatList;

    private SearchView searchView;
    private ProgressBar progressBar;
    View view;
    private ItemChatAdapter adapter;

    private RecyclerView recyclerView;
    TextView no_message;
    Button chatbutton;

    public CommunauteFragment() {
        // Required empty public constructor
        this.chatList = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunauteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunauteFragment newInstance(String param1, String param2) {
        CommunauteFragment fragment = new CommunauteFragment();
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

        setHasOptionsMenu(true);
        chatList = search();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_communaute, container, false);
        chatbutton = view.findViewById(R.id.chatButtonTest);
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getActivity(), MessengerActivity.class);
                startActivity(chatIntent);
            }
        });
        no_message = view.findViewById(R.id.no_message);

        Context context = view.getContext();
        //progressBar = view.findViewById(R.id.videoListProgress);

        // white background notification bar
        //whiteNotificationBar(recyclerView);

        // Set the adapter
        recyclerView = view.findViewById(R.id.userChatRecyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 36));

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
            no_message.setVisibility(View.INVISIBLE);
            chatbutton.setVisibility(View.INVISIBLE);
            chatList = search();
            adapter = new ItemChatAdapter(getActivity(), chatList);
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
                //chatList = search();
                //adapter = new PublicVideoAdapter(getActivity(), chatList,chatList);
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
            chatList = search();
            adapter = new ItemChatAdapter(getActivity(), chatList);
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
    @Override
    public void onRefresh() {
        //appellé lors de l'action Pull To Refresh
        //searchVideo();
        chatList = search();

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

    private List<UserListChat> search(){
        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject o = new JSONObject(new String(response.data));
                            JSONArray obj = new JSONArray(o.getString("users"));
                            int objLength = obj.length();
                            //Toast.makeText(getContext(), obj.toString(), Toast.LENGTH_SHORT).show();
                            int i = 0; chatList.clear();
                            for (i = 0; i < objLength; i++)
                            {
                                UserListChat video = new UserListChat(obj.getJSONObject(i).getString("u_id"),obj.getJSONObject(i).getString("nom"),obj.getJSONObject(i).getString("profile_pic"));
                                /*video.setId(Long.parseLong(obj.getJSONObject(i).getString("id")));
                                //video.setDate_ajout(Date.parse(objData.getString("date_ajout")));
                                video.setCategorie(obj.getJSONObject(i).getString("categorie"));
                                video.setNom(obj.getJSONObject(i).getString("nom"));
                                video.setTags(obj.getJSONObject(i).getString("tags"));
                                video.setType(obj.getJSONObject(i).getString("type"));
                                video.setSource(obj.getJSONObject(i).getString("source"));
                                video.setUser_id(Long.parseLong(obj.getJSONObject(i).getString("user_id")));*/
                                chatList.add(video);
                            }


                            if (chatList!=null) {
                                //Toast.makeText(getContext(), chatList.get(1).getNom(), Toast.LENGTH_SHORT).show();
                                adapter = new ItemChatAdapter( getActivity(), chatList);
                            }
                            recyclerView.setAdapter(adapter);

                            Log.e("VakoTest", "Video Loaded");
                            //displayToast("Video Loaded");
                            //progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            //Dismiss the dialog


                            //Toast.makeText(getContext(), "Recherche impossible."+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("VakoTest", "Video not Loaded for JsonException"+e.getMessage());
                            displayToast("Video not Loaded for JsonException"+e.getMessage());
                            //progressBar.setVisibility(View.GONE);
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
                        //progressBar.setVisibility(View.GONE);
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
        return chatList;
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
