package com.zts.web_demo2.Repository.impl;

import com.zts.web_demo2.Entity.Comment;
import com.zts.web_demo2.Repository.CommentRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123");
    }

    @Override
    public List<Comment> findByPostIdAndStatusApproved(Long postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ? AND status = 'approved' ORDER BY created_at ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = mapResultSetToComment(rs);
                comments.add(comment);
            }
        } catch (Exception e) {
            System.err.println("查询评论失败: " + e.getMessage());
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (post_id, author_name, author_email, author_website, content, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, 'pending', NOW(), NOW())";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, comment.getPostId());
            stmt.setString(2, comment.getAuthorName());
            stmt.setString(3, comment.getAuthorEmail());
            stmt.setString(4, comment.getAuthorWebsite());
            stmt.setString(5, comment.getContent());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    comment.setId(id);
                    return comment;
                }
            }
        } catch (Exception e) {
            System.err.println("保存评论失败: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Comment findById(Long id) {
        String sql = "SELECT * FROM comments WHERE comment_id = ?";
        Comment comment = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                comment = mapResultSetToComment(rs);
            }
        } catch (Exception e) {
            System.err.println("查询评论失败: " + e.getMessage());
            e.printStackTrace();
        }

        return comment;
    }

    @Override
    public Comment updateStatus(Long id, String status) {
        String sql = "UPDATE comments SET status = ?, updated_at = NOW() WHERE comment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return findById(id);
            }
        } catch (Exception e) {
            System.err.println("更新评论状态失败: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE comment_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            System.err.println("删除评论失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = mapResultSetToComment(rs);
                comments.add(comment);
            }
        } catch (Exception e) {
            System.err.println("查询所有评论失败: " + e.getMessage());
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public int countByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ? AND status = 'approved'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("查询评论总数失败: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private Comment mapResultSetToComment(ResultSet rs) throws Exception {
        Comment comment = new Comment();
        comment.setId(rs.getLong("comment_id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setAuthorName(rs.getString("author_name"));
        comment.setAuthorEmail(rs.getString("author_email"));
        comment.setAuthorWebsite(rs.getString("author_website"));
        comment.setContent(rs.getString("content"));
        comment.setStatus(rs.getString("status"));
        comment.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        comment.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

        return comment;
    }
}