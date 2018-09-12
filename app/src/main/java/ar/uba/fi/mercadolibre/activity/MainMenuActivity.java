package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ar.uba.fi.mercadolibre.R;

public class MainMenuActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void createArticle(View view) {
        startActivity(new Intent(getApplicationContext(), CreateArticleActivity.class));
    }

    public void listArticles(View view) {
        startActivity(new Intent(getApplicationContext(), ListArticlesActivity.class));
    }
}
