-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_chat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_chat;

-- 用户信息表
CREATE TABLE IF NOT EXISTS user_info (
                                         user_id VARCHAR(50) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    type VARCHAR(20) DEFAULT 'user' COMMENT '用户类型：user/admin', -- 类型值固定且简短，保留VARCHAR
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) COMMENT '用户信息表';

-- 聊天详情表
CREATE TABLE IF NOT EXISTS chat_detail (
                                           id VARCHAR(50) PRIMARY KEY COMMENT '详情ID',
    creator_id VARCHAR(100) NOT NULL COMMENT '创建者ID',
    memory_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话ID', -- 会话ID通常是固定格式的字符串，保留VARCHAR
    create_time BIGINT NOT NULL COMMENT '创建时间戳',
    INDEX idx_creator_id (creator_id),
    INDEX idx_memory_id (memory_id)
    ) COMMENT '聊天详情表';

-- 聊天历史表
CREATE TABLE IF NOT EXISTS chat_history (
                                            id VARCHAR(50) PRIMARY KEY COMMENT '历史ID',
    memory_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话ID',
    last_question TEXT COMMENT '最后问题', -- 已使用TEXT，保留
    last_answer TEXT COMMENT '最后回答', -- 已使用TEXT，保留
    last_time BIGINT NOT NULL COMMENT '最后时间戳',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    creator_id VARCHAR(100) NOT NULL COMMENT '创建者ID',
    INDEX idx_creator_id (creator_id),
    INDEX idx_memory_id (memory_id),
    INDEX idx_last_time (last_time)
    ) COMMENT '聊天历史表';

-- 聊天消息表（重点调整）
CREATE TABLE IF NOT EXISTS multi_chat_message (
                                                  id VARCHAR(50) PRIMARY KEY COMMENT '消息ID',
    memory_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    chat_message_type VARCHAR(100) NOT NULL COMMENT '消息类型：USER/AI', -- 类型值固定且简短，保留VARCHAR
    real_chat_message LONGTEXT NOT NULL COMMENT '真实聊天消息', -- 已使用LONGTEXT，保留
    knowledge_id VARCHAR(100) COMMENT '知识库ID',
    timestamp BIGINT NOT NULL COMMENT '时间戳',
    user_id VARCHAR(100) NOT NULL COMMENT '用户ID',
    INDEX idx_memory_id (memory_id),
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp)
    ) COMMENT '聊天消息表';

-- 知识库信息表
CREATE TABLE IF NOT EXISTS knowledge_info (
                                              id VARCHAR(50) PRIMARY KEY COMMENT '知识库ID',
    name VARCHAR(200) NOT NULL COMMENT '知识库名称', -- 名称通常不会超长，保留VARCHAR
    create_time DATETIME NOT NULL COMMENT '创建时间',
    creator_id VARCHAR(100) NOT NULL COMMENT '创建者ID',
    INDEX idx_creator_id (creator_id),
    INDEX idx_create_time (create_time)
    ) COMMENT '知识库信息表';

-- AI角色表（重点调整）
CREATE TABLE IF NOT EXISTS ai_character (
                                            id VARCHAR(50) PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(100) NOT NULL COMMENT '角色名称', -- 名称通常不会超长，保留VARCHAR
    description TEXT COMMENT '角色描述', -- 已使用TEXT，保留
    personality TEXT COMMENT '性格特征', -- 已使用TEXT，保留
    background_story TEXT COMMENT '背景故事', -- 已使用TEXT，保留
    speaking_style TEXT COMMENT '说话风格', -- 已使用TEXT，保留
    avatar_url TEXT COMMENT '头像URL', -- 原VARCHAR(500)可能不够（URL可能更长），改为TEXT
    system_prompt LONGTEXT NOT NULL COMMENT '系统提示词', -- 提示词可能很长，改为LONGTEXT
    creator_id VARCHAR(100) NOT NULL COMMENT '创建者ID',
    is_public BOOLEAN DEFAULT true COMMENT '是否公开',
    popularity_score INT DEFAULT 0 COMMENT '热度评分',
    created_time BIGINT NOT NULL COMMENT '创建时间',
    updated_time BIGINT NOT NULL COMMENT '更新时间',
    voice_id VARCHAR(50) COMMENT '关联的音色ID',
    INDEX idx_creator_id (creator_id),
    INDEX idx_name (name),
    INDEX idx_popularity (popularity_score),
    INDEX idx_created_time (created_time),
    INDEX idx_voice_id (voice_id)
    ) COMMENT 'AI角色表';

