package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

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

        findViewById(R.id.btn_account_settings).setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
        findViewById(R.id.btn_cloud_dienste).setOnClickListener(v -> startActivity(new Intent(this, CloudActivity.class)));

        findViewById(R.id.toolbar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        Spinner spinner_periodic_scan = findViewById(R.id.spinner_periodic_scan);
        String[] ps_options = getResources().getStringArray(R.array.settings_periodic_scan_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ps_options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_periodic_scan.setAdapter(adapter);

        Button btBlacklist = findViewById(R.id.btn_blacklist);
        btBlacklist.setOnClickListener(this::showBlacklistDialog);

        if (getIntent().getBooleanExtra("show_blacklist", false)) {
            showBlacklistDialog(btBlacklist);
        }

    }

    private void showBlacklistDialog(View v) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_blacklist, null);
        EditText etBLFilterAdd = dialogView.findViewById(R.id.etBLFilterAdd);
        Button btBLFilterAdd = dialogView.findViewById(R.id.btBLFilterAdd);
        btBLFilterAdd.setEnabled(false);
        etBLFilterAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                btBLFilterAdd.setEnabled(!editable.toString().isEmpty());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        RecyclerView rvBLFilters = dialogView.findViewById(R.id.rvBLFilters);
        rvBLFilters.setLayoutManager(new LinearLayoutManager(this));
        MockDataManager dm = MockDataManager.getInstance();
        BlacklistAdapter adapter = new BlacklistAdapter(dm.blacklistFilters);
        rvBLFilters.setAdapter(adapter);

        btBLFilterAdd.setOnClickListener(v2 -> {
            dm.blacklistFilters.add(etBLFilterAdd.getText().toString());
            etBLFilterAdd.setText("");
            adapter.notifyDataSetChanged();

        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.blacklist_title)
                .setView(dialogView)
                .setPositiveButton(R.string.blacklist_ok, null)
                .setNeutralButton(R.string.blacklist_help, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    startActivity(new Intent(this, FAQActivity.class));
                }).show();

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
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
        } else if (id == R.id.nav_expanded_scan) {
            startActivity(new Intent(this, ScanSettingsActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
