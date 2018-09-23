package ar.uba.fi.mercadolibre.activity;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import ar.uba.fi.mercadolibre.R;

public class BaseActivity extends AppCompatActivity {
    static final public boolean KEEP_DRAWER_OPEN_ON_ITEM_CLICK = false;

    static final public int HOME_IDENTIFIER = 1;
    static final public int MY_ACCOUNT_IDENTIFIER = 2;

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
        new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(false)
                .withCloseOnClick(true)
                .withToolbar(toolbar)
                .withSelectedItem(HOME_IDENTIFIER)
                .addDrawerItems(home, myAccount)
                .withOnDrawerItemClickListener(drawerItemClickListener())
                .build();
    }

    private Drawer.OnDrawerItemClickListener drawerItemClickListener() {
        return new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch ((int) drawerItem.getIdentifier()) {
                    case HOME_IDENTIFIER:
                        break;
                    case MY_ACCOUNT_IDENTIFIER:
                        break;
                    default:
                        break;
                }
                return KEEP_DRAWER_OPEN_ON_ITEM_CLICK;
            }
        };
    }
}
