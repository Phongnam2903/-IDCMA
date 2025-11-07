package com.example.idcma_project_prm392.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.idcma_project_prm392.database.dao.CertificateDao;
import com.example.idcma_project_prm392.database.dao.ProfileDao;
import com.example.idcma_project_prm392.database.dao.ShareRecordDao;
import com.example.idcma_project_prm392.database.dao.UserDao;
import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import com.example.idcma_project_prm392.database.entity.ProfileEntity;
import com.example.idcma_project_prm392.database.entity.SettingsEntity;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import com.example.idcma_project_prm392.database.entity.UserEntity;

/**
 * Room Database class - Singleton pattern
 */
@Database(
    entities = {CertificateEntity.class, UserEntity.class, ProfileEntity.class, SettingsEntity.class, ShareRecordEntity.class},
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;

    public abstract CertificateDao certificateDao();
    public abstract UserDao userDao();
    public abstract ProfileDao profileDao();
    public abstract ShareRecordDao shareRecordDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "idcma_database"
                    )
                    .allowMainThreadQueries() // Cho phép chạy trên main thread (cho demo)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}

