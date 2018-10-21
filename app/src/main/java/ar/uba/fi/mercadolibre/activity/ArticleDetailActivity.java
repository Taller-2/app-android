package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.utils.ArticleImageSliderManager;
import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

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

        Slider s = findViewById(R.id.detail_article_slider);

        ArticleImageSliderManager manager = new ArticleImageSliderManager(s, article);

        int corner = getResources().getDimensionPixelSize(R.dimen.slider_image_corner);
        manager.init(this, corner);
    }

}