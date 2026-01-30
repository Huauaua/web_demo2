package com.zts.web_demo2.Service.impl;

import com.zts.web_demo2.Entity.Article;
import com.zts.web_demo2.Entity.Comment;
import com.zts.web_demo2.Repository.CommentRepository;
import com.zts.web_demo2.Service.ArticleService;
import com.zts.web_demo2.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ArticleService articleService;

    @Override
    public List<Comment> getApprovedCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndStatusApproved(postId);
        // 为评论设置文章标题
        for (Comment comment : comments) {
            Article article = articleService.getArticleById(comment.getPostId());
            if (article != null) {
                comment.setPostName(article.getTitle());
            } else {
                comment.setPostName("未知文章");
            }
        }
        return comments;
    }

    @Override
    public Comment saveComment(Comment comment) {
        // 设置默认状态为待审核
        comment.setStatus("pending");
        Comment savedComment = commentRepository.save(comment);
        // 设置文章标题
        Article article = articleService.getArticleById(savedComment.getPostId());
        if (article != null) {
            savedComment.setPostName(article.getTitle());
        } else {
            savedComment.setPostName("未知文章");
        }
        return savedComment;
    }

    @Override
    public Comment approveComment(Long id) {
        Comment updatedComment = commentRepository.updateStatus(id, "approved");
        // 设置文章标题
        Article article = articleService.getArticleById(updatedComment.getPostId());
        if (article != null) {
            updatedComment.setPostName(article.getTitle());
        } else {
            updatedComment.setPostName("未知文章");
        }
        return updatedComment;
    }

    @Override
    public Comment rejectComment(Long id) {
        Comment updatedComment = commentRepository.updateStatus(id, "spam");
        // 设置文章标题
        Article article = articleService.getArticleById(updatedComment.getPostId());
        if (article != null) {
            updatedComment.setPostName(article.getTitle());
        } else {
            updatedComment.setPostName("未知文章");
        }
        return updatedComment;
    }

    @Override
    public boolean deleteComment(Long id) {
        return commentRepository.deleteById(id);
    }

    @Override
    public Comment getCommentById(Long id) {
        Comment comment = commentRepository.findById(id);
        // 设置文章标题
        if (comment != null) {
            Article article = articleService.getArticleById(comment.getPostId());
            if (article != null) {
                comment.setPostName(article.getTitle());
            } else {
                comment.setPostName("未知文章");
            }
        }
        return comment;
    }

    @Override
    public List<Comment> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        // 为所有评论设置文章标题
        for (Comment comment : comments) {
            Article article = articleService.getArticleById(comment.getPostId());
            if (article != null) {
                comment.setPostName(article.getTitle());
            } else {
                comment.setPostName("未知文章");
            }
        }
        return comments;
    }

    @Override
    public int getCommentCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
    
    @Override
    public List<Comment> getCommentsByStatus(String status) {
        List<Comment> allComments = commentRepository.findAll();
        // 为评论设置文章标题
        for (Comment comment : allComments) {
            Article article = articleService.getArticleById(comment.getPostId());
            if (article != null) {
                comment.setPostName(article.getTitle());
            } else {
                comment.setPostName("未知文章");
            }
        }
        return allComments.stream()
                .filter(comment -> status.equals(comment.getStatus()))
                .toList();
    }
}