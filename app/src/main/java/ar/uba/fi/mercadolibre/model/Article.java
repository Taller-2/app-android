package ar.uba.fi.mercadolibre.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ArticleController;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public double getPrice() {
        return price;
    }

    public ArrayList<String> getPictureURLs() {
        return pictures;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
    }

    public void post(Callback<Article> callback) {
        ControllerFactory.getArticleController()
                .create(this)
                .enqueue(callback);
    }

    public void addPicture(String pictureURL) {
        this.pictures.add(pictureURL);
    }
}
