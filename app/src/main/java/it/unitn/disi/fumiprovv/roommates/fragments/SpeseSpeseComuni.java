package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SpeseComuniAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Event;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.utils.ItemAdapter;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseSpeseComuni#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseSpeseComuni extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SpeseComuniAdapter speseComuniAdapter;

    public SpeseSpeseComuni() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeseSpeseComuni.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseSpeseComuni newInstance(String param1, String param2) {
        SpeseSpeseComuni fragment = new SpeseSpeseComuni();
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
        View view = inflater.inflate(R.layout.fragment_spese_spese_comuni, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSpese);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        speseComuniAdapter = new SpeseComuniAdapter();
        recyclerView.setAdapter(speseComuniAdapter);

        //carica le spese comuni dal db
        loadSpese();

        //aggiungi listener al bottone aggiungi spesa


        return view;
    }

    private void loadSpese() {

        List<SpesaComune> filteredEvents = new ArrayList<>();
        filteredEvents.clear();
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String casa = houseViewModel.getHouseId();
        db.collection("speseComuni").whereEqualTo("casa",casa).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Map<String, Object> data = document.getData();

                        String casa = (String) data.get("casa");
                        String nome = (String) data.get("nome");
                        Long valore = (Long) data.get("valore");
                        String responsabile = (String) data.get("responsabile");
                        String ripetizione = (String) data.get("ripetizione");

                        SpesaComune temp = new SpesaComune(casa, nome, valore, responsabile, ripetizione);

                        filteredEvents.add(temp);
                        Log.d("prova", temp.toString());
                    }
                    speseComuniAdapter.setData(filteredEvents);
                } else {
                    Log.d("error", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}