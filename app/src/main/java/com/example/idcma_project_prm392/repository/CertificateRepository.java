package com.example.idcma_project_prm392.repository;

import android.content.Context;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.dao.CertificateDao;
import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import com.example.idcma_project_prm392.database.mapper.EntityMapper;
import com.example.idcma_project_prm392.model.Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository pattern để abstract data layer
 */
public class CertificateRepository {
    
    private CertificateDao certificateDao;
    
    public CertificateRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.certificateDao = db.certificateDao();
    }

    public List<Certificate> getAllActiveCertificates() {
        return EntityMapper.toCertificateList(certificateDao.getAllActiveCertificates());
    }

    public List<Certificate> getActiveCertificatesByUserId(String userId) {
        return EntityMapper.toCertificateList(certificateDao.getActiveCertificatesByUserId(userId));
    }

    public List<Certificate> searchActiveCertificatesByName(String keyword) {
        return EntityMapper.toCertificateList(certificateDao.searchActiveCertificatesByName(keyword));
    }

    public List<Certificate> searchActiveCertificatesByUserIdAndName(String userId, String keyword) {
        return EntityMapper.toCertificateList(certificateDao.searchActiveCertificatesByUserIdAndName(userId, keyword));
    }

    // (tuỳ chọn)
    public List<Certificate> getArchivedCertificatesByUserId(String userId) {
        return EntityMapper.toCertificateList(certificateDao.getArchivedCertificatesByUserId(userId));
    }


    public List<Certificate> getActiveByUserId(String userId) {
        return EntityMapper.toCertificateList(certificateDao.getActiveByUserId(userId));
    }

    public List<Certificate> getAllCertificates() {
        List<CertificateEntity> entities = certificateDao.getAllCertificates();
        return EntityMapper.toCertificateList(entities);
    }
    
    public List<Certificate> getCertificatesByUserId(String userId) {
        List<CertificateEntity> entities = certificateDao.getCertificatesByUserId(userId);
        return EntityMapper.toCertificateList(entities);
    }
    
    public Certificate getCertificateById(long id) {
        CertificateEntity entity = certificateDao.getCertificateById(id);
        return EntityMapper.toCertificate(entity);
    }
    
    public List<Certificate> searchCertificatesByName(String keyword) {
        List<CertificateEntity> entities = certificateDao.searchCertificatesByName(keyword);
        return EntityMapper.toCertificateList(entities);
    }
    
    public List<Certificate> searchCertificatesByUserIdAndName(String userId, String keyword) {
        List<CertificateEntity> entities = certificateDao.searchCertificatesByUserIdAndName(userId, keyword);
        return EntityMapper.toCertificateList(entities);
    }
    
    public long insertCertificate(Certificate certificate) {
        CertificateEntity entity = EntityMapper.toCertificateEntity(certificate);
        return certificateDao.insertCertificate(entity);
    }
    
    public void updateCertificate(Certificate certificate) {
        CertificateEntity entity = EntityMapper.toCertificateEntity(certificate);
        certificateDao.updateCertificate(entity);
    }
    
    public void deleteCertificate(Certificate certificate) {
        CertificateEntity entity = EntityMapper.toCertificateEntity(certificate);
        certificateDao.deleteCertificate(entity);
    }
    
    public void deleteCertificateById(long id) {
        certificateDao.deleteCertificateById(id);
    }
}

