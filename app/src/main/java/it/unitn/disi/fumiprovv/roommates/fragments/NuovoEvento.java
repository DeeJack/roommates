package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Event;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuovoEvento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuovoEvento extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public NuovoEvento() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuovoEvento.
     */
    // TODO: Rename and change types and number of parameters
    public static NuovoEvento newInstance(String param1, String param2) {
        NuovoEvento fragment = new NuovoEvento();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nuovo_evento, container, false);
        Button submitButton = view.findViewById(R.id.submitButton);
        EditText nomeEvento = (EditText) view.findViewById(R.id.nomeEvento);
        DatePicker dataEvento = (DatePicker) view.findViewById(R.id.dataEvento);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String eventName = nomeEvento.getText().toString();
                int y = dataEvento.getYear();
                int m = dataEvento.getMonth();
                int d = dataEvento.getDayOfMonth();
                Calendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
                c.set(y,m,d);

                DocumentReference casa = db.collection("case").document("OKBVOT");

                // Create a data model object
                Event event = new Event(casa, eventName, new Long(d), new Long(m+1), new Long(y));

                //aggiungi evento al db
                db.collection("eventi")
                        .add(event)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("nuovoEvento", "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("nuovoEvento", "Error adding document", e);
                            }
                        });
            }
        });
        return view;
    }
}