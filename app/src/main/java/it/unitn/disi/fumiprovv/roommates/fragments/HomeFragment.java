package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.HomeListAdapter;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //((TextView) view.findViewById(R.id.user_name)).setText(auth.getCurrentUser().getEmail());
        //Button logoutButton = view.findViewById(R.id.logoutButton);
        //logoutButton.setOnClickListener(this::onLogoutClick);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setDrawerLocked(false);
        mainActivity.setName(auth.getCurrentUser().getDisplayName());

        mainActivity.selectHome();

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        RecyclerView utentiListView = view.findViewById(R.id.usersListView);
        HomeListAdapter adapter = new HomeListAdapter(getContext(), new ArrayList<>());

        utentiListView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        utentiListView.setAdapter(adapter);

        showEvents(view);
        showDuties(view);

        db.collection("case").document(houseViewModel.getHouseId()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            DocumentSnapshot document = task.getResult();
            List<Map<String, Object>> roommates = (List<Map<String, Object>>) document.get("roommates");
            List<String> names = new ArrayList<>();
            for (Map<String, Object> roommate : roommates) {
                //prendo nome utente
                db.collection("utenti").document((String) roommate.get("userId")).get().addOnCompleteListener(task1 -> {
                    String nome = task1.getResult().getString("name");
                    names.add(nome);
                    adapter.notifyDataSetChanged();
                });
            }
            adapter.setUsers(names);
        });

        return view;
    }

    private void showEvents(View view) {
        ListView eventsListView = view.findViewById(R.id.eventsListView);
        TextView emptyView = view.findViewById(R.id.list_empty);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        eventsListView.setEmptyView(emptyView);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("HomeFragment", "showEvents: " + year + " " + month + " " + day + " " + houseViewModel.getHouseId());

        db.collection("eventi").where(Filter.and(Filter.equalTo("anno", year), Filter.equalTo("mese", month), Filter.equalTo("giorno", day), Filter.equalTo("casa", db.collection("case").document(houseViewModel.getHouseId())))).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            List<String> events = new ArrayList<>();
            Log.d("HomeFragment", "showEvents: " + task.getResult().getDocuments());
            for (DocumentSnapshot document : task.getResult()) {
                events.add((String) document.getData().get("nome"));
            }
            Log.d("HomeFragment", "showEvents: " + events);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, events);
            eventsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
    }

    private void showDuties(View view) {
        ListView dutiesListView = view.findViewById(R.id.dutiesListView);
        TextView emptyView = view.findViewById(R.id.list_empty2);
        dutiesListView.setEmptyView(emptyView);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        db.collection("turniPulizia").whereEqualTo("house", houseViewModel.getHouseId()).whereArrayContains("users", FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            List<String> duties = new ArrayList<>();
            for (DocumentSnapshot document : task.getResult()) {
                Log.d("HomeFragment", "showDuties: " + document.getData().toString());
                Map<String, Object> data = document.getData();

                long weekStart = (long) data.get("weekStart");
                long yearStart = (long) data.get("yearStart");

                long currentYear = Calendar.getInstance().get(Calendar.YEAR);
                long currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

                List<String> users = (List<String>) data.get("users");

                //calcola indice
                long weeksPassed = ((currentYear - yearStart) * 52) + currentWeek - weekStart + 1;

                long userIndex = (weeksPassed - 1) % users.size();
                int index = Math.toIntExact(userIndex);

                if (Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getUid(), users.get(index))) {
                    duties.add((String) document.getData().get("name"));
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, duties);
            dutiesListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
    }
}