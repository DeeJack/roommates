package it.unitn.disi.fumiprovv.roommates.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Roommate;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

public class ModeratorDialogFragment extends DialogFragment {
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public ModeratorDialogFragment() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Crea un ListView per visualizzare gli elementi
        ListView listView = new ListView(getContext());
        getItems().thenAccept(roommates -> {
            String[] items = roommates.stream().map(Roommate::getUserId).toArray(String[]::new);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);

            // Imposta il listener di click sugli elementi della lista
            listView.setOnItemClickListener((parent, view, position, id) -> onClickListener(roommates, position, view));

            // Imposta il ListView nel dialo

        });
        builder.setView(listView);

        builder.setTitle("Lista di elementi");
        return builder.create();
    }

    public void onClickListener(List<Roommate> roommates, int position, View view) {
        Roommate selectedRoommate = roommates.get(position);

        // Get the house id from the viewModel
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        // Look for the document with that id in the DB
        DocumentReference documentSnapshotTask = db.collection("case").document(houseViewModel.getHouseId());
        documentSnapshotTask.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();

            List<Map<String, Object>> roommatesList = (List<Map<String, Object>>) document.getData().get("roommates");
            // FInd the user with the same id as the logged user
            Optional<Map<String, Object>> currentUserOpt = roommatesList.stream()
                    .filter(roommate -> Objects.equals(roommate.get("userId"), mAuth.getCurrentUser().getUid())).findFirst();
            Optional<Map<String, Object>> targetUserOpt = roommatesList.stream()
                    .filter(roommate -> Objects.equals(roommate.get("userId"), selectedRoommate.getUserId())).findFirst();

            // Should be impossible unless the other uses leave the house as the dialog is already open
            if (!currentUserOpt.isPresent() || !targetUserOpt.isPresent()) {
                return;
            }

            Map<String, Object> currentUser = currentUserOpt.get();
            Map<String, Object> targetUser = targetUserOpt.get();

            db.collection("case").document(houseViewModel.getHouseId())
                    .update("roommates", FieldValue.arrayRemove(new Roommate(mAuth.getCurrentUser().getUid(), (Timestamp) currentUser.get("joinDate"), false)));
            db.collection("case").document(houseViewModel.getHouseId())
                    .update("roommates", FieldValue.arrayRemove(new Roommate((String) targetUser.get("userId"), (Timestamp) targetUser.get("joinDate"), true)));

            NavigationUtils.navigateTo(R.id.action_settingsFragment_self, view);
        });

        getDialog().dismiss(); // Chiudi il dialog dopo il click
    }

    public CompletableFuture<List<Roommate>> getItems() {
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        DocumentReference documentSnapshotTask = db.collection("case").document(houseViewModel.getHouseId());
        CompletableFuture<List<Roommate>> roommatesTask = new CompletableFuture<>();
        documentSnapshotTask.get().addOnCompleteListener((task) -> {
            if (!task.isSuccessful()) {
                roommatesTask.complete(new ArrayList<>());
                return;
            }
            DocumentSnapshot document = task.getResult();
            List<Map<String, Object>> roommatesList = (List<Map<String, Object>>) document.getData().get("roommates");
            List<Roommate> roommates = roommatesList.stream()
                    .map(roommate -> new Roommate((String) roommate.get("userId"), (Timestamp) roommate.get("joinDate"), (Boolean) roommate.get("moderator")))
                    .collect(Collectors.toList());
            roommatesTask.complete(roommates);
        });
        return roommatesTask;
    }


}
