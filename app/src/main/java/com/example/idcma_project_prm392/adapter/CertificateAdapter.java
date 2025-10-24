package com.example.idcma_project_prm392.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.view.CertificateDetailActivity;
import com.example.idcma_project_prm392.utils.DateUtils;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {

    private final Context context;
    private final List<Certificate> list;

    public CertificateAdapter(Context context, List<Certificate> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_certification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate cert = list.get(position);

        holder.txtName.setText(cert.getName());
        holder.txtIssuer.setText("Issuer: " + cert.getIssuer());

        // Xử lý ngày hết hạn
        String expiryDate = cert.getExpiryDate();
        if (expiryDate != null && !expiryDate.isEmpty()) {
            holder.txtDate.setText("Expires: " + DateUtils.formatDate(expiryDate));

            if (DateUtils.isExpiringSoon(expiryDate)) {
                holder.txtDate.setTextColor(Color.RED);
            } else {
                holder.txtDate.setTextColor(Color.BLACK);
            }
        } else {
            holder.txtDate.setText("No expiry date");
            holder.txtDate.setTextColor(Color.GRAY);
        }

        // Chuyển sang màn chi tiết
        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, CertificateDetailActivity.class);
            intent.putExtra("certId", cert.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView txtName, txtIssuer, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardCert);
            txtName = itemView.findViewById(R.id.txtCertName);
            txtIssuer = itemView.findViewById(R.id.txtIssuer);
            txtDate = itemView.findViewById(R.id.txtExpiryDate);
        }
    }
}
