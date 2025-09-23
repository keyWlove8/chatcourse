-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_chat DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_chat;

-- 用户信息表
CREATE TABLE IF NOT EXISTS user_info (
    user_id VARCHAR(50) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    type VARCHAR(20) DEFAULT 'user' COMMENT '用户类型：user/admin',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户信息表';

-- 聊天详情表
CREATE TABLE IF NOT EXISTS chat_detail (
    id VARCHAR(50) PRIMARY KEY COMMENT '详情ID',
    creator_id VARCHAR(50) NOT NULL COMMENT '创建者ID',
    memory_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话ID',
    create_time BIGINT NOT NULL COMMENT '创建时间戳',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_memory_id (memory_id)
) COMMENT '聊天详情表';

-- 聊天历史表
CREATE TABLE IF NOT EXISTS chat_history (
    id VARCHAR(50) PRIMARY KEY COMMENT '历史ID',
    memory_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话ID',
    last_question TEXT COMMENT '最后问题',
    last_answer TEXT COMMENT '最后回答',
    last_time BIGINT NOT NULL COMMENT '最后时间戳',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    creator_id VARCHAR(50) NOT NULL COMMENT '创建者ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_memory_id (memory_id),
    INDEX idx_last_time (last_time)
) COMMENT '聊天历史表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS multi_chat_message (
    id VARCHAR(50) PRIMARY KEY COMMENT '消息ID',
    memory_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    chat_message_type VARCHAR(20) NOT NULL COMMENT '消息类型：USER/AI',
    real_chat_message LONGTEXT NOT NULL COMMENT '真实聊天消息',
    contents_metadata LONGTEXT COMMENT '内容元数据',
    knowledge_id VARCHAR(50) COMMENT '知识库ID',
    timestamp BIGINT NOT NULL COMMENT '时间戳',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    detail_id VARCHAR(50) NOT NULL COMMENT '详情ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_memory_id (memory_id),
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_detail_id (detail_id)
) COMMENT '聊天消息表';

-- 知识库信息表
CREATE TABLE IF NOT EXISTS knowledge_info (
    id VARCHAR(50) PRIMARY KEY COMMENT '知识库ID',
    name VARCHAR(200) NOT NULL COMMENT '知识库名称',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    creator_id VARCHAR(50) NOT NULL COMMENT '创建者ID',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_create_time (create_time)
) COMMENT '知识库信息表';

-- AI角色表
CREATE TABLE IF NOT EXISTS ai_character (
    id VARCHAR(50) PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description TEXT COMMENT '角色描述',
    personality TEXT COMMENT '性格特征',
    background_story TEXT COMMENT '背景故事',
    speaking_style TEXT COMMENT '说话风格',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    system_prompt TEXT NOT NULL COMMENT '系统提示词',
    creator_id VARCHAR(50) NOT NULL COMMENT '创建者ID',
    is_public BOOLEAN DEFAULT true COMMENT '是否公开',
    popularity_score INT DEFAULT 0 COMMENT '热度评分',
    created_time BIGINT NOT NULL COMMENT '创建时间',
    updated_time BIGINT NOT NULL COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_name (name),
    INDEX idx_popularity (popularity_score),
    INDEX idx_created_time (created_time)
) COMMENT 'AI角色表';

-- 插入默认管理员用户
-- 密码处理流程：原始密码 -> 前端MD5 -> 后端BCrypt
-- 原始密码：123456
-- 前端MD5：e10adc3949ba59abbe56e057f20f883e
-- 后端BCrypt：$2a$12$... (BCrypt加密MD5结果)
-- 注意：这里的BCrypt哈希是对MD5结果"e10adc3949ba59abbe56e057f20f883e"的加密
INSERT INTO user_info (user_id, username, password, type) VALUES 
('admin_001', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8X8v5QKz2K', 'admin'),
('user_001', 'user', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj8X8v5QKz2K', 'user')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 插入默认角色
INSERT INTO ai_character (id, name, description, personality, background_story, speaking_style, system_prompt, creator_id, is_public, popularity_score, created_time, updated_time) VALUES 
('char_001', '小助手', '一个友好的AI助手', '友善、耐心、乐于助人', '我是一个AI助手，专门帮助用户解决问题', '说话温和、简洁明了', '你是一个友好的AI助手，请用温和、简洁的语言回答用户的问题。', 'admin_001', true, 0, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('char_002', '编程导师', '专业的编程指导老师', '专业、严谨、有耐心', '我是一名资深的编程导师，有丰富的教学经验', '说话专业、条理清晰', '你是一名专业的编程导师，请用专业、条理清晰的语言指导用户学习编程。', 'admin_001', true, 0, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000)
ON DUPLICATE KEY UPDATE name = VALUES(name);
