package com.example.Covid19App;

public class PlaylistModel {

    String playListId, publishedAt, title, description, thumbnail, channelName;

    public PlaylistModel() {
    }

    public PlaylistModel(String playListId, String publishedAt, String title, String description, String thumbnail, String channelName) {
        this.playListId = playListId;
        this.publishedAt = publishedAt;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.channelName = channelName;
    }

    public String getPlayListId() {
        return playListId;
    }

    public void setPlayListId(String playListId) {
        this.playListId = playListId;
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
}
