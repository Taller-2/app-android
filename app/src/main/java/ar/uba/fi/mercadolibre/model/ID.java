package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ID {
    @SerializedName("$oid")
    @Expose
    private String id;

    String getID() {
        return id;
    }
}
