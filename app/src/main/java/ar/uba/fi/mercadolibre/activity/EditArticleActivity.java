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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditArticleActivity extends BaseActivity {

    private static final int GET_FROM_GALLERY = 3;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    Uri image = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        initData();
    }

    private void initData() {
        Article a = getArticle();
        setDeleteOnClick(findViewById(R.id.edit_article_delete), a);
        setSaveOnClick(findViewById(R.id.edit_article_save));
        setEditImageOnClick(findViewById(R.id.edit_article_image));
        ((EditText)findViewById(R.id.edit_article_name)).setText(a.getName());
        ((EditText)findViewById(R.id.edit_article_description)).setText(a.getDescription());
        ((EditText)findViewById(R.id.edit_article_price)).setText(String.valueOf(a.getPrice()));
        ((EditText)findViewById(R.id.edit_article_units)).setText(String.valueOf(a.getAvailableUnits()));
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
                                try {
                                    String msg = response.errorBody().string();
                                    Log.e("Article delete", msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            if (selectedImage == null) {
                Log.e("Article edit", "Uploaded image from user is null");
                return;
            }
            setImage(selectedImage);
        }
    }

    private void setImage(Uri image) {
        ImageView imageView = findViewById(R.id.edit_article_image);
        Picasso.get().load(image).into(imageView);
        this.image = image;
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
        Log.d("Article edit",
                "Starting image upload");

        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        Article a = getArticle();
        String path =
                user.getUid() + "/" + a.getID() + "/" + image.getLastPathSegment();

        UploadTask task = storageRef.child(path).putFile(image);
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Article edit",
                        "Image upload to firebase failed: " + exception.getMessage());
            }
        }).addOnSuccessListener(
            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Article edit",
                            "Image upload to firebase successful");
                }
            }
        );
    }

    final Article getArticle() {
        Bundle extra = getIntent().getExtras();
        assert extra != null;
        return (Article) extra.get("article");
    }
}
