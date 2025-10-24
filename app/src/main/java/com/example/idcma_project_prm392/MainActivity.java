package com.example.idcma_project_prm392;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idcma_project_prm392.utils.FirebaseUtils;
import com.example.idcma_project_prm392.view.DashboardActivity;
import com.example.idcma_project_prm392.view.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseUtils.currentUser() != null) {
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}