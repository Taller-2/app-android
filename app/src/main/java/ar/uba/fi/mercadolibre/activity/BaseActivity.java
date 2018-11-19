package ar.uba.fi.mercadolibre.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.client.RetrofitClient;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.ControllerFactory;
import ar.uba.fi.mercadolibre.controller.InvalidResponseException;
import ar.uba.fi.mercadolibre.model.Article;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {
    static final public boolean KEEP_DRAWER_OPEN_ON_ITEM_CLICK = false;

    static final public int HOME_IDENTIFIER = 1;
    static final public int MY_ACCOUNT_IDENTIFIER = 2;
    static final public int SIGN_OUT_IDENTIFIER = 3;
    static final public int MY_ITEMS_IDENTIFIER = 4;
    static final public int MY_PURCHASES_IDENTIFIER = 5;
    static final public int SCAN_QR_IDENTIFIER = 6;
    static final public int LIST_ARTICLES_IDENTIFIER = 7;
    static final public int MAP_IDENTIFIER = 8;
    static final public int CHAT_IDENTIFIER = 9;

    private SparseArray<Runnable> activityClasses = null;

    private SparseArray<Runnable> getActivitiesByIdentifier() {
        SparseArray<Runnable> activities = new SparseArray<>();
        activities.append(HOME_IDENTIFIER, new ActivityStarter(MainMenuActivity.class));
        activities.append(MY_ACCOUNT_IDENTIFIER, new ActivityStarter(AccountActivity.class));
        activities.append(SIGN_OUT_IDENTIFIER, new ActivityStarter(SignOutActivity.class));
        activities.append(MY_ITEMS_IDENTIFIER, new ActivityStarter(UserArticlesActivity.class));
        activities.append(MY_PURCHASES_IDENTIFIER, new ActivityStarter(UserPurchasesActivity.class));
        activities.append(LIST_ARTICLES_IDENTIFIER, new ActivityStarter(ListArticlesActivity.class));
        activities.append(MAP_IDENTIFIER, new ActivityStarter(MapActivity.class));
        activities.append(CHAT_IDENTIFIER, new ActivityStarter(ChatActivity.class));
        activities.append(SCAN_QR_IDENTIFIER, new Runnable() {
            @Override
            public void run() {
                startQrScan();
            }
        });
        return activities;
    }

    public int identifierForDrawer() {
        return HOME_IDENTIFIER;
    }

    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        super.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityClasses = getActivitiesByIdentifier();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        RetrofitClient.firebaseToken = task.getResult().getToken();
                    }
                }
            });
        }
    }

    protected void toast(int message) {
        Toast.makeText(
                getApplicationContext(),
                getString(message),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        Toolbar toolbar = createToolbar();
        addToolbar(toolbar);
        addDrawer(toolbar);
    }

    private Toolbar createToolbar() {
        Toolbar toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void addToolbar(Toolbar toolbar) {
        // toolbar needs a vertical LinearLayout with
        // id of toolbar_container for it to be added
        LinearLayout container = null;
        try {
            container = findViewById(R.id.toolbar_container);
        } catch (ClassCastException exception) {
            // toolbar_container is not a LinearLayout
        }
        if (container == null) return;
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(toolbar, 0);
    }

    private void addDrawer(Toolbar toolbar) {
        PrimaryDrawerItem home = new PrimaryDrawerItem()
                .withIdentifier(HOME_IDENTIFIER)
                .withName(R.string.home)
                .withIcon(R.drawable.ic_home_black_24dp);
        PrimaryDrawerItem myAccount = new PrimaryDrawerItem()
                .withIdentifier(MY_ACCOUNT_IDENTIFIER)
                .withName(R.string.my_account)
                .withIcon(R.drawable.ic_person_black_24dp);
        PrimaryDrawerItem signOut = new PrimaryDrawerItem()
                .withIdentifier(SIGN_OUT_IDENTIFIER)
                .withName(R.string.sign_out)
                .withIcon(R.drawable.ic_exit_to_app_black_24dp);
        PrimaryDrawerItem myItems = new PrimaryDrawerItem()
                .withIdentifier(MY_ITEMS_IDENTIFIER)
                .withName(R.string.my_items)
                .withIcon(R.drawable.ic_baseline_loyalty_24px);
        PrimaryDrawerItem scanQr = new PrimaryDrawerItem()
                .withIdentifier(SCAN_QR_IDENTIFIER)
                .withName(R.string.scan_qr)
                .withIcon(R.drawable.ic_qr_code);
        PrimaryDrawerItem myPurchases = new PrimaryDrawerItem()
                .withIdentifier(MY_PURCHASES_IDENTIFIER)
                .withName(R.string.my_purchases)
                .withIcon(R.drawable.ic_baseline_shopping_cart_24px);
        PrimaryDrawerItem listArticles = new PrimaryDrawerItem()
                .withIdentifier(LIST_ARTICLES_IDENTIFIER)
                .withName(R.string.list_articles)
                .withIcon(R.drawable.ic_search_black_24dp);
        PrimaryDrawerItem map = new PrimaryDrawerItem()
                .withIdentifier(MAP_IDENTIFIER)
                .withName(R.string.open_activity_map)
                .withIcon(R.drawable.ic_location_on_black_24dp);
        PrimaryDrawerItem chat = new PrimaryDrawerItem()
                .withIdentifier(CHAT_IDENTIFIER)
                .withName(R.string.open_chat);
        new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withCloseOnClick(true)
                .withToolbar(toolbar)
                .withSelectedItem(identifierForDrawer())
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        myAccount,
                        myItems,
                        myPurchases,
                        new DividerDrawerItem(),
                        listArticles,
                        map,
                        scanQr,
                        chat,
                        new DividerDrawerItem(),
                        signOut
                )
                .withOnDrawerItemClickListener(drawerItemClickListener())
                .build();
    }

    private Drawer.OnDrawerItemClickListener drawerItemClickListener() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                int index = (int) drawerItem.getIdentifier();
                activityClasses.get(index).run();
                return KEEP_DRAWER_OPEN_ON_ITEM_CLICK;
            }
        };
    }

    public void fillTextView(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    protected <Data> Data getDataOrThrowException(
            @NonNull Response<APIResponse<Data>> response
    ) throws InvalidResponseException {
        if (!response.isSuccessful()) {
            ResponseBody errorBody = response.errorBody();
            String message;
            try {
                message = errorBody == null ? "Error body was null" : errorBody.string();
            } catch (IOException e) {
                message = "IOException while reading the response: " + e.getMessage();
            }
            throw new InvalidResponseException(message);
        }
        APIResponse<Data> body = response.body();
        if (body == null) throw new InvalidResponseException("Response body was null");
        return body.getData();
    }

    /**
     * Tries to return dialog wrapped by APIResponse.
     * If it can't, it will call onGetDataFailure and return null.
     *
     * @return Data wrapped by APIResponse or null if there was an error
     */
    protected <Data> Data getData(@NonNull Response<APIResponse<Data>> response) {
        try {
            return getDataOrThrowException(response);
        } catch (InvalidResponseException e) {
            onGetDataFailure(e);
            return null;
        }
    }

    protected void onGetDataFailure(@NonNull Throwable t) {
        Log.e("onGetDataFailure", t.getMessage());
        toast(R.string.generic_error);
        finish();
    }

    private void startQrScan() {
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.d("QR SCAN", contents);
                getArticle(contents);

            }
            if (resultCode == RESULT_CANCELED) {
                Log.d("QR SCAN", "QR Scan cancelled");
            }
        }
    }

    void getArticle(String id) {
        final Context context = this;
        ControllerFactory.getArticleController().getByID(id).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Intent i = new Intent(context, ArticleDetailActivity.class);
                i.putExtra("article", response.body());
                startActivity(i);
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                Log.e("Article GET", t.getMessage());
            }
        });
    }

    class ActivityStarter implements Runnable {
        private Class activityClass;

        ActivityStarter(Class activityClass) {
            this.activityClass = activityClass;
        }

        @Override
        public void run() {
            startActivity(new Intent(
                    getApplicationContext(),
                    activityClass
            ));
        }
    }
}
