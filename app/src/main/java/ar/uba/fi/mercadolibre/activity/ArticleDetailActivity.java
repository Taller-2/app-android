package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.text.NumberFormat;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.controller.PurchaseBody;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.views.ArticleSlider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailActivity extends BaseActivity {

    private Article article = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras == null) {
            return;
        }
        article = (Article) extras.get("article");
        if (article == null) {
            return;
        }

        initContent();
    }

    private void initContent() {
        fillTextView(R.id.detail_article_name, article.getName());
        fillTextView(R.id.detail_article_description, article.getDescription());

        String units = String.valueOf(article.getAvailableUnits());
        fillTextView(R.id.detail_article_available_nits, units);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        fillTextView(R.id.detail_article_price, format.format(article.getPrice()));

        ArticleSlider s = findViewById(R.id.detail_article_slider);
        int corner = getResources().getDimensionPixelSize(R.dimen.slider_image_corner);
        s.init(article, corner);

        findViewById(R.id.buy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });
    }

    private void buy() {
        String articleId = article.getID();
        int units = 1;

        ControllerFactory.getPurchaseController()
                .purchaseArticle(new PurchaseBody(articleId, units))
                .enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    // Move to a payment method screen here
                    toast(R.string.ok);
                    finish();
                    return;
                }
                Log.e("Purchase POST", response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Purchase POST", t.getMessage());
            }
        });
    }

}