package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import com.example.idcma_project_prm392.dto.CertificateReportDTO;

import java.util.List;

@Dao
public interface ShareRecordDao {

    @Insert
    void insert(ShareRecordEntity shareRecord);

    @Update
    void update(ShareRecordEntity shareRecord);

    @Delete
    void delete(ShareRecordEntity shareRecord);

    @Query("SELECT c.name AS c_name, c.issuer AS c_issuer, sr.recipient_email AS sr_recipient_email, sr.share_date AS sr_share_date, sr.status AS sr_status, sr.link AS sr_link FROM share_record sr, certificates c WHERE sr.user_id = :userId AND sr.certificateId = c.credential_id")
    List<CertificateReportDTO> getAllByUser(String userId);

    @Query("SELECT * FROM share_record WHERE id = :id LIMIT 1")
    ShareRecordEntity getById(long id);

    @Query("DELETE FROM share_record")
    void clearAll();
}
