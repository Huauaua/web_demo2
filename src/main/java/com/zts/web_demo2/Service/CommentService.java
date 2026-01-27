package com.zts.web_demo2.Service;

import com.zts.web_demo2.Entity.Comment;

import java.util.List;

public interface CommentService {
    // 根据文章ID获取已批准的评论
    List<Comment> getApprovedCommentsByPostId(Long postId);

    // 保存评论（自动设置为待审核状态）
    Comment saveComment(Comment comment);

    // 审核评论（更新状态）
    Comment approveComment(Long id);

    // 拒绝评论
    Comment rejectComment(Long id);

    // 删除评论
    boolean deleteComment(Long id);

    // 根据ID获取评论
    Comment getCommentById(Long id);

    // 获取所有评论（用于管理）
    List<Comment> getAllComments();
    
    // 根据状态获取评论
    List<Comment> getCommentsByStatus(String status);

    // 获取文章的评论总数
    int getCommentCountByPostId(Long postId);
}