package com.anilduyguc.youtubeclone.repository;

import com.anilduyguc.youtubeclone.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideRepository extends MongoRepository<Video, String> {
}
