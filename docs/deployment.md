# 启动和配置参数说明

## 1. 项目启动指南

### 1.1 后端启动

#### 1.1.1 开发环境启动

**方式一：使用Maven直接启动**

```bash
# 进入backend目录
cd backend

# 启动开发环境
mvn spring-boot:run -Dspring.profiles.active=dev
```

**方式二：编译后启动**

```bash
# 进入backend目录
cd backend

# 编译打包
mvn clean package -DskipTests

# 启动开发环境
java -jar -Dspring.profiles.active=dev target/sensitive-data-detector-1.0.0.jar
```

#### 1.1.2 生产环境启动

```bash
# 进入backend目录
cd backend

# 编译打包
mvn clean package -DskipTests

# 启动生产环境
java -jar -Dspring.profiles.active=prod target/sensitive-data-detector-1.0.0.jar
```

### 1.2 前端启动

#### 1.2.1 开发环境启动

```bash
# 进入frontend目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

#### 1.2.2 生产环境构建

```bash
# 进入frontend目录
cd frontend

# 安装依赖
npm install

# 构建生产版本
npm run build

# 构建产物将生成在dist目录下
```

## 2. 配置参数说明

### 2.1 配置文件结构

项目使用Spring Boot的多环境配置，主要配置文件包括：

- `application.yml`：基础配置文件
- `application-dev.yml`：开发环境配置
- `application-prod.yml`：生产环境配置

### 2.2 核心配置项说明

#### 2.2.1 应用基本配置

```yaml
spring:
  application:
    name: sensitive-data-detector  # 应用名称
```

#### 2.2.2 服务器配置

```yaml
server:
  port: 8080  # 服务端口
  error:
    include-stacktrace: always  # 是否在错误响应中包含堆栈跟踪
  tomcat:
    max-threads: 200  # Tomcat最大线程数
    min-spare-threads: 20  # Tomcat最小空闲线程数
    connection-timeout: 30000  # 连接超时时间（毫秒）
```

#### 2.2.3 数据库配置

**开发环境（MySQL数据库）**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sensitive_data_dev?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai  # MySQL数据库URL
    driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL驱动类
    username: root  # 用户名
    password: password  # 密码
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect  # MySQL方言
    hibernate:
      ddl-auto: update  # 自动更新表结构
    show-sql: true  # 显示SQL语句
    properties:
      hibernate.format_sql: true  # 格式化SQL语句
```

**生产环境（MySQL）**

```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/sensitive_data?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai  # MySQL数据库URL
    driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL驱动类
    username: ${DB_USERNAME:root}  # 用户名（支持环境变量）
    password: ${DB_PASSWORD:password}  # 密码（支持环境变量）
    hikari:
      minimum-idle: 10  # 最小空闲连接数
      maximum-pool-size: 100  # 最大连接池大小
      connection-timeout: 30000  # 连接超时时间
      idle-timeout: 600000  # 空闲连接超时时间
      max-lifetime: 1800000  # 连接最大生命周期
      connection-test-query: SELECT 1  # 连接测试SQL
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect  # MySQL方言
    hibernate:
      ddl-auto: validate  # 验证表结构
    show-sql: false  # 不显示SQL语句
```

#### 2.2.4 Redis配置

**开发环境（单节点Redis）**

```yaml
spring:
  redis:
    host: localhost  # Redis主机
    port: 6379  # Redis端口
    database: 1  # 数据库索引
    timeout: 5000ms  # 连接超时时间
    lettuce:
      pool:
        max-active: 50  # 最大活跃连接数
        max-idle: 10  # 最大空闲连接数
        min-idle: 5  # 最小空闲连接数
        max-wait: -1ms  # 最大等待时间
```

**生产环境（Redis集群）**

```yaml
spring:
  redis:
    cluster:
      nodes: redis1:6379,redis2:6379,redis3:6379,redis4:6379,redis5:6379,redis6:6379  # Redis集群节点
      max-redirects: 3  # 最大重定向次数
    database: 0  # 数据库索引
    timeout: 3000ms  # 连接超时时间
    lettuce:
      pool:
        max-active: 200  # 最大活跃连接数
        max-idle: 50  # 最大空闲连接数
        min-idle: 20  # 最小空闲连接数
        max-wait: 1000ms  # 最大等待时间
```

#### 2.2.5 Kafka配置

