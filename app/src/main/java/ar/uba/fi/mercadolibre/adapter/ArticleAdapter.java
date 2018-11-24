package ar.uba.fi.mercadolibre.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.activity.ArticleDetailActivity;
import ar.uba.fi.mercadolibre.activity.BaseActivity;
import ar.uba.fi.mercadolibre.activity.EditArticleActivity;
import ar.uba.fi.mercadolibre.activity.UserArticlesActivity;
import ar.uba.fi.mercadolibre.model.Article;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private Context context;
    private boolean userIsOwner;

    public ArticleAdapter(Context context, List<Article> articles, boolean userIsOwner) {
        super(context, 0, articles);
        this.context = context;
        this.userIsOwner = userIsOwner;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_article, parent, false);
        }
        final Article article = getItem(position);
        if (article == null) return view;

        ((TextView) view.findViewById(R.id.item_name)).setText(article.getName());
        ((TextView) view.findViewById(R.id.item_available_units)).setText(
                NumberFormat.getIntegerInstance().format(article.getAvailableUnits()) + " " + getContext().getString(R.string.available_units_tag)
        );
        ((TextView) view.findViewById(R.id.item_price)).setText(NumberFormat.getCurrencyInstance().format(article.getPrice()));

        if (!article.getPictureURLs().isEmpty()) {
            ImageView image = view.findViewById(R.id.list_article_image);
            loadImage(image, article);
        }

        if (userIsOwner) {
            addDetailClickEvent(view, article, EditArticleActivity.class);
        } else {
            addDetailClickEvent(view, article, ArticleDetailActivity.class);
        }
        return view;
    }

    private void addDetailClickEvent(View view, final Article article, final Class<? extends Activity> activity) {
        final Activity a = (Activity) this.context;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(a, activity);
                i.putExtra("article", article);
                a.startActivity(i);

            }
        });
    }

    private void loadImage(final ImageView imageView, Article a) {
        final String path = a.getPictureURLs().get(0);
        Picasso.get().load(path).into(imageView);
    }
}
