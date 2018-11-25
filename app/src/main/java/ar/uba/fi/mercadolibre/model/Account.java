package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account extends BaseModel {
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("profile_picture_url")
    @Expose
    private String profilePictureURL;

    @SerializedName("user_id")
    @Expose
    private String userID;

    @SerializedName("score")
    @Expose
    private int score;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureURL() {
        return this.profilePictureURL;
    }

    public void setProfilePictureURL(String path) {
        this.profilePictureURL = path;
    }

    public String getUserID() {
        return userID;
    }

    public int getScore() {
        return score;
    }
}
