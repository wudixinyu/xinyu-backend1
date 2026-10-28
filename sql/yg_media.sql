-- ----------------------------
-- AI素材库业务表
-- 用途：管理端维护图片/视频素材（上传、检索、预览、重命名、删除），
--       文件经 /ai/media/upload 存入 ruoyi.profile 目录，DB 记录 /profile/... 相对路径。
-- ----------------------------
-- 执行说明：请在同一会话中整体执行本脚本（依赖用户变量 @ai_parent）。
-- 执行前校验（应为空）：SELECT menu_id, menu_name FROM sys_menu WHERE menu_id BETWEEN 2400 AND 2499;
-- 回滚段（如需撤销菜单）：
--   delete from sys_role_menu where menu_id between 2400 and 2499;
--   delete from sys_menu where menu_id between 2400 and 2499;
-- ----------------------------

-- ----------------------------
-- 1、素材库表
-- ----------------------------
drop table if exists yg_media;
create table yg_media (
  media_id          bigint(20)      not null auto_increment    comment '素材主键',
  media_name        varchar(255)    not null                   comment '素材名称（可重命名）',
  original_name     varchar(255)    default ''                 comment '上传时原始文件名',
  media_type        tinyint(2)      not null default 1         comment '素材类型(1:图片 2:视频)',
  file_url          varchar(500)    not null                   comment '文件地址(/profile/...)',
  file_size         bigint(20)      default 0                  comment '文件大小(字节)',
  ext               varchar(20)     default ''                 comment '扩展名(jpg/png/mp4等)',
  category          varchar(100)    default ''                 comment '分类/标签',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (media_id),
  key idx_media_name (media_name(64)),
  key idx_media_type (media_type)
) engine=innodb auto_increment=1 comment = 'AI素材库表';


-- ----------------------------
-- 2、字典数据（素材类型）
-- ----------------------------
insert into sys_dict_type values(null, '素材类型', 'yg_media_type', '0', 'admin', sysdate(), '', null, 'AI素材库类型');

insert into sys_dict_data values(null, 1, '图片', '1', 'yg_media_type', '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(null, 2, '视频', '2', 'yg_media_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 3、菜单（使用 2400+ 预留ID段，动态挂到"AI管理"目录下）
-- ----------------------------
SET @ai_parent = IFNULL(
  (SELECT menu_id FROM sys_menu WHERE menu_name='AI管理' AND menu_type='M' AND parent_id=0 LIMIT 1), 0);

insert into sys_menu values('2400', '素材库', @ai_parent, '11', 'aimedia', 'ai/media/index', '', '', 1, 0, 'C', '0', '0', 'ai:media:list', 'upload', 'admin', sysdate(), '', null, 'AI素材库菜单');
insert into sys_menu values('2401', '素材查询', '2400', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:media:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2402', '素材上传', '2400', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:media:upload', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2403', '素材修改', '2400', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:media:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2404', '素材删除', '2400', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'ai:media:remove', '#', 'admin', sysdate(), '', null, '');
