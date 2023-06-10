package it.unitn.disi.fumiprovv.roommates.fragments.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.models.Roommate;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

public class ModeratorDialogFragment extends DialogFragment {
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private final Button newModeratorButton;

    public ModeratorDialogFragment(Button newModeratorButton) {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.newModeratorButton = newModeratorButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Crea un ListView per visualizzare gli elementi
        ListView listView = new ListView(getContext());
        getItems().thenAccept(roommates -> getNames(roommates).thenAccept(names -> {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, names);
            listView.setAdapter(adapter);

            // Imposta il listener di click sugli elementi della lista
            listView.setOnItemClickListener((parent, view, position, id) -> onClickListener(roommates, position));
        }));
        builder.setView(listView);

        builder.setTitle("Lista di elementi");
        return builder.create();
    }

    public void onClickListener(List<Roommate> roommates, int position) {
        Roommate selectedRoommate = roommates.get(position);

        if (Objects.equals(selectedRoommate.getUserId(), mAuth.getCurrentUser().getUid()))
            return;

        // Get the house id from the viewModel
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        // Look for the document with that id in the DB
        DocumentReference documentSnapshotTask = db.collection("case").document(houseViewModel.getHouseId());
        documentSnapshotTask.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();

            List<Map<String, Object>> roommatesList = (List<Map<String, Object>>) document.getData().get("roommates");
            // FInd the user with the same id as the logged user
            roommatesList.stream()
                    .filter(roommate -> Objects.equals(roommate.get("userId"), mAuth.getCurrentUser().getUid()))
                    .findFirst()
                    .ifPresent(roommate -> roommate.put("moderator", false));
            roommatesList.stream()
                    .filter(roommate -> Objects.equals(roommate.get("userId"), selectedRoommate.getUserId()))
                    .findFirst()
                    .ifPresent(roommate -> roommate.put("moderator", true));

            db.collection("case").document(houseViewModel.getHouseId())
                    .update("roommates", roommatesList);
            if (newModeratorButton != null) {
                newModeratorButton.setEnabled(false);
            }
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

    public CompletableFuture<List<String>> getNames(List<Roommate> roommates) {
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        CompletableFuture<List<String>> namesTask = new CompletableFuture<>();

        Query query = db.collection("utenti")
                .whereEqualTo("casa", houseViewModel.getHouseId());

        Task<QuerySnapshot> querySnapshot = query.get();
        querySnapshot.addOnCompleteListener((task) -> {
            if (!task.isSuccessful()) {
                namesTask.complete(new ArrayList<>());
                return;
            }
            Map<String, String> namesIds = new HashMap<>();
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                String name = document.getString("name");
                namesIds.put(document.getId(), name);
            }
            // Sort names list based on the order of the roommates list where the id are the same
            List<String> names = new ArrayList<>();
            for (Roommate roommate : roommates) {
                String userId = roommate.getUserId();
                String name = namesIds.get(userId);
                if (name != null) {
                    names.add(name);
                }
            }
            namesTask.complete(names);
        });


        return namesTask;
    }


}
