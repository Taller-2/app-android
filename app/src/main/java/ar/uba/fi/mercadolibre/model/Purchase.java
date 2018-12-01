package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Purchase extends BaseModel {
    public enum Status {
        PENDING, DONE, CANCELLED, DOES_NOT_EXIST
    }

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;

    @SerializedName("shipment_status")
    @Expose
    private String shipmentStatus;

    @SerializedName("article")
    @Expose
    private Article article;

    @SerializedName("units")
    @Expose
    private int units;

    public Article getArticle() {
        return article;
    }

    public Status getPaymentStatus() {
        if (paymentStatus == null) return Status.DOES_NOT_EXIST;
        switch (paymentStatus) {
            case "pending":
                return Status.PENDING;
            case "approved":
                return Status.DONE;
            default:
                return Status.CANCELLED;
        }
    }

    public Status getShipmentStatus() {
        if (shipmentStatus == null) return Status.DOES_NOT_EXIST;
        switch (shipmentStatus) {
            case "pending":
                return Status.PENDING;
            case "shipped":
                return Status.DONE;
            default:
                return Status.CANCELLED;
        }
    }
}
