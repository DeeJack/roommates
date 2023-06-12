package it.unitn.disi.fumiprovv.roommates.fragments.shoppinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.CheckboxListAdapter;
import it.unitn.disi.fumiprovv.roommates.models.ShoppingItem;
import it.unitn.disi.fumiprovv.roommates.models.User;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyItemsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private View view;
    private CheckboxListAdapter adapter;
    private ArrayList<ShoppingItem> items;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BuyItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuyItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuyItemsFragment newInstance(String param1, String param2) {
        BuyItemsFragment fragment = new BuyItemsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_buy_items, container, false);
        Bundle bundle = getArguments();
        if (bundle.getSerializable("shoppingItems") == null) {
            requireActivity().onBackPressed();
            return view;
        }

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        this.adapter = new CheckboxListAdapter(getContext(), new ArrayList<>());
        ListView usersListView = view.findViewById(R.id.listViewRoommates);

        ProgressBar progressBar = view.findViewById(R.id.buyItemsProgressbar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("utenti").whereEqualTo("casa", houseViewModel.getHouseId()).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    List<User> users = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        //Note note = documentSnapshot.getString("userId");
                        return new User(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("name")
                        );
                    }).collect(Collectors.toList());
                    //String[] items = notes.stream().map(note -> (String) note.get("text")).toArray(String[]::new);
                    adapter.setItems(users);

                    usersListView.setAdapter(adapter);
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    usersListView.setDividerHeight(dividerHeight);

                    // Imposta il listener di click sugli elementi della lista
                    //usersListView.setOnItemClickListener((parent, view1, position, id) -> {
                    //    User selectedItem = users.get(position);
                    //});
                    progressBar.setVisibility(View.GONE);
                });
        this.items = (ArrayList<ShoppingItem>) bundle.getSerializable("shoppingItems");
        String[] itemsString = this.items.stream().map(ShoppingItem::getName).toArray(String[]::new);
        ListView shoppingItemsListView = view.findViewById(R.id.listViewProducts);
        ArrayAdapter<String> shoppingItemsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemsString);
        shoppingItemsListView.setAdapter(shoppingItemsAdapter);

        return view;
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
                    buyItems();
                }
                return true;
            }
        }, this);
    }

    private void buyItems() {
        // It should never happen, but just in case
        if (view == null || adapter == null)
            return;
        String name = ((EditText) view.findViewById(R.id.buyNameField)).getText().toString();
        double amount;
        try {
            amount = Double.parseDouble(((EditText) view.findViewById(R.id.buyAmountField)).getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Inserisci un importo valido", Toast.LENGTH_SHORT).show();
            return;
        }
        Set<User> usersPaying = adapter.getCheckedItems();

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        Map<String, Object> expense = new HashMap<>();
        expense.put("name", name);
        expense.put("amount", amount);
        expense.put("payer", mAuth.getCurrentUser().getUid());
        expense.put("date", Timestamp.now());
        expense.put("houseId", houseViewModel.getHouseId());
        expense.put("shoppingItems", items.stream().map(ShoppingItem::getName).collect(Collectors.toList()));

        ProgressBar progressBar = view.findViewById(R.id.buyItemsProgressbar);
        progressBar.setVisibility(View.VISIBLE);

        List<DocumentReference> usersPayingReferences = new ArrayList<>();
        usersPaying.forEach(user ->
                usersPayingReferences.add(db.collection("utenti").document(user.getId())));
        expense.put("usersPaying", usersPayingReferences);

        WriteBatch batch = db.batch();

        batch.set(db.collection("listaspesaeffettuata").document(), expense);
        // Delete all shopping items
        for (ShoppingItem item : items) {
            batch.delete(db.collection("listaspesa").document(item.getId()));
        }

        //hehehe qua dobbiamo settare i debiti
        //ho voglia di marmellata
        //vado a mangiare la marmellata

        Double amountPerUser = amount/usersPaying.size();

        List<DocumentReference> documentReferences = new ArrayList<>();

        for(User u : usersPaying) {
            if(!u.getId().equals(mAuth.getCurrentUser().getUid())) {
                Query query1 = db.collection("debiti")
                        .whereEqualTo("idFrom", u.getId())
                        .whereEqualTo("idTo", mAuth.getCurrentUser().getUid());
                Query query2 = db.collection("debiti")
                        .whereEqualTo("idFrom", mAuth.getCurrentUser().getUid())
                        .whereEqualTo("idTo", u.getId());

                query1.get().addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        double currentAmount = documentSnapshot.getDouble("amount");
                        double newAmount = currentAmount + amountPerUser;

                        DocumentReference debtRef = db.collection("debiti").document(documentSnapshot.getId());
                        debtRef.update("amount", newAmount);

                        documentReferences.add(debtRef);
                    }
                }).addOnFailureListener(e -> {
                    // Handle query failure
                });

                query2.get().addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        double currentAmount = documentSnapshot.getDouble("amount");
                        double newAmount = currentAmount - amountPerUser;

                        DocumentReference debtRef = db.collection("debiti").document(documentSnapshot.getId());
                        debtRef.update("amount", newAmount);

                        documentReferences.add(debtRef);
                    }
                }).addOnFailureListener(e -> {
                    // Handle query failure
                });
            }
        }

        /*try {
            Tasks.await(Tasks.whenAll((Task<?>) documentReferences));

            // All updates completed successfully

        } catch (Exception e) {
            // Handle errors in updates
        }*/

        batch.commit().addOnCompleteListener(commitTask -> {
            progressBar.setVisibility(View.GONE);
            if (!commitTask.isSuccessful())
                return;
            requireActivity().onBackPressed();
        });
    }
}