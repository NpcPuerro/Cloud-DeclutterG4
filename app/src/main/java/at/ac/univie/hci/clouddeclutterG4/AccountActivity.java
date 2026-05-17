package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class AccountActivity extends AppCompatActivity {
    private TextView txPwMatch;
    private TextView txAccEmailMatch;
    private EditText etEmail;
    private EditText etOldPwd;
    private EditText etNewPwd;
    private EditText etNewPwdConf;
    private Button btEditConf;
    private Button btChangePwd;
    private Button btAccLogout;
    private Button btAccDelAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        txPwMatch = findViewById(R.id.txPwMatch);
        etEmail = findViewById(R.id.etAccEmail);
        etOldPwd = findViewById(R.id.etAccOldPwd);
        etNewPwd = findViewById(R.id.etAccNewPwd);
        etNewPwdConf = findViewById(R.id.etAccNewPwd2);
        btEditConf = findViewById(R.id.btEditConf);
        btChangePwd = findViewById(R.id.btAccChangePwd);
        txAccEmailMatch = findViewById(R.id.txAccEmailMatch);
        btAccLogout = findViewById(R.id.btAccLogout);
        btAccDelAccount = findViewById(R.id.btAccDelAccount);

        etEmail.setText(LoginData.getLogin());
        etEmail.setEnabled(false);

        btChangePwd.setOnClickListener(this::changePwd);
        btChangePwd.setEnabled(false);

        btEditConf.setOnClickListener(this::editButton);

        btAccLogout.setOnClickListener(this::logout);
        btAccDelAccount.setOnClickListener(this::deleteAccount);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                updateEditButton();
                txAccEmailMatch.setVisibility(Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches() ? View.INVISIBLE : View.VISIBLE);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        etOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                updatePwdBtn();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });

        etNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                updatePwdConfirm();
                updatePwdBtn();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        etNewPwdConf.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updatePwdConfirm();
                updatePwdBtn();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void updatePwdConfirm() {
        txPwMatch.setVisibility(View.VISIBLE);
        String newPwd = etNewPwd.getText().toString();
        String newPwdConf = etNewPwdConf.getText().toString();
        if (!newPwd.isEmpty() && !newPwdConf.isEmpty() && newPwd.equals(newPwdConf)) {
            txPwMatch.setText(R.string.loginPwdYesMatch);
        }
        else {
            txPwMatch.setText(R.string.loginPwdNoMatch);
        }
    }

    private void updatePwdBtn() {
        String newPwd = etNewPwd.getText().toString();
        String newPwdConf = etNewPwdConf.getText().toString();

        btChangePwd.setEnabled(
                !etOldPwd.getText().toString().isEmpty() &&
                !newPwd.isEmpty() &&
                !newPwdConf.isEmpty() &&
                newPwd.equals(newPwdConf)
        );
    }

    private void editButton(View v) {
        if (etEmail.isEnabled()) {
            String email = etEmail.getText().toString();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && LoginData.changeEmail(email)) {
                etEmail.setEnabled(false);
                btEditConf.setText(R.string.accEditEmail);
            }
            else {
                Snackbar.make(
                        v,
                        R.string.invalidEmailWarning,
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        }
        else {
            etEmail.setEnabled(true);
            btEditConf.setText(R.string.accSaveEmail);
        }
    }

    private void updateEditButton() {
        btEditConf.setEnabled(Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches());
    }

    private void changePwd(View v) {
        String oldPassword = etOldPwd.getText().toString();
        String newPassword = etNewPwd.getText().toString();

        if (LoginData.changePassword(oldPassword, newPassword)) {
            etOldPwd.setText("");
            etNewPwd.setText("");
            etNewPwdConf.setText("");
            txPwMatch.setVisibility(View.INVISIBLE);


            Snackbar.make(
                    v,
                    R.string.accChangedPwd,
                    Snackbar.LENGTH_SHORT
            ).show();
        }
        else {
            Snackbar.make(
                    v,
                    R.string.accWrongPassword,
                    Snackbar.LENGTH_SHORT
            ).show();
        }

    }

    private void logout(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.accLogout)
                .setMessage(R.string.accLogoutMsg)
                .setPositiveButton(R.string.accLogoutConfirm, (dialog, which) -> {
                    LoginData.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void deleteAccount(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.accDeleteAccount)
                .setMessage(R.string.accDeleteAccountMsg)
                .setPositiveButton(R.string.accDeleteAccountConfirm, (dialog, which) -> {
                    LoginData.deleteAccount();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

}