package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.idcma_project_prm392.database.entity.ProfileEntity;
import java.util.List;

/**
 * DAO (Data Access Object) cho Profile operations
 */
@Dao
public interface ProfileDao {
    
    @Query("SELECT * FROM profiles")
    List<ProfileEntity> getAllProfiles();

    @Query("SELECT * FROM profiles WHERE id = :id")
    ProfileEntity getProfileById(long id);

    @Query("SELECT * FROM profiles WHERE user_id = :userId")
    ProfileEntity getProfileByUserId(String userId);

    @Query("SELECT * FROM profiles WHERE slug = :slug")
    ProfileEntity getProfileBySlug(String slug);

    @Query("SELECT * FROM profiles WHERE is_public = :isPublic")
    List<ProfileEntity> getPublicProfiles(boolean isPublic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProfile(ProfileEntity profile);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertProfiles(List<ProfileEntity> profiles);

    @Update
    void updateProfile(ProfileEntity profile);

    @Delete
    void deleteProfile(ProfileEntity profile);

    @Query("DELETE FROM profiles WHERE id = :id")
    void deleteProfileById(long id);

    @Query("DELETE FROM profiles WHERE user_id = :userId")
    void deleteProfileByUserId(String userId);
}

