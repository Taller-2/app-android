package ar.uba.fi.mercadolibre.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.filter.InputFilterMinMax;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class CreateArticleActivity extends BaseActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    boolean creating = false;

    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    int[] textFieldIDs = {
            R.id.name,
            R.id.description,
            R.id.available_units,
            R.id.price
    };
    int submitButtonID = R.id.publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions[0].equals(ACCESS_COARSE_LOCATION) && creating) {
            // Estabamos creando un article, volvemos a la lógica de creación
            setupArticleData();
        }
    }

    public void createArticle(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            creating = true;
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 0);
        } else {
            setupArticleData();
        }

    }

    private void setupArticleData() {
        // Obtengo location (lat + lon) para los datos geográficos del article
        Task<Location> t;
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;  // Sólo llamar a esta función si hay permisos!
        }
        t = mFusedLocationClient.getLastLocation();
        t.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    postArticle(location);
                } else {
                    Log.w("Article POST", "Location == null");
                    toast(R.string.publish_article_error);
                    finish();
                }
            }
        });
    }

    private void postArticle(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        ControllerFactory.getArticleController().create(
                new Article(
                        getViewText(R.id.name),
                        getViewText(R.id.description),
                        Integer.parseInt(getViewText(R.id.available_units)),
                        Integer.parseInt(getViewText(R.id.price)),
                        lat,
                        lon
                )
        ).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                if (response.isSuccessful()) {
                    toast(R.string.publish_article_success);
                } else {
                    toast(R.string.publish_article_error);
                    Log.e("Article POST", response.toString());
                }
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                Log.e("Article POST", t.toString());
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
