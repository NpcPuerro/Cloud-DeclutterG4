package at.ac.univie.hci.clouddeclutterG4.ui.faq;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import at.ac.univie.hci.clouddeclutterG4.R;

public class FAQQuestionAdapter extends RecyclerView.Adapter<FAQQuestionAdapter.VH> {
    private List<FAQQuestion> questions;

    public FAQQuestionAdapter(@NonNull List<FAQQuestion> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_question, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FAQQuestion question = questions.get(position);

        holder.txQuestion.setText(question.getQuestion());
        holder.txAnswer.setText(question.getAnswer());

        holder.txAnswer.setVisibility(question.isExpanded() ? View.VISIBLE : View.GONE);
        holder.ivFAQExpand.setRotation(question.isExpanded() ? 180f : 0f);

        holder.header.setOnClickListener(v -> {
            question.toggleExpand();
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
    static class VH extends RecyclerView.ViewHolder {
        TextView txQuestion;
        TextView txAnswer;
        ImageView ivFAQExpand;
        View header;
        VH(View v) {
            super(v);
            this.txQuestion = v.findViewById(R.id.txFAQQuestion);
            this.txAnswer = v.findViewById(R.id.txFAQAnswer);
            this.ivFAQExpand = v.findViewById(R.id.ivFAQExpand);
            this.header = v.findViewById(R.id.layFAQQue);
        }
    }
}
