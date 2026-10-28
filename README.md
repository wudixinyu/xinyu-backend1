<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">YAOGO 管理系统（后台）</h1>
<h4 align="center">基于 RuoYi v3.9.2（Spring Boot 4.x + Vue 前后端分离）定制开发的业务管理后台</h4>
<p align="center">
	<a href="https://gitee.com/y_project/RuoYi-Vue"><img src="https://img.shields.io/badge/RuoYi-v3.9.2-brightgreen.svg"></a>
	<a href="https://www.oracle.com/java/technologies/downloads/"><img src="https://img.shields.io/badge/JDK-17%2B-orange.svg"></a>
	<a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-green.svg"></a>
</p>

## 平台简介

本项目是在若依（RuoYi）快速开发平台之上定制开发的业务管理后台，除保留若依的系统管理、权限控制、代码生成等基础能力外，围绕 **游戏王卡片查询、AI 创作、知识库问答、亚马逊广告数据分析** 等业务场景扩展了多个功能模块，并向移动端开放数据同步接口。

* 前端采用 Vue、Element（提供 Vue2/Vue3/TS 多版本，可混用搭配）。
* 后端采用 Spring Boot、Spring Security、Redis & Jwt。
* 基于 Jwt 的 Token 认证，支持多终端（Web、App、小程序）无缝接入。
* 支持加载动态权限菜单，细粒度到按钮级别的权限控制。
* 内置强大代码生成器，一键生成前后端代码（java、vue、xml、sql），支持 CRUD 下载。
* 集成 LangChain4j，支持本地知识库检索增强问答（RAG）。

## 技术栈

| 类别 | 技术 | 版本 |
| :--- | :--- | :--- |
| 核心框架 | Spring Boot | 4.1.0 |
| 安全框架 | Spring Security + Jwt | - / 0.9.1 |
| 持久层 | MyBatis + PageHelper | 4.1.0 / 4.1.1 |
| 数据库 | MySQL | 5.7+ |
| 连接池 | Druid | 1.2.28 |
| 缓存 | Redis | - |
| 接口文档 | SpringDoc OpenAPI | 3.1.0 |
| AI / RAG | LangChain4j | 1.20.2 |
| 文档解析 | Apache PDFBox | 3.0.8 |
| 报表导出 | Apache POI | 5.5.1 |
| 前端 | Vue + Element | 多版本 |

## 业务模块

在若依基础功能之上，本系统扩展了以下业务模块（控制层位于 `ruoyi-admin` 的 `com.ruoyi.web.controller` 下对应包，领域模型与服务位于 `ruoyi-system`）：

### 卡片管理（card）

1. **游戏王卡片查询**：接入 ygocdb API v0，卡片信息采用 cards.zip 全量导入 + 单卡接口按需补录；type/attribute/race/ot/setcode 等按 YGOPro 位掩码原值存储，并另设解析列优化查询。
2. **卡组系列**：维护 setcode 与中/日/英文名称映射，支持按系列检索卡片。
3. **禁限卡表**：来源 `/api/v0/limits.json`，按 OCG/TCG/SC 环境与生效日期记录限制/准限制/禁止状态。
4. **用户卡包**：用户自建卡包的增删改查。
5. **决斗场地**：纯前端可视化模拟对战页面，无后端表。

### AI 管理（ai）

6. **AI 提示词**：维护文本/图片/视频/音乐/旁白/多模态六类提示词，移动端通过 `/ai/prompt/list` 同步查看。
7. **模型管理**：维护 AI 生成模型档案（文本/图片/视频/音乐等），走 OpenAI 兼容接口；与知识库模型配置表 `yg_kb_model_config` 完全独立。
8. **技能管理**：维护 AI 技能（标识、分类、说明与提示词/脚本内容），供能力编排与提示词调用。
9. **AI 创作画布**：基于 Fabric.js 的绘图画布，画布 JSON 存库、缩略图落盘；画布节点可发起文本/图片/视频生成任务。
10. **AI 生成任务**：后端异步调用模型管理配置的接口执行生成，图片/视频结果自动落盘并存入素材库。
11. **AI 素材库**：图片/视频素材的上传、检索、预览、重命名与删除，文件存入 `ruoyi.profile` 目录。
12. **知识库（本地 RAG）**：基于 LangChain4j + Ollama/llama.cpp 的检索增强问答，管理端维护知识库与文档，移动端通过 `/ai/kb/list`、`/ai/kb/chat` 使用。

