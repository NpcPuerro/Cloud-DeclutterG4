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

public class SignupFragment extends Fragment {
    private EditText etSignupEmail;
    private EditText etSignupPassword;
    private EditText etSignupPasswordConfirm;
    private TextView txPwMatch;

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        etSignupEmail = view.findViewById(R.id.etSignupEmail);
        etSignupPassword = view.findViewById(R.id.etSignupPassword);
        etSignupPasswordConfirm = view.findViewById(R.id.etSignupPasswordConfirm);
        txPwMatch = view.findViewById(R.id.txPwMatch);

        Button signupButton = view.findViewById(R.id.btSignupSubmit);
        signupButton.setOnClickListener(this::submit);

        TextView txNoAccount = view.findViewById(R.id.txYesAccount);
        String linkText = getString(R.string.loginYesAccountLink);
        String fullText = getString(R.string.loginYesAccount, linkText);

        Spannable spannable = LoginUtils.getSpannable(fullText, linkText, this::showLogin);

        txNoAccount.setText(spannable);
        txNoAccount.setMovementMethod(LinkMovementMethod.getInstance());

        etSignupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePwdConfirm();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etSignupPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updatePwdConfirm();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return view;
    }

    private void updatePwdConfirm() {
        txPwMatch.setVisibility(View.VISIBLE);
        if (etSignupPassword.getText().toString().equals(etSignupPasswordConfirm.getText().toString())) {
            txPwMatch.setText(R.string.loginPwdYesMatch);
        }
        else {
            txPwMatch.setText(R.string.loginPwdNoMatch);
        }
    }

    private void showLogin(View v) {
        LoginActivity activity = (LoginActivity) requireActivity();
        activity.showLoginFragment();
    }

    // TODO: change submit logic
    private void submit(View v) {
        if (etSignupEmail == null) return;
        String email = etSignupEmail.getText().toString();
        
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}