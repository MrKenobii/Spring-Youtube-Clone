package com.anilduyguc.youtubeclone.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile multipartFile);
}
