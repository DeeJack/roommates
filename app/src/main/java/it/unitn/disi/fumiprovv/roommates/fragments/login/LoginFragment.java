package it.unitn.disi.fumiprovv.roommates.fragments.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import it.unitn.disi.fumiprovv.roommates.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login, container, false);
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
        navController.navigate(R.id.forgotPasswordFragment);
    }

    public void onLoginButtonClick(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.homeFragment);
    }

    public void onRegistrationButtonClick(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.registrationFragment);
    }
}