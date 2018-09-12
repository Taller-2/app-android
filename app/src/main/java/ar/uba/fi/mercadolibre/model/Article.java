package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Article {
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
    private int price;

    public Article(String name, String description, int availableUnits, int price) {
        this.name = name;
        this.description = description;
        this.availableUnits = availableUnits;
        this.price = price;
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
    public int getPrice() {
        return price;
    }
}
