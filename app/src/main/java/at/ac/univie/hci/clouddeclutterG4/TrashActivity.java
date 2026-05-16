//new File Naomi

package at.ac.univie.hci.clouddeclutterG4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class TrashActivity extends AppCompatActivity {

    private LinearLayout container;
    private final List<View> itemViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        container = findViewById(R.id.trash_container);
        refreshList();

        findViewById(R.id.btn_restore).setOnClickListener(v -> restoreSelectedItems());
        findViewById(R.id.btn_delete_forever).setOnClickListener(v -> deleteSelectedItemsForever());
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void restoreSelectedItems() {
        List<FileItem> selectedItems = getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_files_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        MockDataManager dm = MockDataManager.getInstance();
        for (FileItem item : selectedItems) {
            dm.trashItems.remove(item);
            dm.cleanupItems.add(item);
        }
        refreshList();
        Toast.makeText(this, R.string.msg_restored, Toast.LENGTH_SHORT).show();
    }

    private void deleteSelectedItemsForever() {
        List<FileItem> selectedItems = getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_files_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        MockDataManager dm = MockDataManager.getInstance();
        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_deleted_forever)
                .setMessage(R.string.trash_empty_confirm_msg)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                    for (FileItem item : selectedItems) {
                        dm.trashItems.remove(item);
                    }
                    refreshList();
                    Toast.makeText(this, R.string.msg_deleted_forever, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private List<FileItem> getSelectedItems() {
        List<FileItem> selectedItems = new ArrayList<>();
        MockDataManager dm = MockDataManager.getInstance();
        for (int i = 0; i < itemViews.size(); i++) {
            CheckBox cb = itemViews.get(i).findViewById(R.id.item_checkbox);
            if (cb.isChecked()) {
                selectedItems.add(dm.trashItems.get(i));
            }
        }
        return selectedItems;
    }

    private void refreshList() {
        container.removeAllViews();
        itemViews.clear();
        LayoutInflater inflater = LayoutInflater.from(this);
        MockDataManager dm = MockDataManager.getInstance();

        for (FileItem item : dm.trashItems) {
            View itemView = inflater.inflate(R.layout.item_file, container, false);
            ImageView icon = itemView.findViewById(R.id.item_icon);
            TextView title = itemView.findViewById(R.id.item_title);
            TextView info = itemView.findViewById(R.id.item_info);

            icon.setImageResource(item.iconResId);
            title.setText(item.name);
            String infoText = String.format("%s | %s | %s", item.sizeDisplay, item.source, getString(R.string.trash_item_days_left));
            info.setText(infoText);

            itemViews.add(itemView);
            container.addView(itemView);
        }

        if (dm.trashItems.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText(R.string.msg_no_files_found);
            emptyText.setPadding(40, 60, 40, 40);
            emptyText.setTextSize(22);
            container.addView(emptyText);
        }
    }
}
