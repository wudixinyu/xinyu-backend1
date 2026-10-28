-- ----------------------------
-- 画布业务表
-- 用途：管理端维护绘图画布（基于 Fabric.js，画布 JSON 存 content 字段，
--       缩略图经 /common/upload 存 ruoyi.profile 目录，DB 记录 /profile/... 相对路径）。
-- ----------------------------
-- 执行说明：请在同一会话中整体执行本脚本（依赖用户变量 @ai_parent）。
-- 执行前校验（应为空）：SELECT menu_id, menu_name FROM sys_menu WHERE menu_id BETWEEN 2300 AND 2399;
-- 回滚段（如需撤销菜单）：
--   delete from sys_role_menu where menu_id between 2300 and 2399;
--   delete from sys_menu where menu_id between 2300 and 2399;
-- ----------------------------

-- ----------------------------
-- 1、画布表
-- ----------------------------
drop table if exists yg_canvas;
create table yg_canvas (
  canvas_id         bigint(20)      not null auto_increment    comment '画布主键',
  canvas_name       varchar(200)    not null                   comment '画布名称',
  content           longtext        not null                   comment '画布JSON（fabric.js toJSON）',
  thumbnail         varchar(500)    default ''                 comment '缩略图地址(/profile/...)',
  canvas_width      int(11)         default 900                comment '画布宽度(px)',
  canvas_height     int(11)         default 600                comment '画布高度(px)',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (canvas_id),
  key idx_canvas_name (canvas_name(64)),
  key idx_canvas_status (status)
) engine=innodb auto_increment=1 comment = '画布表';


-- ----------------------------
-- 2、菜单（使用 2300+ 预留ID段，动态挂到"AI管理"目录下）
-- ----------------------------
SET @ai_parent = IFNULL(
  (SELECT menu_id FROM sys_menu WHERE menu_name='AI管理' AND menu_type='M' AND parent_id=0 LIMIT 1), 0);

insert into sys_menu values('2300', '画布', @ai_parent, '10', 'aicanvas', 'ai/canvas/index', '', '', 1, 0, 'C', '0', '0', 'ai:canvas:list', 'build', 'admin', sysdate(), '', null, '画布管理菜单');
insert into sys_menu values('2301', '画布查询', '2300', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:canvas:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2302', '画布新增', '2300', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:canvas:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2303', '画布修改', '2300', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:canvas:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2304', '画布删除', '2300', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:canvas:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2305', '画布导出', '2300', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:canvas:export', '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、示例数据
-- ----------------------------
insert into yg_canvas (canvas_name, content, thumbnail, canvas_width, canvas_height, status, create_by, create_time, remark) values
('示例画布', '{"nodes":[]}', '', 900, 600, '0', 'admin', sysdate(), '空白示例画布');
