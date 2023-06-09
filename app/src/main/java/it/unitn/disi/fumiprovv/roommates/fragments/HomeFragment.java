package it.unitn.disi.fumiprovv.roommates.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.NoteListAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.UserAdapter;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //((TextView) view.findViewById(R.id.user_name)).setText(auth.getCurrentUser().getEmail());
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this::onLogoutClick);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setDrawerLocked(false);
        mainActivity.setName(auth.getCurrentUser().getDisplayName());

        mainActivity.selectHome();

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        ArrayList<String> listUtenti = new ArrayList<>();
        ListView utentiListView = view.findViewById(R.id.usersListView);

        db.collection("case").document(houseViewModel.getHouseId()).get()
                .addOnCompleteListener(task -> {
                    UserAdapter adapter = new UserAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        return;
                    }
                    DocumentSnapshot document = task.getResult();
                    List<Map<String, Object>> roommates = (List<Map<String, Object>>) document.get("roommates");
                    for (Map<String, Object> roommate : roommates) {
                        //prendo nome utente
                        db.collection("utenti").document((String)roommate.get("userId")).get().addOnCompleteListener(task1 -> {
                            String nome = task1.getResult().getString("name");
                            adapter.addUser(nome);
                            adapter.notifyDataSetChanged();
                        });
                    }
                    utentiListView.setAdapter(adapter);
                });

        return view;
    }

    void onLogoutClick(View view) {
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        houseViewModel.setHouseId("");
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("house", MODE_PRIVATE);
        sharedPref.edit().putString("houseId", "").apply();
        FirebaseAuth.getInstance().signOut();
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_homeFragment_to_loginFragment);
    }
}