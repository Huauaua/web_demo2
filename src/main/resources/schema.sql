Create DATABASE if not exists myblog;

CREATE TABLE posts
(
    post_id              INT PRIMARY KEY AUTO_INCREMENT,
    title                VARCHAR(200)        NOT NULL,
    slug                 VARCHAR(200) UNIQUE NOT NULL COMMENT 'URL友好标识',
    excerpt              TEXT COMMENT '文章摘要',
    content              LONGTEXT            NOT NULL COMMENT '文章内容',
    featured_image       VARCHAR(500) COMMENT '封面图片URL',

    -- 元数据
    meta_title           VARCHAR(200),
    meta_description     VARCHAR(500),
    meta_keywords        VARCHAR(500),

    -- 状态和统计
    status               ENUM ('draft', 'published', 'archived') DEFAULT 'draft',
    view_count           INT                                     DEFAULT 0,
    comment_count        INT                                     DEFAULT 0,
    reading_time_minutes INT COMMENT '预计阅读时间(分钟)',

    -- 时间信息
    published_at         TIMESTAMP           NULL COMMENT '发布时间',
    created_at           TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 索引
    INDEX idx_status_published (status, published_at),
    INDEX idx_created_at (created_at),
    INDEX idx_slug (slug)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 添加全文索引用于搜索
ALTER TABLE posts
    ADD FULLTEXT INDEX ft_search (title, excerpt, content);

CREATE TABLE categories
(
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50)        NOT NULL,
    slug        VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    sort_order  INT       DEFAULT 0 COMMENT '排序权重',
    post_count  INT       DEFAULT 0 COMMENT '文章数量统计',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 插入默认分类
INSERT INTO categories (name, slug, description)
VALUES ('技术', 'technology', '技术相关的文章'),
       ('效率', 'efficiency', '提升工作效率的方法和工具'),
       ('生活', 'life', '生活感悟和日常记录'),
       ('学习', 'learning', '学习方法和知识管理'),
       ('健康', 'health', '健康管理和生活平衡'),
       ('旅行', 'travel', '旅行见闻和摄影');

CREATE TABLE tags
(
    tag_id     INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50)        NOT NULL,
    slug       VARCHAR(50) UNIQUE NOT NULL,
    post_count INT       DEFAULT 0 COMMENT '文章数量统计',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE post_categories
(
    post_id     INT NOT NULL,
    category_id INT NOT NULL,
    is_primary  BOOLEAN DEFAULT FALSE COMMENT '是否为主要分类',
    PRIMARY KEY (post_id, category_id),
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (category_id) ON DELETE CASCADE,
    INDEX idx_category (category_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE post_tags
(
    post_id INT NOT NULL,
    tag_id  INT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags (tag_id) ON DELETE CASCADE,
    INDEX idx_tag (tag_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE comments
(
    comment_id     INT PRIMARY KEY AUTO_INCREMENT,
    post_id        INT          NOT NULL,
    author_name    VARCHAR(100) NOT NULL,
    author_email   VARCHAR(100),
    author_website VARCHAR(255),
    content        TEXT         NOT NULL,
    status         ENUM ('approved', 'pending', 'spam') DEFAULT 'pending',
    created_at     TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    INDEX idx_post_status (post_id, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DELIMITER //

-- 创建第一个存储过程
CREATE PROCEDURE update_category_count(IN cat_id INT)
BEGIN
    UPDATE categories c
    SET c.post_count = (SELECT COUNT(DISTINCT pc.post_id)
                        FROM post_categories pc
                                 INNER JOIN posts p ON pc.post_id = p.post_id
                        WHERE pc.category_id = cat_id
                          AND p.status = 'published')
    WHERE c.category_id = cat_id;
END //

-- 创建第二个存储过程
CREATE PROCEDURE update_tag_count(IN t_id INT)
BEGIN
    UPDATE tags t
    SET t.post_count = (SELECT COUNT(DISTINCT pt.post_id)
                        FROM post_tags pt
                                 INNER JOIN posts p ON pt.post_id = p.post_id
                        WHERE pt.tag_id = t_id
                          AND p.status = 'published')
    WHERE t.tag_id = t_id;
END //

DELIMITER ;

-- 当文章状态改变时，更新相关分类和标签的计数
DELIMITER $$
CREATE TRIGGER after_post_update
    AFTER UPDATE
    ON posts
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        -- 更新文章涉及的所有分类
        UPDATE categories c
            JOIN post_categories pc
            ON c.category_id = pc.category_id
        SET c.post_count = (SELECT COUNT(DISTINCT pc2.post_id)
                            FROM post_categories pc2
                                     JOIN posts p ON pc2.post_id = p.post_id
                            WHERE pc2.category_id = c.category_id
                              AND p.status = 'published')
        WHERE pc.post_id = NEW.post_id;

        -- 更新文章涉及的所有标签
        UPDATE tags t
            JOIN post_tags pt
            ON t.tag_id = pt.tag_id
        SET t.post_count = (SELECT COUNT(DISTINCT pt2.post_id)
                            FROM post_tags pt2
                                     JOIN posts p ON pt2.post_id = p.post_id
                            WHERE pt2.tag_id = t.tag_id
                              AND p.status = 'published')
        WHERE pt.post_id = NEW.post_id;
    END IF;
END$$
DELIMITER ;