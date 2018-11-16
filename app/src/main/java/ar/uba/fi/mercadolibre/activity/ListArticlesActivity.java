package ar.uba.fi.mercadolibre.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ArticleAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.dialogs.ArticlesFilterDialog;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.views.TagsSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ListArticlesActivity extends BaseActivity implements ArticlesFilterDialog.Click {
    private FusedLocationProviderClient mFusedLocationClient;

    Dialog dialog;

    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_list_articles);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            listAllArticles();
            return;
        }

        String name = (String) extras.get("article_name");

        if (name == null || name.length() == 0){
            listAllArticles();
            return;
        }
        listArticlesByName(name);
    }

    private void listArticlesByName(String name) {
        listArticles(ControllerFactory.getArticleController().listByName(name));
    }
    private void listAllArticles() {
        listArticles(ControllerFactory.getArticleController().list());
    }
    private void listArticles(Call<APIResponse<List<Article>>> articlesCall) {
        articlesCall.enqueue(new Callback<APIResponse<List<Article>>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<Article>>> call,
                                   @NonNull Response<APIResponse<List<Article>>> response) {
                List<Article> articles = getData(response);
                if (articles == null) return;
                if (articles.isEmpty()) {
                    toast(R.string.no_results);
                    return;
                }
                ((ListView) findViewById(R.id.articleList)).setAdapter(
                        new ArticleAdapter(ListArticlesActivity.this, articles, false)
                );
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<Article>>> call,
                                  @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_articles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.list_article_settings) {
            DialogFragment newFragment = new ArticlesFilterDialog();
            newFragment.show(getSupportFragmentManager(), "filters");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void search() {
        @SuppressLint("MissingPermission") Task<Location> t = mFusedLocationClient.getLastLocation();
        t.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Log.w("Article POST", "Location == null");
                    toast(R.string.location_is_mandatory);
                    return;
                }
                executeSearch(location);

            }
        });
    }

    private boolean hasFineLocationPermissions() {
        return ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (String permission :
                    permissions) {
                if (permission.equals(ACCESS_FINE_LOCATION)) {
                    search();
                    return;
                }
            }
            toast(R.string.location_is_mandatory);
        }
    }

    private String getViewText(int id) {
        return ((EditText) dialog.findViewById(id)).getText().toString();
    }
    private void executeSearch(Location location) {
        String name = getViewText(R.id.filter_search_text);
        if (name.length() == 0) {
            name = null;
        }

        TagsSpinner tags = dialog.findViewById(R.id.filter_tags);
        String category = tags.getSelectedTag();
        Double min_price = parseDoubleOrNull(getViewText(R.id.filter_min_price));
        Double max_price = parseDoubleOrNull(getViewText(R.id.filter_max_price));
        Double max_distance = parseDoubleOrNull(getViewText(R.id.filter_max_distance));

        Double latitude = null;
        Double longitude = null;
        if (max_distance != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        listArticles(ControllerFactory.getArticleController()
                .search(name, latitude, longitude, max_distance, min_price, max_price, category));
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return Double.parseDouble(value);

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        this.dialog = dialog.getDialog();
        if (!hasFineLocationPermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 0);
            return;
        }
        search();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        listAllArticles();
    }
}
