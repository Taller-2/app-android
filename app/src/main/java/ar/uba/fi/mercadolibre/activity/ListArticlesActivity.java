package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.ArticleBase;
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

        ControllerFactory.getArticleController().list().enqueue(new Callback<ArticleBase>() {
            @Override
            public void onResponse(@NonNull Call<ArticleBase> call, @NonNull Response<ArticleBase> response) {
                if (!response.isSuccessful()) {
                    toast(R.string.generic_error);
                    Log.e("Articles GET", response.errorBody().toString());
                    return;
                }
                ArticleBase body = response.body();
                ((ListView) findViewById(R.id.articleList)).setAdapter(
                        new ArticleAdapter(ListArticlesActivity.this, body.getData())
                );
            }

            @Override
            public void onFailure(@NonNull Call<ArticleBase> call, @NonNull Throwable t) {
                Log.e("Articles GET", t.getMessage());
                toast(R.string.generic_error);
            }
        });
    }
}
