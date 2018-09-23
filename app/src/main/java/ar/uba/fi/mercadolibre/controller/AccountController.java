package ar.uba.fi.mercadolibre.controller;

import ar.uba.fi.mercadolibre.model.Account;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AccountController {
    @GET("account/current/")
    Call<Account> currentAccount();
}
