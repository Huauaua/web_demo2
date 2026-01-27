package com.zts.web_demo2.Service.impl;

import com.zts.web_demo2.Entity.Comment;
import com.zts.web_demo2.Repository.CommentRepository;
import com.zts.web_demo2.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> getApprovedCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndStatusApproved(postId);
    }

    @Override
    public Comment saveComment(Comment comment) {
        // 设置默认状态为待审核
        comment.setStatus("pending");
        return commentRepository.save(comment);
    }

    @Override
    public Comment approveComment(Long id) {
        return commentRepository.updateStatus(id, "approved");
    }

    @Override
    public Comment rejectComment(Long id) {
        return commentRepository.updateStatus(id, "spam");
    }

    @Override
    public boolean deleteComment(Long id) {
        return commentRepository.deleteById(id);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public int getCommentCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
    
    @Override
    public List<Comment> getCommentsByStatus(String status) {
        List<Comment> allComments = commentRepository.findAll();
        return allComments.stream()
                .filter(comment -> status.equals(comment.getStatus()))
                .toList();
    }
}