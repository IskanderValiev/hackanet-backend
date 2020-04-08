package com.hackanet.services;

import com.hackanet.models.FileInfo;
import com.hackanet.models.User;

import java.util.List;

public interface FileInfoService extends RetrieveService<FileInfo> {
    FileInfo get(String name);
    void delete(FileInfo fileInfo);
    FileInfo save(FileInfo fileInfo);
    List<FileInfo> getByIdsIn(List<Long> ids);
    FileInfo createAndSave(User user, String url);
    boolean isImage(FileInfo fileInfo);
}
