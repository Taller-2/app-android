package ar.uba.fi.mercadolibre.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import ar.uba.fi.mercadolibre.activity.CreateArticleActivity;
import ar.uba.fi.mercadolibre.activity.EditArticleActivity;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private Context context;
    private boolean showEditButton;
    public ArticleAdapter(Context context, List<Article> articles, boolean show_delete) {
        super(context, 0, articles);
        this.context = context;
        this.showEditButton = show_delete;
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

        if (showEditButton) {
            addEditButton(view, article);
        } else {
            view.findViewById(R.id.edit_article).setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private void addEditButton(View view, final Article article) {
        final Activity a = (Activity) this.context;

        view.findViewById(R.id.edit_article).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(a, EditArticleActivity.class);
                    i.putExtra("article", article);
                    a.startActivity(i);
                }
            }
        );
    }

}
