package ar.uba.fi.mercadolibre.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends BaseActivity {
    double MAX_DIFF = 0.003;
    MapView map;
    GeoPoint buenosAires = new GeoPoint(-34.62, -58.44);
    HashMap<String, Article> articles;
    OverlayItem lastClickedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMap();
        placeArticlesOnMap();
    }

    private void initMap() {
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
        repositionMap();
    }

    private void repositionMap() {
        IMapController mapController = map.getController();
        mapController.setZoom(11L);
        mapController.setCenter(buenosAires);
    }

    private void placeArticlesOnMap() {
        ControllerFactory.getArticleController().list().enqueue(new Callback<APIResponse<List<Article>>>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse<List<Article>>> call,
                                   @NonNull Response<APIResponse<List<Article>>> response) {
                List<Article> articles = getData(response);
                if (articles == null) return;
                if (articles.isEmpty()) toast(R.string.no_results);
                initArticles(articles);
                placeArticlesOnMap(buildOverlayItems(articles));
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse<List<Article>>> call,
                                  @NonNull Throwable t) {
                onGetDataFailure(t);
            }
        });
    }

    private void initArticles(List<Article> articles) {
        this.articles = new HashMap<>();
        for (Article article : articles) this.articles.put(article.getID(), article);
    }

    private ArrayList<OverlayItem> buildOverlayItems(List<Article> articles) {
        ArrayList<OverlayItem> items = new ArrayList<>();
        ArrayList<IGeoPoint> geoPoints = new ArrayList<>();
        for (Article article : articles) {
            IGeoPoint geoPoint = moveIfAlreadyFound(
                    article.getGeoPoint(),
                    geoPoints
            );
            geoPoints.add(geoPoint);
            items.add(new OverlayItem(
                    article.getID(),
                    String.format(
                            Locale.getDefault(),
                            "%s, $%.2f",
                            article.getName(),
                            article.getPrice()
                    ),
                    getString(R.string.touch_again_for_detail),
                    geoPoint
            ));
        }
        return items;
    }

    private IGeoPoint moveIfAlreadyFound(final IGeoPoint geoPoint, ArrayList<IGeoPoint> geoPoints) {
        if (!Iterables.any(geoPoints, new Predicate<IGeoPoint>() {
            @Override
            public boolean apply(@NonNull IGeoPoint usedGeoPoint) {
                return usedGeoPoint.getLatitude() == geoPoint.getLatitude() &&
                        usedGeoPoint.getLongitude() == geoPoint.getLongitude();
            }
        })) return geoPoint;

        return moveIfAlreadyFound(
                new GeoPoint(
                        geoPoint.getLatitude() + getRandomDiff(),
                        geoPoint.getLongitude() + getRandomDiff()
                ),
                geoPoints
        );
    }

    private double getRandomDiff() {
        double minusOneToOne = (Math.random() - 0.5) * 2;
        return minusOneToOne * MAX_DIFF;
    }

    private void placeArticlesOnMap(ArrayList<OverlayItem> items) {
        final boolean eventWasHandled = true;

        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> listener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                if (item == lastClickedItem) {
                    Article article = articles.get(item.getUid());
                    if (article == null) {
                        toast(R.string.generic_error);
                        return eventWasHandled;
                    }
                    Intent intent = new Intent(getApplicationContext(), ArticleDetailActivity.class);
                    intent.putExtra("article", article);
                    startActivity(intent);
                }
                lastClickedItem = item;
                return eventWasHandled;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                return eventWasHandled;
            }
        };
        ItemizedOverlayWithFocus<OverlayItem> overlay =
                new ItemizedOverlayWithFocus<>(
                        items,
                        listener,
                        getApplicationContext()
                );
        overlay.setFocusItemsOnTap(true);
        map.getOverlays().add(overlay);
        repositionMap();
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
