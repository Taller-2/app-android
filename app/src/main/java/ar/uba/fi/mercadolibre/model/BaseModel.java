package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseModel {
    @SerializedName("_id")
    @Expose
    private ID id;

    public String getID() {
        return id.getID();
    }
}
