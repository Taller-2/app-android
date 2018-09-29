package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ArticleController {
    @GET("article/")
    Call<APIResponse<List<Article>>> list();

    @POST("article/")
    Call<Article> create(@Body Article article);

    @DELETE("article/{id}/")
    Call<Object> destroy(@Path("id") String id);
}
