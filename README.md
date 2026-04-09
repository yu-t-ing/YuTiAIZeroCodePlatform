# 姜瓷AI零代码生成平台

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green)
![Vue](https://img.shields.io/badge/Vue-3.5.13-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

基于AI的零代码应用生成平台，让每个人都能快速创建Web应用

[在线演示](#) • [快速开始](#快速开始) • [功能特性](#功能特性) • [技术文档](#技术文档)

</div>

## 📖 项目简介

**姜瓷AI零代码生成平台**是一个创新的AI驱动开发平台，用户只需通过自然语言描述需求，AI就能自动生成完整的Vue前端应用。无需编程基础，人人都是开发者！

### 核心亮点

- 🤖 **AI智能生成**：基于LangGraph4j和Spring AI，支持复杂的多文件代码生成
- 🎨 **精美UI设计**：现代化的玻璃拟态设计风格，流畅的动画效果
- 👤 **用户中心**：支持用户注册、登录、头像上传、个人信息管理
- 📊 **管理后台**：完整的应用管理、用户管理、对话管理功能
- ⚡ **实时交互**：基于SSE的实时流式输出，即时看到AI生成过程
- 🚀 **性能优化**：智能节流机制，确保页面流畅不卡顿

## ✨ 功能特性

### 用户功能
- 🔐 **用户认证**：注册、登录、权限管理
- 👤 **个人中心**：头像上传（支持base64）、昵称编辑、个人简介
- 💬 **AI对话**：与应用进行实时对话，生成代码
- 📦 **应用管理**：创建、编辑、删除自己的应用
- 🎯 **多种代码类型**：支持单页HTML、多文件Vue项目生成

### 管理功能
- 👥 **用户管理**：查看、编辑、删除用户信息
- 📱 **应用管理**：管理所有用户创建的应用
- 💭 **对话管理**：查看和管理用户的AI对话历史
- 📈 **数据统计**：用户数量、应用数量、对话统计

### 技术特性
- 🔄 **SSE流式输出**：实时展示AI生成过程
- ⚙️ **代码生成工作流**：支持复杂的多步骤代码生成
- 🎨 **自定义主题**：玻璃拟态设计，支持深色模式
- 📱 **响应式设计**：完美适配桌面和移动设备
- 🔒 **权限控制**：基于角色的访问控制（RBAC）

## 🛠 技术栈

### 后端技术
- **核心框架**：Spring Boot 3.5.4
- **AI框架**：LangGraph4j、Spring AI
- **数据库**：MySQL 8.0 + MyBatis-Flex
- **缓存**：Redis（可选）
- **对象存储**：腾讯云COS（可选）
- **监控**：Prometheus + Actuator
- **API文档**：Swagger/Knife4j

### 前端技术
- **框架**：Vue 3.5.13 + TypeScript
- **UI组件**：Ant Design Vue 4.x
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **HTTP客户端**：Axios
- **样式方案**：CSS3 + CSS Variables
- **构建工具**：Vite

## 🚀 快速开始

### 环境要求

- **JDK**: 21+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Maven**: 3.6+

### 后端启动

1. **克隆项目**
```bash
git clone https://github.com/yu-t-ing/YuTiAIZeroCodePlatform.git
cd YuTiAIZeroCodePlatform
```

2. **配置数据库**
```sql
CREATE DATABASE IF NOT EXISTS yu_ti_code_mother DEFAULT CHARSET utf8mb4;
```

3. **修改配置**
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yu_ti_code_mother
    username: your_username
    password: your_password
```

4. **启动项目**
```bash
mvn clean install
cd target
java -jar yuti-ai-code-mother-0.0.1-SNAPSHOT.jar
```

后端服务将在 http://localhost:8101 启动

### 前端启动

1. **进入前端目录**
```bash
cd yu-ai-code-mother-frontend
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

前端应用将在 http://localhost:5173 启动

## 📁 项目结构

```
YuTiAIZeroCodePlatform/
├── src/main/java/com/yuti/yutiaicodemother/
│   ├── ai/                      # AI相关服务
│   │   ├── AiCodeGeneratorService.java
│   │   └── tools/               # AI工具类
│   ├── controller/              # 控制器层
│   │   ├── AppController.java
│   │   ├── UserController.java
│   │   └── ChatHistoryController.java
│   ├── service/                 # 业务逻辑层
│   ├── model/                   # 数据模型
│   ├── config/                  # 配置类
│   └── langgraph4j/             # AI工作流
├── src/main/resources/
│   ├── mapper/                  # MyBatis映射文件
│   └── application.yml          # 配置文件
├── yu-ai-code-mother-frontend/  # 前端项目
│   ├── src/
│   │   ├── pages/              # 页面组件
│   │   │   ├── user/          # 用户相关页面
│   │   │   ├── admin/         # 管理员页面
│   │   │   └── app/           # 应用页面
│   │   ├── components/        # 公共组件
│   │   ├── api/               # API接口
│   │   └── stores/            # 状态管理
│   └── package.json
└── sql/                        # 数据库脚本
```

## 🎯 主要功能演示

### 1. 用户注册登录
- 支持账号密码注册
- 实时表单验证
- 安全的密码加密存储

### 2. 个人中心
![用户中心](docs/images/user-profile.png)
- 头像上传（base64格式）
- 昵称和简介编辑
- 用户统计信息展示

### 3. AI应用生成
![AI生成](docs/images/ai-generation.gif)
- 自然语言描述需求
- 实时代码生成展示
- 支持多文件项目生成

### 4. 管理后台
- 用户管理：查看、编辑、删除用户
- 应用管理：管理所有应用
- 对话管理：查看AI对话历史

## 🔧 配置说明

### 必需配置

#### 1. AI模型配置
```yaml
spring:
  ai:
    openai:
      api-key: your-api-key
      base-url: https://api.openai.com
```

#### 2. 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yu_ti_code_mother
    username: root
    password: your-password
```

### 可选配置

#### 1. Redis配置（用于缓存和限流）
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

#### 2. 对象存储配置（用于文件上传）
```yaml
cos:
  secret-id: your-secret-id
  secret-key: your-secret-key
  region: ap-guangzhou
  bucket: your-bucket
```

## 📦 部署指南

### Docker部署（推荐）

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d
```

### 传统部署

1. **打包后端**
```bash
mvn clean package
```

2. **打包前端**
```bash
cd yu-ai-code-mother-frontend
npm run build
```

3. **部署到服务器**
- 将target目录下的jar包上传到服务器
- 将前端dist目录部署到Nginx
- 配置Nginx反向代理

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交Pull Request

## 📝 更新日志

### v1.1.0 (2026-04-06)
- ✨ 新增用户个人中心功能
- ✨ 支持头像上传（base64格式）
- 🐛 修复AI生成时页面卡顿问题
- 🎨 优化前端UI，提升用户体验
- 🐛 修复用户头像字段长度限制问题
- ⚡ 优化SSE流式输出性能

### v1.0.0 (初始版本)
- 🎉 基础AI代码生成功能
- 👤 用户注册登录
- 📱 应用管理和对话管理
- 🎨 现代化UI设计

## ❓ 常见问题

### 1. 数据库连接失败？
检查MySQL服务是否启动，用户名密码是否正确。

### 2. AI生成失败？
确认已配置有效的API密钥，并检查网络连接。

### 3. 前端无法访问后端？
检查后端服务是否在8101端口启动，前端API配置是否正确。

### 4. 头像上传失败？
确保数据库中`userAvatar`字段为TEXT类型，执行SQL修改：
```sql
ALTER TABLE user MODIFY COLUMN userAvatar TEXT COMMENT '用户头像';
```

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Ant Design Vue](https://antdv.com/)
- [LangGraph4j](https://github.com/langchain4j/langchain4j)
- [MyBatis-Flex](https://mybatis-flex.com/)

## 📮 联系方式

- 作者：姜瓷
- GitHub：[@yu-t-ing](https://github.com/yu-t-ing)
- 邮箱：your-email@example.com

---

<div align="center">
  <p>如果这个项目对你有帮助，请给一个 ⭐️ 支持一下！</p>
  <p>Made with ❤️ by 姜瓷</p>
</div>
