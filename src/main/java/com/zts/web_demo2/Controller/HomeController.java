package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final ArticleService articleService;

    public HomeController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "index"; // 返回主页模板
    }
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "about";
    }
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("articles", articleService.getAllArticles());
        return "contact";
    }

}