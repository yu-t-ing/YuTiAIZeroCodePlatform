-- 修改用户头像字段类型，支持存储base64图片
-- @author 姜瓷
-- 执行前请确认：use yu_ti_code_mother;

USE yu_ti_code_mother;

-- 修改 userAvatar 字段为 LONGTEXT，支持存储大图片
ALTER TABLE user MODIFY COLUMN userAvatar LONGTEXT COMMENT '用户头像';
