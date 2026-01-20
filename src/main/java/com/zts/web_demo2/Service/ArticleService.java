package com.zts.web_demo2.Service;

import com.zts.web_demo2.Entity.Article;
import java.util.List;

public interface ArticleService {
    List<Article> getAllArticles();
    Article getArticleById(Long id);
    List<Article> getArticlesByCategory(String category);
    List<Article> getArticlesByTag(String tag);
    List<Article> getArticlesByYear(int year);
    List<Article> getPaginatedArticles(int page, int size);
    
    // 新增方法：创建文章
    Article createArticle(Article article);
    
    // 新增方法：更新文章
    Article updateArticle(Long id, Article article);
    
    // 新增方法：删除文章
    boolean deleteArticle(Long id);
}