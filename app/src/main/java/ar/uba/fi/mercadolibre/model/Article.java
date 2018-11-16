package ar.uba.fi.mercadolibre.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import retrofit2.Callback;

public class Article extends BaseModel {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("available_units")
    @Expose
    private int availableUnits;

    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("pictures")
    @Expose
    private ArrayList<String> pictures;

    @SerializedName("tags")
    @Expose
    private ArrayList<String> tags;

    public Article(String name, String description, int availableUnits, int price,
                   double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.availableUnits = availableUnits;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pictures = new ArrayList<>();
    }

    public Article() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(int lengthLimit) {
        if (name == null || name.length() <= lengthLimit) return getName();
        return name.substring(0, lengthLimit) + "...";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<String> getPictureURLs() {
        return pictures;
    }

    public void setLatLon(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void post(Callback<Article> callback) {
        ControllerFactory.getArticleController()
                .create(this)
                .enqueue(callback);
    }

    public void addPicture(String pictureURL) {
        if (this.pictures == null) {
            this.pictures = new ArrayList<>();
        }
        this.pictures.add(pictureURL);
    }

    public IGeoPoint getGeoPoint() {
        return new GeoPoint(this.latitude, this.longitude);
    }
}