**开发环境**

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092  # Kafka服务器地址
    consumer:
      group-id: sensitive-data-detector-group-dev  # 消费者组ID
      auto-offset-reset: earliest  # 偏移量重置策略
    producer:
      acks: 1  # 确认级别
```

**生产环境**

```yaml
spring:
  kafka:
    bootstrap-servers: kafka1:9092,kafka2:9092,kafka3:9092  # Kafka集群地址
    consumer:
      group-id: sensitive-data-detector-group-prod  # 消费者组ID
      auto-offset-reset: latest  # 偏移量重置策略
      enable-auto-commit: false  # 禁用自动提交
      max-poll-records: 500  # 每次拉取最大记录数
    producer:
      acks: all  # 确认级别
      retries: 3  # 重试次数
      batch-size: 16384  # 批次大小
      linger-ms: 1  #  linger时间
      buffer-memory: 33554432  # 缓冲区大小
```

#### 2.2.6 缓存配置

```yaml
spring:
  cache:
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=5m  # 开发环境较短的过期时间
```

#### 2.2.7 限流配置

```yaml
resilience4j:
  ratelimiter:
    instances:
      sensitiveDataDetector:
        limitForPeriod: 10000  # 每个周期允许的请求数（开发环境较宽松）
        limitRefreshPeriod: 1s  # 周期刷新时间
        timeoutDuration: 1000ms  # 超时时间
  
  circuitbreaker:
    instances:
      sensitiveDataDetector:
        registerHealthIndicator: true  # 注册健康指示器
        slidingWindowSize: 50  # 滑动窗口大小
        minimumNumberOfCalls: 5  # 最小调用次数
        permittedNumberOfCallsInHalfOpenState: 5  # 半开状态允许的调用次数
        automaticTransitionFromOpenToHalfOpenEnabled: true  # 自动从开状态转换为半开状态
        waitDurationInOpenState: 3s  # 开状态等待时间
        failureRateThreshold: 70  # 失败率阈值
```

#### 2.2.8 应用特有配置

```yaml
sensitive:
  data:
    detector:
      # 检测线程池配置
      thread-pool:
        core-size: 5  # 核心线程数（开发环境较低）
        max-size: 20  # 最大线程数（开发环境较低）
        queue-capacity: 500  # 队列容量（开发环境较低）
        keep-alive-time: 60  # 线程存活时间（秒）
      
      # 规则配置
      rules:
        refresh-interval: 60  # 规则刷新间隔（秒）
        max-rules: 1000  # 最大规则数
      
      # 缓存配置
      cache:
        enabled: true  # 是否启用缓存
        ttl: 300  # 缓存过期时间（秒）
        max-size: 10000  # 最大缓存大小
```

#### 2.2.9 Spring Cloud配置

**开发环境**

```yaml
spring:
  cloud:
    consul:
      enabled: false  # 禁用Consul服务注册与发现
    config:
      enabled: false  # 禁用配置中心
      import: "optional:configserver:"  # 配置中心导入地址
      import-check:
        enabled: false  # 禁用配置导入检查
    gateway:
      enabled: false  # 禁用网关
```

#### 2.2.10 性能监控配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info,env  # 暴露的端点
  metrics:
    export:
      prometheus:
        enabled: true  # 启用Prometheus指标导出
  endpoint:
    health:
      show-details: always  # 总是显示健康详情
```

## 3. 环境变量说明

### 3.1 后端环境变量

| 环境变量名 | 描述 | 默认值 | 示例值 |
| --- | --- | --- | --- |
| DB_USERNAME | 数据库用户名 | root | sensitive_data_user |
| DB_PASSWORD | 数据库密码 | password | MySecurePassword123 |
| SPRING_PROFILES_ACTIVE | 激活的Spring配置文件 | 无 | prod |
| SERVER_PORT | 服务端口 | 8080 | 8081 |
| SPRING_REDIS_HOST | Redis主机 | localhost | redis1 |
| SPRING_REDIS_PORT | Redis端口 | 6379 | 6379 |
| SPRING_KAFKA_BOOTSTRAP_SERVERS | Kafka服务器地址 | localhost:9092 | kafka1:9092,kafka2:9092 |

### 3.2 前端环境变量

| 环境变量名 | 描述 | 默认值 | 示例值 |
| --- | --- | --- | --- |
| VITE_API_BASE_URL | 后端API基础URL | http://localhost:8080 | https://api.example.com |
| VITE_WS_BASE_URL | WebSocket基础URL | ws://localhost:8080 | wss://api.example.com |

