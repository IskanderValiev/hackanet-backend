package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.models.FileInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Component("fileInfoMapper")
public class FileInfoMapper implements Mapper<FileInfo, FileInfoDto> {

    @Override
    public FileInfoDto map(FileInfo from) {
        if (from == null)
            return null;

        return FileInfoDto.builder()
                .id(from.getId())
                .previewLink(from.getPreviewLink())
                .build();
    }
}