### 数据管理（data）

13. **亚马逊广告数据分析**：按"日期 + 广告活动"粒度记录广告日报，支持报表导入与手动维护、图表分析；ACOS/CTR/CVR 等派生指标由统计接口实时计算，不落库。

### 系统运营（update）

14. **更新日志**：管理端维护版本更新记录（版本、内容、状态），移动端通过 `/update/log/list` 同步展示。

## 内置功能（若依基础能力）

1.  用户管理：系统操作者配置。
2.  部门管理：组织机构（公司、部门、小组）树结构展现，支持数据权限。
3.  岗位管理：用户所属担任职务配置。
4.  菜单管理：菜单、操作权限、按钮权限标识配置。
5.  角色管理：菜单权限分配、按机构划分数据范围权限。
6.  字典管理：系统固定数据维护。
7.  参数管理：系统动态参数配置。
8.  通知公告：通知公告信息发布维护。
9.  操作日志：正常/异常操作日志记录与查询。
10. 登录日志：登录日志记录查询，包含登录异常。
11. 在线用户：当前系统活跃用户状态监控。
12. 定时任务：在线任务调度（添加、修改、删除）与执行结果日志。
13. 代码生成：前后端代码生成（java、html、xml、sql），支持 CRUD 下载。
14. 系统接口：根据业务代码自动生成 API 接口文档。
15. 服务监控：CPU、内存、磁盘、堆栈等信息监视。
16. 缓存监控：缓存信息查询、命令统计等。
17. 在线构建器：拖动表单元素生成 HTML 代码。
18. 连接池监视：数据库连接池状态监视，分析 SQL 找出性能瓶颈。

## 项目结构

```
yaogoht
├── ruoyi-admin        // 主应用入口：启动类与各业务控制层（controller）
├── ruoyi-framework    // 框架核心：安全、拦截器、数据源等
├── ruoyi-system       // 领域模型、服务层与 Mapper（含业务表 domain/service/mapper）
├── ruoyi-quartz       // 定时任务模块
├── ruoyi-generator    // 代码生成模块
├── ruoyi-common       // 通用工具
├── sql                // 数据库脚本（核心表 + 业务表 + 菜单初始化）
├── bin                // 启动脚本
├── doc                // 相关文档
├── ry.bat / ry.sh     // 打包后启动脚本
└── pom.xml            // Maven 父工程（Spring Boot 4.1.0 / JDK 17）
```

## 快速开始

### 环境要求

* JDK 17+
* Maven 3.6+
* MySQL 5.7+
* Redis
* （可选，知识库 RAG）Ollama 或 llama.cpp 服务

### 数据库初始化

按以下顺序执行 `sql/` 目录脚本：

| 顺序 | 脚本 | 说明 |
| :--- | :--- | :--- |
| 1 | `ry_20260417.sql` | 若依核心表（用户、角色、菜单、字典等） |
| 2 | `quartz.sql` | 定时任务表 |
| 3 | `yg_update_log.sql` | 更新日志表（后续业务脚本会向其追加版本记录，需先建表） |
| 4 | `yg_business.sql` | 游戏王卡片信息、卡组系列、禁限卡表 |
| 5 | `yg_card_pack.sql` / `yg_card_duel.sql` | 用户卡包、决斗场地菜单 |
| 6 | `yg_ai_prompt.sql` | AI 提示词 |
| 7 | `yg_knowledge_base.sql` | 知识库与文档、知识库模型配置 |
| 8 | `yg_canvas.sql` / `yg_media.sql` | 画布、AI 素材库 |
| 9 | `yg_ai_model.sql` / `yg_ai_generate_task.sql` | AI 生成模型档案、生成任务 |
| 10 | `yg_ai_skill.sql` | AI 技能 |
| 11 | `yg_amazon_ads.sql` | 亚马逊广告日报与"数据管理"菜单 |

