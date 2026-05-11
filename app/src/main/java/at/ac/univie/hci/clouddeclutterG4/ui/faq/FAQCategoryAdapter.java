package at.ac.univie.hci.clouddeclutterG4.ui.faq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import at.ac.univie.hci.clouddeclutterG4.R;

public class FAQCategoryAdapter extends RecyclerView.Adapter<FAQCategoryAdapter.VH> {
    private List<FAQCategory> categories;

    public FAQCategoryAdapter(List<FAQCategory> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FAQCategory category = categories.get(position);

        DividerItemDecoration divider =
                new DividerItemDecoration(holder.rvQuestions.getContext(),
                        DividerItemDecoration.VERTICAL);

        holder.rvQuestions.addItemDecoration(divider);

        holder.rvQuestions.setLayoutManager(new LinearLayoutManager(holder.rvQuestions.getContext()));
        FAQQuestionAdapter adapter = new FAQQuestionAdapter(category.getQuestions());
        holder.rvQuestions.setAdapter(adapter);

        holder.txCategoryTitle.setText(category.getTitle());
    }
    @Override
    public int getItemCount() {
        return categories.size();
    }
    static class VH extends RecyclerView.ViewHolder {

        TextView txCategoryTitle;
        RecyclerView rvQuestions;
        VH(View v){
            super(v);
            this.txCategoryTitle = v.findViewById(R.id.txFAQCategory);
            this.rvQuestions = v.findViewById(R.id.rvFAQQuestionList);
        }

    }
}
