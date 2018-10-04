package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListArticlesActivity extends BaseActivity {
    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_articles);

        ControllerFactory.getArticleController().list().enqueue(new Callback<APIResponse<List<Article>>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<Article>>> call,
                                   @NonNull Response<APIResponse<List<Article>>> response) {
                List<Article> articles = getData(response);
                if (articles == null) return;
                if (articles.isEmpty()) {
                    toast(R.string.no_results);
                    return;
                }
                ((ListView) findViewById(R.id.articleList)).setAdapter(
                        new ArticleAdapter(ListArticlesActivity.this, articles, false)
                );
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<Article>>> call,
                                  @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }
}
