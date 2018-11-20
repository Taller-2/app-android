package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.Categories;
import ar.uba.fi.mercadolibre.model.ShipmentCost;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ArticleController {
    @GET("article/")
    Call<APIResponse<List<Article>>> list();

    @GET("article/{id}")
    Call<Article> getByID(@Path("id") String id);

    @GET("article/{id}/shipment_cost")
    Call<APIResponse<ShipmentCost>> shipmentCost(
            @Path("id") String id,
            @Query("my_lat") Double myLatitude,
            @Query("my_lon") Double myLongitude,
            @Query("payment_method") String paymentMethod
    );

    @GET("article/")
    Call<APIResponse<List<Article>>> listByUser(@Query("user") String user);

    @GET("article/")
    Call<APIResponse<List<Article>>> search(
            @Query("name_contains") String name,
            @Query("my_lat") Double myLatitude,
            @Query("my_lon") Double myLongitude,
            @Query("max_distance") Double maxDistance,
            @Query("price_min") Double priceMin,
            @Query("price_max") Double priceMax,
            @Query("tags") String category
    );

    @POST("article/")
    Call<Article> create(@Body Article article);

    @DELETE("article/{id}/")
    Call<Object> destroy(@Path("id") String id);

    @PATCH("article/")
    Call<Article> update(@Body Article article);

    @GET("article/categories")
    Call<Categories> listCategories();
}
