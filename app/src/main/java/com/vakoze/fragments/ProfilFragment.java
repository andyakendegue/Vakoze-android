package com.vakoze.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.vakoze.private_profile.ModifierInfosActivity;
import com.vakoze.R;
import com.vakoze.lib.EndPoints;
import com.vakoze.lib.SharedPrefManager;
import com.vakoze.lib.VolleyMultipartRequest;
import com.vakoze.models.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentTabHost mTabHost;
    private TextView textNom, textPrenom, textEmail, textPhone, nbrFan, nbrAbo;
    private ImageView profile_pic;
    private Button btnModify;

    // Phone number
    String TAG = "ProfilFragmentTAG";
    Context mContext;
    Activity activity = (Activity)mContext;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    View profilView;
    User user;
    private static final int PERMISSION_REQUEST_CODE = 1;
    FragmentPagerAdapter adapterViewPager;

    public ProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
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
        //getting the current user
        user = SharedPrefManager.getInstance(getActivity()).getUser();
        searchFollows(user.getId());
        searchFans(user.getId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profilView = inflater.inflate(R.layout.fragment_profil, container, false);
        textNom = profilView.findViewById(R.id.textNom);
        textPrenom = profilView.findViewById(R.id.textPrenom);
        textEmail = profilView.findViewById(R.id.textEmail);
        textPhone = profilView.findViewById(R.id.textPhone);
        profile_pic = profilView.findViewById(R.id.profile_pic);
        nbrFan = profilView.findViewById(R.id.nbrFan);
        nbrFan.setText("0");
        nbrAbo = profilView.findViewById(R.id.nbrAbo);
        nbrAbo.setText("0");
        //profile_pic.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_square);
        profile_pic.setImageResource(R.drawable.profile_pic);
        btnModify = profilView.findViewById(R.id.btnModified);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent modifyProfile = new Intent(getActivity(), ModifierInfosActivity.class);
                startActivity(modifyProfile);
            }
        });
        if(user.getProfile_pic()!=null&&!user.getProfile_pic().isEmpty()&&!user.getProfile_pic().equals("")){
            Glide.with(getActivity())
                    .load(user.getProfile_pic())
                    //.fitCenter()
                    .into(profile_pic);
        } else {
            //profile_pic.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_square);
            //profile_pic.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

            Glide.with(getActivity())
                    //.load(user.getProfile_pic())
                    .load(Uri.parse("android.resource://com.vakoze/" + R.drawable.profile_pic))
                    //.fitCenter()
                    .into(profile_pic);
        }
        //setting the values to the textviews
        textNom.setText(user.getNom());
        //toolbar.setBackgroundResource();
        textPrenom.setText(user.getPrenom());
        textEmail.setText(user.getEmail());
        if(user.getPhone().equals("")||user.getPhone().equals(null)){
            textPhone.setText(String.valueOf(user.getId()));
        } else {
            textPhone.setText(String.valueOf(user.getPhone()));
        }

        /*
        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
            Log.d(TAG, "Phone number: " + getPhone());
        }*/
        /*
        TelephonyManager mTelephonyMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

             String imsi = mTelephonyMgr.getSubscriberId();
        }
        */
        ViewPager vpPager = (ViewPager) profilView.findViewById(R.id.container);
        adapterViewPager = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) profilView.findViewById(R.id.tabProfil);
        tabLayout.setupWithViewPager(vpPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_actualites2);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_reposts2);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_categories2);
        return profilView;
    }

    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return phoneMgr.getLine1Number();
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Phone number: " + getPhone());
                } else {
                    Toast.makeText(activity,"Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission((Activity)mContext, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
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
        //mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
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

    public void searchFans(Long id){
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/fans/"+id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject objData = null;
                        try {
                            objData = new JSONObject(new String(response.data));

                            if(!objData.getBoolean("error")){

                                nbrFan.setText(objData.getString("fans"));
                            } else if(objData.getBoolean("error")){
                                nbrFan.setText("0");
                                displayToast("L'utilisateur n'a aucun fan.");
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
    public void searchFollows(Long id){
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/follow/"+id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        JSONObject objData = null;
                        try {
                            objData = new JSONObject(new String(response.data));

                            if(!objData.getBoolean("error")){
                                nbrAbo.setText(objData.getString("followers"));

                            } else if(objData.getBoolean("error")){
                                nbrAbo.setText("0");
                                displayToast("L'utilisateur n'a aucun Abonnements.");
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

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        android.content.res.Resources res;
        int[] clrItems;
        private int NUM_ITEMS = 3;
        private String tabTitles[] = new String[] { "Mes Videos", "Mes Reposts", "Mes Catégories" };
        private int[] imageResId = new int[]{
                R.drawable.ic_actualites2,
                R.drawable.ic_reposts2,
                R.drawable.ic_categories2};

        private Context context;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    //return FirstFragment.newInstance(0, "Page # 1");
                    return new MesVideosFragment();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    //return FirstFragment.newInstance(1, "Page # 2");
                    return new MesRepostsFragment();
                case 2: // Fragment # 1 - This will show SecondFragment
                    //return SecondFragment.newInstance(2, "Page # 3");
                    return new MesCategoriesVideosFragment();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator

        @Override
        public CharSequence getPageTitle(int position) {

            return tabTitles[position] ;
        }

    }

}
