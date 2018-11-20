package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ar.uba.fi.mercadolibre.R;

public class MainMenuActivity extends BaseActivity {
    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_app_menu, menu);
        menu.findItem(R.id.action_search).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(getApplicationContext(), ListArticlesActivity.class);
                i.putExtra("show_filters", Boolean.valueOf(true));
                startActivity(i);
                return true;
            }
        });
        return true;
    }

    public void createArticle(View view) {
        startActivity(new Intent(getApplicationContext(), EditArticleActivity.class));
    }

    public void listArticles(View view) {
        Intent i = new Intent(getApplicationContext(), ListArticlesActivity.class);
        i.putExtra("show_filters", Boolean.valueOf(true));
        startActivity(i);
    }

    public void openActivityMap(View view) {
        startActivity(new Intent(getApplicationContext(), MapActivity.class));
    }

    public void scanQR(View view) {
        startQrScan();
    }
}
