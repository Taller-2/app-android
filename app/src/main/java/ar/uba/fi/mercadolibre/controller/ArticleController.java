package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.Article;
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

    @GET("article/")
    Call<APIResponse<List<Article>>> listByName(@Query("name") String name);

    @GET("article/")
    Call<APIResponse<List<Article>>> listByUser(@Query("user") String user);

    @GET("article/")
    Call<APIResponse<List<Article>>> search(
            @Query("name") String name,
            @Query("my_lat") Double myLatitude,
            @Query("my_lon") Double myLongitude,
            @Query("max_distance") Double maxDistance,
            @Query("price_min") Double priceMin,
            @Query("price_max") Double priceMax
    );

    @POST("article/")
    Call<Article> create(@Body Article article);

    @DELETE("article/{id}/")
    Call<Object> destroy(@Path("id") String id);

    @PATCH("article/")
    Call<Article> update(@Body Article article);
}
