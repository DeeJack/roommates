package it.unitn.disi.fumiprovv.roommates.fragments.duties;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.TurniPuliziaAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Turno;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TurniPuliziaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TurniPuliziaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TurniPuliziaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TurniPuliziaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TurniPuliziaFragment newInstance(String param1, String param2) {
        TurniPuliziaFragment fragment = new TurniPuliziaFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_turni_pulizia, container, false);

        ListView turniListView = view.findViewById(R.id.turniListView);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        Button addTurniButton = view.findViewById(R.id.addTurniButton);
        addTurniButton.setVisibility(View.GONE);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection("case").document(houseViewModel.getHouseId()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            List<Map<String, Object>> roommates = (List<Map<String, Object>>) task.getResult().get("roommates");
            roommates.stream().filter(roommate -> Objects.equals(roommate.get("userId"), auth.getCurrentUser().getUid())).forEach(roommate -> {
                if ((boolean) roommate.get("moderator")) {
                    addTurniButton.setVisibility(View.VISIBLE);
                }
            });
        });

        //navigazione a nuovo turno di pulizia
        addTurniButton.setOnClickListener(v -> NavigationUtils.navigateTo(R.id.action_turniPuliziaFragment_to_nuovoTurnoPulizia, view));

        db.collection("turniPulizia").whereEqualTo("house", houseViewModel.getHouseId()).get().addOnCompleteListener(task -> {
            //NoteListAdapter adapter = new NoteListAdapter(getContext(), new ArrayList<>());
            TurniPuliziaAdapter adapter = new TurniPuliziaAdapter(getContext(), new ArrayList<>());
            if (!task.isSuccessful()) {
                return;
            }
            List<Turno> turni = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                Turno turno = new Turno(documentSnapshot.getString("name"), documentSnapshot.getLong("weekStart"), documentSnapshot.getLong("yearStart"), (ArrayList<String>) documentSnapshot.get("users"), documentSnapshot.getString("house"), documentSnapshot.getLong("yearLast"), documentSnapshot.getLong("weekLast"));
                turno.setId(documentSnapshot.getId());
                Log.d("turnoaaa", turno.getUsers().toString());
                for (String id : turno.getUsers()) {
                    Log.d("turnobbb", id);
                    db.collection("utenti").document(id).get().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            return;
                        }
                        String nome = task1.getResult().getString("name");
                        Log.d("turnoccc", nome);
                        turno.addUserName(id, nome);
                        Log.d("turnoddd", turno.getUserNameAt(id));
                        adapter.notifyDataSetChanged();
                    });
                }
                        /*for(String id: turno.getUsers()) {
                            documentSnapshot.getDocumentReference(id).get().addOnCompleteListener(task1 -> {
                                if (!task1.isSuccessful()) {
                                    return;
                                }
                                turno.addUserName((String) task1.getResult().get("name"));
                                adapter.notifyDataSetChanged();
                            });
                        }*/

                return turno;
            }).collect(Collectors.toList());
            adapter.setTurni(turni);

            turniListView.setAdapter(adapter);
            if (getContext() == null) return;
            int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
            turniListView.setDividerHeight(dividerHeight);

            // Imposta il listener di click sugli elementi della lista
            //turniListView.setOnItemClickListener((parent, view1, position, id) -> {
            //    Turno selectedItem = turni.get(position);
            //});
        });

        return view;
    }
}