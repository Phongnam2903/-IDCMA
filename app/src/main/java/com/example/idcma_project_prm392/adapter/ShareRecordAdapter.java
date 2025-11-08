package com.example.idcma_project_prm392.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import com.example.idcma_project_prm392.dto.CertificateReportDTO;

import java.util.List;

public class ShareRecordAdapter extends RecyclerView.Adapter<ShareRecordAdapter.ViewHolder> {
    // Nếu bạn bắt buộc phải dùng tên CertificateRecordAdapter, hãy đổi tên class này.

    private List<CertificateReportDTO> recordList;
    private Context context;

    public ShareRecordAdapter(List<CertificateReportDTO> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // SỬ DỤNG layout item_certificate_record
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_certificate_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CertificateReportDTO record = recordList.get(position);

        String recipient = record.shareRecord.getRecipientEmail();
        holder.tvRecipientEmail.setText("Chia sẻ với: " +
                (!TextUtils.isEmpty(recipient) ? recipient : "Người dùng ẩn danh"));

        holder.tvCertificateName.setText("Tên Chứng chỉ: #" + record.certificate.getName());

        holder.tvIssuer.setText("Tổ chức cấp: " + record.certificate.getIssuer());

        String shareDate = record.shareRecord.getShareDate();
        holder.tvShareDate.setText("Ngày chia sẻ: " +
                (!TextUtils.isEmpty(shareDate) ? shareDate : "N/A"));

        String status = record.shareRecord.getStatus();
        holder.tvStatus.setText("Trạng thái: " + (status != null ? status : "N/A"));

        int statusColor;
        if ("Active".equalsIgnoreCase(status)) {
            statusColor = ContextCompat.getColor(context, android.R.color.holo_green_dark);
        } else if ("Expired".equalsIgnoreCase(status) || "Revoked".equalsIgnoreCase(status)) {
            statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
        } else {
            statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);
        }
        holder.tvStatus.setTextColor(statusColor);

        String link = record.shareRecord.getLink();
        if (!TextUtils.isEmpty(link)) {
            holder.tvLink.setText("Liên kết: [Nhấn để xem]");
            holder.tvLink.setVisibility(View.VISIBLE);

            holder.tvLink.setOnClickListener(v -> {
                // TODO: Triển khai logic mở link (ví dụ: dùng Intent) hoặc copy link
                Toast.makeText(context, "Link chia sẻ: " + link, Toast.LENGTH_LONG).show();
            });
        } else {
            holder.tvLink.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Đã chọn bản ghi ID: " + record.shareRecord.getId(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void updateList(List<CertificateReportDTO> newList) {
        this.recordList = newList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertificateName, tvIssuer, tvRecipientEmail, tvShareDate, tvStatus, tvLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIssuer = itemView.findViewById(R.id.tvIssuer);
            tvCertificateName = itemView.findViewById(R.id.tvCertificateName);
            tvRecipientEmail = itemView.findViewById(R.id.tvRecipientEmail);
            tvShareDate = itemView.findViewById(R.id.tvShareDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvLink = itemView.findViewById(R.id.tvLink);
        }
    }
}