package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.filter.InputFilterMinMax;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateArticleActivity extends BaseActivity {
    int[] textFieldIDs = {
            R.id.name,
            R.id.description,
            R.id.available_units,
            R.id.price
    };
    int submitButtonID = R.id.publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);
        InputFilter[] filter = new InputFilter[]{
                new InputFilterMinMax(1, Integer.MAX_VALUE)
        };
        ((EditText) findViewById(R.id.available_units)).setFilters(filter);
        ((EditText) findViewById(R.id.price)).setFilters(filter);
        for (int id : textFieldIDs) {
            ((EditText) findViewById(id)).addTextChangedListener(watcher);
        }
        findViewById(submitButtonID).setEnabled(false);
    }

    public void createArticle(View view) {
        ControllerFactory.getArticleController().create(
                new Article(
                        getViewText(R.id.name),
                        getViewText(R.id.description),
                        Integer.parseInt(getViewText(R.id.available_units)),
                        Integer.parseInt(getViewText(R.id.price))
                )
        ).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                toast(response.isSuccessful() ?
                        R.string.publish_article_success :
                        R.string.publish_article_error);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                toast(R.string.publish_article_error);
                finish();
            }
        });
    }

    private String getViewText(int viewID) {
        return ((EditText) findViewById(viewID)).getText().toString();
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean shouldEnable = true;
            for (int id : textFieldIDs) {
                if (((EditText) findViewById(id)).getText().toString().length() == 0) {
                    shouldEnable = false;
                    break;
                }
            }
            findViewById(submitButtonID).setEnabled(shouldEnable);
        }
    };
}
