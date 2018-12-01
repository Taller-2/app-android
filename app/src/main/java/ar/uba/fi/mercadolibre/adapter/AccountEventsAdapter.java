package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.AccountEvent;

public class AccountEventsAdapter extends ArrayAdapter<AccountEvent> {
    public AccountEventsAdapter(Context context, ArrayList<AccountEvent> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_account_event, parent, false);
        }
        final AccountEvent event = getItem(position);
        if (event == null) return view;

        try {
            ((TextView) view.findViewById(R.id.timestamp)).setText(
                    SimpleDateFormat
                            .getDateTimeInstance()
                            .format(event.getTimestamp())
            );
        } catch (ParseException e) {
            Log.e("AccountEvent timestamp", e.getMessage());
        }

        Resources resources = view.getResources();

        int eventName;
        switch (event.getType()) {
            case SALE:
                eventName = R.string.sale;
                break;
            case PURCHASE:
                eventName = R.string.purchase;
                break;
            default:
                eventName = R.string.publication;
        }

        ((TextView) view.findViewById(R.id.event)).setText(
                String.format(
                        resources.getString(R.string.account_event_detail),
                        resources.getString(eventName),
                        event.getArticleName()
                )
        );

        ((TextView) view.findViewById(R.id.score_increment)).setText(
                String.format(
                        resources.getString(R.string.score_increment),
                        Integer.toString(event.getScoreIncrement())
                )
        );

        return view;
    }
}
