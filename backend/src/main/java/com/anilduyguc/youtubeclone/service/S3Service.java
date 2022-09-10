package com.anilduyguc.youtubeclone.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements FileService{
    public static final String BUCKET_NAME="youtube-clone-kenobii";
    private final AmazonS3Client amazonS3Client;

    @Override
    public String uploadFile(MultipartFile multipartFile){

        var filenameExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        var key = UUID.randomUUID().toString() + "." + filenameExtension;
        var metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3Client.putObject( BUCKET_NAME, key, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An exception occurred while uploading the file");
        }

        amazonS3Client.setObjectAcl( BUCKET_NAME, key, CannedAccessControlList.PublicRead);
        return amazonS3Client.getResourceUrl( BUCKET_NAME, key);

    }
}
