package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account {
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

    public String getName() {
        return name;
    }

    public String getProfilePictureURL() {
        return this.profilePictureURL;
    }

    public String getUserID() {
        return userID;
    }

    public int getScore() {
        return score;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePictureURL(String path) {
        this.profilePictureURL = path;
    }
}
