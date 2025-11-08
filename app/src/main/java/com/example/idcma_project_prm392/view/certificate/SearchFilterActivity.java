package com.example.idcma_project_prm392.view.certificate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idcma_project_prm392.R;
import com.example.idcma_project_prm392.adapter.CertificateAdapter;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.repository.CertificateRepository;
import com.example.idcma_project_prm392.utils.DateUtils;
import com.example.idcma_project_prm392.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFilterActivity extends AppCompatActivity {

    private EditText edtSearch;
    private ImageButton btnFilter, btnClearSearch;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState, tvResultCount;

    private CertificateAdapter adapter;
    private ArrayList<Certificate> allCertificates = new ArrayList<>();
    private ArrayList<Certificate> filteredCertificates = new ArrayList<>();

    private CertificateRepository certificateRepository;
    private SessionManager sessionManager;

    // Filter states
    private Set<String> selectedCategories = new HashSet<>();
    private String sortBy = "name"; // name, date, expiring
    private boolean showExpiringSoonOnly = false;
    private String archiveFilter = "active"; // "all", "active", "archived"

    // Debounce handler
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long DEBOUNCE_DELAY = 500; // 500ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tìm kiếm & Lọc");
        }

        // Initialize views
        edtSearch = findViewById(R.id.edtSearch);
        btnFilter = findViewById(R.id.btnFilter);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        recyclerView = findViewById(R.id.recyclerSearchResults);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvResultCount = findViewById(R.id.tvResultCount);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CertificateAdapter(filteredCertificates);
        recyclerView.setAdapter(adapter);

        // Initialize Repository and SessionManager
        certificateRepository = new CertificateRepository(this);
        sessionManager = new SessionManager(this);

        // Setup search with debounce
        setupSearchDebounce();

        // Setup listeners
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
        btnClearSearch.setOnClickListener(v -> clearSearch());

        // Load data
        loadCertificates();
    }

    private void setupSearchDebounce() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Schedule new search with debounce
                searchRunnable = () -> performSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCertificates() {
        progressBar.setVisibility(View.VISIBLE);

        String currentUserId = sessionManager.getUserId();

        if (currentUserId == null || !sessionManager.isLoggedIn()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load certificates from Room Database
        new Thread(() -> {
            List<Certificate> certificates = certificateRepository.getCertificatesByUserId(currentUserId);
            
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                allCertificates.clear();
                
                if (certificates != null) {
                    allCertificates.addAll(certificates);
                }

                // Apply initial filter
                applyFiltersAndSort();
            });
        }).start();
    }

    private void performSearch(String query) {
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        String searchQuery = edtSearch.getText().toString().trim().toLowerCase();
        filteredCertificates.clear();

        for (Certificate cert : allCertificates) {
            boolean matchesSearch = true;
            boolean matchesCategory = true;
            boolean matchesExpiring = true;
            boolean matchesArchived = true;

            // Search filter (name, issuer, credentialId)
            if (!searchQuery.isEmpty()) {
                matchesSearch = (cert.getName() != null && cert.getName().toLowerCase().contains(searchQuery)) ||
                               (cert.getIssuer() != null && cert.getIssuer().toLowerCase().contains(searchQuery)) ||
                               (cert.getCredentialId() != null && cert.getCredentialId().toLowerCase().contains(searchQuery));
            }

            // Category filter (using tags)
            if (!selectedCategories.isEmpty() && cert.getTags() != null) {
                matchesCategory = false;
                for (String tag : cert.getTags()) {
                    if (selectedCategories.contains(tag)) {
                        matchesCategory = true;
                        break;
                    }
                }
            }

            // Expiring soon filter
            if (showExpiringSoonOnly) {
                matchesExpiring = cert.getExpiryDate() != null && 
                                 DateUtils.isExpiringSoon(cert.getExpiryDate());
            }

            // Archived filter
            switch (archiveFilter) {
                case "active":
                    matchesArchived = !cert.isArchived(); // Chỉ hiển thị active
                    break;
                case "archived":
                    matchesArchived = cert.isArchived(); // Chỉ hiển thị archived
                    break;
                case "all":
                default:
                    matchesArchived = true; // Hiển thị tất cả
                    break;
            }

            // Add if all filters match
            if (matchesSearch && matchesCategory && matchesExpiring && matchesArchived) {
                filteredCertificates.add(cert);
            }
        }

        // Apply sorting
        sortCertificates();

        // Update UI
        updateUI();
    }

    private void sortCertificates() {
        switch (sortBy) {
            case "name":
                Collections.sort(filteredCertificates, (c1, c2) -> {
                    String name1 = c1.getName() != null ? c1.getName() : "";
                    String name2 = c2.getName() != null ? c2.getName() : "";
                    return name1.compareToIgnoreCase(name2);
                });
                break;

            case "date":
                Collections.sort(filteredCertificates, (c1, c2) -> {
                    Date d1 = DateUtils.tryParse(c1.getIssueDate());
                    Date d2 = DateUtils.tryParse(c2.getIssueDate());
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return d2.compareTo(d1); // Newest first
                });
                break;

            case "expiring":
                Collections.sort(filteredCertificates, (c1, c2) -> {
                    Date d1 = DateUtils.tryParse(c1.getExpiryDate());
                    Date d2 = DateUtils.tryParse(c2.getExpiryDate());
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return d1.compareTo(d2); // Earliest expiry first
                });
                break;
        }
    }

    private void updateUI() {
        adapter.notifyDataSetChanged();

        // Update result count
        int count = filteredCertificates.size();
        tvResultCount.setText(count + " kết quả");
        tvResultCount.setVisibility(View.VISIBLE);

        // Show/hide empty state
        if (filteredCertificates.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
            
            if (edtSearch.getText().toString().trim().isEmpty() && 
                selectedCategories.isEmpty() && 
                !showExpiringSoonOnly && 
                archiveFilter.equals("active")) {
                tvEmptyState.setText("Chưa có chứng chỉ nào");
            } else {
                tvEmptyState.setText("Không tìm thấy kết quả\nphù hợp với bộ lọc");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        bottomSheet.setContentView(sheetView);

        // Get views from bottom sheet
        ChipGroup chipGroupCategories = sheetView.findViewById(R.id.chipGroupCategories);
        RadioGroup radioGroupSort = sheetView.findViewById(R.id.radioGroupSort);
        RadioGroup radioGroupArchive = sheetView.findViewById(R.id.radioGroupArchive);
        Chip chipExpiringSoon = sheetView.findViewById(R.id.chipExpiringSoon);

        // Set current filter states
        chipExpiringSoon.setChecked(showExpiringSoonOnly);
        
        // Set current archive filter
        RadioButton radioArchiveActive = sheetView.findViewById(R.id.radioArchiveActive);
        RadioButton radioArchiveArchived = sheetView.findViewById(R.id.radioArchiveArchived);
        RadioButton radioArchiveAll = sheetView.findViewById(R.id.radioArchiveAll);
        
        if (archiveFilter.equals("active")) {
            radioArchiveActive.setChecked(true);
        } else if (archiveFilter.equals("archived")) {
            radioArchiveArchived.setChecked(true);
        } else {
            radioArchiveAll.setChecked(true);
        }

        // Set current sort
        if (sortBy.equals("name")) {
            ((RadioButton) sheetView.findViewById(R.id.radioSortName)).setChecked(true);
        } else if (sortBy.equals("date")) {
            ((RadioButton) sheetView.findViewById(R.id.radioSortDate)).setChecked(true);
        } else if (sortBy.equals("expiring")) {
            ((RadioButton) sheetView.findViewById(R.id.radioSortExpiring)).setChecked(true);
        }

        // Populate categories from existing certificates
        populateCategories(chipGroupCategories);

        // Apply button
        sheetView.findViewById(R.id.btnApplyFilter).setOnClickListener(v -> {
            // Get selected categories
            selectedCategories.clear();
            for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupCategories.getChildAt(i);
                if (chip.isChecked()) {
                    selectedCategories.add(chip.getText().toString());
                }
            }

            // Get sort option
            int selectedSortId = radioGroupSort.getCheckedRadioButtonId();
            if (selectedSortId == R.id.radioSortName) {
                sortBy = "name";
            } else if (selectedSortId == R.id.radioSortDate) {
                sortBy = "date";
            } else if (selectedSortId == R.id.radioSortExpiring) {
                sortBy = "expiring";
            }

            // Get quick filters
            showExpiringSoonOnly = chipExpiringSoon.isChecked();
            
            // Get archive filter
            int selectedArchiveId = radioGroupArchive.getCheckedRadioButtonId();
            if (selectedArchiveId == R.id.radioArchiveActive) {
                archiveFilter = "active";
            } else if (selectedArchiveId == R.id.radioArchiveArchived) {
                archiveFilter = "archived";
            } else if (selectedArchiveId == R.id.radioArchiveAll) {
                archiveFilter = "all";
            }

            // Apply filters
            applyFiltersAndSort();
            bottomSheet.dismiss();
        });

        // Reset button
        sheetView.findViewById(R.id.btnResetFilter).setOnClickListener(v -> {
            selectedCategories.clear();
            sortBy = "name";
            showExpiringSoonOnly = false;
            archiveFilter = "active"; // Mặc định chỉ hiển thị active
            applyFiltersAndSort();
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    private void populateCategories(ChipGroup chipGroup) {
        chipGroup.removeAllViews();

        // Collect all unique tags from certificates
        Set<String> allTags = new HashSet<>();
        for (Certificate cert : allCertificates) {
            if (cert.getTags() != null) {
                allTags.addAll(cert.getTags());
            }
        }

        // Add default categories if no tags
        if (allTags.isEmpty()) {
            allTags.add("IT");
            allTags.add("Business");
            allTags.add("Language");
            allTags.add("Design");
        }

        // Create chips for each category
        for (String tag : allTags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChecked(selectedCategories.contains(tag));
            chipGroup.addView(chip);
        }
    }

    private void clearSearch() {
        edtSearch.setText("");
        btnClearSearch.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
