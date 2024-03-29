package it.unitn.disi.fumiprovv.roommates.fragments.surveys;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listViewOptions;
    private View view;

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
        setupMenu();
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.note_new_bar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menuAddButton) {
                    createSurvey();
                }
                return true;
            }
        }, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_nuovo_sondaggio, container, false);

        EditText editTextOption = view.findViewById(R.id.nuovaOpzioneText);
        Button buttonAddOption = view.findViewById(R.id.buttonAddOption);
        listViewOptions = view.findViewById(R.id.listViewOptions);
        ArrayList<String> surveyOptions = new ArrayList<String>();
        CheckBox c = view.findViewById(R.id.sceltaMultipla);
        Log.d("sm", c.toString());

        OptionsSurveyAdapter optionsAdapter = new OptionsSurveyAdapter(view.getContext(), surveyOptions);

        listViewOptions.setAdapter(optionsAdapter);

        buttonAddOption.setOnClickListener(v -> {
            String option = editTextOption.getText().toString().trim();
            if (!option.isEmpty()) {
                surveyOptions.add(option);
                optionsAdapter.notifyDataSetChanged();
                editTextOption.setText("");
            }
        });

        return view;
    }

    private void createSurvey() {
        EditText question = view.findViewById(R.id.nuovaDomanda);
        CheckBox multipleChoice = view.findViewById(R.id.sceltaMultipla);
        String domanda = question.getText().toString().trim();
        List<String> options = getOptionsFromListView();
        ArrayList<Long> voti = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            voti.add(0L);
        }
        long currentTimeMillis = System.currentTimeMillis();
        boolean sm = multipleChoice.isChecked();
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String casa = houseViewModel.getHouseId();

        if (options.size() == 0) {
            Toast.makeText(getContext(), getString(R.string.no_options), Toast.LENGTH_SHORT).show();
            return;
        }
        if (domanda.trim().length() == 0) {
            Toast.makeText(getContext(), getString(R.string.no_text_survey), Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("case").document(casa).get().addOnCompleteListener(task -> {
            ArrayList<Object> prova = (ArrayList<Object>) task.getResult().get("roommates");
            long inquilini = prova.size();
            Sondaggio survey = new Sondaggio(
                    "id",
                    domanda,
                    new ArrayList<>(options),
                    voti,
                    currentTimeMillis,
                    sm,
                    mAuth.getUid(),
                    casa,
                    new ArrayList<>(),
                    inquilini,
                    0L);

            db.collection("sondaggi")
                    .add(survey)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("nuovoSondaggio", "DocumentSnapshot written with ID: " + documentReference.getId());
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("erroreNuovoSondaggio", "Error adding document", e);
                        requireActivity().onBackPressed();
                    });
        });
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