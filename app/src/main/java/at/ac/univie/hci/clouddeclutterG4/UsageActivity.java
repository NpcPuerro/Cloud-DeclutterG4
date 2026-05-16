package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class UsageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_add_cloud).setOnClickListener(v -> {
            startActivity(new Intent(this, CloudActivity.class));
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        refreshUsage();
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
}
