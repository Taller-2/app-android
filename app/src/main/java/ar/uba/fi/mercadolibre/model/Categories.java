package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Categories implements Serializable {

    @SerializedName("categories")
    @Expose
    private ArrayList<String> categories;

    public ArrayList<String> getCategories() {
        return categories;
    }
}
