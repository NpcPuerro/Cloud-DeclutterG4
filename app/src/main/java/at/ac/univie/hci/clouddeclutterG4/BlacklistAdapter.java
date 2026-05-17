package at.ac.univie.hci.clouddeclutterG4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.VH> {
    private List<String> filters;

    public BlacklistAdapter(List<String> filters) {
        this.filters = filters;
    }

    @NonNull
    @Override
    public BlacklistAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blacklist_filter_item, parent, false);
        return new BlacklistAdapter.VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistAdapter.VH holder, int position) {
        String current_filter = filters.get(position);

        holder.txBLFilter.setText(current_filter);
        holder.ivBLDelFilterBtn.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                filters.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, filters.size());
            }
        });
    }
    @Override
    public int getItemCount() {
        return filters.size();
    }
    static class VH extends RecyclerView.ViewHolder {

        TextView txBLFilter;
        ImageView ivBLDelFilterBtn;
        VH(View v){
            super(v);
            this.txBLFilter = v.findViewById(R.id.txBLFilter);
            this.ivBLDelFilterBtn = v.findViewById(R.id.ivBLDelFilterBtn);
        }

    }
}
