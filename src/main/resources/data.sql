-- 插入示例标签数据
INSERT INTO tags (name, slug) VALUES
('前端开发', 'frontend'),
('JavaScript', 'javascript'),
('TypeScript', 'typescript'),
('React', 'react'),
('Vue', 'vue'),
('效率工具', 'productivity-tools'),
('学习方法', 'learning-methods'),
('旅行', 'travel'),
('摄影', 'photography'),
('Python', 'python'),
('数据分析', 'data-analysis'),
('健康', 'health'),
('远程工作', 'remote-work'),
('CSS', 'css'),
('Node.js', 'nodejs');

-- 插入示例文章数据
INSERT INTO posts (title, slug, excerpt, content, featured_image, meta_title, meta_description, status, reading_time_minutes, published_at) VALUES
(
  '现代前端开发趋势与思考',
  'modern-frontend-development-trends',
  '探索2023年及以后的前端开发趋势，从框架演进到开发范式的变化，以及如何在这些变化中保持竞争力。',
  '<p>在快速发展的前端开发领域，我们需要时刻关注技术趋势的变化。从组件化架构到微前端，从静态站点生成到边缘计算，每一项新技术都在推动着行业的进步。</p><p>本文将详细分析当前主流的前端技术栈及其应用场景，帮助开发者做出合适的技术选型决策。</p>',
  'https://images.unsplash.com/photo-1547658719-da2b51169166?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  '现代前端开发趋势与思考',
  '探索2023年及以后的前端开发趋势，从框架演进到开发范式的变化',
  'published',
  8,
  NOW()
),
(
  '提升工作效率的10个实用技巧',
  '10-practical-tips-for-improving-work-efficiency',
  '在信息过载的时代，如何保持专注并高效完成工作？分享我实践过的10个有效方法，希望能对你有所帮助。',
  '<p>在当今快节奏的工作环境中，提高效率是每个人都追求的目标。本文分享了10个经过实践验证的效率提升技巧：</p><ol><li>番茄工作法的应用</li><li>任务优先级划分</li><li>自动化重复性工作</li><li>合理安排休息时间</li><li>使用合适的工具</li></ol>',
  'https://images.unsplash.com/photo-1552664730-d307ca884978?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  '提升工作效率的10个实用技巧',
  '在信息过载的时代，如何保持专注并高效完成工作？分享10个实用技巧',
  'published',
  6,
  NOW()
),
(
  '记录旅行的意义：摄影与文字的双重记忆',
  'the-meaning-of-recording-travel',
  '旅行不仅仅是去到一个新地方，更是对自我认知的扩展。通过摄影和文字记录旅程，让记忆更加鲜活而深刻。',
  '<p>每一次旅行都是独特的体验，值得被永久保存。摄影能够捕捉瞬间的美好，而文字则能记录当时的情感和思考。</p><p>本文分享了我在旅行中记录经历的心得体会，以及如何将这些珍贵的记忆整理成有意义的内容。</p>',
  'https://images.unsplash.com/photo-1503220317375-aaad61436b1b?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  '记录旅行的意义：摄影与文字的双重记忆',
  '旅行不仅仅是去到一个新地方，更是对自我认知的扩展。通过摄影和文字记录旅程',
  'published',
  7,
  NOW()
),
(
  '高效学习的方法论：从被动接收主动构建',
  'efficient-learning-methodology',
  '在学习新知识时，我们常常陷入被动接收信息的误区。本文将探讨如何转变为主动构建知识体系的学习者。',
  '<p>传统的学习方式往往是被动接受信息，但真正的掌握需要主动构建知识体系。本文介绍了几种高效学习方法：</p><ul><li>费曼学习法</li><li>间隔重复</li><li>思维导图</li><li>实践应用</li></ul><p>这些方法可以帮助你更有效地掌握新知识。</p>',
  'https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  '高效学习的方法论：从被动接收主动构建',
  '探讨如何转变为主动构建知识体系的学习者，介绍高效学习方法',
  'published',
  9,
  NOW()
),
(
  'Python数据分析入门与实践指南',
  'python-data-analysis-guide',
  '使用Python进行数据分析已成为现代职场的重要技能。本文将从基础概念到实际案例，带你快速入门数据分析。',
  '<p>Python凭借其丰富的库生态系统，成为了数据分析领域的首选语言。本文将介绍pandas、numpy、matplotlib等核心库的使用方法。</p><pre><code>import pandas as pd\nimport numpy as np\nimport matplotlib.pyplot as plt\n\n# 示例代码\nseries = pd.Series([1, 2, 3, 4])\nprint(series.mean())</code></pre>',
  'https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  'Python数据分析入门与实践指南',
  '从基础概念到实际案例，带你快速入门Python数据分析',
  'published',
  12,
  NOW()
),
(
  '远程工作者的健康管理策略',
  'remote-worker-health-management',
  '随着远程工作成为常态，如何在工作与生活之间找到平衡，保持身心健康？分享我的实践经验。',
  '<p>远程工作带来了灵活性，但也带来了新的挑战。在家办公容易模糊工作与生活的界限，影响身心健康。</p><p>本文分享了一些实用的健康管理策略：</p><ul><li>制定规律作息</li><li>设置独立工作空间</li><li>定时运动</li><li>保持社交</li></ul>',
  'https://images.unsplash.com/photo-1573164713988-8665fc963095?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  '远程工作者的健康管理策略',
  '分享远程工作状态下保持身心健康的实用策略',
  'published',
  7,
  NOW()
),
(
  'TypeScript高级类型使用指南',
  'typescript-advanced-types-guide',
  '深入探讨TypeScript的高级类型特性，包括条件类型、映射类型和模板字面量类型，提升类型安全性和代码质量。',
  '<p>TypeScript的高级类型系统提供了强大的类型操作能力。本文将详细介绍以下概念：</p><ul><li>条件类型 (Conditional Types)</li><li>映射类型 (Mapped Types)</li><li>模板字面量类型 (Template Literal Types)</li></ul><p>这些特性可以帮助编写更安全、更灵活的代码。</p>',
  'https://images.unsplash.com/photo-1604656770886-403d1f384c90?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  'TypeScript高级类型使用指南',
  '深入探讨TypeScript的高级类型特性，提升类型安全性',
  'published',
  10,
  NOW()
),
(
  '如何建立个人知识管理系统',
  'personal-knowledge-management-system',
  '在这个信息爆炸的时代，建立一个有效的个人知识管理系统至关重要。分享我的知识管理流程和工具选择。',
  '<p>有效的知识管理不仅包括信息的收集，更重要的是组织、加工和应用。一个好的知识管理系统应该具备以下特点：</p><ol><li>易于检索</li><li>支持关联</li><li>便于更新</li><li>可长期维护</li></ol><p>本文将分享具体的实施方法。</p>',
  'https://images.unsplash.com/photo-1517487881594-2787fef5ebf7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
  '如何建立个人知识管理系统',
  '分享建立有效的个人知识管理系统的方法和工具',
  'published',
  8,
  NOW()
);

