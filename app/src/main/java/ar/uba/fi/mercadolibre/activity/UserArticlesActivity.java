package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.controller.InvalidResponseException;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.Article;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserArticlesActivity extends BaseActivity {
    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_articles);
        getAccount();
    }

    void getAccount() {
        ControllerFactory.getAccountController().currentAccount().enqueue(new Callback<APIResponse<Account>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<Account>> call, @NonNull Response<APIResponse<Account>> response) {
                Account account = null;
                try {
                    if (response.body() != null) account = response.body().getData();
                } catch (InvalidResponseException e) {
                    onFailure(call, e);
                    return;
                }
                if (!response.isSuccessful() || account == null) {
                    onFetchFailure();
                    ResponseBody body = response.errorBody();
                    Log.e(
                            "Account fetch",
                            body != null ? body.toString() : "Empty response"
                    );
                    return;
                }
                fillList(account);
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call, @NonNull Throwable t) {
                onFetchFailure();
                Log.e("Account fetch", t.getMessage());
            }
        });
    }

    private void onFetchFailure() {
        toast(R.string.generic_error);
        finish();
    }

    private void fillList(Account account) {
        Log.d("UAA", account.getUserID());
        ControllerFactory.getArticleController().listByUser(account.getUserID()).enqueue(new Callback<APIResponse<List<Article>>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<Article>>> call,
                                   @NonNull Response<APIResponse<List<Article>>> response) {
                List<Article> articles = getData(response);
                if (articles == null) return;
                if (articles.isEmpty()) {
                    toast(R.string.user_has_no_articles);
                    return;
                }
                ((ListView) findViewById(R.id.articleList)).setAdapter(
                        new ArticleAdapter(UserArticlesActivity.this, articles, true)
                );
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<Article>>> call,
                                  @NonNull Throwable t) {
                Log.e("Articles GET", t.getMessage());
                toast(R.string.generic_error);
            }
        });
    }
}
