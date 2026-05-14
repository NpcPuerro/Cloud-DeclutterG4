package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class UsageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        findViewById(R.id.btn_add_cloud).setOnClickListener(v -> {
            startActivity(new Intent(this, CloudActivity.class));
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }
}
