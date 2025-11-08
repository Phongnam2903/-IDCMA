package com.example.idcma_project_prm392.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableCertificateAdapter extends RecyclerView.Adapter<SelectableCertificateAdapter.ViewHolder> {

    private List<Certificate> certificateList;

    // Dùng một Set để lưu ID (Long) của các chứng chỉ được chọn
    // Dùng Long vì Room Database (và code của bạn) dùng Long
    private Set<Long> selectedIds = new HashSet<>();

    public SelectableCertificateAdapter(List<Certificate> certificateList) {
        this.certificateList = certificateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate_selectable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate cert = certificateList.get(position);
        holder.tvCertName.setText(cert.getName());
        holder.tvCertIssuer.setText(cert.getIssuer());

        // Kiểm tra xem ID của cert này có trong Set không
        final boolean isSelected = selectedIds.contains(cert.getId());
        holder.checkBox.setChecked(isSelected);

        // Xử lý sự kiện khi người dùng click vào CẢ HÀNG
        holder.itemView.setOnClickListener(v -> {
            if (isSelected) {
                // Nếu đã chọn -> Hủy chọn
                selectedIds.remove(cert.getId());
                holder.checkBox.setChecked(false);
            } else {
                // Nếu chưa chọn -> Chọn
                selectedIds.add(Long.valueOf(cert.getId()));
                holder.checkBox.setChecked(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return certificateList.size();
    }

    // Hàm quan trọng: Trả về danh sách các ID đã được chọn
    public Set<Long> getSelectedIds() {
        return selectedIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCertName, tvCertIssuer;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCertName = itemView.findViewById(R.id.tv_cert_name);
            tvCertIssuer = itemView.findViewById(R.id.tv_cert_issuer);
            checkBox = itemView.findViewById(R.id.checkbox_select);
        }
    }
}