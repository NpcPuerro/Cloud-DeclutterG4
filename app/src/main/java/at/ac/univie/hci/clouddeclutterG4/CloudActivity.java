//new File Naomi

package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

public class CloudActivity extends AppCompatActivity {

    private MaterialSwitch dropboxSwitch;
    private Button connectDropbox;
    private TextView statusDropbox;
    private TextView accountDropbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cloud);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dropboxSwitch = findViewById(R.id.switch_dropbox);
        connectDropbox = findViewById(R.id.btn_connect_dropbox);
        statusDropbox = findViewById(R.id.status_dropbox);
        accountDropbox = findViewById(R.id.account_dropbox);

        setupSwitch("Google Drive", R.id.switch_google);
        setupSwitch("OneDrive", R.id.switch_onedrive);
        setupSwitch("iCloud", R.id.switch_icloud);

        connectDropbox.setOnClickListener(v -> {
            startActivity(new Intent(this, DropboxLoginActivity.class));
        });

        dropboxSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MockDataManager dm = MockDataManager.getInstance();
            MockDataManager.CloudService dropbox = dm.cloudServices.get("Dropbox");
            if (dropbox != null) dropbox.isActive = isChecked;
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDropboxUI();
    }

    private void refreshDropboxUI() {
        MockDataManager dm = MockDataManager.getInstance();
        MockDataManager.CloudService dropbox = dm.cloudServices.get("Dropbox");
        if (dropbox != null) {
            dropboxSwitch.setChecked(dropbox.isActive);
            if (dropbox.isConnected) {
                connectDropbox.setVisibility(View.GONE);
                dropboxSwitch.setVisibility(View.VISIBLE);
                accountDropbox.setVisibility(View.VISIBLE);
                statusDropbox.setText(R.string.status_connected);
                statusDropbox.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
            } else {
                connectDropbox.setVisibility(View.VISIBLE);
                dropboxSwitch.setVisibility(View.GONE);
                accountDropbox.setVisibility(View.GONE);
                statusDropbox.setText(R.string.status_not_connected);
                statusDropbox.setTextColor(0xFF666666);
            }
        }
    }

    private void setupSwitch(String name, int id) {
        MaterialSwitch s = findViewById(id);
        MockDataManager dm = MockDataManager.getInstance();
        MockDataManager.CloudService service = dm.cloudServices.get(name);
        if (service != null) {
            s.setChecked(service.isActive);
            s.setOnCheckedChangeListener((buttonView, isChecked) -> service.isActive = isChecked);
        }
    }
}
