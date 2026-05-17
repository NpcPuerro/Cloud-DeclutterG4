package at.ac.univie.hci.clouddeclutterG4;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class DropboxLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dropbox_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            MockDataManager dm = MockDataManager.getInstance();
            MockDataManager.CloudService dropbox = dm.cloudServices.get("Dropbox");
            if (dropbox != null) {
                dropbox.isConnected = true;
                dropbox.isActive = true;
            }
            Snackbar.make(findViewById(R.id.main), R.string.msg_dropbox_success, Snackbar.LENGTH_LONG).show();
            finish();
        });

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }
}
