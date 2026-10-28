-- ----------------------------
-- 游戏王卡片查询业务表（接入 ygocdb API v0）
-- 同步策略：cards.zip 全量导入 + 单卡接口按需补录
-- 说明：type/attribute/race/ot/setcode 等为 YGOPro 位掩码原值存储，
--       常用查询字段另设解析列，避免位运算无法走索引的问题。
-- ----------------------------

-- ----------------------------
-- 1、卡片信息表
-- ----------------------------
drop table if exists yg_card_info;
create table yg_card_info (
  card_id           bigint(20)      not null                   comment '卡片密码(id)',
  cid               int(11)         default null               comment '官方数据库标识(cid)',
  cn_name           varchar(200)    default ''                 comment '中文名称(YGOPro译名)',
  sc_name           varchar(200)    default ''                 comment '官方简体中文名称',
  md_name           varchar(200)    default ''                 comment 'Master Duel中文名称',
  jp_name           varchar(200)    default ''                 comment '官方日文名称',
  jp_ruby           varchar(200)    default ''                 comment '日文假名注音',
  en_name           varchar(200)    default ''                 comment '官方英文名称',
  ot                int(11)         default 0                  comment '可用环境位掩码(1:OCG 2:TCG 4:动画 8: illegal 16:视频 32:非法 64:仅OCG独有 128:仅TCG独有 等)',
  setcode           bigint(20) unsigned default 0              comment '卡组系列code位掩码(64bit,关联yg_card_set)',
  type              int(11)         default 0                  comment '卡片类型位掩码(1:怪兽 2:魔法 4:陷阱 16:通常 32:效果 ...)',
  card_kind         tinyint(2)      default 1                  comment '卡片大类(1:怪兽 2:魔法 3:陷阱,由type解析)',
  attribute         int(11)         default 0                  comment '属性位掩码(1:地 2:水 4:炎 8:风 16:光 32:暗 64:神)',
  race              int(11)         default 0                  comment '种族位掩码(龙/战士/魔法使者等)',
  atk               int(11)         default -2                 comment '攻击力(-2:?)',
  def               int(11)         default -2                 comment '防御力(连接怪兽该字段存link_marker位掩码)',
  level_raw         int(11)         default 0                  comment '等级原始值(灵摆卡高16位含刻度)',
  card_level        tinyint(4)      default 0                  comment '等级/阶级(Rank<<8解析)',
  lscale            tinyint(4)      default null               comment '灵摆左刻度(非灵摆为NULL)',
  rscale            tinyint(4)      default null               comment '灵摆右刻度(非灵摆为NULL)',
  link_marker       int(11)         default null               comment '连接标记位掩码(非连接怪兽为NULL)',
  types_desc        varchar(500)    default ''                 comment '类型描述文本(text.types)',
  effect_desc       text                                       comment '效果文本(text.desc)',
  pendulum_desc     text                                       comment '灵摆效果文本(text.pdesc)',
  image_url         varchar(200)    default ''                 comment '卡图地址(CDN相对路径,前端拼接!half等参数)',
  sync_source       tinyint(2)      default 1                  comment '同步来源(1:全量导入 2:按需补录)',
  sync_time         datetime                                   comment '最后同步时间',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (card_id),
  unique key uk_cid (cid),
  key idx_cn_name (cn_name(64)),
  key idx_en_name (en_name(64)),
  key idx_kind_attr (card_kind, attribute),
  key idx_level (card_level),
  key idx_sync_time (sync_time)
) engine=innodb comment = '游戏王卡片信息表';


-- ----------------------------
-- 2、卡组系列表（setcode 映射,来源 cards.zip 的 setcodes 字段）
-- ----------------------------
drop table if exists yg_card_set;
create table yg_card_set (
  set_id            bigint(20)      not null auto_increment    comment '系列主键',
  set_code          varchar(20)     not null                   comment '系列code(十六进制字符串,如0x57)',
  set_value         bigint(20) unsigned default 0              comment '系列code数值(用于与卡片setcode按位匹配)',
  cn_name           varchar(100)    default ''                 comment '系列中文名',
  jp_name           varchar(100)    default ''                 comment '系列日文名',
  en_name           varchar(100)    default ''                 comment '系列英文名',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (set_id),
  unique key uk_set_code (set_code),
  key idx_set_cn_name (cn_name)
) engine=innodb auto_increment=1 comment = '卡组系列表';


-- ----------------------------
-- 3、禁限卡表（来源 /api/v0/limits.json）
-- ----------------------------
drop table if exists yg_limit_list;
create table yg_limit_list (
  limit_id          bigint(20)      not null auto_increment    comment '主键',
  card_id           bigint(20)      not null                   comment '卡片密码(id)',
  ot_type           varchar(10)     not null                   comment '适用环境(ocg/tcg/sc)',
  limit_type        tinyint(2)      not null default 0         comment '限制类型(0:无限制 1:限制 2:准限制 3:禁止)',
  effective_date    date            not null                   comment '生效日期',
  is_current        char(1)         default '1'                comment '是否当前卡表（1是 0历史）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (limit_id),
  unique key uk_card_ot_date (card_id, ot_type, effective_date),
  key idx_current (ot_type, is_current, limit_type)
) engine=innodb auto_increment=1 comment = '禁限卡表';


