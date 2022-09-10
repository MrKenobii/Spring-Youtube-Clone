package com.anilduyguc.youtubeclone.service;

import com.anilduyguc.youtubeclone.model.Video;
import com.anilduyguc.youtubeclone.repository.VideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Service s3Service;
    private final VideRepository videRepository;

    public void uploadVideo(MultipartFile multipartFile){
        System.out.println(multipartFile.getName());
        String videoUrl = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        videRepository.save(video);

    }
}
