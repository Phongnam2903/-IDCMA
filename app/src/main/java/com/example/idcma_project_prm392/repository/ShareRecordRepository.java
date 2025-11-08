package com.example.idcma_project_prm392.repository;

import android.content.Context;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.dao.ShareRecordDao;
import com.example.idcma_project_prm392.database.entity.ShareRecordEntity;
import java.util.List;

/**
 * Repository pattern để abstract data layer cho ShareRecord
 */
public class ShareRecordRepository {
    
    private ShareRecordDao shareRecordDao;
    
    public ShareRecordRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.shareRecordDao = db.shareRecordDao();
    }

    public List<ShareRecordEntity> getShareRecordsByUserId(String userId) {
        return shareRecordDao.getShareRecordsByUserId(userId);
    }

    public List<ShareRecordEntity> getShareRecordsByCertificateId(String certificateId) {
        return shareRecordDao.getShareRecordsByCertificateId(certificateId);
    }

    public ShareRecordEntity getShareRecordByToken(String token) {
        return shareRecordDao.getShareRecordByToken(token);
    }

    public List<ShareRecordEntity> getShareRecordsByUserIdAndCertificateId(String userId, String certificateId) {
        return shareRecordDao.getShareRecordsByUserIdAndCertificateId(userId, certificateId);
    }

    public List<ShareRecordEntity> getActiveShareRecords() {
        return shareRecordDao.getActiveShareRecords();
    }

    public List<ShareRecordEntity> getActiveShareRecordsByUserId(String userId) {
        return shareRecordDao.getActiveShareRecordsByUserId(userId);
    }

    public long insertShareRecord(ShareRecordEntity shareRecord) {
        return shareRecordDao.insertShareRecord(shareRecord);
    }

    public void updateShareRecord(ShareRecordEntity shareRecord) {
        shareRecordDao.updateShareRecord(shareRecord);
    }

    public void deleteShareRecord(ShareRecordEntity shareRecord) {
        shareRecordDao.deleteShareRecord(shareRecord);
    }

    public void deleteShareRecordById(long id) {
        shareRecordDao.deleteShareRecordById(id);
    }

    public void markExpiredRecords(String currentDate) {
        shareRecordDao.markExpiredRecords(currentDate);
    }
}

