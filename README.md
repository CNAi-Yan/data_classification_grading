# 敏感数据识别系统

一个基于Java Spring Boot和Vue的敏感数据识别应用，能够快速识别文本中的敏感信息并提供处理建议。

## 功能特点

- **多类型敏感数据识别**：支持识别身份证号、手机号、邮箱、银行卡号等多种敏感数据类型
- **实时检测**：通过WebSocket实现输入过程中的实时敏感数据检测
- **分类展示**：将识别出的敏感数据按类型分类并高亮显示
- **风险等级评估**：根据敏感程度对数据进行风险等级划分（高、中、低）
- **处理建议**：针对不同类型的敏感数据提供脱敏、加密等处理建议
- **一键复制**：支持一键复制检测结果

## 技术栈

### 后端
- Java 1.8
- Spring Boot 2.7.x
- WebSocket（实时检测）
- 正则表达式（敏感数据识别）

### 前端
- Vue 3
- Element Plus（UI组件库）
- Axios（HTTP请求）
- StompJS + SockJS（WebSocket客户端）

## 项目结构

```
sensitive-data-detector/
├── backend/                 # 后端代码
│   ├── src/main/java/com/sensitive/data/
│   │   ├── controller/      # REST API控制器
│   │   ├── service/         # 业务逻辑层
│   │   │   ├── detector/    # 敏感数据识别服务
│   │   │   └── processor/   # 数据处理服务
│   │   ├── model/           # 数据模型
│   │   ├── util/            # 工具类
│   │   │   ├── regex/       # 正则表达式工具
│   │   │   └── validator/   # 数据验证工具
│   │   └── config/          # 配置类
│   └── pom.xml              # Maven配置文件
├── frontend/                # 前端代码
│   ├── src/
│   │   ├── components/      # Vue组件
│   │   ├── views/           # 页面视图
│   │   ├── utils/           # 工具函数
│   │   ├── assets/          # 静态资源
│   │   ├── router/          # 路由配置
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   ├── package.json         # NPM配置文件
│   ├── index.html           # HTML模板
│   └── vite.config.js       # Vite配置文件
└── README.md                # 项目说明文档
```

## 快速开始

### 后端启动

1. 确保已安装JDK 1.8和Maven
2. 进入backend目录
3. 执行以下命令启动服务：

```bash
mvn spring-boot:run
```

服务将在 http://localhost:8080 上运行

### 前端启动

1. 确保已安装Node.js和npm
2. 进入frontend目录
3. 安装依赖：

```bash
npm install
```

4. 启动开发服务器：

```bash
npm run dev
```

前端应用将在 http://localhost:3000 上运行

## API接口

### 1. 检测文本中的敏感数据

```
POST /api/detect/text
```

请求体：文本内容（纯文本）

响应：
```json
{
  "originalText": "检测的原始文本",
  "detectedItems": [
    {
      "content": "敏感数据内容",
      "type": {
        "name": "敏感数据类型名称",
        "code": "类型代码",
        "riskLevel": {
          "name": "风险等级名称",
          "color": "风险等级颜色"
        }
      },
      "startPosition": 0,
      "endPosition": 10,
      "suggestion": "处理建议"
    }
  ],
  "totalDetected": 1,
  "processingTimeMs": 100
}
```

### 2. 获取支持的敏感数据类型

```
GET /api/detect/types
```

响应：
```json
[
  {
    "code": "idCard",
    "name": "身份证号",
    "riskLevel": "高风险",
    "riskColor": "#ff4d4f"
  },
  // 其他类型...
]
```

### 3. 获取特定类型数据的处理建议

```
GET /api/detect/suggestions/{type}
```

响应：
```
"建议使用掩码处理，保留前6位和后4位，中间用*代替"
```

### 4. WebSocket实时检测

连接到`/ws`端点，订阅`/topic/detectionResults`主题，并向`/app/detect/realtime`发送消息。

## 支持的敏感数据类型

- 个人身份信息（身份证号、护照号、驾照号）
- 联系方式（手机号、邮箱、地址）
- 金融信息（银行卡号、信用卡号、银行账号）
- 账号密码（用户名、密码）
- 企业敏感信息（商业机密、内部代码）
- 其他敏感信息（政治敏感、色情内容、暴力内容、版权内容、健康记录、宗教信仰）

## 安全考虑

- 传输加密：使用HTTPS和WebSocket SSL
- 存储安全：不持久化用户输入的敏感数据
- 处理安全：识别后立即清除内存中的原始数据

## 许可证

MIT