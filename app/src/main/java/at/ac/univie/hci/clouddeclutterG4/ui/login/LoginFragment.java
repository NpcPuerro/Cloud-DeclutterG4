package at.ac.univie.hci.clouddeclutterG4.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import at.ac.univie.hci.clouddeclutterG4.LoginActivity;
import at.ac.univie.hci.clouddeclutterG4.LoginData;
import at.ac.univie.hci.clouddeclutterG4.MainActivity;
import at.ac.univie.hci.clouddeclutterG4.R;

public class LoginFragment extends Fragment {
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private TextView txLoginEmailMatch;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etLoginEmail = view.findViewById(R.id.etLoginEmail);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        txLoginEmailMatch = view.findViewById(R.id.txLoginEmailMatch);

        Button loginButton = view.findViewById(R.id.btLoginSubmit);
        loginButton.setOnClickListener(this::submit);

        TextView txNoAccount = view.findViewById(R.id.txNoAccount);
        String accLinkText = getString(R.string.loginNoAccountLink);
        String accFullText = getString(R.string.loginNoAccount, accLinkText);

        Spannable accSpannable = LoginUtils.getSpannable(accFullText, accLinkText, this::showSignup);

        txNoAccount.setText(accSpannable);
        txNoAccount.setMovementMethod(LinkMovementMethod.getInstance());

        TextView txForgotPassword = view.findViewById(R.id.txForgotPassword);
        String fpwdLinkText = getString(R.string.loginForgotPasswordLink);
        String fpwdFullText = getString(R.string.loginForgotPassword, fpwdLinkText);

        Spannable fpwdSpannable = LoginUtils.getSpannable(fpwdFullText, fpwdLinkText, this::showForgotPassword);

        txForgotPassword.setText(fpwdSpannable);
        txForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        etLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                txLoginEmailMatch.setVisibility(Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches() ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        etLoginPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) return true;
                submit(textView);
                return true;
            }
            return false;
        });

        return view;
    }

    private void showSignup(View v) {
        LoginActivity activity = (LoginActivity) requireActivity();
        activity.showSignupFragment();
    }

    private void showForgotPassword(View v) {
        LoginActivity activity = (LoginActivity) requireActivity();
        activity.showForgotPasswordFragment();
    }

    private void submit(View v) {
        if (etLoginEmail == null) return;
        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etLoginPassword.requestFocus();
            return;
        }

        if (!LoginData.login(email, password)) {
            Snackbar.make(
                    v,
                    R.string.loginWrongLoginData,
                    Snackbar.LENGTH_LONG
            ).show();
            return;
        }

        
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}