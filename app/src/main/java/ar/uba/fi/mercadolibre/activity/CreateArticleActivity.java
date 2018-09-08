package ar.uba.fi.mercadolibre.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ar.uba.fi.mercadolibre.R;
import filter.InputFilterMinMax;

public class CreateArticleActivity extends AppCompatActivity {
    int[] textFieldIDs = {
            R.id.name,
            R.id.description,
            R.id.available_units,
            R.id.price
    };
    int submitButtonID = R.id.publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);
        InputFilter[] filter = new InputFilter[]{
                new InputFilterMinMax(1, Integer.MAX_VALUE)
        };
        ((EditText) findViewById(R.id.available_units)).setFilters(filter);
        ((EditText) findViewById(R.id.price)).setFilters(filter);
        for (int id : textFieldIDs) {
            ((EditText) findViewById(id)).addTextChangedListener(watcher);
        }
        findViewById(submitButtonID).setEnabled(false);
    }

    public void createArticle(View view) {
        // TODO: actual submission goes here, toast text should depend on response
        Toast.makeText(
                getApplicationContext(),
                getString(R.string.publish_article_success),
                Toast.LENGTH_SHORT
        ).show();
        finish();
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean shouldEnable = true;
            for (int id : textFieldIDs) {
                if (((EditText) findViewById(id)).getText().toString().length() == 0) {
                    shouldEnable = false;
                    break;
                }
            }
            findViewById(submitButtonID).setEnabled(shouldEnable);
        }
    };
}
