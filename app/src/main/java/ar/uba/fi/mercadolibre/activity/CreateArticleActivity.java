package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.filter.InputFilterMinMax;
import ar.uba.fi.mercadolibre.server_api.Article;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateArticleActivity extends AppCompatActivity {
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://taller2-app-server.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        HashMap<String, Object> body = new HashMap<>();
        body.put("name", ((EditText) findViewById(R.id.name)).getText().toString());
        body.put("description", ((EditText) findViewById(R.id.description)).getText().toString());
        body.put("available_units", Integer.parseInt(((EditText) findViewById(R.id.available_units)).getText().toString()));
        body.put("price", Integer.parseInt(((EditText) findViewById(R.id.price)).getText().toString()));
        Single<Object> response = retrofit
                .create(Article.class)
                .post("application/json", body);

        response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(Object bject) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.publish_article_success),
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.publish_article_error),
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }
                });
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