-- ----------------------------
-- 4、用户收藏表
-- ----------------------------
drop table if exists yg_user_collection;
create table yg_user_collection (
  collect_id        bigint(20)      not null auto_increment    comment '收藏主键',
  user_id           bigint(20)      not null                   comment '用户id',
  card_id           bigint(20)      not null                   comment '卡片密码(id)',
  quantity          int(4)          default 1                  comment '数量(1-3)',
  create_time       datetime                                   comment '收藏时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (collect_id),
  unique key uk_user_card (user_id, card_id),
  key idx_user_id (user_id)
) engine=innodb auto_increment=1 comment = '用户卡片收藏表';


-- ----------------------------
-- 5、字典数据（前端展示/筛选用）
-- ----------------------------
insert into sys_dict_type values(null, '卡片大类',   'yg_card_kind',  '0', 'admin', sysdate(), '', null, '游戏王卡片大类');
insert into sys_dict_type values(null, '限制类型',   'yg_limit_type', '0', 'admin', sysdate(), '', null, '禁限卡表限制类型');
insert into sys_dict_type values(null, '同步来源',   'yg_sync_source','0', 'admin', sysdate(), '', null, '卡片数据同步来源');

insert into sys_dict_data values(null, 1, '怪兽',     '1', 'yg_card_kind', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '魔法',     '2', 'yg_card_kind', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '陷阱',     '3', 'yg_card_kind', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');

insert into sys_dict_data values(null, 1, '无限制',   '0', 'yg_limit_type', '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '限制',     '1', 'yg_limit_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 3, '准限制',   '2', 'yg_limit_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 4, '禁止',     '3', 'yg_limit_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');

insert into sys_dict_data values(null, 1, '全量导入', '1', 'yg_sync_source','', 'info',    'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '按需补录', '2', 'yg_sync_source','', 'info',    'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 6、菜单（挂到 sys_menu,使用 2000+ 预留ID段）
-- ----------------------------
-- 一级目录：卡片管理
insert into sys_menu values('2000', '卡片管理', '0', '5', 'card', null, '', '', 1, 0, 'M', '0', '0', '', 'star', 'admin', sysdate(), '', null, '游戏王卡片管理目录');

-- 卡片信息
insert into sys_menu values('2001', '卡片信息', '2000', '1', 'info', 'card/info/index', '', '', 1, 0, 'C', '0', '0', 'card:info:list', 'list', 'admin', sysdate(), '', null, '卡片信息菜单');
insert into sys_menu values('2002', '卡片查询', '2001', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'card:info:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2003', '卡片新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'card:info:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2004', '卡片修改', '2001', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'card:info:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2005', '卡片删除', '2001', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'card:info:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2006', '全量同步', '2001', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'card:info:sync',   '#', 'admin', sysdate(), '', null, '');

-- 卡组系列
insert into sys_menu values('2010', '卡组系列', '2000', '2', 'set', 'card/set/index', '', '', 1, 0, 'C', '0', '0', 'card:set:list', 'tag', 'admin', sysdate(), '', null, '卡组系列菜单');
insert into sys_menu values('2011', '系列查询', '2010', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'card:set:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2012', '系列同步', '2010', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'card:set:sync',   '#', 'admin', sysdate(), '', null, '');

-- 禁限卡表
insert into sys_menu values('2020', '禁限卡表', '2000', '3', 'limit', 'card/limit/index', '', '', 1, 0, 'C', '0', '0', 'card:limit:list', 'checkbox', 'admin', sysdate(), '', null, '禁限卡表菜单');
insert into sys_menu values('2021', '禁限查询', '2020', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'card:limit:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2022', '禁限同步', '2020', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'card:limit:sync',  '#', 'admin', sysdate(), '', null, '');

-- 用户收藏（管理端查看）
insert into sys_menu values('2030', '用户收藏', '2000', '4', 'collection', 'card/collection/index', '', '', 1, 0, 'C', '0', '0', 'card:collection:list', 'people', 'admin', sysdate(), '', null, '用户收藏菜单');
insert into sys_menu values('2031', '收藏查询', '2030', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'card:collection:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2032', '收藏删除', '2030', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'card:collection:remove', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 7、普通角色授权（移动端用户需要查卡/收藏权限）
-- card:info:list(2001) card:info:query(2002)
-- ----------------------------
insert into sys_role_menu values ('2', '2000');
insert into sys_role_menu values ('2', '2001');
insert into sys_role_menu values ('2', '2002');
