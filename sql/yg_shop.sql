-- ----------------------------
-- 店铺管理业务表（多店铺 + WooCommerce 订单/商品/文章）
-- 用途：管理端"店铺管理"菜单，
--       1) 店铺设置：维护多个 WooCommerce 店铺（地址 + Consumer Key/Secret）；
--       2) Woo订单/商品/文章：调用 WooCommerce REST API / WordPress REST API 同步到本地，支持查询与清理。
-- ----------------------------

-- ----------------------------
-- 1、店铺设置表（物理删除，不设 del_flag）
-- ----------------------------
drop table if exists yg_shop_store;
create table yg_shop_store (
  store_id          bigint(20)      not null auto_increment    comment '店铺主键',
  store_name        varchar(100)    not null                   comment '店铺名称',
  store_url         varchar(255)    not null                   comment '店铺地址(如 https://shop.example.com)',
  consumer_key      varchar(255)    default ''                 comment 'WooCommerce Consumer Key',
  consumer_secret   varchar(255)    default ''                 comment 'WooCommerce Consumer Secret',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  sort              int(11)         default 0                  comment '显示排序',
  last_sync_time    datetime                                   comment '最近同步时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (store_id),
  unique key uk_store_url (store_url)
) engine=innodb auto_increment=1 comment = '店铺设置表';


-- ----------------------------
-- 2、WooCommerce订单表（按 店铺+远端订单ID 唯一，同步时覆盖更新）
-- ----------------------------
drop table if exists yg_shop_woo_order;
create table yg_shop_woo_order (
  order_id          bigint(20)      not null auto_increment    comment '主键',
  store_id          bigint(20)      not null                   comment '店铺ID',
  woo_order_id      bigint(20)      not null                   comment 'WooCommerce订单ID',
  order_number      varchar(64)     default ''                 comment '订单号',
  status            varchar(32)     default ''                 comment '订单状态(pending/processing/completed等)',
  currency          varchar(10)     default ''                 comment '币种',
  total             decimal(12,2)   default 0.00               comment '订单总额',
  customer_name     varchar(200)    default ''                 comment '客户姓名',
  customer_email    varchar(200)    default ''                 comment '客户邮箱',
  payment_method    varchar(100)    default ''                 comment '支付方式',
  item_count        int(11)         default 0                  comment '商品件数',
  items_summary     varchar(1000)   default ''                 comment '商品摘要',
  date_created      datetime                                   comment '下单时间',
  date_paid         datetime                                   comment '支付时间',
  create_time       datetime                                   comment '创建时间',
  update_time       datetime                                   comment '更新时间',
  primary key (order_id),
  unique key uk_store_order (store_id, woo_order_id),
  key idx_order_status (status),
  key idx_order_created (date_created)
) engine=innodb auto_increment=1 comment = 'WooCommerce订单表';


-- ----------------------------
-- 3、WooCommerce商品表（按 店铺+远端商品ID 唯一）
-- ----------------------------
drop table if exists yg_shop_woo_product;
create table yg_shop_woo_product (
  product_id        bigint(20)      not null auto_increment    comment '主键',
  store_id          bigint(20)      not null                   comment '店铺ID',
  woo_product_id    bigint(20)      not null                   comment 'WooCommerce商品ID',
  name              varchar(500)    default ''                 comment '商品名称',
  sku               varchar(200)    default ''                 comment 'SKU',
  type              varchar(32)     default ''                 comment '商品类型(simple/variable等)',
  status            varchar(32)     default ''                 comment '商品状态(publish/draft等)',
  price             decimal(12,2)   default 0.00               comment '现价',
  regular_price     decimal(12,2)   default 0.00               comment '原价',
  sale_price        decimal(12,2)   default 0.00               comment '促销价',
  stock_quantity    int(11)         default null               comment '库存数量',
  stock_status      varchar(32)     default ''                 comment '库存状态(instock/outofstock)',
  categories        varchar(500)    default ''                 comment '商品分类(斜杠分隔)',
  image_url         varchar(500)    default ''                 comment '主图地址',
  permalink         varchar(500)    default ''                 comment '商品链接',
  date_created      datetime                                   comment '创建时间(店铺)',
  date_modified     datetime                                   comment '更新时间(店铺)',
  create_time       datetime                                   comment '同步时间',
  update_time       datetime                                   comment '更新时间',
  primary key (product_id),
  unique key uk_store_product (store_id, woo_product_id),
  key idx_product_status (status),
  key idx_product_sku (sku)
) engine=innodb auto_increment=1 comment = 'WooCommerce商品表';


-- ----------------------------
-- 4、WooCommerce文章表（WordPress文章，按 店铺+远端文章ID 唯一）
-- ----------------------------
drop table if exists yg_shop_woo_post;
create table yg_shop_woo_post (
  post_id           bigint(20)      not null auto_increment    comment '主键',
  store_id          bigint(20)      not null                   comment '店铺ID',
  woo_post_id       bigint(20)      not null                   comment 'WordPress文章ID',
  title             varchar(500)    default ''                 comment '文章标题',
  slug              varchar(255)    default ''                 comment '文章别名',
  status            varchar(32)     default ''                 comment '文章状态(publish/draft等)',
  excerpt           varchar(1000)   default ''                 comment '摘要(纯文本)',
  content           mediumtext                                 comment '正文(纯文本)',
  link              varchar(500)    default ''                 comment '文章链接',
  date_created      datetime                                   comment '发布时间',
  date_modified     datetime                                   comment '更新时间',
  create_time       datetime                                   comment '同步时间',
  update_time       datetime                                   comment '更新时间',
  primary key (post_id),
  unique key uk_store_post (store_id, woo_post_id),
  key idx_post_status (status),
  key idx_post_created (date_created)
) engine=innodb auto_increment=1 comment = 'WooCommerce文章表';


