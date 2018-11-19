package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ar.uba.fi.mercadolibre.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> imageURLs;
    private Callback onRefresh;

    public ImageAdapter(Context c, ArrayList<String> imageURLs, Callback onRefresh) {
        mContext = c;
        this.imageURLs = imageURLs;
        this.onRefresh = onRefresh;
    }

    public int getCount() {
        return imageURLs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.article_image, parent, false);
        }

        final ImageView imageView = convertView.findViewById(R.id.image_adapter_image);
        Picasso p = Picasso.get();
        p.setLoggingEnabled(true);
        p.load(imageURLs.get(position)).into(imageView);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeImage(v, position);
                return true;
            }
        });
        return imageView;
    }

    private void removeImage(View v, int imagePosition) {
        imageURLs.remove(imagePosition);
        notifyDataSetChanged();
        onRefresh.onSuccess();
    }
}
