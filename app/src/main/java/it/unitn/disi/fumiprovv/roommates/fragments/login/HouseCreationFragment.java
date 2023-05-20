package it.unitn.disi.fumiprovv.roommates.fragments.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.House;
import it.unitn.disi.fumiprovv.roommates.models.Roommate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseCreationFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_house_creation, container, false);
        Button joinBtn = (Button) view.findViewById(R.id.joinButton);
        joinBtn.setOnClickListener((a) -> onJoinButtonClick(view));
        Button createBtn = (Button) view.findViewById(R.id.createButton);
        createBtn.setOnClickListener((a) -> onCreateButtonClick(view));
        return view;
    }

    public void onJoinButtonClick(View view) {
        String houseId = ((TextView) view.findViewById(R.id.joinHouseField)).getText().toString();
        String userId = mAuth.getUid();
        db.collection("case").document(houseId).update("roommates", FieldValue.arrayUnion(new Roommate(userId, Timestamp.now(), false)))
                .addOnSuccessListener(task -> {
                    //House house = task.toObject(House.class);
                    //house.addRoommate(new Roommate(userId, Timestamp.now(), false));
                    //db.collection("case").document(houseId).set(house);
                    db.collection("utenti").document(userId).update("casa", houseId);
                })
                .addOnFailureListener(task -> {
                   Toast.makeText(getContext(), R.string.error_join_house, Toast.LENGTH_SHORT).show();
                });

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_houseCreationFragment_to_homeFragment);
    }

    public void onCreateButtonClick(View view) {
        String houseName = ((TextView) view.findViewById(R.id.createHouseField)).getText().toString();
        String userId = mAuth.getUid();
        House house = House.createHouse(houseName, userId);
        db.collection("case").document(house.getId()).set(house).addOnSuccessListener(aVoid -> {
                    db.collection("utenti").document(userId).update("casa", house.getId());

                    Bundle bundle = new Bundle();
                    bundle.putString("houseName", house.getName());
                    bundle.putString("houseId", house.getId());
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_houseCreationFragment_to_houseCreatedFragment, bundle);
                })
                .addOnFailureListener(e -> {
                    Log.d("HouseCreationFragment", "Error writing document", e);
                    Toast.makeText(getContext(), "Error creating house", Toast.LENGTH_SHORT).show();
                });
    }
}