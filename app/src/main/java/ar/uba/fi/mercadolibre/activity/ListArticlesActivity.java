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
import ar.uba.fi.mercadolibre.model.Article;
import okhttp3.ResponseBody;
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
                if (!response.isSuccessful()) {
                    ResponseBody errorBody = response.errorBody();
                    onFailure(call, new Exception(
                            errorBody == null ? "Error body was null" : errorBody.toString()
                    ));
                    return;
                }
                APIResponse<List<Article>> body = response.body();
                if (body == null) {
                    onFailure(call, new Exception("Response body was null"));
                    return;
                }
                List<Article> articles;
                try {
                    articles = body.getData();
                } catch (InvalidResponseException e) {
                    onFailure(call, e);
                    return;
                }
                ((ListView) findViewById(R.id.articleList)).setAdapter(
                        new ArticleAdapter(ListArticlesActivity.this, articles)
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
