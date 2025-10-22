-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('旅游推荐', '2000', '1', 'recommend', 'system/recommend/index', 1, 0, 'C', '0', '0', 'system:recommend:list', 'guide', 'admin', sysdate(), '', null, '旅游推荐菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('旅游推荐查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:recommend:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('旅游推荐新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:recommend:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('旅游推荐修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:recommend:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('旅游推荐删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:recommend:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('旅游推荐导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:recommend:export',       '#', 'admin', sysdate(), '', null, '');