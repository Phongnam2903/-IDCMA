package com.example.idcma_project_prm392.repository;

import android.content.Context;
import com.example.idcma_project_prm392.database.AppDatabase;
import com.example.idcma_project_prm392.database.dao.UserDao;
import com.example.idcma_project_prm392.database.entity.UserEntity;
import com.example.idcma_project_prm392.database.mapper.EntityMapper;
import com.example.idcma_project_prm392.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Repository pattern cho User operations
 * Bao gồm password hashing (SHA-256)
 */
public class UserRepository {
    
    private UserDao userDao;
    
    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.userDao = db.userDao();
    }
    
    public User getUserByEmail(String email) {
        UserEntity entity = userDao.getUserByEmail(email);
        return EntityMapper.toUser(entity);
    }
    
    public User getUserById(long id) {
        UserEntity entity = userDao.getUserById(id);
        return EntityMapper.toUser(entity);
    }
    
    public User login(String email, String password) {
        String passwordHash = hashPassword(password);
        UserEntity entity = userDao.getUserByEmailAndPassword(email, passwordHash);
        return EntityMapper.toUser(entity);
    }
    
    public long register(User user) {
        // Hash password trước khi lưu
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String passwordHash = hashPassword(user.getPassword());
            user.setPassword(passwordHash);
        }
        
        UserEntity entity = EntityMapper.toUserEntity(user);
        return userDao.insertUser(entity);
    }
    
    public void updateUser(User user) {
        // Hash password nếu có thay đổi
        if (user.getPassword() != null && !user.getPassword().isEmpty() && 
            !user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
            // Nếu password chưa được hash (không phải bcrypt)
            String passwordHash = hashPassword(user.getPassword());
            user.setPassword(passwordHash);
        }
        
        UserEntity entity = EntityMapper.toUserEntity(user);
        userDao.updateUser(entity);
    }
    
    public void deleteUser(User user) {
        UserEntity entity = EntityMapper.toUserEntity(user);
        userDao.deleteUser(entity);
    }
    
    public boolean emailExists(String email) {
        UserEntity entity = userDao.getUserByEmail(email);
        return entity != null;
    }
    
    /**
     * Hash password bằng SHA-256 (đơn giản, có thể nâng cấp lên bcrypt sau)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback: return original password (not secure)
        }
    }
}

