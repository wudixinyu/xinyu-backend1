-- ----------------------------
-- 知识库（本地 RAG）业务表
-- 用途：管理端维护知识库与文档（基于 LangChain4j + Ollama/llama.cpp 的检索增强问答），
--       移动端通过 /ai/kb/list 与 /ai/kb/chat 接口使用（同步展示）。
-- ----------------------------

-- ----------------------------
-- 1、知识库表
-- ----------------------------
drop table if exists yg_knowledge_base;
create table yg_knowledge_base (
  kb_id             bigint(20)      not null auto_increment    comment '知识库主键',
  kb_name           varchar(100)    not null                   comment '知识库名称',
  description       varchar(500)    default ''                 comment '描述',
  embedding_model   varchar(100)    default ''                 comment '向量化模型(建库时固化)',
  embedding_dim     int(11)         default 0                  comment '向量维度',
  doc_count         int(11)         default 0                  comment '文档数',
  chunk_count       int(11)         default 0                  comment '分块数',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (kb_id),
  key idx_kb_name (kb_name(64)),
  key idx_kb_status (status)
) engine=innodb auto_increment=1 comment = '知识库表';


-- ----------------------------
-- 2、知识库文档表
-- ----------------------------
drop table if exists yg_kb_document;
create table yg_kb_document (
  doc_id            bigint(20)      not null auto_increment    comment '文档主键',
  kb_id             bigint(20)      not null                   comment '所属知识库ID',
  file_name         varchar(255)    not null                   comment '原始文件名',
  file_path         varchar(500)    not null                   comment '存储路径',
  file_type         varchar(10)     default ''                 comment '文件类型(txt/md/pdf/docx)',
  file_size         bigint(20)      default 0                  comment '文件大小(字节)',
  chunk_count       int(11)         default 0                  comment '分块数',
  index_status      char(1)         default '0'                comment '索引状态（0待索引 1索引中 2成功 3失败）',
  error_msg         varchar(1000)   default null               comment '失败原因',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (doc_id),
  key idx_doc_kb_id (kb_id, del_flag)
) engine=innodb auto_increment=1 comment = '知识库文档表';


-- ----------------------------
-- 3、知识库问答日志表
-- ----------------------------
drop table if exists yg_kb_chat_log;
create table yg_kb_chat_log (
  log_id            bigint(20)      not null auto_increment    comment '日志主键',
  kb_id             bigint(20)      not null                   comment '知识库ID',
  question          varchar(2000)   not null                   comment '问题',
  answer            mediumtext                                 comment '回答',
  source_json       text                                       comment '引用来源(JSON)',
  cost_ms           bigint(20)      default 0                  comment '耗时(毫秒)',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  primary key (log_id),
  key idx_log_kb_id (kb_id)
) engine=innodb auto_increment=1 comment = '知识库问答日志表';


-- ----------------------------
-- 4、字典数据（索引状态）
-- ----------------------------
insert into sys_dict_type values(null, '知识库索引状态', 'yg_kb_index_status', '0', 'admin', sysdate(), '', null, '知识库文档索引状态');

