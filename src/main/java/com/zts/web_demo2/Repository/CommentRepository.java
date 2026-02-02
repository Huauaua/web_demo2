package com.zts.web_demo2.Repository;

import com.zts.web_demo2.Entity.Comment;

import java.util.List;

public interface CommentRepository {
    // 根据文章ID获取已批准的评论
    List<Comment> findByPostIdAndStatusApproved(Long postId);

    // 保存评论
    Comment save(Comment comment);

    // 根据评论ID查找评论
    Comment findById(Long id);

    // 更新评论状态
    Comment updateStatus(Long id, String status);

    // 删除评论
    boolean deleteById(Long id);

    // 获取所有评论（用于管理）
    List<Comment> findAll();

    // 根据文章ID获取评论总数
    int countByPostId(Long postId);
    
    // 分页获取所有评论
    List<Comment> findPaginated(int page, int size);
    
    // 获取评论总数
    int countAll();
    
    // 根据状态分页获取评论
    List<Comment> findPaginatedByStatus(int page, int size, String status);
    
    // 根据状态获取评论数量
    int countByStatus(String status);
}