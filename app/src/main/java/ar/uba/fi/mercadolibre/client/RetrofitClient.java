package ar.uba.fi.mercadolibre.client;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static String firebaseToken = null;
    private static Retrofit.Builder retrofit = null;

    public static Retrofit getClient(String url) {
        if (retrofit != null) return retrofit.build();
        retrofit = new Retrofit
                .Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
        if (firebaseToken != null) {
            retrofit.client(httpClientBuilder().build());
        }
        return retrofit.build();
    }

    private static OkHttpClient.Builder httpClientBuilder() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                return chain.proceed(
                        chain.request().newBuilder().addHeader(
                                "Authorization",
                                firebaseToken
                        ).build()
                );
            }
        });
        return httpClientBuilder;
    }
}
