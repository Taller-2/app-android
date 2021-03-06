package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.Purchase;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PurchaseController {

    @GET("purchase/")
    Call<APIResponse<List<Purchase>>> getUserPurchases();

    @POST("purchase/")
    Call<Object> purchaseArticle(@Body PurchaseBody body);

    @GET("my_sales/")
    Call<APIResponse<List<Purchase>>> getUserSales();
}
