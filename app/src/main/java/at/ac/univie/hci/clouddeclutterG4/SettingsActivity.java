package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_scan_settings).setOnClickListener(v -> startActivity(new Intent(this, ScanSettingsActivity.class))); //new File Naomi
        findViewById(R.id.btn_cloud_dienste).setOnClickListener(v -> startActivity(new Intent(this, CloudActivity.class)));

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        Spinner spinner_periodic_scan = findViewById(R.id.spinner_periodic_scan);
        String[] ps_options = getResources().getStringArray(R.array.settings_periodic_scan_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ps_options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_periodic_scan.setAdapter(adapter);

    }
}
