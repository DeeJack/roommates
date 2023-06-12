package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.ExpenseAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.SpeseComuniAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Expense;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseStorico#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseStorico extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SpeseStorico() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeseStorico.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseStorico newInstance(String param1, String param2) {
        SpeseStorico fragment = new SpeseStorico();
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
        View view = inflater.inflate(R.layout.fragment_spese_storico, container, false);
        ExpenseAdapter adapter = new ExpenseAdapter(getContext(), new ArrayList<>());

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.storico_spese));

        ListView lista = view.findViewById(R.id.listaStoricoSpese);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        db.collection("listaspesaeffettuata").whereEqualTo("houseId", houseViewModel.getHouseId()).get().addOnCompleteListener( task -> {
            if (!task.isSuccessful()) {
                return;
            }
            List<Expense> spese = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                Expense spesa = new Expense(
                        (String) documentSnapshot.get("name"),
                        (Double) documentSnapshot.get("amount"),
                        (String) documentSnapshot.get("payer"),
                        documentSnapshot.getTimestamp("date").toDate(),
                        (ArrayList<DocumentReference>) documentSnapshot.get("usersPaying"));
                Log.d("boh", spesa.getPaganti().toString());
                db.collection("utenti").document(documentSnapshot.getString("payer")).get().addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        return;
                    }
                    String nome = task1.getResult().getString("name");
                    for(DocumentReference d : (ArrayList<DocumentReference>) documentSnapshot.get("usersPaying")) {
                        d.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String fieldValue = documentSnapshot.getString("name");
                                    spesa.addUserName(fieldValue);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    spesa.setUserNamePayer(nome);
                    adapter.notifyDataSetChanged();
                });

                return spesa;
            }).collect(Collectors.toList());
            adapter.setItems(spese);

            lista.setAdapter(adapter);
            if (getContext() == null) {
                return;
            }
        });

        return view;
    }
}