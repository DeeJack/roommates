package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.NoteListAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.TurniAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Note;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_turnipulizia, container, false);

        ListView turniThisWeekList = view.findViewById(R.id.listThisWeekCleaning);
        ListView turniNextWeekList = view.findViewById(R.id.listNextWeekCleaning);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        db.collection("turniPulizia").whereEqualTo("house", houseViewModel.getHouseId()).get().addOnCompleteListener( task -> {
            TurniAdapter adapter1 = new TurniAdapter(getContext(), new ArrayList<Turno>(), true);
            TurniAdapter adapter2 = new TurniAdapter(getContext(), new ArrayList<Turno>(), false);
            if (!task.isSuccessful()) {
                return;
            }
            List<Turno> turni = task.getResult().getDocuments().stream().map( documentSnapshot -> {
                Turno turno = new Turno(
                        (String) documentSnapshot.get("name"),
                        (Long) documentSnapshot.get("weekStart"),
                        (Long) documentSnapshot.get("yearStart"),
                        (ArrayList<String>) documentSnapshot.get("users"),
                        (String) documentSnapshot.get("house"),
                        (Long) documentSnapshot.get("yearLast"),
                        (Long) documentSnapshot.get("weekLast")
                        );
                turno.setId((String) documentSnapshot.getId());
                return turno;
            }).collect(Collectors.toList());
            adapter1.setTurni(turni);
            adapter2.setTurni(turni);

            turniThisWeekList.setAdapter(adapter1);
            turniNextWeekList.setAdapter(adapter2);
        });

        /*db.collection("turniPulizia").whereEqualTo("casa", houseViewModel.getHouseId()).get().addOnCompleteListener( task -> {
            TurniAdapter adapter = new TurniAdapter(getContext(), new ArrayList<Turno>(), false);
            if (!task.isSuccessful()) {
                return;
            }
            List<Turno> turni = task.getResult().getDocuments().stream().map( documentSnapshot -> {
                Turno turno = new Turno(
                        (String) documentSnapshot.get("nome"),
                        (Long) documentSnapshot.get("settimanaInizio"),
                        (Long) documentSnapshot.get("annoInizio"),
                        (ArrayList<String>) documentSnapshot.get("utenti"),
                        (String) documentSnapshot.get("casa")
                );
                turno.setId((String) documentSnapshot.getId());
                return turno;
            }).collect(Collectors.toList());
            adapter.setTurni(turni);

            turniNextWeekList.setAdapter(adapter);
        });*/

        view.findViewById(R.id.buttonNuovoTurno).setOnClickListener(v -> NavigationUtils.navigateTo(R.id.action_turniPuliziaFragment_to_nuovoTurnoPulizia, view));

        return view;
    }
}