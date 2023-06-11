package it.unitn.disi.fumiprovv.roommates.utils;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    public static void conditionalLogin(NavController navController, SharedPreferences sharedPref,
                                        ViewModelStoreOwner owner, Intent intent) {
        conditionalLogin(navController, null, sharedPref, owner, intent);
    }

    public static void conditionalLogin(
            NavController navController, Bundle bundle, SharedPreferences sharedPref,
            ViewModelStoreOwner owner, Intent intent) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("utenti").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String casaId = document.getString("casa");
                    HouseViewModel houseViewModel = new ViewModelProvider(owner).get(HouseViewModel.class);
                    if (casaId != null) {
                        houseViewModel.setHouseId(casaId);
                        sharedPref.edit().putString("houseId", casaId).apply();

                        try {
                            navController.navigate(R.id.action_loginFragment_to_homeFragment, bundle);
                        } catch (IllegalArgumentException e) {
                            Log.d(TAG, "conditionalLogin: " + e.getMessage());
                        }
                        if (intent != null) {
                            handleIntent(intent, navController);
                        }

                    } else {
                        houseViewModel.setHouseId("");
                        sharedPref.edit().putString("houseId", "").apply();
                        navController.navigate(R.id.action_loginFragment_to_houseCreationFragment, bundle);
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private static boolean handleIntent(Intent intent, NavController navController) {
        Uri data = intent.getData();
        boolean result = false;

        if (data != null) { // Clicked on a deep link
            String path = data.getPath();
            if (path.equals("/survey")) { // Clicked on a roommates.asd/survey... link
                Bundle bundle = new Bundle();
                bundle.putString("id", data.getQueryParameter("id"));
                result = true;
                navController.navigate(R.id.action_to_sondaggi, bundle);
            } else if (path.equals("/notes")) { // Note link
                Bundle bundle = new Bundle();
                bundle.putString("id", data.getQueryParameter("id"));
                result = true;
                navController.navigate(R.id.action_to_note, bundle);
            } else if (path.equals("/turni")) { // Note link
                Bundle bundle = new Bundle();
                bundle.putString("id", data.getQueryParameter("id"));
                result = true;
                navController.navigate(R.id.action_to_turnipulizia, bundle);
            }
        }
        return result;
    }
}
