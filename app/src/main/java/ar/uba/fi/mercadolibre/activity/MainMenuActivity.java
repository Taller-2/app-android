package ar.uba.fi.mercadolibre.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

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
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(getApplicationContext(), ListArticlesActivity.class);
                i.putExtra("article_name", query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public void createArticle(View view) {
        startActivity(new Intent(getApplicationContext(), EditArticleActivity.class));
    }

    public void listArticles(View view) {
        startActivity(new Intent(getApplicationContext(), ListArticlesActivity.class));
    }

    public void openActivityMap(View view) {
        startActivity(new Intent(getApplicationContext(), MapActivity.class));
    }

    public void openChat(View view) {
        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
    }
}
