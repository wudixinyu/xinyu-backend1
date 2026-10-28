-- ----------------------------
-- AI生成模型档案表
-- 用途：管理端维护 AI 生成模型档案（文本/图片/视频/音乐等），
--       与知识库模型配置表 yg_kb_model_config 完全独立，不复用表/接口/权限。
-- ----------------------------
-- 执行说明：请在同一会话中整体执行本脚本（依赖用户变量 @ai_parent）。
-- 执行前校验（应为空）：SELECT menu_id, menu_name FROM sys_menu WHERE menu_id BETWEEN 2500 AND 2599;
-- 回滚段（如需撤销菜单）：
--   delete from sys_role_menu where menu_id between 2500 and 2599;
--   delete from sys_menu where menu_id between 2500 and 2599;
-- ----------------------------

-- ----------------------------
-- 1、AI生成模型档案表
-- ----------------------------
drop table if exists yg_ai_model;
create table yg_ai_model (
  model_id          bigint(20)      not null auto_increment    comment '模型主键',
  model_name        varchar(100)    not null                   comment '模型名称(如gpt-4o/Midjourney/Suno等)',
  model_type        tinyint(2)      not null default 1         comment '模型类型(1:文本 2:图片 3:视频 4:音乐 5:旁白 6:多模态 7:向量化)',
  provider          varchar(100)    default ''                 comment '提供方(OpenAI/Anthropic/阿里/Suno等)',
  api_url           varchar(300)    default ''                 comment 'API地址',
  api_key           varchar(300)    default ''                 comment 'API Key',
  sort              int(4)          default 0                  comment '显示排序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (model_id),
  key idx_ai_model_name (model_name(64)),
  key idx_ai_model_type (model_type, status)
) engine=innodb auto_increment=1 comment = 'AI生成模型档案表';


-- ----------------------------
-- 2、字典数据（模型类型，前6项与 yg_prompt_type 对齐）
-- ----------------------------
insert into sys_dict_type values(null, 'AI模型类型', 'yg_model_type', '0', 'admin', sysdate(), '', null, 'AI生成模型类型');

insert into sys_dict_data values(null, 1, '文本',   '1', 'yg_model_type', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '图片',   '2', 'yg_model_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '视频',   '3', 'yg_model_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 4, '音乐',   '4', 'yg_model_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 5, '旁白',   '5', 'yg_model_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 6, '多模态', '6', 'yg_model_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 7, '向量化', '7', 'yg_model_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、菜单（使用 2500+ 预留ID段，动态挂到"AI管理"目录下）
-- ----------------------------
SET @ai_parent = IFNULL(
  (SELECT menu_id FROM sys_menu WHERE menu_name='AI管理' AND menu_type='M' AND parent_id=0 LIMIT 1), 0);

insert into sys_menu values('2500', '模型管理', @ai_parent, '12', 'aimodel', 'ai/model/index', '', '', 1, 0, 'C', '0', '0', 'ai:model:list', 'star', 'admin', sysdate(), '', null, 'AI模型管理菜单');
insert into sys_menu values('2501', '模型查询', '2500', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:model:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2502', '模型新增', '2500', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:model:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2503', '模型修改', '2500', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:model:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2504', '模型删除', '2500', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:model:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2505', '模型导出', '2500', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:model:export', '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 4、示例数据
-- ----------------------------
insert into yg_ai_model (model_name, model_type, provider, api_url, api_key, sort, status, create_by, create_time, remark) values
('gpt-4o', 1, 'OpenAI', 'https://api.openai.com/v1', '', 1, '0', 'admin', sysdate(), '文本对话模型示例'),
('Midjourney', 2, 'Midjourney', '', '', 2, '0', 'admin', sysdate(), '图片生成模型示例'),
('Suno', 4, 'Suno', 'https://api.suno.com', '', 3, '0', 'admin', sysdate(), '音乐生成模型示例');
