package at.ac.univie.hci.clouddeclutterG4.ui.faq;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import at.ac.univie.hci.clouddeclutterG4.FAQActivity;
import at.ac.univie.hci.clouddeclutterG4.R;

public class ContactFragment extends Fragment {
    private Button btFaqContactSend;
    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        FAQActivity activity = (FAQActivity) requireActivity();
        activity.setToolbarTitle(R.string.faqContact);

        btFaqContactSend = view.findViewById(R.id.btFaqContactSend);
        btFaqContactSend.setOnClickListener(this::submit);

        return view;
    }

    private void submit(View view) {
        Snackbar.make(
                view,
                R.string.faqSendConfigmMsg,
                Snackbar.LENGTH_LONG
        ).show();
    }
}
