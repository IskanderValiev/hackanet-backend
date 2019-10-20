package com.hackanet.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Iskander Valiev
 * created by isko
 * on 5/9/19
 */
@Slf4j
public class FileUtil {

    public static File convertMultipartToFile(MultipartFile file) {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convFile)){
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("IOException has been occurred during converting multipart file");
            e.printStackTrace();
        }
        return convFile;
    }
}
