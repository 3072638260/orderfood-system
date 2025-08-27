-- 修复admin账号登录问题
-- 问题：管理员登录验证User表，但数据库初始化脚本只在Customer表中创建了admin账号
-- 解决方案：在User表中创建admin账号，并确保密码使用MD5加密

USE `system-orderfood-op`;

-- 1. 创建User表（如果不存在）
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `image` varchar(200) DEFAULT NULL COMMENT '头像',
  `role` varchar(20) DEFAULT 'admin' COMMENT '角色',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员用户表';

-- 2. 插入admin账号到User表（密码：123456，MD5加密后：e10adc3949ba59abbe56e057f20f883e）
INSERT IGNORE INTO `user` (`user_name`, `password`, `email`, `role`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@orderfood.com', 'admin');

-- 3. 同步更新Customer表中的admin密码为MD5加密
UPDATE `customer` SET `password` = 'e10adc3949ba59abbe56e057f20f883e' WHERE `customer_name` = 'admin';

-- 4. 更新其他测试用户的密码为MD5加密（密码：123456）
UPDATE `customer` SET `password` = 'e10adc3949ba59abbe56e057f20f883e' WHERE `customer_name` IN ('张三', '李四', '王五');

-- 5. 验证数据
SELECT '=== User表admin账号 ===' as message;
SELECT * FROM `user` WHERE `user_name` = 'admin';

SELECT '=== Customer表admin账号 ===' as message;
SELECT `id`, `customer_name`, `password`, `email` FROM `customer` WHERE `customer_name` = 'admin';

SELECT '=== 修复完成 ===' as message;
SELECT '管理员登录地址: http://localhost:8088' as admin_login;
SELECT '用户登录地址: http://localhost:8088/toUserLogin' as user_login;
SELECT '默认账号: admin, 密码: 123456' as default_account;

COMMIT;