# 移动端智能外卖点餐系统

实训大作业项目。面向手机端的智能外卖点餐 H5 应用，页面按手机宽度设计，包含商家与商品浏览、购物车、下单支付、订单管理、评论与收藏、收货地址，并接入大模型实现 AI 客服问答。

## 技术栈

**前端（移动端 H5）**

- Vue 3 + Vue Router 4
- Element Plus
- Axios + qs
- Vue CLI 5 构建

**后端（两个独立服务，需要同时启动）**

- `go2-serve`：Spring Boot 2.3.7 + MyBatis-Plus，端口 10001，提供全部业务接口
- `go-chat-ai`：Spring Boot 3.4.3 + Spring AI，端口 10002，提供 AI 问答（兼容 OpenAI 协议，示例使用阿里云百炼 qwen-max）

**数据库**

- MySQL 8，库名 `system`

## 项目结构

```
├── 前端项目/                 移动端 H5 页面
├── 后端项目/
│   ├── go2-serve/            业务后端（端口 10001）
│   └── go-chat-ai/           AI 问答后端（端口 10002）
└── system.sql                数据库结构与数据
```

## 本地运行

### 1. 导入数据库

```bash
mysql -u root -p -e "CREATE DATABASE system DEFAULT CHARSET utf8mb4"
mysql -u root -p system < system.sql
```

### 2. 配置后端

两个后端的 `application.yaml` 含数据库密码与 API Key，未纳入版本管理。请复制同目录下的示例文件后填入自己的信息：

```bash
cp 后端项目/go2-serve/src/main/resources/application.example.yaml 后端项目/go2-serve/src/main/resources/application.yaml
cp 后端项目/go-chat-ai/src/main/resources/application.example.yaml 后端项目/go-chat-ai/src/main/resources/application.yaml
```

### 3. 启动两个后端

```bash
cd 后端项目/go2-serve && ./mvnw spring-boot:run    # 业务接口，端口 10001
cd 后端项目/go-chat-ai && ./mvnw spring-boot:run   # AI 问答，端口 10002
```

### 4. 启动前端

```bash
cd 前端项目
npm install
npm run serve
```

页面为移动端布局，建议用手机浏览器访问；在桌面浏览器中请按 `F12` 打开开发者工具，切换到移动设备视图（如 iPhone）查看，否则布局会被拉伸。

前端默认调用 `http://localhost:10001`（业务接口）与 `http://localhost:10002/ai/chat`（AI 问答），如需改端口请同步修改 `前端项目/src/api/index.js` 与 `前端项目/src/views/Chat.vue`。

## 说明

`application.yaml` 因包含本机数据库密码与大模型 API Key，已加入 `.gitignore`，请按示例文件自行创建。
