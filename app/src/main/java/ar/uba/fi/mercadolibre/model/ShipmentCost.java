package ar.uba.fi.mercadolibre.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShipmentCost implements Serializable {
    public static final String STATUS_ENABLED = "enabled";
    public static final String STATUS_FREE = "free";
    public static final String STATUS_DISABLED = "disabled";

    @SerializedName("cash")
    @Expose
    private PaymentMethodDetail cash;

    @SerializedName("credit")
    @Expose
    private PaymentMethodDetail credit;

    @SerializedName("debit")
    @Expose
    private PaymentMethodDetail debit;

    public boolean isEnabled(PaymentMethod method) {
        switch (method) {
            case CASH:
                return cash.status != null && !cash.status.equals(STATUS_DISABLED);
            case CREDIT:
                return credit.status != null && !credit.status.equals(STATUS_DISABLED);
            case DEBIT:
                return debit.status != null && !debit.status.equals(STATUS_DISABLED);
        }
        return false;
    }

    public float getCost(PaymentMethod method) {
        switch (method) {
            case CASH:
                return cash.cost;
            case CREDIT:
                return credit.cost;
            case DEBIT:
                return debit.cost;
        }
        return 0;
    }

    public enum PaymentMethod {
        CASH, CREDIT, DEBIT
    }

    private class PaymentMethodDetail implements Serializable {
        @SerializedName("cost")
        @Expose
        private float cost;

        @SerializedName("status")
        @Expose
        private String status;
    }
}
