package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.ContactListAdapter;
import it.unitn.disi.fumiprovv.roommates.models.Contact;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ProgressBar progressBar = view.findViewById(R.id.contactProgressbar);
        progressBar.setVisibility(View.VISIBLE);

        ListView contactListView = view.findViewById(R.id.contactsListView);
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        ImageButton addContactButton = view.findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(v -> {
            NavigationUtils.navigateTo(R.id.action_contactFragment_to_newContactFragment, view);
        });

        db.collection("contatti").whereEqualTo("houseId", db.collection("case").document(houseViewModel.getHouseId())).get()
                .addOnCompleteListener(task -> {
                    ContactListAdapter adapter = new ContactListAdapter(getContext(), new ArrayList<>());
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    //List<Map<String, Object>> notes = task.getResult().getDocuments().stream()
                    //        .map(DocumentSnapshot::getData).collect(Collectors.toList());
                    List<Contact> contacts = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        //Note note = documentSnapshot.getString("userId");
                        Contact contact = new Contact(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("name"),
                                documentSnapshot.getString("number")
                        );
//                        documentSnapshot.getDocumentReference("userId").get().addOnCompleteListener(task1 -> {
//                            if (!task1.isSuccessful()) {
//                                return;
//                            }
//                            contact.setUserName((String) task1.getResult().get("name"));
//                            adapter.notifyDataSetChanged();
//                        });
                        return contact;
                    }).collect(Collectors.toList());
                    //String[] items = notes.stream().map(note -> (String) note.get("text")).toArray(String[]::new);
                    adapter.setContacts(contacts);

                    contactListView.setAdapter(adapter);
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    contactListView.setDividerHeight(dividerHeight);

                    // Imposta il listener di click sugli elementi della lista
                    contactListView.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view1, position, id) -> {
                        Contact selectedItem = contacts.get(position);
                        String a = "";
                    });
                    progressBar.setVisibility(View.GONE);
                });

        return view;
    }
}