-- 上下文消息表
CREATE TABLE IF NOT EXISTS context_message (
                                               item_index INT NOT NULL AUTO_INCREMENT COMMENT '项目索引',
    memory_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    chat_message_type VARCHAR(100) NOT NULL COMMENT '消息类型：USER/AI',
    contents_metadata LONGTEXT COMMENT '内容元数据',
    timestamp BIGINT NOT NULL COMMENT '时间戳',
    knowledge_id VARCHAR(100) COMMENT '知识库ID',
    PRIMARY KEY (item_index, memory_id),
    INDEX idx_memory_id (memory_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_knowledge_id (knowledge_id)
    ) COMMENT '上下文消息表';

-- 音色信息表
CREATE TABLE IF NOT EXISTS voice_info (
    id VARCHAR(50) PRIMARY KEY COMMENT '音色ID',
    voice_code VARCHAR(100) NOT NULL UNIQUE COMMENT '声音码/标识',
    description TEXT COMMENT '声音描述',
    gender VARCHAR(20) NOT NULL COMMENT '声音性别：male/female/neutral',
    name VARCHAR(100) NOT NULL COMMENT '声音名称',
    language VARCHAR(20) NOT NULL COMMENT '语言',
    create_time BIGINT NOT NULL COMMENT '创建时间',
    creator_id VARCHAR(100) NOT NULL COMMENT '创建者ID',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    INDEX idx_voice_code (voice_code),
    INDEX idx_gender (gender),
    INDEX idx_name (name),
    INDEX idx_language (language),
    INDEX idx_creator_id (creator_id),
    INDEX idx_create_time (create_time),
    INDEX idx_is_enabled (is_enabled)
) COMMENT '音色信息表';

-- 插入默认管理员用户
INSERT INTO user_info (user_id, username, password, type) VALUES
                                                              ('admin_001', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8X8v5QKz2K', 'admin'),
                                                              ('user_001', 'user', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8X8v5QKz2K', 'user')
    ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 插入默认角色
INSERT INTO ai_character (id, name, description, personality, background_story, speaking_style, system_prompt, creator_id, is_public, popularity_score, created_time, updated_time) VALUES
                                                                                                                                                                                        ('char_001', '小助手', '一个友好的AI助手', '友善、耐心、乐于助人', '我是一个AI助手，专门帮助用户解决问题', '说话温和、简洁明了', '你是一个友好的AI助手，请用温和、简洁的语言回答用户的问题。', 'admin_001', true, 0, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
                                                                                                                                                                                        ('char_002', '编程导师', '专业的编程指导老师', '专业、严谨、有耐心', '我是一名资深的编程导师，有丰富的教学经验', '说话专业、条理清晰', '你是一名专业的编程导师，请用专业、条理清晰的语言指导用户学习编程。', 'admin_001', true, 0, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000)
    ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 插入默认音色
INSERT INTO voice_info (id, voice_code, description, gender, name, language, create_time, creator_id, is_enabled) VALUES
                                                                                                        ('voice_001', 'xiaoyun', '温柔女声，适合日常对话', 'female', '小云', 'zh-CN', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_002', 'xiaogang', '沉稳男声，适合专业场景', 'male', '小刚', 'zh-CN', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_003', 'xiaoli', '活泼女声，适合轻松话题', 'female', '小丽', 'zh-CN', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_004', 'xiaoming', '中性声音，适合正式场合', 'neutral', '小明', 'zh-CN', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_005', 'alice', '温柔英文女声，适合英语学习', 'female', 'Alice', 'en-US', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_006', 'bob', '沉稳英文男声，适合商务场景', 'male', 'Bob', 'en-US', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_007', 'yuki', '温柔日文女声，适合日语学习', 'female', 'Yuki', 'ja-JP', UNIX_TIMESTAMP() * 1000, 'admin_001', 1),
                                                                                                        ('voice_008', 'takeshi', '沉稳日文男声，适合正式场合', 'male', 'Takeshi', 'ja-JP', UNIX_TIMESTAMP() * 1000, 'admin_001', 1)
    ON DUPLICATE KEY UPDATE name = VALUES(name);
