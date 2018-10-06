package ar.uba.fi.mercadolibre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.koushikdutta.ion.Ion;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOError;
import java.io.IOException;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.client.RetrofitClient;
import ar.uba.fi.mercadolibre.controller.APIResponse;
import ar.uba.fi.mercadolibre.controller.InvalidResponseException;
import okhttp3.ResponseBody;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity {
    static final public boolean KEEP_DRAWER_OPEN_ON_ITEM_CLICK = false;

    static final public int HOME_IDENTIFIER = 1;
    static final public int MY_ACCOUNT_IDENTIFIER = 2;
    static final public int SIGN_OUT_IDENTIFIER = 3;
    static final public int MY_ITEMS_IDENTIFIER = 4;

    private static final SparseArray<Class<?>> activityClasses =
            getActivitiesByIdentifier();

    private static SparseArray<Class<?>> getActivitiesByIdentifier() {
        SparseArray<Class<?>> activities = new SparseArray<>();
        activities.append(HOME_IDENTIFIER, MainMenuActivity.class);
        activities.append(MY_ACCOUNT_IDENTIFIER, AccountDetailActivity.class);
        activities.append(SIGN_OUT_IDENTIFIER, SignOutActivity.class);
        activities.append(MY_ITEMS_IDENTIFIER, UserArticlesActivity.class);
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
        new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withCloseOnClick(true)
                .withToolbar(toolbar)
                .withSelectedItem(identifierForDrawer())
                .addDrawerItems(home, myAccount, myItems, new DividerDrawerItem(), signOut)
                .withOnDrawerItemClickListener(drawerItemClickListener())
                .build();
    }

    private Drawer.OnDrawerItemClickListener drawerItemClickListener() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                startActivity(new Intent(
                        getApplicationContext(),
                        activityClasses.get((int) drawerItem.getIdentifier())
                ));
                return KEEP_DRAWER_OPEN_ON_ITEM_CLICK;
            }
        };
    }

    public void fillTextView(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    public void fillImageViewWithURL(int id, String url) {
        ImageView imageView = findViewById(id);
        Ion.with(imageView)
                .resize(imageView.getWidth(), imageView.getHeight())
                .crossfade(true)
                .fitXY()
                .load(url);
    }

    protected <Data> Data getDataOrThrowException(
            @NonNull Response<APIResponse<Data>> response
    ) throws InvalidResponseException {
        if (!response.isSuccessful()) {
            ResponseBody errorBody = response.errorBody();
            String message;
            try {
                message  = errorBody.string();
            } catch (IOException e) {
                message = "Error body was null";
            }
            throw new InvalidResponseException(message);
        }
        APIResponse<Data> body = response.body();
        if (body == null) throw new InvalidResponseException("Response body was null");
        return body.getData();
    }

    /**
     * Tries to return data wrapped by APIResponse.
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
}
