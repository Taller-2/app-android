package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
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

        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<APIResponse<Account>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Account>> call, @NonNull Response<APIResponse<Account>> response) {
                Account account = getData(response);
                if (account == null) return;
                onGetDataSuccess(account);
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call, @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    private void onGetDataSuccess(Account account) {
        fillTextView(R.id.email, account.getEmail());
        fillTextView(R.id.name, account.getName());
        fillImageViewWithURL(R.id.picture, account.getPicture());
    }
}
