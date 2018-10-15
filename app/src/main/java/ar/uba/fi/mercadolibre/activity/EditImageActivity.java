package ar.uba.fi.mercadolibre.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.ImageAdapter;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.utils.FirebaseImageManager;

public class EditImageActivity extends BaseActivity {
    private static final int GET_FROM_GALLERY = 3;
    private Article article = null;
    FirebaseImageManager mFirebaseImageManager =
            new FirebaseImageManager();

    @Override
    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_edit_image);
        article = getArticleFromIntent();
        if (article == null) {
            return;
        }
        initAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_gallery) {
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                    GET_FROM_GALLERY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            final Uri selectedImage = data.getData();
            if (selectedImage == null) {
                Log.e("Article edit", "Uploaded image from user is null");
                return;
            }
            findViewById(R.id.loading_spinner_layout).setVisibility(View.VISIBLE);
            mFirebaseImageManager.upload(selectedImage, selectedImage.toString(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    refreshImages(selectedImage);
                }
            });

        }
    }

    private void refreshImages(Uri selectedImage) {
        final GridView gridview = findViewById(R.id.edit_image_gridview);
        gridview.setVisibility(View.VISIBLE);
        findViewById(R.id.no_images_layout).setVisibility(View.GONE);

        mFirebaseImageManager.getDownloadUrl(selectedImage.toString(), new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                article.addPicture(uri.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageAdapter adapter = (ImageAdapter) gridview.getAdapter();
                        if (adapter == null) {
                            initAdapter();
                        }
                        adapter = (ImageAdapter) gridview.getAdapter();
                        adapter.notifyDataSetChanged();
                        findViewById(R.id.loading_spinner_layout).setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void initAdapter() {
        final GridView gridview = findViewById(R.id.edit_image_gridview);
        ArrayList<String> pictures = article.getPictureURLs();
        if (pictures == null || pictures.size() == 0) {
            findViewById(R.id.no_images_layout).setVisibility(View.VISIBLE);
            gridview.setVisibility(View.INVISIBLE);
            return;
        }
        findViewById(R.id.no_images_layout).setVisibility(View.GONE);
        gridview.setVisibility(View.VISIBLE);
        gridview.setAdapter(new ImageAdapter(this, pictures, new Callback() {
            @Override
            public void onSuccess() {
                refreshLayout();
            }

            @Override
            public void onError(Exception e) {

            }
        }));
    }

    public void refreshLayout() {
        ArrayList<String> pictures = article.getPictureURLs();
        final GridView gridview = findViewById(R.id.edit_image_gridview);
        if (pictures != null && pictures.size() == 0) {
            findViewById(R.id.no_images_layout).setVisibility(View.VISIBLE);
            gridview.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        Article oldArticle = getArticleFromIntent();
        if (oldArticle == null) {
            super.onBackPressed();
            return;
        }
        ArrayList<String> oldPictures = oldArticle.getPictureURLs();
        if (oldPictures != null && oldPictures.size() == article.getPictureURLs().size()) {
            super.onBackPressed();
            return;
        }
        Intent i = getIntent().putExtra("article", article);
        setResult(RESULT_OK, i);
        super.onBackPressed();
    }

    private Article getArticleFromIntent() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras == null) {
            return null;
        }
        return (Article) i.getExtras().get("article");
    }
}
