package it.unitn.disi.fumiprovv.roommates.fragments.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.House;
import it.unitn.disi.fumiprovv.roommates.models.Roommate;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;
import it.unitn.disi.fumiprovv.roommates.viewmodels.JoinHouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseCreationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HouseCreationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HouseCreationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HouseCreationFragment newInstance(String param1, String param2) {
        HouseCreationFragment fragment = new HouseCreationFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setDrawerLocked(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_house_creation, container, false);
        Button joinBtn = view.findViewById(R.id.joinButton);
        joinBtn.setOnClickListener((a) -> onJoinButtonClick(view));
        Button createBtn = view.findViewById(R.id.createButton);
        createBtn.setOnClickListener((a) -> onCreateButtonClick(view));
        JoinHouseViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(JoinHouseViewModel.class);
        if (sharedViewModel.isHouseIdValid()) {
            ((TextView) view.findViewById(R.id.joinHouseField)).setText(sharedViewModel.getHouseId());
        }
        return view;
    }

    public void onJoinButtonClick(View view) {
        ProgressBar progressBar = view.findViewById(R.id.houseProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        String houseId = ((TextView) view.findViewById(R.id.joinHouseField)).getText().toString().toUpperCase();
        String userId = mAuth.getUid();

        if (userId == null) return;
        if (houseId.isEmpty()) {
            Toast.makeText(getContext(), R.string.error_empty_house_id, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        WriteBatch batch = db.batch();
        batch.update(db.collection("case").document(houseId), "roommates", FieldValue.arrayUnion(new Roommate(userId, Timestamp.now(), false)));
        batch.update(db.collection("utenti").document(userId), "casa", houseId);

        db.collection("case").document(houseId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> roommates = (List<Map<String, Object>>) document.get("roommates");
                    if (roommates != null) {
                        for (Map<String, Object> roommate : roommates) {
                            String user = (String) roommate.get("userId");
                            // Use the userId as needed
                            Map<String, Object> newDebt = new HashMap<>();
                            newDebt.put("houseId", houseId);
                            newDebt.put("idFrom", userId);
                            newDebt.put("idTo", user);
                            newDebt.put("amount", new Double(0.0));
                            db.collection("debiti").add(newDebt);
                        }
                    }
                } else {
                    // Document does not exist
                }
            } else {
                // Handle task failure
            }
        });

        batch.commit().addOnSuccessListener(task -> {
            updateHouseId(houseId);

            progressBar.setVisibility(View.GONE);
            NavigationUtils.navigateTo(R.id.action_houseCreationFragment_to_homeFragment, view);
        }).addOnFailureListener(task -> {
            Toast.makeText(getContext(), R.string.error_join_house, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    public void updateHouseId(String houseId) {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("house", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("houseId", houseId);
        editor.apply();

        HouseViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        sharedViewModel.setHouseId(houseId);
    }

    public void onCreateButtonClick(View view) {
        ProgressBar progressBar = view.findViewById(R.id.houseProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        String houseName = ((TextView) view.findViewById(R.id.createHouseField)).getText().toString();
        String userId = mAuth.getUid();
        House house = House.createHouse(houseName, userId);

        if (userId == null) return;

        WriteBatch batch = db.batch();
        batch.set(db.collection("case").document(house.getId()), house);
        batch.update(db.collection("utenti").document(userId), "casa", house.getId());

        batch.commit().addOnSuccessListener(task -> {
            Bundle bundle = new Bundle();
            bundle.putString("houseName", house.getName());
            bundle.putString("houseId", house.getId());

            updateHouseId(house.getId());

            progressBar.setVisibility(View.GONE);
            NavigationUtils.navigateTo(R.id.action_houseCreationFragment_to_houseCreatedFragment, view, bundle);
        }).addOnFailureListener(task -> {
            Toast.makeText(getContext(), R.string.error_create_house, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}