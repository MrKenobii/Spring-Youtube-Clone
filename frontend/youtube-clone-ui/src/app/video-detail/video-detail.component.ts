import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {VideoService} from "../video.service";
import {UserService} from "../user.service";

@Component({
  selector: 'app-video-detail',
  templateUrl: './video-detail.component.html',
  styleUrls: ['./video-detail.component.css']
})
export class VideoDetailComponent implements OnInit {
  videoId!: string;
  videoUrl!: string;
  videoAvailable: boolean = false;
  videoTitle!: string;
  videoDescription!: string;
  tags: Array<string> = [];
  likeCount: number = 0;
  dislikeCount: number = 0;
  viewCount: number = 0;
  showSubscribeButton: boolean = true;
  showUnsubscribeButton: boolean = false;

  constructor(private activatedRoute:ActivatedRoute, private videoService: VideoService, private userService: UserService) {
    this.videoId= this.activatedRoute.snapshot.params['videoId'];
    this.videoService.getVideo(this.videoId).subscribe(data => {
      this.videoUrl = data.videoUrl;
      this.videoTitle=data.title;
      this.videoDescription=data.description;
      this.tags=data.tags;
      this.videoAvailable=true;
      this.likeCount=data.likeCount;
      this.dislikeCount=data.dislikeCount;
      this.viewCount=data.viewCount;
    })
  }

  ngOnInit(): void {
  }

  likeVideo() {
    this.videoService.likeVideo(this.videoId).subscribe(data => {
      this.likeCount = data.likeCount;
      this.dislikeCount = data.dislikeCount;
    });
  }

  dislikeVideo() {
    this.videoService.dislikeVideo(this.videoId).subscribe(data => {
      this.likeCount = data.likeCount;
      this.dislikeCount = data.dislikeCount;
    });
  }

  subscribeToUser() {
    this.userService.subscribeToUser(this.userService.getUserId()).subscribe(data => {
      this.showUnsubscribeButton=true;
      this.showSubscribeButton=false;
    });
  }

  unSubscribeToUser() {
    this.userService.unSubscribeToUser(this.userService.getUserId()).subscribe(data => {
      this.showUnsubscribeButton=false;
      this.showSubscribeButton=true;
    });
  }
}
