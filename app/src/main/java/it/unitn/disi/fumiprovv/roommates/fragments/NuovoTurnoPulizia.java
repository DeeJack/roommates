package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import it.unitn.disi.fumiprovv.roommates.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuovoTurnoPulizia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuovoTurnoPulizia extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NuovoTurnoPulizia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuovoTurnoPulizia.
     */
    // TODO: Rename and change types and number of parameters
    public static NuovoTurnoPulizia newInstance(String param1, String param2) {
        NuovoTurnoPulizia fragment = new NuovoTurnoPulizia();
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
        View view = inflater.inflate(R.layout.fragment_nuovo_turno_pulizia, container, false);

        Spinner userSpinner = view.findViewById(R.id.userSpinner);
        Button buttonAggiungi = view.findViewById(R.id.buttonNewUser);

        ArrayList<String> userNames = new ArrayList<>();
        userNames.add("uno");
        userNames.add("due");
        userNames.add("tre");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);

        ListView userListView = view.findViewById(R.id.selectedUsersList);

        ArrayList<String> selectedUsers = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selectedUsers);
        userListView.setAdapter(adapter);

        buttonAggiungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userNames.size()>0) {
                    String selectedUser = (String) userSpinner.getSelectedItem().toString();
                    Log.d("user", selectedUser);
                    selectedUsers.add(selectedUser);
                    userNames.remove(selectedUser);
                    if(userNames.size()>0)
                        userSpinner.setPrompt(userNames.get(0));
                    adapter.notifyDataSetChanged();
                    spinnerAdapter.notifyDataSetChanged();
                }

            }
        });

        /*userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedUser = (String) adapterView.getItemAtPosition(position);
                Log.d("user", selectedUser);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle the case where no user is selected
            }
        });*/

        return view;
    }
}