package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private android.widget.Button btnCleanup;
    private android.widget.Button btnMainScan;
    private android.widget.Button btnAddCloud;
    private android.widget.Button btnUsage;
    private android.widget.TextView statusOk;
    private android.widget.TextView statusBad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_main, R.string.nav_main); 
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnCleanup = findViewById(R.id.btn_cleanup);
        btnCleanup.setOnClickListener(v -> startActivity(new Intent(this, scanningActivity.class))); 
        btnUsage = findViewById(R.id.btn_usage);
        btnUsage.setOnClickListener(v -> startActivity(new Intent(this, UsageActivity.class)));
        btnMainScan = findViewById(R.id.btn_main_scan);
        btnMainScan.setOnClickListener(v -> startActivity(new Intent(this, ScanSettingsActivity.class)));
        btnAddCloud = findViewById(R.id.btn_add_cloud);
        btnAddCloud.setOnClickListener(view -> startActivity(new Intent(this, CloudActivity.class)));
        statusOk = findViewById(R.id.status_for_the_app_good);
        statusBad = findViewById(R.id.status_for_the_app_bad);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonStates();
    }

    private void updateButtonStates() {
        MockDataManager dm = MockDataManager.getInstance();
        boolean anyActive = false;
        for (MockDataManager.CloudService service : dm.cloudServices.values()) {
            if (service.isConnected && service.isActive) {
                anyActive = true;
                break;
            }
        }
        int mainButtonsVisibility = anyActive ? android.view.View.VISIBLE : android.view.View.GONE;
        int btnAddCloudVisibility = anyActive ? android.view.View.GONE : android.view.View.VISIBLE;

        btnCleanup.setVisibility(mainButtonsVisibility);
        btnMainScan.setVisibility(mainButtonsVisibility);
        statusOk.setVisibility(mainButtonsVisibility);
        btnUsage.setVisibility(mainButtonsVisibility);

        if (btnAddCloud != null) {
            btnAddCloud.setVisibility(btnAddCloudVisibility);
            statusBad.setVisibility(btnAddCloudVisibility);
        }
        //btnCleanup.setEnabled(anyActive);
        //btnCleanup.setAlpha(anyActive ? 1.0f : 0.5f);
        //.setEnabled(anyActive);
        //btnMainScan.setAlpha(anyActive ? 1.0f : 0.5f);


        // Also update the navigation drawer menu item
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            MenuItem cleanupItem = navigationView.getMenu().findItem(R.id.nav_cleanup);
            if (cleanupItem != null) {
                cleanupItem.setEnabled(anyActive);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
        } else if (id == R.id.nav_usage) {
            startActivity(new Intent(this, UsageActivity.class));
        } else if (id == R.id.nav_cleanup) {
            if (btnCleanup.isEnabled()) {
                startActivity(new Intent(this, scanningActivity.class));
            }
        } else if (id == R.id.nav_trash) {
            startActivity(new Intent(this, TrashActivity.class));
        } else if (id == R.id.nav_faq) {
            startActivity(new Intent(this, FAQActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}