package com.example.idcma_project_prm392.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.utils.DateUtils;
import com.example.idcma_project_prm392.view.certificate.CertificateDetailActivity;

import java.util.ArrayList;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {

    private ArrayList<Certificate> certList;
    private Context context;

    public CertificateAdapter(ArrayList<Certificate> certList) {
        this.certList = certList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_certificate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate c = certList.get(position);
        
        // Set basic info
        holder.tvName.setText(c.getName());
        holder.tvIssuer.setText("Tổ chức cấp: " + (c.getIssuer() != null ? c.getIssuer() : "N/A"));
        holder.tvIssueDate.setText("Ngày cấp: " + (c.getIssueDate() != null ? c.getIssueDate() : "N/A"));
        
        // Set expiry date
        String expiryDate = c.getExpiryDate();
        if (!TextUtils.isEmpty(expiryDate)) {
            holder.tvExpiryDate.setText("Hết hạn: " + expiryDate);
            holder.tvExpiryDate.setVisibility(View.VISIBLE);
            
            // Check if expiring soon (within 7 days)
            boolean isExpiringSoon = DateUtils.isExpiringSoon(expiryDate);
            
            if (isExpiringSoon) {
                // Show visual indicators
                holder.expiringSoonIndicator.setVisibility(View.VISIBLE);
                holder.tvExpiringBadge.setVisibility(View.VISIBLE);
                
                // Change expiry date text color to red
                holder.tvExpiryDate.setTextColor(0xFFFF0000); // Red color
            } else {
                // Hide indicators
                holder.expiringSoonIndicator.setVisibility(View.GONE);
                holder.tvExpiringBadge.setVisibility(View.GONE);
                
                // Reset to default color
                holder.tvExpiryDate.setTextColor(0xFF777777); // Gray color
            }
        } else {
            holder.tvExpiryDate.setText("Hết hạn: Không có thông tin");
            holder.tvExpiryDate.setVisibility(View.VISIBLE);
            holder.expiringSoonIndicator.setVisibility(View.GONE);
            holder.tvExpiringBadge.setVisibility(View.GONE);
        }
        
        // Set click listener to open detail activity
        holder.itemView.setOnClickListener(v -> {
            if (c.getId() != null && !c.getId().isEmpty()) {
                Intent intent = new Intent(context, CertificateDetailActivity.class);
                intent.putExtra("CERTIFICATE_ID", c.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return certList.size();
    }

    public void updateList(ArrayList<Certificate> newList) {
        this.certList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIssuer, tvIssueDate, tvExpiryDate, tvExpiringBadge;
        View expiringSoonIndicator;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCertName);
            tvIssuer = itemView.findViewById(R.id.tvIssuer);
            tvIssueDate = itemView.findViewById(R.id.tvIssueDate);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvExpiringBadge = itemView.findViewById(R.id.tvExpiringBadge);
            expiringSoonIndicator = itemView.findViewById(R.id.expiringSoonIndicator);
        }
    }
}
