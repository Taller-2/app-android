package ar.uba.fi.mercadolibre.controller;

import ar.uba.fi.mercadolibre.model.Account;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

public interface AccountController {
    @GET("account/current/")
    Call<APIResponse<Account>> currentAccount();

    @PATCH("account/current/")
    Call<Object> updateCurrentAccount(@Body Account account);
}
