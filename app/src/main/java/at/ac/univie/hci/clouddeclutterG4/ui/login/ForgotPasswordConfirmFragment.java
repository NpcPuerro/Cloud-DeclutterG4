package at.ac.univie.hci.clouddeclutterG4.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import at.ac.univie.hci.clouddeclutterG4.LoginActivity;
import at.ac.univie.hci.clouddeclutterG4.R;

public class ForgotPasswordConfirmFragment extends Fragment {
    private Button btGoBack;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_pwd2, container, false);

        btGoBack = view.findViewById(R.id.btFpwdGoBack);
        btGoBack.setOnClickListener(this::submit);

        return view;
    }

    private void submit(View v) {
        LoginActivity activity = (LoginActivity) requireActivity();
        activity.returnToLogin();
    }
}
