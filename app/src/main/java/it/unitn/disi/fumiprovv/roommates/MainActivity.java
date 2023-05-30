package it.unitn.disi.fumiprovv.roommates;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        View view = findViewById(R.id.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

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

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_item_home) {
                    navController.navigate(R.id.homeFragment);
                } else if (itemId == R.id.menu_item_calendario) {
                    navController.navigate(R.id.action_to_calendario);
                } else if (itemId == R.id.menu_item_gestioneSpese) {
                    navController.navigate(R.id.action_to_spese_comuni);
                } else if (itemId == R.id.menu_item_note) {
                    navController.navigate(R.id.noteFragment);
                } else if (itemId == R.id.menu_item_rubrica) {
                    navController.navigate(R.id.contactFragment);
                } else if (itemId == R.id.menu_item_listaSpesa) {
                    navController.navigate(R.id.shoppingListFragment);
                } else if (itemId == R.id.menu_item_sondaggi) {
                    navController.navigate(R.id.homeFragment);
                } else if (itemId == R.id.menu_item_turni) {
                    navController.navigate(R.id.action_to_turnipulizia);
                }

                // Close the drawer
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }
}