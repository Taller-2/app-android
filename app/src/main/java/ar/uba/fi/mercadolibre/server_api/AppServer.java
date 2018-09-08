package ar.uba.fi.mercadolibre.server_api;

import android.util.Log;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppServer {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private String url;

    public AppServer(String url) {
        this.url = url;
    }

    public void api() {
        // Actualmente esto siempre falla porque falta una API key, pero funciona,
        // Se loguea el error en el onError abajo
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Single<ExampleResponse> response = apiService.getExample(3);

        response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ExampleResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(ExampleResponse person) {
                        Log.i("Sign up", person.getStatusMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Sign up", e.getLocalizedMessage());

                    }
                });
    }

}
