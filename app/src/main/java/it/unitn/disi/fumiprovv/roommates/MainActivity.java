package it.unitn.disi.fumiprovv.roommates;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;
import it.unitn.disi.fumiprovv.roommates.viewmodels.JoinHouseViewModel;
import it.unitn.disi.fumiprovv.roommates.viewmodels.UserViewModel;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        View view = findViewById(R.id.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        nameView = (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.nameDrawerField);
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

            SharedPreferences sharedPref = getSharedPreferences("house", MODE_PRIVATE);
            HouseViewModel houseViewModel = new ViewModelProvider(this).get(HouseViewModel.class);
            houseViewModel.setHouseId(sharedPref.getString("houseId", ""));

            //navController.navigate(R.id.tabsFragment);
            NavigationUtils.conditionalLogin(navController, sharedPref, this);
        } else {
            // TODO: remove
            SharedPreferences sharedPref = getSharedPreferences("house", MODE_PRIVATE);
            String houseId = sharedPref.getString("houseId", "");
            HouseViewModel houseViewModel = new ViewModelProvider(this).get(HouseViewModel.class);
            houseViewModel.setHouseId(houseId);
            //navController.navigate(R.id.loginFragment);
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
            navController.navigate(R.id.homeFragment);
        } else if (itemId == R.id.menu_item_calendario) {
            navController.navigate(R.id.calendarioFragment);
        } else if (itemId == R.id.menu_item_gestioneSpese) {
            navController.navigate(R.id.homeFragment);
        } else if (itemId == R.id.menu_item_note) {
            navController.navigate(R.id.action_to_note);
        } else if (itemId == R.id.menu_item_rubrica) {
            navController.navigate(R.id.action_to_rubrica);
        } else if (itemId == R.id.menu_item_listaSpesa) {
            navController.navigate(R.id.action_to_spesa);
        } else if (itemId == R.id.menu_item_sondaggi) {
            navController.navigate(R.id.homeFragment);
        } else if (itemId == R.id.menu_item_turni) {
            navController.navigate(R.id.turniPuliziaFragment);
        } else if (itemId == R.id.menu_item_impostazioni) {
            navController.navigate(R.id.action_to_impostazioni);
        }
        menuItem.setChecked(true);
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
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void showHamburger(boolean show) {
        if (show)
            drawerToggle.setDrawerIndicatorEnabled(true);
        else
            drawerToggle.setDrawerIndicatorEnabled(false);
    }

    public void selectHome() {
        nvDrawer.getMenu().getItem(0).setChecked(true);
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
    }
}