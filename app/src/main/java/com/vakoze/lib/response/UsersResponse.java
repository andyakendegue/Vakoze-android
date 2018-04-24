package com.vakoze.lib.response;

import com.vakoze.lib.mention.MentionPerson;

import java.util.ArrayList;

/**
 * Created by capp on 20/03/2018.
 */

public class UsersResponse {

    String status;
    String msg;

    ArrayList<MentionPerson>  data;

    public ArrayList<MentionPerson> getData() {
        return data;
    }
}
