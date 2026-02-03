package com.zts.web_demo2.Util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;

public class MarkdownUtil {
    
    private static final Parser PARSER;
    private static final HtmlRenderer RENDERER;
    
    static {
        // 配置解析器，添加常用的Markdown扩展
        List<Extension> extensions = Arrays.asList(
            TablesExtension.create(),           // 表格支持
            HeadingAnchorExtension.create(),    // 标题锚点
            TaskListItemsExtension.create()     // 任务列表
        );
        
        PARSER = Parser.builder()
            .extensions(extensions)
            .build();
            
        RENDERER = HtmlRenderer.builder()
            .extensions(extensions)
            .attributeProviderFactory(context -> new CustomAttributeProvider())
            .build();
    }
    
    /**
     * 将Markdown文本转换为HTML
     * @param markdown Markdown文本
     * @return 转换后的HTML
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        Node document = PARSER.parse(markdown);
        return RENDERER.render(document);
    }
    
    /**
     * 生成文章摘要（从Markdown内容中提取纯文本）
     * @param markdown Markdown文本
     * @param maxLength 最大长度
     * @return 摘要文本
     */
    public static String generateExcerpt(String markdown, int maxLength) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        // 移除Markdown标记，提取纯文本
        String plainText = markdown.replaceAll("[*\\-_`\\[\\]()#+.!]", "");
        plainText = plainText.replaceAll("\\s+", " ").trim();
        
        if (plainText.length() <= maxLength) {
            return plainText;
        }
        
        return plainText.substring(0, maxLength) + "...";
    }
}