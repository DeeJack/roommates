package it.unitn.disi.fumiprovv.roommates.fragments.login;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.User;
import it.unitn.disi.fumiprovv.roommates.utils.FieldValidation;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "param2"; // No pass for security reasons

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
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
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        Button registrationBtn = (Button) view.findViewById(R.id.registrationButton);
        registrationBtn.setOnClickListener((a) -> onRegistrationButtonClick(view));
        return view;
    }

    public void onRegistrationButtonClick(View view) {
        String email = ((TextView) view.findViewById(R.id.registerEmailField)).getText().toString();
        if (!FieldValidation.checkEmailRequirements(email)) {
            Toast.makeText(view.getContext(), "Email does not meet requirements.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String password = ((TextView) view.findViewById(R.id.registerPasswordField)).getText().toString();
        String password2 = ((TextView) view.findViewById(R.id.registerPassword2Field)).getText().toString();

        if (!Objects.equals(password, password2)) {
            Toast.makeText(view.getContext(), "Passwords do not match.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!FieldValidation.checkPasswordRequirements(password)) {
            Toast.makeText(view.getContext(), "Password does not meet requirements.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.registrationProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        String name = ((TextView) view.findViewById(R.id.registerNameField)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = task.getUser();
                    user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build());

                    createUserOnDb(name, user.getUid(), view, progressBar);
                })
                .addOnFailureListener(task -> {
                    Log.w(TAG, "createUserWithEmail:failure", task.getCause());
                    Toast.makeText(getContext(), "Registrazione fallit7.",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void createUserOnDb(String name, String uid, View view, ProgressBar progressBar) {
        db.collection("utenti").document(uid).set(new User(name))
                .addOnSuccessListener(task -> {
                    NavigationUtils.navigateTo(R.id.action_registrationFragment_to_houseCreationFragment, view);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(task -> {
                    Log.w(TAG, "createUserOnDb:failure", task.getCause());
                    Toast.makeText(getContext(), "Registrazione fallita (errore del db).",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}