-- ----------------------------
-- 亚马逊广告数据分析业务表
-- 用途：管理端"数据管理 > 亚马逊广告数据分析"模块，
--       记录广告活动日报（日期+广告活动粒度），支持报表导入/手动维护与图表分析。
--       ACOS/CTR/CVR 等派生指标由统计接口计算，不落库。
-- ----------------------------

-- ----------------------------
-- 1、亚马逊广告活动日报表（物理删除，不设 del_flag，避免与导入唯一键冲突）
-- ----------------------------
drop table if exists yg_amazon_ads_daily;
create table yg_amazon_ads_daily (
  daily_id          bigint(20)      not null auto_increment    comment '主键',
  report_date       date            not null                   comment '报告日期',
  profile_id        varchar(64)     default ''                 comment '广告档案/店铺标识(可选)',
  campaign_name     varchar(255)    not null                   comment '广告活动名称',
  impressions       bigint(20)      default 0                  comment '曝光量',
  clicks            bigint(20)      default 0                  comment '点击量',
  spend             decimal(12,2)   default 0.00               comment '花费',
  sales             decimal(12,2)   default 0.00               comment '销售额',
  orders            int(11)         default 0                  comment '订单数',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (daily_id),
  unique key uk_date_profile_campaign (report_date, profile_id, campaign_name(100)),
  key idx_campaign_date (campaign_name(100), report_date),
  key idx_profile_date  (profile_id, report_date)
) engine=innodb auto_increment=1 comment = '亚马逊广告活动日报表';


-- ----------------------------
-- 2、菜单（使用 2600+ 预留ID段；顶级目录"数据管理"挂在"知识库"相关菜单下方）
-- ----------------------------
-- 现占 order=8 的模型管理后移，为"数据管理"目录腾位
update sys_menu set order_num = 9 where menu_id = '2208';

