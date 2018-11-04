package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.Purchase;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PurchaseController {

    @GET("purchase/")
    Call<APIResponse<List<Purchase>>> getUserPurchases();

}
