package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Config.MailConfig;
import com.zts.web_demo2.Service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Controller
public class HomeController {
    private final ArticleService articleService;
    private final MailConfig mailConfig;
    
    @Autowired
    private JavaMailSender mailSender;

    public HomeController(ArticleService articleService, MailConfig mailConfig) {
        this.articleService = articleService;
        this.mailConfig = mailConfig;
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
    
    @PostMapping("/contact")
    @ResponseBody
    public String sendContactEmail(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String subject,
                                   @RequestParam String message,
                                   @RequestParam(required = false) String category) {
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            // 设置邮件信息
            helper.setTo(mailConfig.getToEmail());
            helper.setSubject("网站联系表单 - " + subject);
            helper.setFrom(mailConfig.getFromEmail());
            
            // 构建邮件内容
            StringBuilder content = new StringBuilder();
            content.append("<h2>新联系消息</h2>");
            content.append("<p><strong>姓名：</strong> ").append(name).append("</p>");
            content.append("<p><strong>邮箱：</strong> ").append(email).append("</p>");
            content.append("<p><strong>主题：</strong> ").append(subject).append("</p>");
            if (category != null && !category.isEmpty()) {
                content.append("<p><strong>消息类别：</strong> ").append(category).append("</p>");
            }
            content.append("<p><strong>消息内容：</strong></p>");
            content.append("<p>").append(message.replace("\n", "<br>")).append("</p>");
            content.append("<p><em>发送时间：").append(java.time.LocalDateTime.now()).append("</em></p>");
            
            helper.setText(content.toString(), true);
            
            mailSender.send(mimeMessage);
            return "success";
            
        } catch (MessagingException e) {
            e.printStackTrace();
            return "error";
        }
    }
}