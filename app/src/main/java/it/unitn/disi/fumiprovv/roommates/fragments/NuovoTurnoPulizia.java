package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Turno;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuovoTurnoPulizia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuovoTurnoPulizia extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NuovoTurnoPulizia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuovoTurnoPulizia.
     */
    // TODO: Rename and change types and number of parameters
    public static NuovoTurnoPulizia newInstance(String param1, String param2) {
        NuovoTurnoPulizia fragment = new NuovoTurnoPulizia();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nuovo_turno_pulizia, container, false);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        Spinner userSpinner = view.findViewById(R.id.userSpinner);
        Button buttonAggiungi = view.findViewById(R.id.buttonNewUser);
        Button buttonCrea = view.findViewById(R.id.creaTurnoButton);
        EditText luogoText = view.findViewById(R.id.placeTurno);

        ArrayList<String> userNames = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);

        ListView userListView = view.findViewById(R.id.selectedUsersList);

        ArrayList<String> selectedUsers = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selectedUsers);
        userListView.setAdapter(adapter);

        String houseId = houseViewModel.getHouseId();
        if (houseId != null) {
            DocumentReference houseRef = db.collection("case").document(houseId);
            houseRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    ArrayList<Map<String,String>> roommates = (ArrayList<Map<String,String>>) documentSnapshot.get("roommates");
                    if (roommates != null) {
                        for (Map<String, String> roommate : roommates) {
                            String userId = roommate.get("userId");
                            if (userId != null) {
                                userIds.add(userId);
                                db.collection("utenti").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String nome = (String) task.getResult().get("name");
                                            userNames.add(nome);
                                        }
                                    }
                                });
                            }
                        }
                        spinnerAdapter.addAll(userNames);
                        spinnerAdapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(e -> {
                Log.d("error", "Failed to retrieve users from the house: " + e.getMessage());
            });
        }

        buttonAggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userNames.size()>0) {
                    String selectedUser = (String) userSpinner.getSelectedItem().toString();
                    Log.d("user", selectedUser);
                    selectedUsers.add(selectedUser);
                    userNames.remove(selectedUser);
                    if(userNames.size()>0)
                        userSpinner.setPrompt(userNames.get(0));
                    adapter.notifyDataSetChanged();
                    spinnerAdapter.notifyDataSetChanged();
                }

            }
        });

        buttonCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                Long currentWeek = new Long(calendar.get(Calendar.WEEK_OF_YEAR));
                Long currentYear = new Long(calendar.get(Calendar.YEAR));
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                Long weekLast = new Long(calendar.get(Calendar.WEEK_OF_YEAR));
                Turno t = new Turno(
                    luogoText.getText().toString(), currentWeek, currentYear, selectedUsers, houseViewModel.getHouseId(), currentYear, weekLast
                );
                db.collection("turniPulizia").add(t).addOnCompleteListener(task -> {
                    if(!task.isSuccessful()) {
                        return;
                    }
                    String documentId = task.getResult().getId();
                    DocumentReference documentRef = db.collection("turniPulizia").document(documentId);
                    documentRef.update("id", documentId)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("success", "successfully updated id");
                            })
                            .addOnFailureListener(e -> {
                                Log.d("error", "error updating the id");
                            });
                });
            }
        });

        return view;
    }
}