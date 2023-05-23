package it.unitn.disi.fumiprovv.roommates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

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

            // TODO: remove
            SharedPreferences sharedPref = getSharedPreferences("house", MODE_PRIVATE);
            String houseId = sharedPref.getString("houseId", "");
            HouseViewModel houseViewModel = new ViewModelProvider(this).get(HouseViewModel.class);
            houseViewModel.setHouseId(houseId);

            navController.navigate(R.id.settingsFragment);

            //NavigationUtils.conditionalLogin(navController, sharedPref, houseViewModel);
        } else {
            //navController.navigate(R.id.loginFragment);
            // The starting fragment is already the login fragment
        }
    }
}