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
}