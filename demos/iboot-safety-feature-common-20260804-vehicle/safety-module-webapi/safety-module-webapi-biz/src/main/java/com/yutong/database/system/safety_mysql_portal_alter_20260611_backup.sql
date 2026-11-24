/*
    mysql
    数据库服务器IP  10.66.139.219  safety
*/
use safety;

CREATE TABLE IF NOT EXISTS safety.safety_sys_perm_bak_20260611 LIKE safety.safety_sys_perm;
INSERT INTO safety.safety_sys_perm_bak_20260611 SELECT * FROM safety.safety_sys_perm;

CREATE TABLE IF NOT EXISTS safety.safety_sys_role_bak_20260611 LIKE safety.safety_sys_role;
INSERT INTO safety.safety_sys_role_bak_20260611 SELECT * FROM safety.safety_sys_role;

CREATE TABLE IF NOT EXISTS safety.safety_sys_role_perm_bak_20260611 LIKE safety.safety_sys_role_perm;
INSERT INTO safety.safety_sys_role_perm_bak_20260611 SELECT * FROM safety.safety_sys_role_perm;

CREATE TABLE IF NOT EXISTS safety.safety_sys_user_bak_20260611 LIKE safety.safety_sys_user;
INSERT INTO safety.safety_sys_user_bak_20260611 SELECT * FROM safety.safety_sys_user;

CREATE TABLE IF NOT EXISTS safety.safety_sys_user_role_bak_20260611 LIKE safety.safety_sys_user_role;
INSERT INTO safety.safety_sys_user_role_bak_20260611 SELECT * FROM safety.safety_sys_user_role;

CREATE TABLE IF NOT EXISTS safety.safety_dict_item_bak_20260611 LIKE safety.safety_dict_item;
INSERT INTO safety.safety_dict_item_bak_20260611 SELECT * FROM safety.safety_dict_item;

CREATE TABLE IF NOT EXISTS safety.safety_dict_supper_bak_20260611 LIKE safety.safety_dict_supper;
INSERT INTO safety.safety_dict_supper_bak_20260611 SELECT * FROM safety.safety_dict_supper;

commit;