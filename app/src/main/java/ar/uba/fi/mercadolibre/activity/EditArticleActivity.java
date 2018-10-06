package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditArticleActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        Article a = (Article) getIntent().getExtras().get("article");
        setDeleteOnClick(findViewById(R.id.edit_article_delete), a);
        ((EditText)findViewById(R.id.edit_article_name)).setText(a.getName());
        ((EditText)findViewById(R.id.edit_article_description)).setText(a.getDescription());
        ((EditText)findViewById(R.id.edit_article_price)).setText(String.valueOf(a.getPrice()));
        ((EditText)findViewById(R.id.edit_article_units)).setText(String.valueOf(a.getAvailableUnits()));
    }

    private void setDeleteOnClick(View delete, final Article article) {
        delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ControllerFactory.getArticleController().destroy(
                                article.getID()
                        ).enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                                if (!response.isSuccessful()) {
                                    onDeleteFailure();
                                    try {
                                        String msg = response.errorBody().string();
                                        Log.e("Article delete", msg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                                onDeleteSuccess();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                                onDeleteFailure();
                                Log.e("Article delete", t.getMessage());

                            }
                        });

                    }
                }
        );
    }
    private void onDeleteSuccess() {
        Toast.makeText(
                this,
                R.string.delete_success,
                Toast.LENGTH_SHORT
        ).show();
        startActivity(new Intent(this, UserArticlesActivity.class));
    }

    private void onDeleteFailure() {
        Toast.makeText(
                this,
                R.string.generic_error,
                Toast.LENGTH_SHORT
        ).show();
    }
}
