package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShipmentCost {
    @SerializedName("cost")
    @Expose
    private float cost;

    @SerializedName("status")
    @Expose
    private String status;

    public boolean isEnabled() {
        if (status == null) return false;
        return !status.equals("disabled");
    }

    public float getCost() {
        return cost;
    }
}
