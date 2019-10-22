package com.hackanet.services;

import com.hackanet.models.FileInfo;

import java.util.List;

public interface FileInfoService {
    FileInfo get(Long id);
    FileInfo get(String name);
    void delete(FileInfo fileInfo);
    FileInfo save(FileInfo fileInfo);
    List<FileInfo> getByIdsIn(List<Long> ids);
}
