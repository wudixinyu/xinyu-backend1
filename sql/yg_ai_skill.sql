-- ----------------------------
-- AI技能业务表
-- 用途：AI管理下维护技能（Skill），记录技能标识、分类、说明与技能内容（提示词/脚本），
--       供 AI 能力编排、提示词调用等场景使用。
-- ----------------------------
-- 执行说明：请在同一会话中整体执行本脚本（依赖用户变量 @ai_parent）。
-- 执行前校验（应为空）：SELECT menu_id, menu_name FROM sys_menu WHERE menu_id BETWEEN 2700 AND 2799;
-- 回滚段（如需撤销菜单）：
--   delete from sys_role_menu where menu_id between 2700 and 2799;
--   delete from sys_menu where menu_id between 2700 and 2799;
-- ----------------------------

-- ----------------------------
-- 1、技能表
-- ----------------------------
drop table if exists yg_ai_skill;
create table yg_ai_skill (
  skill_id          bigint(20)      not null auto_increment    comment '技能主键',
  skill_name        varchar(100)    not null                   comment '技能名称',
  skill_code        varchar(100)    not null                   comment '技能标识（唯一）',
  category          varchar(50)     default ''                 comment '技能分类',
  description       varchar(500)    default ''                 comment '技能说明',
  content           longtext                                   comment '技能内容（提示词/脚本）',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (skill_id),
  unique key uk_skill_code (skill_code),
  key idx_skill_name (skill_name(64)),
  key idx_skill_status (status)
) engine=innodb auto_increment=1 comment = 'AI技能表';


-- ----------------------------
-- 2、菜单（使用 2700+ 预留ID段，动态挂到"AI管理"目录下）
-- ----------------------------
SET @ai_parent = IFNULL(
  (SELECT menu_id FROM sys_menu WHERE menu_name='AI管理' AND menu_type='M' AND parent_id=0 LIMIT 1), 0);

insert into sys_menu values('2700', '技能管理', @ai_parent, '13', 'aiskill', 'ai/skill/index', '', '', 1, 0, 'C', '0', '0', 'ai:skill:list', 'component', 'admin', sysdate(), '', null, 'AI技能管理菜单');
insert into sys_menu values('2701', '技能查询', '2700', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:skill:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2702', '技能新增', '2700', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:skill:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2703', '技能修改', '2700', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:skill:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2704', '技能删除', '2700', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:skill:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2705', '技能导出', '2700', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:skill:export', '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、示例数据
-- ----------------------------
insert into yg_ai_skill (skill_name, skill_code, category, description, content, status, create_by, create_time, remark) values
('示例技能', 'demo_skill', '通用', '示例技能，可在管理页修改或删除', '你是一个乐于助人的AI助手。', '0', 'admin', sysdate(), '空白示例技能');
