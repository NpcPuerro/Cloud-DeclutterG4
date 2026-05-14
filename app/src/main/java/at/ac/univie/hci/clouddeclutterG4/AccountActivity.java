package at.ac.univie.hci.clouddeclutterG4;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class AccountActivity extends AppCompatActivity {
    private TextView txPwMatch;
    private EditText etEmail;
    private EditText etOldPwd;
    private EditText etNewPwd;
    private EditText etNewPwdConf;
    private ImageView ivBtEditConf;
    private Button btChangePwd;
    private String theEMail = "rainer.winkler@gmail.com";

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

        txPwMatch = findViewById(R.id.txPwMatch);
        etEmail = findViewById(R.id.etAccEmail);
        etOldPwd = findViewById(R.id.etAccOldPwd);
        etNewPwd = findViewById(R.id.etAccNewPwd);
        etNewPwdConf = findViewById(R.id.etAccNewPwd2);
        ivBtEditConf = findViewById(R.id.ivBtEditConf);
        btChangePwd = findViewById(R.id.btAccChangePwd);


        etEmail.setText(theEMail);
        etEmail.setEnabled(false);

        btChangePwd.setOnClickListener(this::changePwd);
        btChangePwd.setEnabled(false);

        ivBtEditConf.setOnClickListener(this::editButton);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateEditButton();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePwdBtn();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePwdConfirm();
                updatePwdBtn();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        if (etNewPwd.getText().toString().equals(etNewPwdConf.getText().toString())) {
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
            if (Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                etEmail.setEnabled(false);
                ivBtEditConf.setImageResource(android.R.drawable.ic_menu_edit);
            }
            else {
                Snackbar.make(
                        v,
                        "Not a valid Email",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        }
        else {
            etEmail.setEnabled(true);
            ivBtEditConf.setImageResource(android.R.drawable.ic_menu_save);
        }
    }

    private void updateEditButton() {
        ivBtEditConf.setEnabled(Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches());
    }

    private void changePwd(View v) {
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

}