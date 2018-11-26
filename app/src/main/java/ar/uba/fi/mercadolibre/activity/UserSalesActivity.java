package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.adapter.PurchasesAdapter;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Purchase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSalesActivity extends BaseActivity {
    @Override
    public int identifierForDrawer() {
        return MY_SALES_IDENTIFIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_purchases);
        ((TextView) findViewById(R.id.noPurchases)).setText(R.string.no_sales);
        init();
    }

    private void init() {
        ControllerFactory.getPurchaseController().getUserSales().enqueue(new Callback<APIResponse<List<Purchase>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Purchase>>> call, Response<APIResponse<List<Purchase>>> response) {
                List<Purchase> data = getData(response);
                if (data == null) {
                    Log.e("Sales GET", "data was null");
                    return;
                }
                if (data.size() == 0) {
                    showEmptyMessage();
                    return;
                }
                ((ListView) findViewById(R.id.userPurchases)).setAdapter(
                        new PurchasesAdapter(UserSalesActivity.this, data)
                );
            }

            @Override
            public void onFailure(Call<APIResponse<List<Purchase>>> call, Throwable t) {
                Log.e("Sales GET", t.toString());
            }
        });
    }

    private void showEmptyMessage() {
        TextView message = findViewById(R.id.noPurchases);
        message.setVisibility(View.VISIBLE);
        findViewById(R.id.userPurchases).setVisibility(View.GONE);
    }
}
