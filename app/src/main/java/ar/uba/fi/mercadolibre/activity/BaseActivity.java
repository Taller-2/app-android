package ar.uba.fi.mercadolibre.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    protected void toast(int message) {
        Toast.makeText(
                getApplicationContext(),
                getString(message),
                Toast.LENGTH_SHORT
        ).show();
    }
}
