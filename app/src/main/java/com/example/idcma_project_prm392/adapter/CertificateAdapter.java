package com.example.idcma_project_prm392.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;

import java.util.ArrayList;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder> {

    private ArrayList<Certificate> certList;

    public CertificateAdapter(ArrayList<Certificate> certList) {
        this.certList = certList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_certificate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate c = certList.get(position);
        holder.tvName.setText(c.getName());
        holder.tvIssuer.setText("Tổ chức cấp: " + c.getIssuer());
        holder.tvDate.setText("Ngày cấp: " + c.getIssueDate());
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
        TextView tvName, tvIssuer, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCertName);
            tvIssuer = itemView.findViewById(R.id.tvIssuer);
            tvDate = itemView.findViewById(R.id.tvIssueDate);
        }
    }
}
