package com.hackanet.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hackanet.config.AmazonStorageConfig;
import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.json.mappers.FileInfoMapper;
import com.hackanet.models.FileInfo;
import com.hackanet.models.User;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.hackanet.security.utils.SecurityUtils.checkFileAccess;


/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
@Slf4j
@Service
public class AmazonUploadService implements UploadService {

    @Autowired
    private AmazonStorageConfig config;

    @Autowired
    private FileInfoService fileInfoService;

    private AmazonS3 client;

    @PostConstruct
    private void initAmazon() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(config.getAccessKey(), config.getSecretKey());
        this.client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    private void uploadFileToS3bucket(String filename, File file) {
        this.client.putObject(new PutObjectRequest(config.getBucketName(), filename, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    /**
    * The method uploads file.
    *
    * @throws BadRequestException
    *   If multipart file is null, the method will throw BadRequestException
    *
    * */
    @Transactional
    public FileInfo uploadFile(User user, MultipartFile multipartFile) {
        File file = FileUtil.convertMultipartToFile(multipartFile);
        String filename = multipartFile.getOriginalFilename();
        String fileUrl = config.getEndpointUrl() + "/" + config.getBucketName() + "/" + filename;
        uploadFileToS3bucket(filename, file);
        file.delete();

        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            if (Objects.isNull(image))
                throw BadRequestException.forUploadingFile();

            FileInfo fileInfo = FileInfo.builder()
                    .name(filename)
                    .previewLink(fileUrl)
                    .user(user)
                    .height(image.getHeight())
                    .width(image.getWidth())
                    .size(multipartFile.getSize())
                    .type(multipartFile.getContentType())
                    .build();

            fileInfo = fileInfoService.save(fileInfo);
            return fileInfo;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * The method deletes file from S3 bucket.
     *
     * @throws ForbiddenException
     *  if current user does not have necessary rights, exception will be thrown
     *
     * */
    public void deleteFileFromS3Bucket(Long id, User user) {
        FileInfo fileInfo = fileInfoService.get(id);
        String fileName = fileInfo.getPreviewLink().substring(fileInfo.getPreviewLink().lastIndexOf("/") + 1);
        checkFileAccess(fileInfo, user);
        client.deleteObject(new DeleteObjectRequest(config.getBucketName() + "/", fileName));
        fileInfoService.delete(fileInfo);
    }

    public void deleteFileFromS3Bucket(String filename, User user) {
        FileInfo fileInfo = fileInfoService.get(filename);
        checkFileAccess(fileInfo, user);
        client.deleteObject(new DeleteObjectRequest(config.getBucketName() + "/", filename));
        fileInfoService.delete(fileInfo);
    }
}
