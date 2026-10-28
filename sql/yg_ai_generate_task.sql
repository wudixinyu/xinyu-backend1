-- ----------------------------
-- AI生成任务表
-- 用途：画布（AI创作画布）节点发起的文本/图片/视频生成任务，
--       后端异步调用"模型管理"(yg_ai_model)配置的 OpenAI 兼容接口，
--       图片/视频结果自动落盘并存入素材库(yg_media)。
-- ----------------------------
-- 执行说明：请在同一会话中整体执行本脚本。
-- 执行前校验（应为空）：SELECT menu_id FROM sys_menu WHERE menu_id = 2306;
-- ----------------------------

-- ----------------------------
-- 1、AI生成任务表
-- ----------------------------
drop table if exists yg_ai_generate_task;
create table yg_ai_generate_task (
  task_id           bigint(20)      not null auto_increment    comment '任务主键',
  canvas_id         bigint(20)      default 0                  comment '来源画布ID(0表示非画布发起)',
  node_id           varchar(64)     default ''                 comment '来源画布节点ID',
  model_id          bigint(20)      not null                   comment '使用模型ID(yg_ai_model)',
  generate_type     tinyint(2)      not null                   comment '生成类型(1:文本 2:图片 3:视频)',
  prompt            text            not null                   comment '提示词',
  status            char(1)         default '0'                comment '状态（0排队 1生成中 2成功 3失败）',
  result_text       mediumtext                                 comment '文本生成结果',
  result_url        varchar(500)    default ''                 comment '媒体结果地址(/profile/...)',
  media_id          bigint(20)      default 0                  comment '生成的素材ID(yg_media, 图片/视频)',
  error_msg         varchar(1000)   default null               comment '失败原因',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (task_id),
  key idx_task_status (status),
  key idx_task_canvas (canvas_id, node_id)
) engine=innodb auto_increment=1 comment = 'AI生成任务表';


-- ----------------------------
-- 2、字典数据（任务状态）
-- ----------------------------
insert into sys_dict_type values(null, 'AI生成任务状态', 'yg_generate_task_status', '0', 'admin', sysdate(), '', null, 'AI生成任务状态');

insert into sys_dict_data values(null, 1, '排队',   '0', 'yg_generate_task_status', '', 'info',    'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '生成中', '1', 'yg_generate_task_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '成功',   '2', 'yg_generate_task_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 4, '失败',   '3', 'yg_generate_task_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、画布"AI生成"按钮权限（2306，挂在"画布"菜单 2300 下）
-- ----------------------------
insert into sys_menu values('2306', 'AI生成', '2300', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:canvas:generate', '#', 'admin', sysdate(), '', null, '画布节点AI生成按钮权限');


-- ----------------------------
-- 4、旧绘图画布数据迁移为创作画布节点格式
-- ----------------------------
update yg_canvas set content = '{"nodes":[]}' where content is null or content not like '%"nodes"%';
update yg_canvas set thumbnail = '' where thumbnail <> '';
