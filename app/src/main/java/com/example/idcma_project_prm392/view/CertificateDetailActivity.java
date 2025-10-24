package com.example.idcma_project_prm392.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.utils.FirebaseUtils;
import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseUser;

public class CertificateDetailActivity extends AppCompatActivity {

    private String certificateId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_detail);

        certificateId = getIntent().getStringExtra("certificate_id");
        ImageView ivPreview = findViewById(R.id.ivPreview);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvIssuer = findViewById(R.id.tvDetailIssuer);
        TextView tvDates = findViewById(R.id.tvDetailDates);
        TextView tvCredential = findViewById(R.id.tvDetailCredentialId);
        Button btnShare = findViewById(R.id.btnShare);
        Button btnDelete = findViewById(R.id.btnDelete);

        FirebaseUser user = FirebaseUtils.currentUser();
        if (user == null) {
            finish();
            return;
        }

        FirebaseUtils.certificateDoc(user.getUid(), certificateId).get().addOnSuccessListener(doc -> {
            Certificate c = doc.toObject(Certificate.class);
            if (c == null) return;
            if (c.getFileUrl() != null && !c.getFileUrl().isEmpty()) {
                Picasso.get().load(c.getFileUrl()).into(ivPreview);
            }
            tvName.setText(c.getName());
            tvIssuer.setText(c.getIssuer());
            tvDates.setText("Issue: " + c.getIssueDate() + " | Expiry: " + c.getExpiryDate());
            tvCredential.setText("Credential ID: " + c.getCredentialId());
        });

        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Share flow to be implemented (API)", Toast.LENGTH_SHORT).show();
        });

        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete certificate")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUtils.deleteCertificate(user.getUid(), certificateId).addOnSuccessListener(unused -> {
                            Toast.makeText(CertificateDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show());
    }
}