## 4. 部署建议

### 4.1 开发环境部署

- 后端：直接使用Maven启动，便于开发和调试
- 前端：使用npm run dev启动开发服务器，支持热更新
- 数据库：使用本地MySQL数据库，需要提前创建数据库
- Redis：使用本地Redis单节点
- Kafka：使用本地Kafka单节点

### 4.2 生产环境部署

#### 4.2.1 后端部署

- 使用Docker容器化部署
- 配置健康检查和自动重启
- 使用Nginx作为API网关
- 配置负载均衡，部署多个实例
- 使用监控工具（如Prometheus + Grafana）监控服务状态

#### 4.2.2 前端部署

- 构建生产版本后，使用Nginx部署静态资源
- 配置Nginx反向代理到后端API
- 启用HTTP/2和HTTPS
- 配置缓存策略，提高静态资源加载速度

#### 4.2.3 数据库部署

- 使用MySQL主从复制架构
- 配置定期备份
- 启用慢查询日志，优化查询性能
- 配置连接池，合理设置连接数

#### 4.2.4 Redis部署

- 使用Redis集群，提高可用性和性能
- 配置持久化策略（RDB + AOF）
- 监控Redis内存使用情况，设置合理的内存淘汰策略

#### 4.2.5 Kafka部署

- 使用Kafka集群，至少3个节点
- 配置适当的分区数和副本数
- 监控Kafka主题的生产和消费情况
- 配置日志清理策略，避免磁盘空间不足

## 5. 常见问题排查

### 5.1 后端启动失败

- 检查端口是否被占用：`netstat -tlnp | grep 8080`
- 检查配置文件是否正确：特别是数据库、Redis、Kafka等服务的连接信息
- 检查依赖是否缺失：使用`mvn dependency:tree`查看依赖树
- 查看日志文件：Spring Boot默认日志输出到控制台，生产环境可配置输出到文件

### 5.2 前端无法连接后端

- 检查后端服务是否正常运行：`curl http://localhost:8080/actuator/health`
- 检查CORS配置是否正确：后端是否允许前端域名访问
- 检查前端配置的API地址是否正确：查看`.env`文件中的`VITE_API_BASE_URL`配置

### 5.3 敏感数据检测不准确

- 检查正则表达式规则是否正确：查看`RegexPatterns.java`文件
- 检查规则是否需要更新：根据业务需求调整敏感数据识别规则
- 查看日志中的检测过程：开启DEBUG日志级别，查看详细的检测过程

## 6. 性能优化建议

### 6.1 后端优化

- 调整线程池大小，根据服务器CPU核数合理设置
- 优化正则表达式，避免回溯问题
- 合理设置缓存大小和过期时间，提高缓存命中率
- 使用异步处理，提高并发能力
- 优化数据库查询，添加适当的索引

### 6.2 前端优化

- 减少HTTP请求次数，合并资源文件
- 启用Gzip压缩，减少传输数据量
- 使用懒加载，优化首屏加载速度
- 合理使用WebSocket，避免频繁连接断开
- 优化前端代码，减少不必要的计算和渲染

## 7. 监控与告警

### 7.1 后端监控

- 使用Spring Boot Actuator暴露健康检查、指标等端点
- 集成Prometheus和Grafana，监控服务指标
- 配置告警规则，当服务出现异常时及时通知

### 7.2 前端监控

- 使用前端监控工具（如Sentry）监控前端错误
- 监控页面加载性能和用户行为
- 配置告警规则，当页面出现严重错误时及时通知

## 8. 版本更新与回滚

### 8.1 版本更新

- 制定详细的更新计划，包括更新内容、时间、影响范围等
- 先在测试环境进行更新测试，确保更新不会引入新问题
- 生产环境更新时，采用灰度发布或蓝绿部署，降低风险
- 更新完成后，进行功能验证和性能测试

### 8.2 版本回滚

- 制定回滚计划，包括回滚步骤、时间、影响范围等
- 当更新出现严重问题时，及时执行回滚操作
- 回滚完成后，进行功能验证和性能测试
- 分析回滚原因，避免类似问题再次发生

# 总结

本文档详细说明了敏感数据识别系统的启动方法和配置参数，包括开发环境和生产环境的部署指南。在实际部署过程中，应根据具体的硬件资源和业务需求，合理调整配置参数，以达到最佳的性能和可靠性。

同时，建议定期监控系统运行状态，及时发现和解决问题，确保系统的稳定运行。