# 开发规范手册

## 1. 文档规范

### 1.1 语言要求
- 所有Markdown文档必须使用中文编写
- 专业术语可使用英文，但需在首次出现时提供中文解释

### 1.2 格式规范
- 使用标准Markdown语法
- 标题层级清晰，使用`#`、`##`、`###`等表示
- 代码块使用三个反引号包裹，并指定语言类型
- 表格使用标准Markdown表格语法
- 列表使用`-`或`1.`表示，保持缩进一致

### 1.3 文档结构
- 每个文档应有明确的标题和目录
- 包含必要的章节：概述、使用说明、配置方法、注意事项等
- 重要信息使用**加粗**或> 引用强调

## 2. 后端开发规范（Java + Spring Boot + Spring Cloud）

### 2.1 代码命名规范
- **类名**：使用大驼峰命名法，如`UserService`
- **方法名**：使用小驼峰命名法，如`getUserById`
- **变量名**：使用小驼峰命名法，如`userName`
- **常量名**：使用全大写，下划线分隔，如`MAX_PAGE_SIZE`
- **包名**：使用小写字母，如`com.example.demo`
- **接口名**：使用大驼峰命名法，以`I`开头或使用业务名称，如`IUserService`或`UserRepository`

### 2.2 目录结构规范
```
src/main/java
└── com.example.demo
    ├── DemoApplication.java       # 启动类
    ├── config/                     # 配置类
    ├── controller/                 # 控制器
    ├── service/                    # 业务逻辑
    │   └── impl/                   # 业务实现
    ├── mapper/                     # 数据访问
    ├── entity/                     # 实体类
    ├── dto/                        # 数据传输对象
    ├── vo/                         # 视图对象
    ├── exception/                  # 异常处理
    └── utils/                      # 工具类
```

### 2.3 代码风格规范
- 缩进：使用4个空格
- 换行：每行不超过120个字符
- 注释：
  - 类注释：使用Javadoc，包含类的功能描述
  - 方法注释：使用Javadoc，包含参数、返回值和异常说明
  - 复杂逻辑添加单行注释
- 空行：在方法之间、代码块之间添加空行，提高可读性

### 2.4 Spring Boot配置规范
- 使用`application.yml`或`application.properties`，优先使用yml
- 配置项按功能分组
- 敏感信息使用环境变量或配置中心管理
- 不同环境使用不同配置文件，如`application-dev.yml`、`application-prod.yml`

### 2.5 Spring Cloud微服务架构规范
- 服务命名：使用小写字母，横线分隔，如`user-service`
- 注册中心：使用Nacos或Eureka
- 配置中心：使用Nacos Config或Spring Cloud Config
- 网关：使用Spring Cloud Gateway
- 负载均衡：使用Ribbon或Spring Cloud LoadBalancer
- 服务调用：使用OpenFeign
- 熔断降级：使用Sentinel或Hystrix
- 链路追踪：使用SkyWalking或Zipkin

### 2.6 数据库操作规范
- 实体类使用`@Entity`或`@Table`注解
- 主键使用自增或UUID，优先使用自增
- 字段命名：使用下划线命名法，如`user_name`
- 查询使用MyBatis Plus或JPA，避免手写复杂SQL
- 事务管理：使用`@Transactional`注解，明确事务边界
- 批量操作：使用批量插入、更新，避免循环操作数据库

### 2.7 API设计规范
- 遵循RESTful风格
- URL使用小写字母，横线分隔，如`/api/users/{id}`
- HTTP方法使用：
  - GET：查询资源
  - POST：创建资源
  - PUT：更新资源
  - DELETE：删除资源
