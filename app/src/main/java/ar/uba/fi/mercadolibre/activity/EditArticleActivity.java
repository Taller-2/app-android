package ar.uba.fi.mercadolibre.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class EditArticleActivity extends BaseActivity {
    private FusedLocationProviderClient mFusedLocationClient;
    private Article article = null;

    private static final int REQUEST_COARSE_LOCATION = 1;
    private static final int EDIT_IMAGES = 2;

    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    int[] textFieldIDs = {
            R.id.edit_article_name,
            R.id.edit_article_description,
            R.id.edit_article_units,
            R.id.edit_article_price
    };
    int submitButtonID = R.id.edit_article_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_edit_article);
        initData();
        setUpTextWatcher();
        setUpImages();
        setSaveOnClick(findViewById(R.id.edit_article_save));
        setDeleteOnClick(findViewById(R.id.edit_article_delete), article);

    }

    private void setUpTextWatcher() {
        for (int id : textFieldIDs) {
            ((EditText) findViewById(id)).addTextChangedListener(watcher);
        }
        if (article.getID() == null) {
            findViewById(submitButtonID).setEnabled(false);
        }
    }

    private void setUpImages() {
        initCarousel();
        findViewById(R.id.edit_article_edit_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditArticleImagesActivity.class);
                i.putExtra("article", article);
                startActivityForResult(i, EDIT_IMAGES);
            }
        });
    }

    private void fillEditText(int edit_text_id, String text) {
        ((EditText) findViewById(edit_text_id)).setText(text);
    }

    private void initData() {
        initArticle();
        if (article == null) {
            findViewById(R.id.edit_article_delete).setVisibility(View.INVISIBLE);
            return;
        }
        init_text_views(article);
    }

    private void initCarousel() {
        Slider slider = findViewById(R.id.slider);
        slider.removeAllViews();
        ArrayList<String> pictures = article.getPictureURLs();
        if (pictures == null || pictures.size() == 0) {
            ImageView blank = new ImageView(this);
            blank.setImageResource(R.drawable.ic_menu_camera);
            slider.addView(blank);
            return;
        }

        List<Slide> slideList = new ArrayList<>();
        int corner = getResources().getDimensionPixelSize(R.dimen.slider_image_corner);
        for (int i = 0; i < article.getPictureURLs().size(); i++) {
            String pictureURL = article.getPictureURLs().get(i);
            slideList.add(new Slide(i, pictureURL, corner));
        }
        slider.addSlides(slideList);

    }
    private void init_text_views(Article article) {
        fillEditText(R.id.edit_article_name, article.getName());
        fillEditText(R.id.edit_article_description, article.getDescription());
        String price = null;
        String units = null;
        if (article.getID() != null) {
            price = String.valueOf(article.getPrice());
            units = String.valueOf(article.getAvailableUnits());
        }
        fillEditText(R.id.edit_article_price, price);
        fillEditText(R.id.edit_article_units, units);
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
                                    String msg;
                                    try {
                                        ResponseBody body = response.errorBody();
                                        msg = body == null ? "Error body was null" : body.string();
                                    } catch (IOException e) {
                                        msg = "IOException while reading the response: " + e.getMessage();
                                    }
                                    Log.e("Article delete", msg);
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
        finish();
    }

    private void onDeleteFailure() {
        Toast.makeText(
                this,
                R.string.generic_error,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_IMAGES && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            article = (Article) extras.get("article");
            if (article == null) {
                return;
            }
            initCarousel();
        }

    }

    private void setSaveOnClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        article.setName(getViewText(R.id.edit_article_name));
        article.setDescription(getViewText(R.id.edit_article_description));
        article.setAvailableUnits(Integer.parseInt(getViewText(R.id.edit_article_units)));
        article.setPrice(Double.parseDouble(getViewText(R.id.edit_article_price)));

        if (article.getID() == null) {
            createArticle();
            return;
        }

        ControllerFactory.getArticleController().update(article).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                Log.d("Article edit", "Successfully updated article");
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                Log.d("Article edit", "Update article error", t);
            }
        });
    }

    private void initArticle() {
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            article = new Article();
            return;
        }
        article = (Article) extra.get("article");
        if (article == null) {
            article = new Article();
        }
    }

    private String getViewText(int view_id) {
        return ((EditText) findViewById(view_id)).getText().toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_COARSE_LOCATION) {
            for (String permission :
                    permissions) {
                if (permission.equals(ACCESS_COARSE_LOCATION)) {
                    // Back to the create article logic
                    setupArticleData();
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void setupArticleData() {
        // Precondition: Location permission already granted
        Task<Location> t = mFusedLocationClient.getLastLocation();
        t.addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Log.w("Article POST", "Location == null");
                    toast(R.string.location_is_mandatory);
                    finish();
                    return;
                }

                postArticle(location);
            }
        });
    }
    private void postArticle(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        article.setLatLon(lat, lon);
        article.post(new Callback<Article>() {
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
                onGetDataFailure(t);
            }
        });
    }
    public void createArticle() {
        if (!hasCoarseLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        setupArticleData();
    }

    private boolean hasCoarseLocationPermission() {
        return ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
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
