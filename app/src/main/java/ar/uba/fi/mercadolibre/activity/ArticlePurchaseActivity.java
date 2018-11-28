package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.controller.PurchaseBody;
import ar.uba.fi.mercadolibre.model.Article;
import ar.uba.fi.mercadolibre.model.ShipmentCost;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlePurchaseActivity extends BaseActivity {
    private AppCompatSpinner paymentMethod;
    private AppCompatSpinner availableUnits;
    private EditText address;
    private CheckBox shipmentEnabled;
    private Button submit;

    private Article article;
    private ShipmentCost cost;
    private ShipmentCost.PaymentMethod method = ShipmentCost.PaymentMethod.CASH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_purchase);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        article = (Article) extras.get("article");
        cost = (ShipmentCost) extras.get("shipmentCost");
        if (article == null || cost == null) finish();
        init();
    }

    private void init() {
        fillTextView(R.id.description, article.getName());
        initPaymentMethod();
        initAvailableUnits();
        initAddress();
        initShipmentCheckbox();
        initSubmitButton();

        refresh();
    }

    private void initPaymentMethod() {
        paymentMethod = findViewById(R.id.payment_method);
        paymentMethod.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(
                        getString(R.string.cash),
                        getString(R.string.credit_card),
                        getString(R.string.debit_card)
                )
        ));
        paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        method = ShipmentCost.PaymentMethod.CASH;
                        break;
                    case 1:
                        method = ShipmentCost.PaymentMethod.CREDIT;
                        break;
                    default:
                        method = ShipmentCost.PaymentMethod.DEBIT;
                }
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void initAvailableUnits() {
        availableUnits = findViewById(R.id.available_units);
        ArrayList<Integer> options = new ArrayList<>();
        for (int i = 1; i <= article.getAvailableUnits(); i++) {
            options.add(i);
        }
        availableUnits.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                options
        ));
        availableUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                refresh();
            }
        });
    }

    private void initAddress() {
        address = findViewById(R.id.address);
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refresh();
            }
        });
    }

    private void initShipmentCheckbox() {
        shipmentEnabled = findViewById(R.id.shipment_enabled);
        shipmentEnabled.setEnabled(cost.isEnabled(method));
        shipmentEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refresh();
            }
        });
    }

    private void initSubmitButton() {
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        ControllerFactory
                .getPurchaseController()
                .purchaseArticle(new PurchaseBody(
                        article.getID(),
                        units(),
                        totalPrice(),
                        method,
                        address.getText().toString()
                ))
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                        if (response.isSuccessful()) {
                            toast(R.string.ok);
                            startActivity(new Intent(getApplicationContext(), UserPurchasesActivity.class));
                            finish();
                            return;
                        }
                        ResponseBody body = response.errorBody();
                        Log.e("Purchase POST", body == null ? "error body was null" : body.toString());
                        toast(R.string.generic_error);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                        Log.e("Purchase POST", t.getMessage());
                        toast(R.string.generic_error);
                    }
                });
    }

    private void refresh() {
        fillTextView(
                R.id.article_price,
                Integer.toString(units())
                        + " x "
                        + NumberFormat.getCurrencyInstance().format(article.getPrice())
        );
        fillTextView(
                R.id.shipment_cost,
                shipmentIsEnabled()
                        ? NumberFormat.getCurrencyInstance().format(shipmentPrice())
                        : "-"
        );
        fillTextView(
                R.id.total_amount,
                NumberFormat.getCurrencyInstance().format(totalPrice())
        );
        toggleAddressEnabled();
        toggleSubmitEnabled();
    }

    private void toggleAddressEnabled() {
        address.setEnabled(shipmentIsEnabled());
    }

    private void toggleSubmitEnabled() {
        submit.setEnabled(!shipmentIsEnabled() || addressIsNotBlank());
    }

    private boolean shipmentIsEnabled() {
        return cost.isEnabled(method) && shipmentEnabled.isChecked();
    }

    private boolean addressIsNotBlank() {
        return !address.getText().toString().equals("");
    }

    private int units() {
        return (Integer) availableUnits.getSelectedItem();
    }

    private double shipmentPrice() {
        return cost.getCost(method);
    }

    private double totalPrice() {
        double shipment = shipmentIsEnabled() ? shipmentPrice() : 0;
        return shipment + units() * article.getPrice();
    }
}
