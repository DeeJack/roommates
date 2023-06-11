package it.unitn.disi.fumiprovv.roommates.fragments.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Roommate;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button newModerator;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", true);
        boolean isWhosHomeEnabled = sharedPreferences.getBoolean("isWhosHomeEnabled", false);
        SwitchCompat darkMode = view.findViewById(R.id.switchDarkMode);
        SwitchCompat whosHome = view.findViewById(R.id.switchWhosHome);
        darkMode.setChecked(isDarkMode);
        whosHome.setChecked(isWhosHomeEnabled);
        darkMode.setOnCheckedChangeListener((a, b) -> save());
        whosHome.setOnCheckedChangeListener((a, b) -> save());

        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener((view1) -> logout(view));


        Button leaveBtn = view.findViewById(R.id.leaveBtn);
        leaveBtn.setOnClickListener((view1) -> leaveHouse(view));

        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        ImageView shareBtn = view.findViewById(R.id.shareButton2);
        shareBtn.setOnClickListener((view1) -> openShareDialog(houseViewModel.getHouseId()));

        TextView houseId = view.findViewById(R.id.houseIdField2);
        houseId.setText(houseViewModel.getHouseId());

        TextView userName = view.findViewById(R.id.settingsNameField);
        //UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userName.setText(mAuth.getCurrentUser().getDisplayName());

        newModerator = view.findViewById(R.id.newModeratorBtn);

        db.collection("case").document(houseViewModel.getHouseId()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            DocumentSnapshot document = task.getResult();
            List<Map<String, Object>> roommates = (List<Map<String, Object>>) document.get("roommates");
            for (Map<String, Object> roommate : roommates) {
                if (Objects.equals(roommate.get("userId"), mAuth.getCurrentUser().getUid()) && Boolean.TRUE.equals(roommate.get("moderator"))) {
                    newModerator.setEnabled(true);
                    newModerator.setOnClickListener((view1) -> openNewModeratorDialog());
                }
            }

        });

        return view;
    }

    private void openNewModeratorDialog() {
        new ModeratorDialogFragment(newModerator).show(getChildFragmentManager(), "ModeratorDialogFragment");
    }

    // load from shared preferences
    public void save() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SwitchCompat darkMode = requireView().findViewById(R.id.switchDarkMode);
        SwitchCompat whosHome = requireView().findViewById(R.id.switchWhosHome);
        editor.putBoolean("isDarkMode", darkMode.isChecked());
        editor.putBoolean("isWhosHomeEnabled", whosHome.isChecked());
        editor.apply();
    }

    public void logout(View view) {
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        houseViewModel.setHouseId("");
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("house", MODE_PRIVATE);
        sharedPref.edit().putString("houseId", "").apply();
        mAuth.signOut();
        NavigationUtils.navigateTo(R.id.action_settingsFragment_to_loginFragment, view);
    }

    private void leaveHouse(View view) {
        HouseViewModel houseViewModel = new ViewModelProvider(requireActivity()).get(HouseViewModel.class);
        DocumentReference documentSnapshotTask = db.collection("case").document(houseViewModel.getHouseId());
        documentSnapshotTask.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();

            List<Map<String, Object>> roommatesList = (List<Map<String, Object>>) document.getData().get("roommates");

            WriteBatch batch = db.batch();

            roommatesList.stream().filter(roommate -> Objects.equals(roommate.get("userId"), mAuth.getCurrentUser().getUid())).findFirst().ifPresent(roommate -> {
                if (roommatesList.size() == 1) {
                    batch.delete(documentSnapshotTask);
                } else {
                    batch.update(documentSnapshotTask, "roommates", FieldValue.arrayRemove(new Roommate(mAuth.getCurrentUser().getUid(), (Timestamp) roommate.get("joinDate"), (Boolean) roommate.get("moderator"))));
                    if (Boolean.TRUE.equals(roommate.get("moderator"))) {
                        Map<String, Object> otherRoommate = roommatesList.stream().filter(r -> !Objects.equals(r.get("userId"), mAuth.getCurrentUser().getUid())).findFirst().get();
                        // remove the otherRoommate and add it again with moderator = true
                        batch.update(documentSnapshotTask, "roommates", FieldValue.arrayRemove(new Roommate(otherRoommate.get("userId").toString(), (Timestamp) otherRoommate.get("joinDate"), (Boolean) otherRoommate.get("moderator"))));
                        batch.update(documentSnapshotTask, "roommates", FieldValue.arrayUnion(new Roommate(otherRoommate.get("userId").toString(), (Timestamp) otherRoommate.get("joinDate"), true)));
                    }
                }
                batch.update(db.collection("utenti").document(mAuth.getCurrentUser().getUid()), "casa", null);
                batch.commit().addOnSuccessListener(task1 -> {
                    NavigationUtils.navigateTo(R.id.action_settingsFragment_to_houseCreationFragment, view);
                }).addOnFailureListener(task1 -> {
                    Toast.makeText(getContext(), "Errore!", Toast.LENGTH_SHORT).show();
                });
            });
        });

        /*documentSnapshotTask.addOnFailureListener(task -> {
            Log.d("SettingsFragment", "Error removing roommate from house");
            Toast.makeText(getContext(), "Errore!", Toast.LENGTH_SHORT).show();
        });*/

    }

    public void openShareDialog(String houseId) {
        String text = getString(R.string.share_code_text).replace("{code}", houseId).replace("{link}", "http://roommates.asd/join?code=" + houseId).replace("\\n", "\n");

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_code_title));
        startActivity(shareIntent);
    }
}