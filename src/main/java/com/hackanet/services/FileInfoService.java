package com.hackanet.services;

import com.hackanet.models.FileInfo;

public interface FileInfoService {
    FileInfo get(Long id);
    FileInfo get(String name);
    void delete(FileInfo fileInfo);
    FileInfo save(FileInfo fileInfo);
}
