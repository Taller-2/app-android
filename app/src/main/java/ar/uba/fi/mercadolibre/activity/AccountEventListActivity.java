package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.AccountEventsAdapter;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.AccountEvent;

public class AccountEventListActivity extends BaseActivity {
    @Override
    public int identifierForDrawer() {
        return MY_ACCOUNT_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_event_list);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        Account account = (Account) extras.get("account");
        if (account == null) {
            finish();
            return;
        }
        ArrayList<AccountEvent> events = account.getEvents();
        if (events == null || events.size() == 0) {
            toast(R.string.no_account_events);
            finish();
            return;
        }
        ((ListView) findViewById(R.id.events)).setAdapter(
                new AccountEventsAdapter(
                        AccountEventListActivity.this,
                        account.getEvents()
                )
        );
    }
}
