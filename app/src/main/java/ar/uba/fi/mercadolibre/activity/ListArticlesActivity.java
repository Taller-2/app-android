package ar.uba.fi.mercadolibre.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SearchView;

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
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.list_articles_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listArticlesByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    listAllArticles();
                }
                return true;
            }
        });
        return true;
    }

}
