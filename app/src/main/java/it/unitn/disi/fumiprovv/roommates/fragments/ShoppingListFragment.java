package it.unitn.disi.fumiprovv.roommates.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.adapters.ShoppingListAdapter;
import it.unitn.disi.fumiprovv.roommates.models.ShoppingItem;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingListFragment newInstance(String param1, String param2) {
        ShoppingListFragment fragment = new ShoppingListFragment();
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
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        ImageView addItemButton = view.findViewById(R.id.addItemShoppingButton);
        ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), new ArrayList<>());
        addItemButton.setOnClickListener(view1 -> addItem(view, adapter));
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);

        ListView notesListView = view.findViewById(R.id.shoppingListView);

        Button buyItemsButton = view.findViewById(R.id.buyButton);
        buyItemsButton.setOnClickListener(view1 -> buyItems(view, adapter));

        ProgressBar shoppingBar = view.findViewById(R.id.shoppingListProgressbar);
        shoppingBar.setVisibility(View.VISIBLE);

        db.collection("listaspesa").whereEqualTo("houseId", db.collection("case").document(houseViewModel.getHouseId())).get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    //List<Map<String, Object>> notes = task.getResult().getDocuments().stream()
                    //        .map(DocumentSnapshot::getData).collect(Collectors.toList());
                    List<ShoppingItem> shoppingItems = task.getResult().getDocuments().stream().map(documentSnapshot -> {
                        //Note note = documentSnapshot.getString("userId");
                        ShoppingItem shoppingItem = new ShoppingItem(
                                documentSnapshot.getId(),
                                (String) documentSnapshot.get("name")
                        );
                        return shoppingItem;
                    }).collect(Collectors.toList());
                    //String[] items = notes.stream().map(note -> (String) note.get("text")).toArray(String[]::new);
                    adapter.setItems(shoppingItems);

                    notesListView.setAdapter(adapter);
                    if (getContext() == null) {
                        return;
                    }
                    int dividerHeight = getResources().getDimensionPixelSize(R.dimen.divider_height);
                    notesListView.setDividerHeight(dividerHeight);

                    // Imposta il listener di click sugli elementi della lista
                    notesListView.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view1, position, id) -> {
                        ShoppingItem selectedItem = shoppingItems.get(position);
                        String a = "";
                    });
                    shoppingBar.setVisibility(View.GONE);
                });


        return view;
    }

    private void buyItems(View view, ShoppingListAdapter adapter) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("shoppingItems", adapter.getCheckedItems());
        ArrayList<ShoppingItem> items = adapter.getCheckedItems();
        NavigationUtils.navigateTo(R.id.action_shoppingListFragment_to_buyItemsFragment, view, bundle);
    }

    public void addItem(View view, ShoppingListAdapter adapter) {
        EditText itemName = (EditText) view.findViewById(R.id.itemNameField);
        String name = itemName.getText().toString();
        if (name.equals("")) {
            return;
        }
        itemName.setText("");
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("houseId", db.collection("case").document(houseViewModel.getHouseId()));
        db.collection("listaspesa").add(item)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    adapter.addItem(new ShoppingItem(task.getResult().getId(), name));
                });
    }
}