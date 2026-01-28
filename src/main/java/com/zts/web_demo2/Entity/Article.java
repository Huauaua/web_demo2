package com.zts.web_demo2.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Article {
    public static final String DEFAULT_IMAGE_URL = "https://example.com/default-image.jpg";
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private String author;
    private String category;
    private String[] tags;
    private LocalDateTime publishDate;
    private String imageUrl;
    private Integer readTime; // 阅读时间（分钟）
    private Integer number; // 文章序号

    private String previousTitle;
    private String nextTitle = null;
}