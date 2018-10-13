package ar.uba.fi.mercadolibre.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.utils.FirebaseImageManager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class EditArticleActivity extends BaseActivity {
    private FusedLocationProviderClient mFusedLocationClient;

    private static final int GET_FROM_GALLERY = 3;
    private static final int REQUEST_COARSE_LOCATION = 1;
    Uri newImageUri = null;

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        initData();
        for (int id : textFieldIDs) {
            ((EditText) findViewById(id)).addTextChangedListener(watcher);
        }
        findViewById(submitButtonID).setEnabled(false);

    }

    private void fillEditText(int edit_text_id, String text) {
        ((EditText) findViewById(edit_text_id)).setText(text);
    }

    private void initData() {
        final Article article = getArticle();
        setSaveOnClick(findViewById(R.id.edit_article_save));
        setEditImageOnClick(findViewById(R.id.edit_article_image));
        if (article == null) {
            findViewById(R.id.edit_article_delete).setVisibility(View.INVISIBLE);
            return;
        }

        setDeleteOnClick(findViewById(R.id.edit_article_delete), article);
        init_text_views(article);

        if (!article.getPictureURLs().isEmpty()) {
            final String path = article.getPictureURLs().get(0);
            ImageView view = findViewById(R.id.edit_article_image);
            new FirebaseImageManager().loadImageInto(path, view);
        }
    }

    private void init_text_views(Article article) {
        fillEditText(R.id.edit_article_name, article.getName());
        fillEditText(R.id.edit_article_description, article.getDescription());
        fillEditText(R.id.edit_article_price, String.valueOf(article.getPrice()));
        fillEditText(R.id.edit_article_units, String.valueOf(article.getAvailableUnits()));
    }

    private void setEditImageOnClick(View view) {
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                                GET_FROM_GALLERY);

                    }
                }
        );
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


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            if (selectedImage == null) {
                Log.e("Article edit", "Uploaded image from user is null");
                return;
            }
            setImage(selectedImage);
        }
    }

    private void setImage(Uri imageUri) {
        ImageView imageView = findViewById(R.id.edit_article_image);
        Picasso.get().load(imageUri)
                .resize(imageView.getWidth(), imageView.getHeight())
                .into(imageView);
        this.newImageUri = imageUri;
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
        final Article article = getArticle();
        if (article == null) {
            createArticle();
            return;
        }
        article.setName(getViewText(R.id.edit_article_name));
        article.setDescription(getViewText(R.id.edit_article_description));
        article.setAvailableUnits(Integer.parseInt(getViewText(R.id.edit_article_units)));
        article.setPrice(Double.parseDouble(getViewText(R.id.edit_article_price)));

        ControllerFactory.getArticleController().update(article).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                Log.d("Article edit", "Successfully updated article");
                uploadImageToFirebase(article);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                Log.d("Article edit", "Update article error", t);
            }
        });
    }

    private void uploadImageToFirebase(Article article) {
        if (newImageUri == null) {
            return;
        }
        Log.d("Article edit", "Starting image upload");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        String path =
                user.getUid() + "/" + article.getID() + "/" + newImageUri.getLastPathSegment();

        new FirebaseImageManager().upload(newImageUri, path);
        article.addPicture(path);
    }

    final Article getArticle() {
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            return null;
        }
        return (Article) extra.get("article");
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

        Article article = new Article(
                getViewText(R.id.edit_article_name),
                getViewText(R.id.edit_article_description),
                Integer.parseInt(getViewText(R.id.edit_article_units)),
                Integer.parseInt(getViewText(R.id.edit_article_price)),
                lat,
                lon
        );

        uploadImageToFirebase(article);
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
