package com.zts.web_demo2.Service.impl;

import com.zts.web_demo2.Entity.Article;
import com.zts.web_demo2.Service.ArticleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    // 模拟文章数据
    private List<Article> articles = Arrays.asList(
        createArticle(1L, "现代前端开发趋势与思考", 
            "在前端开发领域，变化是永恒的主题。从jQuery时代到现代前端框架的兴起，再到如今各种工具链和开发范式的演进，前端开发已经变得前所未有的复杂而强大...\n\n2023年，我们看到了一些明显的趋势正在塑造前端开发的未来。这些趋势不仅关乎技术选择，更关乎开发体验、性能优化和团队协作...", 
            "探索2023年及以后的前端开发趋势，从框架演进到开发范式的变化，以及如何在这些变化中保持竞争力。",
            "张三", "技术", 
            new String[]{"前端开发", "JavaScript", "TypeScript", "React", "Vue", "性能优化"},
            LocalDateTime.of(2023, 10, 15, 10, 0),
            "https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=1170&q=80",
            8),
        
        createArticle(2L, "提升工作效率的10个实用技巧", 
            "在信息过载的时代，如何保持专注并高效完成工作？分享我实践过的10个有效方法，希望能对你有所帮助...\n\n1. 设定清晰的目标\n2. 使用番茄工作法\n3. 减少干扰源\n4. 合理安排休息时间\n5. 使用效率工具...",
            "在信息过载的时代，如何保持专注并高效完成工作？分享我实践过的10个有效方法，希望能对你有所帮助。",
            "李四", "效率", 
            new String[]{"生产力", "时间管理", "工作效率"},
            LocalDateTime.of(2023, 10, 10, 9, 30),
            "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?ixlib=rb-4.0.3&auto=format&fit=crop&w=1170&q=80",
            6),
            
        createArticle(3L, "记录旅行的意义：摄影与文字的双重记忆", 
            "旅行不仅仅是去到一个新地方，更是对自我认知的扩展。通过摄影和文字记录旅程，让记忆更加鲜活而深刻...\n\n每次旅行都是一次心灵的洗礼，通过镜头捕捉美好瞬间，用文字记录内心感受，这样的双重记忆方式让我们能更好地回味旅途中的点点滴滴...",
            "旅行不仅仅是去到一个新地方，更是对自我认知的扩展。通过摄影和文字记录旅程，让记忆更加鲜活而深刻。",
            "王五", "生活", 
            new String[]{"旅行", "摄影", "生活方式"},
            LocalDateTime.of(2023, 10, 5, 14, 15),
            "https://images.unsplash.com/photo-1517077304055-6e89abbf09b0?ixlib=rb-4.0.3&auto=format&fit=crop&w=1169&q=80",
            7),
            
        createArticle(4L, "高效学习的方法论：从被动接收主动构建", 
            "在学习新知识时，我们常常陷入被动接收信息的误区。本文将探讨如何转变为主动构建知识体系的学习者...\n\n主动学习的核心在于将知识内化，而不是简单地记忆表面信息。通过实践、反思和教授他人等方式，我们可以更有效地掌握知识...",
            "在学习新知识时，我们常常陷入被动接收信息的误区。本文将探讨如何转变为主动构建知识体系的学习者。",
            "赵六", "学习", 
            new String[]{"学习方法", "知识管理", "教育"},
            LocalDateTime.of(2023, 9, 28, 11, 20),
            "https://images.unsplash.com/photo-1515378791036-0648a3ef77b2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1170&q=80",
            9),
            
        createArticle(5L, "Python数据分析入门与实践指南", 
            "使用Python进行数据分析已成为现代职场的重要技能。本文将从基础概念到实际案例，带你快速入门数据分析...\n\nPython拥有丰富的数据分析库，如pandas、numpy、matplotlib等，这些工具可以帮助我们高效地处理和可视化数据...",
            "使用Python进行数据分析已成为现代职场的重要技能。本文将从基础概念到实际案例，带你快速入门数据分析。",
            "孙七", "技术", 
            new String[]{"Python", "数据分析", "机器学习"},
            LocalDateTime.of(2023, 9, 20, 16, 45),
            "https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&auto=format&fit=crop&w=1170&q=80",
            12),
            
        createArticle(6L, "远程工作者的健康管理策略", 
            "随着远程工作成为常态，如何在工作与生活之间找到平衡，保持身心健康？分享我的实践经验...\n\n远程工作虽然提供了更大的灵活性，但也带来了新的挑战，特别是对身体健康和心理健康的影响...",
            "随着远程工作成为常态，如何在工作与生活之间找到平衡，保持身心健康？分享我的实践经验。",
            "周八", "健康", 
            new String[]{"健康", "远程工作", "生活方式"},
            LocalDateTime.of(2023, 9, 15, 13, 10),
            "https://images.unsplash.com/photo-1545235617-9465d2a55698?ixlib=rb-4.0.3&auto=format&fit=crop&w=1160&q=80",
            7)
    );

    private Article createArticle(Long id, String title, String content, String excerpt, String author, 
                                  String category, String[] tags, LocalDateTime publishDate, String imageUrl, Integer readTime) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setContent(content);
        article.setExcerpt(excerpt);
        article.setAuthor(author);
        article.setCategory(category);
        article.setTags(tags);
        article.setPublishDate(publishDate);
        article.setImageUrl(imageUrl);
        article.setReadTime(readTime);
        return article;
    }

    @Override
    public List<Article> getAllArticles() {
        return articles;
    }

    @Override
    public Article getArticleById(Long id) {
        return articles.stream()
                .filter(article -> article.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Article> getArticlesByCategory(String category) {
        return articles.stream()
                .filter(article -> article.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getArticlesByTag(String tag) {
        return articles.stream()
                .filter(article -> Arrays.stream(article.getTags())
                        .anyMatch(t -> t.equalsIgnoreCase(tag)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getArticlesByYear(int year) {
        return articles.stream()
                .filter(article -> article.getPublishDate().getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getPaginatedArticles(int page, int size) {
        int startIndex = page * size;
        if (startIndex >= articles.size()) {
            return Arrays.asList();
        }
        int endIndex = Math.min(startIndex + size, articles.size());
        return articles.subList(startIndex, endIndex);
    }
}