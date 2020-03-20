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

    public static File getCitiesFile() {
        return new File("/Users/isko/Desktop/Projects/hackanet-backend/src/main/resources/location/GeoLite2-City.mmdb");
    }

    public static File getCountriesFile() {
        return new File("location/GeoLite2-Country.mmdb");
    }
}
