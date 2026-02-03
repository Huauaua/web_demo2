package com.zts.web_demo2.Util;

import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.AttributeProvider;

import java.util.Map;

public class CustomAttributeProvider implements AttributeProvider {

    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
        // 为链接添加target="_blank"和rel="noopener"属性
        if (node instanceof Link) {
            attributes.put("target", "_blank");
            attributes.put("rel", "noopener noreferrer");
        }
        
        // 为代码块添加基础样式
        if ("code".equals(tagName)) {
            attributes.put("class", "code-inline");
        }
        
        if ("pre".equals(tagName)) {
            attributes.put("class", "code-block");
        }
    }
}