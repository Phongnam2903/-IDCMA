package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;

import java.util.List;

/**
 * DAO (Data Access Object) cho ShareRecord operations
 */
@Dao
public interface ShareRecordDao {

    @Insert
    void insert(ShareRecordEntity shareRecord);
    @Query("SELECT * FROM share_record WHERE user_id = :userId ORDER BY share_date DESC")
    List<ShareRecordEntity> getShareRecordsByUserId(String userId);

    @Query("SELECT * FROM share_record WHERE certificate_id = :certificateId ORDER BY share_date DESC")
    List<ShareRecordEntity> getShareRecordsByCertificateId(String certificateId);

    @Query("SELECT * FROM share_record WHERE share_token = :token")
    ShareRecordEntity getShareRecordByToken(String token);

    @Query("SELECT * FROM share_record WHERE user_id = :userId AND certificate_id = :certificateId ORDER BY share_date DESC")
    List<ShareRecordEntity> getShareRecordsByUserIdAndCertificateId(String userId, String certificateId);

    @Query("SELECT * FROM share_record WHERE is_expired = 0 AND status = 'Active'")
    List<ShareRecordEntity> getActiveShareRecords();

    @Query("SELECT * FROM share_record WHERE user_id = :userId AND is_expired = 0 AND status = 'Active' ORDER BY share_date DESC")
    List<ShareRecordEntity> getActiveShareRecordsByUserId(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertShareRecord(ShareRecordEntity shareRecord);

    @Update
    void updateShareRecord(ShareRecordEntity shareRecord);

    @Delete
    void deleteShareRecord(ShareRecordEntity shareRecord);

    @Query("SELECT * FROM share_record WHERE user_id = :userId")
    List<ShareRecordEntity> getAllByUser(String userId);
    @Query("DELETE FROM share_record WHERE id = :id")
    void deleteShareRecordById(long id);

    @Query("SELECT * FROM share_record WHERE id = :id LIMIT 1")
    ShareRecordEntity getById(long id);
    @Query("UPDATE share_record SET is_expired = 1, status = 'Expired' WHERE expiration_date < :currentDate AND is_expired = 0")
    void markExpiredRecords(String currentDate);

    @Query("DELETE FROM share_record")
    void clearAll();
}
