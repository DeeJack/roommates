package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.NoteListAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.TurniAdapterProva;
import it.unitn.disi.fumiprovv.roommates.models.Note;
import it.unitn.disi.fumiprovv.roommates.models.Turno;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TurniProvaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TurniProvaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TurniProvaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TurniProvaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TurniProvaFragment newInstance(String param1, String param2) {
        TurniProvaFragment fragment = new TurniProvaFragment();
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
        View view = inflater.inflate(R.layout.fragment_turni_prova, container, false);

        ListView turniListView = view.findViewById(R.id.turniListView);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        ImageButton addTurniButton = view.findViewById(R.id.addTurniButton);

        //navigazione a nuovo turno di pulizia
        addTurniButton.setOnClickListener(v -> {
            NavigationUtils.navigateTo(R.id.action_turniProvaFragment_to_newTurnoProva, view);
        });

        db.collection("turniPulizia").whereEqualTo("house", houseViewModel.getHouseId())
                .get()
                .addOnCompleteListener(task -> {
                    //NoteListAdapter adapter = new NoteListAdapter(getContext(), new ArrayList<>());
                    TurniAdapterProva adapter = new TurniAdapterProva(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        return;
                    }
                    List<Turno> turni = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        Turno turno = new Turno(
                                documentSnapshot.getString("name"),
                                documentSnapshot.getLong("weekStart"),
                                documentSnapshot.getLong("yearStart"),
                                (ArrayList<String>) documentSnapshot.get("users"),
                                documentSnapshot.getString("house"),
                                documentSnapshot.getLong("yearLast"),
                                documentSnapshot.getLong("weekLast")
                        );
                        turno.setId(documentSnapshot.getId());
                        Log.d("turnoaaa", turno.toString());
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
                    if (getContext() == null)
                        return;
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    turniListView.setDividerHeight(dividerHeight);

                    // Imposta il listener di click sugli elementi della lista
                    turniListView.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view1, position, id) -> {
                        Turno selectedItem = turni.get(position);
                        String a = "";
                    });
                });

        return view;
    }
}