package com.example.idcma_project_prm392.dto;

import androidx.room.Embedded;

import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;

public class CertificateReportDTO {
    @Embedded(prefix = "c_")
    public CertificateEntity certificate;

    @Embedded(prefix = "sr_")
    public ShareRecordEntity shareRecord;
}
