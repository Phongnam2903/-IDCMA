package com.example.idcma_project_prm392.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class để quản lý file storage local thay vì Firebase Storage
 */
public class LocalStorageHelper {
    
    private static final String TAG = "LocalStorageHelper";
    private static final String CERTIFICATES_DIR = "certificates";
    private static final String PROFILE_IMAGES_DIR = "profile_images";

    /**
     * Lấy thư mục lưu certificates cho user cụ thể
     */
    public static File getCertificatesDirectory(Context context, String userId) {
        File appDir = new File(context.getFilesDir(), CERTIFICATES_DIR);
        File userDir = new File(appDir, userId);
        
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        
        return userDir;
    }

    /**
     * Lấy thư mục lưu profile images
     */
    public static File getProfileImagesDirectory(Context context) {
        File appDir = new File(context.getFilesDir(), PROFILE_IMAGES_DIR);
        
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        
        return appDir;
    }

    /**
     * Copy file từ URI vào local storage
     * @param context Context
     * @param sourceUri URI của file nguồn
     * @param userId ID của user
     * @param fileName Tên file (nếu null sẽ tự động generate)
     * @return File path của file đã lưu, hoặc null nếu lỗi
     */
    public static String saveCertificateFile(Context context, Uri sourceUri, String userId, String fileName) {
        try {
            File destDir = getCertificatesDirectory(context, userId);
            
            // Generate filename nếu không có
            if (fileName == null || fileName.isEmpty()) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String extension = getFileExtension(context, sourceUri);
                fileName = "cert_" + timeStamp + extension;
            }
            
            File destFile = new File(destDir, fileName);
            
            // Copy file
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) {
                Log.e(TAG, "Cannot open input stream for URI: " + sourceUri);
                return null;
            }
            
            OutputStream outputStream = new FileOutputStream(destFile);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();
            
            Log.d(TAG, "File saved: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
            
        } catch (IOException e) {
            Log.e(TAG, "Error saving file: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy file extension từ URI
     */
    private static String getFileExtension(Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.contains("pdf")) {
                return ".pdf";
            } else if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
                return ".jpg";
            } else if (mimeType.contains("png")) {
                return ".png";
            } else if (mimeType.contains("gif")) {
                return ".gif";
            }
        }
        return ".dat"; // Default extension
    }

    /**
     * Xóa file certificate
     */
    public static boolean deleteCertificateFile(Context context, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                Log.d(TAG, "File deleted: " + filePath + " - " + deleted);
                return deleted;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting file: " + e.getMessage(), e);
        }
        
        return false;
    }

    /**
     * Lấy URI từ file path
     */
    public static Uri getUriFromPath(Context context, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Log.e("LocalStorageHelper", "File không tồn tại tại đường dẫn: " + filePath);
            return null;
        }

        try {
            String providerAuthority = "com.example.idcma_project_prm392.provider";

            return FileProvider.getUriForFile(context, providerAuthority, file);

        } catch (IllegalArgumentException e) {
            Log.e("LocalStorageHelper", "Lỗi tạo URI cho FileProvider. Bạn đã cấu hình provider_paths.xml và AndroidManifest.xml chưa? " + e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra file có tồn tại không
     */
    public static boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Lấy file size
     */
    public static long getFileSize(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        
        File file = new File(filePath);
        if (file.exists()) {
            return file.length();
        }
        
        return 0;
    }
}

