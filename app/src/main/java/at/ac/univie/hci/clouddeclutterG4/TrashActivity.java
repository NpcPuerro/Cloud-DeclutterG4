package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class TrashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private LinearLayout container;
    private final List<View> itemViews = new ArrayList<>();
    private DrawerLayout drawerLayout;

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
        for (View itemView : itemViews) {
            CheckBox cb = itemView.findViewById(R.id.item_checkbox);
            if (cb != null) {
                cb.setChecked(true);
            }
        }
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
                    }
                    refreshList();
                    Snackbar.make(findViewById(R.id.main), R.string.msg_deleted_forever, Snackbar.LENGTH_LONG).show();
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
            icon.setOnClickListener(v -> showImagePreview(item));
            title.setText(item.name);
            CharSequence infoText = android.text.TextUtils.concat(
                    item.sizeDisplay + " | " + item.source + " | ",
                    getText(R.string.trash_item_days_left)
            );
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
