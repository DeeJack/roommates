package it.unitn.disi.fumiprovv.roommates.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.StoricoPagamentiUtentiAdapter;
import it.unitn.disi.fumiprovv.roommates.models.PagamentoUtente;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuovaSpesaComune#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuovaSpesaComune extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String,String> userIds = new HashMap<>();

    public NuovaSpesaComune() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuovaSpesaComune.
     */
    // TODO: Rename and change types and number of parameters
    public static NuovaSpesaComune newInstance(String param1, String param2) {
        NuovaSpesaComune fragment = new NuovaSpesaComune();
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
                switch (menuItem.getItemId()) {
                    case R.id.menuAddButton:
                        addSpesaComuneToDb();
                        break;
                    default:
                        break;
                }
                return true;
            }
        }, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nuova_spesa_comune, container, false);

        Spinner ripetizioneSpinner = view.findViewById(R.id.ripetizioneSpinner);
        Spinner utentiSpinner = view.findViewById(R.id.utentiSpinner);

        List<String> itemsRipetizione = new ArrayList<>();
        itemsRipetizione.add(getResources().getString(R.string.mensile)); //hardcode
        itemsRipetizione.add(getResources().getString(R.string.settimanale));
        itemsRipetizione.add(getResources().getString(R.string.mai));

        ArrayAdapter<String> adapterRipetizione = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, itemsRipetizione);
        adapterRipetizione.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ripetizioneSpinner.setAdapter(adapterRipetizione);

        ArrayAdapter<String> adapterUtenti = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        adapterUtenti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        utentiSpinner.setAdapter(adapterUtenti);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        db.collection("utenti").whereEqualTo("casa", houseViewModel.getHouseId())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //progressBar.setVisibility(View.GONE);
                        return;
                    }
                    List<String> utenti = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        String utente = documentSnapshot.getString("name");
                        userIds.put(utente, documentSnapshot.getId());
                        return utente;
                    }).collect(Collectors.toList());
                    adapterUtenti.addAll(utenti);
                    adapterUtenti.notifyDataSetChanged();
                    utentiSpinner.setAdapter(adapterUtenti);
                });

        return view;
    }

    private void addSpesaComuneToDb(){
        String nome = ((EditText) getView().findViewById(R.id.nameSpesaComune)).getText().toString();
        String amountString = ((EditText) getView().findViewById(R.id.amountSpesaComune)).getText().toString();
        //if no input
        if(nome.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.inserire_dati), Toast.LENGTH_SHORT).show();
            return;
        }
        //get data
        Double amount = Double.parseDouble(amountString);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        String house = houseViewModel.getHouseId();
        Spinner spinner = (Spinner) getView().findViewById(R.id.ripetizioneSpinner);
        String ripetizione = spinner.getSelectedItem().toString().trim();
        spinner = (Spinner) getView().findViewById(R.id.utentiSpinner);
        String utente = spinner.getSelectedItem().toString().trim();
        String responsabile = userIds.get(utente);

        //create new spesa comune
        Map<String, Object> newSpesa = new HashMap<>();
        newSpesa.put(getResources().getString(R.string.name), nome);
        newSpesa.put(getResources().getString(R.string.houseId), house);
        newSpesa.put(getResources().getString(R.string.amount), amount);
        newSpesa.put(getResources().getString(R.string.repeating), ripetizione);
        newSpesa.put(getResources().getString(R.string.payer), responsabile);
        if(ripetizione.equals(getResources().getString(R.string.mensile))) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            newSpesa.put("lastMonth", month);
            newSpesa.put("lastYear", year);
        } else if (ripetizione.equals(getResources().getString(R.string.settimanale))) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            int year = calendar.get(Calendar.YEAR);
            newSpesa.put("lastWeek", week);
            newSpesa.put("lastYear", year);
        }
        db.collection("speseComuni").add(newSpesa)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requireActivity().onBackPressed();
                    }
                });
    }
}