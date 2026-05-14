package at.ac.univie.hci.clouddeclutterG4.ui.login;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import at.ac.univie.hci.clouddeclutterG4.LoginActivity;
import at.ac.univie.hci.clouddeclutterG4.R;

public class ForgotPasswordFragment extends Fragment {
    private Button btFpwdConfirm;
    private EditText etEmail;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_pwd, container, false);

        btFpwdConfirm = view.findViewById(R.id.btFpwdConfirm);
        etEmail = view.findViewById(R.id.etFpwdEmail);

        btFpwdConfirm.setOnClickListener(this::submit);

        return view;
    }

    private void submit(View v) {
        if (etEmail == null) return;
        if (Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            LoginActivity activity = (LoginActivity) requireActivity();
            activity.showForgotPasswordConfirmFragment();
        }
        else {
            Snackbar.make(
                    v,
                    R.string.invalidEmailWarning,
                    Snackbar.LENGTH_SHORT
            ).show();
        }

    }
}
