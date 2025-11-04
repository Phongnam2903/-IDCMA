package com.example.idcma_project_prm392.database.mapper;

import com.example.idcma_project_prm392.database.entity.CertificateEntity;
import com.example.idcma_project_prm392.database.entity.UserEntity;
import com.example.idcma_project_prm392.model.Certificate;
import com.example.idcma_project_prm392.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class để convert giữa Entity và Model classes
 */
public class EntityMapper {

    // Certificate mapping
    public static Certificate toCertificate(CertificateEntity entity) {
        if (entity == null) return null;
        
        Certificate cert = new Certificate();
        cert.setId(String.valueOf(entity.getId()));
        cert.setUserId(entity.getUserId());
        cert.setName(entity.getName());
        cert.setIssuer(entity.getIssuer());
        cert.setCredentialId(entity.getCredentialId());
        cert.setIssueDate(entity.getIssueDate());
        cert.setExpiryDate(entity.getExpiryDate());
        cert.setFileUrl(entity.getFilePath()); // Map filePath to fileUrl for compatibility
        cert.setArchived(entity.isArchived());
        cert.setTags(entity.getTags() != null ? entity.getTags() : new ArrayList<>());
        
        return cert;
    }

    public static CertificateEntity toCertificateEntity(Certificate cert) {
        if (cert == null) return null;
        
        CertificateEntity entity = new CertificateEntity();
        
        // Parse ID if exists
        if (cert.getId() != null && !cert.getId().isEmpty()) {
            try {
                entity.setId(Long.parseLong(cert.getId()));
            } catch (NumberFormatException e) {
                // ID will be auto-generated if not valid
            }
        }
        
        entity.setUserId(cert.getUserId());
        entity.setName(cert.getName());
        entity.setIssuer(cert.getIssuer());
        entity.setCredentialId(cert.getCredentialId());
        entity.setIssueDate(cert.getIssueDate());
        entity.setExpiryDate(cert.getExpiryDate());
        entity.setFilePath(cert.getFileUrl()); // Map fileUrl to filePath
        entity.setArchived(cert.isArchived());
        entity.setTags(cert.getTags() != null ? cert.getTags() : new ArrayList<>());
        
        return entity;
    }

    public static List<Certificate> toCertificateList(List<CertificateEntity> entities) {
        List<Certificate> certificates = new ArrayList<>();
        if (entities != null) {
            for (CertificateEntity entity : entities) {
                certificates.add(toCertificate(entity));
            }
        }
        return certificates;
    }

    // User mapping
    public static User toUser(UserEntity entity) {
        if (entity == null) return null;
        
        User user = new User();
        user.setId(String.valueOf(entity.getId()));
        user.setFullName(entity.getFullName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPasswordHash()); // Map passwordHash to password for compatibility
        user.setRole(entity.getRole());
        user.setProfileImage(entity.getProfileImagePath()); // Map profileImagePath to profileImage
        
        return user;
    }

    public static UserEntity toUserEntity(User user) {
        if (user == null) return null;
        
        UserEntity entity = new UserEntity();
        
        // Parse ID if exists
        if (user.getId() != null && !user.getId().isEmpty()) {
            try {
                entity.setId(Long.parseLong(user.getId()));
            } catch (NumberFormatException e) {
                // ID will be auto-generated if not valid
            }
        }
        
        entity.setEmail(user.getEmail());
        entity.setFullName(user.getFullName());
        entity.setPasswordHash(user.getPassword()); // Map password to passwordHash
        entity.setRole(user.getRole());
        entity.setProfileImagePath(user.getProfileImage()); // Map profileImage to profileImagePath
        
        return entity;
    }
}

