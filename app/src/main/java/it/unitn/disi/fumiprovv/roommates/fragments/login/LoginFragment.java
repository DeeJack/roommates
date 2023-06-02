package it.unitn.disi.fumiprovv.roommates.fragments.login;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import it.unitn.disi.fumiprovv.roommates.MainActivity;
import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.utils.FieldValidation;
import it.unitn.disi.fumiprovv.roommates.utils.NavigationUtils;
import it.unitn.disi.fumiprovv.roommates.viewmodels.HouseViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setDrawerLocked(true);

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button loginBtn = (Button) view.findViewById(R.id.loginButton);
        loginBtn.setOnClickListener((a) -> onLoginButtonClick(view));
        Button registrationBtn = (Button) view.findViewById(R.id.registrationButton);
        registrationBtn.setOnClickListener((a) -> onRegistrationButtonClick(view));
        TextView forgotPassword = (TextView) view.findViewById(R.id.forgotPassButton);
        forgotPassword.setOnClickListener((a) -> onForgotPasswordButtonClick(view));
        return view;
    }

    private void onForgotPasswordButtonClick(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
    }

    public void onLoginButtonClick(View view) {
        String email = ((TextView) view.findViewById(R.id.registerEmailField)).getText().toString();

        if (!FieldValidation.checkEmailRequirements(email)) {
            Toast.makeText(getContext(), getString(R.string.email_not_valid), Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        String password = ((TextView) view.findViewById(R.id.registerPasswordField)).getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    Log.d(TAG, "signInWithEmail:success");
                    progressBar.setVisibility(View.GONE);
                    NavController navController = Navigation.findNavController(view);

                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("house", Context.MODE_PRIVATE);
                    HouseViewModel houseViewModel = new ViewModelProvider(this).get(HouseViewModel.class);

                    NavigationUtils.conditionalLogin(navController, sharedPref, requireActivity());
                })
                .addOnFailureListener(task -> {
                    Toast.makeText(getContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "signInWithEmail:failure" + task.getMessage(), task.getCause());
                    progressBar.setVisibility(View.GONE);
                });
    }

    public void onRegistrationButtonClick(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_loginFragment_to_registrationFragment);
    }
}