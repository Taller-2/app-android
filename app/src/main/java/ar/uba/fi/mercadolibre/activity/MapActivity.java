package ar.uba.fi.mercadolibre.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import ar.uba.fi.mercadolibre.R;

public class MapActivity extends BaseActivity {
    MapView map = null;
    GeoPoint buenosAires = new GeoPoint(-34.62, -58.44);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Configuration.getInstance().load(
                context,
                PreferenceManager.getDefaultSharedPreferences(context)
        );
        setContentView(R.layout.activity_map);
        map = findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        IMapController mapController = map.getController();
        mapController.setZoom(12L);
        mapController.setCenter(buenosAires);
    }

    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
