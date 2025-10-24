package com.example.idcma_project_prm392;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.idcma_project_prm392.adapter.CertificateAdapter;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.view.certificate.AddCertificateActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CertificateAdapter adapter;
    private ArrayList<Certificate> certList;
    private FirebaseFirestore db;
    private FloatingActionButton fabAdd;
    private EditText edtSearch;
    private ImageButton btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerCertificates);
        fabAdd = findViewById(R.id.fabAdd);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        db = FirebaseFirestore.getInstance();
        certList = new ArrayList<>();
        adapter = new CertificateAdapter(certList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadCertificates();

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddCertificateActivity.class))
        );

        btnSearch.setOnClickListener(v -> searchCertificate(edtSearch.getText().toString()));
    }

    private void loadCertificates() {
        db.collection("certificates")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    certList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Certificate c = doc.toObject(Certificate.class);
                        certList.add(c);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void searchCertificate(String keyword) {
        ArrayList<Certificate> filtered = new ArrayList<>();
        for (Certificate c : certList) {
            if (c.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(c);
            }
        }
        adapter.updateList(filtered);
    }
}