-- ----------------------------
-- 5、Webhook表（店铺端Webhook登记，接收回调时用于校验签名与记录推送结果）
-- ----------------------------
drop table if exists yg_shop_webhook;
create table yg_shop_webhook (
  webhook_id            bigint(20)      not null auto_increment    comment '主键',
  store_id              bigint(20)      not null                   comment '店铺ID',
  woo_webhook_id        bigint(20)      not null                   comment 'WooCommerce Webhook ID',
  name                  varchar(200)    not null                   comment 'Webhook名称',
  topic                 varchar(64)     not null                   comment '主题(order.created/order.updated等)',
  delivery_url          varchar(500)    not null                   comment '回调地址',
  secret                varchar(100)    default ''                 comment '密钥(用于HMAC-SHA256签名校验)',
  status                char(1)         default '0'                comment '状态（0启用 1停用）',
  last_delivery_time    datetime                                   comment '最近推送时间',
  last_delivery_status  char(1)         default null               comment '最近推送结果（0成功 1失败）',
  create_by             varchar(64)     default ''                 comment '创建者',
  create_time           datetime                                   comment '创建时间',
  update_by             varchar(64)     default ''                 comment '更新者',
  update_time           datetime                                   comment '更新时间',
  remark                varchar(500)    default null               comment '备注',
  primary key (webhook_id),
  unique key uk_store_webhook (store_id, woo_webhook_id),
  key idx_webhook_topic (topic)
) engine=innodb auto_increment=1 comment = '店铺Webhook表';


-- ----------------------------
-- 6、菜单（使用 2900+ 预留ID段；顶级目录"店铺管理"排在"工具使用"下方）
-- ----------------------------
insert into sys_menu values('2900', '店铺管理', '0', '11', 'shop', null, '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', null, '店铺管理目录');
insert into sys_menu values('2901', '店铺设置', '2900', '1', 'store', 'shop/store/index', '', '', 1, 0, 'C', '0', '0', 'shop:store:list', 'system', 'admin', sysdate(), '', null, '多店铺设置菜单');
insert into sys_menu values('2902', '店铺查询', '2901', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:store:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2903', '店铺新增', '2901', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:store:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2904', '店铺修改', '2901', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:store:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2905', '店铺删除', '2901', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:store:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2906', '连接测试', '2901', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:store:test',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2910', 'Woo订单', '2900', '2', 'order', 'shop/woo/order/index', '', '', 1, 0, 'C', '0', '0', 'shop:woo:order:list', 'list', 'admin', sysdate(), '', null, 'WooCommerce订单菜单');
insert into sys_menu values('2911', '订单查询', '2910', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:order:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2912', '订单同步', '2910', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:order:sync',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2913', '订单删除', '2910', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:order:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2920', 'Woo商品', '2900', '3', 'product', 'shop/woo/product/index', '', '', 1, 0, 'C', '0', '0', 'shop:woo:product:list', 'shopping', 'admin', sysdate(), '', null, 'WooCommerce商品菜单');
insert into sys_menu values('2921', '商品查询', '2920', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:product:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2922', '商品同步', '2920', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:product:sync',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2923', '商品删除', '2920', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:product:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2924', '商品新增', '2920', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:product:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2925', '商品修改', '2920', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:product:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2926', '商品导入', '2920', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:product:import', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2930', 'Woo文章', '2900', '4', 'post', 'shop/woo/post/index', '', '', 1, 0, 'C', '0', '0', 'shop:woo:post:list', 'documentation', 'admin', sysdate(), '', null, 'WooCommerce文章菜单');
insert into sys_menu values('2931', '文章查询', '2930', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:post:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2932', '文章同步', '2930', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:post:sync',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2933', '文章删除', '2930', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:woo:post:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2940', 'Webhooks', '2900', '5', 'webhook', 'shop/webhook/index', '', '', 1, 0, 'C', '0', '0', 'shop:webhook:list', 'link', 'admin', sysdate(), '', null, '店铺Webhooks菜单');
insert into sys_menu values('2941', 'Webhook查询', '2940', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:webhook:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2942', 'Webhook新增', '2940', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:webhook:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2943', 'Webhook修改', '2940', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:webhook:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2944', 'Webhook删除', '2940', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:webhook:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2945', 'Webhook同步', '2940', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'shop:webhook:sync',   '#', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 7、更新日志（向 yg_update_log 追加本模块记录，需先执行 yg_update_log.sql 建表）
-- ----------------------------
insert into yg_update_log (version, content, status, create_by, create_time, remark) values
('V1.6.0', '1. 新增店铺管理模块，支持多店铺设置、WooCommerce订单/商品/文章同步与查询。', '0', 'admin', sysdate(), '店铺管理'),
('V1.7.0', '1. 新增Webhooks管理，支持店铺Webhook配置与接收回调自动更新订单/商品。', '0', 'admin', sysdate(), 'Webhooks');


-- ----------------------------
-- 8、定时任务（增量执行：已建过表的环境只需单独执行本节 INSERT，无需重跑整份脚本）
--    每天 10:00 同步全部启用店铺的订单/商品/文章；任务Bean见 com.ruoyi.quartz.task.YgShopSyncTask
-- ----------------------------
insert into sys_job
(job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
values
(100, '店铺数据每日同步', 'DEFAULT', 'ygShopSyncTask.syncAll', '0 0 10 * * ?', '3', '1', '0', 'admin', sysdate(), '每天10点同步全部启用店铺的订单/商品/文章');

