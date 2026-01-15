package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Entity.Article;
import com.zts.web_demo2.Service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 显示文章列表页面
    @GetMapping
    public String articlesPage(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return "articles"; // 返回 articles.html 模板
    }

    // 获取所有文章（JSON格式）
    @GetMapping("/api/all")
    @ResponseBody
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    // 根据ID获取单篇文章
    @GetMapping("/api/{id}")
    @ResponseBody
    public Article getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    // 根据分类获取文章
    @GetMapping("/api/category/{category}")
    @ResponseBody
    public List<Article> getArticlesByCategory(@PathVariable String category) {
        return articleService.getArticlesByCategory(category);
    }

    // 根据标签获取文章
    @GetMapping("/api/tag/{tag}")
    @ResponseBody
    public List<Article> getArticlesByTag(@PathVariable String tag) {
        return articleService.getArticlesByTag(tag);
    }

    // 根据年份获取文章
    @GetMapping("/api/year/{year}")
    @ResponseBody
    public List<Article> getArticlesByYear(@PathVariable int year) {
        return articleService.getArticlesByYear(year);
    }

    // 分页获取文章
    @GetMapping("/api/page/{page}/size/{size}")
    @ResponseBody
    public List<Article> getPaginatedArticles(@PathVariable int page, @PathVariable int size) {
        return articleService.getPaginatedArticles(page, size);
    }

    // 显示单篇文章详情页面
    @GetMapping("/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article != null) {
            model.addAttribute("article", article);
            return "article"; // 返回 article.html 模板
        } else {
            return "redirect:/articles"; // 如果文章不存在，重定向到文章列表
        }
    }
}