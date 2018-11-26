package ar.uba.fi.mercadolibre.adapter;

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

import java.util.ArrayList;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.activity.ChatActivity;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.Purchase;

public class PurchasesAdapter extends ArrayAdapter<Purchase> {

    public PurchasesAdapter(@NonNull Context context, List<Purchase> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Purchase purchase = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_purchase, parent, false);
        }
        Article article = purchase.getArticle();
        ((TextView) convertView.findViewById(R.id.articleName)).setText(article.getName());
        loadImage((ImageView) convertView.findViewById(R.id.purchasedItemImage), article);

        final Context context = getContext();
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("chat_room", purchase.getID());
                context.startActivity(i);
            }
        });
        return convertView;
    }

    private void loadImage(final ImageView imageView, Article article) {
        ArrayList<String> pictures = article.getPictureURLs();
        if (pictures.isEmpty()) {
            return;
        }

        final String path = pictures.get(0);
        Picasso.get().load(path).into(imageView);
    }

}
