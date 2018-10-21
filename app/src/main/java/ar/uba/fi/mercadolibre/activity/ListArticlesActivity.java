package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.dialogs.ArticlesFilterDialog;
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

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            listAllArticles();
            return;
        }

        String name = (String) extras.get("article_name");

        if (name == null || name.length() == 0){
            listAllArticles();
            return;
        }
        listArticlesByName(name);
    }

    private void listArticlesByName(String name) {
        listArticles(ControllerFactory.getArticleController().listByName(name));
    }
    private void listAllArticles() {
        listArticles(ControllerFactory.getArticleController().list());
    }
    private void listArticles(Call<APIResponse<List<Article>>> articlesCall) {
        articlesCall.enqueue(new Callback<APIResponse<List<Article>>>() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_articles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.list_article_settings) {
            DialogFragment newFragment = new ArticlesFilterDialog();
            newFragment.show(getSupportFragmentManager(), "filters");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
