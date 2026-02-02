package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Entity.Article;
import com.zts.web_demo2.Service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final ArticleService articleService;
    
    public AdminController(ArticleService articleService) {
        this.articleService = articleService;
    }
    
    // 后台管理首页
    @GetMapping
    public String adminHome(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 10; // 每页显示10篇文章
        List<Article> articles = articleService.getPaginatedArticles(page, pageSize);
        int totalArticles = articleService.getArticleCount();
        int totalPages = (int) Math.ceil((double) totalArticles / pageSize);
        
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalArticles", totalArticles);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("articleCount", totalArticles);
        model.addAttribute("categoryCount", articleService.getCategoryCount());
        model.addAttribute("tagCount", articleService.getTagCount());
        return "admin/index"; // 返回后台管理首页模板
    }
    
    // 文章管理页面
    @GetMapping("/articles")
    public String articlesManagement(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 10; // 每页显示10篇文章
        List<Article> articles = articleService.getPaginatedArticles(page, pageSize);
        int totalArticles = articleService.getArticleCount();
        int totalPages = (int) Math.ceil((double) totalArticles / pageSize);
        
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalArticles", totalArticles);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("articleCount", totalArticles);
        model.addAttribute("categoryCount", articleService.getCategoryCount());
        model.addAttribute("tagCount", articleService.getTagCount());
        return "admin/articles"; // 返回文章管理页面模板
    }
    
    // 编辑文章页面
    @GetMapping("/articles/edit/{id}")
    public String editArticle(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id);
        if (article != null) {
            model.addAttribute("article", article);
            return "admin/edit-article"; // 返回编辑文章页面模板
        } else {
            return "redirect:/admin/articles";
        }
    }
    
    // 新建文章页面
    @GetMapping("/articles/new")
    public String newArticle(Model model) {
        model.addAttribute("article", new Article());
        return "admin/edit-article"; // 使用相同的编辑页面，但传入空对象
    }
    
    // 更新文章
    @PostMapping("/articles/update/{id}")
    public String updateArticle(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String excerpt,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Integer readTime,
            Model model) {
        
        try {
            Article article = articleService.getArticleById(id);
            if (article == null) {
                model.addAttribute("error", "找不到要编辑的文章");
                return "admin/edit-article";
            }
            
            // 更新文章属性
            article.setTitle(title != null ? title : article.getTitle());
            article.setContent(content != null ? content : article.getContent());
            article.setExcerpt(excerpt != null ? excerpt : article.getExcerpt());
            article.setAuthor(author != null ? author : article.getAuthor());
            article.setCategory(category != null ? category : article.getCategory());
            article.setImageUrl(imageUrl != null ? imageUrl : article.getImageUrl());
            article.setReadTime(readTime != null ? readTime : article.getReadTime());
            
            // 处理标签 - 按逗号分割
            if (tags != null && !tags.trim().isEmpty()) {
                String[] tagArray = tags.split(",");
                for (int i = 0; i < tagArray.length; i++) {
                    tagArray[i] = tagArray[i].trim();
                }
                article.setTags(tagArray);
            }
            
            Article updatedArticle = articleService.updateArticle(id, article);
            if (updatedArticle != null) {
                return "redirect:/admin/articles";
            } else {
                model.addAttribute("error", "更新文章失败");
                model.addAttribute("article", article);
                return "admin/edit-article";
            }
        } catch (Exception e) {
            model.addAttribute("error", "更新文章时发生错误: " + e.getMessage());
            Article article = articleService.getArticleById(id);
            model.addAttribute("article", article != null ? article : new Article());
            return "admin/edit-article";
        }
    }
    
    // 创建新文章
    @PostMapping("/articles/create")
    public String createArticle(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String excerpt,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Integer readTime,
            Model model) {
        
        try {
            Article article = new Article();
            article.setTitle(title);
            article.setContent(content);
            article.setExcerpt(excerpt);
            article.setAuthor(author);
            article.setCategory(category);
            article.setImageUrl(imageUrl);
            article.setReadTime(readTime);
            
            // 处理标签 - 按逗号分割
            if (tags != null && !tags.trim().isEmpty()) {
                String[] tagArray = tags.split(",");
                for (int i = 0; i < tagArray.length; i++) {
                    tagArray[i] = tagArray[i].trim();
                }
                article.setTags(tagArray);
            }
            
            Article createdArticle = articleService.createArticle(article);
            if (createdArticle != null) {
                return "redirect:/admin/articles";
            } else {
                model.addAttribute("error", "创建文章失败");
                model.addAttribute("article", article);
                return "admin/edit-article";
            }
        } catch (Exception e) {
            model.addAttribute("error", "创建文章时发生错误: " + e.getMessage());
            Article article = new Article();
            model.addAttribute("article", article);
            return "admin/edit-article";
        }
    }
    
    // 删除文章
    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        try {
            boolean success = articleService.deleteArticle(id);
            if (success) {
                return "redirect:/admin/articles";
            }
        } catch (Exception e) {
            // 可以添加删除失败的消息
        }
        return "redirect:/admin/articles";
    }
}