insert into sys_menu values('2600', '数据管理', '0', '8', 'data', null, '', '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '', null, '数据管理目录');
insert into sys_menu values('2601', '亚马逊广告数据分析', '2600', '1', 'amazonAds', 'data/amazonAds/index', '', '', 1, 0, 'C', '0', '0', 'data:amazonAds:list', 'shopping', 'admin', sysdate(), '', null, '亚马逊广告数据分析菜单');
insert into sys_menu values('2602', '广告数据查询', '2601', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'data:amazonAds:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2603', '广告数据新增', '2601', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'data:amazonAds:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2604', '广告数据修改', '2601', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'data:amazonAds:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2605', '广告数据删除', '2601', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'data:amazonAds:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2606', '广告数据导入', '2601', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'data:amazonAds:import',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2607', '广告数据导出', '2601', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'data:amazonAds:export',  '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、示例数据（近两周 3 个广告活动，便于图表联调）
-- ----------------------------
insert into yg_amazon_ads_daily (report_date, profile_id, campaign_name, impressions, clicks, spend, sales, orders, create_by, create_time) values
(date_sub(curdate(), interval 13 day), '', 'SP-精确-主推ASIN', 12000, 96,  18.40, 132.50, 7, 'admin', sysdate()),
(date_sub(curdate(), interval 12 day), '', 'SP-精确-主推ASIN', 13500, 110, 21.10, 148.00, 8, 'admin', sysdate()),
(date_sub(curdate(), interval 11 day), '', 'SP-精确-主推ASIN', 11800, 88,  16.80, 96.30,  5, 'admin', sysdate()),
(date_sub(curdate(), interval 10 day), '', 'SP-精确-主推ASIN', 14200, 125, 24.50, 180.20, 9, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  '', 'SP-精确-主推ASIN', 15100, 132, 26.20, 205.60, 11,'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  '', 'SP-精确-主推ASIN', 12900, 101, 19.60, 121.40, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  '', 'SP-精确-主推ASIN', 13800, 118, 22.80, 166.90, 9, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  '', 'SP-精确-主推ASIN', 14600, 127, 25.10, 178.30, 9, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  '', 'SP-精确-主推ASIN', 13100, 99,  18.90, 110.50, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  '', 'SP-精确-主推ASIN', 15500, 140, 27.80, 219.40, 12,'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  '', 'SP-精确-主推ASIN', 16200, 149, 30.20, 236.80, 13,'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  '', 'SP-精确-主推ASIN', 14800, 121, 23.70, 158.20, 8, 'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  '', 'SP-精确-主推ASIN', 15900, 144, 28.60, 228.50, 12,'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  '', 'SP-精确-主推ASIN', 10200, 84,  15.90, 112.30, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 13 day), '', 'SP-自动-新品',     8600,  52,  9.80,  45.60,  2, 'admin', sysdate()),
(date_sub(curdate(), interval 12 day), '', 'SP-自动-新品',     9200,  61,  11.40, 68.20,  3, 'admin', sysdate()),
(date_sub(curdate(), interval 11 day), '', 'SP-自动-新品',     8100,  47,  8.60,  32.40,  1, 'admin', sysdate()),
(date_sub(curdate(), interval 10 day), '', 'SP-自动-新品',     9800,  66,  12.90, 89.50,  4, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  '', 'SP-自动-新品',     10500, 73,  14.20, 102.30, 5, 'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  '', 'SP-自动-新品',     8900,  55,  10.10, 51.80,  2, 'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  '', 'SP-自动-新品',     9600,  64,  11.80, 74.60,  3, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  '', 'SP-自动-新品',     10200, 70,  13.40, 95.20,  4, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  '', 'SP-自动-新品',     8700,  50,  9.20,  38.90,  2, 'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  '', 'SP-自动-新品',     10900, 78,  15.10, 118.40, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  '', 'SP-自动-新品',     11400, 85,  16.80, 132.60, 7, 'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  '', 'SP-自动-新品',     9400,  58,  10.90, 61.20,  3, 'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  '', 'SP-自动-新品',     11100, 80,  15.60, 124.80, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  '', 'SP-自动-新品',     7200,  41,  7.30,  26.50,  1, 'admin', sysdate()),
(date_sub(curdate(), interval 13 day), '', 'SB-品牌词',        21000, 168, 33.60, 268.40, 13,'admin', sysdate()),
(date_sub(curdate(), interval 12 day), '', 'SB-品牌词',        22800, 186, 37.20, 305.10, 15,'admin', sysdate()),
(date_sub(curdate(), interval 11 day), '', 'SB-品牌词',        19600, 149, 29.40, 212.80, 10,'admin', sysdate()),
(date_sub(curdate(), interval 10 day), '', 'SB-品牌词',        23500, 198, 40.10, 342.60, 17,'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  '', 'SB-品牌词',        24800, 215, 44.30, 388.20, 19,'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  '', 'SB-品牌词',        20900, 163, 32.10, 241.50, 12,'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  '', 'SB-品牌词',        22400, 181, 36.50, 312.40, 15,'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  '', 'SB-品牌词',        23100, 192, 39.20, 336.80, 16,'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  '', 'SB-品牌词',        19800, 152, 30.40, 224.60, 11,'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  '', 'SB-品牌词',        25400, 226, 46.80, 412.30, 20,'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  '', 'SB-品牌词',        26100, 238, 49.50, 448.90, 22,'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  '', 'SB-品牌词',        21600, 171, 34.20, 262.70, 13,'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  '', 'SB-品牌词',        25800, 231, 47.90, 428.50, 21,'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  '', 'SB-品牌词',        16400, 118, 22.60, 152.30, 7, 'admin', sysdate());


-- ----------------------------
-- 4、更新日志（向 yg_update_log 追加本模块记录，需先执行 yg_update_log.sql 建表）
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.2.0', '1. 新增"数据管理"目录；\n2. 新增亚马逊广告数据分析模块，支持广告活动日报导入、手动维护与图表分析。', '0', 'admin', sysdate(), '亚马逊广告数据分析');
