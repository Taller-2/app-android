package ar.uba.fi.mercadolibre.ServerApi;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    final String endpoint = "";
    @GET("person/{person_id}")
    Single<ExampleResponse> getExample(@Path("person_id") int personId);
}
