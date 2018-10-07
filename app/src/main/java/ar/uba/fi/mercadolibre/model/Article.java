package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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

    public ArrayList<String> getPictures() {
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
    
}
