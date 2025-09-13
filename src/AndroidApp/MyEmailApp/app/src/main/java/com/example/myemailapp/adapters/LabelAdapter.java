package com.example.myemailapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myemailapp.R;
import com.example.myemailapp.data.database.entity.Label;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {

    private List<Label> labels;
    private LabelClickListener listener;

    public interface LabelClickListener {
        void onEditClick(Label label);
        void onDeleteClick(Label label);

        void onSearchClick(Label label);
    }

    public LabelAdapter(List<Label> labels, LabelClickListener listener) {
        this.labels = labels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_label, parent, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        Label label = labels.get(position);
        holder.bind(label);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void updateLabels(List<Label> newLabels) {
        this.labels = newLabels;
        notifyDataSetChanged();
    }

    class LabelViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView labelName;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        private ImageButton btnSearch;

        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_label);
            labelName = itemView.findViewById(R.id.text_view_label_name);
            btnEdit = itemView.findViewById(R.id.btn_edit_label);
            btnDelete = itemView.findViewById(R.id.btn_delete_label);
            btnSearch = itemView.findViewById(R.id.btn_search_label);
        }

        public void bind(Label label) {
            labelName.setText(label.getName());

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(label);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(label);
                }
            });

            btnSearch.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSearchClick(label);
                }
            });

            // Optional: Add some visual feedback
            cardView.setOnClickListener(v -> {
                // haha
            });
        }
    }
}