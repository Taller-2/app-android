package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserArticlesActivity extends BaseActivity {
    Account account = null;

    public static final int USER_ARTICLES_ACTIVITY_RESULT = 1;

    @Override
    public int identifierForDrawer() {
        return MY_ITEMS_IDENTIFIER;
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
                account = getData(response);
                if (account == null) return;
                fillList();
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<Account>> call, @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    private void fillList() {
        Log.d("UAA", account.getUserID());
        ControllerFactory.getArticleController().listByUser(account.getUserID()).enqueue(new Callback<APIResponse<List<Article>>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<Article>>> call,
                                   @NonNull Response<APIResponse<List<Article>>> response) {
                final List<Article> articles = getData(response);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == USER_ARTICLES_ACTIVITY_RESULT) {
            fillList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (account != null) {
            fillList();
        }
    }
}
