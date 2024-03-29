package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SituazioniAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.StoricoPagamentiUtentiAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Debt;
import it.unitn.disi.fumiprovv.roommates.models.PagamentoUtente;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseSituazioneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseSituazioneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SpeseSituazioneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeseSituazioneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseSituazioneFragment newInstance(String param1, String param2) {
        SpeseSituazioneFragment fragment = new SpeseSituazioneFragment();
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
        View view = inflater.inflate(R.layout.fragment_spese_situazione, container, false);
        ProgressBar progressBar = view.findViewById(R.id.situazioneProgressbar);
        progressBar.setVisibility(View.VISIBLE);

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.situazione_spese));

        ListView listSituazioni = view.findViewById(R.id.situazioniListView);
        ListView listStorico = view.findViewById(R.id.situazioniStoricoListView);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String currentUser = mAuth.getUid();
        AtomicInteger completedTasks = new AtomicInteger();

        Query query = db.collection("debiti")
                .where(Filter.or(
                        Filter.equalTo(getString(R.string.idFrom), currentUser),
                        Filter.equalTo(getString(R.string.idTo), currentUser)
                ));

        query.get().addOnCompleteListener(task -> {
            SituazioniAdapter adapterSituazioni = new SituazioniAdapter(getContext(), new ArrayList<>());
            if (!task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);
                return;
            }
            List<Debt> debiti = task.getResult().getDocuments().stream()
                    .filter(documentSnapshot ->
                            !(Objects.equals(documentSnapshot.getString("idFrom"), currentUser) &&
                                    Objects.equals(documentSnapshot.getString("idTo"), currentUser)))
                    .map(documentSnapshot -> {
                Debt debito = new Debt(
                        documentSnapshot.getId(),
                        documentSnapshot.getString("houseId"),
                        documentSnapshot.getString("idFrom"),
                        documentSnapshot.getString("idTo"),
                        documentSnapshot.getDouble("amount")
                );
                String idFrom = documentSnapshot.getString("idFrom");
                String idTo = documentSnapshot.getString("idTo");

                db.collection("utenti").document(idFrom).get().addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    String nome = task1.getResult().getString("name");
                    debito.setUserNameFrom(nome);
                    adapterSituazioni.notifyDataSetChanged();
                    completedTasks.set(completedTasks.get()+1);
                    if(completedTasks.get() == 6) {
                        adapterSituazioni.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                db.collection("utenti").document(idTo).get().addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    String nome = task1.getResult().getString("name");
                    debito.setUserNameTo(nome);
                    adapterSituazioni.notifyDataSetChanged();
                    //adapterSituazioni.notifyDataSetChanged();
                    completedTasks.set(completedTasks.get()+1);
                    if(completedTasks.get() == 6) {
                        adapterSituazioni.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                return debito;
            }).collect(Collectors.toList());
            adapterSituazioni.setDebts(debiti);
            adapterSituazioni.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

            listSituazioni.setAdapter(adapterSituazioni);
            if (getContext() == null)
                return;
            int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
            listSituazioni.setDividerHeight(dividerHeight);

            completedTasks.set(completedTasks.get()+1);
            if(completedTasks.get() == 6) {
                progressBar.setVisibility(View.GONE);
            }
        });

        db.collection("storicoPagamentiUtenti").whereEqualTo("house", houseViewModel.getHouseId())
                .get()
                .addOnCompleteListener(task -> {
                    StoricoPagamentiUtentiAdapter adapterStorico = new StoricoPagamentiUtentiAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    List<PagamentoUtente> pagamenti = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        PagamentoUtente pagamento = new PagamentoUtente(
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
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                            String nome = task1.getResult().getString("name");
                            pagamento.setUserNameFrom(nome);
                            adapterStorico.notifyDataSetChanged();
                            completedTasks.set(completedTasks.get()+1);
                            if(completedTasks.get() == 4) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        db.collection("utenti").document(idTo).get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                            String nome = task1.getResult().getString("name");
                            pagamento.setUserNameTo(nome);
                            adapterStorico.notifyDataSetChanged();
                            completedTasks.set(completedTasks.get()+1);
                            if(completedTasks.get() == 4) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        return pagamento;
                    }).collect(Collectors.toList());
                    adapterStorico.setItems(pagamenti);

                    listStorico.setAdapter(adapterStorico);
                    if (getContext() == null)
                        return;
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    listStorico.setDividerHeight(dividerHeight);
                    completedTasks.set(completedTasks.get()+1);
                    if(completedTasks.get() == 4) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        return view;
    }
}