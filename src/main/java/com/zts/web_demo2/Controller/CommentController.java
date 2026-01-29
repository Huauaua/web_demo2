package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Entity.Comment;
import com.zts.web_demo2.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 获取文章的所有已批准评论（API接口）
    @GetMapping("/api/post/{postId}")
    @ResponseBody
    public List<Comment> getCommentsByPost(@PathVariable Long postId) {
        return commentService.getApprovedCommentsByPostId(postId);
    }

    // 提交评论
    @PostMapping("/api/submit")
    @ResponseBody
    public Comment submitComment(@RequestBody Comment comment) {
        // 设置默认状态为待审核
        comment.setStatus("pending");
        return commentService.saveComment(comment);
    }

    // 提交评论（表单方式）
    @PostMapping("/submit")
    public String submitCommentForm(@RequestParam Long postId,
                                   @RequestParam String authorName,
                                   @RequestParam String authorEmail,
                                   @RequestParam String content,
                                   @RequestParam(required = false) String authorWebsite,
                                   Model model) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorName(authorName);
        comment.setAuthorEmail(authorEmail);
        comment.setAuthorWebsite(authorWebsite);
        comment.setContent(content);

        Comment savedComment = commentService.saveComment(comment);

        // 重定向到文章详情页
        return "redirect:/articles/" + postId;
    }

    // 获取文章评论总数
    @GetMapping("/api/count/{postId}")
    @ResponseBody
    public int getCommentCount(@PathVariable Long postId) {
        return commentService.getCommentCountByPostId(postId);
    }
}