package at.ac.univie.hci.clouddeclutterG4;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.snackbar.Snackbar;

public class CloudConnectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dropbox_login);

        String cloudKey = getIntent().getStringExtra("cloud_name");
        if (cloudKey == null) cloudKey = "Cloud";

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());

            if (cloudKey.contains("Google Drive")) toolbar.setTitle(R.string.google_login_title);
            else if (cloudKey.contains("OneDrive")) toolbar.setTitle(R.string.onedrive_login_title);
            else if (cloudKey.contains("iCloud")) toolbar.setTitle(R.string.icloud_login_title);
            else toolbar.setTitle(R.string.dropbox_login_title);
        }

        final String finalCloudKey = cloudKey;
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            MockDataManager dm = MockDataManager.getInstance();
            MockDataManager.CloudService service = dm.cloudServices.get(finalCloudKey);
            if (service != null) {
                service.isConnected = true;
                service.isActive = true;
            }
            
            int msgRes = R.string.msg_dropbox_success;
            if (finalCloudKey.contains("Google Drive")) msgRes = R.string.msg_google_success;
            else if (finalCloudKey.contains("OneDrive")) msgRes = R.string.msg_onedrive_success;
            else if (finalCloudKey.contains("iCloud")) msgRes = R.string.msg_icloud_success;

            Snackbar.make(findViewById(R.id.main), msgRes, Snackbar.LENGTH_LONG).show();
            finish();
        });

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }
}
