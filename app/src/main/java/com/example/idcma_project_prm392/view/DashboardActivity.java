package com.example.idcma_project_prm392.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.adapter.CertificateAdapter;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private CertificateAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        RecyclerView rv = findViewById(R.id.rvCertificates);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CertificateAdapter(certificate -> {
            Intent i = new Intent(DashboardActivity.this, CertificateDetailActivity.class);
            i.putExtra("certificate_id", certificate.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        loadCertificates();
    }

    private void loadCertificates() {
        FirebaseUser user = FirebaseUtils.currentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        FirebaseUtils.fetchCertificates(user.getUid()).addOnSuccessListener(snap -> {
            List<Certificate> list = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snap) {
                Certificate c = doc.toObject(Certificate.class);
                c.setId(doc.getId());
                list.add(c);
            }
            adapter.submitList(list);
        });
    }
}
