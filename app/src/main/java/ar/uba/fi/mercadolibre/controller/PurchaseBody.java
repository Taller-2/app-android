package ar.uba.fi.mercadolibre.controller;

public class PurchaseBody {
    private String article_id;
    private int units;

    public PurchaseBody(String article_id, int units) {
        this.article_id = article_id;
        this.units = units;
    }
}
