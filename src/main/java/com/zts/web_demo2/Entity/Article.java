package com.zts.web_demo2.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Article {
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

    private String previousTitle;
    private String nextTitle = null;
}