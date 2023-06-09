package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SituazioniAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.StoricoPagamentiAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Debt;
import it.unitn.disi.fumiprovv.roommates.models.Pagamento;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseSituazioneProva#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseSituazioneProva extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SpeseSituazioneProva() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeseSituazioneProva.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseSituazioneProva newInstance(String param1, String param2) {
        SpeseSituazioneProva fragment = new SpeseSituazioneProva();
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
        View view = inflater.inflate(R.layout.fragment_spese_situazione_prova, container, false);

        ListView listSituazioni = view.findViewById(R.id.situazioniListView);
        ListView listStorico = view.findViewById(R.id.situazioniStoricoListView);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String currentUser = mAuth.getUid();

        db.collection("debiti").whereEqualTo("house", houseViewModel.getHouseId())
                .whereEqualTo("idFrom", currentUser)
                .get()
                .addOnCompleteListener(task -> {
                    SituazioniAdapter adapterSituazioni = new SituazioniAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        return;
                    }
                    List<Debt> debiti = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        Debt debito = new Debt(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("house"),
                                documentSnapshot.getString("idFrom"),
                                documentSnapshot.getString("idTo"),
                                documentSnapshot.getDouble("amount"),
                                documentSnapshot.getString("inverse")
                        );
                        Log.d("debito", debito.toString());
                        String idFrom = documentSnapshot.getString("idFrom");
                        String idTo = documentSnapshot.getString("idTo");

                        db.collection("utenti").document(idFrom).get().addOnCompleteListener(task1 -> {
                                    if (!task1.isSuccessful()) {
                                        return;
                                    }
                                    String nome = task1.getResult().getString("name");
                                    debito.setUserNameFrom(nome);
                                    adapterSituazioni.notifyDataSetChanged();
                        });
                        db.collection("utenti").document(idTo).get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                return;
                            }
                            String nome = task1.getResult().getString("name");
                            debito.setUserNameTo(nome);
                            adapterSituazioni.notifyDataSetChanged();
                        });
                        return debito;
                    }).collect(Collectors.toList());
                    Log.d("boh", debiti.toString());
                    adapterSituazioni.setDebts(debiti);
                    //adapterSituazioni.notifyDataSetChanged();

                    listSituazioni.setAdapter(adapterSituazioni);
                    if (getContext() == null)
                        return;
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    listSituazioni.setDividerHeight(dividerHeight);
                });

        db.collection("storicoPagamentiUtenti").whereEqualTo("house", houseViewModel.getHouseId())
                .get()
                .addOnCompleteListener(task -> {
                    StoricoPagamentiAdapter adapterStorico = new StoricoPagamentiAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        return;
                    }
                    List<Pagamento> pagamenti = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        Pagamento pagamento = new Pagamento(
                                documentSnapshot.getString("house"),
                                documentSnapshot.getString("from"),
                                documentSnapshot.getString("to"),
                                documentSnapshot.getDouble("amount"),
                                documentSnapshot.getTimestamp("date")
                        );
                        String idFrom = documentSnapshot.getString("from");
                        String idTo = documentSnapshot.getString("to");

                        db.collection("utenti").document(idFrom).get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                return;
                            }
                            String nome = task1.getResult().getString("name");
                            pagamento.setUserNameFrom(nome);
                            adapterStorico.notifyDataSetChanged();
                        });
                        db.collection("utenti").document(idTo).get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                return;
                            }
                            String nome = task1.getResult().getString("name");
                            pagamento.setUserNameTo(nome);
                            adapterStorico.notifyDataSetChanged();
                        });
                        return pagamento;
                    }).collect(Collectors.toList());
                    adapterStorico.setItems(pagamenti);

                    listStorico.setAdapter(adapterStorico);
                    if (getContext() == null)
                        return;
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    listStorico.setDividerHeight(dividerHeight);
                });

        return view;
    }
}