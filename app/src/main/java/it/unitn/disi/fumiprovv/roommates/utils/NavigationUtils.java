package it.unitn.disi.fumiprovv.roommates.utils;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

public class NavigationUtils {
    public static void navigateTo(int destinationId, View view) {
        navigateTo(destinationId, view, null);
    }

    public static void navigateTo(int destinationId, View view, Bundle bundle) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(destinationId, bundle);
    }

    public static void conditionalLogin(NavController navController, SharedPreferences sharedPref, ViewModelStoreOwner owner) {
        conditionalLogin(navController, null, sharedPref, owner);
    }

    public static void conditionalLogin(NavController navController, Bundle bundle, SharedPreferences sharedPref, ViewModelStoreOwner owner) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("utenti").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String casaId = document.getString("casa");
                    if (casaId != null) {
                        HouseViewModel houseViewModel = new ViewModelProvider(owner).get(HouseViewModel.class);
                        houseViewModel.setHouseId(casaId);
                        sharedPref.edit().putString("houseId", casaId).apply();

                        navController.navigate(R.id.action_loginFragment_to_homeFragment, bundle);
                    } else {
                        HouseViewModel houseViewModel = new ViewModelProvider(owner).get(HouseViewModel.class);
                        houseViewModel.setHouseId("");
                        sharedPref.edit().putString("houseId", "").apply();
                        navController.navigate(R.id.action_loginFragment_to_houseCreationFragment, bundle);
                    }
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}
