package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import java.util.List;

/**
 * DAO (Data Access Object) cho Certificate operations
 */
@Dao
public interface CertificateDao {
    
    @Query("SELECT * FROM certificates")
    List<CertificateEntity> getAllCertificates();

    @Query("SELECT * FROM certificates WHERE user_id = :userId")
    List<CertificateEntity> getCertificatesByUserId(String userId);

    @Query("SELECT * FROM certificates WHERE id = :id")
    CertificateEntity getCertificateById(long id);

    @Query("SELECT * FROM certificates WHERE name LIKE '%' || :keyword || '%'")
    List<CertificateEntity> searchCertificatesByName(String keyword);

    @Query("SELECT * FROM certificates WHERE user_id = :userId AND name LIKE '%' || :keyword || '%'")
    List<CertificateEntity> searchCertificatesByUserIdAndName(String userId, String keyword);

    @Query("SELECT * FROM certificates WHERE is_archived = :isArchived")
    List<CertificateEntity> getCertificatesByArchivedStatus(boolean isArchived);

    @Query("SELECT * FROM certificates WHERE user_id = :userId AND is_archived = :isArchived")
    List<CertificateEntity> getCertificatesByUserIdAndArchivedStatus(String userId, boolean isArchived);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCertificate(CertificateEntity certificate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCertificates(List<CertificateEntity> certificates);

    @Update
    void updateCertificate(CertificateEntity certificate);

    @Delete
    void deleteCertificate(CertificateEntity certificate);

    @Query("DELETE FROM certificates WHERE id = :id")
    void deleteCertificateById(long id);

    @Query("DELETE FROM certificates WHERE user_id = :userId")
    void deleteCertificatesByUserId(String userId);
}

