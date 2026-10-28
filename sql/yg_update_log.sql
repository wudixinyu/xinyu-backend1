-- ----------------------------
-- 更新日志业务表
-- 用途：管理端维护版本更新记录（版本、时间、内容），
--       移动端通过 /update/log/list 接口查看（同步展示）。
-- ----------------------------

-- ----------------------------
-- 1、更新日志表
-- ----------------------------
drop table if exists yg_update_log;
create table yg_update_log (
  log_id          bigint(20)      not null auto_increment    comment '日志主键',
  version         varchar(50)     not null                   comment '版本号',
  content         text            not null                   comment '更新内容',
  status          char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag        char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by       varchar(64)     default ''                 comment '创建者',
  create_time     datetime                                   comment '创建时间',
  update_by       varchar(64)     default ''                 comment '更新者',
  update_time     datetime                                   comment '更新时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (log_id),
  key idx_version (version)
) engine=innodb auto_increment=1 comment = '更新日志表';


-- ----------------------------
-- 2、菜单（使用 2110+ 预留ID段，挂在侧边栏"AI提示词"下方）
-- ----------------------------
insert into sys_menu values('2110', '更新日志', '0', '7', 'updatelog', 'update/log/index', '', '', 1, 0, 'C', '0', '0', 'update:log:list', 'time', 'admin', sysdate(), '', null, '更新日志菜单');
insert into sys_menu values('2111', '日志查询', '2110', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'update:log:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2112', '日志新增', '2110', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'update:log:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2113', '日志修改', '2110', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'update:log:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2114', '日志删除', '2110', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'update:log:remove', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 3、普通角色授权（移动端用户需要查看更新日志）
-- update:log:list(2110) update:log:query(2111)
-- ----------------------------
insert into sys_role_menu values ('2', '2110');
insert into sys_role_menu values ('2', '2111');


-- ----------------------------
-- 4、示例数据
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.0.0', '1. 新增卡片图鉴浏览与搜索功能；\n2. 新增我的收藏；\n3. 修复若干已知问题。', '0', 'admin', sysdate(), '首发版本'),
('V1.1.0', '1. 新增 AI 提示词模块；\n2. 新增卡包与决斗功能；\n3. 优化图片放大查看体验。', '0', 'admin', sysdate(), '功能更新');