> 注意：`yg_ai_model.sql`、`yg_ai_skill.sql`、`yg_canvas.sql`、`yg_media.sql` 等脚本依赖用户变量 `@ai_parent`，请在**同一会话中整体执行**；各脚本头部附有菜单 ID 占用校验与回滚语句。

### 修改配置

* `ruoyi-admin/src/main/resources/application-druid.yml`：数据库连接、账号密码。
* `ruoyi-admin/src/main/resources/application.yml`：Redis 连接、服务端口（默认 8080）、文件上传路径 `ruoyi.profile`（默认 `/home/ruoyi/uploadPath`，Windows 本地开发可改为 `D:/ruoyi/uploadPath`）、验证码类型等。

### 启动

* 开发环境：IDEA 中运行 `RuoYiApplication`，或命令行 `mvn spring-boot:run -pl ruoyi-admin`。
* 生产环境：`mvn clean package` 后执行 `java -jar ruoyi-admin/target/ruoyi-admin.jar`，或使用 `ry.bat` / `ry.sh`。

默认账号：`admin / admin123`

## 注意事项

* **IDEA 构建资源缺失**：IDEA 的 Maven 构建可能未将 `src/main/resources` 下全部资源复制到 `target/classes`，导致启动时出现占位符缺失（如 `xss.excludes`）或 `mybatis-config.xml`、i18n 配置文件找不到等问题；建议以 `mvn compile` 或完整 Rebuild Project 后再启动。
* **知识库向量服务**：llama.cpp 的 llama-server 默认不开启 embeddings 端点，需以 `--embeddings` 参数启动并指定向量模型（如 bge-m3），且注意服务端口与 Tomcat 8080 冲突；若使用 Ollama 则无需额外配置。切换 embedding 模型后需保证向量维度一致，否则已有知识库检索会错乱。

## 版本分支说明

RuoYi-Vue 后端项目提供 Spring Boot 2.x / 3.x / 4.x 多版本分支的并行维护，本项目基于 **master 分支（Spring Boot 4.x，JDK 17+）**：

| 名称              | 说明                      | 地址                                                    |
| :---------------- | :------------------------ | :------------------------------------------------------ |
| master 默认分支   | Spring Boot 4.x (JDK 17+) | https://gitee.com/y_project/RuoYi-Vue                   |
| springboot3 分支  | Spring Boot 3.x (JDK 17+) | https://gitee.com/y_project/RuoYi-Vue/tree/springboot3  |
| springboot2 分支  | Spring Boot 2.x (JDK 8+)  | https://gitee.com/y_project/RuoYi-Vue/tree/springboot2  |

RuoYi-Vue 前端项目提供 Vue 2.x / 3.x / JavaScript / TypeScript 版本，均可混用搭配：

| 项目名称      | **RuoYi-Vue2** | **RuoYi-Vue3** | **RuoYi-Vue3-TypeScript**   |
| :---          | :---           | :---           | :---                        |
| **前端框架**  | Vue 2         | Vue 3          | Vue 3                       |
| **脚本语言**  | JavaScript    | JavaScript     | TypeScript                  |
| **构建工具**  | Vue CLI       | Vite           | Vite                        |
| **UI 组件库** | Element UI    | Element Plus   | Element Plus                |
| **状态管理**  | Vuex          | Pinia          | Pinia                       |
| **路由管理**  | Vue Router 3  | Vue Router 4   | Vue Router 4                |
| **仓库地址**  | [RuoYi-Vue2](https://gitcode.com/yangzongzhuan/RuoYi-Vue2) | [RuoYi-Vue3](https://gitcode.com/yangzongzhuan/RuoYi-Vue3) | [RuoYi-Vue3-TypeScript](https://gitcode.com/yangzongzhuan/RuoYi-Vue3/tree/typescript) |

## 致谢

* 感谢若依（RuoYi）开源平台，若依是一套全部开源的快速开发平台，毫无保留给个人及企业免费使用。
* 若依文档地址：http://doc.ruoyi.vip
* 卡片数据来源：[ygocdb](https://ygocdb.com)（游戏王卡片数据库）
