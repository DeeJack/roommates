package it.unitn.disi.fumiprovv.roommates.fragments.surveys;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SondaggiAdapter;
import it.unitn.disi.fumiprovv.roommates.adapters.SurveyAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

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
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sondaggi, container, false);

        ListView sondaggiListView = view.findViewById(R.id.sondaggiListView);
        TextView noSurveysTextView = view.findViewById(R.id.noSurveysTextView);
        sondaggiListView.setEmptyView(noSurveysTextView);
        ProgressBar surveyProgressbar = view.findViewById(R.id.surveyProgressbar);

        Button newSurveyButton = view.findViewById(R.id.button_new_sondaggio);
        newSurveyButton.setOnClickListener(view1 -> NavigationUtils.navigateTo(R.id.action_sondaggiFragment_to_nuovo_sondaggio, view));

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String s = houseViewModel.getHouseId();
        Log.d("houseId", s);

        db.collection("sondaggi")
                .whereEqualTo("casa", houseViewModel.getHouseId())
                .orderBy("tempoCreazione", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    SondaggiAdapter adapter = new SondaggiAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        surveyProgressbar.setVisibility(View.GONE);
                        return;
                    }
                    List<Sondaggio> sondaggi = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        Sondaggio sondaggio = new Sondaggio(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("domanda"),
                                (ArrayList<String>) documentSnapshot.get("opzioni"),
                                (ArrayList<Long>) documentSnapshot.get("voti"),
                                documentSnapshot.getLong("tempoCreazione"),
                                documentSnapshot.getBoolean("sceltaMultipla"),
                                documentSnapshot.getString("creatore"),
                                documentSnapshot.getString("casa"),
                                (ArrayList<String>) documentSnapshot.get("votanti"),
                                documentSnapshot.getLong("maxVotanti"),
                                documentSnapshot.getLong("votiTotali")
                        );
                        /*documentSnapshot.getDocumentReference("userId").get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                return;
                            }
                            adapter.notifyDataSetChanged();
                        });*/
                        return sondaggio;
                    }).collect(Collectors.toList());
                    adapter.setSondaggi(sondaggi);

                    sondaggiListView.setAdapter(adapter);
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    sondaggiListView.setDividerHeight(dividerHeight);
                    surveyProgressbar.setVisibility(View.GONE);
                });

        return view;
    }

    /*private void loadSurveys() {
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
    }*/
}