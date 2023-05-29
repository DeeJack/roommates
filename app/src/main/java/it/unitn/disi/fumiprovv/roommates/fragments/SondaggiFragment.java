package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;
import it.unitn.disi.fumiprovv.roommates.adapters.SurveyAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SondaggiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SondaggiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    SurveyAdapter surveyAdapter;

    public SondaggiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SondaggiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SondaggiFragment newInstance(String param1, String param2) {
        SondaggiFragment fragment = new SondaggiFragment();
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
        View view =  inflater.inflate(R.layout.fragment_sondaggi, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.surveysRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        surveyAdapter = new SurveyAdapter();
        recyclerView.setAdapter(surveyAdapter);

        Button b = (Button) view.findViewById(R.id.button_new_sondaggio);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_sondaggiFragment_to_nuovo_sondaggio);
            }
        });

        loadSurveys();

        return view;
    }

    private void loadSurveys() {
        // Query Firestore collection for surveys
        db.collection("sondaggi")
                .orderBy("tempoCreazione", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        List<Sondaggio> surveyList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            //Log.d("boh", document.toString());
                            // Retrieve survey data and create Survey object
                            String question = document.getString("domanda");
                            ArrayList<String> options = (ArrayList<String>) document.get("opzioni");
                            ArrayList<Long> votes = (ArrayList<Long>) document.get("voti");
                            Long t = (Long) document.get("tempoCreazione");
                            boolean scelta = (boolean) document.get("sceltaMultipla");
                            String userId = mAuth.getUid();
                            //prendere id dell'utente attuale

                            if (question != null && options != null) {
                                DocumentReference casa = db.collection("case").document("OKBVOT");
                                Sondaggio survey = new Sondaggio(question, options, votes, t, scelta, db.collection("utenti").document(userId), casa);
                                surveyList.add(survey);
                            }
                        }
                        // Set survey data in adapter
                        surveyAdapter.setData(surveyList);
                    } else {
                        Log.d("SurveyFragment", "Error getting surveys: ", task.getException());
                    }
                });
    }
}