-- ----------------------------
-- 决斗场地（卡片管理 > 决斗场地）菜单
-- 纯前端可视化页面,无后端表,仅注册菜单
-- ----------------------------
delete from sys_role_menu where menu_id in (2050, 2051);
delete from sys_menu where menu_id in (2050, 2051);
insert into sys_menu values('2050', '决斗场地', '2000', '6', 'duel', 'card/duel/index', '', '', 1, 0, 'C', '0', '0', 'card:duel:list', 'star', 'admin', sysdate(), '', null, '决斗场地模拟对战页面');
insert into sys_menu values('2051', '决斗场地查询', '2050', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'card:duel:query', '#', 'admin', sysdate(), '', null, '');
