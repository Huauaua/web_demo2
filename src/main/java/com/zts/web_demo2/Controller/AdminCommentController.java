package com.zts.web_demo2.Controller;

import com.zts.web_demo2.Entity.Comment;
import com.zts.web_demo2.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/comments")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    // 评论管理页面
    @GetMapping
    public String commentsManagement(@RequestParam(required = false) String status, Model model) {
        List<Comment> comments;
        
        if (status != null && !status.isEmpty()) {
            // 这里需要添加按状态筛选的逻辑
            comments = getCommentsByStatus(status);
        } else {
            comments = commentService.getAllComments();
        }
        
        model.addAttribute("comments", comments);
        return "admin/comments"; // 返回评论管理页面模板
    }
    
    // 根据状态获取评论的辅助方法
    private List<Comment> getCommentsByStatus(String status) {
        return commentService.getCommentsByStatus(status);
    }

    // 审核评论
    @PostMapping("/approve/{id}")
    public String approveComment(@PathVariable Long id) {
        commentService.approveComment(id);
        return "redirect:/admin/comments";
    }

    // 拒绝评论
    @PostMapping("/reject/{id}")
    public String rejectComment(@PathVariable Long id) {
        commentService.rejectComment(id);
        return "redirect:/admin/comments";
    }
    

    // 删除评论
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return "redirect:/admin/comments";
    }
    

    // 批量操作：审核选中的评论
    @PostMapping("/batch/approve")
    public String batchApproveComments(@RequestParam("ids") List<Long> ids) {
        for (Long id : ids) {
            commentService.approveComment(id);
        }
        return "redirect:/admin/comments";
    }
    

    // 批量操作：拒绝选中的评论
    @PostMapping("/batch/reject")
    public String batchRejectComments(@RequestParam("ids") List<Long> ids) {
        for (Long id : ids) {
            commentService.rejectComment(id);
        }
        return "redirect:/admin/comments";
    }
    

    // 批量操作：删除选中的评论
    @PostMapping("/batch/delete")
    public String batchDeleteComments(@RequestParam("ids") List<Long> ids) {
        for (Long id : ids) {
            commentService.deleteComment(id);
        }
        return "redirect:/admin/comments";
    }
    
}