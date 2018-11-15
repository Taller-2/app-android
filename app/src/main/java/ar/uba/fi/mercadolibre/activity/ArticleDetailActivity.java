package ar.uba.fi.mercadolibre.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.NumberFormat;
import java.util.Locale;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.controller.InvalidResponseException;
import ar.uba.fi.mercadolibre.controller.PurchaseBody;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.ShipmentCost;
import ar.uba.fi.mercadolibre.views.ArticleSlider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ArticleDetailActivity extends BaseActivity {
    private Article article = null;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        fetchShipmentCost();
    }

    private void initContent() {
        fillTextView(R.id.detail_article_name, article.getName());
        fillTextView(R.id.detail_article_description, article.getDescription());
        fillTextView(R.id.categories, TextUtils.join(", ", article.getTags()));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String permission : permissions) {
            if (permission.equals(ACCESS_COARSE_LOCATION) || permission.equals(ACCESS_FINE_LOCATION)) {
                fetchShipmentCost();
                return;
            }
        }
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(
                this,
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void fetchShipmentCost() {
        if (!(hasPermission(ACCESS_COARSE_LOCATION) && hasPermission(ACCESS_FINE_LOCATION))) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 0);
            return;
        }
        @SuppressLint("MissingPermission") Task<Location> t = mFusedLocationClient.getLastLocation();
        t.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Log.w("Shipment Cost", "Location == null");
                    return;
                }
                ControllerFactory.getArticleController().shipmentCost(article.getID(), location.getLatitude(), location.getLongitude(), "cash").enqueue(new Callback<APIResponse<ShipmentCost>>() {
                    @Override
                    public void onResponse(Call<APIResponse<ShipmentCost>> call, Response<APIResponse<ShipmentCost>> response) {
                        ShipmentCost shipmentCost;
                        String text;
                        try {
                            shipmentCost = getDataOrThrowException(response);
                        } catch (InvalidResponseException e) {
                            Log.e("shipmentCostError", e.getMessage());
                            toast(R.string.shipment_cost_error);
                            return;
                        }
                        if (shipmentCost.isEnabled()) {
                            text = String.format(Locale.getDefault(), "$%.2f", shipmentCost.getCost());
                        } else {
                            text = getString(R.string.does_not_ship);
                        }
                        fillTextView(R.id.shipment_cost, text);
                    }

                    @Override
                    public void onFailure(Call<APIResponse<ShipmentCost>> call, Throwable t) {
                        Log.w("Shipment Cost", t.getMessage());
                    }
                });
            }
        });
    }
}
