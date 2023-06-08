package it.unitn.disi.fumiprovv.roommates.fragments;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.User;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewTurnoProva#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTurnoProva extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> selectedUsers = new ArrayList<>();
    ArrayList<String> userIds = new ArrayList<>();

    public NewTurnoProva() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTurnoProva.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTurnoProva newInstance(String param1, String param2) {
        NewTurnoProva fragment = new NewTurnoProva();
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
                        addTurnoToDb();
                        break;
                    default:
                        break;
                }
                return true;
            }
        }, this);
    }

    private void addTurnoToDb() {
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Long currentWeek = new Long(calendar.get(Calendar.WEEK_OF_YEAR));
        Long currentYear = new Long(calendar.get(Calendar.YEAR));
        Map<String, Object> newTurno = new HashMap<>();
        newTurno.put("house", houseViewModel.getHouseId());
        newTurno.put("name", ((EditText) getView().findViewById(R.id.turnoNameField)).getText().toString());
        newTurno.put("users", selectedUsers);
        newTurno.put("weekStart", currentWeek);
        newTurno.put("yearStart", currentYear);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        newTurno.put("weekLast", calendar.get(Calendar.WEEK_OF_YEAR));
        newTurno.put("yearLast", calendar.get(Calendar.YEAR));

        db.collection("turniPulizia").add(newTurno)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requireActivity().onBackPressed();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_turno_prova, container, false);

        Spinner spinner = view.findViewById(R.id.userSpinner);
        ImageView buttonNewUser = view.findViewById(R.id.addUserToTurnoButton);

        ArrayList<String> userNames = new ArrayList<>();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userIds);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ListView userListView = view.findViewById(R.id.usersTurniListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selectedUsers);
        userListView.setAdapter(adapter);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        db.collection("utenti").whereEqualTo("casa", houseViewModel.getHouseId()).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    List<String> users = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        //Note note = documentSnapshot.getString("userId");
                        User user = new User(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("name")
                        );
                        return user.getName();
                    }).collect(Collectors.toList());
                    //String[] items = notes.stream().map(note -> (String) note.get("text")).toArray(String[]::new);
                    spinnerAdapter.addAll(users);
                    //adapter.setItems(users);

                    userListView.setAdapter(adapter);
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    userListView.setDividerHeight(dividerHeight);

                    // Imposta il listener di click sugli elementi della lista
                    userListView.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view1, position, id) -> {
                        String selectedItem = users.get(position);
                        String a = "";
                    });
                });

        buttonNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userIds.size()>0) {
                    String selectedUser = (String) spinner.getSelectedItem().toString();
                    Log.d("user", selectedUser);
                    selectedUsers.add(selectedUser);
                    userIds.remove(selectedUser);
                    if(userNames.size()>0)
                        spinner.setPrompt(userIds.get(0));
                    adapter.notifyDataSetChanged();
                    spinnerAdapter.notifyDataSetChanged();
                }

            }
        });

        return view;
    }
}