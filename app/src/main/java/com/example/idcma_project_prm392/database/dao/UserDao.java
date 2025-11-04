package com.example.idcma_project_prm392.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.idcma_project_prm392.database.entity.UserEntity;
import java.util.List;

/**
 * DAO (Data Access Object) cho User operations
 */
@Dao
public interface UserDao {
    
    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    UserEntity getUserById(long id);

    @Query("SELECT * FROM users WHERE email = :email")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password_hash = :passwordHash")
    UserEntity getUserByEmailAndPassword(String email, String passwordHash);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertUsers(List<UserEntity> users);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("DELETE FROM users WHERE id = :id")
    void deleteUserById(long id);

    @Query("DELETE FROM users WHERE email = :email")
    void deleteUserByEmail(String email);
}

