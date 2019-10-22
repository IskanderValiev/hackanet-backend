package com.hackanet.repositories;

import com.hackanet.models.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> getByName(String name);
    List<FileInfo> findAllByIdIn(List<Long> ids);
}
