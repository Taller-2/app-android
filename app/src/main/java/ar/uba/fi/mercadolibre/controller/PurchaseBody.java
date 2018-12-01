package ar.uba.fi.mercadolibre.controller;

import ar.uba.fi.mercadolibre.model.ShipmentCost;

public class PurchaseBody {
    private String article_id;
    private int units;
    private double price;
    private String payment_method;
    private String shipment_address;

    public PurchaseBody(String article_id,
                        int units,
                        double price,
                        ShipmentCost.PaymentMethod payment_method,
                        String shipment_address) {
        this.article_id = article_id;
        this.units = units;
        this.price = price;
        switch (payment_method) {
            case CASH:
                this.payment_method = "cash";
                break;
            case DEBIT:
                this.payment_method = "debit";
                break;
            case CREDIT:
                this.payment_method = "credit";
        }
        this.shipment_address = shipment_address;
    }
}
