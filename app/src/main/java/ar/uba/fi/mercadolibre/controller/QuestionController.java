package ar.uba.fi.mercadolibre.controller;

import java.util.List;

import ar.uba.fi.mercadolibre.model.Question;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QuestionController {

    @GET("article/{id}/question/")
    Call<APIResponse<List<Question>>> list(
            @Path("id") String articleId
    );

    @POST("article/{id}/question/")
    Call<Question> create(@Path("id") String articleId,
                          @Body Question question);

    @GET("question/")
    Call<APIResponse<List<Question>>> listByArticleOwner(
            @Query("owner") String userId
    );

    @PATCH("question/")
    Call<Question> patch(@Body Question question);

    @GET("question/{id}/")
    Call<Question> get(@Path("id") String questionId);
}
