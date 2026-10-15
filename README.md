# 高校助学金评定与管理系统

面向高校学生资助业务场景的前后端分离系统，围绕助学金「申请 → 审核 → 综合评定 → 结果公示」全流程，提供管理员、辅导员（教师）、学生三类角色的独立工作台。项目为个人独立开发，包含 **35+ 个后端接口、29 个前端页面**。

- **后端仓库（本仓库）**：Spring Boot + MyBatis-Plus
- **前端仓库**：<https://gitee.com/timeguoke/college_grant_frontend>

---

## 一、项目背景

传统助学金评定依赖纸质表格与人工汇总，存在材料收集慢、评分口径不统一、辅导员跨班数据越权查看、结果统计耗时等问题。本系统将评定流程线上化，并把评定标准抽象为**可配置的多维加权评分模型**，由系统自动算分排名、匹配助学金等级，同时通过角色权限与数据隔离保证评定过程规范、可追溯。

## 二、技术栈

| 分层 | 技术选型 |
| --- | --- |
| 后端 | Java 8、Spring Boot 2.7.18、Spring MVC、MyBatis-Plus 3.5.2 |
| 安全 | JWT（jjwt 0.9.1）+ 拦截器，RBAC 三角色鉴权 |
| 数据库 | MySQL 8、HikariCP 连接池、MySQL 驱动 8.0.33 |
| 工具组件 | EasyExcel 3.3.2（导入导出）、Knife4j 3.0.2（接口文档）、阿里云 OSS（材料附件） |
| 前端 | Vue 3.4、Vue Router 4、Pinia 2、Axios、Element Plus 2.5、Vite 5 |

## 三、系统功能

### 管理员端
- 学生 / 教师信息管理，支持 Excel 批量导入、导出与导入结果回显
- 助学金类型与等级管理（名额、金额、申请条件、评分维度权重配置）
- 全部申请审核、成绩/评分记录管理、评定结果查看与导出
- 公告通知发布、数据看板（申请量、通过率、各状态漏斗统计）

### 辅导员（教师）端
- 查看本辅导员名下学生（按 `counselor_id` 数据隔离）
- 助学金申请材料审核、按四维模型录入/核算评分
- 提交综合评定结果、查看本辅导员范围的评定排名
- 首页数据看板、通知公告、个人信息维护

### 学生端
- 在线填写助学金申请、上传证明材料（OSS）
- 查看申请审核进度与评定结果公示
- 资助政策与常见问题浏览、修改个人信息与密码

## 四、技术亮点

1. **可配置的四维加权评定模型**：将评分抽象为经济情况、家庭负担、特殊困难、突发困难四个维度，权重由管理员动态配置并校验权重之和（=100%），评定时按加权总分自动排名并匹配助学金等级，避免人工口径不一致。
2. **RBAC + 行级数据隔离防越权**：JWT 解析后通过拦截器统一鉴权，按 `/api/admin`、`/api/teacher`、`/api/student` 路径前缀区分角色；教师端业务层统一追加 `counselor_id` 过滤条件，详情类接口再做归属校验，防止水平越权查看其他辅导员的数据。
3. **ThreadLocal 透传登录上下文**：拦截器解析 Token 后将当前用户存入 `UserContext`，业务层直接取用，请求结束时统一清理，避免 Tomcat 线程池复用导致的用户串号。
4. **EasyExcel 批量导入导出**：学生、教师、评定结果等数据支持模板批量导入，逐行监听解析并返回成功/失败明细，大文件下内存占用低、错误数据可定位到具体行。
5. **统一返回结构与全局异常处理**：`Result` / `PageResult` 统一响应体，`GlobalExceptionHandler` 兜底业务异常与系统异常，前端无需处理分散的错误格式。
6. **前后端分离工程化**：前端 Axios 统一封装请求拦截器（自动携带 Token、异常提示），Vite 配置 `/api` 代理解决开发环境跨域，后端 CORS 配置放行生产环境跨域。

## 五、项目结构

```
college_grant_system
├── college_grant.sql                 # 数据库脚本（含建库语句，9 张表）
├── 接口文档新方案2.json               # Apifox 可导入的接口文档
└── src/main
    ├── java/com/collegegrant
    │   ├── common
    │   │   ├── context/UserContext.java     # ThreadLocal 登录上下文
    │   │   ├── result/                       # Result、PageResult 统一响应体
    │   │   └── util/                         # JwtUtil、OssUtil
    │   ├── config/                           # 拦截器、CORS、MP、全局异常等配置
    │   ├── interceptor/JwtInterceptor.java   # JWT 鉴权拦截器
    │   ├── controller
    │   │   ├── admin/                        # 管理员端接口
    │   │   ├── teacher/                      # 辅导员端接口
    │   │   ├── student/                      # 学生端接口
    │   │   └── login/                        # 登录、改密等公共接口
    │   ├── service/impl/                     # 业务逻辑（评定、申请、导入等）
    │   ├── mapper/                           # MyBatis-Plus Mapper
    │   └── pojo
    │       ├── entity/                       # 数据库实体
    │       ├── dto/                          # 请求参数对象
    │       └── vo/                           # 返回视图对象
    └── resources
        ├── application.yml                   # 公共配置（端口 8080）
        ├── application-dev.yml               # 本地私密配置（不入库，需自建）
        └── mapper/*.xml                      # 关联查询 SQL
```

## 六、快速开始

### 环境要求

- JDK 1.8+、Maven 3.6+
- MySQL 8.0（5.7 亦可，需调整驱动参数）
- Node.js 16+（前端）

### 1. 初始化数据库

```sql
-- 直接执行根目录脚本，内含建库语句与初始数据
source college_grant.sql;
```

脚本会创建 `college_grant` 数据库（utf8mb4）、9 张业务表及测试账号数据。

### 2. 启动后端

在 `src/main/resources/` 下新建 **`application-dev.yml`**（该文件含数据库密码等敏感信息，已被 .gitignore 忽略，不入库）：

```yaml
grant:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: college_grant
    username: root
    password: 你的MySQL密码

# 阿里云 OSS（可选：仅证明材料上传需要，不配置不影响系统启动与核心流程）
aliyun:
  oss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: 你的AccessKeyId
    access-key-secret: 你的AccessKeySecret
    bucket-name: 你的Bucket
    url-prefix: https://你的Bucket.endpoint/
```

启动：

```bash
mvn spring-boot:run
# 或在 IDE 中运行 CollegeGrantSystemApplication
```

后端服务运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
# 克隆前端仓库
git clone https://gitee.com/timeguoke/college_grant_frontend.git
cd college_grant_frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，开发环境下 `/api` 请求由 Vite 代理转发至 `http://localhost:8080`。

## 七、接口文档与测试账号

服务启动后访问 Knife4j 文档：<http://localhost:8080/doc.html>
（也可将根目录 `接口文档新方案2.json` 导入 Apifox 查看）

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `123456` |
| 辅导员 | `12023066` | `123456` |
| 学生 | `23012066` | `123456` |
