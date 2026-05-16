package at.ac.univie.hci.clouddeclutterG4;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FAQActivity extends AppCompatActivity {
    private static final String FAQ_FILENAME = "faqdata.json";
    private RecyclerView rvFAQCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);
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
}