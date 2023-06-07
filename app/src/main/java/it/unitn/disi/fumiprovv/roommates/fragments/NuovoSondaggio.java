package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.OptionsSurveyAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuovoSondaggio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuovoSondaggio extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listViewOptions;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public NuovoSondaggio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuovoSondaggio.
     */
    // TODO: Rename and change types and number of parameters
    public static NuovoSondaggio newInstance(String param1, String param2) {
        NuovoSondaggio fragment = new NuovoSondaggio();
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
        View view = inflater.inflate(R.layout.fragment_nuovo_sondaggio, container, false);

        EditText editTextOption = view.findViewById(R.id.nuovaOpzioneText);
        EditText question = view.findViewById(R.id.nuovaDomanda);
        Button buttonAddOption = view.findViewById(R.id.buttonAddOption);
        listViewOptions = view.findViewById(R.id.listViewOptions);
        ArrayList<String> surveyOptions = new ArrayList<String>();
        Button buttonNuovoSondaggio = view.findViewById(R.id.buttonCreaSondaggio);
        CheckBox c = view.findViewById(R.id.sceltaMultipla);
        Log.d("sm", c.toString());

        OptionsSurveyAdapter optionsAdapter= new OptionsSurveyAdapter(view.getContext(), surveyOptions);

        listViewOptions.setAdapter(optionsAdapter);

        buttonAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String option = editTextOption.getText().toString().trim();
                if (!option.isEmpty()) {
                    surveyOptions.add(option);
                    optionsAdapter.notifyDataSetChanged();
                    editTextOption.setText("");
                }
            }
        });

        buttonNuovoSondaggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = question.getText().toString().trim();
                List<String> options = getOptionsFromListView();
                ArrayList<Long> voti = new ArrayList<Long>();
                for(int i=0;i<options.size();i++) {
                    voti.add(new Long(0));
                }
                String userId = mAuth.getUid();
                long currentTimeMillis = System.currentTimeMillis();
                //CheckBox checkBox = view.findViewById(R.id.sceltaMultipla);
                //Log.d("ciao", checkBox.toString());
                Boolean sm;
                if (c.isChecked()) {
                    sm = Boolean.TRUE;
                } else {
                    sm = Boolean.FALSE;
                }
                HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
                String casa = houseViewModel.getHouseId();
                db.collection("case").document(casa).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        ArrayList<Object> prova = (ArrayList<Object>) task.getResult().get("roommates");
                        Long inquilini = new Long(prova.size());
                        Sondaggio survey = new Sondaggio(
                                "id",
                                q,
                                new ArrayList<String>(options),
                                voti,
                                new Long(currentTimeMillis),
                                sm,
                                mAuth.getUid(),
                                //db.collection("utenti").document(userId),
                                casa,
                                new ArrayList<String>(),
                                inquilini,
                                new Long(0));

                        db.collection("sondaggi")
                                .add(survey)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("nuovoSondaggio", "DocumentSnapshot written with ID: " + documentReference.getId());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("erroreNuovoSondaggio", "Error adding document", e);
                                    }
                                });
                    }
                });
                /*Sondaggio survey = new Sondaggio(
                        "id",
                        q,
                        new ArrayList<String>(options),
                        voti,
                        new Long(currentTimeMillis),
                        sm,
                        mAuth.getUid(),
                        //db.collection("utenti").document(userId),
                        casa,
                        new ArrayList<String>(),
                        new Long(2), //da modificare con numero di inquilini nella casa
                        new Long(0));

                db.collection("sondaggi")
                        .add(survey)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("nuovoSondaggio", "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("erroreNuovoSondaggio", "Error adding document", e);
                            }
                        });*/
            }
        });

        return view;
    }

    private List<String> getOptionsFromListView() {
        ArrayList<String> options = new ArrayList<>();
        for (int i = 0; i < listViewOptions.getChildCount(); i++) {
            View itemView = listViewOptions.getChildAt(i);
            TextView editTextOption = itemView.findViewById(R.id.opzioneText);
            String option = editTextOption.getText().toString().trim();
            if (!option.isEmpty()) {
                options.add(option);
            }
        }
        return options;
    }
}