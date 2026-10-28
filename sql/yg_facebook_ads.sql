-- ----------------------------
-- Facebook广告数据分析业务表
-- 用途：管理端"数据管理 > Facebook广告数据分析"模块，
--       记录广告活动日报（日期+广告活动粒度），支持报表导入/手动维护与图表分析。
--       ACOS/CTR/CVR 等派生指标由统计接口计算，不落库。
-- ----------------------------

-- ----------------------------
-- 1、Facebook广告活动日报表（物理删除，不设 del_flag，避免与导入唯一键冲突）
-- ----------------------------
drop table if exists yg_facebook_ads_daily;
create table yg_facebook_ads_daily (
  daily_id          bigint(20)      not null auto_increment    comment '主键',
  report_date       date            not null                   comment '报告日期',
  account_id        varchar(64)     default ''                 comment '广告账户(标识,可选)',
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
  unique key uk_date_account_campaign (report_date, account_id, campaign_name(100)),
  key idx_campaign_date (campaign_name(100), report_date),
  key idx_account_date  (account_id, report_date)
) engine=innodb auto_increment=1 comment = 'Facebook广告活动日报表';


-- ----------------------------
-- 2、菜单（使用 2610+ 预留ID段；顶级目录"数据管理"(2600)已存在，不再重复插入）
-- ----------------------------
insert into sys_menu values('2610', 'Facebook广告数据分析', '2600', '2', 'facebookAds', 'data/facebookAds/index', '', '', 1, 0, 'C', '0', '0', 'data:facebookAds:list', 'share', 'admin', sysdate(), '', null, 'Facebook广告数据分析菜单');
insert into sys_menu values('2611', '广告数据查询', '2610', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'data:facebookAds:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2612', '广告数据新增', '2610', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'data:facebookAds:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2613', '广告数据修改', '2610', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'data:facebookAds:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2614', '广告数据删除', '2610', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'data:facebookAds:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2615', '广告数据导入', '2610', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'data:facebookAds:import',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2616', '广告数据导出', '2610', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'data:facebookAds:export',  '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、示例数据（近两周 3 个广告活动，便于图表联调）
-- ----------------------------
insert into yg_facebook_ads_daily (report_date, account_id, campaign_name, impressions, clicks, spend, sales, orders, create_by, create_time) values
(date_sub(curdate(), interval 13 day), 'act_10001', 'FB-转化-主推商品', 12000, 96,  18.40, 132.50, 7, 'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'act_10001', 'FB-转化-主推商品', 13500, 110, 21.10, 148.00, 8, 'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'act_10001', 'FB-转化-主推商品', 11800, 88,  16.80, 96.30,  5, 'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'act_10001', 'FB-转化-主推商品', 14200, 125, 24.50, 180.20, 9, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'act_10001', 'FB-转化-主推商品', 15100, 132, 26.20, 205.60, 11,'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'act_10001', 'FB-转化-主推商品', 12900, 101, 19.60, 121.40, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'act_10001', 'FB-转化-主推商品', 13800, 118, 22.80, 166.90, 9, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'act_10001', 'FB-转化-主推商品', 14600, 127, 25.10, 178.30, 9, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'act_10001', 'FB-转化-主推商品', 13100, 99,  18.90, 110.50, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'act_10001', 'FB-转化-主推商品', 15500, 140, 27.80, 219.40, 12,'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'act_10001', 'FB-转化-主推商品', 16200, 149, 30.20, 236.80, 13,'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'act_10001', 'FB-转化-主推商品', 14800, 121, 23.70, 158.20, 8, 'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'act_10001', 'FB-转化-主推商品', 15900, 144, 28.60, 228.50, 12,'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'act_10001', 'FB-转化-主推商品', 10200, 84,  15.90, 112.30, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 13 day), 'act_10002', 'FB-再营销-购物车', 8600,  52,  9.80,  45.60,  2, 'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'act_10002', 'FB-再营销-购物车', 9200,  61,  11.40, 68.20,  3, 'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'act_10002', 'FB-再营销-购物车', 8100,  47,  8.60,  32.40,  1, 'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'act_10002', 'FB-再营销-购物车', 9800,  66,  12.90, 89.50,  4, 'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'act_10002', 'FB-再营销-购物车', 10500, 73,  14.20, 102.30, 5, 'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'act_10002', 'FB-再营销-购物车', 8900,  55,  10.10, 51.80,  2, 'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'act_10002', 'FB-再营销-购物车', 9600,  64,  11.80, 74.60,  3, 'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'act_10002', 'FB-再营销-购物车', 10200, 70,  13.40, 95.20,  4, 'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'act_10002', 'FB-再营销-购物车', 8700,  50,  9.20,  38.90,  2, 'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'act_10002', 'FB-再营销-购物车', 10900, 78,  15.10, 118.40, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'act_10002', 'FB-再营销-购物车', 11400, 85,  16.80, 132.60, 7, 'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'act_10002', 'FB-再营销-购物车', 9400,  58,  10.90, 61.20,  3, 'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'act_10002', 'FB-再营销-购物车', 11100, 80,  15.60, 124.80, 6, 'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'act_10002', 'FB-再营销-购物车', 7200,  41,  7.30,  26.50,  1, 'admin', sysdate()),
(date_sub(curdate(), interval 13 day), 'act_10003', 'FB-兴趣-新客',     21000, 168, 33.60, 268.40, 13,'admin', sysdate()),
(date_sub(curdate(), interval 12 day), 'act_10003', 'FB-兴趣-新客',     22800, 186, 37.20, 305.10, 15,'admin', sysdate()),
(date_sub(curdate(), interval 11 day), 'act_10003', 'FB-兴趣-新客',     19600, 149, 29.40, 212.80, 10,'admin', sysdate()),
(date_sub(curdate(), interval 10 day), 'act_10003', 'FB-兴趣-新客',     23500, 198, 40.10, 342.60, 17,'admin', sysdate()),
(date_sub(curdate(), interval 9 day),  'act_10003', 'FB-兴趣-新客',     24800, 215, 44.30, 388.20, 19,'admin', sysdate()),
(date_sub(curdate(), interval 8 day),  'act_10003', 'FB-兴趣-新客',     20900, 163, 32.10, 241.50, 12,'admin', sysdate()),
(date_sub(curdate(), interval 7 day),  'act_10003', 'FB-兴趣-新客',     22400, 181, 36.50, 312.40, 15,'admin', sysdate()),
(date_sub(curdate(), interval 6 day),  'act_10003', 'FB-兴趣-新客',     23100, 192, 39.20, 336.80, 16,'admin', sysdate()),
(date_sub(curdate(), interval 5 day),  'act_10003', 'FB-兴趣-新客',     19800, 152, 30.40, 224.60, 11,'admin', sysdate()),
(date_sub(curdate(), interval 4 day),  'act_10003', 'FB-兴趣-新客',     25400, 226, 46.80, 412.30, 20,'admin', sysdate()),
(date_sub(curdate(), interval 3 day),  'act_10003', 'FB-兴趣-新客',     26100, 238, 49.50, 448.90, 22,'admin', sysdate()),
(date_sub(curdate(), interval 2 day),  'act_10003', 'FB-兴趣-新客',     21600, 171, 34.20, 262.70, 13,'admin', sysdate()),
(date_sub(curdate(), interval 1 day),  'act_10003', 'FB-兴趣-新客',     25800, 231, 47.90, 428.50, 21,'admin', sysdate()),
(date_sub(curdate(), interval 0 day),  'act_10003', 'FB-兴趣-新客',     16400, 118, 22.60, 152.30, 7, 'admin', sysdate());


-- ----------------------------
-- 4、更新日志（向 yg_update_log 追加本模块记录，需先执行 yg_update_log.sql 建表）
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.3.0', '1. 新增Facebook广告数据分析模块，支持广告活动日报导入、手动维护与图表分析。', '0', 'admin', sysdate(), 'Facebook广告数据分析');
