package vakoze.blomidtech.vakoze.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vakoze.blomidtech.vakoze.R;
import vakoze.blomidtech.vakoze.adapters.CustomAdapter;
import vakoze.blomidtech.vakoze.fragments.dummy.DummyContent.DummyItem;
import vakoze.blomidtech.vakoze.lib.EndPoints;
import vakoze.blomidtech.vakoze.lib.SharedPrefManager;
import vakoze.blomidtech.vakoze.models.Contact;
import vakoze.blomidtech.vakoze.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<Contact> contactModelArrayList;
    private static ProgressDialog progressDialog;
    View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactFragment newInstance(int columnCount) {
        ContactFragment fragment = new ContactFragment();
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
        view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        listView = (ListView) view.findViewById(R.id.listView);

        Collection<User> users = getAllUsers();

        contactModelArrayList = new ArrayList<>();

        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Contact contactModel = new Contact();
            contactModel.setName(name);
            contactModel.setNumber(phoneNumber);
            contactModelArrayList.add(contactModel);
            Log.d("name>>",name+"  "+phoneNumber);
        }
        phones.close();

        customAdapter = new CustomAdapter(getActivity(),contactModelArrayList);
        listView.setAdapter(customAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public List<User> getAllUsers() {
        final List<User> users = null;

        loadProgress();
        //getting the current user
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        final Long userId = user.getId();
        //our custom volley request
        JsonObjectRequest volleyMultipartRequest = new JsonObjectRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        JSONObject obj = null;
                        JSONArray JsonUsers = null;
                        try {
                            //obj = new JSONObject(response);
                            JsonUsers = new JSONArray(response.getString("users"));
                            int objLength = JsonUsers.length();
                            int i = 0;
                            users.clear();

                            for (i = 0; i < objLength; i++)
                            {
                                User contact = new User(Long.parseLong(JsonUsers.getJSONObject(i).getString("")),JsonUsers.getJSONObject(i).getString(""), JsonUsers.getJSONObject(i).getString(""), JsonUsers.getJSONObject(i).getString(""), JsonUsers.getJSONObject(i).getString(""), JsonUsers.getJSONObject(i).getString(""),JsonUsers.getJSONObject(i).getString(""));

                                users.add(contact);
                            }


                            if(!response.getBoolean("error")){
                                displayToast("Upload Réussi");
                                Log.e("VakoError : Upload Réussi",response.getString("message"));
                            } else {
                                displayToast("Upload Non Réussi :"+response.getString("message"));
                                Log.e("VakoError : Upload Non Réussi",response.getString("message"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayToast("erreur"+e.getMessage());
                            Log.e("VakoError : "+e.getMessage(),String.valueOf(response));
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


        return users;
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
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

}
