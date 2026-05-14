package at.ac.univie.hci.clouddeclutterG4.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import at.ac.univie.hci.clouddeclutterG4.LoginActivity;
import at.ac.univie.hci.clouddeclutterG4.MainActivity;
import at.ac.univie.hci.clouddeclutterG4.R;

public class LoginFragment extends Fragment {
    private EditText etLoginEmail;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etLoginEmail = view.findViewById(R.id.etLoginEmail);

        Button loginButton = view.findViewById(R.id.btLoginSubmit);
        loginButton.setOnClickListener(this::submit);

        TextView txNoAccount = view.findViewById(R.id.txNoAccount);
        String linkText = getString(R.string.loginNoAccountLink);
        String fullText = getString(R.string.loginNoAccount, linkText);

        Spannable spannable = LoginUtils.getSpannable(fullText, linkText, this::showSignup);

        txNoAccount.setText(spannable);
        txNoAccount.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private void showSignup(View v) {
        LoginActivity activity = (LoginActivity) requireActivity();
        activity.showSignupFragment();
    }

    // TODO: change submit logic
    private void submit(View v) {
        if (etLoginEmail == null) return;
        String email = etLoginEmail.getText().toString();
        
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}