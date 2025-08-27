-- 网上订餐系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `system-orderfood-op` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `system-orderfood-op`;

-- 1. 创建客户表
CREATE TABLE IF NOT EXISTS `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `customer_name` varchar(50) NOT NULL COMMENT '客户姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `cimage` varchar(200) DEFAULT NULL COMMENT '头像',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_name` (`customer_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户表';

-- 2. 创建商家表
CREATE TABLE IF NOT EXISTS `store` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `store_name` varchar(100) NOT NULL COMMENT '商家名称',
  `descr` text COMMENT '商家描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家表';

-- 3. 创建菜品表
CREATE TABLE IF NOT EXISTS `food` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `food_name` varchar(100) NOT NULL COMMENT '菜品名称',
  `store` varchar(100) DEFAULT NULL COMMENT '所属商家',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int(11) DEFAULT 0 COMMENT '库存',
  `descr` text COMMENT '菜品描述',
  `fimage` varchar(200) DEFAULT NULL COMMENT '菜品图片',
  `status` int(1) DEFAULT 1 COMMENT '状态(1:上架 0:下架)',
  `star` double DEFAULT 0 COMMENT '评分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜品表';

-- 4. 创建订单表
CREATE TABLE IF NOT EXISTS `f_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `cid` int(11) NOT NULL COMMENT '客户ID',
  `fid` int(11) NOT NULL COMMENT '菜品ID',
  `count` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `total` decimal(10,2) NOT NULL COMMENT '总价',
  `order_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `status` int(1) DEFAULT 0 COMMENT '订单状态(0:待处理 1:已完成 2:已取消)',
  `isorder` int(1) DEFAULT 0 COMMENT '是否已下单(0:购物车 1:已下单)',
  `dessert` int(11) DEFAULT 0 COMMENT '甜品',
  `drink` int(11) DEFAULT 0 COMMENT '饮料',
  `fruit` int(11) DEFAULT 0 COMMENT '水果',
  `salad` int(11) DEFAULT 0 COMMENT '沙拉',
  `congee` int(11) DEFAULT 0 COMMENT '粥类',
  PRIMARY KEY (`id`),
  KEY `fk_order_customer` (`cid`),
  KEY `fk_order_food` (`fid`),
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`cid`) REFERENCES `customer` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_food` FOREIGN KEY (`fid`) REFERENCES `food` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- 5. 创建评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `food_id` int(11) NOT NULL COMMENT '菜品ID',
  `food_name` varchar(100) DEFAULT NULL COMMENT '菜品名称',
  `customer` varchar(50) DEFAULT NULL COMMENT '评论人',
  `cimage` varchar(200) DEFAULT NULL COMMENT '评论人头像',
  `comment` text COMMENT '评论内容',
  `city` varchar(50) DEFAULT NULL COMMENT '评论人城市',
  `comment_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `star` int(1) DEFAULT 5 COMMENT '评分(1-5星)',
  PRIMARY KEY (`id`),
  KEY `fk_comment_food` (`food_id`),
  CONSTRAINT `fk_comment_food` FOREIGN KEY (`food_id`) REFERENCES `food` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评论表';

-- 6. 创建商品表(如果需要)
CREATE TABLE IF NOT EXISTS `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int(11) DEFAULT 0 COMMENT '库存',
  `descr` text COMMENT '商品描述',
  `status` int(1) DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';

-- 插入示例数据

-- 插入商家数据
INSERT INTO `store` (`store_name`, `descr`) VALUES
('川香小厨', '正宗川菜，麻辣鲜香'),
('粤式茶餐厅', '港式茶餐厅，精致粤菜'),
('北京烤鸭店', '传统北京烤鸭，皮脆肉嫩'),
('江南小笼', '上海小笼包，汤汁丰富'),
('西北面馆', '手工拉面，汤浓面劲');

-- 插入菜品数据
INSERT INTO `food` (`food_name`, `store`, `price`, `stock`, `descr`, `fimage`, `status`, `star`) VALUES
('麻婆豆腐', '川香小厨', 28.00, 50, '经典川菜，麻辣鲜香，嫩滑豆腐配肉末', 'traditional-2.png', 1, 4.5),
('宫保鸡丁', '川香小厨', 32.00, 30, '传统川菜，鸡肉鲜嫩，花生脆香', 'traditional-3.png', 1, 4.3),
('白切鸡', '粤式茶餐厅', 45.00, 20, '粤式经典，鸡肉鲜嫩，蘸料丰富', 'traditional-4.png', 1, 4.6),
('蒸蛋羹', '粤式茶餐厅', 18.00, 40, '嫩滑蒸蛋，营养丰富', 'traditional-5.png', 1, 4.2),
('北京烤鸭', '北京烤鸭店', 88.00, 15, '传统北京烤鸭，皮脆肉嫩，配荷叶饼', 'traditional-6.png', 1, 4.8),
('小笼包', '江南小笼', 25.00, 60, '上海小笼包，皮薄馅大，汤汁丰富', 'featured-1.png', 1, 4.4),
('牛肉拉面', '西北面馆', 22.00, 35, '手工拉面，牛肉鲜美，汤浓面劲', 'featured-2.png', 1, 4.1),
('酸辣土豆丝', '川香小厨', 16.00, 80, '爽脆土豆丝，酸辣开胃', 'featured-3.png', 1, 4.0);

-- 插入管理员客户数据
INSERT INTO `customer` (`customer_name`, `email`, `phone`, `address`, `password`, `city`, `sex`, `age`) VALUES
('admin', 'admin@orderfood.com', '13800138000', '系统管理员', '123456', '北京', '男', 30),
('张三', 'zhangsan@example.com', '13800138001', '北京市朝阳区xxx街道', '123456', '北京', '男', 25),
('李四', 'lisi@example.com', '13800138002', '上海市浦东新区xxx路', '123456', '上海', '女', 28),
('王五', 'wangwu@example.com', '13800138003', '广州市天河区xxx大道', '123456', '广州', '男', 32);

-- 插入示例评论数据
INSERT INTO `comment` (`food_id`, `food_name`, `customer`, `comment`, `city`, `star`) VALUES
(1, '麻婆豆腐', '张三', '味道很正宗，麻辣适中，豆腐很嫩！', '北京', 5),
(2, '宫保鸡丁', '李四', '鸡肉很嫩，花生很香脆，不错的川菜！', '上海', 4),
(3, '白切鸡', '王五', '鸡肉很新鲜，蘸料调得很好，推荐！', '广州', 5),
(5, '北京烤鸭', '张三', '正宗的北京烤鸭，皮脆肉嫩，值得推荐！', '北京', 5),
(6, '小笼包', '李四', '汤汁很丰富，皮薄馅大，很好吃！', '上海', 4);

COMMIT;

-- 查询验证数据
SELECT '=== 数据库初始化完成 ===' as message;
SELECT COUNT(*) as customer_count FROM customer;
SELECT COUNT(*) as store_count FROM store;
SELECT COUNT(*) as food_count FROM food;
SELECT COUNT(*) as comment_count FROM comment;