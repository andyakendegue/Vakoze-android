package com.vakoze.lib;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.vakoze.models.User;

/**
 * Created by capp on 13/02/2018.
 */

public class TimelineFunctions {

    public void like(){

    }
    public void comment(){

    }
    public void sharePost(){

    }
    public void repost(){

    }
    public void subscribe(){

    }
    public void unsubscribe(){

    }
    public void search(){

    }
    public void caution(){

    }

    public static User searchUser(final String UserId, final Context context){
        final User[] user = new User[1];



        //user = new User();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, EndPoints.UPLOAD_URL+"/user/all",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONArray obj = new JSONArray(new String(response.data));
                            int objLength = obj.length();

                            for (int i = 0; i < objLength; i++)
                            {
                                JSONObject objData=obj.getJSONObject(i);
                                Long id = objData.getLong("id");
                                if(id == Long.parseLong(UserId)){


                                    user[0] = new User(Long.parseLong(objData.getString("id")), objData.getString("u_id"), objData.getString("nom"), objData.getString("prenom"), objData.getString("email"), objData.getString("phone"), objData.getString("profile_pic"));


                                }

                            }







                        } catch (JSONException e) {
                            //Dismiss the dialog


                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Dismiss the dialog


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
        Volley.newRequestQueue(context).add(volleyMultipartRequest);


        if(user[0] == null){
            return null;
        } else {
            return user[0];
        }

    }


}
