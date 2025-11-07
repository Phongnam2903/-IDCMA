package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;

import java.util.List;

@Dao
public interface ShareRecordDao {

    @Insert
    void insert(ShareRecordEntity shareRecord);

    @Update
    void update(ShareRecordEntity shareRecord);

    @Delete
    void delete(ShareRecordEntity shareRecord);

    @Query("SELECT * FROM share_record WHERE user_id = :userId")
    List<ShareRecordEntity> getAllByUser(String userId);

    @Query("SELECT * FROM share_record WHERE id = :id LIMIT 1")
    ShareRecordEntity getById(long id);

    @Query("DELETE FROM share_record")
    void clearAll();
}
