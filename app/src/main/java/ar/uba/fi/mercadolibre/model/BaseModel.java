package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseModel implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;

    BaseModel() {
    }

    public String getID() {
        return id;
    }
}
