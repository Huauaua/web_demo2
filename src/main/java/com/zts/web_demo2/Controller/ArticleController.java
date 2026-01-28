package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Entity.Article;
import com.zts.web_demo2.Entity.Comment;
import com.zts.web_demo2.Service.ArticleService;
import com.zts.web_demo2.Service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final CommentService commentService;

    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
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
        Article prevArticle = articleService.getPreviousArticleById(id);
        Article nextArticle = articleService.getNextArticleById(id);
        if(prevArticle != null) article.setPreviousTitle(prevArticle.getTitle());
        else article.setPreviousTitle(null);
        if(nextArticle != null) article.setNextTitle(nextArticle.getTitle());
        else article.setNextTitle(null);
        model.addAttribute("article", article);
        
        // 添加评论数据
        List<Comment> comments = commentService.getApprovedCommentsByPostId(id);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", comments.size());
        
        return "article"; // 返回 article.html 模板
    }

    // 显示写博客页面
    @GetMapping("/write")
    public String writeArticlePage(Model model) {
        model.addAttribute("article", new Article());
        return "write-article"; // 返回 write-article.html 模板
    }
    // 创建新文章
    @PostMapping("/api/create")
    @ResponseBody
    public Article createArticle(@RequestBody Article article) {
        return articleService.createArticle(article);
    }

    // 更新文章
    @PutMapping("/api/update/{id}")
    @ResponseBody
    public Article updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return articleService.updateArticle(id, article);
    }

    // 删除文章
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public boolean deleteArticle(@PathVariable Long id) {
        return articleService.deleteArticle(id);
    }

    // 发布文章页面
    @PostMapping("/publish")
    public String publishArticle(Article article, Model model) {
        Article savedArticle = articleService.createArticle(article);
        return "redirect:/articles/" + savedArticle.getId();
    }
}