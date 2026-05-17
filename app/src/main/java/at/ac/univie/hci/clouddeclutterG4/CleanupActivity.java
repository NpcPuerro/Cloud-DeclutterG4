package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

public class CleanupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private LinearLayout container;
    private List<FileItem> currentDisplayedItems = new ArrayList<>();
    private Set<FileItem> selectedFiles = new HashSet<>();
    private List<String> selectedClouds = new ArrayList<>();
    private String filterNameContains = "";
    private List<String> filterTypes = new ArrayList<>();
    private long filterMinSize = 0;
    private long filterMaxSize = Long.MAX_VALUE;
    private long filterMinDate = 0;
    private long filterMaxDate = Long.MAX_VALUE;
    private int currentSortIdx = 0;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cleanup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_main, R.string.nav_main);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        container = findViewById(R.id.cleanup_container);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("clouds")) {
            applyScanSettings(intent);
        }

        refreshList();

        findViewById(R.id.btn_select_all).setOnClickListener(v -> selectAllItems());
        findViewById(R.id.btn_delete).setOnClickListener(v -> deleteSelectedItems());
        findViewById(R.id.btn_filter).setOnClickListener(v -> showFilterDialog());
        findViewById(R.id.btn_sort).setOnClickListener(v -> showSortDialog());
        findViewById(R.id.btn_done).setOnClickListener(v -> {
            Intent reportIntent = new Intent(this, ReportActivity.class);
            startActivity(reportIntent);
            finish();
        });
        findViewById(R.id.toolbar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void selectAllItems() {
        if (currentDisplayedItems.isEmpty()) return;

        boolean allVisibleChecked = true;
        for (FileItem item : currentDisplayedItems) {
            if (!selectedFiles.contains(item)) {
                allVisibleChecked = false;
                break;
            }
        }

        if (allVisibleChecked) {
            selectedFiles.removeAll(currentDisplayedItems);
        } else {
            selectedFiles.addAll(currentDisplayedItems);
        }

        refreshList();
    }

    private void updateSelectAllButtonText() {
        android.widget.Button btn = findViewById(R.id.btn_select_all);
        if (btn == null || currentDisplayedItems.isEmpty()) return;

        boolean allVisibleChecked = true;
        for (FileItem item : currentDisplayedItems) {
            if (!selectedFiles.contains(item)) {
                allVisibleChecked = false;
                break;
            }
        }

        btn.setText(allVisibleChecked ? R.string.btn_deselect_all : R.string.btn_select_all);
    }

    private void applyScanSettings(Intent intent) {
        selectedClouds = intent.getStringArrayListExtra("clouds");
        filterNameContains = intent.getStringExtra("nameContains");
        if (filterNameContains == null) filterNameContains = "";

        String typesStr = intent.getStringExtra("fileTypes");
        filterTypes.clear();
        if (typesStr != null && !typesStr.equals(getString(R.string.select_all))) {
            String[] split = typesStr.split(", ");
            for (String s : split) {
                filterTypes.add(mapToInternalType(s));
            }
        }

        String minSizeStr = intent.getStringExtra("minSize");
        String minUnit = intent.getStringExtra("minUnit");
        long min = convertToBytes(minSizeStr, minUnit);
        if (min != -1) filterMinSize = min;

        String maxSizeStr = intent.getStringExtra("maxSize");
        String maxUnit = intent.getStringExtra("maxUnit");
        long max = convertToBytes(maxSizeStr, maxUnit);
        if (max != -1) filterMaxSize = max;

        filterMinDate = intent.getLongExtra("startDateMillis", 0);
        filterMaxDate = intent.getLongExtra("endDateMillis", Long.MAX_VALUE);
        if (filterMaxDate == 0) filterMaxDate = Long.MAX_VALUE;
    }

    private String mapToInternalType(String displayName) {
        if (displayName.equals("Bilder")) return "Bild";
        if (displayName.equals("Videos")) return "Video";
        if (displayName.equals("Dokumente")) return "Dokument";
        if (displayName.equals("Audio")) return "Audio";
        if (displayName.equals("Archive")) return "Backup";
        return displayName;
    }

    private long convertToBytes(String valueStr, String unit) {
        if (valueStr == null || valueStr.isEmpty()) return -1;
        try {
            long val = Long.parseLong(valueStr);
            if (unit == null) return val;
            switch (unit) {
                case "kB": return val * 1024;
                case "MB": return val * 1024 * 1024;
                case "GB": return val * 1024 * 1024 * 1024;
                default: return val;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void deleteSelectedItems() {
        if (selectedFiles.isEmpty()) {
            Snackbar.make(findViewById(R.id.main), R.string.msg_no_files_selected, Snackbar.LENGTH_SHORT).show();
            return;
        }

        MockDataManager dm = MockDataManager.getInstance();
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_delete)
                .setMessage(getString(R.string.msg_confirm_move_to_trash, selectedFiles.size()))
                .setPositiveButton(R.string.delete_confirm, (dialog, which) -> {
                    for (FileItem item : selectedFiles) {
                        dm.cleanupItems.remove(item);
                        dm.trashItems.add(item);
                    }
                    selectedFiles.clear();
                    refreshList();
                    Snackbar.make(findViewById(R.id.main), R.string.msg_moved_to_trash, Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void showFilterDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        EditText etName = dialogView.findViewById(R.id.filter_name);
        Spinner spType = dialogView.findViewById(R.id.filter_type);
        EditText etMin = dialogView.findViewById(R.id.filter_min_size);
        EditText etMax = dialogView.findViewById(R.id.filter_max_size);

        final long MB = 1024 * 1024;
        String[] types = {"Alle", "Bild", "Video", "Dokument", "Backup", "Audio"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        etName.setText(filterNameContains);
        if (!filterTypes.isEmpty()) {
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(filterTypes.get(0))) spType.setSelection(i);
            }
        }
        if(filterMinSize > 0) etMin.setText(String.valueOf(filterMinSize / MB));
        if(filterMaxSize < Long.MAX_VALUE) etMax.setText(String.valueOf(filterMaxSize / MB));

        new AlertDialog.Builder(this)
                .setTitle(R.string.filter_title)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    filterNameContains = etName.getText().toString();
                    String selectedType = spType.getSelectedItem().toString();
                    filterTypes.clear();
                    if (!selectedType.equals("Alle")) {
                        filterTypes.add(selectedType);
                    }
                    try {
                        String minStr = etMin.getText().toString();
                        filterMinSize = minStr.isEmpty() ? 0 : Long.parseLong(minStr) * MB;
                        String maxStr = etMax.getText().toString();
                        filterMaxSize = maxStr.isEmpty() ? Long.MAX_VALUE : Long.parseLong(maxStr) * MB;
                    } catch (Exception e) {
                        Snackbar.make(findViewById(R.id.main), "Ungültige Größe", Snackbar.LENGTH_SHORT).show();
                    }
                    refreshList();
                })
                .setNegativeButton(R.string.btn_reset, (dialog, which) -> {
                    filterNameContains = "";
                    filterTypes.clear();
                    filterMinSize = 0;
                    filterMaxSize = Long.MAX_VALUE;
                    refreshList();
                })
                .show();
    }

    private void showSortDialog() {
        String[] options = {"Name A-Z", "Name Z-A", "Größe (Groß zuerst)", "Größe (Klein zuerst)", "Datum (Neu zuerst)", "Datum (Alt zuerst)"};
        new AlertDialog.Builder(this)
                .setTitle("Sortieren")
                .setSingleChoiceItems(options, currentSortIdx, (dialog, which) -> {
                    currentSortIdx = which;
                    refreshList();
                    dialog.dismiss();
                })
                .show();
    }

    private void refreshList() {
        container.removeAllViews();

        MockDataManager dm = MockDataManager.getInstance();
        List<Pattern> patterns = dm.blacklistFilters.stream()
                .map(MockDataManager::convertPattern)
                .collect(Collectors.toList());
        List<FileItem> allItems = dm.cleanupItems.stream()
                .filter(item -> patterns.stream()
                        .noneMatch(pattern -> pattern.matcher(item.name).matches()))
                .collect(Collectors.toList());
        currentDisplayedItems = new ArrayList<>();

        for (FileItem item : allItems) {
            boolean matches = true;
            if (selectedClouds != null && !selectedClouds.isEmpty()) {
                if (!selectedClouds.contains(item.source)) matches = false;
            } else {
                MockDataManager.CloudService service = dm.cloudServices.get(item.source);
                if (service == null || !service.isConnected || !service.isActive) matches = false;
            }

            if (matches && !filterNameContains.isEmpty() && !item.name.toLowerCase().contains(filterNameContains.toLowerCase())) matches = false;
            if (matches && !filterTypes.isEmpty() && !filterTypes.contains(item.type)) matches = false;
            if (matches && (item.sizeBytes < filterMinSize || item.sizeBytes > filterMaxSize)) matches = false;
            if (matches && (item.dateMillis < filterMinDate || item.dateMillis > filterMaxDate)) matches = false;

            if (matches) currentDisplayedItems.add(item);
        }

        currentDisplayedItems.sort((f1, f2) -> {
            switch (currentSortIdx) {
                case 0: return f1.name.compareToIgnoreCase(f2.name);
                case 1: return f2.name.compareToIgnoreCase(f1.name);
                case 2: return Long.compare(f2.sizeBytes, f1.sizeBytes);
                case 3: return Long.compare(f1.sizeBytes, f2.sizeBytes);
                case 4: return Long.compare(f2.dateMillis, f1.dateMillis);
                case 5: return Long.compare(f1.dateMillis, f2.dateMillis);
                default: return 0;
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        for (FileItem item : currentDisplayedItems) {
            View itemView = inflater.inflate(R.layout.item_file, container, false);
            ImageView icon = itemView.findViewById(R.id.item_icon);
            TextView title = itemView.findViewById(R.id.item_title);
            TextView info = itemView.findViewById(R.id.item_info);

            icon.setImageResource(item.iconResId);
            icon.setOnClickListener(v -> showImagePreview(item));
            title.setText(item.name);
            String dateStr = sdf.format(new Date(item.dateMillis));
            info.setText(String.format("%s | %s | %s | %s", item.sizeDisplay, item.type, item.source, dateStr));

            CheckBox cb = itemView.findViewById(R.id.item_checkbox);
            cb.setChecked(selectedFiles.contains(item));
            cb.setOnClickListener(v -> {
                if (cb.isChecked()) {
                    selectedFiles.add(item);
                } else {
                    selectedFiles.remove(item);
                }
                updateSelectAllButtonText();
            });

            container.addView(itemView);
        }
        updateSelectAllButtonText();

        if (currentDisplayedItems.isEmpty()) {
            TextView emptyText = new TextView(this);
            String text = dm.cleanupItems.isEmpty() ? getString(R.string.msg_no_files_found) : "Keine Dateien entsprechen den Filtern.";
            emptyText.setText(text);
            emptyText.setPadding(40, 60, 40, 40);
            emptyText.setTextSize(22);
            container.addView(emptyText);
        }
    }

    private void showImagePreview(FileItem item) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(item.iconResId);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(32, 32, 32, 32);

        new AlertDialog.Builder(this)
                .setTitle(item.name)
                .setView(imageView)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
        } else if (id == R.id.nav_usage) {
            startActivity(new Intent(this, UsageActivity.class));
        } else if (id == R.id.nav_trash) {
            startActivity(new Intent(this, TrashActivity.class));
        } else if (id == R.id.nav_main) {
            startActivity(new Intent(this, MainActivity.class));
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
