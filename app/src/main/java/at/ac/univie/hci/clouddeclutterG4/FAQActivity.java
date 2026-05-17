package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import at.ac.univie.hci.clouddeclutterG4.ui.faq.ContactFragment;
import at.ac.univie.hci.clouddeclutterG4.ui.faq.FAQCategoryAdapter;
import at.ac.univie.hci.clouddeclutterG4.ui.faq.FAQCategory;
import at.ac.univie.hci.clouddeclutterG4.ui.faq.FAQFragment;
import at.ac.univie.hci.clouddeclutterG4.ui.login.LoginFragment;
import at.ac.univie.hci.clouddeclutterG4.ui.login.SignupFragment;

public class FAQActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String FAQ_FILENAME = "faqdata.json";
    private RecyclerView rvFAQCategoryList;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_main, R.string.nav_main);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container_faq), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_faq, FAQFragment.newInstance())
                    .commitNow();
        }
        findViewById(R.id.toolbar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    public void showContact() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container_faq,
                        new ContactFragment()
                )
                .addToBackStack(null)
                .commit();
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
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        } else if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}