package com.anilduyguc.youtubeclone.service;

import com.anilduyguc.youtubeclone.dto.CommentDto;
import com.anilduyguc.youtubeclone.dto.UploadVideoResponse;
import com.anilduyguc.youtubeclone.dto.VideoDto;
import com.anilduyguc.youtubeclone.model.Comment;
import com.anilduyguc.youtubeclone.model.Video;
import com.anilduyguc.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;

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


        increaseVideoCount(video);
        userService.addVideoToHistory(videoId);

        return mapToVideoDto(video);
    }

    private void increaseVideoCount(Video video) {
        video.incrementViewCount();
        videoRepository.save(video);
    }

    public VideoDto likeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        if (userService.ifLikedVideo(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        } else if (userService.ifDislikedVideo(videoId)) {
            videoById.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        } else {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    public VideoDto dislikeVideo(String videoId) {
        Video videoById = getVideoById(videoId);

        if (userService.ifDislikedVideo(videoId)) {
            videoById.decrementDislikes();
            userService.removeFromDislikedVideos(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDislikes();
            userService.addToDislikedVideos(videoId);
        } else {
            videoById.incrementDislikes();
            userService.addToDislikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    private VideoDto mapToVideoDto(Video videoById) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDislikes().get());
        videoDto.setViewCount(videoById.getViewCount().get());

        return videoDto;
    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(commentDto.getAuthorId());
        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getCommentList();

        return commentList.stream().map(this::mapToCommentDto).toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        return commentDto;
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream().map(this::mapToVideoDto).toList();

    }
}
