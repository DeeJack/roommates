package it.unitn.disi.fumiprovv.roommates.fragments.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.NoteListAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Note;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
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
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        ListView notesListView = view.findViewById(R.id.notesListView);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        Button addNoteButton = view.findViewById(R.id.addNoteButton);
        addNoteButton.setOnClickListener(v ->
                NavigationUtils.navigateTo(R.id.action_noteFragment_to_newNoteFragment, view));

        db.collection("note").whereEqualTo("houseId", db.collection("case")
                        .document(houseViewModel.getHouseId()))
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    NoteListAdapter adapter = new NoteListAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        return;
                    }
                    //List<Map<String, Object>> notes = task.getResult().getDocuments().stream()
                    //        .map(DocumentSnapshot::getData).collect(Collectors.toList());
                    List<Note> notes = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        //Note note = documentSnapshot.getString("userId");
                        Note note = new Note(
                                documentSnapshot.getId(),
                                documentSnapshot.getDocumentReference("userId").getId(),
                                "",
                                documentSnapshot.getTimestamp("creationDate"),
                                (String) documentSnapshot.get("text")
                        );
                        documentSnapshot.getDocumentReference("userId").get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                return;
                            }
                            note.setUserName((String) task1.getResult().get("name"));
                            adapter.notifyDataSetChanged();
                        });
                        return note;
                    }).collect(Collectors.toList());
                    //String[] items = notes.stream().map(note -> (String) note.get("text")).toArray(String[]::new);
                    adapter.setNotes(notes);

                    notesListView.setAdapter(adapter);
                    if (getContext() == null)
                        return;
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    notesListView.setDividerHeight(dividerHeight);

                    // Imposta il listener di click sugli elementi della lista
                    //notesListView.setOnItemClickListener((parent, view1, position, id) -> {
                    //    Note selectedItem = notes.get(position);
                    //});
                });


        return view;
    }
}