-- ----------------------------
-- AI提示词业务表
-- 用途：管理端维护 AI 生成提示词（文本/图片/视频/音乐/旁白/多模态），
--       移动端通过 /ai/prompt/list 接口查看（同步展示）。
-- ----------------------------

-- ----------------------------
-- 1、AI提示词表
-- ----------------------------
drop table if exists yg_ai_prompt;
create table yg_ai_prompt (
  prompt_id         bigint(20)      not null auto_increment    comment '提示词主键',
  prompt_name       varchar(200)    not null                   comment '提示词标题',
  prompt_type       tinyint(2)      not null default 1         comment '生成类型(1:文本 2:图片 3:视频 4:音乐 5:旁白 6:多模态)',
  prompt_content    text            not null                   comment '提示词内容',
  model_name        varchar(100)    default ''                 comment '适用模型(如ChatGPT/Midjourney/Suno等)',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (prompt_id),
  key idx_prompt_name (prompt_name(64)),
  key idx_prompt_type (prompt_type, status)
) engine=innodb auto_increment=1 comment = 'AI提示词表';


-- ----------------------------
-- 2、字典数据（生成类型）
-- ----------------------------
insert into sys_dict_type values(null, '提示词类型', 'yg_prompt_type', '0', 'admin', sysdate(), '', null, 'AI提示词生成类型');

insert into sys_dict_data values(null, 1, '文本',   '1', 'yg_prompt_type', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '图片',   '2', 'yg_prompt_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '视频',   '3', 'yg_prompt_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 4, '音乐',   '4', 'yg_prompt_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 5, '旁白',   '5', 'yg_prompt_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 6, '多模态', '6', 'yg_prompt_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、菜单（使用 2100+ 预留ID段，挂在侧边栏"卡片管理"下方）
-- ----------------------------
insert into sys_menu values('2100', 'AI提示词', '0', '6', 'aiprompt', 'ai/prompt/index', '', '', 1, 0, 'C', '0', '0', 'ai:prompt:list', 'edit', 'admin', sysdate(), '', null, 'AI提示词菜单');
insert into sys_menu values('2101', '提示词查询', '2100', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:prompt:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2102', '提示词新增', '2100', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:prompt:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2103', '提示词修改', '2100', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:prompt:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2104', '提示词删除', '2100', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:prompt:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2105', '提示词导出', '2100', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:prompt:export', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 4、普通角色授权（移动端用户需要查看提示词）
-- ai:prompt:list(2100) ai:prompt:query(2101)
-- ----------------------------
insert into sys_role_menu values ('2', '2100');
insert into sys_role_menu values ('2', '2101');


-- ----------------------------
-- 5、示例数据
-- ----------------------------
insert into yg_ai_prompt (prompt_name, prompt_type, prompt_content, model_name, status, create_by, create_time, remark) values
('赛博朋克城市夜景（图片）', 2, 'A futuristic cyberpunk city at night, neon lights reflecting on wet streets, flying cars, ultra detailed, cinematic lighting, 8k', 'Midjourney', '0', 'admin', sysdate(), '图片生成示例'),
('产品宣传旁白（旁白）', 5, '用沉稳大气的男声，为一款智能手表撰写60秒广告旁白，突出健康监测与长续航卖点，语言简洁有感染力。', '通义/剪映', '0', 'admin', sysdate(), '旁白生成示例'),
('背景音乐（音乐）', 4, 'upbeat electronic dance music with strong bass drop, 120 bpm, energetic, suitable for workout video', 'Suno', '0', 'admin', sysdate(), '音乐生成示例');
