package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TrashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private LinearLayout container;
    private DrawerLayout drawerLayout;
    private String filterNameContains = "";
    private List<String> filterTypes = new ArrayList<>();
    private long filterMinSize = 0;
    private long filterMaxSize = Long.MAX_VALUE;
    private long filterMinDate = 0;
    private long filterMaxDate = Long.MAX_VALUE;
    private int currentSortIdx = 0;
    private List<FileItem> currentDisplayedItems = new ArrayList<>();
    private Set<FileItem> selectedFiles = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash);

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

        container = findViewById(R.id.trash_container);
        refreshList();

        findViewById(R.id.btn_restore).setOnClickListener(v -> restoreSelectedItems());
        findViewById(R.id.btn_delete_forever).setOnClickListener(v -> deleteSelectedItemsForever());
        findViewById(R.id.btn_empty_trash).setOnClickListener(v -> emptyTrash());
        findViewById(R.id.btn_filter).setOnClickListener(v -> showFilterDialog());
        findViewById(R.id.btn_sort).setOnClickListener(v -> showSortDialog());
        findViewById(R.id.toolbar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void emptyTrash() {
        MockDataManager dm = MockDataManager.getInstance();
        selectedFiles.addAll(dm.trashItems);
        deleteSelectedItemsForever();
    }

    private void restoreSelectedItems() {
        List<FileItem> selectedItems = getSelectedItems();
        if (selectedItems.isEmpty()) {
            Snackbar.make(findViewById(R.id.main), R.string.msg_no_files_selected, Snackbar.LENGTH_LONG).show();
            return;
        }
        MockDataManager dm = MockDataManager.getInstance();
        for (FileItem item : selectedItems) {
            dm.trashItems.remove(item);
            dm.cleanupItems.add(item);
        }
        selectedFiles.clear();
        refreshList();
        Snackbar.make(findViewById(R.id.main), R.string.msg_restored, Snackbar.LENGTH_LONG).show();
    }

    private void deleteSelectedItemsForever() {
        List<FileItem> selectedItems = getSelectedItems();
        if (selectedItems.isEmpty()) {
            Snackbar.make(findViewById(R.id.main), R.string.msg_no_files_selected, Snackbar.LENGTH_LONG).show();
            return;
        }
        MockDataManager dm = MockDataManager.getInstance();
        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_deleted_forever)
                .setMessage(R.string.trash_empty_confirm_msg)
                .setPositiveButton(R.string.delete_confirm, (dialog, which) -> {
                    for (FileItem item : selectedItems) {
                        dm.trashItems.remove(item);
                        selectedFiles.clear();
                    }
                    refreshList();
                    Snackbar.make(findViewById(R.id.main), R.string.msg_deleted_forever, Snackbar.LENGTH_LONG).show();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private List<FileItem> getSelectedItems() {
        return new ArrayList<>(selectedFiles);
    }

    private void refreshList() {
        container.removeAllViews();
        MockDataManager dm = MockDataManager.getInstance();

        currentDisplayedItems = new ArrayList<>();
        for (FileItem item : dm.trashItems) {
            boolean matches = true;
            if (!filterNameContains.isEmpty()) {
                String[] searchTerms = filterNameContains.split(",");
                boolean foundMatch = false;
                for (String term : searchTerms) {
                    String trimmedTerm = term.trim().toLowerCase();
                    String regexPattern = trimmedTerm.replace("*", ".*");
                    if (!regexPattern.endsWith(".*")) regexPattern = regexPattern + ".*";
                    if (item.name.toLowerCase().matches(regexPattern)) {
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) matches = false;
            }
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
            CharSequence infoText = android.text.TextUtils.concat(item.sizeDisplay + " | " + item.source + " | ", getText(R.string.trash_item_days_left));
            info.setText(infoText);

            CheckBox cb = itemView.findViewById(R.id.item_checkbox);
            cb.setChecked(selectedFiles.contains(item));
            cb.setOnClickListener(v -> {
                if (cb.isChecked()) {
                    selectedFiles.add(item);
                } else {
                    selectedFiles.remove(item);
                }
            });
            container.addView(itemView);
        }

        if (currentDisplayedItems.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText(R.string.msg_no_files_found);
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

    private void showFilterDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);
        EditText etName = dialogView.findViewById(R.id.filter_name);
        Spinner spType = dialogView.findViewById(R.id.filter_type);
        EditText etMin = dialogView.findViewById(R.id.filter_min_size);
        EditText etMax = dialogView.findViewById(R.id.filter_max_size);
        TextView dateRangeSelector = dialogView.findViewById(R.id.filter_date_range);

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

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        if (filterMinDate > 0 && filterMaxDate < Long.MAX_VALUE) {
            String startDate = sdf.format(new Date(filterMinDate));
            String endDate = sdf.format(new Date(filterMaxDate));
            dateRangeSelector.setText(startDate + " - " + endDate);
        }

        final long[] tempDates = {filterMinDate, filterMaxDate};

        dateRangeSelector.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(R.string.zeitraum_waehlen)
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                tempDates[0] = selection.first;
                tempDates[1] = selection.second;
                String startDate = sdf.format(new Date(tempDates[0]));
                String endDate = sdf.format(new Date(tempDates[1]));
                dateRangeSelector.setText(startDate + " - " + endDate);
            });

            picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
        });

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
                    filterMinDate = tempDates[0];
                    filterMaxDate = tempDates[1];
                    refreshList();
                })
                .setNeutralButton(R.string.reset_filter, (dialog, which) -> {
                    filterNameContains = "";
                    filterTypes.clear();
                    filterMinSize = 0;
                    filterMaxSize = Long.MAX_VALUE;
                    filterMinDate = 0;
                    filterMaxDate = Long.MAX_VALUE;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
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
