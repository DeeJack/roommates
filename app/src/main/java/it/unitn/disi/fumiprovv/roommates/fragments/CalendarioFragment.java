package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.EventAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Event;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarioFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Calendario.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarioFragment newInstance(String param1, String param2) {
        CalendarioFragment fragment = new CalendarioFragment();
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
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        Button newEventButton = view.findViewById(R.id.button_aggiungi_evento);
        //TextView selectedDateTextView = view.findViewById(R.id.selected_date_text_view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Initialize and set up the adapter
        /*ItemAdapter itemAdapter = new ItemAdapter();
        recyclerView.setAdapter(itemAdapter);*/

        EventAdapter eventAdapter = new EventAdapter();
        recyclerView.setAdapter(eventAdapter);

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        //itemAdapter.setData(getDataFromDatabase());

        /*calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Update the selected date in the TextView

                List<Event> filteredEvents = new ArrayList<>();
                filteredEvents.clear();
                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                Calendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
                c.clear();
                c.set(year,month,dayOfMonth, 0,0,0);
                Long t = c.getTimeInMillis();
                Log.d("data", t.toString());
                //selectedDateTextView.setText(selectedDate);
                DocumentReference casa = db.collection("case").document("OKBVOT");
                db.collection("eventi").whereEqualTo("casa", casa).whereEqualTo("data", t).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //filteredEvents.add(new Event(document.get("casa"), document.get("nome"), document.get("data"));
                                        Map<String, Object> data = document.getData();

                                        DocumentReference casa = (DocumentReference) data.get("casa");
                                        Long dataValue = (Long) data.get("data");
                                        String nome = (String) data.get("nome");
                                        Log.d("GHJGJJ", casa + " " + dataValue + " " + nome);
                                        Event temp = new Event(casa, nome, dataValue);*/
                                        /*DocumentReference casa = (DocumentReference) data.get("casa");
                                        Long giorno = (Long) data.get("giorno");
                                        Long mese = (Long) data.get("mese");
                                        Long anno = (Long) data.get("anno");
                                        String nome = (String) data.get("nome");
                                        Event temp = new Event(casa, nome, giorno, mese, anno);
                                        filteredEvents.add(temp);

                                        Log.d("prendiEventi", document.getId() + " => " + document.getData());
                                        eventAdapter.setData(filteredEvents);
                                        //itemAdapter.setData(filteredEvents);
                                    }
                                } else {
                                    Log.d("prendiEventi", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                Log.d("lista", filteredEvents.toString());

            }
        });*/
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(calendarView.getDate()));
        List<Event> filteredEvents = new ArrayList<>();
        DocumentReference casa = db.collection("case").document(houseViewModel.getHouseId());
        updateCalendar(casa, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR), eventAdapter, filteredEvents);


        calendarView.setOnDateChangeListener((view12, year, month, dayOfMonth) -> {
            filteredEvents.clear();
            Log.d("house id", casa.toString());

            updateCalendar(casa, dayOfMonth, month, year, eventAdapter, filteredEvents);

            Log.d("lista", filteredEvents.toString());
            if (filteredEvents.isEmpty()) {
                // Clear the adapter's data
                eventAdapter.setData(Collections.emptyList());
            }
        });

        newEventButton.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_calendarioFragment_to_nuovoEvento));

        /*db.collection("eventi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();

                                DocumentReference casa = (DocumentReference) data.get("casa");
                                Long dataValue = (Long) data.get("data");
                                String nome = (String) data.get("nome");
                                Log.d("GHJGJJ", casa + " " + dataValue + " " + nome);
                                Event temp = new Event(casa, nome, dataValue);

                                Log.d("boh", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d( "boh","Error getting documents: ", task.getException());
                        }
                    }
                });*/

        return view;
    }

    private void updateCalendar(DocumentReference casa, int dayOfMonth, int month, int year,
                                EventAdapter eventAdapter, List<Event> filteredEvents) {
        db.collection("eventi")
                .whereEqualTo("casa", casa)
                .whereEqualTo("giorno", dayOfMonth)
                .whereEqualTo("mese", month + 1)
                .whereEqualTo("anno", year)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Map<String, Object> data = document.getData();

                            DocumentReference casa1 = (DocumentReference) data.get("casa");
                            Long giorno = (Long) data.get("giorno");
                            Long mese = (Long) data.get("mese");
                            Long anno = (Long) data.get("anno");
                            String nome = (String) data.get("nome");
                            Event temp = new Event(casa1, nome, giorno, mese, anno);

                            filteredEvents.add(temp);
                            Log.d("prova", temp.toString());
                        }
                        eventAdapter.setData(filteredEvents);
                    } else {
                        Log.d("error", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateData(int year, int month, int dayOfMonth) {
    }
}