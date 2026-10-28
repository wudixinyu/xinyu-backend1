-- ----------------------------
-- Shopify店铺数据分析业务表
-- 用途：管理端"数据管理 > Shopify数据分析"模块，
--       记录店铺渠道日报（日期+店铺+销售渠道粒度），支持报表导入/手动维护与图表分析。
--       转化率/客单价等派生指标由统计接口计算，不落库。
-- ----------------------------

-- ----------------------------
-- 1、Shopify店铺渠道日报表（物理删除，不设 del_flag，避免与导入唯一键冲突）
-- ----------------------------
drop table if exists yg_shopify_store_daily;
create table yg_shopify_store_daily (
  daily_id          bigint(20)      not null auto_increment    comment '主键',
  report_date       date            not null                   comment '报告日期',
  store_domain      varchar(128)    default ''                 comment '店铺域名(标识,可选)',
  channel           varchar(255)    not null                   comment '销售渠道',
  sessions          bigint(20)      default 0                  comment '会话数(访客数)',
  orders            int(11)         default 0                  comment '订单数',
  units_sold        int(11)         default 0                  comment '售出件数',
  revenue           decimal(12,2)   default 0.00               comment '销售额',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (daily_id),
  unique key uk_date_store_channel (report_date, store_domain, channel(100)),
  key idx_channel_date (channel(100), report_date),
  key idx_store_date   (store_domain, report_date)
) engine=innodb auto_increment=1 comment = 'Shopify店铺渠道日报表';


-- ----------------------------
-- 2、菜单（使用 2620+ 预留ID段；顶级目录"数据管理"(2600)已存在，不再重复插入）
-- ----------------------------
insert into sys_menu values('2620', 'Shopify数据分析', '2600', '3', 'shopify', 'data/shopify/index', '', '', 1, 0, 'C', '0', '0', 'data:shopify:list', 'shopping', 'admin', sysdate(), '', null, 'Shopify数据分析菜单');
insert into sys_menu values('2621', '店铺数据查询', '2620', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'data:shopify:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2622', '店铺数据新增', '2620', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'data:shopify:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2623', '店铺数据修改', '2620', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'data:shopify:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2624', '店铺数据删除', '2620', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'data:shopify:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2625', '店铺数据导入', '2620', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'data:shopify:import',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2626', '店铺数据导出', '2620', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'data:shopify:export',  '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、示例数据（近两周 3 个销售渠道，便于图表联调）
-- ----------------------------
insert into yg_shopify_store_daily (report_date, store_domain, channel, sessions, orders, units_sold, revenue, create_by, create_time) values
(date_sub(curdate(), interval 13 day), 'ygocdb.myshopify.com', 'Online Store', 1200, 36, 48, 1860.50, 'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'ygocdb.myshopify.com', 'Online Store', 1350, 42, 55, 2148.00, 'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'ygocdb.myshopify.com', 'Online Store', 1180, 31, 40, 1596.30, 'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'ygocdb.myshopify.com', 'Online Store', 1420, 46, 60, 2380.20, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'ygocdb.myshopify.com', 'Online Store', 1510, 51, 66, 2605.60, 'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'ygocdb.myshopify.com', 'Online Store', 1290, 37, 47, 1921.40, 'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'ygocdb.myshopify.com', 'Online Store', 1380, 44, 57, 2266.90, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'ygocdb.myshopify.com', 'Online Store', 1460, 47, 61, 2478.30, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'ygocdb.myshopify.com', 'Online Store', 1310, 35, 44, 1810.50, 'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'ygocdb.myshopify.com', 'Online Store', 1550, 53, 70, 2719.40, 'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'ygocdb.myshopify.com', 'Online Store', 1620, 57, 75, 2936.80, 'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'ygocdb.myshopify.com', 'Online Store', 1480, 43, 55, 2258.20, 'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'ygocdb.myshopify.com', 'Online Store', 1590, 54, 71, 2828.50, 'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'ygocdb.myshopify.com', 'Online Store', 1020, 29, 36, 1512.30, 'admin', sysdate()),
(date_sub(curdate(), interval 13 day), 'ygocdb.myshopify.com', 'Facebook Ads', 860,  17, 21, 845.60,  'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'ygocdb.myshopify.com', 'Facebook Ads', 920,  20, 25, 968.20,  'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'ygocdb.myshopify.com', 'Facebook Ads', 810,  13, 16, 632.40,  'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'ygocdb.myshopify.com', 'Facebook Ads', 980,  22, 28, 1089.50, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'ygocdb.myshopify.com', 'Facebook Ads', 1050, 25, 31, 1202.30, 'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'ygocdb.myshopify.com', 'Facebook Ads', 890,  16, 20, 751.80,  'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'ygocdb.myshopify.com', 'Facebook Ads', 960,  21, 26, 1074.60, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'ygocdb.myshopify.com', 'Facebook Ads', 1020, 24, 30, 1195.20, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'ygocdb.myshopify.com', 'Facebook Ads', 870,  14, 18, 638.90,  'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'ygocdb.myshopify.com', 'Facebook Ads', 1090, 27, 34, 1318.40, 'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'ygocdb.myshopify.com', 'Facebook Ads', 1140, 30, 38, 1432.60, 'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'ygocdb.myshopify.com', 'Facebook Ads', 940,  18, 22, 861.20,  'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'ygocdb.myshopify.com', 'Facebook Ads', 1110, 28, 35, 1324.80, 'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'ygocdb.myshopify.com', 'Facebook Ads', 720,  10, 12, 426.50,  'admin', sysdate()),
(date_sub(curdate(), interval 13 day), 'ygocdb.myshopify.com', 'Google Ads',   640,  12, 15, 588.40,  'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'ygocdb.myshopify.com', 'Google Ads',   690,  14, 18, 665.10,  'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'ygocdb.myshopify.com', 'Google Ads',   580,  9,  12, 452.80,  'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'ygocdb.myshopify.com', 'Google Ads',   730,  16, 20, 742.60,  'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'ygocdb.myshopify.com', 'Google Ads',   780,  18, 23, 828.20,  'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'ygocdb.myshopify.com', 'Google Ads',   650,  11, 14, 571.50,  'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'ygocdb.myshopify.com', 'Google Ads',   710,  15, 19, 712.40,  'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'ygocdb.myshopify.com', 'Google Ads',   740,  16, 21, 756.80,  'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'ygocdb.myshopify.com', 'Google Ads',   610,  10, 13, 504.60,  'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'ygocdb.myshopify.com', 'Google Ads',   800,  19, 25, 872.30,  'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'ygocdb.myshopify.com', 'Google Ads',   840,  21, 27, 948.90,  'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'ygocdb.myshopify.com', 'Google Ads',   670,  12, 16, 602.70,  'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'ygocdb.myshopify.com', 'Google Ads',   820,  20, 26, 898.50,  'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'ygocdb.myshopify.com', 'Google Ads',   520,  7,  9,  352.30,  'admin', sysdate());


-- ----------------------------
-- 4、更新日志（向 yg_update_log 追加本模块记录，需先执行 yg_update_log.sql 建表）
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.4.0', '1. 新增Shopify数据分析模块，支持店铺渠道日报导入、手动维护与图表分析。', '0', 'admin', sysdate(), 'Shopify数据分析');
