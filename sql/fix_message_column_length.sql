-- 修复 chat_history 表的 message 字段长度问题
-- 问题：TEXT 类型最大只能存储 65,535 字节（约 64KB）
-- 当 AI 生成完整代码时，内容会超过这个限制导致数据截断错误
-- 解决方案：将 message 字段改为 LONGTEXT 类型，最大可存储 4GB

-- 执行修复前请确认：
-- 1. 数据库名称为 yu_ti_code_mother
-- 2. chat_history 表已存在
-- 3. 建议先备份数据库

USE yu_ti_code_mother;

-- 修改 message 字段类型为 LONGTEXT
ALTER TABLE chat_history MODIFY COLUMN message LONGTEXT NOT NULL COMMENT '消息';

-- 验证修改是否成功
SHOW CREATE TABLE chat_history;

-- 预期结果应该显示：
-- message longtext NOT NULL COMMENT '消息'
