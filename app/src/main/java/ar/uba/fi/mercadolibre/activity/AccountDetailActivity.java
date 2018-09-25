package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountDetailActivity extends BaseActivity {
    @Override
    public int identifierForDrawer() {
        return MY_ACCOUNT_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<Account>() {
            @Override
            public void onResponse(@NonNull Call<Account> call, @NonNull Response<Account> response) {
                Account account = response.body();
                if (!response.isSuccessful() || account == null) {
                    onFetchFailure();
                    ResponseBody body = response.errorBody();
                    Log.e(
                            "Account fetch",
                            body != null ? body.toString() : "Empty response"
                    );
                    return;
                }
                onFetchSuccess(account);
            }

            @Override
            public void onFailure(@NonNull Call<Account> call, @NonNull Throwable t) {
                onFetchFailure();
                Log.e("Account fetch", t.getMessage());
            }
        });
    }

    private void onFetchFailure() {
        toast(R.string.generic_error);
        finish();
    }

    private void onFetchSuccess(Account account) {
        fillTextView(R.id.email, account.getEmail());
        fillTextView(R.id.name, account.getName());
        fillImageViewWithURL(R.id.picture, account.getPicture());
    }
}