package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.activity.ChatActivity;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.Purchase;

public class PurchasesAdapter extends ArrayAdapter<Purchase> {
    private static HashMap<Purchase.Status, Integer> statusStrings;

    static {
        statusStrings = new HashMap<>();
        statusStrings.put(Purchase.Status.PENDING, R.string.pending);
        statusStrings.put(Purchase.Status.DONE, R.string.done);
        statusStrings.put(Purchase.Status.CANCELLED, R.string.cancelled);
        statusStrings.put(Purchase.Status.DOES_NOT_EXIST, R.string.not_found);
    }

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
        Resources resources = convertView.getResources();
        ((TextView) convertView.findViewById(R.id.payment_status)).setText(
                String.format(
                        resources.getString(R.string.payment_status),
                        resources.getString(statusStrings.get(purchase.getPaymentStatus()))
                )
        );
        TextView shipmentStatus = convertView.findViewById(R.id.shipment_status);
        shipmentStatus.setText(
                String.format(
                        resources.getString(R.string.shipment_status),
                        resources.getString(statusStrings.get(purchase.getShipmentStatus()))
                )
        );
        if (purchase.getShipmentStatus() == Purchase.Status.DOES_NOT_EXIST) {
            shipmentStatus.setVisibility(View.GONE);
        }
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
