package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SpeseComuniAdapter;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseComuniFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseComuniFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SpeseComuniFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProvaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseComuniFragment newInstance(String param1, String param2) {
        SpeseComuniFragment fragment = new SpeseComuniFragment();
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
        View view = inflater.inflate(R.layout.fragment_spese_comuni, container, false);
        SpeseComuniAdapter adapter = new SpeseComuniAdapter(getContext(), new ArrayList<>());

        ListView lista = view.findViewById(R.id.provaList);

        db.collection("speseComuni").get().addOnCompleteListener( task -> {
            if (!task.isSuccessful()) {
                return;
            }
            List<SpesaComune> spese = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                SpesaComune spesa = new SpesaComune((String) documentSnapshot.get("casa"), (String) documentSnapshot.get("nome"), (Long) documentSnapshot.get("valore"), (String) documentSnapshot.get("responsabile"), (String) documentSnapshot.get("ripetizione"));
                return spesa;
            }).collect(Collectors.toList());
            adapter.setItems(spese);

            lista.setAdapter(adapter);
            if (getContext() == null) {
                return;
            }
        });

        Button buttonNuovaSpesaComune = view.findViewById(R.id.buttonNuovaSpesaComune);



        if(true) {
            buttonNuovaSpesaComune.setVisibility(View.VISIBLE);
        }

        return view;
    }
}