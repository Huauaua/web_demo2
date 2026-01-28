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
    public Article createArticle(Article article) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123");
            
            // 生成文章的slug
            String baseSlug = generateSlug(article.getTitle());
            // 确保slug在数据库中是唯一的
            String slug = generateUniqueSlug(connection, baseSlug);
            
            // 插入文章
            String insertPostSql = "INSERT INTO posts (title, slug, excerpt, content, featured_image, reading_time_minutes, status, published_at, created_at) VALUES (?, ?, ?, ?, ?, ?, 'published', NOW(), NOW())";
            
            try (java.sql.PreparedStatement insertStatement = connection.prepareStatement(insertPostSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                insertStatement.setString(1, article.getTitle());
                insertStatement.setString(2, slug);
                insertStatement.setString(3, article.getExcerpt());
                insertStatement.setString(4, article.getContent());
                insertStatement.setString(5, article.getImageUrl());
                insertStatement.setInt(6, article.getReadTime() != null ? article.getReadTime() : 0);
                
                int affectedRows = insertStatement.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new RuntimeException("创建文章失败，没有行被影响");
                }
                
                // 获取生成的文章ID
                try (java.sql.ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    Long postId;
                    if (generatedKeys.next()) {
                        postId = generatedKeys.getLong(1);
                    } else {
                        throw new RuntimeException("创建文章失败，无法获取文章ID");
                    }
                    
                    // 设置分类
                    if (article.getCategory() != null && !article.getCategory().isEmpty()) {
                        setArticleCategory(connection, postId, article.getCategory());
                    }
                    
                    // 设置标签
                    if (article.getTags() != null && article.getTags().length > 0) {
                        setArticleTags(connection, postId, article.getTags());
                    }
                    
                    // 更新文章对象的ID并返回
                    article.setId(postId);
                    return article;
                }
            }
        } catch (Exception e) {
            System.err.println("创建文章失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建文章失败", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // 忽略关闭连接的异常
                }
            }
        }
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
    public Article updateArticle(Long id, Article article) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123");
            
            // 更新文章基本信息
            String updatePostSql = "UPDATE posts SET title = ?, excerpt = ?, content = ?, featured_image = ?, reading_time_minutes = ?, updated_at = NOW() WHERE post_id = ?";
            
            try (java.sql.PreparedStatement updateStatement = connection.prepareStatement(updatePostSql)) {
                updateStatement.setString(1, article.getTitle());
                updateStatement.setString(2, article.getExcerpt());
                updateStatement.setString(3, article.getContent());
                updateStatement.setString(4, article.getImageUrl());
                updateStatement.setInt(5, article.getReadTime() != null ? article.getReadTime() : 0);
                updateStatement.setLong(6, id);
                
                int affectedRows = updateStatement.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new RuntimeException("更新文章失败，没有找到对应的文章");
                }
                
                // 删除旧的分类和标签关联
                deleteOldAssociations(connection, id);
                
                // 设置新的分类
                if (article.getCategory() != null && !article.getCategory().isEmpty()) {
                    setArticleCategory(connection, id, article.getCategory());
                }
                
                // 设置新的标签
                if (article.getTags() != null && article.getTags().length > 0) {
                    setArticleTags(connection, id, article.getTags());
                }
                
                // 返回更新后的文章
                return getArticleById(id);
            }
        } catch (Exception e) {
            System.err.println("更新文章失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("更新文章失败", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // 忽略关闭连接的异常
                }
            }
        }
    }

    @Override
    public boolean deleteArticle(Long id) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123");
            
            // 删除文章（通过设置状态为archived而不是物理删除）
            String deletePostSql = "UPDATE posts SET status = 'archived', updated_at = NOW() WHERE post_id = ?";
            
            try (java.sql.PreparedStatement deleteStatement = connection.prepareStatement(deletePostSql)) {
                deleteStatement.setLong(1, id);
                
                int affectedRows = deleteStatement.executeUpdate();
                
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("删除文章失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // 忽略关闭连接的异常
                }
            }
        }
    }

    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "untitled-" + System.currentTimeMillis();
        }
        
        // 将标题转换为URL友好的slug
        String slug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s-]+", "-");
        
        if (slug.isEmpty()) {
            // 如果处理后slug为空，则使用默认值加上时间戳
            slug = "untitled-" + System.currentTimeMillis();
        }
        
        return slug;
    }
    
    private String generateUniqueSlug(Connection connection, String baseSlug) throws Exception {
        String uniqueSlug = baseSlug;
        int counter = 1;
        
        // 检查数据库中是否已存在相同的slug
        while (isSlugExists(connection, uniqueSlug)) {
            // 如果存在，则添加数字后缀
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }
        
        return uniqueSlug;
    }
    
    private String generateUniqueSlugForCategory(Connection connection, String baseSlug) throws Exception {
        String uniqueSlug = baseSlug;
        int counter = 1;
        
        // 检查数据库中是否已存在相同的分类slug
        while (isCategorySlugExists(connection, uniqueSlug)) {
            // 如果存在，则添加数字后缀
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }
        
        return uniqueSlug;
    }
    
    private String generateUniqueSlugForTag(Connection connection, String baseSlug) throws Exception {
        String uniqueSlug = baseSlug;
        int counter = 1;
        
        // 检查数据库中是否已存在相同的标签slug
        while (isTagSlugExists(connection, uniqueSlug)) {
            // 如果存在，则添加数字后缀
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }
        
        return uniqueSlug;
    }
    
    private boolean isSlugExists(Connection connection, String slug) throws Exception {
        String checkSql = "SELECT 1 FROM posts WHERE slug = ? LIMIT 1";
        try (java.sql.PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, slug);
            try (java.sql.ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    
    private boolean isCategorySlugExists(Connection connection, String slug) throws Exception {
        String checkSql = "SELECT 1 FROM categories WHERE slug = ? LIMIT 1";
        try (java.sql.PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, slug);
            try (java.sql.ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    
    private boolean isTagSlugExists(Connection connection, String slug) throws Exception {
        String checkSql = "SELECT 1 FROM tags WHERE slug = ? LIMIT 1";
        try (java.sql.PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, slug);
            try (java.sql.ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void setArticleCategory(Connection connection, Long postId, String categoryName) throws Exception {
        // 查找或创建分类
        Long categoryId = findOrCreateCategory(connection, categoryName);
        
        // 建立文章-分类关联
        String insertCategorySql = "INSERT INTO post_categories (post_id, category_id, is_primary) VALUES (?, ?, TRUE)";
        try (java.sql.PreparedStatement insertCatStatement = connection.prepareStatement(insertCategorySql)) {
            insertCatStatement.setLong(1, postId);
            insertCatStatement.setLong(2, categoryId);
            insertCatStatement.executeUpdate();
        }
    }

    private void setArticleTags(Connection connection, Long postId, String[] tagNames) throws Exception {
        for (String tagName : tagNames) {
            if (tagName != null && !tagName.trim().isEmpty()) {
                // 查找或创建标签
                Long tagId = findOrCreateTag(connection, tagName.trim());
                
                // 建立文章-标签关联
                String insertTagSql = "INSERT IGNORE INTO post_tags (post_id, tag_id) VALUES (?, ?)";
                try (java.sql.PreparedStatement insertTagStatement = connection.prepareStatement(insertTagSql)) {
                    insertTagStatement.setLong(1, postId);
                    insertTagStatement.setLong(2, tagId);
                    insertTagStatement.executeUpdate();
                }
            }
        }
    }

    private Long findOrCreateCategory(Connection connection, String categoryName) throws Exception {
        // 查找现有分类
        String selectSql = "SELECT category_id FROM categories WHERE name = ?";
        try (java.sql.PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setString(1, categoryName);
            try (java.sql.ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("category_id");
                }
            }
        }
        
        // 创建新分类
        String baseSlug = generateSlug(categoryName);
        // 确保分类slug在数据库中是唯一的
        String uniqueSlug = generateUniqueSlugForCategory(connection, baseSlug);
        
        String insertSql = "INSERT INTO categories (name, slug, description) VALUES (?, ?, ?)";
        try (java.sql.PreparedStatement insertStatement = connection.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, categoryName);
            insertStatement.setString(2, uniqueSlug);
            insertStatement.setString(3, categoryName);
            insertStatement.executeUpdate();
            
            try (java.sql.ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        
        throw new Exception("无法创建或查找分类: " + categoryName);
    }

    private Long findOrCreateTag(Connection connection, String tagName) throws Exception {
        // 查找现有标签
        String selectSql = "SELECT tag_id FROM tags WHERE name = ?";
        try (java.sql.PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            selectStatement.setString(1, tagName);
            try (java.sql.ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("tag_id");
                }
            }
        }
        
        // 创建新标签
        String baseSlug = generateSlug(tagName);
        // 确保标签slug在数据库中是唯一的
        String uniqueSlug = generateUniqueSlugForTag(connection, baseSlug);
        
        String insertSql = "INSERT INTO tags (name, slug) VALUES (?, ?)";
        try (java.sql.PreparedStatement insertStatement = connection.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, tagName);
            insertStatement.setString(2, uniqueSlug);
            insertStatement.executeUpdate();
            
            try (java.sql.ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        
        throw new Exception("无法创建或查找标签: " + tagName);
    }

    private void deleteOldAssociations(Connection connection, Long postId) throws Exception {
        // 删除旧的分类关联
        String deleteCategoriesSql = "DELETE FROM post_categories WHERE post_id = ?";
        try (java.sql.PreparedStatement deleteCatStatement = connection.prepareStatement(deleteCategoriesSql)) {
            deleteCatStatement.setLong(1, postId);
            deleteCatStatement.executeUpdate();
        }
        
        // 删除旧的标签关联
        String deleteTagsSql = "DELETE FROM post_tags WHERE post_id = ?";
        try (java.sql.PreparedStatement deleteTagStatement = connection.prepareStatement(deleteTagsSql)) {
            deleteTagStatement.setLong(1, postId);
            deleteTagStatement.executeUpdate();
        }
    }

    @Override
    public Article getArticleById(Long id) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123");
            
            // 直接从数据库查询特定文章，避免使用getAllArticles方法导致的数据一致性问题
            String selectSql = "SELECT post_id, title, content, excerpt, featured_image, reading_time_minutes, published_at FROM posts WHERE post_id = ? AND status = 'published'";
            try (java.sql.PreparedStatement statement = connection.prepareStatement(selectSql)) {
                statement.setLong(1, id);
                
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
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
                        
                        return article;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("查询文章失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // 忽略关闭连接的异常
                }
            }
        }
    }

    @Override
    public List<Article> getArticlesByCategory(String category) {
        List<Article> articles = new java.util.ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
        
            // 查询指定分类的文章
            String sql = "SELECT DISTINCT p.post_id, p.title, p.content, p.excerpt, p.featured_image, p.reading_time_minutes, p.published_at " +
                         "FROM posts p " +
                         "JOIN post_categories pc ON p.post_id = pc.post_id " +
                         "JOIN categories c ON pc.category_id = c.category_id " +
                         "WHERE c.name = ? AND p.status = 'published' " +
                         "ORDER BY p.published_at DESC";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, category);
                
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Article article = new Article();
                        article.setId(resultSet.getLong("post_id"));
                        article.setTitle(resultSet.getString("title"));
                        article.setContent(resultSet.getString("content"));
                        article.setExcerpt(resultSet.getString("excerpt"));
                        article.setAuthor("博主"); // 默认作者
                        article.setCategory(category); // 已知分类
                        
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
            System.err.println("按分类查询文章失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Article> getArticlesByTag(String tag) {
        List<Article> articles = new java.util.ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
        
            // 查询指定标签的文章
            String sql = "SELECT DISTINCT p.post_id, p.title, p.content, p.excerpt, p.featured_image, p.reading_time_minutes, p.published_at " +
                         "FROM posts p " +
                         "JOIN post_tags pt ON p.post_id = pt.post_id " +
                         "JOIN tags t ON pt.tag_id = t.tag_id " +
                         "WHERE t.name = ? AND p.status = 'published' " +
                         "ORDER BY p.published_at DESC";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, tag);
                
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
                        
                        // 设置标签（当前查询的就是此标签）
                        article.setTags(new String[]{tag});

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
            System.err.println("按标签查询文章失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Article> getArticlesByYear(int year) {
        List<Article> articles = new java.util.ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
        
            // 查询指定年份的文章
            String sql = "SELECT p.post_id, p.title, p.content, p.excerpt, p.featured_image, p.reading_time_minutes, p.published_at " +
                         "FROM posts p " +
                         "WHERE YEAR(p.published_at) = ? AND p.status = 'published' " +
                         "ORDER BY p.published_at DESC";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, year);
                
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
            System.err.println("按年份查询文章失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Article> getPaginatedArticles(int page, int size) {
        List<Article> articles = new java.util.ArrayList<>();
        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
        
            // 查询所有已发布的文章，带分页
            String sql = "SELECT post_id, title, content, excerpt, featured_image, reading_time_minutes, published_at FROM posts WHERE status = 'published' ORDER BY published_at DESC LIMIT ? OFFSET ?";
            int offset = page * size;
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, size);
                statement.setInt(2, offset);
                
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
            System.err.println("分页查询文章失败: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    @Override
    public int getArticleCount() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
            String sql = "SELECT COUNT(*) as count FROM posts WHERE status = 'published'";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取文章总数失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int getCategoryCount() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
            String sql = "SELECT COUNT(*) as count FROM categories";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取分类总数失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public int getTagCount() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
            String sql = "SELECT COUNT(*) as count FROM tags";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取标签总数失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    @Override
    public List<Article> getArticlesWithSequentialNumbers() {
        List<Article> articles = getAllArticles();
        
        // 按发布时间倒序排列，并添加序号
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            article.setNumber(i + 1); // 设置连续序号
        }
        
        return articles;
    }
    
    @Override
    public Article getPreviousArticleById(Long currentId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
            // 查找发布时间早于当前文章或发布时间相同但ID小于当前ID的文章（即上一篇文章）
            String sql = "SELECT post_id, title, content, excerpt, featured_image, reading_time_minutes, published_at FROM posts WHERE status = 'published' AND (published_at < (SELECT published_at FROM posts WHERE post_id = ? AND status = 'published') OR (published_at = (SELECT published_at FROM posts WHERE post_id = ? AND status = 'published') AND post_id < ?)) ORDER BY published_at DESC, post_id DESC LIMIT 1";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, currentId);
                statement.setLong(2, currentId);
                statement.setLong(3, currentId);
                
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Article article = new Article();
                        article.setId(resultSet.getLong("post_id"));
                        article.setTitle(resultSet.getString("title"));
                        article.setContent(resultSet.getString("content"));
                        article.setExcerpt(resultSet.getString("excerpt"));
                        article.setAuthor("博主");
                        
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
                        
                        return article;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("查询上一篇文章失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // 没有上一篇文章时返回null
    }
    
    @Override
    public Article getNextArticleById(Long currentId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaweb", "root", "root123")) {
            // 查找发布时间晚于当前文章或发布时间相同但ID大于当前ID的文章（即下一篇文章）
            String sql = "SELECT post_id, title, content, excerpt, featured_image, reading_time_minutes, published_at FROM posts WHERE status = 'published' AND (published_at > (SELECT published_at FROM posts WHERE post_id = ? AND status = 'published') OR (published_at = (SELECT published_at FROM posts WHERE post_id = ? AND status = 'published') AND post_id > ?)) ORDER BY published_at ASC, post_id ASC LIMIT 1";
            
            try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, currentId);
                statement.setLong(2, currentId);
                statement.setLong(3, currentId);
                
                try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Article article = new Article();
                        article.setId(resultSet.getLong("post_id"));
                        article.setTitle(resultSet.getString("title"));
                        article.setContent(resultSet.getString("content"));
                        article.setExcerpt(resultSet.getString("excerpt"));
                        article.setAuthor("博主");
                        
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
                        
                        return article;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("查询下一篇文章失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // 没有下一篇文章时返回null
    }
}