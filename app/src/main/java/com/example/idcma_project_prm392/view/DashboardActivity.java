//package com.example.idcma_project_prm392.view;
//
//import android.os.Bundle;
//import android.widget.Toast;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.idcma_project_prm392.R;
//
//import com.example.idcma_project_prm392.adapter.CertificateAdapter;
//import com.example.idcma_project_prm392.model.Certificate;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DashboardActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private CertificateAdapter adapter;
//    private List<Certificate> list = new ArrayList<>();
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);
//
//        recyclerView = findViewById(R.id.recyclerCertifications);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        db = FirebaseFirestore.getInstance();
//        loadCertifications();
//    }
//
//    private void loadCertifications() {
//        db.collection("certifications")
//                .get()
//                .addOnSuccessListener(query -> {
//                    list.clear();
//                    for (QueryDocumentSnapshot doc : query) {
//                        Certificate c = doc.toObject(Certificate.class);
//                        list.add(c);
//                    }
//                    adapter = new CertificateAdapter(this, list);
//                    recyclerView.setAdapter(adapter);
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Failed to load: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//}
