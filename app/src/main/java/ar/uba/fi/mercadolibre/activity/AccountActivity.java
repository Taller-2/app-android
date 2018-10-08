package ar.uba.fi.mercadolibre.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends BaseActivity {
    Account account = null;

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
                account = getData(response);
                if (account == null) return;
                updateShownAccount(account);
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call, @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    private void updateShownAccount(Account account) {
        fillTextView(R.id.email, account.getEmail());
        fillTextView(R.id.name, account.getName());
        fillImageViewWithURL(R.id.picture, account.getPicture());
    }

    public void onChangeName(View view) {
        onChange("name", R.string.name);
    }

    public void onChangeEmail(View view) {
        onChange("email", R.string.email);
    }

    private void onChange(final String attributeName, int alertTitle) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);

        alert.setView(input);
        alert.setTitle(alertTitle);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int button) {
                String inputText = input.getText().toString();
                if (attributeName.equals("name")) {
                    account.setName(inputText);
                } else if (attributeName.equals("email")) {
                    account.setEmail(inputText);
                }
                ControllerFactory.getAccountController().updateCurrentAccount(account).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if (!response.isSuccessful()) {
                            onChangeError(response.toString());
                            return;
                        }
                        updateShownAccount(account);
                        toast(R.string.generic_success);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        onChangeError(t.getMessage());
                    }
                });
            }
        });
        alert.show();
    }

    private void onChangeError(String message) {
        Log.e("Account PATCH", message);
        toast(R.string.generic_error);
        finish();
    }
}
