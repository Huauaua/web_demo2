package com.zts.web_demo2.Service.impl;

import com.zts.web_demo2.Entity.Article;
import com.zts.web_demo2.Service.ArticleService;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private Article createArticle(Long id, String title, String content, String excerpt, String author, 
                                  String category, String[] tags, LocalDateTime publishDate, String imageUrl, Integer readTime) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setContent(content);
        article.setExcerpt(excerpt);
        article.setAuthor(author);
        article.setCategory(category);
        article.setTags(tags);
        article.setPublishDate(publishDate);
        article.setImageUrl(imageUrl);
        article.setReadTime(readTime);
        return article;
    }

    public ArticleServiceImpl() {
        // 构造函数，可在此添加初始化逻辑
    }

    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = new java.util.ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
            
            // 查询所有已发布的文章
            try (java.sql.PreparedStatement statement = connection.prepareStatement(
                "SELECT post_id, title, content, excerpt, featured_image, reading_time_minutes, published_at FROM posts WHERE status = 'published' ORDER BY published_at DESC"
            )) {
                
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    
                    while (resultSet.next()) {
                        Article article = new Article();
                        article.setId(resultSet.getLong("post_id"));
                        article.setTitle(resultSet.getString("title"));
                        article.setContent(resultSet.getString("content"));
                        article.setExcerpt(resultSet.getString("excerpt"));
                        article.setAuthor("博主"); // 默认作者
                        
                        // 获取文章分类
                        try (java.sql.PreparedStatement categoryStmt = connection.prepareStatement(
                            "SELECT c.name FROM categories c JOIN post_categories pc ON c.category_id = pc.category_id WHERE pc.post_id = ? LIMIT 1"
                        )) {
                            categoryStmt.setLong(1, resultSet.getLong("post_id"));
                            
                            try (java.sql.ResultSet categoryResult = categoryStmt.executeQuery()) {
                                if (categoryResult.next()) {
                                    article.setCategory(categoryResult.getString("name"));
                                } else {
                                    article.setCategory("未分类");
                                }
                            }
                        }
                        
                        // 获取文章标签
                        try (java.sql.PreparedStatement tagStmt = connection.prepareStatement(
                            "SELECT GROUP_CONCAT(t.name) as tags FROM tags t JOIN post_tags pt ON t.tag_id = pt.tag_id WHERE pt.post_id = ?"
                        )) {
                            tagStmt.setLong(1, resultSet.getLong("post_id"));
                            
                            try (java.sql.ResultSet tagResult = tagStmt.executeQuery()) {
                                if (tagResult.next()) {
                                    String tagsStr = tagResult.getString("tags");
                                    if (tagsStr != null && !tagsStr.isEmpty()) {
                                        article.setTags(tagsStr.split(","));
                                    } else {
                                        article.setTags(new String[]{});
                                    }
                                } else {
                                    article.setTags(new String[]{});
                                }
                            }
                        }
                        
                        article.setPublishDate(resultSet.getObject("published_at", LocalDateTime.class));
                        article.setImageUrl(resultSet.getString("featured_image"));
                        article.setReadTime(resultSet.getInt("reading_time_minutes"));
                        
                        articles.add(article);
                    }
                }
            }
            
            return articles;
        } catch (Exception e) {
            // 如果数据库连接失败，返回空列表
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public Article getArticleById(Long id) {
        List<Article> allArticles = getAllArticles();
        return allArticles.stream()
                .filter(article -> article.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Article> getArticlesByCategory(String category) {
        List<Article> allArticles = getAllArticles();
        return allArticles.stream()
                .filter(article -> article.getCategory() != null && article.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getArticlesByTag(String tag) {
        List<Article> allArticles = getAllArticles();
        return allArticles.stream()
                .filter(article -> article.getTags() != null && Arrays.stream(article.getTags())
                        .anyMatch(t -> t != null && t.equalsIgnoreCase(tag)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getArticlesByYear(int year) {
        List<Article> allArticles = getAllArticles();
        return allArticles.stream()
                .filter(article -> article.getPublishDate() != null && article.getPublishDate().getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getPaginatedArticles(int page, int size) {
        List<Article> allArticles = getAllArticles();
        int startIndex = page * size;
        if (startIndex >= allArticles.size()) {
            return new java.util.ArrayList<>();
        }
        int endIndex = Math.min(startIndex + size, allArticles.size());
        return allArticles.subList(startIndex, endIndex);
    }
}