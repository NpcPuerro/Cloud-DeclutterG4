package at.ac.univie.hci.clouddeclutterG4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import at.ac.univie.hci.clouddeclutterG4.ui.login.LoginFragment;
import at.ac.univie.hci.clouddeclutterG4.ui.login.SignupFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_login, LoginFragment.newInstance())
                    .commitNow();
        }
    }

    public void showLoginFragment() {
        getSupportFragmentManager()
                .popBackStack();
    }

    public void showSignupFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container_login,
                        new SignupFragment()
                )
                .addToBackStack(null)
                .commit();
    }
}