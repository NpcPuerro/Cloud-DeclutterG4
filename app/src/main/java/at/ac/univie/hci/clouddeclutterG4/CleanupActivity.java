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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class CleanupActivity extends AppCompatActivity {

    private LinearLayout container;
    private final List<View> itemViews = new ArrayList<>();
    private List<FileItem> currentDisplayedItems = new ArrayList<>();

    private List<String> selectedClouds = new ArrayList<>();
    private String filterNameContains = "";
    private List<String> filterTypes = new ArrayList<>();
    private long filterMinSize = 0;
    private long filterMaxSize = Long.MAX_VALUE;
    private long filterMinDate = 0;
    private long filterMaxDate = Long.MAX_VALUE;
    private int currentSortIdx = 0;

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

        container = findViewById(R.id.cleanup_container);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("clouds")) {
            applyScanSettings(intent);
        }

        refreshList();

        findViewById(R.id.btn_delete).setOnClickListener(v -> deleteSelectedItems());
        findViewById(R.id.btn_filter).setOnClickListener(v -> showFilterDialog());
        findViewById(R.id.btn_sort).setOnClickListener(v -> showSortDialog());
        findViewById(R.id.btn_done).setOnClickListener(v -> {
            Intent reportIntent = new Intent(this, ReportActivity.class);
            startActivity(reportIntent);
            finish();
        });
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void applyScanSettings(Intent intent) {
        selectedClouds = intent.getStringArrayListExtra("clouds");
        filterNameContains = intent.getStringExtra("nameContains");
        if (filterNameContains == null) filterNameContains = "";

        String typesStr = intent.getStringExtra("fileTypes");
        filterTypes = new ArrayList<>();
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
        List<FileItem> selectedItems = new ArrayList<>();
        for (int i = 0; i < itemViews.size(); i++) {
            CheckBox cb = itemViews.get(i).findViewById(R.id.item_checkbox);
            if (cb.isChecked()) {
                selectedItems.add(currentDisplayedItems.get(i));
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_files_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        MockDataManager dm = MockDataManager.getInstance();
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_delete)
                .setMessage(getString(R.string.trash_empty_confirm_msg)) // Reusing string for now
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    for (FileItem item : selectedItems) {
                        dm.cleanupItems.remove(item);
                        dm.trashItems.add(item);
                    }
                    refreshList();
                    Toast.makeText(this, R.string.msg_deleted_forever, Toast.LENGTH_SHORT).show(); // Reusing string
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
        if(filterMinSize > 0) etMin.setText(String.valueOf(filterMinSize / (1024*1024)));
        if(filterMaxSize < Long.MAX_VALUE) etMax.setText(String.valueOf(filterMaxSize / (1024*1024)));

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_file_types) // Reusing string
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    filterNameContains = etName.getText().toString();
                    String selectedType = spType.getSelectedItem().toString();
                    filterTypes = new ArrayList<>();
                    if (!selectedType.equals("Alle")) {
                        filterTypes.add(selectedType);
                    }
                    try {
                        String minStr = etMin.getText().toString();
                        filterMinSize = minStr.isEmpty() ? 0 : Long.parseLong(minStr) * 1024 * 1024;
                        String maxStr = etMax.getText().toString();
                        filterMaxSize = maxStr.isEmpty() ? Long.MAX_VALUE : Long.parseLong(maxStr) * 1024 * 1024;
                    } catch (Exception e) {
                        Toast.makeText(this, "Ungültige Größe", Toast.LENGTH_SHORT).show();
                    }
                    refreshList();
                })
                .setNeutralButton(R.string.clear_all, (dialog, which) -> {
                    filterNameContains = "";
                    filterTypes = new ArrayList<>();
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
        itemViews.clear();

        MockDataManager dm = MockDataManager.getInstance();
        List<FileItem> allItems = dm.cleanupItems;
        currentDisplayedItems = new ArrayList<>();

        for (FileItem item : allItems) {
            boolean matches = true;
            
            // Filter by selected clouds if provided
            if (selectedClouds != null && !selectedClouds.isEmpty()) {
                if (!selectedClouds.contains(item.source)) matches = false;
            } else {
                // Otherwise fallback to MockDataManager's default filtering (isConnected & isActive)
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
        for (FileItem item : currentDisplayedItems) {
            View itemView = inflater.inflate(R.layout.item_file, container, false);
            ImageView icon = itemView.findViewById(R.id.item_icon);
            TextView title = itemView.findViewById(R.id.item_title);
            TextView info = itemView.findViewById(R.id.item_info);

            icon.setImageResource(item.iconResId);
            title.setText(item.name);
            info.setText(String.format("%s | %s | %s", item.sizeDisplay, item.type, item.source));

            itemViews.add(itemView);
            container.addView(itemView);
        }

        if (currentDisplayedItems.isEmpty()) {
            TextView emptyText = new TextView(this);
            String text = dm.cleanupItems.isEmpty() ? getString(R.string.msg_no_files_found) : "Keine Dateien entsprechen den Filtern.";
            emptyText.setText(text);
            emptyText.setPadding(40, 60, 40, 40);
            emptyText.setTextSize(22);
            container.addView(emptyText);
        }
    }
}
