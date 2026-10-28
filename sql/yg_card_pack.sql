-- ----------------------------
-- 用户卡包管理（卡片管理 > 用户卡包）
-- ----------------------------
-- 1、用户卡包表
drop table if exists yg_user_card_pack;
create table yg_user_card_pack (
  pack_id         bigint(20)      not null auto_increment    comment '卡包主键',
  user_id         bigint(20)      not null                   comment '用户id',
  pack_name       varchar(100)    not null                   comment '卡包名称',
  create_time     datetime                                   comment '创建时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (pack_id),
  key idx_user_id (user_id)
) engine=innodb auto_increment=1 comment = '用户卡包表';

-- 2、用户卡包明细表
drop table if exists yg_user_card_pack_item;
create table yg_user_card_pack_item (
  item_id         bigint(20)      not null auto_increment    comment '明细主键',
  pack_id         bigint(20)      not null                   comment '卡包主键',
  card_id         bigint(20)      not null                   comment '卡片密码(id)',
  quantity        int(4)          default 1                  comment '数量',
  create_time     datetime                                   comment '添加时间',
  remark          varchar(500)    default null               comment '备注',
  primary key (item_id),
  unique key uk_pack_card (pack_id, card_id),
  key idx_pack_id (pack_id)
) engine=innodb auto_increment=1 comment = '用户卡包明细表';

-- 3、菜单（挂到卡片管理 2000 下，使用 2040 预留ID段）
delete from sys_role_menu where menu_id in (2040, 2041, 2042, 2043, 2044);
delete from sys_menu where menu_id in (2040, 2041, 2042, 2043, 2044);
insert into sys_menu values('2040', '用户卡包', '2000', '5', 'pack', 'card/pack/index', '', '', 1, 0, 'C', '0', '0', 'card:pack:list', 'documentation', 'admin', sysdate(), '', null, '用户卡包管理菜单');
insert into sys_menu values('2041', '卡包查询', '2040', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'card:pack:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2042', '卡包新增', '2040', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'card:pack:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2043', '卡包修改', '2040', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'card:pack:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2044', '卡包删除', '2040', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'card:pack:remove', '#', 'admin', sysdate(), '', null, '');