insert into sys_dict_data values(null, 1, '待索引', '0', 'yg_kb_index_status', '', 'info',    'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '索引中', '1', 'yg_kb_index_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '成功',   '2', 'yg_kb_index_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 4, '失败',   '3', 'yg_kb_index_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 5、菜单（使用 2200+ 预留ID段，挂在侧边栏"AI提示词"下方）
-- ----------------------------
insert into sys_menu values('2200', '知识库', '0', '7', 'aikb', 'ai/kb/index', '', '', 1, 0, 'C', '0', '0', 'ai:kb:list', 'book', 'admin', sysdate(), '', null, '知识库菜单');
insert into sys_menu values('2201', '知识库查询', '2200', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2202', '知识库新增', '2200', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2203', '知识库修改', '2200', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2204', '知识库删除', '2200', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2205', '文档管理', '2200', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:upload',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2206', '重建索引', '2200', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:rebuild',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2207', '问答对话', '2200', '7', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:chat',      '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 6、普通角色授权（移动端用户需要查看知识库与问答）
-- ai:kb:list(2200) ai:kb:query(2201) ai:kb:chat(2207)
-- ----------------------------
insert into sys_role_menu values ('2', '2200');
insert into sys_role_menu values ('2', '2201');
insert into sys_role_menu values ('2', '2207');


-- ----------------------------
-- 7、示例数据
-- ----------------------------
insert into yg_knowledge_base (kb_name, description, embedding_model, embedding_dim, doc_count, chunk_count, status, create_by, create_time, remark) values
('产品资料库', '产品说明书、FAQ等文档知识', 'bge-m3', 1024, 0, 0, '0', 'admin', sysdate(), '示例知识库');


-- ----------------------------
-- 8、模型配置表（后台"模型管理"页面维护，保存后运行时生效；单行表）
-- ----------------------------
drop table if exists yg_kb_model_config;
create table yg_kb_model_config (
  id                        bigint(20)      not null auto_increment    comment '主键',
  provider                  varchar(20)     not null default 'ollama'  comment '模型提供方(ollama/openai/custom)，兼容旧数据',
  chat_provider             varchar(20)     not null default 'ollama'  comment '对话模型提供方(ollama/openai/custom)',
  embedding_provider        varchar(20)     not null default 'ollama'  comment '向量化模型提供方(ollama/openai/custom)',
  ollama_base_url           varchar(200)    default 'http://localhost:11434'  comment 'Ollama服务地址',
  ollama_chat_model         varchar(100)    default 'qwen2.5:7b'                comment 'Ollama对话模型',
  ollama_chat_temperature   decimal(3,2)    default 0.30                        comment 'Ollama温度',
  ollama_embedding_model    varchar(100)    default 'bge-m3'                    comment 'Ollama向量化模型',
  ollama_embedding_dim      int(11)         default 1024                        comment 'Ollama向量维度',
  ollama_timeout_seconds    int(11)         default 300                         comment 'Ollama超时秒数',
  openai_base_url           varchar(200)    default 'http://localhost:8080/v1'  comment 'llama-server/OpenAI兼容地址',
  openai_api_key            varchar(100)    default 'none'                      comment 'API Key',
  openai_chat_model         varchar(100)    default 'qwen2.5-7b-instruct-q8_0'  comment 'OpenAI兼容对话模型',
  openai_chat_temperature   decimal(3,2)    default 0.30                        comment 'OpenAI兼容温度',
  openai_embedding_model    varchar(100)    default 'bge-m3'                    comment 'OpenAI兼容向量化模型',
  openai_embedding_dim      int(11)         default 1024                        comment 'OpenAI兼容向量维度',
  openai_timeout_seconds    int(11)         default 300                         comment 'OpenAI兼容超时秒数',
  custom_chat_api_id        bigint(20)      default null                        comment '对话用自定义API ID(yg_kb_custom_api.id)',
  custom_embedding_api_id   bigint(20)      default null                        comment '向量化用自定义API ID(yg_kb_custom_api.id)',
  chunk_strategy            varchar(20)     default 'recursive'                 comment '分块策略(recursive/paragraph/sentence/separator/markdown)',
  chunk_separator           varchar(100)    default ''                          comment '自定义分隔符(strategy=separator时生效)',
  chunk_max_size            int(11)         default 500                         comment '分块最大长度(字符)',
  chunk_overlap             int(11)         default 100                         comment '分块重叠长度(字符)',
  retrieval_max_results     int(11)         default 4                           comment '检索返回片段数',
  retrieval_min_score       decimal(4,3)    default 0.300                       comment '检索最低相似度',
  update_by                 varchar(64)     default ''                          comment '更新者',
  update_time               datetime                                            comment '更新时间',
  primary key (id)
) engine=innodb auto_increment=1 comment = '知识库模型配置表';

-- 已有数据库升级（分块策略功能，勿重复执行）：
-- alter table yg_kb_model_config add column chunk_strategy varchar(20) default 'recursive' comment '分块策略(recursive/paragraph/sentence/separator/markdown)' after openai_timeout_seconds;
-- alter table yg_kb_model_config add column chunk_separator varchar(100) default '' comment '自定义分隔符(strategy=separator时生效)' after chunk_strategy;

-- ----------------------------
-- 8.1、自定义API配置表（OpenAI兼容，可维护多条；模型管理页面维护）
-- ----------------------------
drop table if exists yg_kb_custom_api;
create table yg_kb_custom_api (
  id                        bigint(20)      not null auto_increment    comment '主键',
  name                      varchar(100)    not null                   comment '名称(下拉展示用)',
  base_url                  varchar(200)    default ''                 comment '服务地址(OpenAI兼容)',
  api_key                   varchar(200)    default ''                 comment 'API Key',
  chat_model                varchar(100)    default ''                 comment '对话模型',
  chat_temperature          decimal(3,2)    default 0.30               comment '温度',
  embedding_model           varchar(100)    default ''                 comment '向量化模型',
  embedding_dim             int(11)         default 1024               comment '向量维度',
  timeout_seconds           int(11)         default 60                 comment '超时秒数',
  create_by                 varchar(64)     default ''                 comment '创建者',
  create_time               datetime                                   comment '创建时间',
  update_by                 varchar(64)     default ''                 comment '更新者',
  update_time               datetime                                   comment '更新时间',
  primary key (id)
) engine=innodb auto_increment=1 comment = '知识库自定义API配置表';

-- 已有数据库升级（自定义API多组管理，勿重复执行；须先在库中执行 yg_kb_custom_api 建表）：
-- alter table yg_kb_model_config add column custom_chat_api_id bigint(20) default null comment '对话用自定义API ID' after openai_timeout_seconds;
-- alter table yg_kb_model_config add column custom_embedding_api_id bigint(20) default null comment '向量化用自定义API ID' after custom_chat_api_id;
-- -- 1) 将旧 remote_* 配置迁移为一条自定义API记录（存在 remote_base_url 时）
-- insert into yg_kb_custom_api (name, base_url, api_key, chat_model, chat_temperature, embedding_model, embedding_dim, timeout_seconds, create_by, create_time)
-- select '外部API', remote_base_url, remote_api_key, remote_chat_model, remote_chat_temperature, remote_embedding_model, remote_embedding_dim, remote_timeout_seconds, 'admin', sysdate()
-- from yg_kb_model_config where remote_base_url is not null and remote_base_url <> '' order by id desc limit 1;
-- -- 2) 回填 custom 引用并把 provider=remote 改为 custom
-- update yg_kb_model_config m
--   join (select id from yg_kb_custom_api order by id desc limit 1) c
--   set m.custom_chat_api_id      = if(m.chat_provider = 'remote', c.id, m.custom_chat_api_id),
--       m.custom_embedding_api_id = if(m.embedding_provider = 'remote', c.id, m.custom_embedding_api_id),
--       m.chat_provider           = if(m.chat_provider = 'remote', 'custom', m.chat_provider),
--       m.embedding_provider      = if(m.embedding_provider = 'remote', 'custom', m.embedding_provider),
--       m.provider                = if(m.provider = 'remote', 'custom', m.provider);
-- -- 3) 迁移完成后删除旧列
-- alter table yg_kb_model_config drop column remote_base_url, drop column remote_api_key, drop column remote_chat_model, drop column remote_chat_temperature, drop column remote_embedding_model, drop column remote_embedding_dim, drop column remote_timeout_seconds;


-- ----------------------------
-- 9、模型管理菜单（2208 挂在侧边栏"知识库"下方；仅管理端使用）
-- ----------------------------
insert into sys_menu values('2208', '模型管理', '0', '8', 'aikbmodel', 'ai/kb/model/index', '', '', 1, 0, 'C', '0', '0', 'ai:kb:model', 'system', 'admin', sysdate(), '', null, '知识库模型管理菜单');
insert into sys_menu values('2209', '模型配置修改', '2208', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:kb:model:edit', '#', 'admin', sysdate(), '', null, '');
