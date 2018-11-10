package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShipmentCost {
    public static final String STATUS_ENABLED = "enabled";
    public static final String STATUS_FREE = "free";
    public static final String STATUS_DISABLED = "disabled";

    @SerializedName("cost")
    @Expose
    private float cost;

    @SerializedName("status")
    @Expose
    private String status;

    public boolean isEnabled() {
        if (status == null) return false;
        return !status.equals(STATUS_DISABLED);
    }

    public float getCost() {
        return cost;
    }
}