-- 将文章与分类关联
INSERT INTO post_categories (post_id, category_id, is_primary) VALUES
(1, 1, TRUE), -- 现代前端开发趋势 -> 技术
(2, 2, TRUE), -- 提升工作效率 -> 效率
(3, 3, TRUE), -- 记录旅行的意义 -> 生活
(4, 4, TRUE), -- 高效学习的方法论 -> 学习
(5, 1, TRUE), -- Python数据分析 -> 技术
(6, 5, TRUE), -- 远程工作者的健康管理 -> 健康
(7, 1, TRUE), -- TypeScript高级类型 -> 技术
(8, 4, TRUE); -- 如何建立知识管理系统 -> 学习

-- 将文章与标签关联
INSERT INTO post_tags (post_id, tag_id) VALUES
(1, 1), (1, 2), (1, 4), (1, 14), (1, 15), -- 前端开发, JavaScript, React, CSS, Node.js
(2, 6), (2, 10), -- 效率工具, 数据分析
(3, 8), (3, 9), -- 旅行, 摄影
(4, 7), (4, 10), -- 学习方法, 数据分析
(5, 10), (5, 11), (5, 2), -- 数据分析, Python, JavaScript
(6, 12), (6, 13), -- 健康, 远程工作
(7, 3), (7, 2), (7, 1), -- TypeScript, JavaScript, 前端开发
(8, 7), (8, 10); -- 学习方法, 数据分析

-- 更新分类和标签的计数
UPDATE categories SET post_count = (
  SELECT COUNT(*)
  FROM post_categories pc
  JOIN posts p ON pc.post_id = p.post_id
  WHERE pc.category_id = categories.category_id
  AND p.status = 'published'
);

UPDATE tags SET post_count = (
  SELECT COUNT(*)
  FROM post_tags pt
  JOIN posts p ON pt.post_id = p.post_id
  WHERE pt.tag_id = tags.tag_id
  AND p.status = 'published'
);