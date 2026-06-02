package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class UsageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usage);

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

        findViewById(R.id.btn_add_cloud).setOnClickListener(v -> {
            startActivity(new Intent(this, CloudActivity.class));
        });

        refreshUsage();
        findViewById(R.id.toolbar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUsage();
    }

    private void refreshUsage() {
        LinearLayout container = findViewById(R.id.usage_container);
        if (container == null) return;
        container.removeAllViews();

        MockDataManager dm = MockDataManager.getInstance();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (MockDataManager.CloudService service : dm.cloudServices.values()) {
            if (service.isActive && service.isConnected && !service.name.equals("Gerätespeicher")) {
                View itemView = inflater.inflate(R.layout.item_cloud_usage, container, false);

                TextView nameTxt = itemView.findViewById(R.id.cloud_name);
                TextView totalTxt = itemView.findViewById(R.id.cloud_total);
                TextView usedTxt = itemView.findViewById(R.id.cloud_used);
                ProgressBar progress = itemView.findViewById(R.id.cloud_progress);

                long used = service.getCurrentUsedCapacity(dm.cleanupItems);
                long total = service.totalCapacity;
                int percent = (int) (total > 0 ? (used * 100 / total) : 0);

                nameTxt.setText(service.name);
                totalTxt.setText(getString(R.string.usage_total_capacity, formatSize(total)));
                usedTxt.setText(getString(R.string.usage_used_capacity, formatSize(used), percent));
                progress.setProgress(percent);

                LinearLayout breakdownContainer = itemView.findViewById(R.id.type_breakdown_container);
                addTypeBreakdown(breakdownContainer, service.name, used, dm.cleanupItems);

                container.addView(itemView);
            }
        }
    }

    private void addTypeBreakdown(LinearLayout container, String cloudName, long totalUsed, java.util.List<FileItem> allItems) {
        String[] typesToTrack = {"Audio", "Backup", "Bild", "Dokument", "Video"};
        String[] displayNames = {"Audio", "Backup", "Bilder", "Dokumente", "Videos"};

        for (int i = 0; i < typesToTrack.length; i++) {
            long typeSize = 0;
            for (FileItem item : allItems) {
                if (item.source.equals(cloudName) && item.type.equals(typesToTrack[i])) {
                    typeSize += item.sizeBytes;
                }
            }

            if (typeSize > 0) {
                int typePercent = (int) (totalUsed > 0 ? (typeSize * 100 / totalUsed) : 0);
                TextView typeTxt = new TextView(this);
                typeTxt.setTextSize(18);
                typeTxt.setText(getString(R.string.usage_type_breakdown, displayNames[i], formatSize(typeSize), typePercent));
                container.addView(typeTxt);
            }
        }
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        String[] units = getResources().getStringArray(R.array.size_units);
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        if (digitGroups >= units.length) digitGroups = units.length - 1;
        return String.format(Locale.GERMAN, "%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_trash) {
            startActivity(new Intent(this, TrashActivity.class));
        } else if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
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
