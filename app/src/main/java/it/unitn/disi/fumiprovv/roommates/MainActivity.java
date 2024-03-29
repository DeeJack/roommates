package it.unitn.disi.fumiprovv.roommates;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;
import it.unitn.disi.fumiprovv.roommates.viewmodels.JoinHouseViewModel;
import it.unitn.disi.fumiprovv.roommates.viewmodels.UserViewModel;

public class MainActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the house id from the memory
        SharedPreferences sharedPref = getSharedPreferences("house", MODE_PRIVATE);
        String houseId = sharedPref.getString("houseId", "");
        HouseViewModel houseViewModel = new ViewModelProvider(this).get(HouseViewModel.class);
        houseViewModel.setHouseId(houseId);

        // Load the current user from the ID if logged, very useful to avoid delay thanks to the cache
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AtomicBoolean ready = new AtomicBoolean(false);
        if (mAuth.getCurrentUser() != null)
            db.collection("utenti").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> ready.set(true));

        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.navigation_view);
        //nameView = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.nameDrawerField);
        nameView = nvDrawer.getHeaderView(0).findViewById(R.id.helloUserField);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        //drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        setDrawerLocked(true);

        if (mAuth.getCurrentUser() != null) {
            if (data != null) {
                String code = data.getQueryParameter("code");
                JoinHouseViewModel sharedViewModel = new ViewModelProvider(this).get(JoinHouseViewModel.class);
                sharedViewModel.setHouseId(code);
            }
            UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.setName(mAuth.getCurrentUser().getDisplayName());

            NavigationUtils.conditionalLogin(navController, sharedPref, this, intent);
        } else {
            // The starting fragment is already the login fragment
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setItemIconTintList(getResources().getColorStateList(R.color.primaryTextColor));
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navigationView.setCheckedItem(R.id.menu_item_home);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            selectDrawerItem(menuItem, navController, navigationView);
            return true;
        });
    }

    private void selectDrawerItem(MenuItem menuItem, NavController navController, NavigationView navigationView) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.menu_item_home) {
            navController.navigate(R.id.action_to_home);
        } else if (itemId == R.id.menu_item_calendario) {
            navController.navigate(R.id.action_to_calendario);
        } else if (itemId == R.id.menu_item_gestioneSpese) {
            navController.navigate(R.id.action_to_spese2);
        } else if (itemId == R.id.menu_item_note) {
            navController.navigate(R.id.action_to_note);
        } else if (itemId == R.id.menu_item_rubrica) {
            navController.navigate(R.id.action_to_rubrica);
        } else if (itemId == R.id.menu_item_listaSpesa) {
            navController.navigate(R.id.action_to_spesa);
        } else if (itemId == R.id.menu_item_sondaggi) {
            navController.navigate(R.id.action_to_sondaggi);
        } else if (itemId == R.id.menu_item_turni) {
            navController.navigate(R.id.action_to_turnipulizia);
        } else if (itemId == R.id.menu_item_impostazioni) {
            navController.navigate(R.id.action_to_impostazioni);
        }
        menuItem.setChecked(true);
        /*TextView customTitleTextView = new TextView(this);
        customTitleTextView.setBackgroundColor(Color.TRANSPARENT);
        customTitleTextView.setText(menuItem.getTitle());
        customTitleTextView.setTextColor(Color.WHITE);

        toolbar.addView(customTitleTextView);
        toolbar.setTitle("");*/
        setTitle(menuItem.getTitle());

        // Close the drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void setDrawerLocked(boolean lock) {
        if (lock) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            setTitle("Roommates");
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    public void setName(String name) {
        nameView.setText("Hello " + name);
    }

    public void showHamburger(boolean show) {
        drawerToggle.setDrawerIndicatorEnabled(show);
    }

    public void selectHome() {
        nvDrawer.getMenu().getItem(0).setChecked(true);
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
    }
}