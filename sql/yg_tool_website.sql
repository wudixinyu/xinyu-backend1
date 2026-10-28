-- ----------------------------
-- 工具使用（常用网站工具）业务表
-- 用途：管理端"工具使用"模块，维护常用网站的名称与地址，
--       支持表格/列表两种视图，点击按钮在新窗口打开对应网站。
-- ----------------------------

-- ----------------------------
-- 1、工具网站表（物理删除，不设 del_flag）
-- ----------------------------
drop table if exists yg_tool_website;
create table yg_tool_website (
  tool_id           bigint(20)      not null auto_increment    comment '主键',
  tool_name         varchar(100)    not null                   comment '工具名称',
  website_url       varchar(500)    not null                   comment '网站地址',
  category          varchar(50)     default null               comment '工具分类（字典 yg_tool_category）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (tool_id),
  unique key uk_tool_name (tool_name)
) engine=innodb auto_increment=1 comment = '工具使用网站表';


-- ----------------------------
-- 2、菜单（使用 2800+ 预留ID段；顶级菜单"工具使用"挂在"数据管理"下方）
-- ----------------------------
insert into sys_menu values('2800', '工具使用', '0', '10', 'website', 'tool/website/index', '', '', 1, 0, 'C', '0', '0', 'tool:website:list', 'link', 'admin', sysdate(), '', null, '工具使用菜单');
insert into sys_menu values('2801', '工具查询', '2800', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'tool:website:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2802', '工具新增', '2800', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'tool:website:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2803', '工具修改', '2800', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'tool:website:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2804', '工具删除', '2800', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'tool:website:remove', '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、字典数据（工具分类）
-- ----------------------------
insert into sys_dict_type values(null, '工具分类', 'yg_tool_category', '0', 'admin', sysdate(), '', null, '工具使用网站分类');

insert into sys_dict_data values(null, 1, '开发工具', '1', 'yg_tool_category', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, 'AI工具',   '2', 'yg_tool_category', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '电商平台', '3', 'yg_tool_category', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 4, '数据分析', '4', 'yg_tool_category', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 5, '官方资讯', '5', 'yg_tool_category', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 6, '其他',     '9', 'yg_tool_category', '', 'info',    'Y', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 4、示例数据
-- ----------------------------
insert into yg_tool_website (tool_name, website_url, category, create_by, create_time, remark) values
('若依官网', 'http://ruoyi.vip', '5', 'admin', sysdate(), 'RuoYi 框架官网'),
('GitHub', 'https://github.com', '1', 'admin', sysdate(), '代码托管平台'),
('亚马逊卖家平台', 'https://sellercentral.amazon.com', '3', 'admin', sysdate(), '亚马逊店铺后台'),
('Google Trends', 'https://trends.google.com', '4', 'admin', sysdate(), '关键词趋势分析');


-- ----------------------------
-- 5、存量库升级脚本（已建表的环境执行以下语句即可，勿重复执行 drop/create）
-- ----------------------------
-- alter table yg_tool_website add column category varchar(50) default null comment '工具分类（字典 yg_tool_category）' after website_url;


-- ----------------------------
-- 6、更新日志（向 yg_update_log 追加本模块记录，需先执行 yg_update_log.sql 建表）
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.3.0', '1. 新增"工具使用"模块；\n2. 支持维护常用网站名称与地址，提供表格/列表两种视图；\n3. 点击按钮可在新窗口打开对应网站。', '0', 'admin', sysdate(), '工具使用'),
('V1.3.1', '1. 工具使用新增"分类"功能；\n2. 新增字典 yg_tool_category（开发工具/AI工具/电商平台/数据分析/官方资讯/其他）；\n3. 列表与宫格视图支持分类展示与筛选。', '0', 'admin', sysdate(), '工具使用');
