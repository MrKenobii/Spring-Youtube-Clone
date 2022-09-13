package com.anilduyguc.youtubeclone.service;

import com.anilduyguc.youtubeclone.dto.UploadVideoResponse;
import com.anilduyguc.youtubeclone.dto.VideoDto;
import com.anilduyguc.youtubeclone.model.Video;
import com.anilduyguc.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;

    public UploadVideoResponse uploadVideo(MultipartFile multipartFile){
        System.out.println(multipartFile.getName());
        String videoUrl = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        var savedVideo = videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());

    }

    public VideoDto editVideo(VideoDto videoDto) {
        var video = this.getVideoById(videoDto.getId());

        video.setTitle(videoDto.getTitle());
        video.setDescription(video.getDescription());
        video.setTags(videoDto.getTags());
        video.setThumbnailUrl(videoDto.getThumbnailUrl());
        video.setVideoStatus(videoDto.getVideoStatus());

        videoRepository.save(video);
        return videoDto;



    }

    public String uploadThumbnail(MultipartFile multipartFile, String videoId) {
        var video = getVideoById(videoId);
        String thumbnailUrl = s3Service.uploadFile(multipartFile);
        video.setThumbnailUrl(thumbnailUrl);

        videoRepository.save(video);
        return thumbnailUrl;
    }
    private Video getVideoById(String id){
         return videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No video found with id: " + id));
    }

    public VideoDto getVideoDetails(String videoId) {
        Video video = this.getVideoById(videoId);
        VideoDto videoDto = new VideoDto();

        videoDto.setVideoUrl(video.getVideoUrl());
        videoDto.setThumbnailUrl(video.getThumbnailUrl());
        videoDto.setId(video.getId());
        videoDto.setTitle(video.getTitle());
        videoDto.setDescription(video.getDescription());
        videoDto.setTags(video.getTags());
        videoDto.setVideoStatus(video.getVideoStatus() );

        return videoDto;
    }
}
