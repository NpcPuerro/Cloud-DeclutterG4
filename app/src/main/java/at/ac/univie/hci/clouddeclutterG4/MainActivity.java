package at.ac.univie.hci.clouddeclutterG4;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem; //new File Naomi

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat; //new File Naomi
import androidx.drawerlayout.widget.DrawerLayout; //new File Naomi

import com.google.android.material.navigation.NavigationView; //new File Naomi

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener { //new File Naomi
    private DrawerLayout drawerLayout; //new File Naomi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout); //new File Naomi
        NavigationView navigationView = findViewById(R.id.nav_view); //new File Naomi
        navigationView.setNavigationItemSelectedListener(this); //new File Naomi

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar); //new File Naomi
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_main, R.string.nav_main); //new File Naomi
        drawerLayout.addDrawerListener(toggle); //new File Naomi
        toggle.syncState(); //new File Naomi

        findViewById(R.id.btn_cleanup).setOnClickListener(v -> startActivity(new Intent(this, scanningActivity.class))); //new File Naomi
        findViewById(R.id.btn_usage).setOnClickListener(v -> startActivity(new Intent(this, UsageActivity.class))); //new File Naomi
        findViewById(R.id.btn_main_scan).setOnClickListener(v -> startActivity(new Intent(this, ScanSettingsActivity.class)));
    }

    @Override  //new File Naomi
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cloud) {
            startActivity(new Intent(this, CloudActivity.class));
        } else if (id == R.id.nav_usage) {
            startActivity(new Intent(this, UsageActivity.class));
        } else if (id == R.id.nav_cleanup) {
            startActivity(new Intent(this, scanningActivity.class));
        } else if (id == R.id.nav_trash) {
            startActivity(new Intent(this, TrashActivity.class));
        } else if (id == R.id.nav_faq) {
            startActivity(new Intent(this, FAQActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START); //new File Naomi
        return true; //new File Naomi
    }
}