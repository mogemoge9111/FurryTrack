package com.example.furrytrackapp.Model;

public class Post {
    private String postId;
    private String title;
    private String description;
    private String imageBase64;
    private String hashtags;
    private String userId;
    private long timestamp;

    public Post() {
        // Пустой конструктор нужен для Firebase
    }

    public Post(String postId, String title, String description, String imageBase64, String hashtags, String userId) {
        this.postId = postId;
        this.title = title;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.hashtags = hashtags;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    public String getHashtags() { return hashtags; }
    public void setHashtags(String hashtags) { this.hashtags = hashtags; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}