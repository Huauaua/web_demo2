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
    public String commentsManagement(@RequestParam(required = false) String status, @RequestParam(defaultValue = "0") int page, Model model) {
        int pageSize = 10; // 每页显示10条评论
        List<Comment> comments;
        int totalComments;
        
        if (status != null && !status.isEmpty()) {
            // 按状态筛选的分页查询
            comments = commentService.getPaginatedCommentsByStatus(page, pageSize, status);
            totalComments = commentService.getCommentCountByStatus(status);
        } else {
            // 获取所有评论的分页查询
            comments = commentService.getPaginatedComments(page, pageSize);
            totalComments = commentService.getCommentCount();
        }
        
        int totalPages = (int) Math.ceil((double) totalComments / pageSize);
        
        model.addAttribute("comments", comments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalComments", totalComments);
        model.addAttribute("hasPrev", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("status", status); // 保持状态参数
        
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
    public String batchApproveComments(@RequestParam(value = "ids", required = false) List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                commentService.approveComment(id);
            }
        }
        return "redirect:/admin/comments";
    }
    

    // 批量操作：拒绝选中的评论
    @PostMapping("/batch/reject")
    public String batchRejectComments(@RequestParam(value = "ids", required = false) List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                commentService.rejectComment(id);
            }
        }
        return "redirect:/admin/comments";
    }
    

    // 批量操作：删除选中的评论
    @PostMapping("/batch/delete")
    public String batchDeleteComments(@RequestParam(value = "ids", required = false) List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                commentService.deleteComment(id);
            }
        }
        return "redirect:/admin/comments";
    }
    
    // 后台管理API：获取所有评论
    @GetMapping("/api/all")
    @ResponseBody
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }
    
    // 后台管理API：审核评论
    @PostMapping("/api/approve/{id}")
    @ResponseBody
    public Comment approveCommentApi(@PathVariable Long id) {
        return commentService.approveComment(id);
    }
    
    // 后台管理API：拒绝评论
    @PostMapping("/api/reject/{id}")
    @ResponseBody
    public Comment rejectCommentApi(@PathVariable Long id) {
        return commentService.rejectComment(id);
    }
    
    // 后台管理API：删除评论
    @PostMapping("/api/delete/{id}")
    @ResponseBody
    public boolean deleteCommentApi(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }
    
}