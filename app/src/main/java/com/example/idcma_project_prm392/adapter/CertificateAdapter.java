package com.example.idcma_project_prm392.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Certificate certificate);
    }

    private final List<Certificate> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public CertificateAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Certificate> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate_card, parent, false);
        return new CertViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CertViewHolder holder, int position) {
        Certificate c = items.get(position);
        holder.tvName.setText(c.getName());
        holder.tvIssuer.setText(c.getIssuer());
        String expiry = c.getExpiryDate();
        Date expiryDate = DateUtils.parseIsoDate(expiry);
        String expiryText = "Expiry: " + (expiry != null ? expiry : "N/A");
        if (expiryDate != null) {
            if (DateUtils.isExpired(expiryDate)) {
                expiryText += " (Expired)";
            }
        }
        holder.tvExpiry.setText(expiryText);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(c));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CertViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvIssuer;
        TextView tvExpiry;

        public CertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCertName);
            tvIssuer = itemView.findViewById(R.id.tvIssuer);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
        }
    }
}
