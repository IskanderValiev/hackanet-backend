package com.hackanet.services;

import com.hackanet.models.FileInfo;
import com.hackanet.models.user.User;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    FileInfo uploadFile(User user, MultipartFile multipartFile);
    void deleteFileFromS3Bucket(Long id, User user);
    void deleteFileFromS3Bucket(String filename, User user);
}
