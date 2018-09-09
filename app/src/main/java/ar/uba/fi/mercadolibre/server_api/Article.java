package ar.uba.fi.mercadolibre.server_api;

import java.util.HashMap;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Article {
    @POST("article/")
    Single<Object> post(@Header("Content-Type") String content_type, @Body HashMap<String, Object> body);
}
