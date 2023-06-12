package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.SpeseComuniAdapter;
import it.unitn.disi.fumiprovv.roommates.models.SpesaComune;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setTitle(getString(R.string.spese_comuni));
        SpeseComuniAdapter adapter = new SpeseComuniAdapter(getContext(), new ArrayList<>());

        ListView lista = view.findViewById(R.id.provaList);

        Button buttonNuovaSpesaComune = view.findViewById(R.id.buttonNuovaSpesaComune);
        buttonNuovaSpesaComune.setVisibility(View.GONE);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        db.collection("case").document(houseViewModel.getHouseId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> roommates = (List<Map<String, Object>>) document.get("roommates");
                    if (roommates != null) {
                        for (Map<String, Object> roommate : roommates) {
                            String userId = (String) roommate.get("userId");
                            boolean isModerator = (boolean) roommate.get("moderator");
                            if (Objects.equals(userId, mAuth.getUid()) && isModerator) {
                                buttonNuovaSpesaComune.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    // Document does not exist
                }
            } else {
                // Handle task failure
            }
        });

        db.collection("speseComuni").whereEqualTo(getResources().getString(R.string.houseId), houseViewModel.getHouseId()).get().addOnCompleteListener( task -> {
            if (!task.isSuccessful()) {
                return;
            }
            if (getContext() == null)
                return;
            List<SpesaComune> spese = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                SpesaComune spesa = new SpesaComune(
                        (String) documentSnapshot.getId(),
                        (String) documentSnapshot.get(getResources().getString(R.string.houseId)),
                        (String) documentSnapshot.get(getResources().getString(R.string.name)),
                        (Double) documentSnapshot.get(getResources().getString(R.string.amount)),
                        (String) documentSnapshot.get(getResources().getString(R.string.payer)),
                        (String) documentSnapshot.get(getResources().getString(R.string.repeating)));
                if(documentSnapshot.getString(getResources().getString(R.string.repeating)).equals(getResources().getString(R.string.mensile))) {
                    spesa.setLastMonth(documentSnapshot.getLong("lastMonth"));
                    spesa.setLastYear(documentSnapshot.getLong("lastYear"));
                } else if(documentSnapshot.getString(getResources().getString(R.string.repeating)).equals(getResources().getString(R.string.settimanale))) {
                    spesa.setLastWeek(documentSnapshot.getLong("lastWeek"));
                    spesa.setLastYear(documentSnapshot.getLong("lastYear"));
                }
                db.collection("utenti").document(documentSnapshot.getString(getString(R.string.payer))).get().addOnCompleteListener(task1 -> {
                    String nome = task1.getResult().getString("name");
                    spesa.setUserName(nome);
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



        buttonNuovaSpesaComune.setOnClickListener(view1 -> {
            NavigationUtils.navigateTo(R.id.action_to_nuova_spesa_comune, view);
        });


        return view;
    }

}