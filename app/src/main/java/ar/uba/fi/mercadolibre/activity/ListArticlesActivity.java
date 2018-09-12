package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListArticlesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_articles);

        ControllerFactory.getArticleController().list().enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(@NonNull Call<List<Article>> call, @NonNull Response<List<Article>> response) {
                if (!response.isSuccessful()) {
                    toast(R.string.generic_error);
                    return;
                }
                List<Article> body = response.body();
                if (body == null) body = new ArrayList<>();
                ((ListView) findViewById(R.id.articleList)).setAdapter(
                        new ArticleAdapter(ListArticlesActivity.this, body)
                );
            }

            @Override
            public void onFailure(@NonNull Call<List<Article>> call, @NonNull Throwable t) {
                toast(R.string.generic_error);
            }
        });
    }
}
