# 网上订餐系统

## 项目简介

这是一个基于Spring Boot开发的网上订餐系统，为用户提供在线浏览菜品、下单、支付等功能，同时为管理员提供完整的后台管理功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.3
- **数据库**: MySQL 5.7+
- **ORM框架**: MyBatis Plus 3.0.5
- **模板引擎**: Thymeleaf
- **前端技术**: HTML5, CSS3, JavaScript, Bootstrap 5
- **连接池**: Druid
- **构建工具**: Maven
- **JDK版本**: Java 18

## 功能特性

### 用户端功能
- ✅ 用户注册/登录
- ✅ 菜品浏览和搜索
- ✅ 购物车管理
- ✅ 在线下单
- ✅ 订单管理
- ✅ 菜品收藏
- ✅ 个人信息管理
- ✅ 评论功能

### 管理端功能
- ✅ 管理员登录
- ✅ 用户管理
- ✅ 菜品管理
- ✅ 订单管理
- ✅ 商家管理
- ✅ 数据统计
- ✅ 系统设置

### UI特色
- ✅ 现代化响应式设计
- ✅ 专业的管理后台界面
- ✅ 优化的用户体验
- ✅ 移动设备完美适配

## 项目结构

```
orderfood-system/
├── src/
│   ├── main/
│   │   ├── java/com/zpy/
│   │   │   ├── controller/     # 控制器层
│   │   │   ├── service/        # 服务层
│   │   │   ├── mapper/         # 数据访问层
│   │   │   ├── pojo/           # 实体类
│   │   │   └── DemoApplication.java
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf模板
│   │       ├── static/         # 静态资源
│   │       └── application.yml # 配置文件
│   └── test/                   # 测试代码
├── database_init.sql           # 数据库初始化脚本
├── 修复admin登录.sql           # 管理员登录修复脚本
├── 导入数据库.bat             # 数据库导入工具
├── 同步图片文件.bat           # 图片同步工具
└── README.md
```

## 快速开始

### 环境要求
- JDK 18+
- MySQL 5.7+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/3072638260/orderfood-system.git
   cd orderfood-system
   ```

2. **数据库配置**
   - 创建数据库 `system-orderfood-op`
   - 执行 `database_init.sql` 初始化数据
   - 执行 `修复admin登录.sql` 创建管理员账号

3. **配置文件**
   修改 `src/main/resources/application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/system-orderfood-op
       username: root
       password: 123456
   ```

4. **运行项目**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **访问系统**
   - 管理后台: http://localhost:8088
   - 用户前台: http://localhost:8088/toUserLogin

### 默认账号

**管理员账号**:
- 用户名: admin
- 密码: 123456

**测试用户账号**:
- 用户名: 张三 / 李四 / 王五
- 密码: 123456

## 数据库设计

系统包含以下主要数据表：
- `user` - 管理员用户表
- `customer` - 普通用户表
- `store` - 商家信息表
- `food` - 菜品信息表
- `order` - 订单表
- `comment` - 评论表

## 开发说明

### 安全特性
- 密码MD5加密存储
- POST请求防止密码URL泄露
- 会话管理和权限控制
- SQL注入防护

### 性能优化
- Druid连接池优化数据库连接
- 静态资源CDN加速
- 图片懒加载
- 响应式设计减少移动端流量

## 部署说明

### 生产环境部署
1. 修改 `application.yml` 为生产环境配置
2. 使用 `./mvnw clean package` 打包
3. 部署 `target/demo-0.0.1-SNAPSHOT.jar`
4. 配置反向代理和HTTPS

### Docker部署
```dockerfile
FROM openjdk:18-jre-slim
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 作者: 周浩辉
- 邮箱: zhouhaohui@example.com
- 项目链接: https://github.com/3072638260/orderfood-system

## 致谢

感谢所有为这个项目做出贡献的开发者和测试人员。

---

**注意**: 这是一个毕业设计项目，仅供学习和参考使用。