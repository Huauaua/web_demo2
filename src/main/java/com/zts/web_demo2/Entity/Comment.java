package com.zts.web_demo2.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId; // 对应数据库中的post_id
    private String authorName; // 对应数据库中的author_name
    private String authorEmail; // 对应数据库中的author_email
    private String authorWebsite; // 对应数据库中的author_website
    private String content; // 对应数据库中的content
    private String status; // 对应数据库中的status (approved, pending, spam)
    private LocalDateTime createdAt; // 对应数据库中的created_at
    private LocalDateTime updatedAt; // 对应数据库中的updated_at
}