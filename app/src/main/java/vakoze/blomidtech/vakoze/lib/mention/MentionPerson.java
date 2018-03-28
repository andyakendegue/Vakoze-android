package vakoze.blomidtech.vakoze.lib.mention;

import com.google.gson.annotations.SerializedName;

/**
 * Created by capp on 20/03/2018.
 */

public class MentionPerson {

    @SerializedName("value")
    public String name;
    @SerializedName("uid")
    public String id;
    @SerializedName("image")
    public String imageURL;

    public String getFormattedValue() {
        return "@[" + name + "](" + id + ")";
    }

    public MentionPerson() {
    }

    public MentionPerson(String name, String id, String imageURL) {

        this.name = name;
        this.id = id;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return name;
    }
}
