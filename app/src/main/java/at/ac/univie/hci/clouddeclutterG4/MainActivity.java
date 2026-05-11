package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button goScan;
    private Button goLogin;
    private Button goFAQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        goScan = findViewById(R.id.button);
        goLogin = findViewById(R.id.btGoLogin);
        goFAQ = findViewById(R.id.btGoFAQ);

        goLogin.setOnClickListener(this::gotoLogin);
        goFAQ.setOnClickListener(this::gotoFAQ);
    }

    public void Scan(View v){
        Intent intent = new Intent(this, ScanSettingsActivity.class);
        startActivity(intent);
    }

    public void gotoLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void gotoFAQ(View v) {
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }

}