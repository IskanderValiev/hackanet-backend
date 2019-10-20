package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.models.FileInfo;
import com.hackanet.repositories.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Override
    public FileInfo get(Long id) {
        return fileInfoRepository.findById(id).orElseThrow(() -> NotFoundException.forFileInfo(id));
    }

    @Override
    public FileInfo get(String name) {
        return fileInfoRepository.getByName(name).orElseThrow(() -> NotFoundException.forFileInfo(name));
    }

    @Override
    public void delete(FileInfo fileInfo) {
        fileInfoRepository.delete(fileInfo);
    }

    @Override
    public FileInfo save(FileInfo fileInfo) {
        return fileInfoRepository.save(fileInfo);
    }
}
