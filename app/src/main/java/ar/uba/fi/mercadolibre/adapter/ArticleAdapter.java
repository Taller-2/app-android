package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private Context context;
    private boolean showDeleteButton;
    public ArticleAdapter(Context context, List<Article> articles, boolean show_delete) {
        super(context, 0, articles);
        this.context = context;
        this.showDeleteButton = show_delete;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        final Article article = getItem(position);
        if (view == null) {
            view = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_article, parent, false);
        }
        ((TextView) view.findViewById(R.id.item_name)).setText(article.getName());
        ((TextView) view.findViewById(R.id.item_description)).setText(article.getDescription());
        ((TextView) view.findViewById(R.id.item_available_units)).setText(Integer.toString(article.getAvailableUnits()));
        ((TextView) view.findViewById(R.id.item_price)).setText(Double.toString(article.getPrice()));

        if (showDeleteButton) {
            addDeleteButton(view, article);
        } else {
            view.findViewById(R.id.delete_article).setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void addDeleteButton(View view, final Article article) {
        view.findViewById(R.id.delete_article).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ControllerFactory.getArticleController().destroy(
                                article.getID()
                        ).enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                                if (!response.isSuccessful()) {
                                    onDeleteFailure();
                                    Log.e("Article delete", response.errorBody().toString());
                                    return;
                                }
                                onDeleteSuccess(article);
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

    private void onDeleteSuccess(Article deletedArticle) {
        remove(deletedArticle);
        notifyDataSetChanged();
        Toast.makeText(
                context,
                R.string.delete_success,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void onDeleteFailure() {
        Toast.makeText(
                context,
                R.string.generic_error,
                Toast.LENGTH_SHORT
        ).show();
    }
}
