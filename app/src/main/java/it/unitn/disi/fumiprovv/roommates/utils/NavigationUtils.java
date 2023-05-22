package it.unitn.disi.fumiprovv.roommates.utils;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unitn.disi.fumiprovv.roommates.R;

public class NavigationUtils {
    public static void navigateTo(int destinationId, View view) {
        navigateTo(destinationId, view, null);
    }

    public static void navigateTo(int destinationId, View view, Bundle bundle) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(destinationId, bundle);
    }

    public static void conditionalLogin(NavController navController) {
        conditionalLogin(navController, null);
    }

    public static void conditionalLogin(NavController navController, Bundle bundle) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("utenti").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String casaId = document.getString("casa");
                    if (casaId != null) {
                        navController.navigate(R.id.action_loginFragment_to_homeFragment, bundle);
                    } else {
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
