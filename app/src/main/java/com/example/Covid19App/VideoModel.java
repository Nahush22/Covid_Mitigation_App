package com.example.Covid19App;

public class VideoModel {

    String publishedAt, title, description, thumbnail, channelName, videoId;

    public VideoModel() {

    }

    public VideoModel( String publishedAt, String title, String description, String thumbnail, String channelName, String videoId) {
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.channelName = channelName;
        this.videoId = videoId;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

}
