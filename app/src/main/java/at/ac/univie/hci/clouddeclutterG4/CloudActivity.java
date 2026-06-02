package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class CloudActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private android.widget.LinearLayout dynamicContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cloud);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_main, R.string.nav_main);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dynamicContainer = findViewById(R.id.dynamic_cloud_container);
        findViewById(R.id.btn_add_cloud_service).setOnClickListener(v -> showAddCloudDialog());

        findViewById(R.id.toolbar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAllUI();
        updateMenuState();
    }

    private void updateMenuState() {
        MockDataManager dm = MockDataManager.getInstance();
        boolean anyActive = false;
        for (MockDataManager.CloudService service : dm.cloudServices.values()) {
            if (service.isConnected && service.isActive) {
                anyActive = true;
                break;
            }
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            MenuItem cleanupItem = navigationView.getMenu().findItem(R.id.nav_cleanup);
            if (cleanupItem != null) {
                cleanupItem.setEnabled(anyActive);
            }
        }
    }

    private void refreshAllUI() {
        findViewById(R.id.card_google).setVisibility(View.GONE);
        findViewById(R.id.card_dropbox).setVisibility(View.GONE);
        findViewById(R.id.card_onedrive).setVisibility(View.GONE);
        findViewById(R.id.card_icloud).setVisibility(View.GONE);

        dynamicContainer.removeAllViews();
        
        MockDataManager dm = MockDataManager.getInstance();
        findViewById(R.id.icloud_hidden).setVisibility(dm.hideBrokenIcloud ? View.VISIBLE : View.GONE);
        for (java.util.Map.Entry<String, MockDataManager.CloudService> entry : dm.cloudServices.entrySet()) {
            String name = entry.getKey();
            MockDataManager.CloudService service = entry.getValue();
            
            if (isHardcoded(name)) {
                updateHardcodedCard(name, service);
            } else {
                addDynamicCloudCard(name, service);
            }
        }
    }

    private boolean isHardcoded(String name) {
        return name.equals("Google Drive") || name.equals("Dropbox") || 
               name.equals("OneDrive") || name.equals("iCloud");
    }

    private void updateHardcodedCard(String name, MockDataManager.CloudService service) {
        int cardId = 0, statusId = 0, accountId = 0, connectBtnId = 0, disconnectBtnId = 0, switchId = 0;
        
        switch (name) {
            case "Google Drive":
                cardId = R.id.card_google; statusId = R.id.status_google; 
                accountId = R.id.account_google; connectBtnId = R.id.btn_connect_google; 
                disconnectBtnId = R.id.btn_disconnect_google; switchId = R.id.switch_google;
                break;
            case "Dropbox":
                cardId = R.id.card_dropbox; statusId = R.id.status_dropbox; 
                accountId = R.id.account_dropbox; connectBtnId = R.id.btn_connect_dropbox; 
                disconnectBtnId = R.id.btn_disconnect_dropbox; switchId = R.id.switch_dropbox;
                break;
            case "OneDrive":
                cardId = R.id.card_onedrive; statusId = R.id.status_onedrive; 
                accountId = R.id.account_onedrive; connectBtnId = R.id.btn_connect_onedrive; 
                disconnectBtnId = R.id.btn_disconnect_onedrive; switchId = R.id.switch_onedrive;
                break;
            case "iCloud":
                cardId = R.id.card_icloud; statusId = R.id.status_icloud; 
                accountId = R.id.account_icloud; connectBtnId = R.id.btn_connect_icloud; 
                disconnectBtnId = R.id.btn_disconnect_icloud; switchId = R.id.switch_icloud;
                break;
        }
        
        if (cardId != 0) {
            findViewById(cardId).setVisibility(View.VISIBLE);
            setupCloudCardLogic(name, service, findViewById(statusId), 
                findViewById(accountId), findViewById(connectBtnId), 
                findViewById(disconnectBtnId), findViewById(switchId));
        }
    }

    private void addDynamicCloudCard(String name, MockDataManager.CloudService service) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_cloud_card, dynamicContainer, false);
        ((TextView)card.findViewById(R.id.card_header)).setText(service.name);
        
        setupCloudCardLogic(name, service, card.findViewById(R.id.card_status), 
            card.findViewById(R.id.card_account), card.findViewById(R.id.card_btn_connect), 
            card.findViewById(R.id.card_btn_disconnect), card.findViewById(R.id.card_switch));
            
        dynamicContainer.addView(card);
    }

    private void setupCloudCardLogic(String key, MockDataManager.CloudService service, TextView statusTv, TextView accountTv, Button connectBtn, Button disconnectBtn, MaterialSwitch s) {
        updateCardUI(service, statusTv, accountTv, connectBtn, disconnectBtn, s);

        connectBtn.setOnClickListener(v -> {
            if (key.equals("Dropbox")) {
                startActivity(new Intent(this, DropboxLoginActivity.class));
            } else {
                Intent loginIntent = new Intent(this, CloudConnectActivity.class);
                loginIntent.putExtra("cloud_name", key);
                startActivity(loginIntent);
            }
        });

        disconnectBtn.setOnClickListener(v -> {
            final android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("LÖSCHEN");
            
            android.widget.FrameLayout container = new android.widget.FrameLayout(this);
            android.widget.FrameLayout.LayoutParams params = new  android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 60; 
            params.rightMargin = 60;
            input.setLayoutParams(params);
            container.addView(input);
            
            new AlertDialog.Builder(this)
                    .setTitle("Cloud Dienst entfernen")
                    .setMessage("Geben Sie LÖSCHEN ein, um diesen Cloud Dienst zu entfernen.")
                    .setView(container)
                    .setPositiveButton(R.string.cloud_remove_confirm, (dialog, which) -> {
                        if (input.getText().toString().equals("LÖSCHEN")) {
                            MockDataManager.getInstance().cloudServices.remove(key);
                            refreshAllUI();
                            updateMenuState();
                        } else {
                            Snackbar.make(findViewById(R.id.main), "Falsche Eingabe. Dienst wurde nicht entfernt.", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        });

        s.setOnCheckedChangeListener((buttonView, isChecked) -> service.isActive = isChecked);
    }

    private void updateCardUI(MockDataManager.CloudService service, TextView statusTv, TextView accountTv, Button connectBtn, Button disconnectBtn, MaterialSwitch s) {
        disconnectBtn.setVisibility(View.VISIBLE);

        if (service.isConnected) {
            if (statusTv != null) {
                statusTv.setVisibility(View.VISIBLE);
                statusTv.setText(android.text.Html.fromHtml(getString(R.string.status_connected), android.text.Html.FROM_HTML_MODE_COMPACT));
                statusTv.setTextColor(0xFF008000);
            }
            accountTv.setVisibility(View.VISIBLE);
            accountTv.setText(getString(R.string.account_label, service.accountName));
            accountTv.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
            connectBtn.setVisibility(View.GONE);
            s.setVisibility(View.VISIBLE);
            s.setChecked(service.isActive);
        } else {
            if (statusTv != null) {
                statusTv.setVisibility(View.GONE);
            }
            accountTv.setVisibility(View.GONE);
            connectBtn.setVisibility(View.VISIBLE);
            s.setVisibility(View.GONE);
        }
    }

    private void showAddCloudDialog() {
        String[] providers = {"Google Drive", "iCloud", "Dropbox", "OneDrive"};
        new AlertDialog.Builder(this)
                .setTitle("Cloud Dienst hinzufügen")
                .setItems(providers, (dialog, which) -> {
                    String provider = providers[which];
                    addNewCloud(provider);
                })
                .show();
    }

    private void addNewCloud(String provider) {
        MockDataManager dm = MockDataManager.getInstance();
        
        String keyToAdd = provider;
        if (dm.cloudServices.containsKey(provider)) {
            int userNum = 2;
            while (dm.cloudServices.containsKey(provider + " #" + userNum)) {
                userNum++;
            }
            keyToAdd = provider + " #" + userNum;
        }

        String accountName = "user" + (dm.cloudServices.size() + 1) + "@gmail.com";

        long cap = 5L * 1024 * 1024 * 1024;
        for (MockDataManager.CloudService s : dm.cloudServices.values()) {
            if (s.name.equals(provider)) {
                cap = s.totalCapacity;
                break;
            }
        }
        
        dm.cloudServices.put(keyToAdd, new MockDataManager.CloudService(provider, accountName, false, false, cap, 0));
        refreshAllUI();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_trash) {
            startActivity(new Intent(this, TrashActivity.class));
        } else if (id == R.id.nav_usage) {
            startActivity(new Intent(this, UsageActivity.class));
        } else if (id == R.id.nav_cleanup) {
            MockDataManager dm = MockDataManager.getInstance();
            boolean anyActive = false;
            for (MockDataManager.CloudService service : dm.cloudServices.values()) {
                if (service.isConnected && service.isActive) {
                    anyActive = true;
                    break;
                }
            }
            if (anyActive) {
                startActivity(new Intent(this, scanningActivity.class));
            }
        } else if (id == R.id.nav_main) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_faq) {
            startActivity(new Intent(this, FAQActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.nav_expanded_scan) {
            startActivity(new Intent(this, ScanSettingsActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
