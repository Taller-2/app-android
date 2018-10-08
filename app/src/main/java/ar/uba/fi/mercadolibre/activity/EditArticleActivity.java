package ar.uba.fi.mercadolibre.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class EditArticleActivity extends BaseActivity {

    private static final int GET_FROM_GALLERY = 3;
    Uri newImageUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        initData();
    }

    private void fillEditText(int edit_text_id, String text) {
        ((EditText) findViewById(edit_text_id)).setText(text);
    }

    private void initData() {
        final Article article = getArticle();
        setDeleteOnClick(findViewById(R.id.edit_article_delete), article);
        setSaveOnClick(findViewById(R.id.edit_article_save));
        setEditImageOnClick(findViewById(R.id.edit_article_image));
        fillEditText(R.id.edit_article_name, article.getName());
        fillEditText(R.id.edit_article_description, article.getDescription());
        fillEditText(R.id.edit_article_price, String.valueOf(article.getPrice()));
        fillEditText(R.id.edit_article_units, String.valueOf(article.getAvailableUnits()));

        if (!article.getPictureURLs().isEmpty()) {
            final String path = article.getPictureURLs().get(0);
            ImageView view = findViewById(R.id.edit_article_image);
            new FirebaseImageManager().loadImageInto(path, view);
        }
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
        startActivity(new Intent(this, UserArticlesActivity.class));
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
                startActivity(new Intent(getApplicationContext(), UserArticlesActivity.class));
            }
        });
    }

    private void save() {
        Article article = getArticle();

        article.setName(getViewText(R.id.edit_article_name));
        article.setDescription(getViewText(R.id.edit_article_description));
        article.setAvailableUnits(Integer.parseInt(getViewText(R.id.edit_article_units)));
        article.setPrice(Double.parseDouble(getViewText(R.id.edit_article_price)));

        uploadImageToFirebase(article);
        ControllerFactory.getArticleController().update(article).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                Log.d("Article edit", "Successfully updated article");
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
        article.getPictureURLs().add(path);
    }

    final Article getArticle() {
        Bundle extra = getIntent().getExtras();
        assert extra != null;
        return (Article) extra.get("article");
    }

    private String getViewText(int view_id) {
        return ((EditText) findViewById(view_id)).getText().toString();
    }
}
