package at.ac.univie.hci.clouddeclutterG4.ui.faq;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import at.ac.univie.hci.clouddeclutterG4.FAQActivity;
import at.ac.univie.hci.clouddeclutterG4.R;

public class FAQFragment extends Fragment {
    private static final String FAQ_FILENAME = "faqdata.json";
    private RecyclerView rvFAQCategoryList;
    private Button btFaqContact;
    public static FAQFragment newInstance() {
        return new FAQFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        rvFAQCategoryList = view.findViewById(R.id.rvFAQCategoryList);
        btFaqContact = view.findViewById(R.id.btFaqContact);

        FAQCategory[] arrFAQ = loadFAQ();
        assert arrFAQ != null; // TODO: maybe proper error handling is better
        List<FAQCategory> faqdata = Arrays.stream(arrFAQ).toList();

        rvFAQCategoryList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        FAQCategoryAdapter adapter = new FAQCategoryAdapter(faqdata);
        rvFAQCategoryList.setAdapter(adapter);

        btFaqContact.setOnClickListener(this::contact);

        return view;
    }

    private void contact(View view) {
        FAQActivity activity = (FAQActivity) requireActivity();
        activity.showContact();
    }

    private FAQCategory[] loadFAQ() {
        FAQCategory[] arr = null;
        FAQActivity activity = (FAQActivity) requireActivity();

        try {
            InputStream stream = activity.getAssets().open(FAQ_FILENAME, AssetManager.ACCESS_BUFFER);
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            arr = new Gson().fromJson(reader, FAQCategory[].class);
        }
        catch (IOException e) {
            Log.e("FAQ", e.toString());
        }

        return arr;
    }
}
