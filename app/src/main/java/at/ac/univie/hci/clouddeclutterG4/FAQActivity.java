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

import at.ac.univie.hci.clouddeclutterG4.ui.faq.FAQCategoryAdapter;
import at.ac.univie.hci.clouddeclutterG4.ui.faq.FAQCategory;

public class FAQActivity extends AppCompatActivity {
    private static final String FAQ_FILENAME = "faqdata.json";
    private RecyclerView rvFAQCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faq);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        rvFAQCategoryList = findViewById(R.id.rvFAQCategoryList);

        FAQCategory[] arrFAQ = loadFAQ();
        assert arrFAQ != null; // TODO: maybe proper error handling is better
        List<FAQCategory> faqdata = Arrays.stream(arrFAQ).toList();

        rvFAQCategoryList.setLayoutManager(new LinearLayoutManager(this));
        FAQCategoryAdapter adapter = new FAQCategoryAdapter(faqdata);
        rvFAQCategoryList.setAdapter(adapter);
    }

    private FAQCategory[] loadFAQ() {
        FAQCategory[] arr = null;

        try {
            InputStream stream = getAssets().open(FAQ_FILENAME, AssetManager.ACCESS_BUFFER);
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            arr = new Gson().fromJson(reader, FAQCategory[].class);
        }
        catch (IOException e) {
            Log.e("FAQ", e.toString());
        }

        return arr;
    }
}