- 响应格式统一：
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {}
  }
  ```
- 错误处理：使用全局异常处理器，返回统一错误格式

## 3. 前端开发规范（Vue + Element Plus）

### 3.1 项目结构规范
```
src
├── main.js                # 入口文件
├── App.vue                # 根组件
├── assets/                # 静态资源
├── components/            # 公共组件
├── views/                 # 页面组件
├── router/                # 路由配置
├── store/                 # 状态管理
├── api/                   # API请求
├── utils/                 # 工具函数
└── styles/                # 样式文件
```

### 3.2 组件命名和开发规范
- 组件名：使用大驼峰命名法，如`UserList`
- 文件名：使用短横线分隔，如`user-list.vue`
- 组件职责单一，避免过大组件
- 公共组件放在`components`目录，页面组件放在`views`目录
- 组件通信：
  - 父子组件：使用props和emit
  - 跨组件：使用Vuex或Pinia
  - 祖孙组件：使用provide/inject

### 3.3 代码风格规范
- 缩进：使用2个空格
- 换行：每行不超过120个字符
- 注释：
  - 组件注释：使用`<!-- -->`，包含组件功能描述
  - 复杂逻辑添加单行注释
- 空行：在代码块之间添加空行，提高可读性
- 使用ES6+语法

### 3.4 Element Plus组件使用规范
- 优先使用Element Plus提供的组件，避免重复造轮子
- 组件属性使用kebab-case，如`size="small"`
- 表单验证：使用Element Plus的表单验证规则
- 表格：合理使用分页、排序、筛选功能
- 弹窗：使用`v-model`控制显示/隐藏
- 按钮：根据业务场景选择合适的类型和大小

### 3.5 路由配置规范
- 路由名：使用小驼峰命名法，如`userList`
- 路径：使用小写字母，横线分隔，如`/user-list`
- 懒加载：使用`() => import()`实现路由懒加载
- 嵌套路由：合理使用嵌套路由，保持路由结构清晰
- 路由守卫：使用全局守卫、路由守卫和组件守卫

### 3.6 状态管理规范
- 使用Vuex或Pinia进行状态管理
- 状态按模块划分，如`user`、`product`
-  mutations：用于修改状态，必须是同步函数
-  actions：用于处理异步操作，可调用mutations
-  getters：用于计算派生状态

### 3.7 API调用规范
- API请求封装在`api`目录下，按模块划分
- 使用Axios进行HTTP请求
- 统一处理请求拦截器和响应拦截器
- 错误处理：统一处理网络错误和业务错误
- 接口命名：使用小驼峰命名法，如`getUserList`

## 4. 开发流程规范

### 4.1 代码提交规范
- 提交信息格式：`类型: 描述`
  - 类型：feat（新功能）、fix（修复bug）、docs（文档）、style（代码风格）、refactor（重构）、test（测试）、chore（构建/工具）
  - 描述：简洁明了，不超过50个字符
- 提交前运行lint检查
- 提交前运行单元测试

### 4.2 分支管理规范
- `main`：主分支，用于发布生产版本
- `develop`：开发分支，用于集成各功能分支
- `feature/*`：功能分支，用于开发新功能
- `bugfix/*`：bug修复分支，用于修复生产bug
- `hotfix/*`：紧急修复分支，用于修复线上紧急问题

### 4.3 开发工具配置
- IDE：推荐使用IntelliJ IDEA（后端）和VS Code（前端）
- 代码格式化：使用Prettier（前端）和IDEA自带格式化（后端）
- 代码检查：使用ESLint（前端）和Checkstyle（后端）
- 版本控制：使用Git

### 4.4 测试规范
- 单元测试：使用JUnit（后端）和Jest（前端）
- 集成测试：测试服务间调用
- 接口测试：使用Postman或Swagger
- 前端测试：使用Cypress或Playwright进行端到端测试

### 4.5 部署规范
- 后端：使用Docker容器化部署
- 前端：使用Nginx部署静态资源
- 持续集成/持续部署：使用Jenkins或GitHub Actions
- 监控：使用Prometheus + Grafana进行监控

## 5. 注意事项

- 遵循最小权限原则
- 代码复用，避免重复代码
- 性能优化，避免不必要的计算和网络请求
- 安全性考虑，防止SQL注入、XSS攻击等
- 可维护性，代码结构清晰，易于理解和修改
- 可扩展性，设计时考虑未来需求变化

## 6. 附则

- 本规范适用于所有使用Java + Spring Boot + Spring Cloud + Vue + Element Plus技术栈的项目
- 规范将根据技术发展和项目需求定期更新
- 所有开发人员必须遵守本规范
- 如有疑问或建议，可提交至技术委员会讨论
