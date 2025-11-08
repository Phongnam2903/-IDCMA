package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import java.util.List;

/**
 * DAO (Data Access Object) cho ShareRecord operations
 */
@Dao
public interface ShareRecordDao {

    @Query("SELECT * FROM share_records WHERE user_id = :userId ORDER BY share_date DESC")
    List<ShareRecordEntity> getShareRecordsByUserId(String userId);

    @Query("SELECT * FROM share_records WHERE certificate_id = :certificateId ORDER BY share_date DESC")
    List<ShareRecordEntity> getShareRecordsByCertificateId(String certificateId);

    @Query("SELECT * FROM share_records WHERE share_token = :token")
    ShareRecordEntity getShareRecordByToken(String token);

    @Query("SELECT * FROM share_records WHERE user_id = :userId AND certificate_id = :certificateId ORDER BY share_date DESC")
    List<ShareRecordEntity> getShareRecordsByUserIdAndCertificateId(String userId, String certificateId);

    @Query("SELECT * FROM share_records WHERE is_expired = 0 AND status = 'Active'")
    List<ShareRecordEntity> getActiveShareRecords();

    @Query("SELECT * FROM share_records WHERE user_id = :userId AND is_expired = 0 AND status = 'Active' ORDER BY share_date DESC")
    List<ShareRecordEntity> getActiveShareRecordsByUserId(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertShareRecord(ShareRecordEntity shareRecord);

    @Update
    void updateShareRecord(ShareRecordEntity shareRecord);

    @Delete
    void deleteShareRecord(ShareRecordEntity shareRecord);

    @Query("DELETE FROM share_records WHERE id = :id")
    void deleteShareRecordById(long id);

    @Query("UPDATE share_records SET is_expired = 1, status = 'Expired' WHERE expiration_date < :currentDate AND is_expired = 0")
    void markExpiredRecords(String currentDate);
}

