-- ----------------------------
-- WooCommerce店铺数据分析业务表
-- 用途：管理端"数据管理 > WooCommerce数据分析"模块，
--       记录店铺渠道日报（日期+店铺+销售渠道粒度），支持报表导入/手动维护与图表分析。
--       转化率/客单价等派生指标由统计接口计算，不落库。
-- ----------------------------

-- ----------------------------
-- 1、WooCommerce店铺渠道日报表（物理删除，不设 del_flag，避免与导入唯一键冲突）
-- ----------------------------
drop table if exists yg_woocommerce_store_daily;
create table yg_woocommerce_store_daily (
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
) engine=innodb auto_increment=1 comment = 'WooCommerce店铺渠道日报表';


-- ----------------------------
-- 2、菜单（使用 2630+ 预留ID段；顶级目录"数据管理"(2600)已存在，不再重复插入）
-- ----------------------------
insert into sys_menu values('2630', 'WooCommerce数据分析', '2600', '4', 'woocommerce', 'data/woocommerce/index', '', '', 1, 0, 'C', '0', '0', 'data:woocommerce:list', 'shopping', 'admin', sysdate(), '', null, 'WooCommerce数据分析菜单');
insert into sys_menu values('2631', '店铺数据查询', '2630', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'data:woocommerce:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2632', '店铺数据新增', '2630', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'data:woocommerce:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2633', '店铺数据修改', '2630', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'data:woocommerce:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2634', '店铺数据删除', '2630', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'data:woocommerce:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2635', '店铺数据导入', '2630', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'data:woocommerce:import',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2636', '店铺数据导出', '2630', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'data:woocommerce:export',  '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、示例数据（近两周 3 个销售渠道，便于图表联调）
-- ----------------------------
insert into yg_woocommerce_store_daily (report_date, store_domain, channel, sessions, orders, units_sold, revenue, create_by, create_time) values
(date_sub(curdate(), interval 13 day), 'shop.yaogo-woo.com', 'Direct',        980,  30, 41, 1520.80, 'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'shop.yaogo-woo.com', 'Direct',        1050, 34, 46, 1712.50, 'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'shop.yaogo-woo.com', 'Direct',        920,  26, 35, 1298.60, 'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'shop.yaogo-woo.com', 'Direct',        1120, 38, 51, 1896.40, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'shop.yaogo-woo.com', 'Direct',        1180, 41, 55, 2048.70, 'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'shop.yaogo-woo.com', 'Direct',        1010, 31, 42, 1567.30, 'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'shop.yaogo-woo.com', 'Direct',        1080, 36, 48, 1789.60, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'shop.yaogo-woo.com', 'Direct',        1140, 39, 53, 1932.80, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'shop.yaogo-woo.com', 'Direct',        990,  28, 37, 1408.50, 'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'shop.yaogo-woo.com', 'Direct',        1210, 43, 58, 2118.90, 'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'shop.yaogo-woo.com', 'Direct',        1260, 46, 62, 2265.40, 'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'shop.yaogo-woo.com', 'Direct',        1090, 33, 44, 1658.20, 'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'shop.yaogo-woo.com', 'Direct',        1170, 42, 57, 2087.60, 'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'shop.yaogo-woo.com', 'Direct',        850,  24, 32, 1186.30, 'admin', sysdate()),
(date_sub(curdate(), interval 13 day), 'shop.yaogo-woo.com', 'Organic Search', 760,  15, 19, 762.40,  'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'shop.yaogo-woo.com', 'Organic Search', 820,  18, 23, 895.60,  'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'shop.yaogo-woo.com', 'Organic Search', 710,  12, 15, 598.30,  'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'shop.yaogo-woo.com', 'Organic Search', 870,  20, 26, 986.70,  'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'shop.yaogo-woo.com', 'Organic Search', 930,  23, 30, 1108.50, 'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'shop.yaogo-woo.com', 'Organic Search', 780,  14, 18, 705.20,  'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'shop.yaogo-woo.com', 'Organic Search', 850,  19, 25, 942.80,  'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'shop.yaogo-woo.com', 'Organic Search', 900,  22, 28, 1067.40, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'shop.yaogo-woo.com', 'Organic Search', 750,  13, 17, 648.90,  'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'shop.yaogo-woo.com', 'Organic Search', 960,  25, 33, 1196.80, 'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'shop.yaogo-woo.com', 'Organic Search', 1010, 27, 36, 1289.50, 'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'shop.yaogo-woo.com', 'Organic Search', 830,  16, 21, 798.60,  'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'shop.yaogo-woo.com', 'Organic Search', 980,  26, 34, 1246.70, 'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'shop.yaogo-woo.com', 'Organic Search', 640,  9,  12, 438.20,  'admin', sysdate()),
(date_sub(curdate(), interval 13 day), 'shop.yaogo-woo.com', 'Facebook Ads',  640,  12, 15, 588.40,  'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'shop.yaogo-woo.com', 'Facebook Ads',  690,  14, 18, 665.10,  'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'shop.yaogo-woo.com', 'Facebook Ads',  580,  9,  12, 452.80,  'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'shop.yaogo-woo.com', 'Facebook Ads',  730,  16, 20, 742.60,  'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'shop.yaogo-woo.com', 'Facebook Ads',  780,  18, 23, 828.20,  'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'shop.yaogo-woo.com', 'Facebook Ads',  650,  11, 14, 571.50,  'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'shop.yaogo-woo.com', 'Facebook Ads',  710,  15, 19, 712.40,  'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'shop.yaogo-woo.com', 'Facebook Ads',  740,  16, 21, 756.80,  'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'shop.yaogo-woo.com', 'Facebook Ads',  610,  10, 13, 504.60,  'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'shop.yaogo-woo.com', 'Facebook Ads',  800,  19, 25, 872.30,  'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'shop.yaogo-woo.com', 'Facebook Ads',  840,  21, 27, 948.90,  'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'shop.yaogo-woo.com', 'Facebook Ads',  670,  12, 16, 602.70,  'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'shop.yaogo-woo.com', 'Facebook Ads',  820,  20, 26, 898.50,  'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'shop.yaogo-woo.com', 'Facebook Ads',  520,  7,  9,  352.30,  'admin', sysdate());


-- ----------------------------
-- 4、更新日志（向 yg_update_log 追加本模块记录，需先执行 yg_update_log.sql 建表）
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.5.0', '1. 新增WooCommerce数据分析模块，支持店铺渠道日报导入、手动维护与图表分析。', '0', 'admin', sysdate(), 'WooCommerce数据分析');
