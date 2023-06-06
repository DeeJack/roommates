package it.unitn.disi.fumiprovv.roommates.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SpeseComuniAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.StoricoSituazioniAdapter;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.models.StoricoSituazioni;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpeseSituazione#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeseSituazione extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SpeseSituazione() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeseSituazione.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeseSituazione newInstance(String param1, String param2) {
        SpeseSituazione fragment = new SpeseSituazione();
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
        View view = inflater.inflate(R.layout.fragment_spese_situazione, container, false);

        LinearLayout parentLayout = view.findViewById(R.id.situazioni);

        LinearLayout listaSituazioni = view.findViewById(R.id.situazioni);
        ListView listaStorico = view.findViewById(R.id.storicoSituazioni);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String houseId = houseViewModel.getHouseId();

        StoricoSituazioniAdapter adapter = new StoricoSituazioniAdapter(getContext(), new ArrayList<>());

        db.collection("storicoPagamentiUtenti").whereEqualTo("Casa", houseId).get().addOnCompleteListener( task -> {
            if (!task.isSuccessful()) {
                return;
            }
            List<StoricoSituazioni> storico = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                Number amount = (Number) documentSnapshot.get("Amount");
                Double amountValue;
                if (amount instanceof Long) {
                    amountValue = ((Long) amount).doubleValue();
                } else if (amount instanceof Double) {
                    amountValue = (Double) amount;
                } else {
                    throw new IllegalArgumentException("Invalid amount type");
                }
                StoricoSituazioni item = new StoricoSituazioni((String) documentSnapshot.get("Casa"), (String) documentSnapshot.get("From"), (String) documentSnapshot.get("To"), amountValue, (Timestamp) documentSnapshot.get("Date"));
                return item;
            }).collect(Collectors.toList());
            adapter.setItems((ArrayList<StoricoSituazioni>) storico);
            Log.d("prova", storico.toString());

            listaStorico.setAdapter(adapter);
            if (getContext() == null) {
                return;
            }
        });

        DocumentReference documentSnapshotTask = db.collection("case").document(houseId);
        documentSnapshotTask.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();

            List<Map<String, Object>> roommatesList = (List<Map<String, Object>>) document.getData().get("roommates");;
            for (Map<String, Object> userMap : roommatesList) {
                String userId = (String) userMap.get("userId");
                if (userId != null) {
                    DocumentReference boh = db.collection("utenti").document(userId);
                    boh.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String nome = document.getString("name");
                                    if(!userId.equals(mAuth.getUid())) {
                                        View userFrame = LayoutInflater.from(getContext()).inflate(R.layout.generic_item, null);

                                        TextView titolo = userFrame.findViewById(R.id.titoloGenericItem);
                                        TextView descrizione = userFrame.findViewById(R.id.descrizioneGenericItem);
                                        titolo.setText(nome);
                                        descrizione.setText("Devi tot€ a " + nome);

                                        Button actionButton = userFrame.findViewById(R.id.buttonGenericItem);
                                        actionButton.setVisibility(View.VISIBLE);
                                        actionButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // Handle button click here
                                                //performAction(user);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Pagamento");
                                                builder.setMessage("Vuoi pagare l'utente?");
                                                EditText inputEditText = new EditText(getContext());
                                                inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                builder.setView(inputEditText);
                                                builder.setPositiveButton("Ok", (dialog, which) -> {
                                                    String inputValue = inputEditText.getText().toString();
                                                    if (!TextUtils.isEmpty(inputValue)) {
                                                        double value = Double.parseDouble(inputValue);
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("From", mAuth.getUid());
                                                        data.put("To", document.getId());
                                                        data.put("Amount", new Double(inputValue));
                                                        db.collection("storicoPagamentiUtenti").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d("success", "DocumentSnapshot written with ID: " + documentReference.getId());
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w("failure", "Error adding document", e);
                                                                    }
                                                                });
                                                        Log.d("boh", Double.toString(value));
                                                    } else {
                                                        Log.d("boh", "errore");
                                                    }
                                                });
                                                builder.setNegativeButton("Cancella", (dialog, which) -> {
                                                    // Do nothing
                                                });
                                                builder.show();
                                            }
                                        });

                                        // Add userFrame to the parentLayout
                                        parentLayout.addView(userFrame);
                                    }

                                } else {
                                    Log.d("Error", "Document does not exist");
                                }
                            } else {
                                Log.d("Error", "Error getting document: " + task.getException());
                            }
                        }
                    });
                }
            }

        });

        return view;
    }

}